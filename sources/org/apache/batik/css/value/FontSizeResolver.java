/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.util.CSSConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'font-size' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontSizeResolver
    implements RelativeValueResolver,
               CSSConstants {
    /**
     * The medium CSS value.
     */
    public final static CSSOMReadOnlyValue MEDIUM =
	new CSSOMReadOnlyValue(FontSizeFactory.MEDIUM_VALUE);

    /**
     * Whether the handled property is inherited or not.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Returns the name of the handled property.
     */
    public String getPropertyName() {
	return FONT_SIZE_PROPERTY;
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	return MEDIUM;
    }
    
    /**
     * Resolves the given value if relative, and puts it in the given table.
     * @param element The element to which this value applies.
     * @param pseudoElement The pseudo element if one.
     * @param view The view CSS of the current document.
     * @param styleDeclaration The computed style declaration.
     * @param value The cascaded value.
     * @param priority The priority of the cascaded value.
     * @param origin The origin of the cascaded value.
     */
    public void resolveValue(Element element,
			     String pseudoElement,
			     ViewCSS view,
			     CSSOMReadOnlyStyleDeclaration styleDeclaration,
			     CSSOMReadOnlyValue value,
			     String priority,
			     int origin) {
	ImmutableValue im = value.getImmutableValue();
	boolean b = im == FontSizeFactory.SMALLER_VALUE;
        CSSOMReadOnlyValue val = null;
        if (b || im == FontSizeFactory.LARGER_VALUE) {
            Element p = HiddenChildElementSupport.getParentElement(element);
            if (p == null) {
                val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.SMALL_VALUE
                                             : FontSizeFactory.LARGE_VALUE);
            } else {
		CSSOMReadOnlyStyleDeclaration sd;
		sd = (CSSOMReadOnlyStyleDeclaration)view.getComputedStyle
                    (p, null);
		CSSOMReadOnlyValue prop;
		prop = (CSSOMReadOnlyValue)sd.getPropertyCSSValue
                    (getPropertyName());
		im = prop.getImmutableValue();
                if (im == FontSizeFactory.LARGE_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.MEDIUM_VALUE
                                             : FontSizeFactory.X_LARGE_VALUE);
                } else if (im == FontSizeFactory.MEDIUM_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.SMALL_VALUE
                                             : FontSizeFactory.LARGE_VALUE);
                } else if (im == FontSizeFactory.SMALL_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.MEDIUM_VALUE
                                             : FontSizeFactory.X_LARGE_VALUE);
                } else if (im == FontSizeFactory.X_LARGE_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.LARGE_VALUE
                                             : FontSizeFactory.XX_LARGE_VALUE);
                } else if (im == FontSizeFactory.X_SMALL_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.XX_SMALL_VALUE
                                             : FontSizeFactory.SMALL_VALUE);
                } else if (im == FontSizeFactory.XX_LARGE_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.X_LARGE_VALUE
                                             : FontSizeFactory.XX_LARGE_VALUE);
                } else if (im == FontSizeFactory.XX_SMALL_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                             ? FontSizeFactory.XX_SMALL_VALUE
                                             : FontSizeFactory.X_SMALL_VALUE);
                } else if (im instanceof ImmutableFloat) {
                    short t = ((ImmutableFloat)im).getPrimitiveType();
                    float f = ((ImmutableFloat)im).getFloatValue(t);
                    if (t == CSSPrimitiveValue.CSS_PERCENTAGE) {
                        throw new RuntimeException("!!! %");
                    } else {
                        val = new CSSOMReadOnlyValue
                            (new ImmutableFloat(t, (b) ? f / 1.2f : f * 1.2f));
                    }
                }
                if (val != null) {
                    styleDeclaration.setPropertyCSSValue(getPropertyName(),
                                                         val,
                                                         priority,
                                                         origin);
                }
            }
        }
    }
}
