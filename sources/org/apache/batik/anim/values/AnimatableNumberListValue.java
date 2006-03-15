package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

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
                                       AnimatableValue accumulation) {
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
                    res.numbers[i] += accNumList.numbers[i];
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
