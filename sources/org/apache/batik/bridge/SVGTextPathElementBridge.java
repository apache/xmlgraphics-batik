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
import java.awt.geom.GeneralPath;

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Element;

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
                pathParser.parse(s);
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

