/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.RenderedImage;

import java.awt.image.renderable.RenderContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.filter.ConcreteAffineRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
import org.apache.batik.refimpl.gvt.filter.RasterRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

/**
 * This class bridges an SVG <tt>feImage</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id $
 */
public class SVGFeImageElementBridge implements FilterBridge,
                                                SVGConstants {
    
    public final static String PROTOCOL_DATA = "data:";

    /**
     * Returns the <tt>Filter</tt> that implements the filter
     * operation modeled by the input DOM element
     *
     * @param filteredNode the node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chain
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter create(GraphicsNode filteredNode,
                         BridgeContext bridgeContext,
                         Element filterElement,
                         Element filteredElement,
                         Filter in,
                         FilterRegion filterRegion,
                         Map filterMap){
        SVGElement svgElement = (SVGElement) filterElement;

        String uriStr = XLinkSupport.getXLinkHref(svgElement);
        // nothing referenced.
        if (uriStr == null) {
            return null;
        }

        // feImage's default region is that of the filter chain.
        FilterRegion defaultRegion = filterRegion;
        
        // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           && (parentNode.getNodeType() == parentNode.ELEMENT_NODE)) {
            units = ((Element)parentNode).
                getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
        }
        
        //
        // Now, extraact filter region
        //
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                          null);
        
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);
        
        final FilterRegion primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);
        Filter filter = null;
        if (uriStr.startsWith(PROTOCOL_DATA))
            filter = RasterRable.create(uriStr, null);
        else {
            SVGDocument svgDoc;
            svgDoc = (SVGDocument)filterElement.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            URL url = null;
            try {
                url = new URL(baseURL, uriStr);
            } catch (MalformedURLException mue) {
                return null;
            }

            try {

                URIResolver ur = new URIResolver
                    (svgDoc, bridgeContext.getDocumentLoader());
            
                Node refNode = ur.getNode(url.toString());
                if (refNode == null)
                    return null;

                Element refElement;
                if (refNode.getNodeType() == refNode.DOCUMENT_NODE)
                    refElement = ((SVGDocument)refNode).getRootElement();
                else
                    refElement = (Element)refNode;

                // Cannot access referenced file...
                if(refElement == null){
                    return null;
                }
             
                GraphicsNode gn;
                gn = bridgeContext.getGVTBuilder().build(bridgeContext, 
                                                         refElement);

                GraphicsNodeRableFactory gnrFactory
                    = bridgeContext.getGraphicsNodeRableFactory();
                filter = gnrFactory.createGraphicsNodeRable(gn);

                //
                // Need to translate the image to the x, y coordinate to
                // have the same behavior as the <use> element
                // <!> FIX ME? I THINK THIS IS ONLY PARTIALLY IMPLEMENTING THE
                //     SPEC. 
                filter
                    = new ConcreteAffineRable(filter, new AffineTransform()){
                            public Rectangle2D getBounds2D(){
                                return primitiveRegion.getRegion();
                            }

                            private void computeTransform(){
                                Rectangle2D bounds = getSource().getBounds2D();
                                Rectangle2D region = primitiveRegion.getRegion();
                                AffineTransform at = new AffineTransform();
                                at.translate(region.getX(), region.getY());
                                setAffine(at);
                            }
                                
                            public RenderedImage createRendering(RenderContext rc){
                                computeTransform();
                                return super.createRendering(rc);
                            }
                        };
                
            } catch (Exception ex) {
                // 
                // Need to fit the raster image to the filter
                // region so that we have the same behavior as
                // raster images in the <image> element.
                //
                filter = RasterRable.create(url, null);

                filter
                    = new ConcreteAffineRable(filter, new AffineTransform()){
                            public Rectangle2D getBounds2D(){
                                return primitiveRegion.getRegion();
                            }

                            public AffineTransform getAffine() {
                                computeTransform();
                                return super.getAffine();
                            }
                            
                            private void computeTransform() {
                                Rectangle2D bounds = getSource().getBounds2D();
                                Rectangle2D region = primitiveRegion.getRegion();
                                AffineTransform scale = new AffineTransform();
                                scale.translate(region.getX(), region.getY());
                                scale.scale(region.getWidth()/bounds.getWidth(),
                                            region.getHeight()/bounds.getHeight());
                                scale.translate(-bounds.getX(), -bounds.getY());

                                setAffine(scale);
                            }
                                
                            public RenderedImage createRendering(RenderContext rc){
                                computeTransform();
                                return super.createRendering(rc);
                            }
                        };
            }
        }
        
        filter = new ConcretePadRable(filter,
                                      new Rectangle2D.Double(0, 0, 0, 0),
                                      PadMode.ZERO_PAD) {
                public Rectangle2D getBounds2D(){
                    setPadRect(primitiveRegion.getRegion());
                    return super.getBounds2D();
                }

                public Rectangle2D getPadRect(){
                    setPadRect(primitiveRegion.getRegion());
                    return super.getPadRect();
                }
                
                public java.awt.image.RenderedImage createRendering
                    (java.awt.image.renderable.RenderContext rc){
                    setPadRect(primitiveRegion.getRegion());
                    return super.createRendering(rc);
                }
            };
        
        
        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            // The filter will be added to the filter map. Before
            // we do that, append the filter region crop
            filterMap.put(result, filter);
        }

        return filter;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

}
