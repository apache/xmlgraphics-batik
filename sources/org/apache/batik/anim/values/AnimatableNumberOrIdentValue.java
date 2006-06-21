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
 * A number-or-identifier value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableNumberOrIdentValue extends AnimatableNumberValue {

    /**
     * Whether this value is an identifier.
     */
    protected boolean isIdent;
    
    /**
     * The identifier.
     */
    protected String ident;
    
    /**
     * Creates a new, uninitialized AnimatableNumberOrIdentValue.
     */
    protected AnimatableNumberOrIdentValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableNumberOrIdentValue for a Number value.
     */
    public AnimatableNumberOrIdentValue(AnimationTarget target, float v) {
        super(target, v);
    }

    /**
     * Creates a new AnimatableNumberOrIdentValue for an identifier value.
     */
    public AnimatableNumberOrIdentValue(AnimationTarget target, String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
    }

    /**
     * Returns the CSS text representation of the value.
     */
    public String getCssText() {
        if (isIdent) {
            return ident;
        }
        return super.getCssText();
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to, float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {
        AnimatableNumberOrIdentValue res;
        if (result == null) {
            res = new AnimatableNumberOrIdentValue(target);
        } else {
            res = (AnimatableNumberOrIdentValue) result;
        }
        
        if (isIdent) {
            res.ident = ident;
            res.isIdent = true;
        } else {
            super.interpolate(res, to, interpolation, accumulation, multiplier);
            res.isIdent = false;
        }
        return res;
    }
}
