package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableIntegerValue extends AnimatableValue {

    /**
     * The value.
     */
    protected int value;

    /**
     * Creates a new, uninitialized AnimatableIntegerValue.
     */
    protected AnimatableIntegerValue(AnimationTarget target) {
        super(target);
    }

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
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
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
        
        AnimatableIntegerValue res;
        if (result == null) {
            res = new AnimatableIntegerValue(target);
        } else {
            res = (AnimatableIntegerValue) result;
        }
        res.value = v;
        return res;
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

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return Integer.toString(value);
    }
}
