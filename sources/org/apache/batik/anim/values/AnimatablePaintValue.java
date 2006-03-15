package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatablePaintValue extends AnimatableColorValue {

    // Constants for paintType.
    public static final int PAINT_NONE              = 0;
    public static final int PAINT_CURRENT_COLOR     = 1;
    public static final int PAINT_COLOR             = 2;
    public static final int PAINT_URI               = 3;
    public static final int PAINT_URI_NONE          = 4;
    public static final int PAINT_URI_CURRENT_COLOR = 5;
    public static final int PAINT_URI_COLOR         = 6;
    public static final int PAINT_INHERIT           = 7;

    /**
     * The type of paint.
     */
    protected int paintType;

    /**
     * The URI of the referenced paint server.
     */
    protected String uri;

    /**
     * Creates a new, uninitialized AnimatablePaintValue.
     */
    protected AnimatablePaintValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatablePaintValue.
     */
    protected AnimatablePaintValue(AnimationTarget target, float r, float g,
                                   float b) {
        super(target, r, g, b);
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
            (AnimationTarget target, float r, float g, float b) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
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
            (AnimationTarget target, String uri, float r, float g, float b) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
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
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        AnimatablePaintValue res;
        if (result == null) {
            res = new AnimatablePaintValue(target);
        } else {
            res = (AnimatablePaintValue) result;
        }
        
        if (paintType == PAINT_COLOR) {
            AnimatablePaintValue toPaint = (AnimatablePaintValue) to;
            if (toPaint.paintType == PAINT_COLOR) {
                res.paintType = PAINT_COLOR;
                return super.interpolate(res, to, interpolation, accumulation);
            }
        }
        res.paintType = paintType;
        res.uri = uri;
        res.red = red;
        res.green = green;
        res.blue = blue;
        return res;
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
        return AnimatablePaintValue.createColorPaintValue(target, 0f, 0f, 0f);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        switch (paintType) {
            case PAINT_NONE:
                return "none";
            case PAINT_CURRENT_COLOR:
                return "currentColor";
            case PAINT_COLOR:
                return super.getCssText();
            case PAINT_URI:
                return "url(" + uri + ")";
            case PAINT_URI_NONE:
                return "url(" + uri + ") none";
            case PAINT_URI_CURRENT_COLOR:
                return "url(" + uri + ") currentColor";
            case PAINT_URI_COLOR:
                return "url(" + uri + ") " + super.getCssText();
            default: // PAINT_INHERIT
                return "inherit";
        }
    }
}
