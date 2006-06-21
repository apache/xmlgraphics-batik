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
 * An SVG rect value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableRectValue extends AnimatableValue {

    /**
     * The x coordinate.
     */
    protected float x;

    /**
     * The y coordinate.
     */
    protected float y;

    /**
     * The width.
     */
    protected float width;

    /**
     * The height.
     */
    protected float height;

    /**
     * Creates a new, uninitialized AnimatableRectValue.
     */
    protected AnimatableRectValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableRectValue with one number.
     */
    public AnimatableRectValue(AnimationTarget target, float x, float y,
                               float w, float h) {
        super(target);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    /**
     * Performs interpolation to the given value.  Rect values cannot be
     * interpolated.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableRectValue res;
        if (result == null) {
            res = new AnimatableRectValue(target);
        } else {
            res = (AnimatableRectValue) result;
        }
        res.x = x;
        res.y = y;
        res.width = width;
        res.height = height;
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
     * Returns the width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public AnimatableValue getZeroValue() {
        return null;
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        return null;
    }
}
