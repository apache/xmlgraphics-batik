package org.apache.batik.bridge;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.SimpleAnimation;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.util.SVGConstants;

public class SVGAnimateElementBridge extends SVGAnimationElementBridge {

    /**
     * Returns 'set'.
     */
    public String getLocalName() {
        return SVG_ANIMATE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGAnimateElementBridge();
    }

    /**
     * Creates the animation object for the animation element.
     */
    protected AbstractAnimation createAnimation() {
        AnimatableValue from =
            parseAnimatableValue(SVGConstants.SVG_FROM_ATTRIBUTE);
        AnimatableValue to =
            parseAnimatableValue(SVGConstants.SVG_TO_ATTRIBUTE);
        return new SimpleAnimation(timedElement,
                                   this,
                                   parseCalcMode(),
                                   null,
                                   null,
                                   parseAdditive(),
                                   false,
                                   null,
                                   from,
                                   to,
                                   null);
    }

    /**
     * Returns the parsed calcMode attribute from the animation element.
     */
    protected int parseCalcMode() {
        String calcModeString =
            element.getAttributeNS(null, SVGConstants.SVG_CALC_MODE_ATTRIBUTE);
        if (calcModeString.length() == 0) {
            return getDefaultCalcMode();
        } else if (calcModeString.equals("linear")) {
            return SimpleAnimation.CALC_MODE_LINEAR;
        } else if (calcModeString.equals("discrete")) {
            return SimpleAnimation.CALC_MODE_DISCRETE;
        } else if (calcModeString.equals("paced")) {
            return SimpleAnimation.CALC_MODE_PACED;
        } else if (calcModeString.equals("spline")) {
            return SimpleAnimation.CALC_MODE_SPLINE;
        }
        // XXX
        throw new RuntimeException("Invalid value for 'calcMode' attribute: \"" + calcModeString + "\"");
    }

    /**
     * Returns the parsed 'additive' attribute from the animation element.
     */
    protected boolean parseAdditive() {
        String additiveString =
            element.getAttributeNS(null, SVGConstants.SVG_ADDITIVE_ATTRIBUTE);
        if (additiveString.length() == 0
                || additiveString.equals("replace")) {
            return false;
        } else if (additiveString.equals("sum")) {
            return true;
        }
        // XXX
        throw new RuntimeException("Invalid value for 'additive' attribute: \"" + additiveString + "\"");
    }
    
    /**
     * Returns the calcMode that the animation defaults to if none is specified.
     */
    protected int getDefaultCalcMode() {
        return SimpleAnimation.CALC_MODE_LINEAR;
    }
}
