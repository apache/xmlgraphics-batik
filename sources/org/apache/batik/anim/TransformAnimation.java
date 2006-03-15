package org.apache.batik.anim;

import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;

/**
 * An animation class for 'animateTransform' animations.
 */
public class TransformAnimation extends SimpleAnimation {

    /**
     * The transform type.  This should take one of the constants defined
     * in {@link org.w3c.dom.svg.SVGTransform}.
     */
    protected int type;

    /**
     * Creates a new TransformAnimation.
     */
    public TransformAnimation(TimedElement timedElement,
                              AnimatableElement animatableElement,
                              int calcMode,
                              float[] keyTimes,
                              float[] keySplines,
                              boolean additive,
                              boolean cumulative,
                              AnimatableValue[] values,
                              AnimatableValue from,
                              AnimatableValue to,
                              AnimatableValue by,
                              int type) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines,
              additive, cumulative, values, from, to, by);
    }
}
