/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import java.util.ArrayList;
import java.util.List;

import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.util.CSSConstants;

import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class is the default implementation of the CommonCSSContext.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultCommonCSSContext implements CommonCSSContext {

    /**
     * The default color.
     */
    public final static Color DEFAULT_COLOR = new Color(0, 0, 0);

    /**
     * The default font family.
     */
    public final static List DEFAULT_FONT_FAMILY;
    static {
        DEFAULT_FONT_FAMILY = new ArrayList(3);
        DEFAULT_FONT_FAMILY.add("Arial");
        DEFAULT_FONT_FAMILY.add("Helvetica");
        DEFAULT_FONT_FAMILY.add(CSSConstants.CSS_SANS_SERIF_VALUE);
    }

    /**
     * The user style sheet.
     */
    protected String userStyleSheetURI;

    /**
     * The default color.
     */
    public Color getDefaultColorValue() {
	return DEFAULT_COLOR;
    }

    /**
     * The font-family value.
     */
    public List getDefaultFontFamilyValue() {
	return DEFAULT_FONT_FAMILY;
    }

    /**
     * The user style sheet URI.
     */
    public String getUserStyleSheetURI() {
        return userStyleSheetURI;
    }

    /**
     * Sets the user style sheet URI.
     */
    public void setUserStyleSheetURI(String s) {
        userStyleSheetURI = s;
    }

    /**
     * Returns the font weight 'lighter' than the given weight.
     */
    public float getLighterFontWeight(float f) {
        switch ((int)f) {
        case 100: return 100;
        case 200: return 100;
        case 300: return 200;
        case 400: return 300;
        case 500: return 400;
        case 600: return 400;
        case 700: return 400;
        case 800: return 400;
        case 900: return 400;
        default:
            throw new IllegalArgumentException("" + f);
        }
    }

    /**
     * Returns the font weight 'bolder' than the given weight.
     */
    public float getBolderFontWeight(float f) {
        switch ((int)f) {
        case 100: return 600;
        case 200: return 600;
        case 300: return 600;
        case 400: return 600;
        case 500: return 600;
        case 600: return 700;
        case 700: return 800;
        case 800: return 900;
        case 900: return 900;
        default:
            throw new IllegalArgumentException("" + f);
        }
    }
}
