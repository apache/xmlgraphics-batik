package org.apache.batik.bridge;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.SetAnimation;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.util.SVGConstants;

/**
 * A bridge class for the 'set' animation element.
 */
public class SVGSetElementBridge extends SVGAnimationElementBridge {

    /**
     * Returns 'set'.
     */
    public String getLocalName() {
        return SVG_SET_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGSetElementBridge();
    }

    /**
     * Creates the animation object for the animation element.
     */
    protected AbstractAnimation createAnimation() {
        AnimatableValue to =
            parseAnimatableValue(SVGConstants.SVG_TO_ATTRIBUTE);
        return new SetAnimation(timedElement, this, to);
    }
}
