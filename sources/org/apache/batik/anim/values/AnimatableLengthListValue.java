package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

import org.w3c.dom.svg.SVGLength;

public class AnimatableLengthListValue extends AnimatableValue {

    /**
     * The length types.
     */
    protected int[] lengthTypes;

    /**
     * The length values.  These should be one of the constants defined in
     * {@link SVGLength}.
     */
    protected float[] lengthValues;

    /**
     * Creates a new, uninitialized AnimatableLengthListValue.
     */
    protected AnimatableLengthListValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableLengthListValue.
     */
    public AnimatableLengthListValue(AnimationTarget target, int types[],
                                     float[] values) {
        super(target);
        this.lengthTypes = types;
        this.lengthValues = values;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation) {
        // XXX Can't return 'this'.
        if ((to == null || interpolation == 0) && accumulation == null) {
            return this;
        }
        AnimatableLengthListValue toLengthList = (AnimatableLengthListValue) to;
        AnimatableLengthListValue accLengthList
            = (AnimatableLengthListValue) accumulation;
        int[] newTypes = new int[lengthTypes.length];
        float[] newValues = new float[lengthValues.length];
        for (int i = 0; i < newTypes.length; i++) {
            newTypes[i] = SVGLength.SVG_LENGTHTYPE_NUMBER;
            newValues[i] = target.convertLength
                (lengthTypes[i], lengthValues[i],
                 SVGLength.SVG_LENGTHTYPE_NUMBER);
        }
        if (to != null) {
            int[] toTypes = toLengthList.getLengthTypes();
            float[] toValues = toLengthList.getLengthValues();
            if (toTypes.length == newTypes.length) {
                for (int i = 0; i < toTypes.length; i++) {
                    newValues[i] += interpolation *
                        target.convertLength
                            (toTypes[i], toValues[i],
                             SVGLength.SVG_LENGTHTYPE_NUMBER);
                }
            }
        }
        if (accumulation != null) {
            int[] accTypes = accLengthList.getLengthTypes();
            float[] accValues = accLengthList.getLengthValues();
            if (accTypes.length == newTypes.length) {
                for (int i = 0; i < accTypes.length; i++) {
                    newValues[i] += target.convertLength
                        (accTypes[i], accValues[i],
                         SVGLength.SVG_LENGTHTYPE_NUMBER);
                }
            }
        }

        AnimatableLengthListValue res;
        if (result == null) {
            res = new AnimatableLengthListValue(target);
        } else {
            res = (AnimatableLengthListValue) result;
        }
        res.lengthTypes = newTypes;
        res.lengthValues = newValues;
        return res;
    }

    /**
     * Gets the length types.
     */
    public int[] getLengthTypes() {
        return lengthTypes;
    }

    /**
     * Gets the length values.
     */
    public float[] getLengthValues() {
        return lengthValues;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        float[] vs = new float[lengthValues.length];
        return new AnimatableLengthListValue(target, lengthTypes, vs);
    }

    /**
     * Returns the CSS text representation of the value.
     * Length lists can never be used for CSS properties.
     */
    public String getCssText() {
        return null;
    }
}
