package us.norskog.knime.json.simplewriter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SimpleWriter" Node.
 * Writer for flat Json records- no sub-blocks or arrays supported.
 *
 * @author Lance Norskog
 */
public class SimpleWriterNodeFactory 
        extends NodeFactory<SimpleWriterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleWriterNodeModel createNodeModel() {
        return new SimpleWriterNodeModel();
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
    public NodeView<SimpleWriterNodeModel> createNodeView(final int viewIndex,
            final SimpleWriterNodeModel nodeModel) {
        return new SimpleWriterNodeView(nodeModel);
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
        return new SimpleWriterNodeDialog();
    }

}

