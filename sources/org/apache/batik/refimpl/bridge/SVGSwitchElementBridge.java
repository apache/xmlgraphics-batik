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
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.views.DocumentView;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;

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
        GraphicsNode gn = ctx.getGVTFactory().createCompositeGraphicsNode();
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(element.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        gn.setTransform(at);

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

        return gn;
    }

    public void buildGraphicsNode(GraphicsNode node, BridgeContext ctx,
                                  Element elt) {
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
