/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for values of type color.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SimpleColorFactory
    extends    SVGColorFactory
    implements SVGValueConstants {

    static {
	values.put(CSS_CURRENTCOLOR_VALUE, CURRENTCOLOR_VALUE);
    }

    /**
     * Creates a new SimpleColorFactory object.
     */
    public SimpleColorFactory(Parser p, String prop) {
	super(p, prop);
    }
}
