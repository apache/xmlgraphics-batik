/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.Filter;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;pattern> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPatternElementBridge implements PaintBridge,
                                                SVGConstants,
                                                ErrorConstants {

    /**
     * Constructs a new SVGPatternElementBridge.
     */
    public SVGPatternElementBridge() {}

    /**
     * Creates a <tt>Paint</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param patternElement the pattern element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param opacity the opacity of the Paint to create
     */
    public Paint createPaint(BridgeContext ctx,
                             Element patternElement,
                             Element paintedElement,
                             GraphicsNode paintedNode,
                             float opacity) {


        // extract pattern content
        CompositeGraphicsNode patternContentNode
            = extractPatternContent(patternElement, ctx);
        if (patternContentNode == null) {
            return null; // no content means no paint
        }

        // get pattern region using 'patternUnits'
        Rectangle2D patternRegion = SVGUtilities.convertPatternRegion
            (patternElement, paintedElement, paintedNode, ctx);

        String s;

        // 'patternTransform' attribute - default is an Identity matrix
        AffineTransform patternTransform;
        s = SVGUtilities.getChainableAttributeNS
            (patternElement, null, SVG_PATTERN_TRANSFORM_ATTRIBUTE, ctx);
        if (s.length() != 0) {
            patternTransform = SVGUtilities.convertTransform
                (patternElement, SVG_PATTERN_TRANSFORM_ATTRIBUTE, s);
        } else {
            patternTransform = new AffineTransform();
        }

        // 'overflow' on the painted element
        boolean overflowIsHidden = CSSUtilities.convertOverflow(patternElement);

        // 'clip' on the painted element
        float[] clip = CSSUtilities.convertClip(patternElement);

        // 'patternContentUnits' - default is userSpaceOnUse
        short contentCoordSystem;
        s = SVGUtilities.getChainableAttributeNS
            (patternElement, null, SVG_PATTERN_CONTENT_UNITS_ATTRIBUTE, ctx);
        if (s.length() == 0) {
            contentCoordSystem = SVGUtilities.USER_SPACE_ON_USE;
        } else {
            contentCoordSystem = SVGUtilities.parseCoordinateSystem
                (patternElement, SVG_PATTERN_CONTENT_UNITS_ATTRIBUTE, s);
        }

        // Compute a transform according to viewBox,  preserveAspectRatio
        // and patternContentUnits
        GraphicsNodeRenderContext rc = ctx.getGraphicsNodeRenderContext();
        AffineTransform patternContentTransform;

        // 'viewBox' attribute
        String viewBoxStr = SVGUtilities.getChainableAttributeNS
            (patternElement, null, SVG_VIEW_BOX_ATTRIBUTE, ctx);

        if (viewBoxStr.length() == 0) {
            // No viewBox has been found, handle 'patternContentUnits'
            if (contentCoordSystem == SVGUtilities.OBJECT_BOUNDING_BOX) {
                // objectBoundingBox: a transform for paintedNode bounds
                Rectangle2D r = paintedNode.getGeometryBounds(rc);
                patternContentTransform = new AffineTransform();
                patternContentTransform.translate(r.getX(), r.getY());
                patternContentTransform.scale(r.getWidth(), r.getHeight());
            } else {
                // userSpaceOnUse: no additional transform required
                patternContentTransform = null;
            }
        } else {
            // A viewBox has been found, handle 'preserveAspectRatio'
            String aspectRatioStr = SVGUtilities.getChainableAttributeNS
               (patternElement, null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE, ctx);
            float w = (float)patternRegion.getWidth();
            float h = (float)patternRegion.getHeight();
            patternContentTransform = ViewBox.getPreserveAspectRatioTransform
                (patternElement, viewBoxStr, aspectRatioStr, w, h);

            // <!> FIXME: START
            // - Why do we clip using viewBox region?
            // - Why do we ignore the objectBoundingBox transform?

            // clip the patternContentNode according to the 'overflow' property
            if (overflowIsHidden) {
                float [] vb
                    = ViewBox.parseViewBoxAttribute(patternElement, viewBoxStr);
                Rectangle2D viewBox
                    = new Rectangle2D.Float(vb[0], vb[1], vb[2], vb[3]);

                CompositeGraphicsNode comp = new CompositeGraphicsNode();
                comp.getChildren().add(patternContentNode);

                Filter filter = new GraphicsNodeRable8Bit(comp, rc);
                comp.setClip(new ClipRable8Bit(filter, viewBox));

                patternContentNode = comp;
            }
            // <!> FIXME: END
        }

        // take the opacity into account. opacity is implemented by a Filter
        if (opacity != 1) {
            float[][] matrix = { {1, 0, 0, 0, 0},
                                 {0, 1, 0, 0, 0},
                                 {0, 0, 1, 0, 0},
                                 {0, 0, 0, opacity, 0} };

            // <!> FIXME: Why do we need to create an intermediate node?
            ColorMatrixRable filter = ColorMatrixRable8Bit.buildMatrix(matrix);
            Filter contentRable
                = new GraphicsNodeRable8Bit(patternContentNode, rc);
            filter.setSource(contentRable);
            patternContentNode.setFilter(filter);
            CompositeGraphicsNode comp = new CompositeGraphicsNode();
            comp.getChildren().add(patternContentNode);
            patternContentNode = comp;
        }

        return new PatternPaint(patternContentNode,
                                rc,
                                patternContentTransform,
                                patternRegion,
                                !overflowIsHidden,
                                patternTransform);

    }

    /**
     * Returns the content of the specified pattern element. The
     * content of the pattern can be specified as children of the
     * patternElement or children of one of its 'ancestor' (linked with
     * the xlink:href attribute).
     *
     * @param patternElement the gradient element
     * @param ctx the bridge context to use
     */
    protected static
        CompositeGraphicsNode extractPatternContent(Element patternElement,
                                                    BridgeContext ctx) {

        List refs = new LinkedList();
        for (;;) {
            CompositeGraphicsNode content
                = extractLocalPatternContent(patternElement, ctx);
            if (content != null) {
                return content; // pattern content found, exit
            }
            String uri = XLinkSupport.getXLinkHref(patternElement);
            if (uri.length() == 0) {
                return null; // no xlink:href found, exit
            }
            // check if there is circular dependencies
            SVGOMDocument doc =
                (SVGOMDocument)patternElement.getOwnerDocument();
            URL url;
            try {
                url = new URL(doc.getURLObject(), uri);
            } catch (MalformedURLException ex) {
                throw new BridgeException(patternElement,
                                          ERR_URI_MALFORMED,
                                          new Object[] {uri});

            }
            if (contains(refs, url)) {
                throw new BridgeException(patternElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.add(url);
            patternElement = ctx.getReferencedElement(patternElement, uri);
        }
    }

    /**
     * Returns the content of the specified pattern element or null if any.
     *
     * @param e the pattern element
     * @param ctx the bridge context
     */
    protected static
        CompositeGraphicsNode extractLocalPatternContent(Element e,
                                                         BridgeContext ctx) {

        GVTBuilder builder = ctx.getGVTBuilder();
        CompositeGraphicsNode content = null;
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            // check if the Node is valid
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            GraphicsNode gn = builder.build(ctx, (Element)n);
            // check if a GraphicsNode has been created
            if (gn != null) {
                // lazy instantation of the list of stop elements
                if (content == null) {
                    content = new CompositeGraphicsNode();
                }
                content.getChildren().add(gn);
            }
        }
        return content;
    }

    /**
     * Returns true if the specified list of URLs contains the specified url.
     *
     * @param urls the list of URLs
     * @param key the url to search for
     */
    private static boolean contains(List urls, URL key) {
        Iterator iter = urls.iterator();
        while (iter.hasNext()) {
            URL url = (URL)iter.next();
            if (url.sameFile(key) && url.getRef().equals(key.getRef())) {
                return true;
            }
        }
        return false;
    }
}

