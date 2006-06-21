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
 * A string value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableStringValue extends AnimatableValue {

    /**
     * The string value.
     */
    protected String string;
    
    /**
     * Creates a new, uninitialized AnimatableStringValue.
     */
    protected AnimatableStringValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableStringValue.
     */
    public AnimatableStringValue(AnimationTarget target, String s) {
        super(target);
        string = s;
    }
    
    /**
     * Performs interpolation to the given value.  String values cannot be
     * interpolated.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to, float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableStringValue res;
        if (result == null) {
            res = new AnimatableStringValue(target);
        } else {
            res = (AnimatableStringValue) result;
        }
        res.string = string;
        return res;
    }

    /**
     * Returns the string.
     */
    public String getString() {
        return string;
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
        return string;
    }
}
