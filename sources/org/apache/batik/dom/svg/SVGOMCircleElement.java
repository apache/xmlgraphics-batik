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
import org.w3c.dom.svg.SVGCircleElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGCircleElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMCircleElement
    extends    SVGGraphicsElement
    implements SVGCircleElement {

    /**
     * The DefaultAttributeValueProducer for cx.
     */
    protected final static DefaultAttributeValueProducer
        CX_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_CIRCLE_CX_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for cy.
     */
    protected final static DefaultAttributeValueProducer
        CY_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_CIRCLE_CY_DEFAULT_VALUE;
                }
            };

    /**
     * The reference to the cx attribute.
     */
    protected transient WeakReference cxReference;

    /**
     * The reference to the cy attribute.
     */
    protected transient WeakReference cyReference;

    /**
     * The reference to the r attribute.
     */
    protected transient WeakReference rReference;

    /**
     * Creates a new SVGOMCircleElement object.
     */
    protected SVGOMCircleElement() {
    }

    /**
     * Creates a new SVGOMCircleElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMCircleElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CIRCLE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGCircleElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        SVGAnimatedLength result;
        if (cxReference == null ||
            (result = (SVGAnimatedLength)cxReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_CX_ATTRIBUTE,
                                             CX_DEFAULT_VALUE_PRODUCER);
            cxReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGCircleElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        SVGAnimatedLength result;
        if (cyReference == null ||
            (result = (SVGAnimatedLength)cyReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_CY_ATTRIBUTE,
                                             CY_DEFAULT_VALUE_PRODUCER);
            cyReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGCircleElement#getR()}.
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
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMCircleElement();
    }
}
