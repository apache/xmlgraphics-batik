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
 * A number list in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableNumberListValue extends AnimatableValue {

    /**
     * The numbers.
     */
    protected float[] numbers;

    /**
     * Creates a new, uninitialized AnimatableNumberListValue.
     */
    protected AnimatableNumberListValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableNumberListValue.
     */
    public AnimatableNumberListValue(AnimationTarget target, float[] numbers) {
        super(target);
        this.numbers = numbers;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableNumberListValue res;
        if (result == null) {
            res = new AnimatableNumberListValue(target);
            res.numbers = new float[numbers.length];
        } else {
            res = (AnimatableNumberListValue) result;
            if (res.numbers == null || res.numbers.length != numbers.length) {
                res.numbers = new float[numbers.length];
            }
        }
        
        AnimatableNumberListValue toNumList = (AnimatableNumberListValue) to;
        AnimatableNumberListValue accNumList =
            (AnimatableNumberListValue) accumulation;
        System.arraycopy(numbers, 0, res.numbers, 0, numbers.length);
        if (to != null) {
            if (toNumList.numbers.length == numbers.length) {
                for (int i = 0; i < numbers.length; i++) {
                    res.numbers[i] += interpolation * toNumList.numbers[i];
                }
            }
        }
        if (accumulation != null) {
            if (accNumList.numbers.length == numbers.length) {
                for (int i = 0; i < numbers.length; i++) {
                    res.numbers[i] += multiplier * accNumList.numbers[i];
                }
            }
        }
        return res;
    }

    /**
     * Gets the numbers.
     */
    public float[] getNumbers() {
        return numbers;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        float[] ns = new float[numbers.length];
        return new AnimatableNumberListValue(target, ns);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        sb.append(numbers[0]);
        for (int i = 1; i < numbers.length; i++) {
            sb.append(' ');
            sb.append(numbers[i]);
        }
        return sb.toString();
    }
}
