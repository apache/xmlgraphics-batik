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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;rect> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGRectElementBridge extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public SVGRectElementBridge() {}

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return SVG_RECT_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGRectElementBridge();
    }

    /**
     * Constructs a rectangle according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // 'x' attribute - default is 0
        s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y_ATTRIBUTE, uctx);
        }

        // 'width' attribute - required
        s = e.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        float w;
        if (s.length() != 0) {
            w = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_WIDTH_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_WIDTH_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (w == 0) {
            shapeNode.setShape(null);
	    return;
	}

        // 'height' attribute - required
        s = e.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        float h;
        if (s.length() != 0) {
            h = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_HEIGHT_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_HEIGHT_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (h == 0) {
            shapeNode.setShape(null);
	    return;
	}

        // 'rx' attribute - default is 0
        s = e.getAttributeNS(null, SVG_RX_ATTRIBUTE);
        boolean rxs = (s.length() != 0);
        float rx = 0;
        if (rxs) {
            rx = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_RX_ATTRIBUTE, uctx);
        }
        rx = (rx > w / 2) ? w / 2 : rx;

        // 'ry' attribute - default is 0
        s = e.getAttributeNS(null, SVG_RY_ATTRIBUTE);
        boolean rys = (s.length() != 0);
        float ry = 0;
        if (rys) {
            ry = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_RY_ATTRIBUTE, uctx);
        }
        ry = (ry > h / 2) ? h / 2 : ry;

        Shape shape = null;
        if (rxs && rys) {
            if (rx == 0 || ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, ry*2);
            }
        } else if (rxs) {
            if (rx == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, rx*2);
            }
        } else if (rys) {
            if (ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, ry*2, ry*2);
            }
        } else {
            shape = new Rectangle2D.Float(x, y, w, h);
        }
        shapeNode.setShape(shape);
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_X_ATTRIBUTE) ||
            attrName.equals(SVG_Y_ATTRIBUTE) ||
            attrName.equals(SVG_WIDTH_ATTRIBUTE) ||
            attrName.equals(SVG_HEIGHT_ATTRIBUTE) ||
            attrName.equals(SVG_RX_ATTRIBUTE) ||
            attrName.equals(SVG_RY_ATTRIBUTE)) {

            buildShape(ctx, e, (ShapeNode)node);
            handleGeometryChanged();
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
