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
import javax.swing.ImageIcon;

/**
 * This test validates fix to Bug #4945 which checks that 
 * the generator handles Font transform.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class Bug4945 implements Painter {
    public void paint(Graphics2D g){
        AffineTransform origAT = g.getTransform(); 
        Font origFont = g.getFont(); 

        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                           java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
       
        // 1) create scaled font
        Font font = origFont.deriveFont(AffineTransform.getScaleInstance(1.5, 3));
        g.setFont(font);
        g.drawString("Scaled Font", 20, 40);

        // 2) create translated font
        font = origFont.deriveFont(AffineTransform.getTranslateInstance(50, 20));
        g.setFont(font);
        g.drawString("Translated Font", 20, 80);
        g.drawLine(20, 80, 120, 80);

        // 3) create sheared font
        font = origFont.deriveFont(AffineTransform.getShearInstance(.5, .5));
        g.setFont(font);
        g.drawString("Sheared Font", 20, 120);

        // 4) create rotated font 
        font = origFont.deriveFont(AffineTransform.getRotateInstance(Math.PI/4));
        g.setFont(font);
        g.drawString("Rotated Font", 220, 120);
    }
}

