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
 * A number value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableNumberValue extends AnimatableValue {

    /**
     * The value.
     */
    protected float value;

    /**
     * Creates a new, uninitialized AnimatableNumberValue.
     */
    protected AnimatableNumberValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableNumberValue.
     */
    public AnimatableNumberValue(AnimationTarget target, float v) {
        super(target);
        value = v;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableNumberValue res;
        if (result == null) {
            res = new AnimatableNumberValue(target);
        } else {
            res = (AnimatableNumberValue) result;
        }

        float v = value;
        if (to != null) {
            AnimatableNumberValue toNumber = (AnimatableNumberValue) to;
            v += interpolation * (toNumber.value - value);
        }
        if (accumulation != null) {
            AnimatableNumberValue accNumber = (AnimatableNumberValue) accumulation;
            v += multiplier * accNumber.value;
        }

        res.value = v;
        return res;
    }

    /**
     * Returns the number value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableNumberValue(target, 0);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        String s = Float.toString(value);
        if (s.endsWith(".0")) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }
}
