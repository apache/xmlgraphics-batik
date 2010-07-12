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
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents an ICC named color value.
 *
 * @version $Id$
 */
public class ICCNamedColor extends AbstractValue {

    public static final String ICC_NAMED_COLOR_FUNCTION = "icc-named-color";

    /**
     * The color profile.
     */
    protected String colorProfile;

    /**
     * The color name.
     */
    protected String colorName;

    /**
     * Creates a new ICCColor.
     */
    public ICCNamedColor(String profileName, String colorName) {
        this.colorProfile = profileName;
        this.colorName = colorName;
    }

    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.Value#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_CUSTOM;
    }

    /**
     * Returns the color name.
     */
    public String getColorProfile() throws DOMException {
        return colorProfile;
    }

    /**
     * Returns the color name
     */
    public String getColorName() throws DOMException {
        return colorName;
    }

    /**
     *  A string representation of the current value.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer(ICC_NAMED_COLOR_FUNCTION);
        sb.append('(');
        sb.append(colorProfile);
        sb.append(", ");
        sb.append(colorName);
        sb.append( ')' );
        return sb.toString();
    }

}
