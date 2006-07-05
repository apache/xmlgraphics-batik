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
 * An abstract class for values in the animation engine.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class AnimatableValue {

    /**
     * The target of the animation.
     */
    protected AnimationTarget target;

    /**
     * Whether this value has changed since the last call to
     * {@link #hasChanged()}.  This must be updated within {@link #interpolate}
     * in descendant classes.
     */
    protected boolean hasChanged = true;

    /**
     * Creates a new AnimatableValue.
     */
    protected AnimatableValue(AnimationTarget target) {
        this.target = target;
    }

    /**
     * Performs interpolation to the given value.
     * @param result the object in which to store the result of the
     *               interpolation, or null if a new object should be created
     * @param to the value this value should be interpolated towards, or null
     *           if no actual interpolation should be performed
     * @param interpolation the interpolation distance, 0 &lt;= interpolation
     *                      &lt;= 1
     * @param accumulation an accumulation to add to the interpolated value 
     * @param multiplier an amount the accumulation values should be multiplied
     *                   by before being added to the interpolated value
     */
    public abstract AnimatableValue interpolate(AnimatableValue result,
                                                AnimatableValue to,
                                                float interpolation,
                                                AnimatableValue accumulation,
                                                int multiplier);

    /**
     * Returns a zero value of this AnimatableValue's type.
     */
    public abstract AnimatableValue getZeroValue();

    /**
     * Returns the CSS text representation of the value.
     */
    public abstract String getCssText();
    
    /**
     * Returns whether the value in this AnimatableValue has been modified.
     */
    public boolean hasChanged() {
        boolean ret = hasChanged;
        hasChanged = false;
        return ret;
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        return getClass().getName() + "[" + getCssText() + "]";
    }
}
