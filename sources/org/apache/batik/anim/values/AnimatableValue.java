package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public abstract class AnimatableValue {

    /**
     * The target of the animation.
     */
    protected AnimationTarget target;

    /**
     * Creates a new AnimatableValue.
     */
    protected AnimatableValue(AnimationTarget target) {
        this.target = target;
    }

    /**
     * Performs interpolation to the given value.
     */
    public abstract AnimatableValue interpolate(AnimatableValue to,
                                                float interpolation,
                                                AnimatableValue accumulation);

    /**
     * Adds the given AnimatableValue to this one and returns the result.
     */
    public /*abstract*/ AnimatableValue add(AnimatableValue v) {
        // XXX
        return null;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public abstract AnimatableValue getZeroValue();
}
