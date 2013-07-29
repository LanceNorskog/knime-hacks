package org.norskog.knime.json.flatparser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "JSonFlatParser" Node.
 * Simple Json parser for single-level Json blobs. Multiple levels (sub-blocks & arrays) not supported.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Norskog
 */
public class JSonFlatParserNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring JSonFlatParser node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected JSonFlatParserNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    JSonFlatParserNodeModel.CFGKEY_COUNT,
                    JSonFlatParserNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

