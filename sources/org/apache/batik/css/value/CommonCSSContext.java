/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import java.util.List;

import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.value.CommonCSSContext.Color;

/**
 * This interface represents the context the application must provides
 * to the CSS engine in order to resolve the relative CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CommonCSSContext {
    
    /**
     * The default color.
     */
    Color getDefaultColorValue();

    /**
     * The default font-family value.
     * @return a list of string.
     */
    List getDefaultFontFamilyValue();

    /**
     * The user style sheet URI.
     */
    String getUserStyleSheetURI();

    /**
     * Returns the font weight 'lighter' than the given weight.
     */
    float getLighterFontWeight(float f);

    /**
     * Returns the font weight 'bolder' than the given weight.
     */
    float getBolderFontWeight(float f);

    /**
     * To Store a CSS RGB color.
     */
    public class Color {
        /**
         * The red component.
         */
        protected int red;

        /**
         * The green component.
         */
        protected int green;

        /**
         * The blue component.
         */
        protected int blue;

        /**
         * Creates a new color.
         */
        public Color(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        /**
         * Returns the red component.
         */
        public int getRed() {
            return red;
        }

        /**
         * Returns the green component.
         */
        public int getGreen() {
            return green;
        }

        /**
         * Returns the blue component.
         */
        public int getBlue() {
            return blue;
        }
    }
}
