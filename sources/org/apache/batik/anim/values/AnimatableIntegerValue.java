package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableIntegerValue extends AnimatableValue {

    /**
     * The value.
     */
    protected int value;

    /**
     * Creates a new AnimatableIntegerValue.
     */
    public AnimatableIntegerValue(AnimationTarget target, int v) {
        super(target);
        value = v;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        int v = value;
        if (to != null) {
            AnimatableIntegerValue toInteger = (AnimatableIntegerValue) to;
            v += value + interpolation * (toInteger.getValue() - value);
        }
        if (accumulation != null) {
            AnimatableIntegerValue accInteger = (AnimatableIntegerValue) accumulation;
            v += accInteger.getValue();
        }
        return new AnimatableIntegerValue(target, v);
    }

    /**
     * Returns the integer value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableIntegerValue(target, 0);
    }
}
