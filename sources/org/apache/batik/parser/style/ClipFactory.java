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
import org.w3c.dom.css.CSSValue;

/**
 * This class represents the factories for the "clip" attribute value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ClipFactory extends AbstractRectFactory {
    /**
     * Creates a new ClipFactory object.
     */
    public ClipFactory() {
    }

    /**
     * Returns a CSSValue built from the given SAC lexical unit.
     */
    public CSSValue createCSSValue(LexicalUnit lu) {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_INHERIT:
            return INHERIT;
        case LexicalUnit.SAC_IDENT:
            if (!lu.getStringValue().equalsIgnoreCase(AUTO)) {
                throw new DOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
                     ("invalid.identifier",
                      new Object[] { lu.getStringValue() }));
            }
            return AUTO_VALUE;
        default:
            return super.createCSSValue(lu);
        }
    }
}
