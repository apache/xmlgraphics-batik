/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.apache.batik.css.engine.SVGCSSEngine;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

/**
 * This class provides support for the SVGLocatable interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLocatableSupport {
    /**
     * Creates a new SVGLocatable element.
     */
    public SVGLocatableSupport() {
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getNearestViewportElement()}.
     */
    public static SVGElement getNearestViewportElement(Element e) {
        Element elt = e;
        while (elt != null) {
            elt = SVGCSSEngine.getParentCSSStylableElement(elt);
            if (elt instanceof SVGFitToViewBox) {
                break;
            }
        }
        return (SVGElement)elt;
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getFarthestViewportElement()}.
     */
    public static SVGElement getFarthestViewportElement(Element elt) {
        return (SVGElement)elt.getOwnerDocument().getDocumentElement();
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getBBox()}.
     */
    public static SVGRect getBBox(Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        return new SVGRect() {
                public float getX() {
                    return (float)svgelt.getSVGContext().getBBox().getX();
                }
                public void setX(float x) throws DOMException {
                    throw new DOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                }
                public float getY() {
                    return (float)svgelt.getSVGContext().getBBox().getY();
                }
                public void setY(float y) throws DOMException {
                    throw new DOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                }
                public float getWidth() {
                    return (float)svgelt.getSVGContext().getBBox().getWidth();
                }
                public void setWidth(float width) throws DOMException {
                    throw new DOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                }
                public float getHeight() {
                    return (float)svgelt.getSVGContext().getBBox().getHeight();
                }
                public void setHeight(float height) throws DOMException {
                    throw new DOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                }
            };
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getCTM()}.
     */
    public static SVGMatrix getCTM(Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        return new AbstractSVGMatrix() {
                protected AffineTransform getAffineTransform() {
                    return svgelt.getSVGContext().getCTM();
            }
        };
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getScreenCTM()}.
     */
    public static SVGMatrix getScreenCTM(Element elt) {
	throw new RuntimeException(" !!! TODO: getScreenCTM()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getTransformToElement(SVGElement)}.
     */
    public static SVGMatrix getTransformToElement(Element elt,
                                                  SVGElement element)
	throws SVGException {
        final SVGOMElement currentElt = (SVGOMElement)elt;
        final SVGOMElement targetElt = (SVGOMElement)element;
        return new AbstractSVGMatrix() {
                protected AffineTransform getAffineTransform() {
                    AffineTransform cat = 
                        currentElt.getSVGContext().getGlobalTransform();
                    if (cat == null) {
                        cat = new AffineTransform();
                    }
                    AffineTransform tat = 
                        targetElt.getSVGContext().getGlobalTransform();
                    if (tat == null) {
                        tat = new AffineTransform();
                    }
                    AffineTransform at = new AffineTransform(cat);
                    try {
                        at.preConcatenate(tat.createInverse());
                        return at;
                    } catch (NoninvertibleTransformException ex) {
                        throw currentElt.createSVGException
                            (SVGException.SVG_MATRIX_NOT_INVERTABLE,
                             "noninvertiblematrix",
                             null);
                    }
                }
            };
    }
}
