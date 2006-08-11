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

import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.dom.anim.AnimationTarget;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.apache.batik.dom.svg.SVGOMTransform;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;

/**
 * An SVG transform list value in the animation system.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimatableTransformListValue extends AnimatableValue {

    /**
     * List of transforms.
     */
    protected Vector transforms;

    /**
     * Creates a new, uninitialized AnimatableTransformListValue.
     */
    protected AnimatableTransformListValue(AnimationTarget target) {
        super(target);
    }

    /**
     * Creates a new AnimatableTransformListValue with a single transform.
     */
    public AnimatableTransformListValue(AnimationTarget target,
                                        AbstractSVGTransform t) {
        super(target);
        this.transforms = new Vector();
        this.transforms.add(t);
    }

    /**
     * Creates a new AnimatableTransformListValue with a transform list.
     */
    public AnimatableTransformListValue(AnimationTarget target,
                                        Vector transforms) {
        super(target);
        int size = transforms.size();
        this.transforms = new Vector(size);
        for (int i = 0; i < size; i++) {
            this.transforms.setElementAt(transforms.elementAt(i), i);
        }
    }

    /**
     * Performs interpolation to the given value.
     */
    public AnimatableValue interpolate(AnimatableValue result,
                                       AnimatableValue to,
                                       float interpolation,
                                       AnimatableValue accumulation,
                                       int multiplier) {

        AnimatableTransformListValue toTransformList =
            (AnimatableTransformListValue) to;
        AnimatableTransformListValue accTransformList =
            (AnimatableTransformListValue) accumulation;

        int accSize = accumulation == null ? 0 : accTransformList.transforms.size();
        int newSize = 1 + accSize * multiplier;

        AnimatableTransformListValue res;
        if (result == null) {
            res = new AnimatableTransformListValue(target);
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else {
            res = (AnimatableTransformListValue) result;
            if (res.transforms == null) {
                res.transforms = new Vector(newSize);
                res.transforms.setSize(newSize);
            } else if (res.transforms.size() != newSize) {
                res.transforms.setSize(newSize);
            }
        }

        for (int i = 0; i < accSize; i++) {
            for (int j = i; j < i + multiplier; j++) {
                res.transforms.setElementAt
                    (accTransformList.transforms.elementAt(i), j);
            }
        }

        if (to != null) {
            AbstractSVGTransform ft = (AbstractSVGTransform) transforms.lastElement();
            int type = ft.getType();
            AbstractSVGTransform tt = (AbstractSVGTransform) toTransformList.transforms.lastElement();
            if (type == tt.getType()) {
                AbstractSVGTransform t =
                    (AbstractSVGTransform) res.transforms.elementAt(newSize - 1);
                if (t == null) {
                    t = new SVGOMTransform();
                    res.transforms.setElementAt(t, newSize - 1);
                }
                float x, y, r = 0;
                switch (type) {
                    case SVGTransform.SVG_TRANSFORM_SKEWX:
                    case SVGTransform.SVG_TRANSFORM_SKEWY:
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        if (type == SVGTransform.SVG_TRANSFORM_SKEWX) {
                            t.setSkewX(r);
                        } else if (type == SVGTransform.SVG_TRANSFORM_SKEWY) {
                            t.setSkewY(r);
                        }
                        break;
                    case SVGTransform.SVG_TRANSFORM_SCALE: {
                        SVGMatrix fm = ft.getMatrix();
                        SVGMatrix tm = tt.getMatrix();
                        x = fm.getA();
                        y = fm.getD();
                        x += interpolation * (tm.getA() - x);
                        y += interpolation * (tm.getD() - y);
                        if (type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
                            t.setTranslate(x, y);
                        } else if (type == SVGTransform.SVG_TRANSFORM_SCALE) {
                            t.setScale(x, y);
                        } else {
                            t.setRotate(r, x, y);
                        }
                        break;
                    }
                    case SVGTransform.SVG_TRANSFORM_ROTATE: {
                        x = ft.getX();
                        y = ft.getY();
                        x += interpolation * (tt.getX() - x);
                        y += interpolation * (tt.getY() - y);
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        t.setRotate(r, x, y);
                        break;
                    }
                    case SVGTransform.SVG_TRANSFORM_TRANSLATE: {
                        SVGMatrix fm = ft.getMatrix();
                        SVGMatrix tm = tt.getMatrix();
                        x = fm.getE();
                        y = fm.getF();
                        x += interpolation * (tm.getE() - x);
                        y += interpolation * (tm.getF() - y);
                        if (type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
                            t.setTranslate(x, y);
                        } else if (type == SVGTransform.SVG_TRANSFORM_SCALE) {
                            t.setScale(x, y);
                        } else {
                            t.setRotate(r, x, y);
                        }
                        break;
                    }
                }
            }
        } else {
            AbstractSVGTransform ft =
                (AbstractSVGTransform) transforms.lastElement();
            AbstractSVGTransform t =
                (AbstractSVGTransform) res.transforms.elementAt(newSize - 1);
            if (t == null) {
                t = new SVGOMTransform();
                res.transforms.setElementAt(t, newSize - 1);
            }
            t.assign(ft);
        }

        // XXX Do better checking for changes.
        res.hasChanged = true;

        return res;
    }

    /**
     * Gets the transforms.
     */
    public Iterator getTransforms() {
        return transforms.iterator();
    }

    /**
     * Returns whether two values of this type can have their distance
     * computed, as needed by paced animation.
     */
    public boolean canPace() {
        return true;
    }

    /**
     * Returns the absolute distance between this value and the specified other
     * value.
     */
    public float distanceTo(AnimatableValue other) {
        AnimatableTransformListValue o = (AnimatableTransformListValue) other;
        if (transforms.size() != 1 || o.transforms.size() != 1) {
            return 0f;
        }
        AbstractSVGTransform t1 = (AbstractSVGTransform) transforms.get(0);
        AbstractSVGTransform t2 = (AbstractSVGTransform) o.transforms.get(0);
        short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0f;
        }
        SVGMatrix m1 = t1.getMatrix();
        SVGMatrix m2 = t2.getMatrix();
        float dx, dy = 0;
        switch (type1) {
            case SVGTransform.SVG_TRANSFORM_TRANSLATE:
                dx = m1.getC() - m2.getC();
                dy = m1.getF() - m2.getF();
                break;
            case SVGTransform.SVG_TRANSFORM_ROTATE:
                dx = t1.getAngle() - t2.getAngle();
                break;
            case SVGTransform.SVG_TRANSFORM_SCALE:
                dx = m1.getA() - m2.getA();
                dy = m1.getE() - m2.getE();
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWX:
                dx = m1.getB() - m2.getB();
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWY:
                dx = m1.getD() - m2.getD();
                break;
            default:
                return 0f;
        }
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns a zero value of this AnimatableValue's type.  This returns an
     * empty transform list.
     */
    public AnimatableValue getZeroValue() {
        return new AnimatableTransformListValue(target, new Vector(5));
    }

    /**
     * Returns the CSS text representation of the value.
     * XXX To be done; not so important, just for debugging.
     */
    public String toStringRep() {
        return null;
    }
}
