/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import java.util.Map;
import java.util.Hashtable;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

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

    /**
     * The return value if 'OK' is chosen.
     */
    public final static int OK_OPTION = 0;

    /**
     * The return value if 'Cancel' is chosen.
     */
    public final static int CANCEL_OPTION = 1;

    //////////////////////////////////////////////////////////////
    // GUI Resources Keys
    //////////////////////////////////////////////////////////////

    public static final String ICON_USER_LANGUAGE
        = "org/apache/batik/apps/svgbrowser/resources/userLanguagePref.png";

    public static final String ICON_USER_STYLESHEET
        = "org/apache/batik/apps/svgbrowser/resources/userStylesheetPref.png";

    public static final String ICON_BEHAVIOR
        = "org/apache/batik/apps/svgbrowser/resources/behaviorsPref.png";

    public static final String ICON_NETWORK
        = "org/apache/batik/apps/svgbrowser/resources/networkPref.png";

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

    public static final String TITLE_DIALOG
        = "PreferenceDialog.title.dialog";
    
    public static final String CONFIG_HOST_TEXT_FIELD_LENGTH 
        = "PreferenceDialog.config.host.text.field.length";

    public static final String CONFIG_PORT_TEXT_FIELD_LENGTH
        = "PreferenceDialog.config.port.text.field.length";

    public static final String CONFIG_OK_MNEMONIC 
        = "PreferenceDialog.config.ok.mnemonic";

    public static final String CONFIG_CANCEL_MNEMONIC
        = "PreferenceDialog.config.cancel.mnemonic";

    //////////////////////////////////////////////////////////////
    // Following are the preference keys used in the
    // PreferenceManager model.
    //////////////////////////////////////////////////////////////
    
    public static final String PREFERENCE_KEY_LANGUAGES
        = "preference.key.languages";

    public static final String PREFERENCE_KEY_USER_STYLESHEET
        = "preference.key.user.stylesheet";

    public static final String PREFERENCE_KEY_SHOW_RENDERING
        = "preference.key.show.rendering";

    public static final String PREFERENCE_KEY_AUTO_ADJUST_WINDOW
        = "preference.key.auto.adjust.window";

    public static final String PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING
        = "preference.key.enable.double.buffering";

    public static final String PREFERENCE_KEY_SHOW_DEBUG_TRACE
        = "preference.key.show.debug.trace";

    public static final String PREFERENCE_KEY_PROXY_HOST
        = "preference.key.proxy.host";

    public static final String PREFERENCE_KEY_PROXY_PORT
        = "preference.key.proxy.port";

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
     * Allows selection of the user languages
     */
    protected LanguageDialog.Panel languagePanel;

    /**
     * Allows selection of a user stylesheet
     */
    protected UserStyleDialog.Panel userStylesheetPanel;

    protected JCheckBox showRendering;

    protected JCheckBox autoAdjustWindow;

    protected JCheckBox showDebugTrace;

    protected JCheckBox enableDoubleBuffering;

    protected JTextField host, port;

    /**
     * Code indicating whether the dialog was OKayed
     * or cancelled
     */
    protected int returnCode;

    /**
     * Default constructor
     */
    public PreferenceDialog(PreferenceManager model){
        super((Frame)null, true);

        if(model == null){
            throw new IllegalArgumentException();
        }

        this.model = model;
        buildGUI();
        initializeGUI();
        pack();
    }

    /**
     * Initializes the GUI components with the values
     * from the model.
     */
    protected void initializeGUI(){
        //
        // Initialize language. The set of languages is
        // defined by a String.
        //
        String languages = model.getString(PREFERENCE_KEY_LANGUAGES);
        languagePanel.setLanguages(languages);

        //
        // Initializes the User Stylesheet
        //
        String userStylesheetPath = model.getString(PREFERENCE_KEY_USER_STYLESHEET);
        userStylesheetPanel.setPath(userStylesheetPath);

        //
        // Initializes the browser options
        //
        showRendering.setSelected(model.getBoolean(PREFERENCE_KEY_SHOW_RENDERING));
        autoAdjustWindow.setSelected(model.getBoolean(PREFERENCE_KEY_AUTO_ADJUST_WINDOW));
        enableDoubleBuffering.setSelected(model.getBoolean(PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING));
        showDebugTrace.setSelected(model.getBoolean(PREFERENCE_KEY_SHOW_DEBUG_TRACE));

        //
        // Initialize the proxy options
        //
        host.setText(model.getString(PREFERENCE_KEY_PROXY_HOST));
        port.setText(model.getString(PREFERENCE_KEY_PROXY_PORT));

        //
        // Sets the dialog's title
        //
        setTitle(Resources.getString(TITLE_DIALOG));
    }

    /**
     * Stores current setting in PreferenceManager model
     */
    protected void savePreferences(){
        model.setString(PREFERENCE_KEY_LANGUAGES,
                        languagePanel.getLanguages());
        model.setString(PREFERENCE_KEY_USER_STYLESHEET,
                        userStylesheetPanel.getPath());
        model.setBoolean(PREFERENCE_KEY_SHOW_RENDERING,
                         showRendering.isSelected());
        model.setBoolean(PREFERENCE_KEY_AUTO_ADJUST_WINDOW,
                         autoAdjustWindow.isSelected());
        model.setBoolean(PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING,
                         enableDoubleBuffering.isSelected());
        model.setBoolean(PREFERENCE_KEY_SHOW_DEBUG_TRACE,
                         showDebugTrace.isSelected());
        model.setString(PREFERENCE_KEY_PROXY_HOST,
                        host.getText());
        model.setString(PREFERENCE_KEY_PROXY_PORT,
                        port.getText());
    }

    /**
     * Builds the UI for this dialog
     */
    protected void buildGUI(){
        JPanel panel = new JPanel(new BorderLayout());

        Component config = buildConfigPanel();
        Component list = buildConfigPanelList();

        panel.add(list, BorderLayout.WEST);
        panel.add(config, BorderLayout.CENTER);
        panel.add(buildButtonsPanel(), BorderLayout.SOUTH);
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

        okButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    setVisible(false);
                    returnCode = OK_OPTION;
                    savePreferences();
                    dispose();
                }
            });

        cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    setVisible(false);
                    returnCode = CANCEL_OPTION;
                    dispose();
                }
            });

        addKeyListener(new KeyAdapter(){
                public void keyPressed(KeyEvent e){
                    System.out.println("Got key event");
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        setVisible(false);
                        returnCode = CANCEL_OPTION;
                        dispose();
                    }
                }
            });

        return p;
    }

    protected Component buildConfigPanelList(){
        String[] configList 
            = { Resources.getString(LABEL_NETWORK),
                Resources.getString(LABEL_USER_LANGUAGE),
                Resources.getString(LABEL_BEHAVIOR),
                Resources.getString(LABEL_USER_STYLESHEET),
                };

        final JList list = new JList(configList);
        list.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent evt){
                    if(!evt.getValueIsAdjusting()){
                        configPanelSelector.select(list.getSelectedValue().toString());
                    }
                }
            });
        list.setVisibleRowCount(4);

        // Set Cell Renderer
        ClassLoader cl = this.getClass().getClassLoader();
        Map map= new Hashtable();
        map.put(Resources.getString(LABEL_USER_LANGUAGE), new ImageIcon(cl.getResource(ICON_USER_LANGUAGE)));
        map.put(Resources.getString(LABEL_USER_STYLESHEET), new ImageIcon(cl.getResource(ICON_USER_STYLESHEET)));
        map.put(Resources.getString(LABEL_BEHAVIOR), new ImageIcon(cl.getResource(ICON_BEHAVIOR)));
        map.put(Resources.getString(LABEL_NETWORK), new ImageIcon(cl.getResource(ICON_NETWORK)));

        list.setCellRenderer(new IconCellRenderer(map));

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
        languagePanel = new LanguageDialog.Panel();
        return languagePanel;
    }

    protected Component buildUserStyleSheet(){
        userStylesheetPanel = new UserStyleDialog.Panel();
        return userStylesheetPanel;
    }

    protected Component buildUserFont(){
        return new JButton("User Font");
    }

    protected Component buildBehavior(){
        JGridBagPanel p = new JGridBagPanel();
        showRendering 
            = new JCheckBox(Resources.getString(LABEL_SHOW_RENDERING));
        autoAdjustWindow
            = new JCheckBox(Resources.getString(LABEL_AUTO_ADJUST_WINDOW));
        enableDoubleBuffering
            = new JCheckBox(Resources.getString(LABEL_ENABLE_DOUBLE_BUFFERING));
        showDebugTrace
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
        host = new JTextField(Resources.getInteger(CONFIG_HOST_TEXT_FIELD_LENGTH));
        JLabel hostLabel = new JLabel(Resources.getString(LABEL_HOST));
        port = new JTextField(Resources.getInteger(CONFIG_PORT_TEXT_FIELD_LENGTH));
        JLabel portLabel = new JLabel(Resources.getString(LABEL_PORT));
        p.add(hostLabel, 0, 0, 1, 1, WEST, HORIZONTAL, 0, 0);
        p.add(host, 0, 1, 1, 1, CENTER, HORIZONTAL, 1, 0);
        p.add(portLabel, 1, 0, 1, 1, WEST, HORIZONTAL, 0, 0);
        p.add(port, 1, 1, 1, 1, CENTER, HORIZONTAL, 0, 0);
        p.add(new JLabel(""), 2, 1, 1, 1, CENTER, HORIZONTAL, 0, 0);

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

    /**
     * Shows the dialog
     * @return OK_OPTION or CANCEL_OPTION
     */
    public int showDialog(){
        pack();
        show();
        return returnCode;
    }

    public static void main(String[] args){
        Map defaults = new Hashtable();
        defaults.put(PREFERENCE_KEY_LANGUAGES, "fr");
        defaults.put(PREFERENCE_KEY_SHOW_RENDERING, new Boolean(true));
        defaults.put(PREFERENCE_KEY_AUTO_ADJUST_WINDOW, new Boolean(true));
        defaults.put(PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING, new Boolean(true));
        defaults.put(PREFERENCE_KEY_SHOW_DEBUG_TRACE, new Boolean(true));
        defaults.put(PREFERENCE_KEY_PROXY_HOST, "webcache.eng.sun.com");
        defaults.put(PREFERENCE_KEY_PROXY_PORT, "8080");

        XMLPreferenceManager manager 
            = new XMLPreferenceManager(args[0], defaults);
        PreferenceDialog dlg = new PreferenceDialog(manager);
        int c = dlg.showDialog();
        if(c == OK_OPTION){
            try{
                manager.save();
                System.out.println("Done Saving options");
                System.exit(0);
            }catch(Exception e){
                System.err.println("Could not save options");
                e.printStackTrace();
            }
        }
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

class IconCellRendererOld extends JLabel implements ListCellRenderer {
    Map iconMap;

    public IconCellRendererOld(Map iconMap){
        this.iconMap = iconMap;

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    public Component getListCellRendererComponent
        (
         JList list,
         Object value,            // value to display
         int index,               // cell index
         boolean isSelected,      // is the cell selected
         boolean cellHasFocus)    // the list and the cell have the focus
    {
        String s = value.toString();
        setText(s);
        ImageIcon icon = (ImageIcon)iconMap.get(s);
        if(icon != null){
            setIcon(icon);
            setHorizontalAlignment(CENTER);
            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);
        }
        // if (isSelected) {
        setBackground(java.awt.Color.red); // list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            /*}
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            }*/
            // setEnabled(list.isEnabled());
            // setFont(list.getFont());
        return this;
    }
}

class IconCellRenderer extends JLabel
    implements ListCellRenderer
{
    protected Map map;
    protected static Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public IconCellRenderer(Map map) {
	super();
    this.map = map;
       	noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	setOpaque(true);
	setBorder(noFocusBorder);
    }


    public Component getListCellRendererComponent(
        JList list,
	Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
        
        setComponentOrientation(list.getComponentOrientation());
        
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	}
	else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}

	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

	/*if (value instanceof Icon) {
	    setIcon((Icon)value);
	    setText("");
	}
	else {
	    setIcon(null);
	    setText((value == null) ? "" : value.toString());
        }*/

    setText(value.toString());
        ImageIcon icon = (ImageIcon)map.get(value.toString());
        if(icon != null){
            setIcon(icon);
            setHorizontalAlignment(CENTER);
            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);
        }
	setEnabled(list.isEnabled());
	setFont(list.getFont());

	return this;
    }


   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void validate() {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void revalidate() {}
   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void repaint(long tm, int x, int y, int width, int height) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void repaint(Rectangle r) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	// Strings get interned...
	if (propertyName=="text")
	    super.firePropertyChange(propertyName, oldValue, newValue);
    }

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}
