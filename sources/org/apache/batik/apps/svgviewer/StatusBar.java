/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a viewer status bar.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StatusBar extends JPanel {
    /**
     * The gui resources file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.apps.svgviewer.resources.StatusBarMessages";

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
     * The x position/width label.
     */
    protected JLabel xPosition;
    
    /**
     * The y position/height label.
     */
    protected JLabel yPosition;
    
    /**
     * The message label
     */
    protected JLabel message;

    /**
     * The main message
     */
    protected String mainMessage;

    /**
     * Creates a new status bar
     * @param rm the resource manager that finds the message
     */
    public StatusBar() {
        super(new BorderLayout(5, 5));

        JPanel p = new JPanel(new BorderLayout(0, 0));
        add("West", p);

        xPosition = new JLabel();
        xPosition.setBorder(BorderFactory.createLoweredBevelBorder());
        xPosition.setPreferredSize(new Dimension(75, 16));
        p.add("West", xPosition);

        yPosition = new JLabel();
        yPosition.setBorder(BorderFactory.createLoweredBevelBorder());
        yPosition.setPreferredSize(new Dimension(75, 16));
        p.add("East", yPosition);

        p = new JPanel(new BorderLayout(0, 0));
        message = new JLabel();
        message.setBorder(BorderFactory.createLoweredBevelBorder());
        p.add(message);
        add(p);
        setMainMessage(rManager.getString("Panel.default_message"));
    }

    /**
     * Sets the x position.
     */
    public void setXPosition(int x) {
        xPosition.setText("x: " + x);
    }

    /**
     * Sets the width.
     */
    public void setWidth(int w) {
        xPosition.setText(rManager.getString("Position.width_letters") +
                          " " + w);
    }

    /**
     * Sets the y position.
     */
    public void setYPosition(int y) {
        yPosition.setText("y: " + y);
    }

    /**
     * Sets the height.
     */
    public void setHeight(int h) {
        yPosition.setText(rManager.getString("Position.height_letters") +
                          " " + h);
    }

    /**
     * Sets a temporary message
     * @param s the message
     */
    public void setMessage(String s) {
        message.setText(s);
        setPreferredSize(new Dimension(0, getPreferredSize().height));
        new DisplayThread().start();
    }

    /**
     * Sets the main message
     * @param s the message
     */
    public void setMainMessage(String s) {
        message.setText(mainMessage = s);
        setPreferredSize(new Dimension(0, getPreferredSize().height));
    }

    /**
     * To display the main message
     */
    protected class DisplayThread extends Thread {
        public DisplayThread() {
            setPriority(Thread.MIN_PRIORITY);
        }

        public void run() {
            try {
                Thread.sleep(3000);
            } catch(InterruptedException e) {
            }
            message.setText(mainMessage);
        }
    }
}
