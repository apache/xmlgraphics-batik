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
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;


import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGImageElementBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;

import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.CompositeGraphicsNode;

import org.apache.batik.util.ParsedURL;

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

        ImageNode imgNode = (ImageNode)super.createGraphicsNode(ctx, e);
        if (imgNode == null) {
            return null;
        }

        List dims = new LinkedList();
        List uris = new LinkedList();
        addInfo(e, dims, uris);

        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element se = (Element)n;
            if (!(se.getNamespaceURI().equals(BATIK_EXT_NAMESPACE_URI)) ||
                !(se.getLocalName().equals(BATIK_EXT_SUB_IMAGE_TAG)))
                continue;

            addInfo(se, dims, uris);
        }

        Dimension [] dary = new Dimension[uris.size()];
        ParsedURL [] uary = new ParsedURL[uris.size()];
        Iterator di = dims.iterator();
        Iterator ui = uris.iterator();
        int n=0;
        while (di.hasNext()) {
            int i;
            Dimension d = (Dimension)di.next();
            for (i=0; i<n; i++) {
                if (d.width > dary[i].width) break;
            }
            for (int j=n; j>i; j--) {
                dary[j] = dary[j-1];
                uary[j] = uary[j-1];
            }
            dary[i] = d;
            uary[i] = (ParsedURL)ui.next();
            n++;
        }

        Rectangle2D b = getImageBounds(ctx, e);

        GraphicsNode node = new MultiResGraphicsNode(e, b, uary, dary);

        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            node.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        // 'visibility'
        node.setVisible(CSSUtilities.convertVisibility(e));

        imgNode.setImage(node);

        return imgNode;
    }

    protected void addInfo(Element e, Collection dims, Collection uris) {
        Dimension d = getElementPixelSize(e);
        String uriStr = XLinkSupport.getXLinkHref(e);
        if (uriStr.length() == 0) {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {"xlink:href"});
        }

        String baseURI = XMLBaseSupport.getCascadedXMLBase(e);
        ParsedURL purl;
        if (baseURI == null) purl = new ParsedURL(uriStr);
        else                 purl = new ParsedURL(baseURI, uriStr);

        dims.add(d);
        uris.add(purl);
    }

    protected Dimension getElementPixelSize(Element e) {
        int w=0, h=0;
        String s;

        s = e.getAttributeNS(null,BATIK_EXT_PIXEL_WIDTH_ATTRIBUTE);
        if (s.length() == 0) throw new BridgeException
            (e, ERR_ATTRIBUTE_MISSING,
             new Object[] {BATIK_EXT_PIXEL_WIDTH_ATTRIBUTE});

        try {
            w = (int)SVGUtilities.convertSVGNumber(s);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {BATIK_EXT_PIXEL_WIDTH_ATTRIBUTE, s});
        }

        s = e.getAttributeNS(null,BATIK_EXT_PIXEL_HEIGHT_ATTRIBUTE);
        if (s.length() == 0) throw new BridgeException
            (e, ERR_ATTRIBUTE_MISSING,
             new Object[] {BATIK_EXT_PIXEL_HEIGHT_ATTRIBUTE});
        try {
            h = (int)SVGUtilities.convertSVGNumber(s);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {BATIK_EXT_PIXEL_HEIGHT_ATTRIBUTE, s});
        }

        return new Dimension(w, h);
    }
}
