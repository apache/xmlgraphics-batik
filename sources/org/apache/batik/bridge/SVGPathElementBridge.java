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
import java.awt.geom.GeneralPath;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;
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
     * default shape for the update of 'd' when
     * the value is the empty string.
     */
    protected static final Shape DEFAULT_SHAPE = new GeneralPath();

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
        }
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_D_ATTRIBUTE)) {
            if ( evt.getNewValue().length() == 0 ){
                ((ShapeNode)node).setShape(DEFAULT_SHAPE);
            }
            else{
                buildShape(ctx, e, (ShapeNode)node);
            }
            handleGeometryChanged();
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }

    protected void handleCSSPropertyChanged(int property) {
        switch(property) {
        case SVGCSSEngine.FILL_RULE_INDEX:
            buildShape(ctx, e, (ShapeNode) node);
            handleGeometryChanged();
            break;
        default:
            super.handleCSSPropertyChanged(property);
        }
    }
}
