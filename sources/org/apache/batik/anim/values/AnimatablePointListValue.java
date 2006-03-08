package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePointListValue extends AnimatableNumberListValue {

    /**
     * The numbers.
     */
    protected float[] numbers;

    /**
     * Creates a new AnimatablePointListValue.
     */
    public AnimatablePointListValue(AnimationTarget target, float[] numbers) {
        super(target, numbers);
    }
}
