/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import java.util.EventObject;

import org.w3c.dom.Element;

/**
 * This class represents a CSS event fired by a CSSEngine.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSEngineEvent extends EventObject {

    /**
     * The event target.
     */
    protected Element element;

    /**
     * The changed properties indexes.
     */
    protected int[] properties;

    /**
     * Creates a new CSSEngineEvent.
     */
    public CSSEngineEvent(CSSEngine source, Element elt, int[] props) {
        super(source);
        element = elt;
        properties = props;
    }

    /**
     * Returns the target element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Returns the changed properties.
     */
    public int[] getProperties() {
        return properties;
    }
}
