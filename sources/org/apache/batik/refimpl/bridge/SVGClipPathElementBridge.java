/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.ClipBridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.ObjectBoundingBoxViewport;
import org.apache.batik.bridge.Viewport;

import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;

/**
 * A factory for the &lt;clipPath&gt; SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGClipPathElementBridge implements ClipBridge, SVGConstants {

    /**
     * Returns the <tt>Shape</tt> referenced by the input element's
     * <tt>clip-path</tt> attribute.
     */
    public Clip createClip(BridgeContext ctx,
                            GraphicsNode gn,
                            Element clipElement,
                            Element clipedElement) {
        CSSStyleDeclaration decl
            = ctx.getViewCSS().getComputedStyle(clipElement, null);

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
        GVTFactory gvtFactory = ctx.getGVTFactory();
        Viewport oldViewport = ctx.getCurrentViewport();

        // compute the transform matrix of this clipPath Element
        AffineTransform Tx = AWTTransformProducer.createAffineTransform
           (new StringReader(clipElement.getAttributeNS(null, ATTR_TRANSFORM)),
            ctx.getParserFactory());
        // compute an additional transform related the clipPathUnits
        String units = clipElement.getAttributeNS(null, ATTR_CLIP_PATH_UNITS);
        if (units.length() == 0) {
            units = VALUE_OBJECT_BOUNDING_BOX;
        }
        if (VALUE_OBJECT_BOUNDING_BOX.equals(units)) {
            // units are resolved using objectBoundingBox
            ctx.setCurrentViewport(new ObjectBoundingBoxViewport());
        }
        Tx = SVGUtilities.convertAffineTransform(Tx, gn, units);

        // build the clipPath according to the clipPath's children
        for(Node node=clipElement.getFirstChild();
                node != null;
                node = node.getNextSibling()){

            Element child = (Element)node;

            // check if the node is a valid Element
            if (node.getNodeType() != node.ELEMENT_NODE) {
                throw new Error("Bad node type "+child.getNodeName());
            }

            GraphicsNode clipNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (clipNode == null) {
                throw new Error("Bad node type "+child.getNodeName());
            }

            // compute the outline of the current Element
            CSSStyleDeclaration c =
                ctx.getViewCSS().getComputedStyle(child, null);
            CSSPrimitiveValue v =
                (CSSPrimitiveValue)c.getPropertyCSSValue(CLIP_RULE_PROPERTY);
            int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
                ? GeneralPath.WIND_NON_ZERO
                : GeneralPath.WIND_EVEN_ODD;
            GeneralPath path = new GeneralPath(clipNode.getOutline());
            path.setWindingRule(wr);
            Shape outline = Tx.createTransformedShape(path);

            // apply the clip-path of the current Element
            ShapeNode outlineNode = gvtFactory.createShapeNode();
            outlineNode.setShape(outline);
            Clip clip = CSSUtilities.convertClipPath(child,
                                                     outlineNode,
                                                     ctx);
            if (clip != null) {
                Area area = new Area(outline);
                area.subtract(new Area(clip.getClipPath()));
                outline = area;
            }
            clipPath.add(new Area(outline));
        }

        // apply the clip-path of this clipPath Element (already in user space)
        ShapeNode clipPathNode = gvtFactory.createShapeNode();
        clipPathNode.setShape(clipPath);
        Clip clipElementClipPath =
            CSSUtilities.convertClipPath(clipElement,
                                         clipPathNode,
                                         ctx);
        if (clipElementClipPath != null) {
            clipPath.subtract(new Area(clipElementClipPath.getClipPath()));
        }

        // restore the viewport
        ctx.setCurrentViewport(oldViewport);

        // OTHER PROBLEM: SHOULD TAKE MASK REGION INTO ACCOUNT
        Filter filter = gn.getFilter();
        if (filter == null) {
              // Make the initial source as a RenderableImage
            GraphicsNodeRableFactory gnrFactory
                = ctx.getGraphicsNodeRableFactory();
            filter = gnrFactory.createGraphicsNodeRable(gn);
        }
        return new ConcreteClipRable(filter, clipPath);

    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
