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

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MaskBridge;
import org.apache.batik.bridge.ObjectBoundingBoxViewport;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.refimpl.gvt.filter.ConcreteMaskRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

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
public class SVGMaskElementBridge implements MaskBridge, SVGConstants {

    /**
     * Returns the <tt>Mask</tt> referenced by the input
     * element's <tt>mask</tt> attribute.
     */
    public Mask createMask(GraphicsNode  maskedNode,
                           BridgeContext bridgeContext,
                           Element maskElement,
                           Element maskedElement) {
        //
        // Get the mask region
        //
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(maskElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D maskRegion
            = SVGUtilities.convertMaskRegion(maskElement,
                                             maskedElement,
                                             maskedNode,
                                             uctx);

        //
        // Build the GVT tree that represents the mask
        //
        String maskContentUnits
            = maskElement.getAttributeNS(null, ATTR_MASK_CONTENT_UNITS);
        if(maskContentUnits.length() == 0){
            maskContentUnits = SVG_USER_SPACE_ON_USE_VALUE;
        }
        int maskContentUnitsType;
        try {
            maskContentUnitsType =
                SVGUtilities.parseCoordinateSystem(maskContentUnits);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("mask.maskContentUnits.invalid",
                                       new Object[] {maskContentUnits,
                                                     ATTR_MASK_CONTENT_UNITS}));
        }

        Viewport oldViewport = bridgeContext.getCurrentViewport();
        if(maskContentUnitsType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            bridgeContext.setCurrentViewport(new ObjectBoundingBoxViewport());
        }

        GVTBuilder builder = bridgeContext.getGVTBuilder();
        CompositeGraphicsNode maskNode
            = bridgeContext.getGVTFactory().createCompositeGraphicsNode();
        CompositeGraphicsNode maskNodeContent
            = bridgeContext.getGVTFactory().createCompositeGraphicsNode();
        maskNode.getChildren().add(maskNodeContent);
        boolean hasChildren = false;
        for(Node node=maskElement.getFirstChild();
                node != null;
                node = node.getNextSibling()){

            // check if the node is a valid Element
            if(node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element)node;
            GraphicsNode gn = builder.build(bridgeContext, child) ;
            if(gn == null) {
                continue; // skip element has <mask> can contain <defs>...
                /*
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("mask.subelement.illegal",
                                           new Object[] {node.getLocalName()}));
                                           */
            }
            hasChildren = true;
            maskNodeContent.getChildren().add(gn);
        }
        // restore the viewport
        bridgeContext.setCurrentViewport(oldViewport);
        if (!hasChildren) {
            return null; // no mask defined
        }

        // Get the mask transform
        AffineTransform at =
            SVGUtilities.convertAffineTransform(maskElement,
                                                ATTR_TRANSFORM,
                                              bridgeContext.getParserFactory());


        at = SVGUtilities.convertAffineTransform(at,
                                                 maskedNode,
                                                 maskContentUnitsType);
        maskNodeContent.setTransform(at);

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
