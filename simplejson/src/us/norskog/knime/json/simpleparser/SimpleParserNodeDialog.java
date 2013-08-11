package us.norskog.knime.json.simpleparser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "SimpleParser" Node.
 * Simple high-speed json parser for flat records. No sub-blocks or arrays supported.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Lance Norskog
 */
public class SimpleParserNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring SimpleParser node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected SimpleParserNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentString(
                new SettingsModelString(
                    SimpleParserNodeModel.CFGKEY_FIELDNAME,
                    SimpleParserNodeModel.DEFAULT_FIELDNAME),
                    "Field:"));
    }
}

