package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public class AnimatableLengthOrIdentValue extends AnimatableLengthValue {

    /**
     * Whether this value is an identifier.
     */
    protected boolean isIdent;
    
    /**
     * The identifier.
     */
    protected String ident;
    
    /**
     * Creates a new, uninitialized AnimatableLengthOrIdentValue.
     */
    protected AnimatableLengthOrIdentValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableLengthOrIdentValue for a length value.
     */
    public AnimatableLengthOrIdentValue(AnimationTarget target, int type,
                                        float v) {
        super(target, type, v);
    }

    /**
     * Creates a new AnimatableLengthOrIdentValue for an identifier value.
     */
    public AnimatableLengthOrIdentValue(AnimationTarget target, String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        if (isIdent) {
            return ident;
        }
        return super.getCssText();
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to, float interpolation,
                                       AnimatableValue accumulation) {
        AnimatableLengthOrIdentValue res;
        if (result == null) {
            res = new AnimatableLengthOrIdentValue(target);
        } else {
            res = (AnimatableLengthOrIdentValue) result;
        }
        
        if (isIdent) {
            res.ident = ident;
            res.isIdent = true;
        } else {
            super.interpolate(res, to, interpolation, accumulation);
            res.isIdent = false;
        }
        return res;
    }
}
