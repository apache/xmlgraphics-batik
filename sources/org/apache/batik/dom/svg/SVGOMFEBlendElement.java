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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEBlendElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEBlendElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEBlendElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEBlendElement
{
    /**
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected WeakReference in2Reference;

    /**
     * Creates a new SVGOMFEBlendElement object.
     */
    public SVGOMFEBlendElement() {
    }

    /**
     * Creates a new SVGOMFEBlendElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEBlendElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "feBlend";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEBlendElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
	SVGAnimatedString result;
	if (inReference == null ||
	    (result = (SVGAnimatedString)inReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, ATTR_IN);
	    inReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEBlendElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
	SVGAnimatedString result;
	if (in2Reference == null ||
	    (result = (SVGAnimatedString)in2Reference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, ATTR_IN2);
	    in2Reference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEBlendElement#getMode()}.
     */
    public SVGAnimatedEnumeration getMode() {
        throw new RuntimeException(" !!! SVGFEBlendElement#getMode()");
    }

}
