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
 * This test color opacity on fill and strokes, because this
 * is handled differently in the Java 2D API than in SVG.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Color2 implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Define Colors
        Color blue = Color.blue;
        Color green = Color.green;
        Color transparentBlue = new Color(0, 0, 255, 128);
        Color transparentGreen = new Color(0, 255, 0, 128);

        // Define AlphaComposites
        AlphaComposite srcOver = AlphaComposite.SrcOver;
        AlphaComposite srcOverTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

        // Define rectangle
        Rectangle rect = new Rectangle(10, 40, 100, 50);

        // Define thick stroke
        BasicStroke thickStroke = new BasicStroke(5);

        // First test: Opaque Colors with AlphaComposite
        g.setPaint(Color.black);
        g.drawString("Opaque Colors, Half Transparent AlphaComposite", 10, 30);

        g.setComposite(srcOverTransparent);
        g.setStroke(thickStroke);
        g.setPaint(blue);
        g.fill(rect);
        g.setPaint(green);
        g.draw(rect);
        g.setPaint(Color.black);
        g.fill(rect);

        g.translate(0, 90);

        // Second test: transparent color, opaque Source Over
        g.setPaint(Color.black);
        g.setComposite(srcOver);
        g.drawString("Transparent Colors, Opaque AlphaComposite SrcOver", 10, 30);

        g.setPaint(transparentBlue);
        g.fill(rect);
        g.setPaint(transparentGreen);
        g.draw(rect);
    }
}
