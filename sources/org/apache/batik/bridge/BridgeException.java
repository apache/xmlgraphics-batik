/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

/**
 * The root class from which exceptions thrown by the bridge shall be derived.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeException extends RuntimeException {

    protected GraphicsNode node;

    /**
     * Constructs a new <tt>BridgeException</tt>.
     * @param msg the exception message
     */
    public BridgeException(String msg) {
        this(msg, null);
    }

    /**
     * Constructs a new <tt>BridgeException</tt>.
     * @param msg the exception message
     * @param node the graphics node on which the error occured
     */
    public BridgeException(String msg, GraphicsNode node) {
        super(msg);
        this.node = node;
    }

    /**
     * Returns the graphics node on which the error occured.
     */
    public GraphicsNode getGraphicsNode() {
        return node;
    }
}
