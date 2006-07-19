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
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLineElement;

/**
 * This class implements {@link SVGLineElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMLineElement
    extends    SVGGraphicsElement
    implements SVGLineElement {

    /**
     * Creates a new SVGOMLineElement object.
     */
    protected SVGOMLineElement() {
    }

    /**
     * Creates a new SVGOMLineElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMLineElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_LINE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGLineElement#getX1()}.
     */
    public SVGAnimatedLength getX1() {
        return getAnimatedLengthAttribute
            (null, SVG_X1_ATTRIBUTE, SVG_LINE_X1_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGLineElement#getY1()}.
     */
    public SVGAnimatedLength getY1() {
        return getAnimatedLengthAttribute
            (null, SVG_Y1_ATTRIBUTE, SVG_LINE_Y1_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGLineElement#getX2()}.
     */
    public SVGAnimatedLength getX2() {
        return getAnimatedLengthAttribute
            (null, SVG_X2_ATTRIBUTE, SVG_LINE_X2_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGLineElement#getY2()}.
     */
    public SVGAnimatedLength getY2() {
        return getAnimatedLengthAttribute
            (null, SVG_Y2_ATTRIBUTE, SVG_LINE_Y2_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
    } 

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMLineElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X1_ATTRIBUTE)
                    || ln.equals(SVG_Y1_ATTRIBUTE)
                    || ln.equals(SVG_X2_ATTRIBUTE)
                    || ln.equals(SVG_Y2_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X1_ATTRIBUTE)
                    || ln.equals(SVG_Y1_ATTRIBUTE)
                    || ln.equals(SVG_X2_ATTRIBUTE)
                    || ln.equals(SVG_Y2_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X1_ATTRIBUTE) || ln.equals(SVG_X2_ATTRIBUTE)) {
                return PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_Y1_ATTRIBUTE) || ln.equals(SVG_Y2_ATTRIBUTE)) {
                return PERCENTAGE_VIEWPORT_HEIGHT;
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
            if (ln.equals(SVG_X1_ATTRIBUTE)) {
                updateLengthAttributeValue(getX1(), val);
                return;
            } else if (ln.equals(SVG_Y1_ATTRIBUTE)) {
                updateLengthAttributeValue(getY1(), val);
                return;
            } else if (ln.equals(SVG_X2_ATTRIBUTE)) {
                updateLengthAttributeValue(getX2(), val);
                return;
            } else if (ln.equals(SVG_Y2_ATTRIBUTE)) {
                updateLengthAttributeValue(getY2(), val);
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
            if (ln.equals(SVG_X1_ATTRIBUTE)) {
                return getBaseValue
                    (getX1(), PERCENTAGE_VIEWPORT_WIDTH);
            } else if (ln.equals(SVG_Y1_ATTRIBUTE)) {
                return getBaseValue
                    (getY1(), PERCENTAGE_VIEWPORT_HEIGHT);
            } else if (ln.equals(SVG_X2_ATTRIBUTE)) {
                return getBaseValue
                    (getX2(), PERCENTAGE_VIEWPORT_WIDTH);
            } else if (ln.equals(SVG_Y2_ATTRIBUTE)) {
                return getBaseValue
                    (getY2(), PERCENTAGE_VIEWPORT_HEIGHT);
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
