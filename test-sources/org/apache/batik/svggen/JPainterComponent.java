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
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Simple component which displays the rendering created by
 * a <tt>Painter</tt>.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class JPainterComponent extends JComponent {
    /**
     * <tt>Painter</tt>
     */
    protected Painter painter;

    /**
     * Delegates to its <tt>Painter</tt>
     */
    public void paint(Graphics _g){
        Graphics2D g = (Graphics2D)_g;
        BufferedImage buf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        painter.paint(buf.createGraphics());
        g.drawImage(buf, 0, 0, null);
    }

    /**
     * Constructor
     */
    public JPainterComponent(Painter painter){
        this.painter = painter;
    }

}
