/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;clipPath> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGClipPathElementBridge extends AbstractSVGBridge
    implements ClipBridge {

    /**
     * Constructs a new bridge for the &lt;clipPath> element.
     */
    public SVGClipPathElementBridge() {}

    /**
     * Returns 'clipPath'.
     */
    public String getLocalName() {
        return SVG_CLIP_PATH_TAG;
    }

    /**
     * Creates a <tt>Clip</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param clipElement the element that defines the clip
     * @param clipedElement the element that references the clip element
     * @param clipedNode the graphics node to clip
     */
    public ClipRable createClip(BridgeContext ctx,
                                Element clipElement,
                                Element clipedElement,
                                GraphicsNode clipedNode) {

        String s;

        // 'transform' attribute
        AffineTransform Tx;
        s = clipElement.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            Tx = SVGUtilities.convertTransform
                (clipElement, SVG_TRANSFORM_ATTRIBUTE, s);
        } else {
            Tx = new AffineTransform();
        }

        // 'clipPathUnits' attribute - default is userSpaceOnUse
        short coordSystemType;
        s = clipElement.getAttributeNS(null, SVG_CLIP_PATH_UNITS_ATTRIBUTE);
        if (s.length() == 0) {
            coordSystemType = SVGUtilities.USER_SPACE_ON_USE;
        } else {
            coordSystemType = SVGUtilities.parseCoordinateSystem
                (clipElement, SVG_CLIP_PATH_UNITS_ATTRIBUTE, s);
        }
        // additional transform to move to objectBoundingBox coordinate system
        GraphicsNodeRenderContext rc = ctx.getGraphicsNodeRenderContext();
        if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            Tx = SVGUtilities.toObjectBBox(Tx, clipedNode, rc);
        }

        // Build the GVT tree that represents the clip path
        //
        // The silhouettes of the child elements are logically OR'd
        // together to create a single silhouette which is then used to
        // restrict the region onto which paint can be applied.
        //
        // The 'clipPath' element or any of its children can specify
        // property 'clip-path'.
        //
        Area clipPath = new Area();
        GVTBuilder builder = ctx.getGVTBuilder();
        boolean hasChildren = false;
        for(Node node = clipElement.getFirstChild();
            node != null;
            node = node.getNextSibling()) {

            // check if the node is a valid Element
            if (node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }

            Element child = (Element)node;
            GraphicsNode clipNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (clipNode == null) {
                continue;
            }
            hasChildren = true;

            // compute the outline of the current clipPath's child
            int wr = CSSUtilities.convertClipRule(child);
            GeneralPath path = new GeneralPath(clipNode.getOutline(rc));
            path.setWindingRule(wr);
            Shape outline = Tx.createTransformedShape(path);

            // apply the 'clip-path' of the current clipPath's child
            ShapeNode outlineNode = new ShapeNode();
            outlineNode.setShape(outline);
            ClipRable clip = CSSUtilities.convertClipPath(child,
                                                          outlineNode,
                                                          ctx);
            if (clip != null) {
                Area area = new Area(outline);
                area.subtract(new Area(clip.getClipPath()));
                outline = area;
            }
            clipPath.add(new Area(outline));
        }
        if (!hasChildren) {
            return null; // empty clipPath
        }

        // construct the shape node that represents the clipPath
        ShapeNode clipPathNode = new ShapeNode();
        clipPathNode.setShape(clipPath);

        // apply the 'clip-path' of the clipPath element (already in user space)
        ClipRable clipElementClipPath =
            CSSUtilities.convertClipPath(clipElement, clipPathNode, ctx);
        if (clipElementClipPath != null) {
            clipPath.subtract(new Area(clipElementClipPath.getClipPath()));
        }

        Filter filter = clipedNode.getFilter();
        if (filter == null) {
            // Make the initial source as a RenderableImage
            filter = new GraphicsNodeRable8Bit(clipedNode, rc);
        }
        return new ClipRable8Bit(filter, clipPath);
    }

    /**
     * Performs an update according to the specified event.
     *
     * @param evt the event describing the update to perform
     */
    public void update(BridgeMutationEvent evt) {
        throw new Error("Not implemented");
    }
}
