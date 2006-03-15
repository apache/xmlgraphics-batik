package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableNumberValue extends AnimatableValue {

    /**
     * The value.
     */
    protected float value;

    /**
     * Creates a new, uninitialized AnimatableNumberValue.
     */
    protected AnimatableNumberValue(AnimationTarget target) {
        super(target);
    }
    
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
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        float v = value;
        if (to != null) {
            AnimatableNumberValue toNumber = (AnimatableNumberValue) to;
            v += value + interpolation * (toNumber.value - value);
        }
        if (accumulation != null) {
            AnimatableNumberValue accNumber = (AnimatableNumberValue) accumulation;
            v += accNumber.value;
        }
        
        AnimatableNumberValue res;
        if (result == null) {
            res = new AnimatableNumberValue(target);
        } else {
            res = (AnimatableNumberValue) result;
        }
        res.value = v;
        return res;
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

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return Float.toString(value);
    }
}
