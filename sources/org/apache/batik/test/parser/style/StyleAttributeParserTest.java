/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.parser.style;

import java.io.*;
import org.apache.batik.parser.style.*;
import org.w3c.dom.css.*;

/**
 * To test the StyleAttributeParser test.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleAttributeParserTest {
    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        StyleAttributeParser parser;
        parser = new DefaultStyleAttributeParser
            ("org.w3c.flute.parser.Parser");

        CSSValue v;
        CSSPrimitiveValue pv;

        System.out.println(" **** rect test **** ");
        v = parser.parse(new StringReader("rect(1, 2, 3, 4)"), null, "clip");

        System.out.println("  type: " + v.getCssValueType());
        if (v.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE) {
            throw new RuntimeException("test failed: not a primitive value");
        }

        pv = (CSSPrimitiveValue)v;
        if (pv.getPrimitiveType() != CSSPrimitiveValue.CSS_RECT) {
            throw new RuntimeException("test failed: not a rect value");
        }

        System.out.println("CSS text: " + pv.getCssText());

        System.out.println(" **** TEST OK **** ");
    }

}
