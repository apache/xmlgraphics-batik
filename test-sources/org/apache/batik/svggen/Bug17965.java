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
 * This test validates fix to Bug #17965 and checks that 
 * attributes which do not apply to given element (eg., font-family
 * does not apply to <rect>) are not written out.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class Bug17965 implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                           java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        Font font = new Font("Arial", Font.PLAIN, 30);
        g.setFont(font);
        g.setPaint(Color.blue);
        g.fillRect(0, 0, 50, 50);

        font = new Font("Helvetica", Font.PLAIN, 20);
        g.setFont(font);
        g.fillRect( 50, 50, 50, 50);
    }
}
