/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for 'rect' shapes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractRectFactory extends AbstractValueFactory {
    /**
     * The factory for the parameters.
     */
    protected LengthFactory factory = new LengthFactory(getParser(), null);

    /**
     * Creates a new AbstractRectFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    protected AbstractRectFactory(Parser p) {
	super(p);
    }

    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_FUNCTION:
            if (!lu.getFunctionName().equalsIgnoreCase("rect")) {
                break;
            }
        case LexicalUnit.SAC_RECT_FUNCTION:
            lu = lu.getParameters();
            CSSPrimitiveValue t;
            t = new CSSOMValue(factory, factory.createValue(lu));
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue r;
            r = new CSSOMValue(factory, factory.createValue(lu));
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue b;
            b = new CSSOMValue(factory, factory.createValue(lu));
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            lu = lu.getNextLexicalUnit();
            CSSPrimitiveValue l;
            l = new CSSOMValue(factory, factory.createValue(lu));
            return new ImmutableRect(t, r, b, l);
        }
        throw CSSDOMExceptionFactory.createDOMException
            (DOMException.INVALID_ACCESS_ERR,
             "invalid.lexical.unit",
             new Object[] { new Integer(lu.getLexicalUnitType()),
                            getPropertyName() });
    }
}
