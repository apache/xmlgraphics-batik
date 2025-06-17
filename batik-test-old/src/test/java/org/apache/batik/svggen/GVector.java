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
package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * This test validates the conversion of Java 2D GlyphVectors
 * SVG Shapes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GVector implements Painter {
    public void paint(Graphics2D g) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

         // Set default font
         Font font = new Font("Arial", Font.BOLD, 15);
         g.setFont(font);

         // Colors used for labels and test output
         Color labelColor = new Color(0x666699);
         g.setPaint(labelColor);

         // Simple String
         String text = "This is a GlyphVector";

         // Get GlyphVector from from
         java.awt.font.GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(),
                                                               text);

         g.drawGlyphVector(gv, 30, 30);
    }
}
