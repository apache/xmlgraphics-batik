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
import org.w3c.dom.svg.SVGEllipseElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGEllipseElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMEllipseElement
    extends    SVGGraphicsElement
    implements SVGEllipseElement {
    /**
     * The reference to the cx attribute.
     */
    protected WeakReference cxReference;

    /**
     * The reference to the cy attribute.
     */
    protected WeakReference cyReference;

    /**
     * The reference to the rx attribute.
     */
    protected WeakReference rxReference;

    /**
     * The reference to the ry attribute.
     */
    protected WeakReference ryReference;

    /**
     * Creates a new SVGOMEllipseElement object.
     */
    public SVGOMEllipseElement() {
    }

    /**
     * Creates a new SVGOMEllipseElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMEllipseElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "ellipse";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGEllipseElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
	SVGAnimatedLength result;
	if (cxReference == null ||
	    (result = (SVGAnimatedLength)cxReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "cx");
	    cxReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGEllipseElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
	SVGAnimatedLength result;
	if (cyReference == null ||
	    (result = (SVGAnimatedLength)cyReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "cy");
	    cyReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGEllipseElement#getRx()}.
     */
    public SVGAnimatedLength getRx() {
	SVGAnimatedLength result;
	if (rxReference == null ||
	    (result = (SVGAnimatedLength)rxReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "rx");
	    rxReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGEllipseElement#getRy()}.
     */
    public SVGAnimatedLength getRy() {
	SVGAnimatedLength result;
	if (ryReference == null ||
	    (result = (SVGAnimatedLength)ryReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "ry");
	    ryReference = new WeakReference(result);
	}
	return result;
    } 
}
