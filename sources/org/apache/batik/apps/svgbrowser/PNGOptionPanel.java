/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.apache.batik.util.gui.ExtendedGridBagConstraints;

/**
 * This class represents a panel to choose the color model
 * of the PNG, i.e. RGB or INDEXED.
 *
 * @author <a href="mailto:jun@oop-reserch.com">Jun Inamori</a>
 *
 */
public class PNGOptionPanel extends OptionPanel {

    /**
     * The check box for outputing an indexed PNG.
     */
    protected JCheckBox check;

    /**
     * Creates a new panel.
     */
    public PNGOptionPanel() {
	super(new GridBagLayout());

	ExtendedGridBagConstraints constraints = 
	    new ExtendedGridBagConstraints();

	
	constraints.insets = new Insets(5, 5, 5, 5);

	constraints.weightx = 0;
	constraints.weighty = 0;
	constraints.fill = GridBagConstraints.NONE;
	constraints.setGridBounds(0, 0, 1, 1);
	add(new JLabel(resources.getString("PNGOptionPanel.label")), 
	    constraints);

	check=new JCheckBox();

	constraints.weightx = 1.0;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.setGridBounds(1, 0, 1, 1);
	add(check, constraints);
    }

    /**
     * Returns if indexed or not
     */
    public boolean isIndexed() {
	return check.isSelected();
    }

    /**
     * Shows a dialog to choose the indexed PNG.
     */
    public static boolean showDialog(Component parent) {
        String title = resources.getString("PNGOptionPanel.dialog.title");
        PNGOptionPanel panel = new PNGOptionPanel();
	Dialog dialog = new Dialog(parent, title, panel);
	dialog.pack();
	dialog.show();
	return panel.isIndexed();
    }
}
