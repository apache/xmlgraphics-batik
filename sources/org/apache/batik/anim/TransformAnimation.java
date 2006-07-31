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
package org.apache.batik.anim;

import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;

/**
 * An animation class for 'animateTransform' animations.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class TransformAnimation extends SimpleAnimation {

    /**
     * The transform type.  This should take one of the constants defined
     * in {@link org.w3c.dom.svg.SVGTransform}.
     */
    protected short type;

    /**
     * Creates a new TransformAnimation.
     */
    public TransformAnimation(TimedElement timedElement,
                              AnimatableElement animatableElement,
                              int calcMode,
                              float[] keyTimes,
                              float[] keySplines,
                              boolean additive,
                              boolean cumulative,
                              AnimatableValue[] values,
                              AnimatableValue from,
                              AnimatableValue to,
                              AnimatableValue by,
                              short type) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines,
              additive, cumulative, values, from, to, by);
        this.type = type;
    }
}
