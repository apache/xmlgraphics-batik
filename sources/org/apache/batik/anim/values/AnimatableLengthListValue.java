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
 * An SVG length list value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableLengthListValue extends AnimatableValue {

    /**
     * The length types.
     */
    protected int[] lengthTypes;

    /**
     * The length values.  These should be one of the constants defined in
     * {@link SVGLength}.
     */
    protected float[] lengthValues;

    /**
     * How to interpret percentage values.  These should be one of the
     * {@link AnimationTarget}.PERCENTAGE_* constants.
     */
    protected int[] percentageInterpretations;
    
    /**
     * Creates a new, uninitialized AnimatableLengthListValue.
     */
    protected AnimatableLengthListValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableLengthListValue.
     */
    public AnimatableLengthListValue(AnimationTarget target, int types[],
                                     float[] values, int[] pcInterps) {
        super(target);
        this.lengthTypes = types;
        this.lengthValues = values;
        this.percentageInterpretations = pcInterps;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        int len = lengthTypes.length;

        AnimatableLengthListValue res;
        if (result == null) {
            res = new AnimatableLengthListValue(target);
            res.lengthTypes = new int[len];
            res.lengthValues = new float[len];
            res.percentageInterpretations = new int[len];
        } else {
            res = (AnimatableLengthListValue) result;
            if (res.lengthTypes == null || res.lengthTypes.length != len) {
                res.lengthTypes = new int[len];
                res.lengthValues = new float[len];
                res.percentageInterpretations = new int[len];
            }
        }

        AnimatableLengthListValue toLengthList = (AnimatableLengthListValue) to;
        AnimatableLengthListValue accLengthList
            = (AnimatableLengthListValue) accumulation;

        if (toLengthList != null && toLengthList.lengthTypes.length == len) {
            for (int i = 0; i < len; i++) {
                float toValue;
                if (!AnimatableLengthValue.compatibleTypes
                        (res.lengthTypes[i], res.percentageInterpretations[i],
                         toLengthList.lengthTypes[i],
                         toLengthList.percentageInterpretations[i])) {
                    res.lengthValues[i] = target.svgToUserSpace
                        (res.lengthValues[i], res.lengthTypes[i],
                         res.percentageInterpretations[i]);
                    res.lengthTypes[i] = SVGLength.SVG_LENGTHTYPE_NUMBER;
                    toValue = to.target.svgToUserSpace
                        (toLengthList.lengthValues[i],
                         toLengthList.lengthTypes[i],
                         toLengthList.percentageInterpretations[i]);
                } else {
                    toValue = toLengthList.lengthValues[i];
                }
                res.lengthValues[i] +=
                    interpolation * (toValue - res.lengthValues[i]);
            }
        }

        if (accLengthList != null && accLengthList.lengthTypes.length == len) {
            for (int i = 0; i < len; i++) {
                float accValue;
                if (!AnimatableLengthValue.compatibleTypes
                        (res.lengthTypes[i], res.percentageInterpretations[i],
                         accLengthList.lengthTypes[i],
                         accLengthList.percentageInterpretations[i])) {
                    res.lengthValues[i] = target.svgToUserSpace
                        (res.lengthValues[i], res.lengthTypes[i],
                         res.percentageInterpretations[i]);
                    res.lengthTypes[i] = SVGLength.SVG_LENGTHTYPE_NUMBER;
                    accValue = accLengthList.target.svgToUserSpace
                        (accLengthList.lengthValues[i],
                         accLengthList.lengthTypes[i],
                         accLengthList.percentageInterpretations[i]);
                } else {
                    accValue = accLengthList.lengthValues[i];
                }
                res.lengthValues[i] += multiplier * accValue;
            }
        }

        return res;
    }

    /**
     * Gets the length types.
     */
    public int[] getLengthTypes() {
        return lengthTypes;
    }

    /**
     * Gets the length values.
     */
    public float[] getLengthValues() {
        return lengthValues;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        float[] vs = new float[lengthValues.length];
        return new AnimatableLengthListValue
            (target, lengthTypes, vs, percentageInterpretations);
    }

    /**
     * Returns the CSS text representation of the value.
     * Length lists can never be used for CSS properties.
     */
    public String getCssText() {
        return null;
    }
}
