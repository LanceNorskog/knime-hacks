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
 *   05.06.2009 (Fabian Dill): created
 */
package org.knime.timeseries.node.generator;

import java.util.Calendar;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.timeseries.util.DialogComponentCalendar;
import org.knime.timeseries.util.SettingsModelCalendar;

/**
 * The dialog of the time generator node.
 *
 * @author Fabian Dill
 *
 */
public class DateGeneratorNodeDialog extends DefaultNodeSettingsPane {



    /**
     * Constructor.
     */
    public DateGeneratorNodeDialog() {

        final SettingsModelBoolean useCurrentForStart = createUseCurrentForStart();
        final SettingsModelCalendar startingPointModel = createStartingPointModel();

        createNewGroup("Generated rows:");
        addDialogComponent(new DialogComponentNumber(createNumberOfRowsModel(), "Number of rows:", 10));
        closeCurrentGroup();
        addDialogComponent(new DialogComponentBoolean(useCurrentForStart, "Use execution time as starting time"));
        addDialogComponent(new DialogComponentCalendar(startingPointModel, "Starting point:"));
        addDialogComponent(new DialogComponentCalendar(createEndPointModel(), "End point:"));

        useCurrentForStart.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                // synchronize useCurrentforstart with the deactivation of the starting time
                if (useCurrentForStart.getBooleanValue()) {
                    startingPointModel.setEnabled(false);
                } else {
                    startingPointModel.setEnabled(true);
                }
            }
        });

    }


    /**
     * @return the boolean model for using the execution time as the start time of the interval
     */
    static SettingsModelBoolean createUseCurrentForStart() {
        return new SettingsModelBoolean("StartTimeIsExecutionTime", false);
    }


    /**
     *
     * @return the model for the number of rows to be generated
     */
    static SettingsModelInteger createNumberOfRowsModel() {
        return new SettingsModelIntegerBounded("number-of-generated-rows", 1000, 1, Integer.MAX_VALUE);
    }

    /**
     * @return the calendar model for the starting point
     */
    static SettingsModelCalendar createStartingPointModel() {
        Calendar c = Calendar.getInstance(DateAndTimeCell.UTC_TIMEZONE);
        c.roll(Calendar.YEAR, false);
        return new SettingsModelCalendar("starting-point", c);
    }

    /**
     * @return the calendar model for the end point
     */
    static SettingsModelCalendar createEndPointModel() {
        return new SettingsModelCalendar("end-point", null);
    }

}
