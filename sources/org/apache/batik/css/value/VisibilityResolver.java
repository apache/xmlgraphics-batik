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
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'visibility' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class VisibilityResolver implements RelativeValueResolver {
    /**
     * The inherit CSS value.
     */
    public final static CSSOMReadOnlyValue INHERIT =
	new CSSOMReadOnlyValue(AbstractValueFactory.INHERIT);

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
	return "visibility";
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
	return INHERIT;
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
	if (value.getCssValueType() == CSSValue.CSS_INHERIT) {
	    Element elt = getParentElement(element);
	    if (elt != null) {
		CSSOMReadOnlyStyleDeclaration sd;
		String prop = getPropertyName();
		sd = (CSSOMReadOnlyStyleDeclaration)view.getComputedStyle
                    (elt, null);
		styleDeclaration.setPropertyCSSValue
                    (prop,
                     sd.getPropertyCSSValue(prop),
                     sd.getPropertyPriority(prop),
                     sd.getPropertyOrigin(prop));
	    }
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
