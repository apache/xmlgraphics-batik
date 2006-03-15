package org.apache.batik.anim;

import org.apache.batik.anim.values.AnimatableValue;

/**
 * An interface for animatable elements to expose their underlying values
 * to the compositing functions in {@link AbstractAnimation}.
 * 
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public interface AnimatableElement {
    
    /**
     * Returns the underlying value of the animated attribute.  Used for
     * composition of additive animations.
     */
    AnimatableValue getUnderlyingValue();
}
