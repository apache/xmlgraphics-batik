/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGImageElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;multiImage> element.
 *
 * The 'multiImage' element is similar to the 'image' element (supports
 * all the same attributes and properties) except.
 * <ol>
 *    <li>It can only be used to reference raster content (this is an
 *        implementation thing really)</li>
 *    <li>It has two addtional attributes: 'pixel-width' and
 *        'pixel-height' which are the maximum width and height of the
 *        image referenced by the xlink:href attribute.</li>
 *    <li>It can contain a child element 'subImage' which has only
 *        three attributes, pixel-width, pixel-height and xlink:href.
 *        The image displayed is the smallest image such that
 *        pixel-width and pixel-height are greater than or equal to the
 *        required image size for display.</li>
 * </ol>
 *
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class BatikMultiImageElementBridge extends SVGImageElementBridge
    implements BatikExtConstants {

    BatikMultiImageElementBridge() { }

    /**
     * Returns the Batik Extension namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'multiImage'.
     */
    public String getLocalName() {
        return BATIK_EXT_MULTI_IMAGE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new BatikMultiImageElementBridge();
    }

     /**
      * Creates a graphics node using the specified BridgeContext and for the
      * specified element.
      *  
      * @param  ctx the bridge context to use
      * @param  e   the element that describes the graphics node to build
      * @return a graphics node that represents the specified element
      */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        ImageNode imgNode = (ImageNode)instantiateGraphicsNode();
        if (imgNode == null) {
            return null;
        }

        Rectangle2D b = getImageBounds(ctx, e);

        List uris   = new LinkedList();
        List minDim = new LinkedList();
        List maxDim = new LinkedList();

        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element se = (Element)n;
            if (!(se.getNamespaceURI().equals(BATIK_EXT_NAMESPACE_URI)) ||
                !(se.getLocalName().equals(BATIK_EXT_SUB_IMAGE_REF_TAG)))
                continue;

            addInfo(se, uris, minDim, maxDim, b);
        }

        Dimension [] mindary = new Dimension[uris.size()];
        Dimension [] maxdary = new Dimension[uris.size()];
        ParsedURL [] uary = new ParsedURL[uris.size()];
        Iterator mindi = minDim.iterator();
        Iterator maxdi = maxDim.iterator();
        Iterator ui = uris.iterator();
        int n=0;
        while (mindi.hasNext()) {
            Dimension minD = (Dimension)mindi.next();
            Dimension maxD = (Dimension)maxdi.next();
            int i =0;
            if (minD != null) {
                for (; i<n; i++) {
                    if ((mindary[i] != null) &&
                        (minD.width < mindary[i].width)) {
                        break;
                    }
                }
            }
            for (int j=n; j>i; j--) {
                uary[j]    = uary[j-1];
                mindary[j] = mindary[j-1];
                maxdary[j] = maxdary[j-1];
            }
            
            uary   [i] = (ParsedURL)ui.next();
            mindary[i] = minD;
            maxdary[i] = maxD;
            n++;
        }

        // System.out.println("Bounds: " + bounds);
        // System.out.println("ImgB: " + imgBounds);
        

        GraphicsNode node = new MultiResGraphicsNode(e, b, uary, 
                                                     mindary, maxdary);

        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            node.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        // 'visibility'
        imgNode.setVisible(CSSUtilities.convertVisibility(e));

        imgNode.setImage(node);

        return imgNode;
    }

    /**
     * Returns false as shapes are not a container.
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        this.e = e;
        this.node = node;
        this.ctx = ctx;
        // HACK due to the way images are represented in GVT
        ImageNode imgNode = (ImageNode)node;
        ctx.bind(e, imgNode.getImage());
        ((SVGOMElement)e).setSVGContext(this);
    }

    protected void addInfo(Element e, Collection uris, 
                           Collection minDim, Collection maxDim,
                           Rectangle2D bounds) {
        String uriStr = XLinkSupport.getXLinkHref(e);
        if (uriStr.length() == 0) {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {"xlink:href"});
        }

        String baseURI = XMLBaseSupport.getCascadedXMLBase(e);
        ParsedURL purl;
        if (baseURI == null) purl = new ParsedURL(uriStr);
        else                 purl = new ParsedURL(baseURI, uriStr);
        uris.add(purl);

        
        minDim.add(getElementMinPixel(e, bounds));
        maxDim.add(getElementMaxPixel(e, bounds));
    }

    protected Dimension getElementMinPixel(Element e, Rectangle2D bounds) {
        return getElementPixelSize
            (e, BATIK_EXT_MAX_PIXEL_SIZE_ATTRIBUTE, bounds);
    }
    protected Dimension getElementMaxPixel(Element e, Rectangle2D bounds) {
        return getElementPixelSize
            (e, BATIK_EXT_MIN_PIXEL_SIZE_ATTRIBUTE, bounds);
    }

    protected Dimension getElementPixelSize(Element e, 
                                            String attr,
                                            Rectangle2D bounds) {
        String s;
        s = e.getAttribute(attr);
        if (s.length() == 0) return null;

        Float [] vals = SVGUtilities.convertSVGNumberOptionalNumber
            (e, attr, s);

        if (vals[0] == null) return null;

        float xPixSz = vals[0].floatValue();
        float yPixSz = xPixSz;
        if (vals[1] != null)
            yPixSz = vals[1].floatValue();
        
        return new Dimension((int)(bounds.getWidth()/xPixSz+0.5), 
                             (int)(bounds.getHeight()/yPixSz+0.5)); 
    }
}
