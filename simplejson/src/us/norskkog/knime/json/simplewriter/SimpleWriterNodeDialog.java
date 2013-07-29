package us.norskkog.knime.json.simplewriter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "SimpleWriter" Node.
 * Writer for flat Json records- no sub-blocks or arrays supported.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Lance Norskog
 */
public class SimpleWriterNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring SimpleWriter node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected SimpleWriterNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    SimpleWriterNodeModel.CFGKEY_COUNT,
                    SimpleWriterNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

