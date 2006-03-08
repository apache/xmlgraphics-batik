package org.apache.batik.anim.values;

import java.awt.Color;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePaintValue extends AnimatableColorValue {

    // Constants for paintType.
    public static int PAINT_NONE              = 0;
    public static int PAINT_CURRENT_COLOR     = 1;
    public static int PAINT_COLOR             = 2;
    public static int PAINT_URI               = 3;
    public static int PAINT_URI_NONE          = 4;
    public static int PAINT_URI_CURRENT_COLOR = 5;
    public static int PAINT_URI_COLOR         = 6;
    public static int PAINT_INHERIT           = 7;

    /**
     * The type of paint.
     */
    protected int paintType;

    /**
     * The URI of the referenced paint server.
     */
    protected String uri;

    /**
     * Creates a new AnimatablePaintValue.
     */
    protected AnimatablePaintValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatablePaintValue.
     */
    protected AnimatablePaintValue(AnimationTarget target, Color c) {
        super(target, c);
    }

    /**
     * Creates a new AnimatablePaintValue for a 'none' value.
     */
    public static AnimatablePaintValue createNonePaintValue
            (AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = PAINT_NONE;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a 'currentColor' value.
     */
    public static AnimatablePaintValue createCurrentColorPaintValue
            (AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = PAINT_CURRENT_COLOR;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a color value.
     */
    public static AnimatablePaintValue createColorPaintValue
            (AnimationTarget target, Color c) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, c);
        v.paintType = PAINT_COLOR;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a URI reference.
     */
    public static AnimatablePaintValue createURIPaintValue
            (AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = PAINT_URI;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a URI reference with a
     * 'none' fallback.
     */
    public static AnimatablePaintValue createURINonePaintValue
            (AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = PAINT_URI_NONE;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a URI reference with a
     * 'currentColor' fallback.
     */
    public static AnimatablePaintValue createURICurrentColorPaintValue
            (AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = PAINT_URI_CURRENT_COLOR;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a URI reference with a
     * color fallback.
     */
    public static AnimatablePaintValue createURIColorPaintValue
            (AnimationTarget target, String uri, Color c) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, c);
        v.uri = uri;
        v.paintType = PAINT_URI_COLOR;
        return v;
    }

    /**
     * Creates a new AnimatablePaintValue for a 'inherit' value.
     */
    public static AnimatablePaintValue createInheritPaintValue
            (AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = PAINT_INHERIT;
        return v;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        if (paintType == PAINT_COLOR) {
            if (to instanceof AnimatablePaintValue) {
                AnimatablePaintValue toPaint = (AnimatablePaintValue) to;
                if (toPaint.getPaintType() == PAINT_COLOR) {
                    return super.interpolate(to, interpolation, accumulation);
                }
            } else if (to instanceof AnimatableColorValue) {
                return super.interpolate(to, interpolation, accumulation);
            }
        }
        return this;
    }

    /**
     * Returns the type of paint this value represents.
     */
    public int getPaintType() {
        return paintType;
    }

    /**
     * Returns the paint server URI.
     */
    public String getURI() {
        return uri;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return AnimatablePaintValue.createColorPaintValue(target, Color.BLACK);
    }
}
