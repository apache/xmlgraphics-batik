/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents the factories for the rect CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractRectFactory extends AbstractCSSValueFactory {
    /**
     * The factory for the parameters.
     */
    protected LengthFactory factory = new LengthFactory();

    /**
     * Creates a new AbstractRectFactory object.
     */
    protected AbstractRectFactory() {
    }

    /**
     * Returns a CSSValue built from the given SAC lexical unit.
     */
    public CSSValue createCSSValue(LexicalUnit lu) {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_FUNCTION:
            if (!lu.getFunctionName().equalsIgnoreCase("rect")) {
                break;
            }
        case LexicalUnit.SAC_RECT_FUNCTION:
            lu = lu.getParameters();
            CSSPrimitiveValue t;
            t = (CSSPrimitiveValue)factory.createCSSValue(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw new DOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
                     ("invalid.lexical.unit",
                      new Object[] { new Integer(lu.getLexicalUnitType()) }));
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue r;
            r = (CSSPrimitiveValue)factory.createCSSValue(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw new DOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
                     ("invalid.lexical.unit",
                      new Object[] { new Integer(lu.getLexicalUnitType()) }));
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue b;
            b = (CSSPrimitiveValue)factory.createCSSValue(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw new DOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
                     ("invalid.lexical.unit",
                      new Object[] { new Integer(lu.getLexicalUnitType()) }));
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue l;
            l = (CSSPrimitiveValue)factory.createCSSValue(lu);
            return new CSSRectValue(t, r, b, l);
        }
        throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("invalid.lexical.unit",
              new Object[] { new Integer(lu.getLexicalUnitType()) }));
    }
}
