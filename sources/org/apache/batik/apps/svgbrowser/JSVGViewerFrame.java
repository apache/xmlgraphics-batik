/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.swing.gvt.AbstractImageZoomInteractor;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.AbstractRotateInteractor;
import org.apache.batik.swing.gvt.AbstractZoomInteractor;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;

import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderListener;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderListener;
import org.apache.batik.swing.svg.SVGFileFilter;
import org.apache.batik.swing.svg.SVGUserAgent;

import org.apache.batik.util.gui.DOMViewer;
import org.apache.batik.util.gui.LanguageChangeHandler;
import org.apache.batik.util.gui.LanguageDialog;
import org.apache.batik.util.gui.LocationBar;
import org.apache.batik.util.gui.MemoryMonitor;
import org.apache.batik.util.gui.UserStyleDialog;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.JComponentModifier;
import org.apache.batik.util.gui.resource.MenuFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;
import org.apache.batik.util.gui.resource.ToolBarFactory;

import org.w3c.dom.css.ViewCSS;

import org.w3c.dom.svg.SVGDocument;

/**
 * This class represents a SVG viewer swing frame.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGViewerFrame
    extends    JFrame
    implements ActionMap,
               SVGDocumentLoaderListener,
               GVTTreeBuilderListener,
               GVTTreeRendererListener {

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.GUI";

    // The actions names.
    public final static String OPEN_ACTION = "OpenAction";
    public final static String RELOAD_ACTION = "ReloadAction";
    public final static String CLOSE_ACTION = "CloseAction";
    public final static String EXIT_ACTION = "ExitAction";
    public final static String RESET_TRANSFORM_ACTION = "ResetTransformAction";
    public final static String STOP_ACTION = "StopAction";
    //public final static String DOUBLE_BUFFER_ACTION = "DoubleBufferAction";
    public final static String SHOW_DEBUG_ACTION = "ShowDebugAction";
    public final static String SHOW_RENDERING_ACTION = "ShowRenderingAction";
    public final static String LANGUAGE_ACTION = "LanguageAction";
    public final static String STYLE_SHEET_ACTION = "StyleSheetAction";
    public final static String MONITOR_ACTION = "MonitorAction";
    public final static String DOM_VIEWER_ACTION = "DOMViewerAction";

    /**
     * The cursor indicating that an operation is pending.
     */
    public final static Cursor WAIT_CURSOR =
        new Cursor(Cursor.WAIT_CURSOR);

    /**
     * The default cursor.
     */
    public final static Cursor DEFAULT_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

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
     * The current application.
     */
    protected Application application;

    /**
     * The JSVGComponent.
     */
    protected JSVGComponent svgComponent;

    /**
     * The memory monitor frame.
     */
    protected static JFrame memoryMonitorFrame;

    /**
     * The current path.
     */
    protected String currentPath = ".";

    /**
     * The stop action
     */
    protected StopAction stopAction = new StopAction();

    /**
     * The debug flag.
     */
    protected boolean debug;

    /**
     * The SVG user agent.
     */
    protected SVGUserAgent userAgent = new UserAgent();

    /**
     * The current document.
     */
    protected SVGDocument svgDocument;

    /**
     * The DOM viewer.
     */
    protected DOMViewer domViewer;

    /**
     * The language dialog.
     */
    protected LanguageDialog languageDialog;

    /**
     * The user style dialog.
     */
    protected UserStyleDialog styleSheetDialog;

    /**
     * The location bar.
     */
    protected LocationBar locationBar;

    /**
     * The status bar.
     */
    protected StatusBar statusBar;

    /**
     * The user languages.
     */
    protected String userLanguages = "en";

    /**
     * The user style sheet URI.
     */
    protected String userStyleSheetURI;

    /**
     * The initial transform applied to the document.
     */
    protected AffineTransform initialTransform;

    /**
     * The initial frame title.
     */
    protected String title;

    /**
     * Creates a new SVG viewer frame.
     */
    public JSVGViewerFrame(Application app) {
        application = app;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                application.closeJSVGViewerFrame(JSVGViewerFrame.this);
            }
        });

        listeners.put(OPEN_ACTION, new OpenAction());
        listeners.put(RELOAD_ACTION, new ReloadAction());
        listeners.put(CLOSE_ACTION, new CloseAction());
        listeners.put(EXIT_ACTION, application.createExitAction(this));
        listeners.put(RESET_TRANSFORM_ACTION, new ResetTransformAction());
        listeners.put(STOP_ACTION, stopAction);
        //listeners.put(DOUBLE_BUFFER_ACTION, new DoubleBufferAction());
        listeners.put(SHOW_DEBUG_ACTION, new ShowDebugAction());
        listeners.put(SHOW_RENDERING_ACTION, new ShowRenderingAction());
        listeners.put(LANGUAGE_ACTION, new LanguageAction());
        listeners.put(STYLE_SHEET_ACTION, new StyleSheetAction());
        listeners.put(MONITOR_ACTION, new MonitorAction());
        listeners.put(DOM_VIEWER_ACTION, new DOMViewerAction());

        JPanel p = null;
        try {
            // Create the menu
            MenuFactory mf = new MenuFactory(bundle, this);
            setJMenuBar(mf.createJMenuBar("MenuBar"));

            p = new JPanel(new BorderLayout());
            // Create the toolbar
            ToolBarFactory tbf = new ToolBarFactory(bundle, this);
            JToolBar tb = tbf.createJToolBar("ToolBar");
            tb.setFloatable(false);
            getContentPane().add(p, BorderLayout.NORTH);
            p.add(tb, BorderLayout.NORTH);
            p.add(new javax.swing.JSeparator(), BorderLayout.CENTER);
            p.add(locationBar = new LocationBar(), BorderLayout.SOUTH);

        } catch (MissingResourceException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBorder(BorderFactory.createEtchedBorder());

        p2.add(svgComponent = new JSVGComponent(userAgent, true, true),
               BorderLayout.CENTER);
        p = new JPanel(new BorderLayout());
        p.add(p2, BorderLayout.CENTER);
        p.add(statusBar = new StatusBar(), BorderLayout.SOUTH);

        getContentPane().add(p, BorderLayout.CENTER);

        svgComponent.addSVGDocumentLoaderListener(this);
        svgComponent.addGVTTreeBuilderListener(this);
        svgComponent.addGVTTreeRendererListener(this);

        svgComponent.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (svgDocument == null) {
                        statusBar.setXPosition(e.getX());
                        statusBar.setYPosition(e.getY());
                    } else {
                        try {
                            AffineTransform at = svgComponent.getRenderingTransform();
                            if (at != null) {
                                at = at.createInverse();
                                Point2D p2d =
                                    at.transform(new Point2D.Float(e.getX(), e.getY()),
                                                 null);
                                statusBar.setXPosition((float)p2d.getX());
                                statusBar.setYPosition((float)p2d.getY());
                                return;
                            }
                        } catch (NoninvertibleTransformException ex) {
                        }
                        statusBar.setXPosition(e.getX());
                        statusBar.setYPosition(e.getY());
                    }
                }
            });
        svgComponent.addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    Dimension dim = getSize();
                    if (svgDocument == null) {
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    } else {
                        try {
                            AffineTransform at = svgComponent.getRenderingTransform();
                            if (at != null) {
                                at = at.createInverse();
                                Point2D o =
                                    at.transform(new Point2D.Float(0, 0),
                                                 null);
                                Point2D p2d =
                                    at.transform(new Point2D.Float(dim.width,
                                                                   dim.height),
                                                 null);
                                statusBar.setWidth((float)(p2d.getX() - o.getX()));
                                statusBar.setHeight((float)(p2d.getY() - o.getY()));
                                return;
                            }
                        } catch (NoninvertibleTransformException ex) {
                        }
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    }
                }
            });
        svgComponent.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    Dimension dim = getSize();
                    if (svgDocument == null) {
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    } else {
                        try {
                            AffineTransform at = svgComponent.getRenderingTransform();
                            if (at != null) {
                                at = at.createInverse();
                                Point2D o =
                                    at.transform(new Point2D.Float(0, 0),
                                                 null);
                                Point2D p2d =
                                    at.transform(new Point2D.Float(dim.width,
                                                                   dim.height),
                                                 null);
                                statusBar.setWidth((float)(p2d.getX() - o.getX()));
                                statusBar.setHeight((float)(p2d.getY() - o.getY()));
                                return;
                            }
                        } catch (NoninvertibleTransformException ex) {
                        }
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    }
                }
            });

        locationBar.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String s = locationBar.getText().trim();
                if (!s.equals("")) {
                    File f = new File(s);
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            s = null;
                        } else {
                            s = "file:" + s;
                        }
                    }
                    if (s != null) {
                        if (svgDocument != null) {
                            try {
                                URL docURL = ((SVGOMDocument)svgDocument).getURLObject();
                                URL url = new URL(docURL, s);
                                if (docURL.equals(url)) {
                                    return;
                                }
                            } catch (MalformedURLException ex) {
                            }
                        }
                        locationBar.setText(s);
                        locationBar.addToHistory(s);
                        svgComponent.loadSVGDocument(s);
                    }
                }
            }
        });

        // Interactors initialization ///////////////////////////////////////

        svgComponent.getInteractors().add(new AbstractZoomInteractor() {
            public boolean startInteraction(InputEvent ie) {
                int mods = ie.getModifiers();
                return
                    ie.getID() == MouseEvent.MOUSE_PRESSED &&
                    (mods & ie.BUTTON1_MASK) != 0 &&
                    (mods & ie.CTRL_MASK) != 0;
            }
        });
        svgComponent.getInteractors().add(new AbstractImageZoomInteractor() {
            public boolean startInteraction(InputEvent ie) {
                int mods = ie.getModifiers();
                return
                    ie.getID() == MouseEvent.MOUSE_PRESSED &&
                    (mods & ie.BUTTON3_MASK) != 0 &&
                    (mods & ie.SHIFT_MASK) != 0;
            }
        });
        svgComponent.getInteractors().add(new AbstractPanInteractor() {
            public boolean startInteraction(InputEvent ie) {
                int mods = ie.getModifiers();
                return
                    ie.getID() == MouseEvent.MOUSE_PRESSED &&
                    (mods & ie.BUTTON1_MASK) != 0 &&
                    (mods & ie.SHIFT_MASK) != 0;
            }
        });
        svgComponent.getInteractors().add(new AbstractRotateInteractor() {
            public boolean startInteraction(InputEvent ie) {
                int mods = ie.getModifiers();
                return
                    ie.getID() == MouseEvent.MOUSE_PRESSED &&
                    (mods & ie.BUTTON3_MASK) != 0 &&
                    (mods & ie.CTRL_MASK) != 0;
            }
        });
        
    }

    /**
     * Returns the main JSVGComponent of this frame.
     */
    public JSVGComponent getJSVGComponent() {
        return svgComponent;
    }

    /**
     * To open a new document.
     */
    public class OpenAction extends AbstractAction {
        public OpenAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(currentPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileFilter(new SVGFileFilter());

            int choice = fileChooser.showOpenDialog(JSVGViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    currentPath = f.getCanonicalPath();
                    svgComponent.loadSVGDocument(f.toURL().toString());
                } catch (IOException ex) {
                    // !!! Error dialog
                }
            }
        }
    }

    /**
     * To close the last document.
     */
    public class CloseAction extends AbstractAction {
        public CloseAction() {}
        public void actionPerformed(ActionEvent e) {
            application.closeJSVGViewerFrame(JSVGViewerFrame.this);
        }
    }

    /**
     * To reload the last document.
     */
    public class ReloadAction extends AbstractAction {
        public ReloadAction() {}
        public void actionPerformed(ActionEvent e) {
            if (svgDocument != null) {
                String url = ((SVGOMDocument)svgDocument).getURLObject().toString();
                svgComponent.loadSVGDocument(url.toString());
            }
        }
    }

    /**
     * To reset the document transform.
     */
    public class ResetTransformAction extends AbstractAction {
        public ResetTransformAction() {}
        public void actionPerformed(ActionEvent e) {
            svgComponent.setRenderingTransform(initialTransform);
        }
    }

    /**
     * To stop the current processing.
     */
    public class StopAction extends    AbstractAction
                            implements JComponentModifier {
        java.util.List components = new LinkedList();
        public StopAction() {}
        public void actionPerformed(ActionEvent e) {
            svgComponent.stopProcessing();
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        public void update(boolean enabled) {
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(enabled);
            }
        }
    }

    /**
     * To enable the double buffering.
     */
    public class DoubleBufferAction extends AbstractAction {
        public DoubleBufferAction() {}
        public void actionPerformed(ActionEvent e) {
            svgComponent.setDoubleBufferedRendering
                (((JCheckBoxMenuItem)e.getSource()).isSelected());
        }
    }

    /**
     * To enable the debug traces.
     */
    public class ShowDebugAction extends AbstractAction {
        public ShowDebugAction() {}
        public void actionPerformed(ActionEvent e) {
            debug = ((JCheckBoxMenuItem)e.getSource()).isSelected();
        }
    }

    /**
     * To enable progressive rendering.
     */
    public class ShowRenderingAction extends AbstractAction {
        public ShowRenderingAction() {}
        public void actionPerformed(ActionEvent e) {
            svgComponent.setProgressivePaint
                (((JCheckBoxMenuItem)e.getSource()).isSelected());
        }
    }

    /**
     * To show the language dialog.
     */
    public class LanguageAction extends AbstractAction {
        public LanguageAction() {}
        public void actionPerformed(ActionEvent e) {
            if (languageDialog == null) {
                languageDialog = new LanguageDialog(JSVGViewerFrame.this);
                languageDialog.setLanguageChangeHandler
                    (new LanguageChangeHandler() {
                        public void languageChanged(String lang) {
                            userLanguages = lang;
                        }
                    });
                Rectangle fr = getBounds();
                Dimension ld = languageDialog.getSize();
                languageDialog.setLocation(fr.x + (fr.width  - ld.width) / 2,
                                           fr.y + (fr.height - ld.height) / 2);
                languageDialog.setLanguages(userLanguages);
            }
            languageDialog.show();
        }
    }

    /**
     * To display the memory monitor.
     */
    public class MonitorAction extends AbstractAction {
        public MonitorAction() {}
        public void actionPerformed(ActionEvent e) {
            if (memoryMonitorFrame == null) {
                memoryMonitorFrame = new MemoryMonitor();
                Rectangle fr = getBounds();
                Dimension md = memoryMonitorFrame.getSize();
                memoryMonitorFrame.setLocation(fr.x + (fr.width  - md.width) / 2,
                                               fr.y + (fr.height - md.height) / 2);
            }
            memoryMonitorFrame.show();
        }
    }

    /**
     * To display the user style options dialog.
     */
    public class StyleSheetAction extends AbstractAction {
        public StyleSheetAction() {}
        public void actionPerformed(ActionEvent e) {
            if (styleSheetDialog == null) {
                styleSheetDialog = new UserStyleDialog(JSVGViewerFrame.this);
                styleSheetDialog.setChangeHandler
                    (new UserStyleDialog.ChangeHandler() {
                        public void userStyleSheetURIChanged(String s) {
                            userStyleSheetURI = s;
                        }
                    });
                styleSheetDialog.pack();
                Rectangle fr = getBounds();
                Dimension sd = styleSheetDialog.getSize();
                styleSheetDialog.setLocation(fr.x + (fr.width  - sd.width) / 2,
                                             fr.y + (fr.height - sd.height) / 2);
            }
            styleSheetDialog.pack();
            styleSheetDialog.show();
        }
    }

    /**
     * To display the DOM viewer of the document
     */
    public class DOMViewerAction extends AbstractAction {
        public DOMViewerAction() {}
        public void actionPerformed(ActionEvent e) {
            if (domViewer == null) {
                domViewer = new DOMViewer();
                if (svgDocument != null) {
                    domViewer.setDocument(svgDocument,
                                          (ViewCSS)svgDocument.getDocumentElement());
                }
                Rectangle fr = getBounds();
                Dimension td = domViewer.getSize();
                domViewer.setLocation(fr.x + (fr.width  - td.width) / 2,
                                      fr.y + (fr.height - td.height) / 2);
            }
            domViewer.show();
        }
    }

    // ActionMap /////////////////////////////////////////////////////

    /**
     * The map that contains the action listeners
     */
    protected Map listeners = new HashMap();

    /**
     * Returns the action associated with the given string
     * or null on error
     * @param key the key mapped with the action to get
     * @throws MissingListenerException if the action is not found
     */
    public Action getAction(String key) throws MissingListenerException {
        Action result = (Action)listeners.get(key);
        //if (result == null) {
        //result = canvas.getAction(key);
        //}
        if (result == null) {
            throw new MissingListenerException("Can't find action.", RESOURCES, key);
        }
        return result;
    }

    // SVGDocumentLoaderListener ///////////////////////////////////////////

    long time; // For debug.

    /**
     * Called when the loading of a document was started.
     */
    public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
        if (debug) {
            System.out.println("Load started...");
            time = System.currentTimeMillis();
        }
        statusBar.setMainMessage(resources.getString("Message.documentLoad"));
        stopAction.update(true);
        svgComponent.setCursor(WAIT_CURSOR);
    }

    /**
     * Called when the loading of a document was completed.
     */
    public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
        if (debug) {
            System.out.print("Load completed in ");
            System.out.println((System.currentTimeMillis() - time) + " ms");
        }
        svgDocument = e.getSVGDocument();
        if (domViewer != null) {
            if(domViewer.isVisible()) {
                domViewer.setDocument(svgDocument,
                                      (ViewCSS)svgDocument.getDocumentElement());
            } else {
                domViewer.dispose();
                domViewer = null;
            }
        }
        initialTransform = null;
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
        String s = ((SVGOMDocument)svgDocument).getURLObject().toString();
        locationBar.setText(s);
        if (title == null) {
            title = getTitle();
        }
        int i = s.lastIndexOf("/");
        if (i == -1) {
            setTitle(title + ":" + s);
        } else {
            setTitle(title + ":" + s.substring(i + 1));
        }
    }

    /**
     * Called when the loading of a document was cancelled.
     */
    public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
        if (debug) {
            System.out.println("Load cancelled");
        }
        statusBar.setMainMessage("");
        statusBar.setMessage(resources.getString("Message.documentCancelled"));
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Called when the loading of a document has failed.
     */
    public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
        if (debug) {
            System.out.println("Load failed");
        }
        statusBar.setMainMessage("");
        statusBar.setMessage(resources.getString("Message.documentFailed"));
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    // GVTTreeBuilderListener //////////////////////////////////////////////

    /**
     * Called when a build started.
     * The data of the event is initialized to the old document.
     */
    public void gvtBuildStarted(GVTTreeBuilderEvent e) {
        if (debug) {
            System.out.println("Build started..."); 
            time = System.currentTimeMillis();
        }
        statusBar.setMainMessage(resources.getString("Message.treeBuild"));
        stopAction.update(true);
        svgComponent.setCursor(WAIT_CURSOR);
    }

    /**
     * Called when a build was completed.
     */
    public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
        if (debug) {
            System.out.print("Build completed in ");
            System.out.println((System.currentTimeMillis() - time) + " ms");
        }
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Called when a build was cancelled.
     */
    public void gvtBuildCancelled(GVTTreeBuilderEvent e) {
        if (debug) {
            System.out.println("Build cancelled");
        }
        statusBar.setMainMessage("");
        statusBar.setMessage(resources.getString("Message.treeCancelled"));
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Called when a build failed.
     */
    public void gvtBuildFailed(GVTTreeBuilderEvent e) {
        if (debug) {
            System.out.println("Build failed");
        }
        statusBar.setMainMessage("");
        statusBar.setMessage(resources.getString("Message.treeFailed"));
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    // GVTTreeRendererListener /////////////////////////////////////////////

    /**
     * Called when a rendering is in its preparing phase.
     */
    public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.println("Rendering started...");
            time = System.currentTimeMillis();
        }
        stopAction.update(true);
        svgComponent.setCursor(WAIT_CURSOR);
        statusBar.setMainMessage(resources.getString("Message.treeRendering"));
    }

    /**
     * Called when a rendering started.
     */
    public void gvtRenderingStarted(GVTTreeRendererEvent e) {
        // Do nothing
    }

    /**
     * Called when a rendering was completed.
     */
    public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.print("Rendering completed in ");
            System.out.println((System.currentTimeMillis() - time) + " ms");
        }
        statusBar.setMainMessage("");
        statusBar.setMessage(resources.getString("Message.done"));
        if (initialTransform == null) {
            initialTransform = svgComponent.getRenderingTransform();
        }
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Called when a rendering was cancelled.
     */
    public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.println("Rendering cancelled");
        }
        statusBar.setMainMessage("");
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Called when a rendering failed.
     */
    public void gvtRenderingFailed(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.println("Rendering failed");
        }
        statusBar.setMainMessage("");
        stopAction.update(false);
        svgComponent.setCursor(DEFAULT_CURSOR);
    }

    /**
     * This class implements a SVG user agent.
     */
    protected class UserAgent implements SVGUserAgent {

        /**
         * Creates a new SVGUserAgent.
         */
        protected UserAgent() {
        }

        /**
         * Displays an error message.
         */
        public void displayError(String message) {
            System.err.println(message);
        }
    
        /**
         * Displays an error resulting from the specified Exception.
         */
        public void displayError(Exception ex) {
            ex.printStackTrace(System.err);
        }

        /**
         * Displays a message in the User Agent interface.
         * The given message is typically displayed in a status bar.
         */
        public void displayMessage(String message) {
            System.out.println(message);
        }

        /**
         * Returns a customized the pixel to mm factor.
         */
        public float getPixelToMM() {
            return 0.264583333333333333333f; // 72 dpi
        }

        /**
         * Returns the language settings.
         */
        public String getLanguages() {
            return userLanguages;
        }

        /**
         * Returns the user stylesheet uri.
         * @return null if no user style sheet was specified.
         */
        public String getUserStyleSheetURI() {
            return userStyleSheetURI;
        }

        /**
         * Returns the class name of the XML parser.
         */
        public String getXMLParserClassName() {
            return application.getXMLParserClassName();
        }

        /**
         * Opens a link in a new component.
         * @param doc The current document.
         * @param uri The document URI.
         */
        public void openLink(String uri) {
            application.openLink(uri);
        }
    }
}
