package org.apache.batik.anim;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.timing.TimedElement;

/**
 * An animation class for 'set' animations.
 */
public class SetAnimation extends AbstractAnimation {

    /**
     * The set animation value.
     */
    protected AnimatableValue to;

    /**
     * Creates a new SetAnimation.
     */
    public SetAnimation(TimedElement timedElement,
                        AnimatableElement animatableElement,
                        AnimatableValue to) {
        super(timedElement, animatableElement);
        this.to = to;
    }

    protected void sampledAt(float simpleTime, float simpleDur,
                             int repeatIteration) {
        if (value == null) {
            value = to;
            markDirty();
        }
    }

    protected void sampledLastValue(int repeatIteration) {
        if (value == null) {
            value = to;
            markDirty();
        }
    }
}
