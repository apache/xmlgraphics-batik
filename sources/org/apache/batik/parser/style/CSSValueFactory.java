/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSValue;

/**
 * This is the interface to implement in order to allow the parsing of
 * a custom style attribute by a StyleAttributeParser. 
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSValueFactory {
    /**
     * Returns a CSSValue built from the given SAC lexical unit.
     * @param l The lexical unit representing the value.
     */
    CSSValue createCSSValue(LexicalUnit l);
}
