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
import java.io.StringReader;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.util.SVGConstants;
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
                            Element clippedElement) {

        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(clipElement);

        // Build the GVT tree that represents the clip path
        //
        // The silhouettes of the child elements are logically OR'd
        // together to create a single silhouette which is then used to
        // restrict the region onto which paint can be applied.
        //
        // The 'clipPath' element or any of its children can specify
        // property 'clip-path'.
        //
        GraphicsNodeRenderContext rc = ctx.getGraphicsNodeRenderContext();
        Area clipPath = new Area();
        GVTBuilder builder = ctx.getGVTBuilder();

        // parse the transform attribute
        String transformStr = clipElement.getAttributeNS(null, ATTR_TRANSFORM);
        AffineTransform Tx;
        if (transformStr.length() > 0) {
            Tx = SVGUtilities.convertAffineTransform(transformStr);
        } else {
            Tx = new AffineTransform();
        }

        // parse the clipPathUnits attribute
        String units = clipElement.getAttributeNS(null, SVG_CLIP_PATH_UNITS_ATTRIBUTE);
        if (units.length() == 0) {
            units = SVG_USER_SPACE_ON_USE_VALUE;
        }
        int unitsType;
        try {
            unitsType = SVGUtilities.parseCoordinateSystem(units);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("clipPath.units.invalid",
                                       new Object[] {units,
                                                     SVG_CLIP_PATH_UNITS_ATTRIBUTE}));
        }
        // compute an additional transform related the clipPathUnits
        Tx = SVGUtilities.convertAffineTransform(Tx, gn, rc, unitsType);
        // build the clipPath according to the clipPath's children
        boolean hasChildren = false;
        for(Node node=clipElement.getFirstChild();
                node != null;
                node = node.getNextSibling()){

            // check if the node is a valid Element
            if (node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element)node;
            GraphicsNode clipNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (clipNode == null) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("clipPath.subelement.illegal",
                                        new Object[] {node.getLocalName()}));
            }
            hasChildren = true;
            // compute the outline of the current Element
            CSSStyleDeclaration c = CSSUtilities.getComputedStyle(child);
            CSSPrimitiveValue v = (CSSPrimitiveValue)c.getPropertyCSSValue
                (CSS_CLIP_RULE_PROPERTY);
            int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
                ? GeneralPath.WIND_NON_ZERO
                : GeneralPath.WIND_EVEN_ODD;
            GeneralPath path = new GeneralPath(clipNode.getOutline(rc));
            path.setWindingRule(wr);
            Shape outline = Tx.createTransformedShape(path);

            // apply the clip-path of the current Element
            ShapeNode outlineNode = new ShapeNode();
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
        if (!hasChildren) {
            return null; // no clipPath defined
        }

        // apply the clip-path of this clipPath Element (already in user space)
        ShapeNode clipPathNode = new ShapeNode();
        clipPathNode.setShape(clipPath);
        Clip clipElementClipPath =
            CSSUtilities.convertClipPath(clipElement, clipPathNode, ctx);
        if (clipElementClipPath != null) {
            clipPath.subtract(new Area(clipElementClipPath.getClipPath()));
        }

        // OTHER PROBLEM: SHOULD TAKE MASK REGION INTO ACCOUNT
        Filter filter = gn.getFilter();
        if (filter == null) {
              // Make the initial source as a RenderableImage
            GraphicsNodeRableFactory gnrFactory
                = ctx.getGraphicsNodeRableFactory();
            filter = gnrFactory.createGraphicsNodeRable(gn, rc);
        }
        return new ClipRable8Bit(filter, clipPath);

    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
