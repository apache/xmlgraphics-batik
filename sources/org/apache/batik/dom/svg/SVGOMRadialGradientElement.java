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
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRadialGradientElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGRadialGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMRadialGradientElement
    extends    SVGOMGradientElement
    implements SVGRadialGradientElement {

    /**
     * The reference to the cx attribute.
     */
    protected WeakReference cxReference;

    /**
     * The reference to the cy attribute.
     */
    protected WeakReference cyReference;

    /**
     * The reference to the r attribute.
     */
    protected WeakReference rReference;

    /**
     * The reference to the fx attribute.
     */
    protected WeakReference fxReference;

    /**
     * The reference to the fy attribute.
     */
    protected WeakReference fyReference;

    /**
     * Creates a new SVGOMRadialGradientElement object.
     */
    protected SVGOMRadialGradientElement() {
    }

    /**
     * Creates a new SVGOMRadialGradientElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMRadialGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_RADIAL_GRADIENT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        SVGAnimatedLength result;
        if (cxReference == null ||
            (result = (SVGAnimatedLength)cxReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_CX_ATTRIBUTE, null);
            cxReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        SVGAnimatedLength result;
        if (cyReference == null ||
            (result = (SVGAnimatedLength)cyReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_CY_ATTRIBUTE, null);
            cyReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getR()}.
     */
    public SVGAnimatedLength getR() {
        SVGAnimatedLength result;
        if (rReference == null ||
            (result = (SVGAnimatedLength)rReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_R_ATTRIBUTE, null);
            rReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFx()}.
     */
    public SVGAnimatedLength getFx() {
        SVGAnimatedLength result;
        if (fxReference == null ||
            (result = (SVGAnimatedLength)fxReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, ATTR_FX, null);
            fxReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFy()}.
     */
    public SVGAnimatedLength getFy() {
        SVGAnimatedLength result;
        if (fyReference == null ||
            (result = (SVGAnimatedLength)fyReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, ATTR_FY, null);
            fyReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMRadialGradientElement();
    }
}
