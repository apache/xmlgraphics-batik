/*

   Copyright 2000-2003,2006  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.values.AnimatableNumberOptionalNumberValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
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
    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new RuntimeException("!!! TODO: getKernelUnitLengthX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthY()}.
     */
    public SVGAnimatedNumber getKernelUnitLengthY() {
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
                (this, null, SVG_PRESERVE_ALPHA_ATTRIBUTE, false);
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

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_ORDER_ATTRIBUTE)
                    || ln.equals(SVG_KERNEL_MATRIX_ATTRIBUTE)
                    || ln.equals(SVG_DIVISOR_ATTRIBUTE)
                    || ln.equals(SVG_BIAS_ATTRIBUTE)
                    || ln.equals(SVG_TARGET_X_ATTRIBUTE)
                    || ln.equals(SVG_TARGET_Y_ATTRIBUTE)
                    || ln.equals(SVG_EDGE_MODE_ATTRIBUTE)
                    || ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)
                    || ln.equals(SVG_PRESERVE_ALPHA_ATTRIBUTE)) {
                return true;
            }
        }
        return super.isAttributeAnimatable(ns, ln);
    }

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_ORDER_ATTRIBUTE)
                    || ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_OPTIONAL_NUMBER;
            } else if (ln.equals(SVG_KERNEL_MATRIX_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
            } else if (ln.equals(SVG_DIVISOR_ATTRIBUTE)
                    || ln.equals(SVG_BIAS_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
            } else if (ln.equals(SVG_TARGET_X_ATTRIBUTE)
                    || ln.equals(SVG_TARGET_Y_ATTRIBUTE)) {
                return SVGTypes.TYPE_INTEGER;
            } else if (ln.equals(SVG_EDGE_MODE_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_PRESERVE_ALPHA_ATTRIBUTE)) {
                return SVGTypes.TYPE_BOOLEAN;
            }
        }
        return super.getAttributeType(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        if (ns == null) {
            if (ln.equals(SVG_KERNEL_MATRIX_ATTRIBUTE)) {
                updateNumberListAttributeValue(getKernelMatrix(), val);
                return;
            } else if (ln.equals(SVG_DIVISOR_ATTRIBUTE)) {
                updateNumberAttributeValue(getDivisor(), val);
                return;
            } else if (ln.equals(SVG_BIAS_ATTRIBUTE)) {
                updateNumberAttributeValue(getBias(), val);
                return;
            } else if (ln.equals(SVG_TARGET_X_ATTRIBUTE)) {
                updateIntegerAttributeValue(getTargetX(), val);
                return;
            } else if (ln.equals(SVG_TARGET_Y_ATTRIBUTE)) {
                updateIntegerAttributeValue(getTargetY(), val);
                return;
            } else if (ln.equals(SVG_EDGE_MODE_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getEdgeMode(), val);
                return;
            } else if (ln.equals(SVG_PRESERVE_ALPHA_ATTRIBUTE)) {
                updateBooleanAttributeValue(getPreserveAlpha(), val);
                return;
            } else if (ln.equals(SVG_ORDER_ATTRIBUTE)) {
                // XXX Needs testing.
                if (val == null) {
                    updateIntegerAttributeValue(getOrderX(), null);
                    updateIntegerAttributeValue(getOrderY(), null);
                } else {
                    AnimatableNumberOptionalNumberValue anonv =
                        (AnimatableNumberOptionalNumberValue) val;
                    SVGOMAnimatedInteger ai =
                        (SVGOMAnimatedInteger) getOrderX();
                    ai.setAnimatedValue(Math.round(anonv.getNumber()));
                    ai = (SVGOMAnimatedInteger) getOrderY();
                    if (anonv.hasOptionalNumber()) {
                        ai.setAnimatedValue
                            (Math.round(anonv.getOptionalNumber()));
                    } else {
                        ai.setAnimatedValue(Math.round(anonv.getNumber()));
                    }
                }
                return;
            } else if (ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                // XXX Needs testing.
                if (val == null) {
                    updateNumberAttributeValue(getKernelUnitLengthX(), null);
                    updateNumberAttributeValue(getKernelUnitLengthY(), null);
                } else {
                    AnimatableNumberOptionalNumberValue anonv =
                        (AnimatableNumberOptionalNumberValue) val;
                    SVGOMAnimatedNumber an =
                        (SVGOMAnimatedNumber) getKernelUnitLengthX();
                    an.setAnimatedValue(anonv.getNumber());
                    an = (SVGOMAnimatedNumber) getKernelUnitLengthY();
                    if (anonv.hasOptionalNumber()) {
                        an.setAnimatedValue(anonv.getOptionalNumber());
                    } else {
                        an.setAnimatedValue(anonv.getNumber());
                    }
                }
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }

    /**
     * Returns the underlying value of an animatable XML attribute.
     */
    public AnimatableValue getUnderlyingValue(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_KERNEL_MATRIX_ATTRIBUTE)) {
                return getBaseValue(getKernelMatrix());
            } else if (ln.equals(SVG_DIVISOR_ATTRIBUTE)) {
                return getBaseValue(getDivisor());
            } else if (ln.equals(SVG_BIAS_ATTRIBUTE)) {
                return getBaseValue(getBias());
            } else if (ln.equals(SVG_TARGET_X_ATTRIBUTE)) {
                return getBaseValue(getTargetX());
            } else if (ln.equals(SVG_TARGET_Y_ATTRIBUTE)) {
                return getBaseValue(getTargetY());
            } else if (ln.equals(SVG_EDGE_MODE_ATTRIBUTE)) {
                return getBaseValue(getEdgeMode());
            } else if (ln.equals(SVG_PRESERVE_ALPHA_ATTRIBUTE)) {
                return getBaseValue(getPreserveAlpha());
            } else if (ln.equals(SVG_ORDER_ATTRIBUTE)) {
                return getBaseValue(getOrderX(), getOrderY());
            } else if (ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                return getBaseValue(getKernelUnitLengthX(),
                                    getKernelUnitLengthY());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
