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

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class provides a manager for the 'cursor' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CursorManager extends AbstractValueManager {
    
    /**
     * The identifier values.
     */
    protected final static StringMap values = new StringMap();
    static {
	values.put(CSSConstants.CSS_AUTO_VALUE,
                   ValueConstants.AUTO_VALUE);
	values.put(CSSConstants.CSS_CROSSHAIR_VALUE,
                   ValueConstants.CROSSHAIR_VALUE);
	values.put(CSSConstants.CSS_DEFAULT_VALUE,
                   ValueConstants.DEFAULT_VALUE);
	values.put(CSSConstants.CSS_E_RESIZE_VALUE,
                   ValueConstants.E_RESIZE_VALUE);
	values.put(CSSConstants.CSS_HELP_VALUE,
                   ValueConstants.HELP_VALUE);
	values.put(CSSConstants.CSS_MOVE_VALUE,
                   ValueConstants.MOVE_VALUE);
	values.put(CSSConstants.CSS_N_RESIZE_VALUE,
                   ValueConstants.N_RESIZE_VALUE);
	values.put(CSSConstants.CSS_NE_RESIZE_VALUE,
                   ValueConstants.NE_RESIZE_VALUE);
	values.put(CSSConstants.CSS_NW_RESIZE_VALUE,
                   ValueConstants.NW_RESIZE_VALUE);
	values.put(CSSConstants.CSS_POINTER_VALUE,
                   ValueConstants.POINTER_VALUE);
	values.put(CSSConstants.CSS_S_RESIZE_VALUE,
                   ValueConstants.S_RESIZE_VALUE);
	values.put(CSSConstants.CSS_SE_RESIZE_VALUE,
                   ValueConstants.SE_RESIZE_VALUE);
	values.put(CSSConstants.CSS_SW_RESIZE_VALUE,
                   ValueConstants.SW_RESIZE_VALUE);
	values.put(CSSConstants.CSS_TEXT_VALUE,
                   ValueConstants.TEXT_VALUE);
	values.put(CSSConstants.CSS_W_RESIZE_VALUE,
                   ValueConstants.W_RESIZE_VALUE);
	values.put(CSSConstants.CSS_WAIT_VALUE,
                   ValueConstants.WAIT_VALUE);
    }

    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_CURSOR_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return ValueConstants.AUTO_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        ListValue result = new ListValue();
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return ValueConstants.INHERIT_VALUE;

        case LexicalUnit.SAC_URI:
            do {
                result.append(new URIValue(lu.getStringValue(),
                                           resolveURI(engine.getCSSBaseURI(),
                                                      lu.getStringValue())));
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    throw createMalformedLexicalUnitDOMException();
                }
                if (lu.getLexicalUnitType() !=
                    LexicalUnit.SAC_OPERATOR_COMMA) {
                    throw createInvalidLexicalUnitDOMException
                        (lu.getLexicalUnitType());
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    throw createMalformedLexicalUnitDOMException();
                }
            } while (lu.getLexicalUnitType() == LexicalUnit.SAC_URI);
            if (lu.getLexicalUnitType() != LexicalUnit.SAC_IDENT) {
                throw createInvalidLexicalUnitDOMException
                    (lu.getLexicalUnitType());
            }
            // Fall through...

        case LexicalUnit.SAC_IDENT:
            String s = lu.getStringValue().toLowerCase().intern();
	    Object v = values.get(s);
	    if (v == null) {
		throw createInvalidIdentifierDOMException(lu.getStringValue());
	    }
            result.append((Value)v);
            lu = lu.getNextLexicalUnit();
        }
        if (lu != null) {
            throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
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
        if (value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            ListValue lv = (ListValue)value;
            int len = lv.getLength();
            ListValue result = new ListValue(' ');
            for (int i=0; i<len; i++) {
                Value v = lv.item(0);
                if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                    // Reveal the absolute value as the cssText now.
                    result.append(new URIValue(v.getStringValue(),
                                               v.getStringValue()));
                } else {
                    result.append(v);
                }
            }
            return result;
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }

}
