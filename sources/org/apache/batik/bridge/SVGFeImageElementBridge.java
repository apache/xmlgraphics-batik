/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Map;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.XMLConstants;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
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

        //
        // According the the SVG specification, feImage behaves like
        // <image> if it references an SVG document or a raster image
        // and it behaves like a <use> if it references a document
        // fragment.
        //
        // To provide this behavior, depending on whether the uri 
        // contains a fragment identifier, we create either an 
        // <image> or a <use> element and request the corresponding
        // bridges to build the corresponding GraphicsNode for us.
        // 
        // Then, we take care of the possible transformation needed 
        // from objectBoundingBox space to user space.
        //
        
        GraphicsNode gn = null;
        Document document = filterElement.getOwnerDocument();
        boolean isUse = (uriStr.indexOf("#") != -1);
        Element contentElement = null;
        if (isUse) {
            contentElement = document.createElementNS(SVG_NAMESPACE_URI,
                                                    SVG_USE_TAG);
        } else {
            contentElement = document.createElementNS(SVG_NAMESPACE_URI,
                                                    SVG_IMAGE_TAG);
        }

        
        contentElement.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, XMLConstants.XLINK_PREFIX + 
                                    ":" + SVG_HREF_ATTRIBUTE,
                                    uriStr);

        Element proxyElement = document.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_G_TAG);
        proxyElement.appendChild(contentElement);

        // feImage's default region is that of the filter chain.
        Rectangle2D defaultRegion = filterRegion;

        // Compute the transform from object bounding box to user
        // space if needed.
        AffineTransform at = new AffineTransform();

        // 'primitiveUnits' attribute - default is userSpaceOnUse
        short coordSystemType;
        Element filterDefElement = (Element)(filterElement.getParentNode());
        boolean isBBox = false;
        String s = SVGUtilities.getChainableAttributeNS
            (filterDefElement, null, SVG_PRIMITIVE_UNITS_ATTRIBUTE, ctx);
        if (s.length() == 0) {
            coordSystemType = SVGUtilities.USER_SPACE_ON_USE;
        } else {
                coordSystemType = SVGUtilities.parseCoordinateSystem
                    (filterDefElement, SVG_PRIMITIVE_UNITS_ATTRIBUTE, s);
        }
        
        if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            isBBox = true;
            at = SVGUtilities.toObjectBBox(at, filteredNode);
        }
        
        // get filter primitive chain region
        Rectangle2D primitiveRegionUserSpace
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);
        Rectangle2D primitiveRegion = primitiveRegionUserSpace;

        if (isBBox) {
            try {
                AffineTransform ati = at.createInverse();
                primitiveRegion = ati.createTransformedShape(primitiveRegion).getBounds2D();
            } catch (NoninvertibleTransformException nite) {
                // Should never happen, seem above
                throw new Error();
            }
        }

        contentElement.setAttributeNS(null, SVG_X_ATTRIBUTE, "" + primitiveRegion.getX());
        contentElement.setAttributeNS(null, SVG_Y_ATTRIBUTE, "" + primitiveRegion.getY());
        contentElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE, "" + primitiveRegion.getWidth());
        contentElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE, "" + primitiveRegion.getHeight());
        
        // System.err.println(">>>>>>>>>>>> primitiveRegion : " + primitiveRegion);
        // System.err.println(">>>>>>>>>>>> at              : " + at);

        GraphicsNode node = ctx.getGVTBuilder().build(ctx, proxyElement);
        Filter filter = node.getGraphicsNodeRable(true);
        
        filter = new AffineRable8Bit(filter, at);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        filter = new PadRable8Bit(filter, primitiveRegionUserSpace, PadMode.ZERO_PAD);

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
