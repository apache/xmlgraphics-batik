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

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
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

        CompositeGraphicsNode gn = new CompositeGraphicsNode();

        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(element);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              decl);

        // parse the x attribute, (default is 0)
        String s = element.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = SVGUtilities.svgToUserSpace(element,
                                            SVG_X_ATTRIBUTE, s,
                                            uctx,
                                            UnitProcessor.HORIZONTAL_LENGTH);
        }

        // parse the y attribute, (default is 0)
        s = element.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = SVGUtilities.svgToUserSpace(element,
                                            SVG_Y_ATTRIBUTE, s,
                                            uctx,
                                            UnitProcessor.VERTICAL_LENGTH);
        }

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.preConcatenate(
                 SVGUtilities.convertAffineTransform(element, ATTR_TRANSFORM));

        gn.setTransform(at);

        Rectangle2D rect =
        CSSUtilities.convertEnableBackground((SVGElement)element,
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
        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(element);

        CSSPrimitiveValue val =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(val);
        gn.setComposite(composite);

        // Set the node filter
        Filter filter = CSSUtilities.convertFilter(element, gn, ctx);
        gn.setFilter(filter);

        // Set the node mask
        Mask mask = CSSUtilities.convertMask(element, gn, ctx);
        gn.setMask(mask);

        // Set the node clip
        Clip clip = CSSUtilities.convertClipPath(element, gn, ctx);
        gn.setClip(clip);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
