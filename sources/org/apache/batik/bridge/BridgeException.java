/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

/**
 * Thrown when the bridge has detected an error.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeException extends RuntimeException {

    /** The element on which the error occured. */
    protected Element e;

    /** The error code. */
    protected String code;

    /** The paramters to use for the error message. */
    protected Object [] params;

    /** The line number on which the error occured. */
    protected int line;

    /** The graphics node that represents the current state of the GVT tree. */
    protected GraphicsNode node;

    /**
     * Constructs a new <tt>BridgeException</tt> with the specified parameters.
     *
     * @param e the element on which the error occured
     * @param code the error code
     * @param params the parameters to use for the error message
     */
    public BridgeException(Element e, String code, Object [] params) {
        this.e = e;
        this.code = code;
        this.params = params;
    }

    /**
     * Returns the element on which the error occurred.
     */
    public Element getElement() {
        return e;
    }

    /**
     * Returns the line number on which the error occurred.
     */
    public void setLineNumber(int line) {
        this.line = line;
    }

    /**
     * Sets the graphics node that represents the current GVT tree built.
     *
     * @param node the graphics node
     */
    public void setGraphicsNode(GraphicsNode node) {
        this.node = node;
    }

    /**
     * Returns the graphics node that represents the current GVT tree built.
     */
    public GraphicsNode getGraphicsNode() {
        return node;
    }

    /**
     * Returns the error message according to the error code and parameters.
     */
    public String getMessage() {
        String uri = ((SVGDocument)e.getOwnerDocument()).getURL();
        Object [] fullparams = new Object[params.length+3];
        fullparams[0] = uri;
        fullparams[1] = new Integer(line);
        fullparams[2] = e.getLocalName();
        for (int i=0; i < params.length; ++i) {
            fullparams[i+3] = params[i];
        }
        return Messages.formatMessage(code, fullparams);
    }

    /**
     * Returns the exception's error code
     */
    public String getCode() {
        return code;
    }
}
