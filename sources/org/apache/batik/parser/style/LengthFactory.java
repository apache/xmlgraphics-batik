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
 * This class provides a factory for CSS lengths.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthFactory extends AbstractLengthFactory {
    /**
     * Creates a new LengthFactory object.
     */
    public LengthFactory() {
    }

    /**
     * Returns a CSSValue built from the given SAC lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public CSSValue createCSSValue(LexicalUnit lu) {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_PERCENTAGE:
            return new CSSFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE,
                                     lu.getFloatValue());
        case LexicalUnit.SAC_IDENT:
            if (lu.getStringValue().equalsIgnoreCase(AUTO)) {
                return AUTO_VALUE;
            }
        }
        return super.createCSSValue(lu);
    }
}
