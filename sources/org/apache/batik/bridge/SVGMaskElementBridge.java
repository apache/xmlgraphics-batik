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
            if(node.getNodeType() != Node.ELEMENT_NODE) {
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
