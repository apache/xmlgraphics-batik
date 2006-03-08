package org.apache.batik.anim.values;

import java.awt.Color;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableColorValue extends AnimatableValue {

    /**
     * The color.
     */
    protected Color color;

    /**
     * Creates a new AnimatableColorValue.
     */
    protected AnimatableColorValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableColorValue.
     */
    public AnimatableColorValue(AnimationTarget target, Color c) {
        super(target);
        color = c;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        if ((to == null || interpolation == 0) && accumulation == null) {
            return this;
        }
        float[] comp = this.getRGBColorComponents(null);
        AnimatableColorValue toColor = (AnimatableColorValue) to;
        AnimatableColorValue accColor = (AnimatableColorValue) accumulation;
        // XXX Only sRGB colours, and only sRGB interpolation.
        if (to != null) {
            float[] comp2 = toColor.getRGBColorComponents(null);
            comp[0] += interpolation * (comp2[0] - comp[0]);
            comp[1] += interpolation * (comp2[1] - comp[1]);
            comp[2] += interpolation * (comp2[2] - comp[2]);
        }
        if (accumulation != null) {
            float[] comp2 = accColor.getRGBColorComponents(null);
            comp[0] += interpolation * (comp2[0] - comp[0]);
            comp[1] += interpolation * (comp2[1] - comp[1]);
            comp[2] += interpolation * (comp2[2] - comp[2]);
        }
        return new AnimatableColorValue
            (target, new Color(comp[0], comp[1], comp[2]));
    }

    /**
     * Returns the sRGB color components of this color value.
     */
    public float[] getRGBColorComponents(float[] comp) {
        return color.getRGBColorComponents(comp);
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableColorValue(target, Color.BLACK);
    }
}
