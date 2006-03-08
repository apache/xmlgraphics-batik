package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableNumberValue extends AnimatableValue {

    /**
     * The value.
     */
    protected float value;

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
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        float v = value;
        if (to != null) {
            AnimatableNumberValue toNumber = (AnimatableNumberValue) to;
            v += value + interpolation * (toNumber.getValue() - value);
        }
        if (accumulation != null) {
            AnimatableNumberValue accNumber = (AnimatableNumberValue) accumulation;
            v += accNumber.getValue();
        }
        return new AnimatableNumberValue(target, v);
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
}
