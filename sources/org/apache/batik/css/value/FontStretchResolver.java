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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'font-stretch' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontStretchResolver implements RelativeValueResolver {

    /**
     * The medium CSS value.
     */
    public final static CSSOMReadOnlyValue NORMAL =
	new CSSOMReadOnlyValue(FontStretchFactory.NORMAL_VALUE);

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
	return ValueConstants.CSS_FONT_STRETCH_PROPERTY;
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	return new CSSOMReadOnlyValue(ValueConstants.NORMAL_VALUE);
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
	boolean b = im == ValueConstants.NARROWER_VALUE;
	if (b || im == ValueConstants.WIDER_VALUE) {
	    Element p = HiddenChildElementSupport.getParentElement(element);
	    CSSOMReadOnlyValue val;
	    if (p == null) {
		val = new CSSOMReadOnlyValue((b)
				      ? ValueConstants.SEMI_CONDENSED_VALUE
				      : ValueConstants.SEMI_EXPANDED_VALUE);
	    } else {
		CSSOMReadOnlyStyleDeclaration sd;
		sd = (CSSOMReadOnlyStyleDeclaration)view.getComputedStyle
                    (p, null);
		CSSOMReadOnlyValue prop;
		prop = (CSSOMReadOnlyValue)sd.getPropertyCSSValue
                    (getPropertyName());
		im = prop.getImmutableValue();
                if (im == ValueConstants.NORMAL_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.SEMI_CONDENSED_VALUE
                                   : ValueConstants.SEMI_EXPANDED_VALUE);
                } else if (im == ValueConstants.CONDENSED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.EXTRA_CONDENSED_VALUE
                                   : ValueConstants.SEMI_CONDENSED_VALUE);
                } else if (im == ValueConstants.SEMI_EXPANDED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.NORMAL_VALUE
                                   : ValueConstants.EXPANDED_VALUE);
                } else if (im == ValueConstants.SEMI_CONDENSED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.CONDENSED_VALUE
                                   : ValueConstants.NORMAL_VALUE);
                } else if (im == ValueConstants.EXTRA_CONDENSED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.ULTRA_CONDENSED_VALUE
                                   : ValueConstants.CONDENSED_VALUE);
                } else if (im == ValueConstants.EXTRA_EXPANDED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.EXPANDED_VALUE
                                   : ValueConstants.ULTRA_EXPANDED_VALUE);
                } else if (im == ValueConstants.ULTRA_CONDENSED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.ULTRA_CONDENSED_VALUE
                                   : ValueConstants.EXTRA_CONDENSED_VALUE);
                } else {//if (im == ValueConstants.ULTRA_EXPANDED_VALUE) {
                    val = new CSSOMReadOnlyValue((b)
                                   ? ValueConstants.EXTRA_EXPANDED_VALUE
                                   : ValueConstants.ULTRA_EXPANDED_VALUE);
                }
            }
	    styleDeclaration.setPropertyCSSValue(getPropertyName(),
						 val,
						 priority,
						 origin);
        }
    }
}
