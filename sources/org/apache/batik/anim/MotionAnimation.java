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
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;

/**
 * An animation class for 'animateMotion' animations.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class MotionAnimation extends SimpleAnimation {

    /**
     * The path that describes the motion.
     */
    protected ExtendedGeneralPath path;

    /**
     * The points defining the distance along the path that the
     * keyTimes apply.
     */
    protected float[] keyPoints;

    /**
     * Whether automatic rotation should be performed.
     */
    protected boolean rotateAuto;

    /**
     * Whether the automatic rotation should be reversed.
     */
    protected boolean rotateAutoReverse;

    /**
     * The angle of rotation to use when automatic rotation is
     * not being used.
     */
    protected float rotateAngle;

    /**
     * Creates a new MotionAnimation.
     */
    public MotionAnimation(TimedElement timedElement,
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
                           ExtendedGeneralPath path,
                           float[] keyPoints,
                           boolean rotateAuto,
                           boolean rotateAutoReverse,
                           float rotateAngle) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines,
              additive, cumulative, values, from, to, by);
    }
}
