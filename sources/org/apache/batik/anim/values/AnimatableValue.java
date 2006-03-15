package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;

public abstract class AnimatableValue {

    /**
     * The target of the animation.
     */
    protected AnimationTarget target;

    /**
     * Creates a new AnimatableValue.
     */
    protected AnimatableValue(AnimationTarget target) {
        this.target = target;
    }

    /**
     * Performs interpolation to the given value.
     * @param result the object in which to store the result of the
     *               interpolation, or null if a new object should be created
     * @param to the value this value should be interpolated towards, or null
     *           if no actual interpolation should be performed
     * @param interpolation the interpolation distance, 0 &lt;= interpolation
     *                      &lt;= 1
     * @param accumulation an accumulation to add to the interpolated value 
     */
    public abstract AnimatableValue interpolate(AnimatableValue result,
                                                AnimatableValue to,
                                                float interpolation,
                                                AnimatableValue accumulation);

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public abstract AnimatableValue getZeroValue();

    /**
     * Returns the CSS text representation of the value.
     */
    public abstract String getCssText();
    
    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        return getClass().getName() + "[" + getCssText() + "]";
    }
}
