package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePercentageValue extends AnimatableNumberValue {

    /**
     * Creates a new, uninitialized AnimatablePercentageValue.
     */
    protected AnimatablePercentageValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatablePercentageValue.
     */
    public AnimatablePercentageValue(AnimationTarget target, float v) {
        super(target, v);
    }
    
    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        if (result == null) {
            result = new AnimatablePercentageValue(target);
        }
        return super.interpolate(result, to, interpolation, accumulation);
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatablePercentageValue(target, 0);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return super.getCssText() + "%";
    }
}
