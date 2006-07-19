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

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEColorMatrixElement;

/**
 * This class implements {@link SVGFEColorMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEColorMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEColorMatrixElement {

    /**
     * The 'type' attribute values.
     */
    protected final static String[] TYPE_VALUES = {
        "",
        SVG_MATRIX_VALUE,
        SVG_SATURATE_VALUE,
        SVG_HUE_ROTATE_VALUE,
        SVG_LUMINANCE_TO_ALPHA_VALUE
    };

    /**
     * Creates a new SVGOMFEColorMatrixElement object.
     */
    protected SVGOMFEColorMatrixElement() {
    }

    /**
     * Creates a new SVGOMFEColorMatrixElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEColorMatrixElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COLOR_MATRIX_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEColorMatrixElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEColorMatrixElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
        return getAnimatedEnumerationAttribute
            (null, SVG_TYPE_ATTRIBUTE, TYPE_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEColorMatrixElement#getValues()}.
     */
    public SVGAnimatedNumberList getValues() {
        throw new RuntimeException("!!! TODO: getValues()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEColorMatrixElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_TYPE_ATTRIBUTE)
                    || ln.equals(SVG_VALUES_ATTRIBUTE)) {
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
            } else if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_VALUES_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                updateStringAttributeValue(getIn1(), val);
                return;
            } else if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getType(), val);
                return;
            } else if (ln.equals(SVG_VALUES_ATTRIBUTE)) {
                updateNumberListAttributeValue(getValues(), val);
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return getBaseValue(getIn1());
            } else if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return getBaseValue(getType());
            } else if (ln.equals(SVG_VALUES_ATTRIBUTE)) {
                return getBaseValue(getValues());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
