/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.gvt.filter.Mask;
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
 * A factory for the &lt;switch&gt; SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGSwitchElementBridge
    implements GraphicsNodeBridge,
               SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx,
                                           Element element){
        CompositeGraphicsNode gn;
        gn = ctx.getGVTFactory().createCompositeGraphicsNode();
        AffineTransform at =
            SVGUtilities.convertAffineTransform(element,
                                                ATTR_TRANSFORM,
                                                ctx.getParserFactory());

        gn.setTransform(at);

        CSSStyleDeclaration decl;
        decl = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, decl);

        Rectangle2D rect = CSSUtilities.convertEnableBackground((SVGElement)element,
                                                                decl,
                                                                uctx);
        if (rect != null) {
            gn.setBackgroundEnable(rect);
        }

        return gn;
    }

    public void buildGraphicsNode(GraphicsNode gn, 
                                  BridgeContext ctx,
                                  Element element) {

        Document document = element.getOwnerDocument();
        ViewCSS viewCSS = (ViewCSS) ((DocumentView) document).getDefaultView();
        CSSStyleDeclaration decl = viewCSS.getComputedStyle(element, null);
        CSSPrimitiveValue val =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(val);
        gn.setComposite(composite);

        Filter filter = CSSUtilities.convertFilter(element, gn, ctx);
        gn.setFilter(filter);

        Mask mask = CSSUtilities.convertMask(element, gn, ctx);
        gn.setMask(mask);

        Clip clip = CSSUtilities.convertClipPath(element, gn, ctx);
        gn.setClip(clip);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, gn);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
