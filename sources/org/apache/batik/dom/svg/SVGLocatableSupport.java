/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getY() {
                    return (float)svgelt.getSVGContext().getBBox().getY();
                }
                public void setY(float y) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getWidth() {
                    return (float)svgelt.getSVGContext().getBBox().getWidth();
                }
                public void setWidth(float width) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getHeight() {
                    return (float)svgelt.getSVGContext().getBBox().getHeight();
                }
                public void setHeight(float height) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
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
        final SVGOMElement svgelt  = (SVGOMElement)elt;
        return new AbstractSVGMatrix() {
                protected AffineTransform getAffineTransform() {
                    SVGContext context = svgelt.getSVGContext();
                    AffineTransform ret = context.getGlobalTransform();
                    AffineTransform scrnTrans = context.getScreenTransform();
                    if (scrnTrans != null)
                        ret.preConcatenate(scrnTrans);
                    return ret;
                }
            };
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
