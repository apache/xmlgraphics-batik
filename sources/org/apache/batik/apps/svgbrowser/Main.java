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

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import javax.swing.plaf.FontUIResource;

import org.apache.batik.css.CSSDocumentHandler;

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
     * The XML parser class name key.
     */
    public final static String XML_PARSER_CLASS_NAME_KEY = "org.xml.sax.driver";

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
     * The XML parser class name.
     */
    protected static String xmlParserClassName =
        resources.getString(XML_PARSER_CLASS_NAME_KEY);

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
     * The current index.
     */
    protected int index;

    /**
     * The option handlers.
     */
    protected Map handlers = new HashMap();
    {
        handlers.put("-font-size", new FontSizeHandler());
    }

    /**
     * The number of viewer frames.
     */
    protected int viewerFrames;

    /**
     * The main frame.
     */
    protected JSVGViewerFrame mainFrame;

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
            while (index < arguments.length) {
                OptionHandler oh = (OptionHandler)handlers.get(arguments[index]);
                if (oh == null) {
                    break;
                }
                oh.handleOption();
            }
            createAndShowJSVGViewerFrame();
            if (index < arguments.length) {
                mainFrame.getJSVGCanvas().loadSVGDocument
                    (new File(arguments[index]).toURL().toString());
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
         */
        void handleOption();

        /**
         * Returns the option description.
         */
        String getDescription();
    }

    /**
     * To handle the '-font-size' option.
     */
    protected class FontSizeHandler implements OptionHandler {
        public void handleOption() {
            index++;
            int size = Integer.parseInt(arguments[index++]);

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
        viewerFrames++;
        mainFrame = new JSVGViewerFrame(this);
        mainFrame.setSize(resources.getInteger("Frame.width"),
                          resources.getInteger("Frame.height"));
        mainFrame.setIconImage(frameIcon.getImage());
        mainFrame.setTitle(resources.getString("Frame.title"));
        mainFrame.show();
        return mainFrame;
    }

    /**
     * Closes the given viewer frame.
     */
    public void closeJSVGViewerFrame(JSVGViewerFrame f) {
        if (--viewerFrames == 0) {
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
        return xmlParserClassName;
    }

}
