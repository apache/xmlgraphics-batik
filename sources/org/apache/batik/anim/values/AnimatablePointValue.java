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

/**
 * A point value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatablePointValue extends AnimatableValue {

    /**
     * The x coordinate.
     */
    protected float x;

    /**
     * The y coordinate.
     */
    protected float y;

    /**
     * Creates a new, uninitialized AnimatablePointValue.
     */
    protected AnimatablePointValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatablePointValue with one x.
     */
    public AnimatablePointValue(AnimationTarget target, float x, float y) {
        super(target);
        this.x = x;
        this.y = y;
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatablePointValue res;
        if (result == null) {
            res = new AnimatablePointValue(target);
        } else {
            res = (AnimatablePointValue) result;
        }

        float newX = x, newY = y;

        if (to != null) {
            AnimatablePointValue toValue = (AnimatablePointValue) to;
            newX += interpolation * (toValue.x - x);
            newY += interpolation * (toValue.y - y);
        }
        if (accumulation != null) {
            AnimatablePointValue accValue = (AnimatablePointValue) accumulation;
            newX += multiplier * accValue.x;
            newY += multiplier * accValue.y;
        }

        if (res.x != newX || res.y != newY) {
            res.x = newX;
            res.y = newY;
            res.hasChanged = true;
        }
        return res;
    }

    /**
     * Returns the x coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatablePointValue(target, 0f, 0f);
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        String s = Float.toString(x);
        if (s.endsWith(".0")) {
            sb.append(s.substring(0, s.length() - 2));
        } else {
            sb.append(s);
        }
        sb.append(',');
        s = Float.toString(y);
        if (s.endsWith(".0")) {
            sb.append(s.substring(0, s.length() - 2));
        } else {
            sb.append(s);
        }
        return sb.toString();
    }
}
