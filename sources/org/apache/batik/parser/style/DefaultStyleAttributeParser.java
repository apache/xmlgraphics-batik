/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.apache.batik.parser.ParseException;

/**
 * This class represents a style attribute parser initialized for the SVG
 * style attributes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultStyleAttributeParser extends StyleAttributeParser {
    /**
     * Creates a new StyleAttributeParser object.
     * @param s The name of a SAC compliant CSS parser class.
     */
    public DefaultStyleAttributeParser(String s)
        throws ParseException {
        super(s);
        putCSSValueFactory(null, "clip", new ClipFactory());
    }
}
