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

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'color' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorResolver implements RelativeValueResolver {

    /**
     * The application context.
     */
    protected CommonCSSContext context;
    
    /**
     * Creates a new ColorRelativeValueResolver object.
     * @param ctx The application context.
     */
    public ColorResolver(CommonCSSContext ctx) {
	context = ctx;
    }

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
	return ValueConstants.CSS_COLOR_PROPERTY;
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	CommonCSSContext.Color c = context.getDefaultColorValue();
        return new CSSOMReadOnlyValue
            (new ImmutableRGBColor
                (new CSSOMReadOnlyValue(new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
                                                           c.getRed())),
                 new CSSOMReadOnlyValue(new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
                                                           c.getGreen())),
                 new CSSOMReadOnlyValue(new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
                                                           c.getBlue()))));
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
        // Nothing to do
    }
}
