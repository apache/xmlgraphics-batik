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
import org.apache.batik.bridge.MaskBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.refimpl.gvt.filter.ConcreteMaskRable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.views.DocumentView;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A factory for the &lt;mask&gt; SVG element.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
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
        
        // Get the mask region
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(maskElement, 
                                                          null);
        
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);
        
        FilterRegion maskRegion = SVGUtilities.convertMaskRegion(maskElement,
                                                                 maskedElement,
                                                                 maskedNode,
                                                                 uctx);

        if(maskRegion == null){
            throw new Error();
        }
        else{
            System.out.println("Mask region : " + maskRegion);
        }

        // 
        // <!> FIX ME. HOW SHOULD WE CREATE A GVT TREE THAT REPRESENTS
        //     THE MASK. THIS IS NOT CLEAN.
        //

        org.apache.batik.bridge.GVTBuilder builder 
            = new org.apache.batik.refimpl.bridge.ConcreteGVTBuilder();

        CompositeGraphicsNode maskNodeContent 
            = bridgeContext.getGVTFactory().createCompositeGraphicsNode();

        for(Node child=maskElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                Element e = (Element)child;
                GraphicsNode node 
                    = builder.build(bridgeContext, e) ;
                if(node != null){
                    maskNodeContent.getChildren().add(node);
                }
            }
        }
        
        // OTHER PROBLEM: SHOULD TAKE MASK REGION INTO ACCOUNT
        GraphicsNode maskNode = maskNodeContent;

        // <!> END FIX ME

         if (maskNode == null) {
             System.out.println("Null mask GN");
             System.out.println("maskEl: " + maskElement);
             return null;
         }

         Filter filter = maskedNode.getFilter();
         if (filter == null) {
             // Make the initial source as a RenderableImage
             GraphicsNodeRableFactory gnrFactory
                 = bridgeContext.getGraphicsNodeRableFactory();
             filter = gnrFactory.createGraphicsNodeRable(maskedNode);
         }

        if(maskRegion == null){
            throw new Error();
        }
        else{
            System.out.println("Mask region 2 : " + maskRegion);
        }

         return new ConcreteMaskRable(filter, maskNode, maskRegion);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
