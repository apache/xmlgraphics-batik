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
package org.apache.batik.anim.values;

import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.dom.svg.AbstractSVGPreserveAspectRatio;

import org.w3c.dom.svg.SVGPreserveAspectRatio;

/**
 * An SVG preserveAspectRatio value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatablePreserveAspectRatioValue extends AnimatableValue {

    /**
     * The align value.
     */
    protected short align;

    /**
     * The meet-or-slice value.
     */
    protected short meetOrSlice;

    /**
     * Creates a new, uninitialized AnimatablePreserveAspectRatioValue.
     */
    protected AnimatablePreserveAspectRatioValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatablePreserveAspectRatioValue.
     */
    public AnimatablePreserveAspectRatioValue(AnimationTarget target,
                                              short align, short meetOrSlice) {
        super(target);
        this.align = align;
        this.meetOrSlice = meetOrSlice;
    }
    
    /**
     * Performs interpolation to the given value.  Preserve aspect ratio values
     * cannot be interpolated.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to, float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatablePreserveAspectRatioValue res;
        if (result == null) {
            res = new AnimatablePreserveAspectRatioValue(target);
        } else {
            res = (AnimatablePreserveAspectRatioValue) result;
        }

        short newAlign, newMeetOrSlice;
        if (to != null && interpolation >= 0.5) {
            AnimatablePreserveAspectRatioValue toValue =
                (AnimatablePreserveAspectRatioValue) to;
            newAlign = toValue.align;
            newMeetOrSlice = toValue.meetOrSlice;
        } else {
            newAlign = align;
            newMeetOrSlice = meetOrSlice;
        }

        if (res.align != newAlign || res.meetOrSlice != newMeetOrSlice) {
            res.align = align;
            res.meetOrSlice = meetOrSlice;
            res.hasChanged = true;
        }
        return res;
    }

    /**
     * Returns the align value.
     */
    public short getAlign() {
        return align;
    }

    /**
     * Returns the meet-or-slice value.
     */
    public short getMeetOrSlice() {
        return meetOrSlice;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatablePreserveAspectRatioValue
            (target, SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE,
             SVGPreserveAspectRatio.SVG_MEETORSLICE_MEET);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return AbstractSVGPreserveAspectRatio.getValueAsString
            (align, meetOrSlice);
    }
}
