/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a location bar.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocationBar extends JPanel {
    /**
     * The gui resources file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.util.gui.resources.LocationBar";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager rManager;
    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        rManager = new ResourceManager(bundle);
    }
    
    /**
     * The combo box
     */
    protected JComboBox comboBox;

    /**
     * Creates a new location bar.
     */
    public LocationBar() {
        super(new BorderLayout(5, 5));
        JLabel label = new JLabel(rManager.getString("Panel.label"));
        add("West", label);
        try {
            String s = rManager.getString("Panel.icon");
            URL url  = getClass().getResource(s);
            if (url != null) {
                label.setIcon(new ImageIcon(url));
            }
        } catch (MissingResourceException e) {
        }
        add("Center", comboBox = new JComboBox());
        comboBox.setEditable(true);
    }

    /**
     * Adds an action listener to this component.
     */
    public void addActionListener(ActionListener listener) {
        comboBox.addActionListener(listener);
    }

    /**
     * returns the current item text.
     */
    public String getText() {
        return (String)comboBox.getEditor().getItem();
    }

    /**
     * Sets the current text.
     */
    public void setText(String text) {
        comboBox.getEditor().setItem(text);
    }

    /**
     * Adds the given text to the history.
     */
    public void addToHistory(String text) {
        comboBox.addItem(text);
        comboBox.setPreferredSize
            (new Dimension(0, comboBox.getPreferredSize().height));
    }
}
