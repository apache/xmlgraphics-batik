/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a dialog to display an error (message + Exception).
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class JErrorPane extends JOptionPane {

    /**
     * The resource file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.util.gui.resources.JErrorPane";

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
     * The error message.
     */
    protected String msg;

    /**
     * The stack trace.
     */
    protected String stacktrace;

    /**
     * Constructs a new JErrorPane.
     *
     * @param th the throwable object that describes the errror
     * @param type the dialog type
     */
    public JErrorPane(Throwable th, int type) {
        this.msg = th.getMessage();

        StringWriter writer = new StringWriter();
        th.printStackTrace(new PrintWriter(writer));
        writer.flush();
        this.stacktrace = writer.toString();

        setMessage(new MessagePanel());
        setMessageType(type);
    }

    /**
     * The message panel.
     */
    protected class MessagePanel extends JPanel implements ActionMap {

        /**
         * The button factory.
         */
        protected ButtonFactory bf = new ButtonFactory(bundle, this);

        /**
         * The text area used to show the stack trace.
         */
        protected JComponent detailsArea;

        /**
         * The button used to show or not the details.
         */
        protected JButton showDetailButton;

        /**
         * This flag bit indicates whether or not the stack trace is shown.
         */
        protected boolean isDetailShown = false;

        /**
         * The sub panel that contains the stack trace text area.
         */
        protected JPanel subpanel;

        /**
         * Constructs a new <tt>MessagePanel</tt>.
         */
        public MessagePanel() {
            super(new GridBagLayout());

            listeners.put("ShowDetailButtonAction",
                          new ShowDetailButtonAction());

            ExtendedGridBagConstraints constraints =
                new ExtendedGridBagConstraints();

            JTextArea msgArea = new JTextArea(4, 50);
            msgArea.setFont(new JLabel().getFont());
            msgArea.setForeground(new JLabel().getForeground());
            msgArea.setOpaque(false);
            msgArea.setEditable(false);
            msgArea.setText(msg);
            msgArea.setLineWrap(true);

            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.setWeight(0, 0);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.NONE;
            constraints.setGridBounds(0, 0, 2, 1);
            add(msgArea, constraints);

            constraints.insets = new Insets(0, 10, 10, 10);
            showDetailButton = bf.createJButton("ShowDetailButton");
            constraints.setWeight(0, 0);
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.NONE;
            constraints.setGridBounds(0, 1, 1, 1);
            add(showDetailButton, constraints);

            JTextArea details = new JTextArea();
            details.setText(stacktrace);
            details.setEditable(false);
            detailsArea = new JScrollPane(details);

            subpanel = new JPanel(new BorderLayout());
            constraints.setWeight(1d, 1d);
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.setGridBounds(1, 1, 1, 1);
            add(subpanel, constraints);
        }

        /**
         * The map that contains the listeners
         */
        protected Map listeners = new HashMap();

        /**
         * Returns the action associated with the given string or null on error
         *
         * @param key the key mapped with the action to get
         * @throws MissingListenerException if the action is not found
         */
        public Action getAction(String key) throws MissingListenerException {
            return (Action)listeners.get(key);
        }

        /**
         * The action associated with the 'Show Detail' button.
         */
        protected class ShowDetailButtonAction extends AbstractAction {

            public void actionPerformed(ActionEvent evt) {
                if (isDetailShown) {
                    subpanel.remove(detailsArea);
                    isDetailShown = false;
                    showDetailButton.setText
                        (resources.getString("ShowDetailButton.text"));
                } else {
                    subpanel.add(detailsArea, BorderLayout.CENTER);
                    showDetailButton.setText
                        (resources.getString("ShowDetailButton.text2"));
                    isDetailShown = true;
                }
                ((JDialog)getTopLevelAncestor()).pack();
            }
        }
    }
}
