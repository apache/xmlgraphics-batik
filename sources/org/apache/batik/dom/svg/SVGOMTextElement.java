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
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGTextElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMTextElement
    extends    SVGOMTextContentElement
    implements SVGTextElement {
    /**
     * The reference to the x attribute.
     */
    protected WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected WeakReference yReference;

    /**
     * Creates a new SVGOMTextElement object.
     */
    public SVGOMTextElement() {
    }

    /**
     * Creates a new SVGOMTextElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMTextElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "text";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getX()}.
     */
    public SVGAnimatedLength getX() {
	SVGAnimatedLength result;
	if (xReference == null ||
	    (result = (SVGAnimatedLength)xReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "x");
	    xReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGTextElement#getY()}.
     */
    public SVGAnimatedLength getY() {
	SVGAnimatedLength result;
	if (yReference == null ||
	    (result = (SVGAnimatedLength)yReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "y");
	    yReference = new WeakReference(result);
	}
	return result;
    } 

    // SVGTransformable support /////////////////////////////////////////////

    /**
     * The transformable support.
     */
    protected SVGTransformableSupport transformableSupport;

    /**
     * Returns stylableSupport different from null.
     */
    protected final SVGTransformableSupport getTransformableSupport() {
	if (transformableSupport == null) {
	    transformableSupport = new SVGTransformableSupport();
	}
	return transformableSupport;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getNearestViewportElement()}.
     */
    public SVGElement getNearestViewportElement() {
	return getTransformableSupport().getNearestViewportElement(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getFarthestViewportElement()}.
     */
    public SVGElement getFarthestViewportElement() {
	return getTransformableSupport().getFarthestViewportElement(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getTransform()}.
     */
    public SVGAnimatedTransformList getTransform() {
	return getTransformableSupport().getTransform(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getBBox()}.
     */
    public SVGRect getBBox() {
	return getTransformableSupport().getBBox(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getCTM()}.
     */
    public SVGMatrix getCTM() {
	return getTransformableSupport().getCTM(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getScreenCTM()}.
     */
    public SVGMatrix getScreenCTM() {
	return getTransformableSupport().getScreenCTM(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getTransformToElement(SVGElement)}.
     */
    public SVGMatrix getTransformToElement(SVGElement element)
	throws SVGException {
	return getTransformableSupport().getTransformToElement(element, this);
    }
}
