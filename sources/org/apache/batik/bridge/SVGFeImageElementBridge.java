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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.util.ParsedURL;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

/**
 * Bridge class for the &lt;feImage> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeImageElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;feImage> element.
     */
    public SVGFeImageElementBridge() {}

    /**
     * Returns 'feImage'.
     */
    public String getLocalName() {
        return SVG_FE_IMAGE_TAG;
    }

    /**
     * Creates a <tt>Filter</tt> primitive according to the specified
     * parameters.
     *
     * @param ctx the bridge context to use
     * @param filterElement the element that defines a filter
     * @param filteredElement the element that references the filter
     * @param filteredNode the graphics node to filter
     *
     * @param inputFilter the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chain
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter createFilter(BridgeContext ctx,
                               Element filterElement,
                               Element filteredElement,
                               GraphicsNode filteredNode,
                               Filter inputFilter,
                               Rectangle2D filterRegion,
                               Map filterMap) {

        // 'xlink:href' attribute
        String uriStr = XLinkSupport.getXLinkHref(filterElement);
        if (uriStr.length() == 0) {
            throw new BridgeException(filterElement, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {"xlink:href"});
        }

        // feImage's default region is that of the filter chain.
        Rectangle2D defaultRegion = filterRegion;

        // get filter primitive chain region
        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);

        Filter filter = null;
        // try to load the image as an svg document
        SVGDocument svgDoc = (SVGDocument)filterElement.getOwnerDocument();
        ParsedURL purl;
        URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
        if (baseURL != null)
            purl = new ParsedURL(baseURL.toString(), uriStr);
        else
            purl = new ParsedURL(uriStr);

        // try to load an SVG document
        DocumentLoader loader = ctx.getDocumentLoader();
        URIResolver resolver = new URIResolver(svgDoc, loader);
        boolean toBBoxNeeded = false;
        try {
            Element refElement = null;
            Node n = resolver.getNode(purl.toString(), filterElement);
            if (n.getNodeType() == n.DOCUMENT_NODE) {
                refElement = ((SVGDocument)n).getRootElement();
            } else if (n.getNodeType() == Node.ELEMENT_NODE) {
                refElement = (Element)n;
                toBBoxNeeded = true;
            } else {
                throw new BridgeException
                    (filterElement, ERR_URI_IMAGE_INVALID,
                     new Object[] {uriStr});
            }
            filter = createSVGFeImage
                (ctx, primitiveRegion, refElement, toBBoxNeeded, filterElement, filteredNode);
        } catch (BridgeException ex) {
            throw ex;
        } catch (Exception ex) { /* Nothing to do */ }

        if (filter == null) {
            // try to load the image as a raster image (JPG or PNG)
            filter = createRasterFeImage(ctx, primitiveRegion, purl);
        }

        if (filter == null) {
            throw new BridgeException(filterElement, ERR_URI_IMAGE_INVALID,
                                      new Object[] {"xlink:href", uriStr});
        }

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }

    /**
     * Returns a Filter that represents a svg document or element as an image.
     *
     * @param ctx the bridge context
     * @param primitiveRegion the primitive region
     * @param Element the referenced element
     * @param toBBoxNeeded true if there is a need to transform to ObjectBoundingBox
     *        space
     * @param filterElement parent filter element
     * @param filteredNode node to which the filter applies
     */
    protected static Filter createSVGFeImage(BridgeContext ctx,
                                             Rectangle2D primitiveRegion,
                                             Element refElement,
                                             boolean toBBoxNeeded,
                                             Element filterElement,
                                             GraphicsNode filteredNode) {

        //
        // <!> FIX ME
        // Unresolved issue on the feImage behavior when referencing an
        // image (PNG, JPEG or SVG image).
        // VH & TK, 03/08/2002
        // Furthermore, for feImage referencing doc fragment, should act
        // like a <use>, i.e., CSS cascading and the whole zing bang.
        //
        GraphicsNode node = ctx.getGVTBuilder().build(ctx, refElement);
        Filter filter = node.getGraphicsNodeRable(true);

        AffineTransform at = new AffineTransform();

        if (toBBoxNeeded){
            // 'primitiveUnits' attribute - default is userSpaceOnUse
            short coordSystemType;
            Element filterDefElement = (Element)(filterElement.getParentNode());
            String s = SVGUtilities.getChainableAttributeNS
                (filterDefElement, null, SVG_PRIMITIVE_UNITS_ATTRIBUTE, ctx);
            if (s.length() == 0) {
                coordSystemType = SVGUtilities.USER_SPACE_ON_USE;
            } else {
                coordSystemType = SVGUtilities.parseCoordinateSystem
                    (filterDefElement, SVG_PRIMITIVE_UNITS_ATTRIBUTE, s);
            }
            
            if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
                at = SVGUtilities.toObjectBBox(at, filteredNode);
            }

            Rectangle2D bounds = filteredNode.getGeometryBounds();
            at.preConcatenate(AffineTransform.getTranslateInstance
                              (primitiveRegion.getX() - bounds.getX(), 
                               primitiveRegion.getY() - bounds.getY()));
            
        } else {
            
            // Need to translate the image to the x, y coordinate to
            // have the same behavior as the <use> element
            at.translate(primitiveRegion.getX(), primitiveRegion.getY());
        }

        return new AffineRable8Bit(filter, at);
    }

    /**
     * Returns a Filter that represents an raster image (JPG or PNG).
     *
     * @param ctx the bridge context
     * @param primitiveRegion the primitive region
     * @param url the url of the image
     */
    protected static Filter createRasterFeImage(BridgeContext ctx,
                                                Rectangle2D   primitiveRegion,
                                                ParsedURL     purl) {

        // Need to fit the raster image to the filter region so that
        // we have the same behavior as raster images in the <image> element.
        Filter filter = ImageTagRegistry.getRegistry().readURL(purl);

        Rectangle2D bounds = filter.getBounds2D();
        AffineTransform scale = new AffineTransform();
        scale.translate(primitiveRegion.getX(), primitiveRegion.getY());
        scale.scale(primitiveRegion.getWidth()/(bounds.getWidth()-1),
                    primitiveRegion.getHeight()/(bounds.getHeight()-1));
        scale.translate(-bounds.getX(), -bounds.getY());

        return new AffineRable8Bit(filter, scale);
    }
}
