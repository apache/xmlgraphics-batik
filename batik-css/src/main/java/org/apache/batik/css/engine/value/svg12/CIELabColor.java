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

/**
 * This class represents an CIE L*a*b* color value.
 *
 * @version $Id$
 */
public class CIELabColor extends AbstractCIEColor {

    public static final String CIE_LAB_COLOR_FUNCTION = "cielab";

    /**
     * Creates a new CIELabColor.
     * @param l the L* value
     * @param a the a* value
     * @param b the b* value
     * @param whitepoint the white point in CIE XYZ coordinates
     */
    public CIELabColor(float l, float a, float b, float[] whitepoint) {
        super(new float[] {l, a, b}, whitepoint);
    }

    /**
     * Creates a new CIELabColor with D50 as illuminant.
     */
    public CIELabColor(float l, float a, float b) {
        this(l, a, b, null);
    }

    /** {@inheritDoc} */
    public String getFunctionName() {
        return CIE_LAB_COLOR_FUNCTION;
    }

}
