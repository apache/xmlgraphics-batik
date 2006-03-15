package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableColorValue extends AnimatableValue {

    /**
     * The red component.
     */
    protected float red;

    /**
     * The green component.
     */
    protected float green;

    /**
     * The blue component.
     */
    protected float blue;

    /**
     * Creates a new AnimatableColorValue.
     */
    protected AnimatableColorValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableColorValue.
     */
    public AnimatableColorValue(AnimationTarget target, float r, float g,
                                float b) {
        super(target);
        red = r;
        green = g;
        blue = b;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        AnimatableColorValue toColor = (AnimatableColorValue) to;
        AnimatableColorValue accColor = (AnimatableColorValue) accumulation;
        float r = red;
        float g = green;
        float b = blue;
        // XXX Only sRGB colours, and only sRGB interpolation.
        if (to != null) {
            r += interpolation * (toColor.red - r);
            g += interpolation * (toColor.green - g);
            b += interpolation * (toColor.blue - b);
        }
        if (accumulation != null) {
            r += accColor.red;
            g += accColor.green;
            b += accColor.blue;
        }

        AnimatableColorValue res;
        if (result == null) {
            res = new AnimatableColorValue(target);
        } else {
            res = (AnimatableColorValue) result;
        }
        res.red = r;
        res.green = g;
        res.blue = b;
        return res;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableColorValue(target, 0f, 0f, 0f);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return "rgb(" + Math.round(red * 255) + ","
                      + Math.round(green * 255) + ","
                      + Math.round(blue * 255) + ")";
    }
}
