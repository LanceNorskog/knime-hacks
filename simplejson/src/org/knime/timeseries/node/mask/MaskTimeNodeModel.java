/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   29.09.2009 (Fabian Dill): created
 */
package org.knime.timeseries.node.mask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * Node to mask/remove time or date fields of existing 
 * {@link DateAndTimeValue}s.
 * 
 * @author Fabian Dill, KNIME.com, Zurich, Switzerland
 */
public class MaskTimeNodeModel extends NodeModel {
    
    private static final NodeLogger LOGGER = NodeLogger.getLogger(
            MaskTimeNodeModel.class);
    
    /** Radio button label and identifier for masking the date. */
    static final String MASK_DATE = "Date";
    /** 
     * Radio button label and identifier for masking the time 
     * (with milliseconds). 
     */
    static final String MASK_TIME = "Time (including milliseconds)";
    /** Radio button label and identifier for masking only the milliseconds. */
    static final String MASK_MILLIS = "Milliseconds only";

    /**
     * 
     * @return settings model to store the mask selection
     */
    static SettingsModelString createMaskSelectionModel() {
        return new SettingsModelString("mask.time.selection", MASK_MILLIS);
    }
    
    /**
     * 
     * @return the settings model for the selected column containing the 
     *  {@link DateAndTimeValue}s to be masked
     */
    static SettingsModelString createColumnSelectionModel() {
        return new SettingsModelString("mask.time.selected.column", "");
    }
    
    private void resetMilliSeconds(final Calendar time) {
        time.clear(Calendar.MILLISECOND);
    }
    
    private final SettingsModelString m_maskSelection 
        = createMaskSelectionModel();
    
    private final SettingsModelString m_selectedColumn 
        = createColumnSelectionModel();
    
    private int m_nrInvalids;
    
    private boolean m_onlyInvalids;
    
    /**
     * One in-port with a table containing {@link DateAndTimeValue}s to mask and
     * one out-port containing the masked {@link DateAndTimeValue}s.
     */
    public MaskTimeNodeModel() {
        super(1, 1);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        DataTableSpec inSpec = inSpecs[0];
        // check if there is a date and tme column in niput spec
        if (!inSpec.containsCompatibleType(DateAndTimeValue.class)) {
            throw new InvalidSettingsException(
                    "Input table must contain at least one column " 
                    + "containing time!");
        }
        // do we have a selected column?
        String selectedCol = m_selectedColumn.getStringValue(); 
        if (selectedCol != null 
                && !selectedCol.isEmpty()) {
            // if yes -> exists in input spec?
            if (!inSpec.containsName(selectedCol)) {
                throw new InvalidSettingsException("Selected column " 
                        + selectedCol + "not found in input table!");
            }
        } else {
            // if no -> auto-configure: select first date and time column
            for (DataColumnSpec colSpec : inSpec) {
                if (colSpec.getType().isCompatible(DateAndTimeValue.class)) {
                    String colName = colSpec.getName();
                    m_selectedColumn.setStringValue(colName);
                    setWarningMessage("Auto-configure: selected column " 
                            + colName + "!");
                }
            }
        }
        // return input spec, since appending the new column is not supported
        return inSpecs;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        BufferedDataTable in = inData[0];
        // get the selected column index
        final int colIdx = in.getDataTableSpec().findColumnIndex(
                m_selectedColumn.getStringValue());
        if (colIdx < 0) {
            throw new IllegalArgumentException("Column " 
                    + m_selectedColumn.getStringValue() 
                    + " not found in input table!");
        }
        // create the column spec
        DataColumnSpec existing = in.getDataTableSpec().getColumnSpec(colIdx);
        DataColumnSpecCreator specCreator = new DataColumnSpecCreator(existing);
        // reset the domin in order to prevent unmasked timestamps appear as
        // lower or upper bounds
        specCreator.setDomain(null);
        ColumnRearranger rearranger = new ColumnRearranger(in.getSpec());
        final String maskMode = m_maskSelection.getStringValue();
        m_nrInvalids = 0;
        m_onlyInvalids = true;
        rearranger.replace(new SingleCellFactory(specCreator.createSpec()) {

            @Override
            public DataCell getCell(final DataRow row) {
                DataCell dc = row.getCell(colIdx);
                if (dc.isMissing()) {
                    return DataType.getMissingCell();
                }
                if (dc.getType().isCompatible(DateAndTimeValue.class)) {
                    DateAndTimeValue v = (DateAndTimeValue)dc;
                    Calendar time = v.getUTCCalendarClone();
                    if (maskMode.equals(MASK_DATE)) {
                        DateAndTimeCell.resetDateFields(time);
                        if (!v.hasTime()) {
                            // date is masked and no time -> missing value
                            m_nrInvalids++;
                            return DataType.getMissingCell();
                        }
                        m_onlyInvalids = false;
                        return new DateAndTimeCell(
                                time.getTimeInMillis(), false, 
                                v.hasTime(), v.hasMillis());
                    } else if (maskMode.equals(MASK_TIME)) {
                        DateAndTimeCell.resetTimeFields(time);
                        if (!v.hasDate()) {
                            // time is masked and no date -> missing cell
                            m_nrInvalids++;
                            return DataType.getMissingCell();
                        }
                        m_onlyInvalids = false;
                        return new DateAndTimeCell(
                                time.getTimeInMillis(), v.hasDate(), false, 
                                false);
                    } else if (maskMode.equals(MASK_MILLIS)) {
                        resetMilliSeconds(time);
                        m_onlyInvalids = false;
                        return new DateAndTimeCell(time.getTimeInMillis(), 
                                v.hasDate(), v.hasTime(), false);
                    }
                }
                LOGGER.error("Unsupported data type: " + dc.getType() + "!");
                return DataType.getMissingCell();
            }
            
        }, colIdx);
        BufferedDataTable out = exec.createColumnRearrangeTable(in, rearranger, 
                exec);
        if (m_nrInvalids > 0) {
            String warningMessage = "Produced " + m_nrInvalids 
                + " missing values due to " 
                + "masking of the only existing field!";
            if (m_onlyInvalids) {
                // only invalids -> different message
                warningMessage = "Produced only missing values " 
                        + "-> wrong field masked?";
            } 
            setWarningMessage(warningMessage);
        }
        return new BufferedDataTable[] {out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // nothing to do
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_maskSelection.validateSettings(settings);
        m_selectedColumn.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
    throws InvalidSettingsException {
        m_maskSelection.loadSettingsFrom(settings);
        m_selectedColumn.loadSettingsFrom(settings);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_maskSelection.saveSettingsTo(settings);
        m_selectedColumn.saveSettingsTo(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, 
            final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // no internals
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, 
            final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // no internals
    }

}
