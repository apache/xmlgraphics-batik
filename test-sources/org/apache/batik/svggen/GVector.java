/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.geom.*;

/**
 * This test validates the convertion of Java 2D GlyphVectors
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
