package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableNumberListValue extends AnimatableValue {

    /**
     * The numbers.
     */
    protected float[] numbers;

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
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        if ((to == null || interpolation == 0) && accumulation == null) {
            return this;
        }
        AnimatableNumberListValue toNumList = (AnimatableNumberListValue) to;
        AnimatableNumberListValue accNumList =
            (AnimatableNumberListValue) accumulation;
        float[] newNums = new float[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            newNums[i] = numbers[i];
        }
        if (to != null) {
            float[] toNums = toNumList.getNumbers();
            if (toNums.length == numbers.length) {
                for (int i = 0; i < toNums.length; i++) {
                    newNums[i] += interpolation * toNums[i];
                }
            }
        }
        if (accumulation != null) {
            float[] accNums = accNumList.getNumbers();
            if (accNums.length == numbers.length) {
                for (int i = 0; i < accNums.length; i++) {
                    newNums[i] += accNums[i];
                }
            }
        }
        return new AnimatableNumberListValue(target, newNums);
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
}
