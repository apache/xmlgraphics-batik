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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGTextElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMTextElement
    extends    SVGGraphicsElement
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

    // SVGTextContentElement //////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getTextLength()}.
     */
    public SVGAnimatedLength getTextLength() {
        throw new RuntimeException(" !!! SVGOMTextElement.getTextLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getLengthAdjust()}.
     */
    public SVGAnimatedEnumeration getLengthAdjust() {
        throw new RuntimeException(" !!! SVGOMTextElement.getLengthAdjust()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getNumberOfChars()}.
     */
    public int getNumberOfChars() {
        throw new RuntimeException(" !!! SVGOMTextElement.getNumberOfChars()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getComputedTextLength()}.
     */
    public float getComputedTextLength() {
        throw new RuntimeException(" !!! SVGOMTextElement.getComputedTextLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getSubStringLength(int,int)}.
     */
    public float getSubStringLength(int charnum, int nchars)
        throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getSubStringLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getStartPositionOfChar(int)}.
     */
    public SVGPoint getStartPositionOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getStartPositionOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getEndPositionOfChar(int)}.
     */
    public SVGPoint getEndPositionOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getEndPositionOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getExtentOfChar(int)}.
     */
    public SVGRect getExtentOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getExtentOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getRotationOfChar(int)}.
     */
    public float getRotationOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getRotationOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getCharNumAtPosition(SVGPoint)}.
     */
    public int getCharNumAtPosition(SVGPoint point) {
        throw new RuntimeException(" !!! SVGOMTextElement.getCharNumAtPosition()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int,int)}.
     */
    public void selectSubString(int charnum, int nchars)
        throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextElement.getSubStringLength()");
    }
}
