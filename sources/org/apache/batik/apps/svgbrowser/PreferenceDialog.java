/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.batik.ext.swing.JGridBagPanel;
import org.apache.batik.ext.swing.GridBagConstants;

import org.apache.batik.util.PreferenceManager;
import org.apache.batik.util.gui.LanguageDialog;
import org.apache.batik.util.gui.UserStyleDialog;

/**
 * Dialog that displays user preferences.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class PreferenceDialog extends JDialog 
    implements GridBagConstants {

    public static final String LABEL_USER_OPTIONS 
        = "PreferenceDialog.label.user.options";

    public static final String LABEL_BEHAVIOR
        = "PreferenceDialog.label.behavior";

    public static final String LABEL_NETWORK
        = "PreferenceDialog.label.network";

    public static final String LABEL_USER_LANGUAGE 
        = "PreferenceDialog.label.user.language";

    public static final String LABEL_USER_STYLESHEET
        = "PreferenceDialog.label.user.stylesheet";

    public static final String LABEL_USER_FONT
        = "PreferenceDialog.label.user.font";

    public static final String LABEL_APPLICATIONS
        = "PreferenceDialog.label.applications";

    public static final String LABEL_SHOW_RENDERING
        = "PreferenceDialog.label.show.rendering";

    public static final String LABEL_AUTO_ADJUST_WINDOW
        = "PreferenceDialog.label.auto.adjust.window";

    public static final String LABEL_ENABLE_DOUBLE_BUFFERING
        = "PreferenceDialog.label.enable.double.buffering";

    public static final String LABEL_SHOW_DEBUG_TRACE
        = "PreferenceDialog.label.show.debug.trace";

    public static final String LABEL_HOST
        = "PreferenceDialog.label.host";

    public static final String LABEL_PORT
        = "PreferenceDialog.label.port";

    public static final String LABEL_OK
        = "PreferenceDialog.label.ok";

    public static final String LABEL_CANCEL
        = "PreferenceDialog.label.cancel";

    public static final String TITLE_BEHAVIOR 
        = "PreferenceDialog.title.behavior";

    public static final String TITLE_NETWORK
        = "PreferenceDialog.title.network";
    
    public static final String CONFIG_HOST_TEXT_FIELD_LENGTH 
        = "PreferenceDialog.config.host.text.field.length";

    public static final String CONFIG_PORT_TEXT_FIELD_LENGTH
        = "PreferenceDialog.config.port.text.field.length";

    public static final String CONFIG_OK_MNEMONIC 
        = "PreferenceDialog.config.ok.mnemonic";

    public static final String CONFIG_CANCEL_MNEMONIC
        = "PreferenceDialog.config.cancel.mnemonic";

    /**
     * <tt>PreferenceManager</tt> used to store and retrieve
     * preferences
     */
    protected PreferenceManager model;

    /**
     * Allows selection of the desired configuration panel
     */
    protected ConfigurationPanelSelector configPanelSelector;

    /**
     * Default constructor
     */
    public PreferenceDialog(){
        super((Frame)null, true);

        buildGUI();
        pack();
    }

    /**
     * Builds the UI for this dialog
     */
    protected void buildGUI(){
        JGridBagPanel panel = new JGridBagPanel();

        Component config = buildConfigPanel();
        Component list = buildConfigPanelList();

        panel.add(list,   0, 0, 1, 1, CENTER, BOTH, 0, 1);
        panel.add(config, 1, 0, 1, 1, CENTER, BOTH, 1, 1);

        panel.add(buildButtonsPanel(), 0, 1, 2, 1, EAST, HORIZONTAL, 1, 0);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));

        getContentPane().add(panel);
    }

    /**
     * Creates the OK/Cancel buttons panel
     */
    protected JPanel buildButtonsPanel() {
        JPanel  p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(Resources.getString(LABEL_OK));
        okButton.setMnemonic(Resources.getCharacter(CONFIG_OK_MNEMONIC));
        JButton cancelButton = new JButton(Resources.getString(LABEL_CANCEL));
        cancelButton.setMnemonic(Resources.getCharacter(CONFIG_CANCEL_MNEMONIC));
        p.add(okButton);
        p.add(cancelButton);

        return p;
    }

    protected Component buildConfigPanelList(){
        String[] configList 
            = { Resources.getString(LABEL_USER_LANGUAGE),
                Resources.getString(LABEL_USER_STYLESHEET),
                Resources.getString(LABEL_BEHAVIOR),
                Resources.getString(LABEL_NETWORK)};

        final JList list = new JList(configList);
        list.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent evt){
                    if(!evt.getValueIsAdjusting()){
                        configPanelSelector.select(list.getSelectedValue().toString());
                    }
                }
            });

        list.setSelectedIndex(0);

        return new JScrollPane(list);
    }

    protected Component buildConfigPanel(){
        JPanel configPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        configPanel.setLayout(cardLayout);
        configPanel.add(buildUserLanguage(),
                        Resources.getString(LABEL_USER_LANGUAGE));

        configPanel.add(buildUserStyleSheet(),
                        Resources.getString(LABEL_USER_STYLESHEET));
        
        configPanel.add(buildBehavior(),
                        Resources.getString(LABEL_BEHAVIOR));
        
        configPanel.add(buildNetwork(),
                        Resources.getString(LABEL_NETWORK));
        
        configPanel.add(buildApplications(),
                        Resources.getString(LABEL_APPLICATIONS));
        
        configPanelSelector = new ConfigurationPanelSelector(configPanel,
                                                             cardLayout);

        return configPanel;
    }

    protected Component buildUserOptions(){
        JTabbedPane p = new JTabbedPane();
        p.add(buildUserLanguage(),
              Resources.getString(LABEL_USER_LANGUAGE));
        p.add(buildUserStyleSheet(),
              Resources.getString(LABEL_USER_STYLESHEET));
        p.add(buildUserFont(),
              Resources.getString(LABEL_USER_FONT));
        return p;
    }

    protected Component buildUserLanguage(){
        return new LanguageDialog.Panel();
    }

    protected Component buildUserStyleSheet(){
        return new UserStyleDialog.Panel();
    }

    protected Component buildUserFont(){
        return new JButton("User Font");
    }

    protected Component buildBehavior(){
        JGridBagPanel p = new JGridBagPanel();
        JCheckBox showRendering 
            = new JCheckBox(Resources.getString(LABEL_SHOW_RENDERING));
        JCheckBox autoAdjustWindow
            = new JCheckBox(Resources.getString(LABEL_AUTO_ADJUST_WINDOW));
        JCheckBox enableDoubleBuffering
            = new JCheckBox(Resources.getString(LABEL_ENABLE_DOUBLE_BUFFERING));
        JCheckBox showDebugTrace
            = new JCheckBox(Resources.getString(LABEL_SHOW_DEBUG_TRACE));

        p.add(showRendering,    0, 0, 1, 1, WEST, HORIZONTAL, 1, 0);
        p.add(autoAdjustWindow, 0, 1, 1, 1, WEST, HORIZONTAL, 1, 0);
        p.add(enableDoubleBuffering, 0, 2, 1, 1, WEST, HORIZONTAL, 1, 0);
        p.add(showDebugTrace,   0, 3, 1, 1, WEST, HORIZONTAL, 1, 0);

        p.setBorder(BorderFactory.createCompoundBorder
                    (BorderFactory.createTitledBorder
                     (BorderFactory.createEtchedBorder(),
                     Resources.getString(TITLE_BEHAVIOR)),
                     BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        return p;
    }

    protected Component buildNetwork(){
        JGridBagPanel p = new JGridBagPanel();
        JTextField host = new JTextField(Resources.getInteger(CONFIG_HOST_TEXT_FIELD_LENGTH));
        JLabel hostLabel = new JLabel(Resources.getString(LABEL_HOST));
        JTextField port = new JTextField(Resources.getInteger(CONFIG_PORT_TEXT_FIELD_LENGTH));
        JLabel portLabel = new JLabel(Resources.getString(LABEL_PORT));
        p.add(hostLabel, 0, 0, 1, 1, WEST, HORIZONTAL, 0, 0);
        p.add(host, 0, 1, 1, 1, CENTER, HORIZONTAL, 0, 0);
        p.add(portLabel, 1, 0, 1, 1, WEST, HORIZONTAL, 0, 0);
        p.add(port, 1, 1, 1, 1, CENTER, HORIZONTAL, 0, 0);
        p.add(new JLabel(""), 2, 1, 1, 1, CENTER, HORIZONTAL, 1, 0);

        p.setBorder(BorderFactory.createCompoundBorder
                    (BorderFactory.createTitledBorder
                     (BorderFactory.createEtchedBorder(),
                     Resources.getString(TITLE_BEHAVIOR)),
                     BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        return p;
    }

    protected Component buildApplications(){
        return new JButton("Applications");
    }

    public static void main(String[] args){
        PreferenceDialog dlg = new PreferenceDialog();
        dlg.show();
    }
}


class ConfigurationPanelSelector {
    private CardLayout layout;
    private Container container;

    public ConfigurationPanelSelector(Container container,
                                      CardLayout layout){
        this.layout = layout;
        this.container = container;
    }

    public void select(String panelName){
        layout.show(container, panelName);
    }
}

