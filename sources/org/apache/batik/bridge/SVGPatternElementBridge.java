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

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction;
import org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;pattern> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPatternElementBridge extends AbstractSVGBridge
    implements PaintBridge, ErrorConstants {

    /**
     * Constructs a new SVGPatternElementBridge.
     */
    public SVGPatternElementBridge() {}

    /**
     * Returns 'pattern'.
     */
    public String getLocalName() {
        return SVG_PATTERN_TAG;
    }

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

        // get pattern region using 'patternUnits'. Pattern region is in tile pace.
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

        // 'overflow' on the pattern element
        boolean overflowIsHidden = CSSUtilities.convertOverflow(patternElement);

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
        // and patternContentUnits and the pattern transform attribute.
        //
        // The stack of transforms is:
        //
        // +-------------------------------+
        // | viewPortTranslation           |
        // +-------------------------------+
        // | preserveAspectRatioTransform  |
        // +-------------------------------+
        // + patternContentUnitsTransform  |
        // +-------------------------------+
        //
        // where:
        //   - viewPortTranslation is the transform that translate to
        //     the viewPort's origin.
        //   - preserveAspectRatioTransform is the transformed implied by the
        //     preserveAspectRatio attribute.
        //   - patternContentUnitsTransform is the transform implied by the
        //     patternContentUnits attribute.
        //
        // Note that there is an additional transform from the tiling
        // space to the user space (patternTransform) that is passed
        // separately to the PatternPaintContext.
        //
        AffineTransform patternContentTransform = new AffineTransform();

        //
        // Process viewPortTranslation
        //
        patternContentTransform.translate(patternRegion.getX(),
                                          patternRegion.getY());

        //
        // Process preserveAspectRatioTransform
        //

        // 'viewBox' attribute
        String viewBoxStr = SVGUtilities.getChainableAttributeNS
            (patternElement, null, SVG_VIEW_BOX_ATTRIBUTE, ctx);

        if (viewBoxStr.length() > 0) {
            // There is a viewBox attribute. Then, take
            // preserveAspectRatio into account.
            String aspectRatioStr = SVGUtilities.getChainableAttributeNS
               (patternElement, null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE, ctx);
            float w = (float)patternRegion.getWidth();
            float h = (float)patternRegion.getHeight();
            AffineTransform preserveAspectRatioTransform
                = ViewBox.getPreserveAspectRatioTransform
                (patternElement, viewBoxStr, aspectRatioStr, w, h);

            patternContentTransform.concatenate(preserveAspectRatioTransform);
        } else {
            //
            // Process patternContentUnitsTransform
            //
            if (contentCoordSystem == SVGUtilities.OBJECT_BOUNDING_BOX){
                AffineTransform patternContentUnitsTransform
                    = new AffineTransform();
                Rectangle2D objectBoundingBox = 
                    paintedNode.getGeometryBounds();
                patternContentUnitsTransform.translate
                    (objectBoundingBox.getX(),
                     objectBoundingBox.getY());

                patternContentUnitsTransform.scale
                    (objectBoundingBox.getWidth(),
                     objectBoundingBox.getHeight());

                patternContentTransform.concatenate
                    (patternContentUnitsTransform);
            }
        }

        //
        // Apply transform
        //
        patternContentNode.setTransform(patternContentTransform);

        // take the opacity into account. opacity is implemented by a Filter
        if (opacity != 1) {
            Filter filter = patternContentNode.getGraphicsNodeRable(true);
            filter = new ComponentTransferRable8Bit
                (filter,
                 ConcreteComponentTransferFunction.getLinearTransfer
                 (opacity, 0), //alpha
                 ConcreteComponentTransferFunction.getIdentityTransfer(), //Red
                 ConcreteComponentTransferFunction.getIdentityTransfer(), //Grn
                 ConcreteComponentTransferFunction.getIdentityTransfer());//Blu
            patternContentNode.setFilter(filter);
        }

        return new PatternPaint(patternContentNode,
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
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (!purl.complete())
                throw new BridgeException(patternElement,
                                          ERR_URI_MALFORMED,
                                          new Object[] {uri});

            if (contains(refs, purl)) {
                throw new BridgeException(patternElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.add(purl);
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
     * Returns true if the specified list of ParsedURLs contains the
     * specified url.
     *
     * @param urls the list of ParsedURLs
     * @param key the url to search for */
    private static boolean contains(List urls, ParsedURL key) {
        Iterator iter = urls.iterator();
        while (iter.hasNext()) {
            if (key.equals(iter.next()))
                return true;
        }
        return false;
    }
}

