/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * This class represents a separator for the toolbar buttons.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JToolbarSeparator extends JComponent {
    /**
     * Creates a new JToolbarSeparator object.
     */
    public JToolbarSeparator() {
        setMaximumSize(new Dimension(15, Integer.MAX_VALUE));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        int pos = size.width / 2;
        g.setColor(Color.gray);
        g.drawLine(pos, 3, pos, size.height - 5);
        g.drawLine(pos, 2, pos + 1, 2);
        g.setColor(Color.white);
        g.drawLine(pos + 1, 3, pos + 1, size.height - 5);
        g.drawLine(pos, size.height - 4, pos + 1, size.height - 4);
    }
}
