/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

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

import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGTextPositioningElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextPositioningElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMTextPositioningElement
    extends    SVGOMTextContentElement
    implements SVGTextPositioningElement {

    /**
     * Creates a new SVGOMTextPositioningElement object.
     */
    protected SVGOMTextPositioningElement() {
    }

    /**
     * Creates a new SVGOMTextPositioningElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMTextPositioningElement(String prefix,
                                          AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getX()}.
     */
    public SVGAnimatedLengthList getX() {
        return SVGTextPositioningElementSupport.getX(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getY()}.
     */
    public SVGAnimatedLengthList getY() {
        return SVGTextPositioningElementSupport.getY(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getDx()}.
     */
    public SVGAnimatedLengthList getDx() {
        return SVGTextPositioningElementSupport.getDx(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getDy()}.
     */
    public SVGAnimatedLengthList getDy() {
        return SVGTextPositioningElementSupport.getDy(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPositioningElement#getRotate()}.
     */
    public SVGAnimatedNumberList getRotate() {
        return SVGTextPositioningElementSupport.getRotate(this);
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_DX_ATTRIBUTE)
                    || ln.equals(SVG_DY_ATTRIBUTE)
                    || ln.equals(SVG_ROTATE_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_DX_ATTRIBUTE)
                    || ln.equals(SVG_DY_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH_LIST;
            } else if (ln.equals(SVG_ROTATE_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
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
            if (ln.equals(SVG_X_ATTRIBUTE) || ln.equals(SVG_DX_ATTRIBUTE)) {
                return PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_Y_ATTRIBUTE) || ln.equals(SVG_DY_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X_ATTRIBUTE)) {
                updateLengthListAttributeValue(getX(), val);
                return;
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                updateLengthListAttributeValue(getY(), val);
                return;
            } else if (ln.equals(SVG_DX_ATTRIBUTE)) {
                updateLengthListAttributeValue(getDx(), val);
                return;
            } else if (ln.equals(SVG_DY_ATTRIBUTE)) {
                updateLengthListAttributeValue(getDy(), val);
                return;
            } else if (ln.equals(SVG_ROTATE_ATTRIBUTE)) {
                updateNumberListAttributeValue(getRotate(), val);
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
            if (ln.equals(SVG_X_ATTRIBUTE)) {
                return getBaseValue
                    (getX(), PERCENTAGE_VIEWPORT_WIDTH);
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                return getBaseValue
                    (getY(), PERCENTAGE_VIEWPORT_HEIGHT);
            } else if (ln.equals(SVG_DX_ATTRIBUTE)) {
                return getBaseValue
                    (getDx(), PERCENTAGE_VIEWPORT_WIDTH);
            } else if (ln.equals(SVG_DY_ATTRIBUTE)) {
                return getBaseValue
                    (getDy(), PERCENTAGE_VIEWPORT_HEIGHT);
            } else if (ln.equals(SVG_ROTATE_ATTRIBUTE)) {
                return getBaseValue(getRotate());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
