package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

/**
 * A class for AnimatableValues that cannot be interpolated.
 */
public abstract class UninterpolableAnimatableValue extends AnimatableValue {

    /**
     * Creates a new UninterpolableAnimatableValue.
     */
    protected UninterpolableAnimatableValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        return this;
    }
}
