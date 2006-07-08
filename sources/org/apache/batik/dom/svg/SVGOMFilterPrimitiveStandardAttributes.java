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

import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

/**
 * This class represents a SVGElement with support for standard filter
 * attributes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMFilterPrimitiveStandardAttributes
    extends SVGStylableElement
    implements SVGFilterPrimitiveStandardAttributes {

    /**
     * Creates a new SVGOMFilterPrimitiveStandardAttributes object.
     */
    protected SVGOMFilterPrimitiveStandardAttributes() {
    }

    /**
     * Creates a new SVGOMFilterPrimitiveStandardAttributes object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMFilterPrimitiveStandardAttributes(String prefix,
                                                     AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getX()}.
     */
    public SVGAnimatedLength getX() {
        return getAnimatedLengthAttribute
            (null, SVG_X_ATTRIBUTE, SVG_FILTER_PRIMITIVE_X_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getY()}.
     */
    public SVGAnimatedLength getY() {
        return getAnimatedLengthAttribute
            (null, SVG_Y_ATTRIBUTE, SVG_FILTER_PRIMITIVE_Y_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        return getAnimatedLengthAttribute
            (null, SVG_WIDTH_ATTRIBUTE,
             SVG_FILTER_PRIMITIVE_WIDTH_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, true);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        return getAnimatedLengthAttribute
            (null, SVG_HEIGHT_ATTRIBUTE,
             SVG_FILTER_PRIMITIVE_HEIGHT_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, true);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getResult()}.
     */
    public SVGAnimatedString getResult() {
        return getAnimatedStringAttribute(null, SVG_RESULT_ATTRIBUTE);
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        return ns == null
            && (ln.equals(SVG_X_ATTRIBUTE) || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_WIDTH_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_RESULT_ATTRIBUTE))
            || super.isAttributeAnimatable(ns, ln);
    }

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_WIDTH_ATTRIBUTE)
                    || ln.equals(SVG_HEIGHT_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
            } else if (ln.equals(SVG_RESULT_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            }
        }
        return super.getAttributeType(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Gets how percentage values are interpreted by the given attribute.
     */
    protected short getAttributePercentageInterpretation(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE) || ln.equals(SVG_WIDTH_ATTRIBUTE)) {
                return AnimationTarget.PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_Y_ATTRIBUTE) || ln.equals(SVG_HEIGHT_ATTRIBUTE)) {
                return AnimationTarget.PERCENTAGE_VIEWPORT_HEIGHT;
            }
        }
        return super.getAttributePercentageInterpretation(ns, ln);
    }

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        if (ns == null) {
            if (ln.equals(SVG_RESULT_ATTRIBUTE)) {
                updateStringAttributeValue(getResult(), val);
                return;
            } else if (ln.equals(SVG_X_ATTRIBUTE)) {
                updateLengthAttributeValue(getX(), val);
                return;
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                updateLengthAttributeValue(getY(), val);
                return;
            } else if (ln.equals(SVG_WIDTH_ATTRIBUTE)) {
                updateLengthAttributeValue(getWidth(), val);
                return;
            } else if (ln.equals(SVG_HEIGHT_ATTRIBUTE)) {
                updateLengthAttributeValue(getHeight(), val);
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
