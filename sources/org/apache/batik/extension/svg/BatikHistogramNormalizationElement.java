/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import org.w3c.dom.Node;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.extension.PrefixableStylableExtensionElement;


/**
 * This class implements a histogram normalization extension to SVG.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class BatikHistogramNormalizationElement
    extends    PrefixableStylableExtensionElement 
    implements BatikExtConstants {

    /**
     * Creates a new BatikHistogramNormalizationElement object.
     */
    protected BatikHistogramNormalizationElement() {
    }

    /**
     * Creates a new BatikHistogramNormalizationElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public BatikHistogramNormalizationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new BatikHistogramNormalizationElement();
    }
}
