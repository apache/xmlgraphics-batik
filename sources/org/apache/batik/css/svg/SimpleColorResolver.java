/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableRGBColor;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.RelativeValueResolver;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the simple color CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SimpleColorResolver implements RelativeValueResolver {
    /**
     * 0.
     */
    protected final static ImmutableValue N_0 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 0);

    /**
     * The black CSS value.
     */
    public final static CSSOMReadOnlyValue BLACK;
    static {
        CSSPrimitiveValue v = new CSSOMReadOnlyValue(N_0);
        BLACK = new CSSOMReadOnlyValue(new ImmutableRGBColor(v, v, v));
    }

    /**
     * The handled property name.
     */
    protected String propertyName;

    /**
     * Creates a new SimpleColorResolver object.
     * @param name The name of the handled property.
     */
    public SimpleColorResolver(String name) {
        propertyName = name;
    }

    /**
     * Whether the handled property is inherited or not.
     */
    public boolean isInheritedProperty() {
	return false;
    }

    /**
     * Returns the name of the handled property.
     */
    public String getPropertyName() {
	return propertyName;
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	return BLACK;
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
        if (im == PaintFactory.CURRENTCOLOR_VALUE) {
	    styleDeclaration.setPropertyCSSValue
                (getPropertyName(),
                 styleDeclaration.getPropertyCSSValue("color"),
                 priority,
                 origin);
        }
    }
}
