/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGTextPositioningElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextPositioningElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMTextPositioningElement
    extends    SVGOMTextContentElement
    implements SVGTextPositioningElement {

    /**
     * Creates a new SVGOMTextPositioningElement object.
     */
    protected SVGOMTextPositioningElement() {
    }

    /**
     * Creates a new SVGOMTextPositioningElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMTextPositioningElement(String prefix,
                                          AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getX()}.
     */
    public SVGAnimatedLengthList getX() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getY()}.
     */
    public SVGAnimatedLengthList getY() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getDx()}.
     */
    public SVGAnimatedLengthList getDx() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getDx()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getDy()}.
     */
    public SVGAnimatedLengthList getDy() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getDy()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getRotate()}.
     */
    public SVGAnimatedNumberList getRotate() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getRotate()");
    }

}
