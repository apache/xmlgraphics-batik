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

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.MaskRable8Bit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;mask> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGMaskElementBridge extends AbstractSVGBridge
    implements MaskBridge {

    /**
     * Constructs a new bridge for the &lt;mask> element.
     */
    public SVGMaskElementBridge() {}

    /**
     * Returns 'mask'.
     */
    public String getLocalName() {
        return SVG_MASK_TAG;
    }

    /**
     * Creates a <tt>Mask</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param maskElement the element that defines the mask
     * @param maskedElement the element that references the mask element
     * @param maskedNode the graphics node to mask
     */
    public Mask createMask(BridgeContext ctx,
                           Element maskElement,
                           Element maskedElement,
                           GraphicsNode maskedNode) {

        String s;

        // get mask region using 'maskUnits'
        Rectangle2D maskRegion = SVGUtilities.convertMaskRegion
            (maskElement, maskedElement, maskedNode, ctx);

        //
        // Build the GVT tree that represents the mask
        //
        GVTBuilder builder = ctx.getGVTBuilder();
        CompositeGraphicsNode maskNode = new CompositeGraphicsNode();
        CompositeGraphicsNode maskNodeContent = new CompositeGraphicsNode();
        maskNode.getChildren().add(maskNodeContent);
        boolean hasChildren = false;
        for(Node node = maskElement.getFirstChild();
            node != null;
            node = node.getNextSibling()){

            // check if the node is a valid Element
            if(node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }

            Element child = (Element)node;
            GraphicsNode gn = builder.build(ctx, child) ;
            if(gn == null) {
                continue;
            }
            hasChildren = true;
            maskNodeContent.getChildren().add(gn);
        }
        if (!hasChildren) {
            return null; // empty mask
        }

        // 'transform' attribute
        AffineTransform Tx;
        s = maskElement.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            Tx = SVGUtilities.convertTransform
                (maskElement, SVG_TRANSFORM_ATTRIBUTE, s);
        } else {
            Tx = new AffineTransform();
        }

        // 'maskContentUnits' attribute - default is userSpaceOnUse
        short coordSystemType;
        s = maskElement.getAttributeNS(null, SVG_MASK_CONTENT_UNITS_ATTRIBUTE);
        if (s.length() == 0) {
            coordSystemType = SVGUtilities.USER_SPACE_ON_USE;
        } else {
            coordSystemType = SVGUtilities.parseCoordinateSystem
                (maskElement, SVG_MASK_CONTENT_UNITS_ATTRIBUTE, s);
        }

        // additional transform to move to objectBoundingBox coordinate system
        if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            Tx = SVGUtilities.toObjectBBox(Tx, maskedNode);
        }

        maskNodeContent.setTransform(Tx);

        Filter filter = maskedNode.getFilter();
        if (filter == null) {
            // Make the initial source as a RenderableImage
            filter = maskedNode.getGraphicsNodeRable(true);
        }

        return new MaskRable8Bit(filter, maskNode, maskRegion);
    }
}
