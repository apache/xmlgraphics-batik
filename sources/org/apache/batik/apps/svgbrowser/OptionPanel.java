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
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import java.awt.event.ActionEvent;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a panel to present users with options.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class OptionPanel extends JPanel {

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
     * Creates a new panel.
     */
    public OptionPanel(LayoutManager layout) {
	super(layout);
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
	protected JPanel panel;

	public Dialog(Component parent, String title, JPanel panel) {
	    super(JOptionPane.getFrameForComponent(parent), title);
	    setModal(true);
	    this.panel = panel;
	    getContentPane().add(panel, BorderLayout.CENTER);
	    getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
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

