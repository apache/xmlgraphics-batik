/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

/**
 * An {@link SVGPoint} in the list.
 */
public class SVGPointItem extends AbstractSVGItem implements SVGPoint {

    /**
     * The x value.
     */
    protected float x;

    /**
     * The y value.
     */
    protected float y;

    /**
     * Creates a new SVGPointItem.
     */
    public SVGPointItem(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return a String representation of this SVGPoint.
     */
    protected String getStringValue() {
        return Float.toString( x )
                + ','
                + Float.toString( y );
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#getX()}.
     */
    public float getX() {
        return x;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#getY()}.
     */
    public float getY() {
        return y;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#setX(float)}.
     */
    public void setX(float x) {
        this.x = x;
        resetAttribute();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#setY(float)}.
     */
    public void setY(float y) {
        this.y = y;
        resetAttribute();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#matrixTransform(SVGMatrix)}.
     */
    public SVGPoint matrixTransform(SVGMatrix matrix) {
        return SVGOMPoint.matrixTransform(this, matrix);
    }
}
