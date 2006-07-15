/*

   Copyright 2006  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.util.ArrayList;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.SMILConstants;
import org.apache.batik.anim.SimpleAnimation;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.util.SVGConstants;

/**
 * Bridge class for the 'animate' animation element.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class SVGAnimateElementBridge extends SVGAnimationElementBridge {

    /**
     * Returns 'animate'.
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
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        AnimatableValue from =
            parseAnimatableValue(SVGConstants.SVG_FROM_ATTRIBUTE);
        AnimatableValue to =
            parseAnimatableValue(SVGConstants.SVG_TO_ATTRIBUTE);
        AnimatableValue by =
            parseAnimatableValue(SVGConstants.SVG_BY_ATTRIBUTE);
        return new SimpleAnimation(timedElement,
                                   this,
                                   parseCalcMode(),
                                   parseKeyTimes(),
                                   null,
                                   parseAdditive(),
                                   parseAccumulate(),
                                   parseValues(),
                                   from,
                                   to,
                                   by);
    }

    /**
     * Returns the parsed 'calcMode' attribute from the animation element.
     */
    protected int parseCalcMode() {
        String calcModeString =
            element.getAttributeNS(null, SVGConstants.SVG_CALC_MODE_ATTRIBUTE);
        if (calcModeString.length() == 0) {
            return getDefaultCalcMode();
        } else if (calcModeString.equals(SMILConstants.SMIL_LINEAR_VALUE)) {
            return SimpleAnimation.CALC_MODE_LINEAR;
        } else if (calcModeString.equals(SMILConstants.SMIL_DISCRETE_VALUE)) {
            return SimpleAnimation.CALC_MODE_DISCRETE;
        } else if (calcModeString.equals(SMILConstants.SMIL_PACED_VALUE)) {
            return SimpleAnimation.CALC_MODE_PACED;
        } else if (calcModeString.equals
                (SMILConstants.SMIL_CALC_MODE_ATTRIBUTE)) {
            return SimpleAnimation.CALC_MODE_SPLINE;
        }
        throw new BridgeException
            (ctx, element, ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] { SVGConstants.SVG_CALC_MODE_ATTRIBUTE,
                            calcModeString });
    }

    /**
     * Returns the parsed 'additive' attribute from the animation element.
     */
    protected boolean parseAdditive() {
        String additiveString =
            element.getAttributeNS(null, SVGConstants.SVG_ADDITIVE_ATTRIBUTE);
        if (additiveString.length() == 0 ||
                additiveString.equals(SMILConstants.SMIL_REPLACE_VALUE)) {
            return false;
        } else if (additiveString.equals(SMILConstants.SMIL_SUM_VALUE)) {
            return true;
        }
        throw new BridgeException
            (ctx, element, ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] { SVGConstants.SVG_ADDITIVE_ATTRIBUTE,
                            additiveString });
    }
    
    /**
     * Returns the parsed 'accumulate' attribute from the animation element.
     */
    protected boolean parseAccumulate() {
        String accumulateString =
            element.getAttributeNS(null, SVGConstants.SVG_ACCUMULATE_ATTRIBUTE);
        if (accumulateString.length() == 0 ||
                accumulateString.equals(SMILConstants.SMIL_NONE_VALUE)) {
            return false;
        } else if (accumulateString.equals(SMILConstants.SMIL_SUM_VALUE)) {
            return true;
        }
        throw new BridgeException
            (ctx, element, ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] { SVGConstants.SVG_ACCUMULATE_ATTRIBUTE,
                            accumulateString });
    }

    /**
     * Returns the parsed 'values' attribute from the animation element.
     */
    protected AnimatableValue[] parseValues() {
        String valuesString =
            element.getAttributeNS(null, SVGConstants.SVG_VALUES_ATTRIBUTE);
        int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        ArrayList values = new ArrayList(7);
        int i = 0, start = 0, end;
        char c;
outer:  while (i < len) {
            while (valuesString.charAt(i) == ' ') {
                i++;
                if (i == len) {
                    break outer;
                }
            }
            start = i++;
            c = valuesString.charAt(i);
            while (c != ';') {
                i++;
                if (i == len) {
                    break;
                }
                c = valuesString.charAt(i);
            }
            end = i++;
            AnimatableValue val = eng.parseAnimatableValue
                (element, animationTarget, attributeNamespaceURI,
                 attributeLocalName, isCSS, valuesString.substring(start, end));
            values.add(val);
        }
        AnimatableValue[] ret = new AnimatableValue[values.size()];
        return (AnimatableValue[]) values.toArray(ret);
    }

    /**
     * Returns the parsed 'keyTimes' attribute from the animation element.
     */
    protected float[] parseKeyTimes() {
        String keyTimesString =
            element.getAttributeNS(null, SVGConstants.SVG_KEY_TIMES_ATTRIBUTE);
        int len = keyTimesString.length();
        if (len == 0) {
            return null;
        }
        ArrayList keyTimes = new ArrayList(7);
        int i = 0, start = 0, end;
        char c;
outer:  while (i < len) {
            while (keyTimesString.charAt(i) == ' ') {
                i++;
                if (i == len) {
                    break outer;
                }
            }
            start = i++;
            if (i != len) {
                c = keyTimesString.charAt(i);
                while (c != ' ' && c != ';') {
                    i++;
                    if (i == len) {
                        break;
                    }
                    c = keyTimesString.charAt(i);
                }
            }
            end = i++;
            try {
                float keyTime =
                    Float.parseFloat(keyTimesString.substring(start, end));
                keyTimes.add(new Float(keyTime));
            } catch (NumberFormatException nfe) {
                throw new BridgeException
                    (ctx, element, ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] { SVGConstants.SVG_KEY_TIMES_ATTRIBUTE,
                                    keyTimesString });
            }
        }
        len = keyTimes.size();
        float[] ret = new float[len];
        for (int j = 0; j < len; j++) {
            ret[j] = ((Float) keyTimes.get(j)).floatValue();
        }
        return ret;
    }

    /**
     * Returns the calcMode that the animation defaults to if none is specified.
     */
    protected int getDefaultCalcMode() {
        return SimpleAnimation.CALC_MODE_LINEAR;
    }
}
