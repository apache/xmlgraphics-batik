/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.views.DocumentView;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &lt;use&gt; SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGUseElementBridge
    implements GraphicsNodeBridge,
               SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx,
                                           Element element){
        GraphicsNode gn = ctx.getGVTFactory().createCompositeGraphicsNode();
        CSSStyleDeclaration decl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              decl);
        String s = element.getAttributeNS(null, ATTR_X);
        float x = UnitProcessor.svgToUserSpace(s,
                                               (SVGElement)element,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                               uctx);
        s = element.getAttributeNS(null, ATTR_Y);
        float y = UnitProcessor.svgToUserSpace(s,
                                               (SVGElement)element,
                                               UnitProcessor.VERTICAL_LENGTH,
                                               uctx);
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.preConcatenate(AWTTransformProducer.createAffineTransform
                (new StringReader(element.getAttributeNS(null, ATTR_TRANSFORM)),
                 ctx.getParserFactory()));
        gn.setTransform(at);

        CSSPrimitiveValue val =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(val);
        gn.setComposite(composite);

        Filter filter = CSSUtilities.convertFilter(element, gn, ctx);
        gn.setFilter(filter);

        return gn;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
