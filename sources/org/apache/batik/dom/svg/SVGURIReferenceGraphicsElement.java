/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides support for Xlink to a graphics element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGURIReferenceGraphicsElement
    extends SVGGraphicsElement {

    /**
     * Creates a new SVGURIReferenceGraphicsElement object.
     */
    protected SVGURIReferenceGraphicsElement() {
    }

    /**
     * Creates a new SVGURIReferenceGraphicsElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGURIReferenceGraphicsElement(String prefix,
                                             AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
    }
}
