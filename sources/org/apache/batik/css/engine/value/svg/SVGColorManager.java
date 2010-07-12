/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.svg12.CIELCHColor;
import org.apache.batik.css.engine.value.svg12.CIELabColor;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.batik.css.engine.value.svg12.ICCNamedColor;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGTypes;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

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
     * Implements {@link ValueManager#isAnimatableProperty()}.
     */
    public boolean isAnimatableProperty() {
        return true;
    }

    /**
     * Implements {@link ValueManager#isAdditiveProperty()}.
     */
    public boolean isAdditiveProperty() {
        return true;
    }

    /**
     * Implements {@link ValueManager#getPropertyType()}.
     */
    public int getPropertyType() {
        return SVGTypes.TYPE_COLOR;
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

        //If we have more content here, there is a color function after the sRGB color.
        if (lu.getLexicalUnitType() != LexicalUnit.SAC_FUNCTION) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
        }

        ListValue result = new ListValue(' ');
        result.append(v);

        Value colorValue = parseColorFunction(lu, v);
        if (colorValue != null) {
            result.append(colorValue);
        } else {
            throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
        return result;
    }

    private Value parseColorFunction(LexicalUnit lu, Value v) {
        String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase(ICCColor.ICC_COLOR_FUNCTION)) {
            return createICCColorValue(lu, v);
        }
        return parseColor12Function(lu, v);
    }

    private Value parseColor12Function(LexicalUnit lu, Value v) {
        String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase(ICCNamedColor.ICC_NAMED_COLOR_FUNCTION)) {
            return createICCNamedColorValue(lu, v);
        } else if (functionName.equalsIgnoreCase(CIELabColor.CIE_LAB_COLOR_FUNCTION)) {
            return createCIELabColorValue(lu, v);
        } else if (functionName.equalsIgnoreCase(CIELCHColor.CIE_LCH_COLOR_FUNCTION)) {
            return createCIELCHColorValue(lu, v);
        } else if (functionName.equalsIgnoreCase(DeviceColor.DEVICE_CMYK_COLOR_FUNCTION)) {
            return createDeviceColorValue(lu, v, 4);
        } else if (functionName.equalsIgnoreCase(DeviceColor.DEVICE_RGB_COLOR_FUNCTION)) {
            return createDeviceColorValue(lu, v, 3);
        } else if (functionName.equalsIgnoreCase(DeviceColor.DEVICE_GRAY_COLOR_FUNCTION)) {
            return createDeviceColorValue(lu, v, 1);
        } else if (functionName.equalsIgnoreCase(DeviceColor.DEVICE_NCHANNEL_COLOR_FUNCTION)) {
            return createDeviceColorValue(lu, v, 0);
        }
        return null;
    }

    private Value createICCColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        expectIdent(lu);

        ICCColor icc = new ICCColor(lu.getStringValue());

        lu = lu.getNextLexicalUnit();
        while (lu != null) {
            expectComma(lu);
            lu = lu.getNextLexicalUnit();
            icc.append(getColorValue(lu));
            lu = lu.getNextLexicalUnit();
        }
        return icc;
    }

    private Value createICCNamedColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        expectIdent(lu);
        String profileName = lu.getStringValue();

        lu = lu.getNextLexicalUnit();
        expectComma(lu);
        lu = lu.getNextLexicalUnit();
        expectIdent(lu);
        String colorName = lu.getStringValue();

        ICCNamedColor icc = new ICCNamedColor(profileName, colorName);

        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createCIELabColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        float l = getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float a = getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float b = getColorValue(lu);

        CIELabColor icc = new CIELabColor(l, a, b);

        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createCIELCHColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        float l = getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float c = getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float h = getColorValue(lu);

        CIELCHColor icc = new CIELCHColor(l, c, h);

        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createDeviceColorValue(LexicalUnit lu, Value v, int expectedComponents) {
        lu = lu.getParameters();

        boolean nChannel = (expectedComponents <= 0);
        DeviceColor col = new DeviceColor(nChannel);

        col.append(getColorValue(lu));
        LexicalUnit lastUnit = lu;
        lu = lu.getNextLexicalUnit();
        while (lu != null) {
            expectComma(lu);
            lu = lu.getNextLexicalUnit();
            col.append(getColorValue(lu));
            lastUnit = lu;
            lu = lu.getNextLexicalUnit();
        }
        if (!nChannel && expectedComponents != col.getNumberOfColors()) {
            throw createInvalidLexicalUnitDOMException(lastUnit.getLexicalUnitType());
        }
        return col;
    }

    private void expectIdent(LexicalUnit lu) {
        if (lu.getLexicalUnitType() != LexicalUnit.SAC_IDENT) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
        }
    }

    private void expectComma(LexicalUnit lu) {
        if (lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
        }
    }

    private void expectNonNull(LexicalUnit lu) {
        if (lu == null) {
            throw createInvalidLexicalUnitDOMException((short)-1);
        }
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
        expectNonNull(lu);
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_INTEGER:
            return lu.getIntegerValue();
        case LexicalUnit.SAC_REAL:
            return lu.getFloatValue();
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }
}
