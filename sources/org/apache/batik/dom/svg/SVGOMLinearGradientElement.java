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

import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLinearGradientElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGLinearGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMLinearGradientElement
    extends    SVGOMGradientElement
    implements SVGLinearGradientElement {
    
    /**
     * The reference to the x1 attribute.
     */
    protected WeakReference x1Reference;

    /**
     * The reference to the y1 attribute.
     */
    protected WeakReference y1Reference;

    /**
     * The reference to the x2 attribute.
     */
    protected WeakReference x2Reference;

    /**
     * The reference to the y2 attribute.
     */
    protected WeakReference y2Reference;

    /**
     * Creates a new SVGOMLinearGradientElement object.
     */
    public SVGOMLinearGradientElement() {
    }

    /**
     * Creates a new SVGOMLinearGradientElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMLinearGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_LINEAR_GRADIENT;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLinearGradientElement#getX1()}.
     */
    public SVGAnimatedLength getX1() {
	SVGAnimatedLength result;
	if (x1Reference == null ||
	    (result = (SVGAnimatedLength)x1Reference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, ATTR_X1);
	    x1Reference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLinearGradientElement#getY1()}.
     */
    public SVGAnimatedLength getY1() {
	SVGAnimatedLength result;
	if (y1Reference == null ||
	    (result = (SVGAnimatedLength)y1Reference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, ATTR_Y1);
	    y1Reference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLinearGradientElement#getX2()}.
     */
    public SVGAnimatedLength getX2() {
	SVGAnimatedLength result;
	if (x2Reference == null ||
	    (result = (SVGAnimatedLength)x2Reference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, ATTR_X2);
	    x2Reference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLinearGradientElement#getY2()}.
     */
    public SVGAnimatedLength getY2() {
	SVGAnimatedLength result;
	if (y2Reference == null ||
	    (result = (SVGAnimatedLength)y2Reference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, ATTR_Y2);
	    y2Reference = new WeakReference(result);
	}
	return result;
    } 
}
