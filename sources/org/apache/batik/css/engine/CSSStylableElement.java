/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import java.net.URL;

import org.w3c.dom.Element;

/**
 * This interface must be implemented by the DOM elements which needs
 * CSS support.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSStylableElement extends Element {
    
    /**
     * Returns the computed style of this element/pseudo-element.
     */
    StyleMap getComputedStyleMap(String pseudoElement);

    /**
     * Sets the computed style of this element/pseudo-element.
     */
    void setComputedStyleMap(String pseudoElement, StyleMap sm);

    /**
     * Returns the ID of this element.
     */
    String getXMLId();

    /**
     * Returns the class of this element.
     */
    String getCSSClass();

    /**
     * Returns the CSS base URL of this element.
     */
    URL getCSSBase();

    /**
     * Tells whether this element is an instance of the given pseudo
     * class.
     */
    boolean isPseudoInstanceOf(String pseudoClass);
}
