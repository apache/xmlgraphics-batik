/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * A style reference describes which GraphicsNode and which
 * property of this GraphicsNode should be updated when a 
 * style element (for example a filter) changes due to
 * a modification of the DOM. 
 *
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class StyleReference {
    
    private GraphicsNode node;
    private String styleAttribute;
    
    /**
     * Creates a style reference.
     * @param node the graphics node inpacted.
     * @param styleAttribute the name of the style attribute that is
     * impacted.
     */
    public StyleReference(GraphicsNode node, String styleAttribute) {
        this.node = node;
        this.styleAttribute = styleAttribute;
    }

    /**
     * Returns the graphics node.
     */
    public GraphicsNode getGraphicsNode(){
        return node;
    }
    
    /**
     * Returns the style attribute
     */
    public String getStyleAttribute(){
        return styleAttribute;
    }
    
}
