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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEOffsetElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEOffsetElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEOffsetElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEOffsetElement {

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the dx attribute.
     */
    protected transient WeakReference dxReference;

    /**
     * The reference to the dy attribute.
     */
    protected transient WeakReference dyReference;

    /**
     * Creates a new SVGOMFEOffsetElement object.
     */
    protected SVGOMFEOffsetElement() {
    }

    /**
     * Creates a new SVGOMFEOffsetElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEOffsetElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_OFFSET_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEOffsetElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
	SVGAnimatedString result;
	if (inReference == null ||
	    (result = (SVGAnimatedString)inReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, SVG_IN_ATTRIBUTE);
	    inReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEOffsetElement#getDx()}.
     */
    public SVGAnimatedNumber getDx() {
	SVGAnimatedNumber result;
	if (dxReference == null ||
	    (result = (SVGAnimatedNumber)dxReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, SVG_DX_ATTRIBUTE, null);
	    dxReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEOffsetElement#getDy()}.
     */
    public SVGAnimatedNumber getDy() {
	SVGAnimatedNumber result;
	if (dyReference == null ||
	    (result = (SVGAnimatedNumber)dyReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, SVG_DY_ATTRIBUTE, null);
	    dyReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEOffsetElement();
    }
}
