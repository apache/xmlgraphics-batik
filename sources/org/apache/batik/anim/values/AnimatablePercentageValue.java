package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePercentageValue extends AnimatableNumberValue {

    /**
     * Creates a new AnimatablePercentageValue.
     */
    public AnimatablePercentageValue(AnimationTarget target, float v) {
        super(target, v);
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatablePercentageValue(target, 0);
    }
}
