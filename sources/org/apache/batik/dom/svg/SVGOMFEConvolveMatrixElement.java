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
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEConvolveMatrixElement;

/**
 * This class implements {@link SVGFEConvolveMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEConvolveMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEConvolveMatrixElement {

    /**
     * The 'edgeMode' attribute values.
     */
    protected final static String[] EDGE_MODE_VALUES = {
        "",
        SVG_DUPLICATE_VALUE,
        SVG_WRAP_VALUE,
        SVG_NONE_VALUE
    };

    /**
     * Creates a new SVGOMFEConvolveMatrixElement object.
     */
    protected SVGOMFEConvolveMatrixElement() {
    }

    /**
     * Creates a new SVGOMFEConvolveMatrixElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEConvolveMatrixElement(String prefix,
                                        AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_CONVOLVE_MATRIX_TAG;
    }

    /**
     * <b>DOM</b>: Implements { @link SVGFEConvolveMatrixElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getEdgeMode()}.
     */
    public SVGAnimatedEnumeration getEdgeMode() {
        return getAnimatedEnumerationAttribute
            (null, SVG_EDGE_MODE_ATTRIBUTE, EDGE_MODE_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getKernelMatrix()}.
     */
    public SVGAnimatedNumberList getKernelMatrix() {
        throw new RuntimeException("!!! TODO: getKernelMatrix()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getOrderX()}.
     */
    public SVGAnimatedInteger getOrderX() {
        throw new RuntimeException("!!! TODO: getOrderX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getOrderY()}.
     */
    public SVGAnimatedInteger getOrderY() {
        throw new RuntimeException("!!! TODO: getOrderY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getTargetX()}.
     */
    public SVGAnimatedInteger getTargetX() {
        // Default value relative to orderX...
        throw new RuntimeException("!!! TODO: getTargetX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getTargetY()}.
     */
    public SVGAnimatedInteger getTargetY() {
        // Default value relative to orderY...
        throw new RuntimeException("!!! TODO: getTargetY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getDivisor()}.
     */
    public SVGAnimatedNumber getDivisor() {
        // Default value relative to kernel matrix...
        throw new RuntimeException("!!! TODO: getDivisor()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getBias()}.
     */
    public SVGAnimatedNumber getBias() {
        return getAnimatedNumberAttribute(null, SVG_BIAS_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthX()}.
     */
    public SVGAnimatedLength getKernelUnitLengthX() {
        throw new RuntimeException("!!! TODO: getKernelUnitLengthX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthY()}.
     */
    public SVGAnimatedLength getKernelUnitLengthY() {
        throw new RuntimeException("!!! TODO: getKernelUnitLengthY()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getPreserveAlpha()}.
     */
    public SVGAnimatedBoolean getPreserveAlpha() {
        LiveAttributeValue lav;
        lav = getLiveAttributeValue(null, SVG_PRESERVE_ALPHA_ATTRIBUTE);
        if (lav == null) {
            lav = new SVGOMAnimatedBoolean
                (this, null, SVG_PRESERVE_ALPHA_ATTRIBUTE,
                 getAttributeNodeNS(null, SVG_PRESERVE_ALPHA_ATTRIBUTE),
                 "false");
            putLiveAttributeValue(null, SVG_PRESERVE_ALPHA_ATTRIBUTE, lav);
        }
        return (SVGAnimatedBoolean)lav;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEConvolveMatrixElement();
    }
}
