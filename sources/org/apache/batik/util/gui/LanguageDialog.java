/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a language selection dialog.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LanguageDialog extends JDialog implements ActionMap {
    /**
     * The resource file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.util.gui.resources.LanguageDialogMessages";

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
     * The map that contains the listeners
     */
    protected Map listeners = new HashMap();

    /**
     * The user languages panel.
     */
    protected Panel panel = new Panel();

    /**
     * Creates a new LanguageDialog object.
     */
    public LanguageDialog(JFrame f) {
        super(f);
        setModal(true);
        setTitle(resources.getString("Dialog.title"));

        listeners.put("OKButtonAction",             new OKButtonAction());
        listeners.put("CancelButtonAction",         new CancelButtonAction());

        getContentPane().add(panel);
        getContentPane().add("South", createButtonsPanel());

        pack();
    }    

    // ActionMap implementation ///////////////////////////////////////

    /**
     * Returns the action associated with the given string
     * or null on error
     * @param key the key mapped with the action to get
     * @throws MissingListenerException if the action is not found
     */
    public Action getAction(String key) throws MissingListenerException {
        return (Action)listeners.get(key);
    }

    /**
     * Creates the OK/Cancel buttons panel
     */
    protected JPanel createButtonsPanel() {
        JPanel  p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonFactory bf = new ButtonFactory(bundle, this);
        p.add(bf.createJButton("OKButton"));
        p.add(bf.createJButton("CancelButton"));

        return p;
    }

    /**
     * The language selection panel.
     */
    public static class Panel extends JPanel {

    }

    /**
     * The action associated with the 'OK' button
     */
    protected class OKButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            //mainFrame.setUserLanguages(userLanguagesPanel.getLanguages());
            dispose();
        }
    }

    /**
     * The action associated with the 'Cancel' button
     */
    protected class CancelButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}
