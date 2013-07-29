package org.norskog.knime.json.flatparser;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "JSonFlatParser" Node.
 * Simple Json parser for single-level Json blobs. Multiple levels (sub-blocks & arrays) not supported.
 *
 * @author Norskog
 */
public class JSonFlatParserNodeFactory 
        extends NodeFactory<JSonFlatParserNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JSonFlatParserNodeModel createNodeModel() {
        return new JSonFlatParserNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<JSonFlatParserNodeModel> createNodeView(final int viewIndex,
            final JSonFlatParserNodeModel nodeModel) {
        return new JSonFlatParserNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new JSonFlatParserNodeDialog();
    }

}

