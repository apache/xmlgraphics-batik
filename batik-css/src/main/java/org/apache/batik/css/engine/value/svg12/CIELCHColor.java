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
 * This class represents an CIE LCH color value.
 *
 * @version $Id$
 */
public class CIELCHColor extends AbstractCIEColor {

    public static final String CIE_LCH_COLOR_FUNCTION = "cielch";

    /**
     * Creates a new CIELCHColor.
     * @param l the lightness (L) value
     * @param c the chroma (C) value
     * @param h the hue (H) value
     * @param whitepoint the white point in CIE XYZ coordinates
     */
    public CIELCHColor(float l, float c, float h, float[] whitepoint) {
        super(new float[] {l, c, h}, whitepoint);
    }

    /**
     * Creates a new CIELCHColor with D65 as illuminant.
     * @param l the lightness (L) value
     * @param c the chroma (C) value
     * @param h the hue (H) value
     */
    public CIELCHColor(float l, float c, float h) {
        this(l, c, h, null);
    }

    /** {@inheritDoc} */
    public String getFunctionName() {
        return CIE_LCH_COLOR_FUNCTION;
    }

}
