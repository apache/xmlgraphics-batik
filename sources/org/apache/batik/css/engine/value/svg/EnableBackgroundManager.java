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
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class provides a manager for the 'enable-background' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class EnableBackgroundManager extends LengthManager {
    
    /**
     * The length orientation.
     */
    protected int orientation;

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
	return CSSConstants.CSS_ENABLE_BACKGROUND_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.ACCUMULATE_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_INHERIT:
            return SVGValueConstants.INHERIT_VALUE;

        default:
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());

        case LexicalUnit.SAC_IDENT:
            String id = lu.getStringValue().toLowerCase().intern();
            if (id == CSSConstants.CSS_ACCUMULATE_VALUE) {
                return SVGValueConstants.ACCUMULATE_VALUE;
            }
            if (id != CSSConstants.CSS_NEW_VALUE) {
                throw createInvalidIdentifierDOMException(id);
            }
            ListValue result = new ListValue(' ');
            result.append(SVGValueConstants.NEW_VALUE);
            lu = lu.getNextLexicalUnit();
            if (lu == null) {
                return result;
            }
            result.append(super.createValue(lu, engine));
            for (int i = 1; i < 4; i++) {
                lu = lu.getNextLexicalUnit();
                if (lu == null){
                    throw createMalformedLexicalUnitDOMException();
                }
                result.append(super.createValue(lu, engine));
            }
            return result;
        }
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value,
                                   CSSEngine engine) {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
            throw createInvalidStringTypeDOMException(type);
	}
	if (!value.equalsIgnoreCase(CSSConstants.CSS_ACCUMULATE_VALUE)) {
            throw createInvalidIdentifierDOMException(value);
        }
	return SVGValueConstants.ACCUMULATE_VALUE;
    }

    /**
     * Implements {@link ValueManager#createFloatValue(short,float)}.
     */
    public Value createFloatValue(short unitType, float floatValue)
	throws DOMException {
        throw createDOMException();
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
        if (value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            ListValue lv = (ListValue)value;
            if (lv.getLength() == 5) {
                Value lv1 = lv.item(1);
                orientation = HORIZONTAL_ORIENTATION;
                Value v1 = super.computeValue(elt, pseudo, engine,
                                              idx, sm, lv1);
                Value lv2 = lv.item(2);
                orientation = VERTICAL_ORIENTATION;
                Value v2 = super.computeValue(elt, pseudo, engine,
                                              idx, sm, lv2);
                Value lv3 = lv.item(3);
                orientation = HORIZONTAL_ORIENTATION;
                Value v3 = super.computeValue(elt, pseudo, engine,
                                              idx, sm, lv3);
                Value lv4 = lv.item(4);
                orientation = VERTICAL_ORIENTATION;
                Value v4 = super.computeValue(elt, pseudo, engine,
                                              idx, sm, lv4);

                if (lv1 != v1 || lv2 != v2 ||
                    lv3 != v3 || lv4 != v4) {
                    ListValue result = new ListValue(' ');
                    result.append(lv.item(0));
                    result.append(v1);
                    result.append(v2);
                    result.append(v3);
                    result.append(v4);
                    return result;
                }
            }
        }
        return value;
    }

    /**
     * Indicates the orientation of the property associated with
     * this manager.
     */
    protected int getOrientation() {
        return orientation;
    }

}
