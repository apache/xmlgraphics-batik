/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.svg.DefaultSVGCSSContext;
import org.apache.batik.css.svg.SVGCSSContext;
import org.apache.batik.parser.ParserFactory;


/**
 * This class is the placeholder for SVG application informations.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultSVGContext
    extends    DefaultSVGCSSContext
    implements SVGContext {

    /**
     * The parser factory.
     */
    protected ParserFactory parserFactory;

    /**
     * The pixel to mm conversion factor.
     */
    protected float pixelToMM;

    /**
     * The viewport width.
     */
    protected float viewportWidth;

    /**
     * The viewport height.
     */
    protected float viewportHeight;

    /**
     * Returns the parser factory.
     */
    public ParserFactory getParserFactory() {
        return parserFactory;
    }

    /**
     * Sets the parser factory.
     */
    public void setParserFactory(ParserFactory pf) {
        parserFactory = pf;
    }

    /**
     * Return the pixel to millimeters factor.
     */
    public float getPixelToMM() {
        return pixelToMM;
    }

    /**
     * Sets the pixel to millimeters factor.
     */
    public void setPixelToMM(float f) {
        pixelToMM = f;
    }
}
