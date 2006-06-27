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
 * An SVG point list value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatablePointListValue extends AnimatableNumberListValue {

    /**
     * Creates a new, uninitialized AnimatablePointListValue.
     */
    protected AnimatablePointListValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatablePointListValue.
     */
    public AnimatablePointListValue(AnimationTarget target, float[] numbers) {
        super(target, numbers);
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        if (result == null) {
            result = new AnimatablePointListValue(target);
        }
        return super.interpolate
            (result, to, interpolation, accumulation, multiplier);
    }
}
