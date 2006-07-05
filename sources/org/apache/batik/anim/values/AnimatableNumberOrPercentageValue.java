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

/**
 * A number-or-percentage value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableNumberOrPercentageValue extends AnimatableNumberValue {

    /**
     * Whether the number is a percentage.
     */
    protected boolean isPercentage;

    /**
     * Creates a new, uninitialized AnimatableNumberOrPercentageValue.
     */
    protected AnimatableNumberOrPercentageValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableNumberOrPercentageValue with a number.
     */
    public AnimatableNumberOrPercentageValue(AnimationTarget target, float n) {
        super(target, n);
    }

    /**
     * Creates a new AnimatableNumberOrPercentageValue with either a number
     * or a percentage.
     */
    public AnimatableNumberOrPercentageValue(AnimationTarget target, float n,
                                             boolean isPercentage) {
        super(target, n);
        this.isPercentage = isPercentage;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableNumberOrPercentageValue res;
        if (result == null) {
            res = new AnimatableNumberOrPercentageValue(target);
        } else {
            res = (AnimatableNumberOrPercentageValue) result;
        }

        float newValue;
        boolean newIsPercentage;

        AnimatableNumberOrPercentageValue toValue
            = (AnimatableNumberOrPercentageValue) to;
        AnimatableNumberOrPercentageValue accValue
            = (AnimatableNumberOrPercentageValue) accumulation;

        if (to != null) {
            if (toValue.isPercentage == isPercentage) {
                newValue = value + interpolation * (toValue.value - value);
                newIsPercentage = isPercentage;
            } else {
                if (interpolation >= 0.5) {
                    newValue = toValue.value;
                    newIsPercentage = toValue.isPercentage;
                } else {
                    newValue = value;
                    newIsPercentage = isPercentage;
                }
            }
        } else {
            newValue = value;
            newIsPercentage = isPercentage;
        }

        if (accumulation != null && accValue.isPercentage == newIsPercentage) {
            newValue += multiplier * accValue.value;
        }

        if (res.value != newValue
                || res.isPercentage != newIsPercentage) {
            res.value = newValue;
            res.isPercentage = newIsPercentage;
            res.hasChanged = true;
        }
        return res;
    }

    /**
     * Returns whether the value is a percentage.
     */
    public boolean isPercentage() {
        return isPercentage;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableNumberOrPercentageValue(target, 0, isPercentage);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        String s = Float.toString(value);
        if (s.endsWith(".0")) {
            sb.append(s.substring(0, s.length() - 2));
        } else {
            sb.append(s);
        }
        if (isPercentage) {
            sb.append('%');
        }
        return sb.toString();
    }
}
