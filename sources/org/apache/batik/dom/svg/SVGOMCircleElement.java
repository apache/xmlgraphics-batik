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

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGCircleElement;

/**
 * This class implements {@link SVGCircleElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMCircleElement
    extends    SVGGraphicsElement
    implements SVGCircleElement {

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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CIRCLE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        return getAnimatedLengthAttribute
            (null, SVG_CX_ATTRIBUTE, SVG_CIRCLE_CX_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        return getAnimatedLengthAttribute
            (null, SVG_CY_ATTRIBUTE, SVG_CIRCLE_CY_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getR()}.
     */
    public SVGAnimatedLength getR() {
        return getAnimatedLengthAttribute
            (null, SVG_R_ATTRIBUTE, null,
             SVGOMAnimatedLength.OTHER_LENGTH, true);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMCircleElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is additive.
     */
    public boolean isAttributeAdditive(String ns, String ln) {
        return ns == null && (ln.equals(SVG_CX_ATTRIBUTE)
                || ln.equals(SVG_CY_ATTRIBUTE)
                || ln.equals(SVG_R_ATTRIBUTE))
            || super.isAttributeAdditive(ns, ln);
    }

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_CX_ATTRIBUTE)
                    || ln.equals(SVG_CY_ATTRIBUTE)
                    || ln.equals(SVG_R_ATTRIBUTE)) {
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
            if (ln.equals(SVG_CX_ATTRIBUTE)
                    || ln.equals(SVG_CY_ATTRIBUTE)
                    || ln.equals(SVG_R_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
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
            if (ln.equals(SVG_CX_ATTRIBUTE)) {
                return AnimationTarget.PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_CY_ATTRIBUTE)) {
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
            if (ln.equals(SVG_R_ATTRIBUTE)) {
                updateLengthAttributeValue(getR(), val);
                return;
            } else if (ln.equals(SVG_CX_ATTRIBUTE)) {
                updateLengthAttributeValue(getCx(), val);
                return;
            } else if (ln.equals(SVG_CY_ATTRIBUTE)) {
                updateLengthAttributeValue(getCy(), val);
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
