/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
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
                throw createInvalidLexicalUnitDOMException((short)-1);
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
