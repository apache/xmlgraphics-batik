package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.css.engine.value.FloatValue;

import org.w3c.dom.svg.SVGLength;

public class AnimatableLengthValue extends AnimatableValue {

    /**
     * The length type.
     */
    protected int lengthType;

    /**
     * The length value.  This should be one of the constants defined in
     * {@link SVGLength}.
     */
    protected float lengthValue;

    /**
     * Creates a new AnimatableLengthValue with no length.
     */
    protected AnimatableLengthValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableLengthValue.
     */
    public AnimatableLengthValue(AnimationTarget target, int type, float v) {
        super(target);
        lengthType = type;
        lengthValue = v;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        AnimatableLengthValue toLength = (AnimatableLengthValue) to;
        AnimatableLengthValue accLength = (AnimatableLengthValue) accumulation;
        boolean convert = to != null && !compatibleTypes(toLength.lengthType, lengthType)
            || accumulation != null && !compatibleTypes(accLength.lengthType, lengthType);
        int type = convert ? SVGLength.SVG_LENGTHTYPE_NUMBER : lengthType;
        float value = convert ? toType(lengthType, lengthValue, type) : lengthValue;
        if (to != null) {
            float toValue = convert ? toType(toLength.getLengthType(),
                                              toLength.getLengthValue(), type)
                                    : toLength.getLengthValue();
            value += interpolation * (toValue - value);
        }
        if (accumulation != null) {
            float accValue = convert ? toType(accLength.getLengthType(),
                                               accLength.getLengthValue(), type)
                                     : accLength.getLengthValue();
            value += accValue;
        }
        
        AnimatableLengthValue res;
        if (result == null) {
            res = new AnimatableLengthValue(target);
        } else {
            res = (AnimatableLengthValue) result;
        }
        res.lengthType = type;
        res.lengthValue = value;
        return res;
    }

    protected boolean compatibleTypes(int t1, int t2) {
        return t1 == t2
            || t1 == SVGLength.SVG_LENGTHTYPE_NUMBER
                && t2 == SVGLength.SVG_LENGTHTYPE_PX
            || t1 == SVGLength.SVG_LENGTHTYPE_PX
                && t2 == SVGLength.SVG_LENGTHTYPE_NUMBER;
    }

    protected float toType(int from, float value, int to) {
        // XXX ...
        return 0;
    }

    public int getLengthType() {
        return lengthType;
    }

    public float getLengthValue() {
        return lengthValue;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableLengthValue
            (target, SVGLength.SVG_LENGTHTYPE_NUMBER, 0);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return FloatValue.getCssText((short) (lengthType - 1), lengthValue);
    }
}
