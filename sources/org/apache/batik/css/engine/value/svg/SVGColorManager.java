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
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.ValueManager;

import org.apache.batik.util.CSSConstants;

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the SVGColor property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGColorManager extends ColorManager {

    /**
     * The name of the handled property.
     */
    protected String property;

    /**
     * The default value.
     */
    protected Value defaultValue;

    /**
     * Creates a new SVGColorManager.
     * The default value is black.
     */
    public SVGColorManager(String prop) {
        this(prop, SVGValueConstants.BLACK_RGB_VALUE);
    }

    /**
     * Creates a new SVGColorManager.
     */
    public SVGColorManager(String prop, Value v) {
        property = prop;
        defaultValue = v;
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
	return property;
    }

    
    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
            if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_CURRENTCOLOR_VALUE)) {
                return SVGValueConstants.CURRENTCOLOR_VALUE;
            }
        }
        Value v = super.createValue(lu, engine);
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return v;
        }
        if (lu.getLexicalUnitType() != LexicalUnit.SAC_FUNCTION ||
            !lu.getFunctionName().equalsIgnoreCase("icc-color")) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
        }
        lu = lu.getParameters();
        if (lu.getLexicalUnitType() != LexicalUnit.SAC_IDENT) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
        }
        ListValue result = new ListValue(' ');
        result.append(v);

        ICCColor icc = new ICCColor(lu.getStringValue());
        result.append(icc);

        lu = lu.getNextLexicalUnit();
        while (lu != null) {
            if (lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createInvalidLexicalUnitDOMException
                    (lu.getLexicalUnitType());
            }
            lu = lu.getNextLexicalUnit();
            if (lu == null) {
                throw createInvalidLexicalUnitDOMException
                    (lu.getLexicalUnitType());
            }
            icc.append(getColorValue(lu));
            lu = lu.getNextLexicalUnit();
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
        if (value == SVGValueConstants.CURRENTCOLOR_VALUE) {
            sm.putColorRelative(idx, true);

            int ci = engine.getColorIndex();
            return engine.getComputedStyle(elt, pseudo, ci);
        }
        if (value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            ListValue lv = (ListValue)value;
            Value v = lv.item(0);
            Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
            if (t != v) {
                ListValue result = new ListValue(' ');
                result.append(t);
                result.append(lv.item(1));
                return result;
            }
            return value;
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }

    /**
     * Creates a float value usable as a component of an RGBColor.
     */
    protected float getColorValue(LexicalUnit lu) {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INTEGER:
	    return lu.getIntegerValue();
	case LexicalUnit.SAC_REAL:
	    return lu.getFloatValue();
	}
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }
}
