/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.StringReader;

import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.util.SVGConstants;

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
public class DefaultUnitProcessorContext
    implements UnitProcessor.Context,
               SVGConstants {

    protected SVGContext context;
    protected SVGElement element;

    public DefaultUnitProcessorContext(SVGContext ctx, SVGElement elt) {
        context = ctx;
        element = elt;
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        return context.getPixelToMM();
    }

    /**
     * Returns the font-size medium value in pt.
     */
    public float getMediumFontSize() {
        return 9;
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
     * Returns the viewport width used to compute units.
     */
    public float getViewportWidth() {
        SVGSVGElement svg = element.getOwnerSVGElement();
        if (svg == null) {
            return (float)context.getViewportWidth(element);
        }
        String s = svg.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        LengthParser p = new LengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(s));
        return UnitProcessor.svgToUserSpace(ur.unit,
                                            ur.value,
                                            svg,
                                            UnitProcessor.HORIZONTAL_LENGTH,
                                            this);
    }

    /**
     * Returns the viewport height used to compute units.
     */
    public float getViewportHeight() {
        SVGSVGElement svg = element.getOwnerSVGElement();
        if (svg == null) {
            return (float)context.getViewportHeight(element);
        }
        String s = svg.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        LengthParser p = new LengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(s));
        return UnitProcessor.svgToUserSpace(ur.unit,
                                            ur.value,
                                            svg,
                                            UnitProcessor.VERTICAL_LENGTH,
                                            this);
    }
}
