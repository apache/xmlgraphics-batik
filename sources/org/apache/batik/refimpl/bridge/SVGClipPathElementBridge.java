/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.io.StringReader;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.ClipBridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;

/**
 * A factory for the &lt;clip-path&gt; SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGClipPathElementBridge implements ClipBridge, SVGConstants {

    /**
     * Returns the <tt>Shape</tt> referenced by the input element's
     * <tt>clip-path</tt> attribute.
     */
    public Shape createClip(GraphicsNode clipedNode,
                            BridgeContext bridgeContext,
                            Element clipElement,
                            Element clipedElement) {
        CSSStyleDeclaration decl
            = bridgeContext.getViewCSS().getComputedStyle(clipElement,
                                                          null);

        //
        // <!> FIX ME : ignore 'objectBoundingBox' and 'userSpaceOnUse'
        //

        // Build the GVT tree that represents the clip path
        Area area = new Area();
        GVTBuilder builder = bridgeContext.getGVTBuilder();
        for(Node child=clipElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                Element e = (Element)child;
                GraphicsNode node
                    = builder.build(bridgeContext, e) ;
                if(node != null){
                    area.add(new Area(node.getOutline()));
                }
            }
        }
        GeneralPath clipPath = new GeneralPath(area);

        // apply the winding rule
        CSSPrimitiveValue v;
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CLIP_RULE_PROPERTY);
        int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
            ? GeneralPath.WIND_NON_ZERO
            : GeneralPath.WIND_EVEN_ODD;

        clipPath.setWindingRule(wr);

        // apply the transform on the clip-Path Element
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(clipElement.getAttributeNS(null, ATTR_TRANSFORM)),
             bridgeContext.getParserFactory());

        return at.createTransformedShape(clipPath);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
