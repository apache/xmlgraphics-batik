/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;

import org.w3c.dom.Element;

/**
 * A test &lt;foreignObject> handler factory.  The factory creates a simple
 * &lt;foreignObject> handler that just displays a yellow rectangle.
 *
 * @author <a href='mailto:cam%40mcc%2eid%2eau'>Cameron McCormack</a>
 * @version $Id$
 */
public class TestForeignObjectHandlerFactory implements ForeignObjectHandlerFactory {

    /**
     * Returns the namespace URI of the elements ForeignObjectHandlers created
     * by this factory can handle.
     */
    public String getNamespaceURI() {
        return "http://example.org/foreign";
    }

    /**
     * Creates a new ForeignObjectHandler.
     */
    public ForeignObjectHandler createHandler() {
        return new ForeignObjectHandler() {
            public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e,
                                            float w, float h) {
                Rectangle2D r = new Rectangle2D.Float(0, 0, w, h);
                ShapeNode n = new ShapeNode();
                n.setShape(r);
                FillShapePainter p = new FillShapePainter(r);
                p.setPaint(Color.YELLOW);
                n.setShapePainter(p);
                return n;
            }
        };
    }
}
