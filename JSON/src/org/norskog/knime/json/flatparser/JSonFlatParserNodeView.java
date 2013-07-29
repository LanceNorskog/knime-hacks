package org.norskog.knime.json.flatparser;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "JSonFlatParser" Node.
 * Simple Json parser for single-level Json blobs. Multiple levels (sub-blocks & arrays) not supported.
 *
 * @author Norskog
 */
public class JSonFlatParserNodeView extends NodeView<JSonFlatParserNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link JSonFlatParserNodeModel})
     */
    protected JSonFlatParserNodeView(final JSonFlatParserNodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        JSonFlatParserNodeModel nodeModel = 
            (JSonFlatParserNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

