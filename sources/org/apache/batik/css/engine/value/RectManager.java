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

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class provides a manager for the property with support for
 * rect values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class RectManager extends LengthManager {
    
    /**
     * The current orientation.
     */
    protected int orientation;

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_FUNCTION:
            if (!lu.getFunctionName().equalsIgnoreCase("rect")) {
                break;
            }
        case LexicalUnit.SAC_RECT_FUNCTION:
            lu = lu.getParameters();
            Value top = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value right = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value bottom = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value left = createRectComponent(lu);
            return new RectValue(top, right, bottom, left);
        }
        throw createMalformedRectDOMException();
    }

    private Value createRectComponent(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_AUTO_VALUE)) {
		return ValueConstants.AUTO_VALUE;
	    }
            break;
	case LexicalUnit.SAC_EM:
	    return new FloatValue(CSSPrimitiveValue.CSS_EMS,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_EX:
	    return new FloatValue(CSSPrimitiveValue.CSS_EXS,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PIXEL:
	    return new FloatValue(CSSPrimitiveValue.CSS_PX,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_CENTIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_CM,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_MILLIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_MM,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_INCH:
	    return new FloatValue(CSSPrimitiveValue.CSS_IN,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_POINT:
	    return new FloatValue(CSSPrimitiveValue.CSS_PT,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PICA:
	    return new FloatValue(CSSPrimitiveValue.CSS_PC,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_INTEGER:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PERCENTAGE:
	    return new FloatValue(CSSPrimitiveValue.CSS_PERCENTAGE,
                                  lu.getFloatValue());
        }
        throw createMalformedRectDOMException();
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
        if (value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE) {
            return value;
        }
        if (value.getPrimitiveType() != CSSPrimitiveValue.CSS_RECT) {
            return value;
        }
        RectValue rect = (RectValue)value;

        orientation = VERTICAL_ORIENTATION;
        Value top = super.computeValue(elt, pseudo, engine, idx, sm,
                                       rect.getTop());
        Value bottom = super.computeValue(elt, pseudo, engine, idx, sm,
                                          rect.getBottom());
        orientation = HORIZONTAL_ORIENTATION;
        Value left = super.computeValue(elt, pseudo, engine, idx, sm,
                                        rect.getLeft());
        Value right = super.computeValue(elt, pseudo, engine, idx, sm,
                                         rect.getRight());
        if (top != rect.getTop() ||
            right != rect.getRight() ||
            bottom != rect.getBottom() ||
            left != rect.getLeft()) {
            return new RectValue(top, right, bottom, left);
        } else {
            return value;
        }
    }

    /**
     * Indicates the orientation of the property associated with
     * this manager.
     */
    protected int getOrientation() {
        return orientation;
    }

    private DOMException createMalformedRectDOMException() {
        Object[] p = new Object[] { getPropertyName() };
        String s = Messages.formatMessage("malformed.rect", p);
        return new DOMException(DOMException.SYNTAX_ERR, s);
    }
}
