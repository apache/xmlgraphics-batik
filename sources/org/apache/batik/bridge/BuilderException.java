/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import org.apache.batik.gvt.GraphicsNode;

/**
 * Thrown when the <tt>GVTBuilder</tt> has encountered an error.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BuilderException extends RuntimeException {

    protected Element element;
    protected GraphicsNode root;

    /**
     * Constructs a new <tt>BuilderException</tt>.
     * @param msg the exception message
     * @param e the element on which the error occured
     */
    public BuilderException(Element e, String msg) {
        super(msg);
        this.element = e;
    }

    /**
     * Returns the element responsible on the error.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Returns the root graphics node built.
     */
    public GraphicsNode getRootGraphicsNode() {
        return root;
    }

    /**
     * Sets the current state of the GVT tree.
     * @param root the root graphics node
     */
    public void setRootGraphicsNode(GraphicsNode root) {
        this.root = root;
    }
}
