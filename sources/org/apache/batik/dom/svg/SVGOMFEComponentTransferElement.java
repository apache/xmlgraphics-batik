/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEComponentTransferElement;

/**
 * This class implements {@link SVGFEComponentTransferElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEComponentTransferElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEComponentTransferElement {

    /**
     * Creates a new SVGOMFEComponentTransferElement object.
     */
    protected SVGOMFEComponentTransferElement() {
    }

    /**
     * Creates a new SVGOMFEComponentTransferElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEComponentTransferElement(String prefix,
                                           AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COMPONENT_TRANSFER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEComponentTransferElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEComponentTransferElement();
    }
}
