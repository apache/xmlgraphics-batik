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
 * This test validates that spaces are preserved correctly in drawString
 * calls. Validates bug #2657 fix.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TextSpacePreserve implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(Color.black); // new Color(102, 102, 144));

        int legendX = 10, legendY = 12;
        g.translate(0, 30);


        // Print text with spaces.
        g.drawString("     space before.", legendX, legendY);
        g.drawString("Multiple spaces between A and B: A    B", legendX, legendY + 20);
        g.drawString("This is a first line\n     and this is a second line starting with spaces", 
                     legendX, legendY + 40);
        g.drawString("Should have no trailing spaces", legendX, legendY + 60);
    }
}
