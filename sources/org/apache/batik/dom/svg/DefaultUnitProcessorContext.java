/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGViewport;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * The default unit processor context.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultUnitProcessorContext implements UnitProcessor.Context {

    protected SVGContext context;
    protected SVGElement element;

    public DefaultUnitProcessorContext(SVGContext ctx,
                                       SVGElement elt) {
        context = ctx;
        element = elt;
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        return context.getUserAgent().getPixelToMM();
    }

    /**
     * Returns the parser factory.
     */
    public ParserFactory getParserFactory() {
        return context.getParserFactory();
    }

    /**
     * Returns the font-size medium value in pt.
     */
    public float getMediumFontSize() {
        return 10;
    }

    /**
     * Returns the font-size value.
     */
    public CSSPrimitiveValue getFontSize(SVGElement e) {
        ViewCSS v = (ViewCSS)e.getOwnerSVGElement();
        return UnitProcessor.getFontSize(e, v.getComputedStyle(e, null));
    }

    /**
     * Returns the x-height value.
     */
    public float getXHeight(SVGElement e) {
        return 0.5f;
    }

    /**
     * Returns the viewport to use to compute the percentages and the units.
     */
    public Viewport getViewport() {
        return new SVGViewport((SVGSVGElement)
            HiddenChildElementSupport.getParentElement(element),
                               this);
    }
}
