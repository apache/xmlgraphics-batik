/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.ext.awt.image.renderable.PadMode;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.RasterRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

/**
 * This class bridges an SVG <tt>feImage</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id $
 */
public class SVGFeImageElementBridge implements FilterPrimitiveBridge,
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
                         Rectangle2D filterRegion,
                         Map filterMap){

        GraphicsNodeRenderContext rc =
                          bridgeContext.getGraphicsNodeRenderContext();
        DocumentLoader loader = bridgeContext.getDocumentLoader();

        SVGElement svgElement = (SVGElement) filterElement;

        String uriStr = XLinkSupport.getXLinkHref(svgElement);
        if (uriStr == null) {
            throw new MissingAttributeException(
                Messages.formatMessage("feImage.xlinkHref.required", null));
        }

        //
        // feImage's default region is that of the filter chain.
        //
        Rectangle2D defaultRegion = filterRegion;

        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(filterElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx,
                                                        loader);

        Filter filter = null;
        if (uriStr.startsWith(PROTOCOL_DATA)) {
            filter = RasterRable.create(uriStr, null, null);
        } else {
            SVGDocument svgDoc;
            svgDoc = (SVGDocument)filterElement.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            URL url = null;
            try {
                url = new URL(baseURL, uriStr);
            } catch (MalformedURLException mue) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("feImage.xlinkHref.badURL", null));
            }

            try {

                URIResolver ur =
                    new URIResolver(svgDoc, bridgeContext.getDocumentLoader());

                Node refNode = ur.getNode(url.toString());
                if (refNode == null) {
                    throw new IllegalAttributeValueException(
                        Messages.formatMessage("feImage.xlinkHref.badURL",
                                               null));
                }

                Element refElement;
                if (refNode.getNodeType() == refNode.DOCUMENT_NODE) {
                    refElement = ((SVGDocument)refNode).getRootElement();
                } else {
                    refElement = (Element)refNode;
                }

                // Cannot access referenced file...
                if(refElement == null){
                    throw new IllegalAttributeValueException(
                        Messages.formatMessage("feImage.xlinkHref.badURL",
                                               null));
                }

                GraphicsNode gn =
                    bridgeContext.getGVTBuilder().build(bridgeContext,
                                                        refElement);

                GraphicsNodeRableFactory gnrFactory
                    = bridgeContext.getGraphicsNodeRableFactory();
                filter = gnrFactory.createGraphicsNodeRable(gn, rc);

                //
                // Need to translate the image to the x, y coordinate to
                // have the same behavior as the <use> element
                // <!> FIX ME? I THINK THIS IS ONLY PARTIALLY IMPLEMENTING THE
                //     SPEC.
                // <!> TO DO : HANDLE TRANSFORM
                AffineTransform at = new AffineTransform();
                at.translate(primitiveRegion.getX(), primitiveRegion.getY());

                filter = new AffineRable8Bit(filter, at);

            } catch (Exception ex) {
                //
                // Need to fit the raster image to the filter region
                // so that we have the same behavior as raster images
                // in the <image> element.
                //
                filter = RasterRable.create(url, null, null);

                Rectangle2D bounds = filter.getBounds2D();
                AffineTransform scale = new AffineTransform();
                scale.translate(primitiveRegion.getX(), primitiveRegion.getY());
                scale.scale(primitiveRegion.getWidth()/bounds.getWidth(),
                            primitiveRegion.getHeight()/bounds.getHeight());
                scale.translate(-bounds.getX(), -bounds.getY());

                filter = new AffineRable8Bit(filter, scale);
            }
        }

        filter = new PadRable8Bit(filter,
                                      primitiveRegion,
                                      PadMode.ZERO_PAD);


        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
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
