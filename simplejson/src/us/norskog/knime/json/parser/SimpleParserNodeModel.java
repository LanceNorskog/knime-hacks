package us.norskog.knime.json.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of SimpleParser.
 * Simple high-speed json parser for flat records. No sub-blocks or arrays supported.
 *
 * @author Lance Norskog
 */
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.container.ColumnRearranger;

/**
 * 
 * @author wiswedel, University of Konstanz
 */
final class SimpleParserNodeModel extends NodeModel {

    private SimpleParserSettings m_settings;
    
    /** One input, one output. */
    public SimpleParserNodeModel() {
        super(1, 1);
    }

    /** {@inheritDoc} */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        ColumnRearranger rearranger = 
            createRearranger(inSpecs[0], new AtomicInteger());
        return new DataTableSpec[]{rearranger.createSpec()};
    }
    
    /** {@inheritDoc} */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        AtomicInteger i = new AtomicInteger();
        ColumnRearranger rearranger = createRearranger(inData[0].getSpec(), i);
        BufferedDataTable t = exec.createColumnRearrangeTable(
                inData[0], rearranger, exec);
        if (i.get() > 0) {
            setWarningMessage(i.get() + " input string(s) did not match the " 
                    + "pattern or contained more groups than expected");
        }
        return new BufferedDataTable[]{t};
    }
    
    private ColumnRearranger createRearranger(final DataTableSpec spec,
            final AtomicInteger errorCounter) 
        throws InvalidSettingsException {
        if (m_settings == null) {
            throw new InvalidSettingsException("Not configuration available.");
        }
        final int colIndex = spec.findColumnIndex(m_settings.getColumn());
        if (colIndex < 0) {
            throw new InvalidSettingsException("No such column in input table: "
                    + m_settings.getColumn());
        }
        DataColumnSpec colSpec = spec.getColumnSpec(colIndex);
        if (!colSpec.getType().isCompatible(StringValue.class)) {
            throw new InvalidSettingsException("Selected column does not " 
                    + "contain strings");
        }
        final Pattern p = m_settings.compile();
        int count = 0;
        String patternS = p.pattern();
        boolean isNextSpecial = false;
        boolean isPreviousAParenthesis = false;
        // count openening parentheses to get group count, ignore
        // escaped parentheses "\(" or non-capturing groups "(?"
        for (int i = 0; i < patternS.length(); i++) {
            switch (patternS.charAt(i)) {
            case '\\': 
                isNextSpecial = !isNextSpecial;
                isPreviousAParenthesis = false;
                break;
            case '(' :
                count += isNextSpecial ? 0 : 1;
                isPreviousAParenthesis = !isNextSpecial;
                isNextSpecial = false;
                break;
            case '?':
                if (isPreviousAParenthesis) {
                    count -= 1;
                }
                // no break;
            default : 
                isNextSpecial = false;
                isPreviousAParenthesis = false;
            }
        }
        final int newColCount = count;
        final DataColumnSpec[] newColSpecs = new DataColumnSpec[count];
        for (int i = 0; i < newColCount; i++) {
            String name = DataTableSpec.getUniqueColumnName(spec, "split_" + i);
            newColSpecs[i] = new DataColumnSpecCreator(
                    name, StringCell.TYPE).createSpec();
        }
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        rearranger.append(new AbstractCellFactory(newColSpecs) {
            /** {@inheritDoc} */
            @Override
            public DataCell[] getCells(final DataRow row) {
                DataCell[] result = new DataCell[newColCount];
                Arrays.fill(result, DataType.getMissingCell());
                DataCell c = row.getCell(colIndex);
                if (c.isMissing()) {
                    return result;
                }
                String s = ((StringValue)c).getStringValue();
                Matcher m = p.matcher(s);
                if (m.matches()) {
                    int max = m.groupCount();
                    if (m.groupCount() > newColCount) {
                        errorCounter.incrementAndGet();
                        max = newColCount;
                    }
                    for (int i = 0; i < max; i++) {
                        // group(0) will return the entire string and is not
                        // included in groupCount, see Matcher API for details
                        String str = m.group(i + 1);
                        if (str != null) { // null for optional groups "(...)?"
                            result[i] = new StringCell(str); 
                        }
                    }
                    return result;
                } else {
                    errorCounter.incrementAndGet();
                    return result;
                }
            }
        });
        return rearranger;
    }

    /** {@inheritDoc} */
    @Override
    protected void reset() {
    }

    /** {@inheritDoc} */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        SimpleParserSettings s = new SimpleParserSettings();
        s.loadSettingsInModel(settings);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_settings = new SimpleParserSettings();
        m_settings.loadSettingsInModel(settings);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        if (m_settings != null) {
            m_settings.saveSettingsTo(settings);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void loadInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    /** {@inheritDoc} */
    @Override
    protected void saveInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

}
