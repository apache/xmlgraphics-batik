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

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRadialGradientElement;

/**
 * This class implements {@link SVGRadialGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMRadialGradientElement
    extends    SVGOMGradientElement
    implements SVGRadialGradientElement {

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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_RADIAL_GRADIENT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        return getAnimatedLengthAttribute
            (null, SVG_CX_ATTRIBUTE, SVG_RADIAL_GRADIENT_CX_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        return getAnimatedLengthAttribute
            (null, SVG_CY_ATTRIBUTE, SVG_RADIAL_GRADIENT_CY_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getR()}.
     */
    public SVGAnimatedLength getR() {
        return getAnimatedLengthAttribute
            (null, SVG_R_ATTRIBUTE, SVG_RADIAL_GRADIENT_R_DEFAULT_VALUE,
             SVGOMAnimatedLength.OTHER_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFx()}.
     */
    public SVGAnimatedLength getFx() {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue(null, SVG_FX_ATTRIBUTE);
        if (result == null) {
            result = new AbstractSVGAnimatedLength
                (this, null, SVG_FX_ATTRIBUTE,
                 SVGOMAnimatedLength.HORIZONTAL_LENGTH) {
                    protected String getDefaultValue() {
                        Attr attr = getAttributeNodeNS(null, SVG_CX_ATTRIBUTE);
                        if (attr == null) {
                            return SVG_RADIAL_GRADIENT_CX_DEFAULT_VALUE;
                        }
                        return attr.getValue();
                    }
                };
            putLiveAttributeValue(null, SVG_FX_ATTRIBUTE,
                                  (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFy()}.
     */
    public SVGAnimatedLength getFy() {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue(null, SVG_FY_ATTRIBUTE);
        if (result == null) {
            result = new AbstractSVGAnimatedLength
                (this, null, SVG_FY_ATTRIBUTE,
                 SVGOMAnimatedLength.VERTICAL_LENGTH) {
                    protected String getDefaultValue() {
                        Attr attr = getAttributeNodeNS(null, SVG_CY_ATTRIBUTE);
                        if (attr == null) {
                            return SVG_RADIAL_GRADIENT_CY_DEFAULT_VALUE;
                        }
                        return attr.getValue();
                    }
                };
            putLiveAttributeValue(null, SVG_FY_ATTRIBUTE,
                                  (LiveAttributeValue)result);
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
