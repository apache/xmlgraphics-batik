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
import java.awt.image.*;

/**
 * This test validates the convertion of Java 2D paints
 * into an SVG attributes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Paints implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Get default paint for painting text
        Paint defaultPaint = Color.black;
        g.setPaint(defaultPaint);

        g.translate(0, 30);

        // Define the rectangle that will be drawn multiple
        // times
        Rectangle rect = new Rectangle(10, 20, 100, 60);

        // First, test plain color with transparency
        Color fillColor = new Color(255, 255, 0, 128);
        g.drawString("Semi transparent black", 10, 10);
        g.drawString("Behind Rectangle", 40, 60);
        g.setPaint(fillColor);
        g.fill(rect);

        g.translate(0, 90);

        // Now, test linear gradient
        GradientPaint fillGradient = new GradientPaint(10, 20, Color.red,
                                                       110, 80, Color.yellow);
        g.setPaint(defaultPaint);
        g.drawString("Red to Yellow linear gradient", 10, 10);
        g.setPaint(fillGradient);
        g.fill(rect);

        g.translate(0, 90);

        // Now, test texture paint
        BufferedImage buf = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = buf.createGraphics();
        bg.setPaint(Color.red);
        bg.fillRect(0, 0, 10, 10);
        bg.setPaint(Color.yellow);
        bg.fillRect(10, 10, 10, 10);
        bg.dispose();
        TexturePaint fillTexture = new TexturePaint(buf, new Rectangle(10, 20, 20, 20));
        g.setPaint(defaultPaint);
        g.drawString("Texture Paint", 10, 10);
        g.setPaint(fillTexture);
        g.fill(rect);
    }
}
