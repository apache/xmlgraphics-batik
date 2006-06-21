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
 * An SVG length-or-identifier value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableLengthOrIdentValue extends AnimatableLengthValue {

    /**
     * Whether this value is an identifier.
     */
    protected boolean isIdent;
    
    /**
     * The identifier.
     */
    protected String ident;
    
    /**
     * Creates a new, uninitialized AnimatableLengthOrIdentValue.
     */
    protected AnimatableLengthOrIdentValue(AnimationTarget target) {
        super(target);
    }
    
    /**
     * Creates a new AnimatableLengthOrIdentValue for a length value.
     */
    public AnimatableLengthOrIdentValue(AnimationTarget target, int type,
                                        float v, int pcInterp) {
        super(target, type, v, pcInterp);
    }

    /**
     * Creates a new AnimatableLengthOrIdentValue for an identifier value.
     */
    public AnimatableLengthOrIdentValue(AnimationTarget target, String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
    }

    /**
     * Returns whether this value is an identifier or a length.
     */
    public boolean isIdent() {
        return isIdent;
    }

    /**
     * Returns the identifier.
     */
    public String getIdent() {
        return ident;
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
        AnimatableLengthOrIdentValue res;
        if (result == null) {
            res = new AnimatableLengthOrIdentValue(target);
        } else {
            res = (AnimatableLengthOrIdentValue) result;
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
