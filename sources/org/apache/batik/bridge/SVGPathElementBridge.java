/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.io.StringReader;

import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;

import org.w3c.dom.Element;

import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;path> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPathElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;path> element.
     */
    public SVGPathElementBridge() {}

    /**
     * Returns 'path'.
     */
    public String getLocalName() {
        return SVG_PATH_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGPathElementBridge();
    }

    /**
     * Constructs a path according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {


        String s = e.getAttributeNS(null, SVG_D_ATTRIBUTE);
        if (s.length() != 0) {
            AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            try {
                PathParser pathParser = new PathParser();
                pathParser.setPathHandler(app);
                pathParser.parse(s);
            } catch (ParseException ex) {
                BridgeException bex
                    = new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                          new Object[] {SVG_D_ATTRIBUTE});
                bex.setGraphicsNode(shapeNode);
                throw bex;
            } finally {
                shapeNode.setShape(app.getShape());
            }
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_D_ATTRIBUTE});
        }
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_D_ATTRIBUTE)) {

            buildShape(ctx, e, (ShapeNode)node);
            handleGeometryChanged();
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
