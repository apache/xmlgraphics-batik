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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import org.apache.batik.css.engine.CSSImportNode;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
        if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            Tx = SVGUtilities.toObjectBBox(Tx, clipedNode);
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
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element child = (Element)node;
            GraphicsNode clipNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (clipNode == null) {
                continue;
            }
            hasChildren = true;

            // if this is a 'use' element, get the actual shape used
            if (child instanceof CSSImportNode) {
                SVGOMCSSImportedElementRoot shadow =
                    (SVGOMCSSImportedElementRoot)
                    ((CSSImportNode) child).getCSSImportedElementRoot();
                
                if (shadow != null) {
                    Node shadowChild = shadow.getFirstChild();
                    if (shadowChild != null
                            && shadowChild.getNodeType() == Node.ELEMENT_NODE) {
                        child = (Element) shadowChild;
                    }
                }
            }

            // compute the outline of the current clipPath's child
            int wr = CSSUtilities.convertClipRule(child);
            GeneralPath path;
            Shape cno = clipNode.getOutline();
            AffineTransform cnt = clipNode.getTransform();
            if (cnt != null) {
                path = new GeneralPath(cnt.createTransformedShape(cno));
            } else {
                path = new GeneralPath(cno);
            }
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
            filter = clipedNode.getGraphicsNodeRable(true);
        }
        return new ClipRable8Bit(filter, clipPath);
    }
}
