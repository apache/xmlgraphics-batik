/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgviewer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;

import org.apache.batik.css.CSSDocumentHandler;

import org.apache.batik.util.SwingInitializer;

import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;

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
        //
        // Break down args in case they have been concatenated
        //
        Vector argsVector = new Vector();
        int nArgs = args.length;
        for(int j=0; j<nArgs; j++){
            String arg = args[j];
            StringTokenizer st = new StringTokenizer(arg, " ");
            while(st.hasMoreTokens()){
                argsVector.addElement(st.nextToken());
            }
        }

        args = new String[argsVector.size()];
        for(int j=0; j<args.length; j++){
            args[j] = (String)argsVector.elementAt(j);
        }

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-sf")) {
                i++;
                Font font = new Font("Dialog", Font.PLAIN, 10);
                SwingInitializer.swingDefaultsFontInit(font);
            } else if (args[i].equals("-lnf")) {
                i++;
                if (i >= args.length) {
                    break;
                }
                try {
                    UIManager.setLookAndFeel(args[i]);
                } catch (Exception exc) {
                    System.err.println("Error loading L&F: " + exc);
                }
                i++;
            } else {
                break;
            }
        }
        if (i > 0) {
            String[] t = new String[args.length - i];
            for (int n = 0; n < t.length; n++) {
                t[n] = args[n + i];
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
                Thread.sleep(3000);
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

    /**
     * Opens the given link.
     */
    public void openLink(ViewerFrame f, SVGAElement elt) {
        String show = elt.getXlinkShow();
        String href = elt.getHref().getBaseVal();

        String docURL = ((SVGDocument)elt.getOwnerDocument()).getURL();
        URL uri = null;
        try {
            uri = new URL(new URL(docURL), href);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        if (show.length() > 0 && show.charAt(0) == 'n') {
            createAndShowViewerFrame();
            mainFrame.loadDocument(uri.toString());
        } else {
            f.loadDocument(uri.toString());
        }
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
