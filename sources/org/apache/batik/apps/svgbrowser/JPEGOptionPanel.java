/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;

import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.apache.batik.util.gui.ExtendedGridBagConstraints;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a panel to control jpeg encoding quality.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class JPEGOptionPanel extends JPanel {

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.GUI";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager resources;

    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        resources = new ResourceManager(bundle);
    }

    /**
     * The jpeg encoding quality.
     */
    protected JSlider quality;

    /**
     * Creates a new panel.
     */
    public JPEGOptionPanel() {
	super(new GridBagLayout());

	ExtendedGridBagConstraints constraints = 
	    new ExtendedGridBagConstraints();

	
	constraints.insets = new Insets(5, 5, 5, 5);

	constraints.weightx = 0;
	constraints.weighty = 0;
	constraints.fill = GridBagConstraints.NONE;
	constraints.setGridBounds(0, 0, 1, 1);
	add(new JLabel(resources.getString("JPEGOptionPanel.label")), 
	    constraints);

	quality = new JSlider();
	quality.setMinimum(0);
	quality.setMaximum(100);
	quality.setMajorTickSpacing(10);
	quality.setMinorTickSpacing(5);
	quality.setPaintTicks(true);
	quality.setPaintLabels(true);
	quality.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	Hashtable labels = new Hashtable();
	for (int i=0; i < 100; i+=10) {
	    labels.put(new Integer(i), new JLabel("0."+i/10));
	}
	labels.put(new Integer(100), new JLabel("1"));
	quality.setLabelTable(labels);

	Dimension dim = quality.getPreferredSize();
	quality.setPreferredSize(new Dimension(350, dim.height));

	constraints.weightx = 1.0;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.setGridBounds(1, 0, 1, 1);
	add(quality, constraints);
    }

    /**
     * Returns the jpeg quality.
     */
    public float getQuality() {
	return quality.getValue()/100f;
    }

    public static void main(String [] args) {
	System.out.println(showDialog(null));
    }
    
    /**
     * Shows a dialog to choose the jpeg encoding quality and return the quality
     * as a float.
     */
    public static float showDialog(Component parent) {
	Dialog dialog = new Dialog(parent);
	dialog.pack();
	dialog.show();
	return dialog.getQuality();
    }

    /**
     * This class is modal dialog to choose the jpeg encoding quality.
     */
    public static class Dialog extends JDialog {

	/**
	 * The 'ok' button.
	 */
	protected JButton ok;

	/**
	 * The 'ok' button.
	 */
	protected JPEGOptionPanel panel;

	public Dialog(Component parent) {
	    super(JOptionPane.getFrameForComponent(parent),
		  resources.getString("JPEGOptionPanel.dialog.title"));
	    setModal(true);
	    panel = new JPEGOptionPanel();
	    getContentPane().add(panel, BorderLayout.CENTER);
	    getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Returns the jpeg quality.
	 */
	public float getQuality() {
	    return panel.getQuality();
	}

	/**
	 * Creates the button panel.
	 */
	protected JPanel createButtonPanel() {
	    JPanel panel = new JPanel(new FlowLayout());
	    ok = new JButton(resources.getString("OKButton.text"));
	    ok.addActionListener(new OKButtonAction());
	    panel.add(ok);
	    return panel;
	}

	/**
	 * The action associated to the 'ok' button.
	 */
	protected class OKButtonAction extends AbstractAction {

	    public void actionPerformed(ActionEvent evt) {
		dispose();
	    }
	}
    }
}
