/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class provides a manager for the SVGPaint property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGPaintManager extends SVGColorManager {
    
    /**
     * Creates a new SVGPaintManager.
     */
    public SVGPaintManager(String prop) {
        super(prop);
    }

    /**
     * Creates a new SVGPaintManager.
     * @param prop The property name.
     * @param val The default value.
     */
    public SVGPaintManager(String prop, Value v) {
        super(prop, v);
    }

    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_IDENT:
            if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_NONE_VALUE)) {
                return SVGValueConstants.NONE_VALUE;
            }
        default:
            return super.createValue(lu, engine);
        case LexicalUnit.SAC_URI:
        }
        String uri = resolveURI(engine.getCSSBaseURI(), lu.getStringValue());
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return new StringValue(CSSPrimitiveValue.CSS_URI, uri);
        }

        ListValue result = new ListValue(' ');
        result.append(new StringValue(CSSPrimitiveValue.CSS_URI, uri));

	if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
            if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_NONE_VALUE)) {
                result.append(SVGValueConstants.NONE_VALUE);
                return result;
            }
        }
        Value v = super.createValue(lu, engine);
        if (v.getCssValueType() == CSSValue.CSS_CUSTOM) {
            ListValue lv = (ListValue)v;
            for (int i = 0; i < lv.getLength(); i++) {
                result.append(lv.item(i));
            }
        } else {
            result.append(v);
        }
        return result;
    }

    /**
     * Implements {@link
     * ValueManager#computeValue(CSSStylableElement,String,CSSEngine,int,StyleMap,Value)}.
     */
    public Value computeValue(CSSStylableElement elt,
                              String pseudo,
                              CSSEngine engine,
                              int idx,
                              StyleMap sm,
                              Value value) {
        if (value == SVGValueConstants.NONE_VALUE) {
            return value;
        }
        if (value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            ListValue lv = (ListValue)value;
            Value v = lv.item(0);
            if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                v = lv.item(1);
                if (v == SVGValueConstants.NONE_VALUE) {
                    return value;
                }
                Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
                if (t != v) {
                    ListValue result = new ListValue(' ');
                    result.append(lv.item(0));
                    result.append(t);
                    if (lv.getLength() == 3) {
                        result.append(lv.item(1));
                    }
                    return result;
                }
                return value;
            }
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
}
