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
import org.w3c.dom.svg.SVGURIReference;

/**
 * This class implements both {@link org.w3c.dom.svg.SVGTextPositioningElement}
 * and {@link SVGURIReference}..
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGURIReferenceTextPositioningElement
    extends    SVGOMTextPositioningElement
    implements SVGURIReference {

    /**
     * Creates a new SVGURIReferenceTextPositioningElement object.
     */
    protected SVGURIReferenceTextPositioningElement() {
    }

    /**
     * Creates a new SVGURIReferenceTextPositioningElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGURIReferenceTextPositioningElement(String prefix,
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
