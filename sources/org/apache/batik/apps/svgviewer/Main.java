/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgviewer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.batik.css.CSSDocumentHandler;

import org.apache.batik.util.SwingInitializer;

/**
 * This class represents a SVG viewer.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Main implements ViewerFrame.Application {
    /**
     * The CSS parser class name key.
     */
    public final static String CSS_PARSER_CLASS_NAME_KEY =
        "org.w3c.css.sac.parser";

    /**
     * The XML parser class name key.
     */
    public final static String XML_PARSER_CLASS_NAME_KEY =
        "org.xml.sax.driver";

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgviewer.resources.Application";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;
    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
    }
    
    /**
     * The XML parser class name.
     */
    protected static String xmlParserClassName =
        bundle.getString(XML_PARSER_CLASS_NAME_KEY);

    /**
     * The program entry point.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-sf")) {
            Font font = new Font("Dialog", Font.PLAIN, 10);
            SwingInitializer.swingDefaultsFontInit(font);
            String[] t = new String[args.length - 1];
            for (int i = 0; i < t.length; i++) {
                t[i] = args[i + 1];
            }
            args = t;
        }

        CSSDocumentHandler.setParserClassName
            (bundle.getString(CSS_PARSER_CLASS_NAME_KEY));
        new Main(args);
    }

    /**
     * The number of main frames.
     */
    protected int mainFrames;

    /**
     * The main frame.
     */
    protected ViewerFrame mainFrame;

    /**
     * Creates a viewer application frame.
     * @param args The command-line arguments.
     */
    public Main(String[] args) {
        createAndShowViewerFrame();
        if (args.length > 0) {
            mainFrame.loadDocument(args[0]);
        } else {
            mainFrame.setFixedSize(true);
            mainFrame.loadDocument
                (Main.class.getResource("resources/usage.svg").toString());
            try {
                mainFrame.getLoadingThread().join();
            } catch (InterruptedException e) {
            }
            mainFrame.setFixedSize(false);
        }
    }

    // Application /////////////////////////////////////////////

    /**
     * Creates and shows a new viewer frame.
     */
    public void createAndShowViewerFrame() {
        mainFrames++;
        ViewerFrame vf = new ViewerFrame(this);
        vf.show();
        mainFrame = vf;
    }

    /**
     * Creates a close action.
     */
    public Action createCloseAction(ViewerFrame vf) {
        return new CloseAction(vf);
    }

    /**
     * Creates a exit action.
     */
    public Action createExitAction() {
        return new ExitAction();
    }

    /**
     * Returns the XML parser class name.
     */
    public String getXMLParserClassName() {
        return xmlParserClassName;
    }

    /**
     * Called when the frame will be closed.
     * @return true if the frame must be closed.
     */
    public boolean closeFrame(ViewerFrame f) {
        if (--mainFrames == 0) {
            System.exit(0);
        }
        return true;
    }

    // Actions /////////////////////////////////////////////////
    
    /**
     * To close a frame.
     */
    public class CloseAction extends AbstractAction {
        protected ViewerFrame viewerFrame;
        public CloseAction(ViewerFrame vf) { viewerFrame = vf; }
        public void actionPerformed(ActionEvent e) {
            if (closeFrame(viewerFrame)) {
                viewerFrame.hide();
            }
        }
    }

    /**
     * To exit the application
     */
    public class ExitAction extends AbstractAction {
        public ExitAction() {}
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
