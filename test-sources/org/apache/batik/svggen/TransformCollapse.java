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
 * This test validates that transforms are collapsed when they
 * should.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class TransformCollapse implements Painter {
    public void paint(Graphics2D g){
        g.translate(10, 10);
        g.translate(20, 30);

        // Should see a translate(30, 40) in the output SVg
        g.drawString("translate collapse", 0, 0);

        g.scale(2, 2);
        g.scale(2, 4);
        
        // Should see a scale(4, 8)
        g.drawString("scale collapse", 10, 10);

        g.scale(.25, .125);
        g.rotate(Math.toRadians(90));
        g.rotate(Math.toRadians(-60));

        // Should see a rotate(30)
        g.drawString("rotate collapse", 0, 40);
        
        g.rotate(Math.toRadians(-30));
        // Should get identity
        g.drawString("identity", 0, 80);
    }
}
