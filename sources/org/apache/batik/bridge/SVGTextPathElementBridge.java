/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.apache.batik.dom.util.XLinkSupport;
import org.w3c.dom.Element;
import java.io.StringReader;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.Shape;

/**
 * Bridge class for the &lt;textPath> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGTextPathElementBridge extends AbstractSVGBridge
                                      implements ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;textPath> element.
     */
    public SVGTextPathElementBridge() {}

    /**
     * Returns 'textPath'.
     */
    public String getLocalName() {
        return SVG_TEXT_PATH_TAG;
    }

    /**
     * Creates a TextPath object that represents the path along which the text
     * is to be rendered.
     *
     * @param ctx The bridge context.
     * @param textPathElement The &lt;textPath> element.
     *
     * @return The new TextPath.
     */
    public TextPath createTextPath(BridgeContext ctx, Element textPathElement) {

        // get the referenced element
        String uri = XLinkSupport.getXLinkHref(textPathElement);
        Element pathElement = ctx.getReferencedElement(textPathElement, uri);

        if (pathElement == null || !pathElement.getTagName().equals(SVG_PATH_TAG)) {
            // couldn't find the referenced element
            // or the referenced element was not a path
            throw new BridgeException(textPathElement, ERR_URI_BAD_TARGET,
                                          new Object[] {uri});
        }

        // construct a shape for the referenced path element
        String s = pathElement.getAttributeNS(null, SVG_D_ATTRIBUTE);
        Shape pathShape = null;
        if (s.length() != 0) {
            AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(pathElement));
            try {
                PathParser pathParser = new PathParser();
                pathParser.setPathHandler(app);
                pathParser.parse(new StringReader(s));
            } catch (ParseException ex) {
               throw new BridgeException(pathElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                          new Object[] {SVG_D_ATTRIBUTE});
            } finally {
                pathShape = app.getShape();
            }
        } else {
            throw new BridgeException(pathElement, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_D_ATTRIBUTE});
        }

        // if the reference path element has a transform apply the transform
        // to the path shape
        s = pathElement.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            AffineTransform tr = SVGUtilities.convertTransform(pathElement,
                                                  SVG_TRANSFORM_ATTRIBUTE, s);
            pathShape = tr.createTransformedShape(pathShape);
        }

        // create the TextPath object that we are going to return
        TextPath textPath = new TextPath(new GeneralPath(pathShape));

        // set the start offset if specified
        s = textPathElement.getAttributeNS(null, SVG_START_OFFSET_ATTRIBUTE);
        if (s.length() > 0) {
            float startOffset = 0;
            int percentIndex = s.indexOf("%");
            if (percentIndex != -1) {
                // its a percentage of the length of the path
                float pathLength = textPath.lengthOfPath();
                String percentString = s.substring(0,percentIndex);
                float startOffsetPercent = 0;
                try {
                    startOffsetPercent = SVGUtilities.convertSVGNumber(percentString);
                } catch (NumberFormatException e) {
                    startOffsetPercent = -1;
                }
                if (startOffsetPercent < 0) {
                    throw new BridgeException(textPathElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                              new Object[] {SVG_START_OFFSET_ATTRIBUTE, s});
                }
                startOffset = (float)(startOffsetPercent * pathLength/100.0);

            } else {
                // its an absolute length
                UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, textPathElement);
                startOffset = UnitProcessor.svgOtherLengthToUserSpace(s, SVG_START_OFFSET_ATTRIBUTE, uctx);
            }
            textPath.setStartOffset(startOffset);
        }

        return textPath;
    }


}

