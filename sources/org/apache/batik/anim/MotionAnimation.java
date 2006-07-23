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

import java.awt.geom.Point2D;

import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatablePointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.ext.awt.geom.Cubic;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;
import org.apache.batik.ext.awt.geom.PathLength;

/**
 * An animation class for 'animateMotion' animations.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class MotionAnimation extends InterpolatingAnimation {

    /**
     * The path that describes the motion.
     */
    protected ExtendedGeneralPath path;

    /**
     * The path length calculation object.
     */
    protected PathLength pathLength;

    /**
     * The points defining the distance along the path that the
     * keyTimes apply.
     */
    protected float[] keyPoints;

    /**
     * Indexes to relevant segments of the path (i.e., all except
     * moveto commands).
     */
    protected int[] segmentIndexes;

    /**
     * Whether automatic rotation should be performed.
     */
    protected boolean rotateAuto;

    /**
     * Whether the automatic rotation should be reversed.
     */
    protected boolean rotateAutoReverse;

    /**
     * The angle value of rotation to use when automatic rotation is
     * not being used.
     */
    protected float rotateAngle;

    /**
     * The unit type of {@link #rotateAngle}.  Must be one of the
     * <code>SVG_ANGLETYPE_*</code> constants defined in {@link SVGAngle}.
     */
    protected short rotateAngleUnit;

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
                           float rotateAngle,
                           short rotateAngleUnit) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines,
              additive, cumulative);
        this.keyPoints = keyPoints;
        this.rotateAuto = rotateAuto;
        this.rotateAutoReverse = rotateAutoReverse;
        this.rotateAngle = rotateAngle;
        this.rotateAngleUnit = rotateAngleUnit;

        int segments;
        if (path == null) {
            path = new ExtendedGeneralPath();
            if (values == null || values.length == 0) {
                if (from != null) {
                    AnimatablePointValue fromPt = (AnimatablePointValue) from;
                    float x = fromPt.getX();
                    float y = fromPt.getY();
                    path.moveTo(x, y);
                    if (to != null) {
                        AnimatablePointValue toPt = (AnimatablePointValue) to;
                        path.lineTo(toPt.getX(), toPt.getY());
                    } else if (by != null) {
                        AnimatablePointValue byPt = (AnimatablePointValue) by;
                        path.lineTo(x + byPt.getX(), y + byPt.getY());
                    } else {
                        throw timedElement.createException
                            ("values.to.by.path.missing",
                             new Object[] { null });
                    }
                } else {
                    if (to != null) {
                        AnimatablePointValue unPt = (AnimatablePointValue)
                            animatableElement.getUnderlyingValue();
                        AnimatablePointValue toPt = (AnimatablePointValue) to;
                        path.moveTo(unPt.getX(), unPt.getY());
                        path.lineTo(toPt.getX(), toPt.getY());
                        this.cumulative = false;
                    } else if (by != null) {
                        AnimatablePointValue byPt = (AnimatablePointValue) by;
                        path.moveTo(0, 0);
                        path.lineTo(byPt.getX(), byPt.getY());
                        this.additive = true;
                    } else {
                        throw timedElement.createException
                            ("values.to.by.path.missing",
                             new Object[] { null });
                    }
                }
                segments = 1;
                segmentIndexes = new int[] { 1 };
            } else {
                segmentIndexes = new int[values.length - 1];
                AnimatablePointValue pt = (AnimatablePointValue) values[0];
                path.moveTo(pt.getX(), pt.getY());
                for (int i = 1; i < values.length; i++) {
                    pt = (AnimatablePointValue) values[i];
                    path.lineTo(pt.getX(), pt.getY());
                    segmentIndexes[i - 1] = i;
                }
                segments = values.length - 1;
            }
            pathLength = new PathLength(path);
        } else {
            pathLength = new PathLength(path);
            int totalSegments = pathLength.getNumberOfSegments();
            int[] indexes = new int[totalSegments];
            segments = 0;
            ExtendedPathIterator epi = path.getExtendedPathIterator();
            int idx = 0;
            while (!epi.isDone()) {
                int type = epi.currentSegment();
                if (type != ExtendedPathIterator.SEG_MOVETO) {
                    indexes[segments++] = idx;
                }
                idx++;
                epi.next();
            }
            if (segments == indexes.length) {
                segmentIndexes = indexes;
            } else {
                segmentIndexes = new int[segments];
                System.arraycopy(indexes, 0, segmentIndexes, 0, segments);
            }
        }
        this.path = path;

        if (this.keyTimes != null) {
            if (this.keyTimes.length != segments + 1) {
                throw timedElement.createException
                    ("attribute.malformed",
                     new Object[] { null,
                                    SMILConstants.SMIL_KEY_TIMES_ATTRIBUTE });
            }
        } else {
            int count = segments + 1;
            if (calcMode == CALC_MODE_LINEAR || calcMode == CALC_MODE_SPLINE) {
                this.keyTimes = new float[count];
                for (int i = 0; i < count; i++) {
                    this.keyTimes[i] = (float) i / (count - 1);
                }
            } else if (calcMode == CALC_MODE_DISCRETE) {
                this.keyTimes = new float[count];
                for (int i = 0; i < count; i++) {
                    this.keyTimes[i] = (float) i / count;
                }
            } else { // CALC_MODE_PACED
                // This corrects the keyTimes to be paced, so from now on
                // it can be considered the same as CALC_MODE_LINEAR.
                float totalLength = pathLength.lengthOfPath();
                this.keyTimes = new float[count];
                this.keyTimes[0] = 0;
                for (int i = 1; i < count - 1; i++) {
                    this.keyTimes[i] =
                        pathLength.getLengthAtSegment(i + 1) / totalLength;
                }
                this.keyTimes[count - 1] = 1;
            }
        }

        if (this.keyPoints != null &&
                keyPoints.length != this.keyTimes.length) {
            throw timedElement.createException
                ("attribute.malformed",
                 new Object[] { null,
                                SMILConstants.SMIL_KEY_POINTS_ATTRIBUTE });
        }
    }

    /**
     * Called when the element is sampled at the given unit time.  This updates
     * the {@link #value} of the animation if active.
     */
    protected void sampledAtUnitTime(float unitTime, int repeatIteration) {
        AnimatableValue value, accumulation;
        float interpolation = 0;
        if (unitTime != 1) {
            int keyTimeIndex = 0;
            while (keyTimeIndex < keyTimes.length - 1
                    && unitTime >= keyTimes[keyTimeIndex + 1]) {
                keyTimeIndex++;
            }
            if (keyTimeIndex == keyTimes.length - 1 && calcMode == CALC_MODE_DISCRETE) {
                keyTimeIndex = keyTimes.length - 2;
                interpolation = 1;
            } else {
                if (calcMode == CALC_MODE_LINEAR || calcMode == CALC_MODE_PACED
                        || calcMode == CALC_MODE_SPLINE) {
                    interpolation = (unitTime - keyTimes[keyTimeIndex])
                        / (keyTimes[keyTimeIndex + 1] - keyTimes[keyTimeIndex]);
                    if (calcMode == CALC_MODE_SPLINE && unitTime != 0) {
                        // XXX This could be done better, e.g. with
                        //     Newton-Raphson.
                        Cubic c = keySplineCubics[keyTimeIndex];
                        float tolerance = 0.001f;
                        float min = 0;
                        float max = 1;
                        Point2D.Double p;
                        for (;;) {
                            float t = (min + max) / 2;
                            p = c.eval(t);
                            double x = p.getX();
                            if (Math.abs(x - interpolation) < tolerance) {
                                break;
                            }
                            if (x < interpolation) {
                                min = t;
                            } else {
                                max = t;
                            }
                        }
                        interpolation = (float) p.getY();
                    }
                }
            }
            int seg = segmentIndexes[keyTimeIndex];
            Point2D p = pathLength.pointAtLength(seg, interpolation);
            value = new AnimatablePointValue(null, (float) p.getX(), (float) p.getY());
        } else {
            Point2D p = pathLength.pointAtLength(pathLength.lengthOfPath());
            value = new AnimatablePointValue(null, (float) p.getX(), (float) p.getY());
        }
        if (cumulative) {
            Point2D p = pathLength.pointAtLength(pathLength.lengthOfPath());
            accumulation = new AnimatablePointValue(null, (float) p.getX(), (float) p.getY());
        } else {
            accumulation = null;
        }

        this.value = value.interpolate(this.value, null, interpolation,
                                       accumulation, repeatIteration);
        if (this.value.hasChanged()) {
            markDirty();
        }
    }
}
