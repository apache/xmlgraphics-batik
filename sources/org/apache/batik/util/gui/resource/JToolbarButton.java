/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * This class represents the buttons used in toolbars.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JToolbarButton extends JButton {
    /**
     * Creates a new toolbar button.
     */
    public JToolbarButton() {
        initialize();
    }

    /**
     * Creates a new toolbar button.
     * @param txt The button text.
     */
    public JToolbarButton(String txt) {
        super(txt);
        initialize();
    }

    /**
     * Initializes the button.
     */
    protected void initialize() {
        setBorderPainted(false);
        addMouseListener(new MouseListener());
    }

    /**
     * To manage the mouse interactions.
     */
    protected class MouseListener extends MouseAdapter {
        public void mouseEntered(MouseEvent ev) {
            setBorderPainted(true);
        }
        public void mouseExited(MouseEvent ev) {
            setBorderPainted(false);
        }
    }
}
