/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMReadOnlyValue;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class is the default implementation of the CommonCSSContext.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultCommonCSSContext implements CommonCSSContext {
    /**
     * 0.
     */
    protected final static ImmutableValue N_0 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 0);

    /**
     * The default color.
     */
    public final static CSSOMReadOnlyValue DEFAULT_COLOR_VALUE =
	new CSSOMReadOnlyValue
        (new ImmutableRGBColor(new CSSOMReadOnlyValue(N_0),
                               new CSSOMReadOnlyValue(N_0),
                               new CSSOMReadOnlyValue(N_0)));

    /**
     * The default font family.
     */
    public final static CSSOMReadOnlyValue DEFAULT_FONT_FAMILY;
    static {
	ImmutableValueList l = new ImmutableValueList();
	ImmutableValue v;
	v = new ImmutableString(CSSPrimitiveValue.CSS_STRING, "Arial");
	l.append(new CSSOMReadOnlyValue(v));
	v = new ImmutableString(CSSPrimitiveValue.CSS_STRING, "Helvetica");
	l.append(new CSSOMReadOnlyValue(v));
	l.append(new CSSOMReadOnlyValue(FontFamilyFactory.SANS_SERIF_VALUE));
	DEFAULT_FONT_FAMILY = new CSSOMReadOnlyValue(l);
    }

    /**
     * The default color.
     */
    public CSSOMReadOnlyValue getDefaultColorValue() {
	return DEFAULT_COLOR_VALUE;
    }

    /**
     * The font-family value.
     */
    public CSSOMReadOnlyValue getDefaultFontFamilyValue() {
	return DEFAULT_FONT_FAMILY;
    }
}
