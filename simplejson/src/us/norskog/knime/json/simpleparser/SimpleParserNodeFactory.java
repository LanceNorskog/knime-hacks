package us.norskog.knime.json.simpleparser;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SimpleParser" Node.
 * Simple high-speed json parser for flat records. No sub-blocks or arrays supported.
 *
 * @author Lance Norskog
 */
public class SimpleParserNodeFactory 
        extends NodeFactory<SimpleParserNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleParserNodeModel createNodeModel() {
        return new SimpleParserNodeModel();
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
    public NodeView<SimpleParserNodeModel> createNodeView(final int viewIndex,
            final SimpleParserNodeModel nodeModel) {
        return new SimpleParserNodeView(nodeModel);
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
        return new SimpleParserNodeDialog();
    }

}

