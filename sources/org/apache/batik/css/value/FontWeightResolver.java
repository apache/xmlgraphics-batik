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
import org.w3c.dom.Node;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'font-weight' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontWeightResolver implements RelativeValueResolver {
    /**
     * The medium CSS value.
     */
    public final static CSSOMReadOnlyValue NORMAL =
	new CSSOMReadOnlyValue(FontWeightFactory.NORMAL_VALUE);

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
	return "font-weight";
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	return NORMAL;
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
	boolean b = im == FontWeightFactory.BOLDER_VALUE;
	if (b || im == FontWeightFactory.LIGHTER_VALUE) {
	    Element p = getParentElement(element);
	    CSSOMReadOnlyValue val;
	    if (p == null) {
		val = new CSSOMReadOnlyValue((b)
					     ? FontWeightFactory.VALUE_500
					     : FontWeightFactory.VALUE_300);
	    } else {
		CSSOMReadOnlyStyleDeclaration sd;
		sd = (CSSOMReadOnlyStyleDeclaration)view.getComputedStyle
                    (p, null);
		CSSOMReadOnlyValue prop;
		prop = (CSSOMReadOnlyValue)sd.getPropertyCSSValue
                    (getPropertyName());
		im = prop.getImmutableValue();
		if (im == FontWeightFactory.VALUE_100) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_200
                         : FontWeightFactory.VALUE_100);
		} else if (im == FontWeightFactory.VALUE_200) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_300
                         : FontWeightFactory.VALUE_100);
		} else if (im == FontWeightFactory.VALUE_300) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_400
                         : FontWeightFactory.VALUE_200);
		} else if (im == FontWeightFactory.VALUE_400 ||
			   im == FontWeightFactory.NORMAL_VALUE) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_500
                         : FontWeightFactory.VALUE_300);
		} else if (im == FontWeightFactory.VALUE_500) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_600
                         : FontWeightFactory.VALUE_400);
		} else if (im == FontWeightFactory.VALUE_600) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_700
                         : FontWeightFactory.VALUE_500);
		} else if (im == FontWeightFactory.VALUE_700 ||
			   im == FontWeightFactory.BOLD_VALUE) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_800
                         : FontWeightFactory.VALUE_600);
		} else if (im == FontWeightFactory.VALUE_800) {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_900
                         : FontWeightFactory.VALUE_700);
		} else {
		    val = new CSSOMReadOnlyValue
                        ((b)
                         ? FontWeightFactory.VALUE_900
                         : FontWeightFactory.VALUE_800);
		}
	    }
	    styleDeclaration.setPropertyCSSValue(getPropertyName(),
						 val,
						 priority,
						 origin);
	}
    }

    /**
     * Returns the parent element of the given one, or null.
     */
    protected Element getParentElement(Element e) {
	for (Node n = e.getParentNode(); n != null; n = n.getParentNode()) {
	    if (n.getNodeType() == Node.ELEMENT_NODE) {
		return (Element)n;
	    }
	}
	return null;
    }
}
