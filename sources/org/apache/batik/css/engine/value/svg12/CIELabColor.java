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
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.AbstractValue;

import org.apache.xmlgraphics.java2d.color.ColorSpaces;

import org.w3c.dom.css.CSSValue;

/**
 * This class represents an CIE L*a*b* color value.
 *
 * @version $Id$
 */
public class CIELabColor extends AbstractValue {

    public static final String CIE_LAB_COLOR_FUNCTION = "cielab";

    /** The three color values (L*a*b*). */
    protected float[] values = new float[3];

    /** The white point, initialized to D65. */
    protected float[] whitepoint = ColorSpaces.getCIELabColorSpaceD65().getWhitePoint();

    /**
     * Creates a new ICCColor.
     * @param l the L* value
     * @param a the a* value
     * @param b the b* value
     * @param whitepoint the white point in CIE XYZ coordinates
     */
    public CIELabColor(float l, float a, float b, float[] whitepoint) {
        values[0] = l;
        values[1] = a;
        values[2] = b;
        if (whitepoint != null) {
            System.arraycopy(whitepoint, 0, this.whitepoint, 0, this.whitepoint.length);
        }
    }

    /**
     * Creates a new ICCColor.
     */
    public CIELabColor(float l, float a, float b) {
        this(l, a, b, null);
    }

    /**
     * Returns the color values.
     * @return the color values
     */
    public float[] getColorValues() {
        float[] copy = new float[3];
        System.arraycopy(this.values, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Returns the white point in CIE XYZ coordinates.
     * @return the white point in CIE XYZ coordinates
     */
    public float[] getWhitePoint() {
        float[] copy = new float[3];
        System.arraycopy(this.whitepoint, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.Value#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_CUSTOM;
    }

    /**
     *  A string representation of the current value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer(CIE_LAB_COLOR_FUNCTION);
        sb.append('(');
        sb.append(values[0]);
        sb.append(", ");
        sb.append(values[1]);
        sb.append(", ");
        sb.append(values[2]);
        sb.append( ')' );
        return sb.toString();
    }

}
