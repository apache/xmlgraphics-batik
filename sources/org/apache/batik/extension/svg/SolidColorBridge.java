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

package org.apache.batik.extension.svg;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSValue;

/**
 * Bridge class for a regular polygon element.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 */
public class SolidColorBridge 
    extends AbstractSVGBridge
    implements PaintBridge, BatikExtConstants, CSSConstants, ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public SolidColorBridge() { /* nothing */ }

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return BATIK_EXT_SOLID_COLOR_TAG;
    }

    /**
     * Creates a <tt>Paint</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param paintElement the element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param opacity the opacity of the Paint to create
     */
    public Paint createPaint(BridgeContext ctx,
                             Element paintElement,
                             Element paintedElement,
                             GraphicsNode paintedNode,
                             float opacity) {

        opacity = extractOpacity(paintElement, opacity, ctx);

        return extractColor(paintElement, opacity, ctx);
    }

    protected static float extractOpacity(Element paintElement, 
                                          float opacity,
                                          BridgeContext ctx) {
        Map refs = new HashMap();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex(BATIK_EXT_SOLID_OPACITY_PROPERTY);

        for (;;) {
            Value opacityVal =
                CSSUtilities.getComputedStyle(paintElement, pidx);
        
            // Was solid-opacity explicity set on this element?
            StyleMap sm =
                ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                // It was explicit...
                float attr = PaintServer.convertOpacity(opacityVal);
                return (opacity * attr);
            }

            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return opacity; // no xlink:href found, exit
            }

            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);

            // check if there is circular dependencies
            if (refs.containsKey(purl)) {
                throw new BridgeException(paintElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.put(purl, purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }

    protected static Color extractColor(Element paintElement, 
                                        float opacity,
                                        BridgeContext ctx) {
        Map refs = new HashMap();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex(BATIK_EXT_SOLID_COLOR_PROPERTY);

        for (;;) {
            Value colorDef =
                CSSUtilities.getComputedStyle(paintElement, pidx);
        
            // Was solid-color explicity set on this element?
            StyleMap sm =
                ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                // It was explicit...
                if (colorDef.getCssValueType() ==
                    CSSValue.CSS_PRIMITIVE_VALUE) {
                    return PaintServer.convertColor(colorDef, opacity);
                } else {
                    return PaintServer.convertRGBICCColor
                        (paintElement, colorDef.item(0),
                         (ICCColor)colorDef.item(1),
                         opacity, ctx);
                }
            }


            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                // no xlink:href found, exit    
                return new Color(0, 0, 0, opacity); 
            }

            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);

            // check if there is circular dependencies
            if (refs.containsKey(purl)) {
                throw new BridgeException
                    (paintElement,
                     ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                     new Object[] {uri});
            }
            refs.put(purl, purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
}
