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
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedTextRotate;
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
     * The reference to the x attribute.
     */
    protected WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected WeakReference yReference;

    /**
     * The reference to the dx attribute.
     */
    protected WeakReference dxReference;

    /**
     * The reference to the dy attribute.
     */
    protected WeakReference dyReference;

    /**
     * The reference to the rotate attribute.
     */
    protected WeakReference rotateReference;

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
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getX()}.
     */
    public SVGAnimatedLengthList getX() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getX()");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getY()}.
     */
    public SVGAnimatedLengthList getY() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getY()");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getDx()}.
     */
    public SVGAnimatedLengthList getDx() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getDx()");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getDy()}.
     */
    public SVGAnimatedLengthList getDy() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getDy()");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getRotate()}.
     */
    public SVGAnimatedTextRotate getRotate() {
        throw new RuntimeException(" !!! SVGOMTextPositioningElement.getRotate()");
    }

}
