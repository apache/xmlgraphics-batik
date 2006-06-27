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
 * A number-optional-number value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableNumberOptionalNumberValue extends AnimatableValue {

    /**
     * The first number.
     */
    protected float number;

    /**
     * Whether the optional number is present.
     */
    protected boolean hasOptionalNumber;

    /**
     * The optional number.
     */
    protected float optionalNumber;

    /**
     * Creates a new, uninitialized AnimatableNumberOptionalNumberValue.
     */
    protected AnimatableNumberOptionalNumberValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableNumberOptionalNumberValue with one number.
     */
    public AnimatableNumberOptionalNumberValue(AnimationTarget target,
                                               float n) {
        super(target);
        number = n;
    }

    /**
     * Creates a new AnimatableNumberOptionalNumberValue with two numbers.
     */
    public AnimatableNumberOptionalNumberValue(AnimationTarget target, float n,
                                               float on) {
        super(target);
        number = n;
        optionalNumber = on;
        hasOptionalNumber = true;
    }

    /**
     * Performs interpolation to the given value.  Number-optional-number
     * values cannot be interpolated.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableNumberOptionalNumberValue res;
        if (result == null) {
            res = new AnimatableNumberOptionalNumberValue(target);
        } else {
            res = (AnimatableNumberOptionalNumberValue) result;
        }

        float newNumber, newOptionalNumber;
        boolean newHasOptionalNumber;

        if (to != null && interpolation >= 0.5) {
            AnimatableNumberOptionalNumberValue toValue
                = (AnimatableNumberOptionalNumberValue) to;
            newNumber = toValue.number;
            newOptionalNumber = toValue.optionalNumber;
            newHasOptionalNumber = toValue.hasOptionalNumber;
        } else {
            newNumber = number;
            newOptionalNumber = optionalNumber;
            newHasOptionalNumber = hasOptionalNumber;
        }

        if (res.number != newNumber
                || res.hasOptionalNumber != newHasOptionalNumber
                || res.optionalNumber != newOptionalNumber) {
            res.number = number;
            res.optionalNumber = optionalNumber;
            res.hasOptionalNumber = hasOptionalNumber;
            res.hasChanged = true;
        }
        return res;
    }

    /**
     * Returns the first number.
     */
    public float getNumber() {
        return number;
    }

    /**
     * Returns whether the optional number is present.
     */
    public boolean hasOptionalNumber() {
        return hasOptionalNumber;
    }

    /**
     * Returns the optional number.
     */
    public float getOptionalNumber() {
        return optionalNumber;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return null;
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        String s = Float.toString(number);
        if (s.endsWith(".0")) {
            sb.append(s.substring(0, s.length() - 2));
        } else {
            sb.append(s);
        }
        if (hasOptionalNumber) {
            sb.append(' ');
            s = Float.toString(optionalNumber);
            if (s.endsWith(".0")) {
                sb.append(s.substring(0, s.length() - 2));
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
