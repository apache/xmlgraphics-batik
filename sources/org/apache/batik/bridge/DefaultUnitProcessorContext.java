/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.bridge.Viewport;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * The default unit processor context.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DefaultUnitProcessorContext implements UnitProcessor.Context {

    protected BridgeContext ctx;
    protected CSSStyleDeclaration cssDecl;

    public DefaultUnitProcessorContext(BridgeContext ctx,
                                       CSSStyleDeclaration cssDecl) {
        this.ctx = ctx;
        this.cssDecl = cssDecl;
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        return ctx.getUserAgent().getPixelToMM();
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
        return UnitProcessor.getFontSize(e, cssDecl);
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
        return ctx.getCurrentViewport().getWidth();
    }

    /**
     * Returns the viewport height used to compute units.
     */
    public float getViewportHeight() {
        return ctx.getCurrentViewport().getHeight();
    }
}
