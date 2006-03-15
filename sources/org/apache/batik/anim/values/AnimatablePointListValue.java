package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePointListValue extends AnimatableNumberListValue {

    /**
     * Creates a new, uninitialized AnimatablePointListValue.
     */
    protected AnimatablePointListValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatablePointListValue.
     */
    public AnimatablePointListValue(AnimationTarget target, float[] numbers) {
        super(target, numbers);
    }
    
    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        if (result == null) {
            result = new AnimatablePointListValue(target);
        }
        return super.interpolate(result, to, interpolation, accumulation);
    }
}
