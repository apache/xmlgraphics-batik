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
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.DOMException;

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
            // Fall through
        default:
            return super.createValue(lu, engine);
        case LexicalUnit.SAC_URI:
        }
        String value = lu.getStringValue();
        String uri = resolveURI(engine.getCSSBaseURI(), value);
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return new URIValue(value, uri);
        }

        ListValue result = new ListValue(' ');
        result.append(new URIValue(value, uri));

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
