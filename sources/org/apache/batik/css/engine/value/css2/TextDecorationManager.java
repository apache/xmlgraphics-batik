/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the 'text-decoration' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextDecorationManager extends AbstractValueManager {
    
    /**
     * The identifier values.
     */
    protected final static StringMap values = new StringMap();
    static {
	values.put(CSSConstants.CSS_BLINK_VALUE,
                   ValueConstants.BLINK_VALUE);
	values.put(CSSConstants.CSS_LINE_THROUGH_VALUE,
                   ValueConstants.LINE_THROUGH_VALUE);
	values.put(CSSConstants.CSS_OVERLINE_VALUE,
                   ValueConstants.OVERLINE_VALUE);
	values.put(CSSConstants.CSS_UNDERLINE_VALUE,
                   ValueConstants.UNDERLINE_VALUE);
    }

    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return false;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_TEXT_DECORATION_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return ValueConstants.INHERIT_VALUE;

	case LexicalUnit.SAC_IDENT:
            if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_NONE_VALUE)) {
                return ValueConstants.NONE_VALUE;
	    }
            ListValue lv = new ListValue(' ');
            do {
                if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
                    String s = lu.getStringValue().toLowerCase().intern();
                    Object obj = values.get(s);
                    if (obj == null) {
                        throw createInvalidIdentifierDOMException
                            (lu.getStringValue());
                    }
                    lv.append((Value)obj);
                    lu = lu.getNextLexicalUnit();
                } else {
                    throw createInvalidLexicalUnitDOMException
                        (lu.getLexicalUnitType());
                }
                
            } while (lu != null);
            return lv;
        }
        throw createInvalidLexicalUnitDOMException
            (lu.getLexicalUnitType());
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
            throw createInvalidStringTypeDOMException(type);
	}
        if (!value.equalsIgnoreCase(CSSConstants.CSS_NONE_VALUE)) {
            throw createInvalidIdentifierDOMException(value);
        }
	return ValueConstants.NONE_VALUE;
    }

}
