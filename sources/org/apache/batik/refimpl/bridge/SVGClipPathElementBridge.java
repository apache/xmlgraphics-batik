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

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.refimpl.gvt.AffineTransformSourceBoundingBox;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.util.awt.geom.AffineTransformSource;
import org.apache.batik.util.awt.geom.CompositeAffineTransformSource;
import org.apache.batik.util.awt.geom.DefaultAffineTransformSource;
import org.apache.batik.util.awt.geom.TransformedShape;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;

// For ClipSource
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

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
    public Clip createClip(BridgeContext bridgeContext,
                            GraphicsNode gn,
                            Element clipElement,
                            Element clipedElement) {
        CSSStyleDeclaration decl
            = bridgeContext.getViewCSS().getComputedStyle(clipElement, null);

        // Build the GVT tree that represents the clip path
        //
        // The silhouettes of the child elements are logically OR'd
        // together to create a single silhouette which is then used to
        // restrict the region onto which paint can be applied.
        //
        // The 'clipPath' element or any of its children can specify
        // property 'clip-path'.
        //
        ClipSource area = new ClipSource();
        GVTBuilder builder = bridgeContext.getGVTBuilder();
        Viewport oldViewport = bridgeContext.getCurrentViewport();
        bridgeContext.setCurrentViewport(new ObjectBoundingBoxViewport());

        // Compute the transform matrix of this clipPath Element
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(clipElement.getAttributeNS(null, ATTR_TRANSFORM)),
             bridgeContext.getParserFactory());

        String units = clipElement.getAttributeNS(null, ATTR_CLIP_PATH_UNITS);
        if (units.length() == 0) {
            units = VALUE_OBJECT_BOUNDING_BOX;
        }
        AffineTransformSource ats =
            SVGUtilities.convertAffineTransformSource(at, gn, units);

        for(Node child=clipElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                GraphicsNode node
                    = builder.build(bridgeContext, (Element)child) ;
                if(node != null){
                    CSSStyleDeclaration childDecl
                        = bridgeContext.getViewCSS().getComputedStyle((Element)child, null);
                    GeneralPath outline =
                        new GeneralPath(new TransformedShape(node.getOutline(), ats));
                    // set the clip-rule
                    CSSPrimitiveValue v;
                    v = (CSSPrimitiveValue)childDecl.getPropertyCSSValue(CLIP_RULE_PROPERTY);
                    int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
                        ? GeneralPath.WIND_NON_ZERO
                        : GeneralPath.WIND_EVEN_ODD;
                    outline.setWindingRule(wr);

                    ClipSource clipSource = new ClipSource(outline);
                    // compute clip-path on the child
                    ShapeNode outlineNode =
                        bridgeContext.getGVTFactory().createShapeNode();
                    outlineNode.setShape(outline);
                    Clip clip = CSSUtilities.convertClipPath((Element)child,
                                                             outlineNode,
                                                             bridgeContext);
                    if (clip != null) {
                        Shape clipPath = clip.getClipPath();
                        if (clipPath != null) {
                            clipSource.subtract(new ClipSource(clipPath));
                        }
                    }
                    area.add(clipSource);
                }
            }
        }

        //
        // Now clipPath represents the current clip path defined by the
        // children of the clipPath element in user space.
        //
        ClipSource clipPath = area;

        // Get the clip-path property of this clipPath Element in user space
        ShapeNode outlineNode = bridgeContext.getGVTFactory().createShapeNode();
        outlineNode.setShape(clipPath);
        Clip clipElementClipPath =
            CSSUtilities.convertClipPath(clipElement,
                                         outlineNode,
                                         bridgeContext);
        if (clipElementClipPath != null) {
            ClipSource merge = new ClipSource(clipPath);
            merge.subtract(new ClipSource(clipElementClipPath.getClipPath()));
            clipPath = merge;
        }
        bridgeContext.setCurrentViewport(oldViewport); // restore the viewport

        // OTHER PROBLEM: SHOULD TAKE MASK REGION INTO ACCOUNT
        Filter filter = gn.getFilter();
        if (filter == null) {
              // Make the initial source as a RenderableImage
            GraphicsNodeRableFactory gnrFactory
                = bridgeContext.getGraphicsNodeRableFactory();
            filter = gnrFactory.createGraphicsNodeRable(gn);
        }
        return new ConcreteClipRable(filter, clipPath);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}

class ClipSource implements Shape {

    private Shape resolvedArea;
    private List addList = new LinkedList();
    private List subtractList = new LinkedList();
    private Shape source;

    public ClipSource() {
    }

    public ClipSource(Shape shape) {
        this.source = shape;
    }

    public void add(ClipSource area) {
        addList.add(area);
    }

    public void subtract(ClipSource area) {
        subtractList.add(area);
    }

    protected void resolve() {
        if (resolvedArea == null) {
            Area area;
            if (source != null) {
                area = new Area(source);
            } else {
                area = new Area();
            }
            for(Iterator iter = addList.iterator(); iter.hasNext();) {
                ClipSource source = (ClipSource) iter.next();
                area.add(new Area(source));
            }
            for(Iterator iter = subtractList.iterator(); iter.hasNext();) {
                ClipSource source = (ClipSource) iter.next();
                area.subtract(new Area(source));
            }
            addList = null;
            subtractList = null;
            source = null;
            GeneralPath clipPath = new GeneralPath(area);
            resolvedArea = clipPath;
        }
    }

    public Rectangle getBounds() {
        resolve();
        return resolvedArea.getBounds();
    }

    public Rectangle2D getBounds2D() {
        resolve();
        return resolvedArea.getBounds2D();
    }

    public boolean contains(double x, double y) {
        resolve();
        return resolvedArea.contains(x, y);
    }

    public boolean contains(Point2D p) {
        resolve();
        return resolvedArea.contains(p);
    }

    public boolean intersects(double x, double y, double w, double h) {
        resolve();
        return resolvedArea.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r) {
        resolve();
        return resolvedArea.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        resolve();
        return resolvedArea.contains(x, y, w, h);
    }

    public boolean contains(Rectangle2D r) {
        resolve();
        return resolvedArea.contains(r);
    }

    public PathIterator getPathIterator(AffineTransform at) {
        resolve();
        return resolvedArea.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        resolve();
        return resolvedArea.getPathIterator(at, flatness);
    }
}
