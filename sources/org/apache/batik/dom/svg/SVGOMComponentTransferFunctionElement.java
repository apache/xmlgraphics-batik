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
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGComponentTransferFunctionElement;

/**
 * This class represents the component transfer function elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMComponentTransferFunctionElement
    extends    SVGOMElement
    implements SVGComponentTransferFunctionElement {

    /**
     * The 'type' attribute values.
     */
    protected final static String[] TYPE_VALUES = {
        "",
        SVG_IDENTITY_VALUE,
        SVG_TABLE_VALUE,
        SVG_DISCRETE_VALUE,
        SVG_LINEAR_VALUE,
        SVG_GAMMA_VALUE
    };

    /**
     * Creates a new Element object.
     */
    protected SVGOMComponentTransferFunctionElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected SVGOMComponentTransferFunctionElement(String prefix,
                                                    AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
        return getAnimatedEnumerationAttribute
            (null, SVG_TYPE_ATTRIBUTE, TYPE_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getTableValues()}.
     */
    public SVGAnimatedNumberList getTableValues() {
        throw new RuntimeException("!!! TODO: getTableValues");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getSlope()}.
     */
    public SVGAnimatedNumber getSlope() {
        return getAnimatedNumberAttribute(null, SVG_SLOPE_ATTRIBUTE, 1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getIntercept()}.
     */
    public SVGAnimatedNumber getIntercept() {
        return getAnimatedNumberAttribute(null, SVG_INTERCEPT_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getAmplitude()}.
     */
    public SVGAnimatedNumber getAmplitude() {
        return getAnimatedNumberAttribute(null, SVG_AMPLITUDE_ATTRIBUTE, 1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getExponent()}.
     */
    public SVGAnimatedNumber getExponent() {
        return getAnimatedNumberAttribute(null, SVG_EXPONENT_ATTRIBUTE, 1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getOffset()}.
     */
    public SVGAnimatedNumber getOffset() {
        return getAnimatedNumberAttribute(null, SVG_OFFSET_ATTRIBUTE, 0f);
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_TYPE_ATTRIBUTE)
                    || ln.equals(SVG_TABLE_VALUES_ATTRIBUTE)
                    || ln.equals(SVG_SLOPE_ATTRIBUTE)
                    || ln.equals(SVG_INTERCEPT_ATTRIBUTE)
                    || ln.equals(SVG_AMPLITUDE_ATTRIBUTE)
                    || ln.equals(SVG_EXPONENT_ATTRIBUTE)
                    || ln.equals(SVG_OFFSET_ATTRIBUTE)) {
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
            if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_TABLE_VALUES_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
            } else if (ln.equals(SVG_SLOPE_ATTRIBUTE)
                    || ln.equals(SVG_INTERCEPT_ATTRIBUTE)
                    || ln.equals(SVG_AMPLITUDE_ATTRIBUTE)
                    || ln.equals(SVG_EXPONENT_ATTRIBUTE)
                    || ln.equals(SVG_OFFSET_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
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
            if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getType(), val);
                return;
            } else if (ln.equals(SVG_TABLE_VALUES_ATTRIBUTE)) {
                updateNumberListAttributeValue(getTableValues(), val);
                return;
            } else if (ln.equals(SVG_SLOPE_ATTRIBUTE)) {
                updateNumberAttributeValue(getSlope(), val);
                return;
            } else if (ln.equals(SVG_INTERCEPT_ATTRIBUTE)) {
                updateNumberAttributeValue(getIntercept(), val);
                return;
            } else if (ln.equals(SVG_AMPLITUDE_ATTRIBUTE)) {
                updateNumberAttributeValue(getAmplitude(), val);
                return;
            } else if (ln.equals(SVG_EXPONENT_ATTRIBUTE)) {
                updateNumberAttributeValue(getExponent(), val);
                return;
            } else if (ln.equals(SVG_OFFSET_ATTRIBUTE)) {
                updateNumberAttributeValue(getOffset(), val);
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
            if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return getBaseValue(getType());
            } else if (ln.equals(SVG_TABLE_VALUES_ATTRIBUTE)) {
                return getBaseValue(getTableValues());
            } else if (ln.equals(SVG_SLOPE_ATTRIBUTE)) {
                return getBaseValue(getSlope());
            } else if (ln.equals(SVG_INTERCEPT_ATTRIBUTE)) {
                return getBaseValue(getIntercept());
            } else if (ln.equals(SVG_AMPLITUDE_ATTRIBUTE)) {
                return getBaseValue(getAmplitude());
            } else if (ln.equals(SVG_EXPONENT_ATTRIBUTE)) {
                return getBaseValue(getExponent());
            } else if (ln.equals(SVG_OFFSET_ATTRIBUTE)) {
                return getBaseValue(getOffset());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
