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
import java.awt.geom.Rectangle2D;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.MaskBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.refimpl.gvt.filter.ConcreteMaskRable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A factory for the &lt;mask&gt; SVG element.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGMaskElementBridge
    implements MaskBridge, SVGConstants {

    /**
     * Returns the <tt>Mask</tt> referenced by the input
     * element's <tt>mask</tt> attribute.
     */
    public Mask createMask(GraphicsNode  maskedNode,
                           BridgeContext bridgeContext,
                           Element maskElement,
                           Element maskedElement) {

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle
            (maskElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, 
                                              cssDecl);

        // Get the mask region
        Rectangle2D maskRegion 
            = SVGUtilities.convertMaskRegion(maskElement,
                                             maskedElement,
                                             maskedNode,
                                             uctx);

        if(maskRegion == null) {
            throw new Error();
        }

        // Build the GVT tree that represents the mask
        GVTBuilder builder = bridgeContext.getGVTBuilder();
        CompositeGraphicsNode maskNode
            = bridgeContext.getGVTFactory().createCompositeGraphicsNode();
        for(Node child=maskElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                Element e = (Element)child;
                GraphicsNode node
                    = builder.build(bridgeContext, e) ;
                if(node != null){
                    maskNode.getChildren().add(node);
                }
            }
        }

        // <!> FIXME: Compute the global matrix of this mask Element
        //            Here we just consider the additional transform on mask
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(maskElement.getAttributeNS(null, ATTR_TRANSFORM)),
             bridgeContext.getParserFactory());
        maskNode.setTransform(at);

        // OTHER PROBLEM: SHOULD TAKE MASK REGION INTO ACCOUNT
        Filter filter = maskedNode.getFilter();
        if (filter == null) {
            // Make the initial source as a RenderableImage
            GraphicsNodeRableFactory gnrFactory
                = bridgeContext.getGraphicsNodeRableFactory();
            filter = gnrFactory.createGraphicsNodeRable(maskedNode);
        }

        return new ConcreteMaskRable(filter, maskNode, maskRegion);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
