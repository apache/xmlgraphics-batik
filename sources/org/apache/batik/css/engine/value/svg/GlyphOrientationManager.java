/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the 'glyph-orientation' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class GlyphOrientationManager extends AbstractValueManager {
    
    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#isInheritedProperty()}.
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
        case LexicalUnit.SAC_INHERIT:
            return SVGValueConstants.INHERIT_VALUE;

        case LexicalUnit.SAC_DEGREE:
            return new FloatValue(CSSPrimitiveValue.CSS_DEG,
                                  lu.getFloatValue());

        case LexicalUnit.SAC_GRADIAN:
            return new FloatValue(CSSPrimitiveValue.CSS_GRAD,
                                  lu.getFloatValue());

        case LexicalUnit.SAC_RADIAN:
            return new FloatValue(CSSPrimitiveValue.CSS_RAD,
                                  lu.getFloatValue());

            // For SVG angle properties unit defaults to 'deg'.
        case LexicalUnit.SAC_INTEGER:
            { 
                int n = lu.getIntegerValue();
                return new FloatValue(CSSPrimitiveValue.CSS_DEG, n);
            }
        case LexicalUnit.SAC_REAL:
            { 
                float n = lu.getFloatValue();
                return new FloatValue(CSSPrimitiveValue.CSS_DEG, n);
            }
        }
    
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link ValueManager#createFloatValue(short,float)}.
     */
    public Value createFloatValue(short type, float floatValue)
        throws DOMException {
	switch (type) {
        case CSSPrimitiveValue.CSS_DEG:
        case CSSPrimitiveValue.CSS_GRAD:
        case CSSPrimitiveValue.CSS_RAD:
            return new FloatValue(type, floatValue);
	}
        throw createInvalidFloatValueDOMException(floatValue);
    }
}
