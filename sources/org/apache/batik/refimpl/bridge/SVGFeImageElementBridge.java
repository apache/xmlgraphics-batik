/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.geom.Rectangle2D;
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
     * @param filterRegion the filter area defined for the filter chained
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
             } catch (Exception ex) {
                 filter = RasterRable.create(url, null);
             }
         }

        FilterRegion defaultRegion = new FilterSourceRegion(filter);
        
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
        
        final FilterRegion compositeArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);
        
        filter = new ConcretePadRable(filter,
                                      compositeArea.getRegion(),
                                      PadMode.ZERO_PAD) {
                public Rectangle2D getBounds2D(){
                    setPadRect(compositeArea.getRegion());
                    return super.getBounds2D();
                }
                
                public java.awt.image.RenderedImage createRendering
                    (java.awt.image.renderable.RenderContext rc){
                    setPadRect(compositeArea.getRegion());
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
