/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.Font;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import javax.swing.plaf.FontUIResource;

import org.apache.batik.css.CSSDocumentHandler;

import org.apache.batik.util.PreferenceManager;
import org.apache.batik.util.XMLResourceDescriptor;

import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class contains the main method of an SVG viewer.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Main implements Application {

    /**
     * Creates a viewer frame and shows it..
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new Main(args).run();
    }

    /**
     * The CSS parser class name key.
     */
    public final static String CSS_PARSER_CLASS_NAME_KEY = "org.w3c.css.sac.parser";

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.Main";

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
     * The frame's icon.
     */
    protected static ImageIcon frameIcon = new ImageIcon
        (Main.class.getResource(resources.getString("Frame.icon")));

    /**
     * The arguments.
     */
    protected String[] arguments;

    /**
     * The option handlers.
     */
    protected Map handlers = new HashMap();
    {
        handlers.put("-font-size", new FontSizeHandler());
    }

    /**
     * The viewer frames.
     */
    protected List viewerFrames = new LinkedList();

    /**
     * The preference dialog.
     */
    protected PreferenceDialog preferenceDialog;

    /**
     * Creates a new application.
     * @param args The command-line arguments.
     */
    public Main(String[] args) {
        arguments = args;
        CSSDocumentHandler.setParserClassName
        (resources.getString(CSS_PARSER_CLASS_NAME_KEY));
    }

    /**
     * Runs the application.
     */
    public void run() {
        try {
            int i = 0;
            for (; i < arguments.length; i++) {
                OptionHandler oh = (OptionHandler)handlers.get(arguments[i]);
                if (oh == null) {
                    break;
                }
                i = oh.handleOption(i);
            }
            JSVGViewerFrame frame = createAndShowJSVGViewerFrame();
            while (i < arguments.length) {
                if (arguments[i].length() == 0) {
                    i++;
                    continue;
                }

                File file = new File(arguments[i]);
                String uri = null;

                try{
                    if (file.canRead()) {
                        uri = file.toURL().toString();
                    }
                }catch(SecurityException se){
                    // Cannot access files. 
                }
                
                if(uri == null){
                    uri = arguments[i];
                    URL url = null;
                    try{
                        url = new URL(arguments[i]);
                    }catch(MalformedURLException e){
                        // This is not a valid uri
                        uri = null;
                    }
                }

                if (uri != null) {
                    if (frame == null)
                        frame = createAndShowJSVGViewerFrame();

                    frame.getJSVGCanvas().loadSVGDocument
                        (uri);
                    frame = null;
                } else {
                    // Let the user know that we are
                    // skipping this file...

                    // Note that frame may be null, which is
                    // a valid argument for showMessageDialog

                    // NOTE: Need to revisit Resources/Messages usage to
                    //       have a single entry point. Should have a
                    //       formated message here instead of a + ...
                    JOptionPane.showMessageDialog
                        (frame,
                         resources.getString("Error.skipping.file")
                         + arguments[i]);
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            printUsage();
        }
    }

    /**
     * Prints the command line usage.
     */
    protected void printUsage() {
        System.out.println();

        System.out.println(resources.getString("Command.header"));
        System.out.println(resources.getString("Command.syntax"));
        System.out.println();
        System.out.println(resources.getString("Command.options"));
        Iterator it = handlers.keySet().iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            System.out.println(((OptionHandler)handlers.get(s)).getDescription());
        }
    }

    /**
     * This interface represents an option handler.
     */
    protected interface OptionHandler {
        /**
         * Handles the current option.
         * @return the index of argument just before the next one to handle.
         */
        int handleOption(int i);

        /**
         * Returns the option description.
         */
        String getDescription();
    }

    /**
     * To handle the '-font-size' option.
     */
    protected class FontSizeHandler implements OptionHandler {
        public int handleOption(int i) {
            int size = Integer.parseInt(arguments[++i]);

            Font font = new Font("Dialog", Font.PLAIN, size);
            FontUIResource fontRes = new FontUIResource(font);
            UIManager.put("CheckBox.font", fontRes);
            UIManager.put("PopupMenu.font", fontRes);
            UIManager.put("TextPane.font", fontRes);
            UIManager.put("MenuItem.font", fontRes);
            UIManager.put("ComboBox.font", fontRes);
            UIManager.put("Button.font", fontRes);
            UIManager.put("Tree.font", fontRes);
            UIManager.put("ScrollPane.font", fontRes);
            UIManager.put("TabbedPane.font", fontRes);
            UIManager.put("EditorPane.font", fontRes);
            UIManager.put("TitledBorder.font", fontRes);
            UIManager.put("Menu.font", fontRes);
            UIManager.put("TextArea.font", fontRes);
            UIManager.put("OptionPane.font", fontRes);
            UIManager.put("DesktopIcon.font", fontRes);
            UIManager.put("MenuBar.font", fontRes);
            UIManager.put("ToolBar.font", fontRes);
            UIManager.put("RadioButton.font", fontRes);
            UIManager.put("ToggleButton.font", fontRes);
            UIManager.put("ToolTip.font", fontRes);
            UIManager.put("ProgressBar.font", fontRes);
            UIManager.put("TableHeader.font", fontRes);
            UIManager.put("Panel.font", fontRes);
            UIManager.put("List.font", fontRes);
            UIManager.put("ColorChooser.font", fontRes);
            UIManager.put("PasswordField.font", fontRes);
            UIManager.put("TextField.font", fontRes);
            UIManager.put("Table.font", fontRes);
            UIManager.put("Label.font", fontRes);
            UIManager.put("InternalFrameTitlePane.font", fontRes);
            UIManager.put("CheckBoxMenuItem.font", fontRes);

            return i;
        }
        public String getDescription() {
            return resources.getString("Command.font-size");
        }
    }

    // Application ///////////////////////////////////////////////

    /**
     * Creates and shows a new viewer frame.
     */
    public JSVGViewerFrame createAndShowJSVGViewerFrame() {
        JSVGViewerFrame mainFrame = new JSVGViewerFrame(this);
        mainFrame.setSize(resources.getInteger("Frame.width"),
                          resources.getInteger("Frame.height"));
        mainFrame.setIconImage(frameIcon.getImage());
        mainFrame.setTitle(resources.getString("Frame.title"));
        mainFrame.show();
        viewerFrames.add(mainFrame);
        return mainFrame;
    }

    /**
     * Closes the given viewer frame.
     */
    public void closeJSVGViewerFrame(JSVGViewerFrame f) {
        viewerFrames.remove(f);
        if (viewerFrames.size() == 0) {
            System.exit(0);
        }
        f.setVisible(false);
    }

    /**
     * Creates a new application exit action.
     */
    public Action createExitAction(JSVGViewerFrame vf) {
        return new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
    }

    /**
     * Opens the given link in a new window.
     */
    public void openLink(String url) {
        JSVGViewerFrame f = createAndShowJSVGViewerFrame();
        f.getJSVGCanvas().loadSVGDocument(url);
    }

    /**
     * Returns the XML parser class name.
     */
    public String getXMLParserClassName() {
        return XMLResourceDescriptor.getXMLParserClassName();
    }

    /**
     * Shows the preference dialog.
     */
    public void showPreferenceDialog(JSVGViewerFrame f) {
        if (preferenceDialog == null) {
            Map defaults = new HashMap(11);

            defaults.put(PreferenceDialog.PREFERENCE_KEY_LANGUAGES,
                         Locale.getDefault().getLanguage());
            defaults.put(PreferenceDialog.PREFERENCE_KEY_SHOW_RENDERING,
                         Boolean.FALSE);
            defaults.put(PreferenceDialog.PREFERENCE_KEY_AUTO_ADJUST_WINDOW,
                         Boolean.TRUE);
            defaults.put(PreferenceDialog.PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING,
                         Boolean.TRUE);
            defaults.put(PreferenceDialog.PREFERENCE_KEY_SHOW_DEBUG_TRACE,
                         Boolean.FALSE);
            defaults.put(PreferenceDialog.PREFERENCE_KEY_PROXY_HOST,
                         "");
            defaults.put(PreferenceDialog.PREFERENCE_KEY_PROXY_PORT,
                         "");

            XMLPreferenceManager manager;
            manager = new XMLPreferenceManager("batik-preferences.xml", defaults);
            try {
                manager.load();
                setPreferences(manager);
            } catch (Exception e) {
            }
            preferenceDialog = new PreferenceDialog(manager);
        }
        if (preferenceDialog.showDialog() == PreferenceDialog.OK_OPTION) {
            PreferenceManager manager = preferenceDialog.getPreferenceManager();
            try {
                manager.save();
                setPreferences(manager);
            } catch (Exception e) {
            }
        }
    }

    private void setPreferences(PreferenceManager manager) {
        Iterator it = viewerFrames.iterator();
        while (it.hasNext()) {
            JSVGViewerFrame vf = (JSVGViewerFrame)it.next();
            boolean db = manager.getBoolean
                (PreferenceDialog.PREFERENCE_KEY_ENABLE_DOUBLE_BUFFERING);
            vf.getJSVGCanvas().setDoubleBufferedRendering(db);
            boolean sr = manager.getBoolean
                (PreferenceDialog.PREFERENCE_KEY_SHOW_RENDERING);
            vf.getJSVGCanvas().setProgressivePaint(sr);
            boolean d = manager.getBoolean
                (PreferenceDialog.PREFERENCE_KEY_SHOW_DEBUG_TRACE);
            vf.setDebug(d);
            boolean aa = manager.getBoolean
                (PreferenceDialog.PREFERENCE_KEY_AUTO_ADJUST_WINDOW);
            vf.setAutoAdjust(aa);
        }

        System.setProperty("proxyHost", manager.getString
                           (PreferenceDialog.PREFERENCE_KEY_PROXY_HOST));
        System.setProperty("proxyPort", manager.getString
                           (PreferenceDialog.PREFERENCE_KEY_PROXY_PORT));
    }

    /**
     * Returns the user languages.
     */
    public String getLanguages() {
        if (preferenceDialog == null) {
            return Locale.getDefault().getLanguage();
        }
        return preferenceDialog.getPreferenceManager().getString
            (PreferenceDialog.PREFERENCE_KEY_LANGUAGES);
    }

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    public String getUserStyleSheetURI() {
        if (preferenceDialog == null) {
            return null;
        }
        return preferenceDialog.getPreferenceManager().getString
            (PreferenceDialog.PREFERENCE_KEY_USER_STYLESHEET);
    }
}
