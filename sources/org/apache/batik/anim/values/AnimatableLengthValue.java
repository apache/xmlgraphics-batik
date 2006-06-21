/*

   Copyright 2006  The Apache Software Foundation 

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
package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

import org.w3c.dom.svg.SVGLength;

/**
 * An SVG length value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableLengthValue extends AnimatableValue {

    /**
     * Length units.
     */
    protected final static String[] UNITS = {
        "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc"
    };

    /**
     * The length type.
     */
    protected int lengthType;

    /**
     * The length value.  This should be one of the constants defined in
     * {@link SVGLength}.
     */
    protected float lengthValue;

    /**
     * How to interpret percentage values.  One of the
     * {@link AnimationTarget}.PERCENTAGE_* constants.
     */
    protected int percentageInterpretation;
    
    /**
     * Creates a new AnimatableLengthValue with no length.
     */
    protected AnimatableLengthValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableLengthValue.
     */
    public AnimatableLengthValue(AnimationTarget target, int type, float v,
                                 int pcInterp) {
        super(target);
        lengthType = type;
        lengthValue = v;
        percentageInterpretation = pcInterp;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableLengthValue res;
        if (result == null) {
            res = new AnimatableLengthValue(target);
        } else {
            res = (AnimatableLengthValue) result;
        }

        res.lengthType = lengthType;
        res.lengthValue = lengthValue;
        res.percentageInterpretation = percentageInterpretation;

        if (to != null) {
            AnimatableLengthValue toLength = (AnimatableLengthValue) to;
            float toValue;
            if (!compatibleTypes
                    (res.lengthType, res.percentageInterpretation,
                     toLength.lengthType, toLength.percentageInterpretation)) {
                res.lengthValue = target.svgToUserSpace
                    (res.lengthValue, res.lengthType,
                     res.percentageInterpretation);
                res.lengthType = SVGLength.SVG_LENGTHTYPE_NUMBER;
                toValue = toLength.target.svgToUserSpace
                    (toLength.lengthValue, toLength.lengthType,
                     toLength.percentageInterpretation);
            } else {
                toValue = toLength.lengthValue;
            }
            res.lengthValue += interpolation * (toValue - res.lengthValue);
        }

        if (accumulation != null) {
            AnimatableLengthValue accLength = (AnimatableLengthValue) accumulation;
            float accValue;
            if (!compatibleTypes
                    (res.lengthType, res.percentageInterpretation,
                     accLength.lengthType,
                     accLength.percentageInterpretation)) {
                res.lengthValue = target.svgToUserSpace
                    (res.lengthValue, res.lengthType,
                     res.percentageInterpretation);
                res.lengthType = SVGLength.SVG_LENGTHTYPE_NUMBER;
                accValue = accLength.target.svgToUserSpace
                    (accLength.lengthValue, accLength.lengthType,
                     accLength.percentageInterpretation);
            } else {
                accValue = accLength.lengthValue;
            }
            res.lengthValue += multiplier * accValue;
        }
        
        return res;
    }

    /**
     * Determines if two SVG length types are compatible.
     * @param t1 the first SVG length type
     * @param pi1 the first percentage interpretation type
     * @param t2 the second SVG length type
     * @param pi2 the second percentage interpretation type
     */
    public static boolean compatibleTypes(int t1, int pi1, int t2, int pi2) {
        return t1 == t2
            && (t1 != SVGLength.SVG_LENGTHTYPE_PERCENTAGE || pi1 == pi2)
            || t1 == SVGLength.SVG_LENGTHTYPE_NUMBER
                && t2 == SVGLength.SVG_LENGTHTYPE_PX
            || t1 == SVGLength.SVG_LENGTHTYPE_PX
                && t2 == SVGLength.SVG_LENGTHTYPE_NUMBER;
    }

    public int getLengthType() {
        return lengthType;
    }

    public float getLengthValue() {
        return lengthValue;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableLengthValue
            (target, SVGLength.SVG_LENGTHTYPE_NUMBER, 0f,
             AnimationTarget.PERCENTAGE_VIEWPORT_SIZE);
    }

    /**
     * Returns the CSS text representation of the value.  This could use
     * org.apache.batik.css.engine.value.FloatValue.getCssText, but we don't
     * want a dependency on the CSS package.
     */
    public String getCssText() {
        String s = String.valueOf(lengthValue);
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + UNITS[lengthType - 1];
    }
}
