/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.image.BufferedImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JWindow;

import javax.swing.border.TitledBorder;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.batik.bridge.UserAgent;

import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XSLTransformer;
import org.apache.batik.gvt.event.EventDispatcher;

import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.gvt.event.AWTEventDispatcher;

import org.apache.batik.util.SVGFileFilter;
import org.apache.batik.util.DocumentEvent;
import org.apache.batik.util.DocumentLoadingEvent;
import org.apache.batik.util.DocumentPropertyEvent;
import org.apache.batik.util.DocumentListener;
import org.apache.batik.util.DocumentLoadRunnable;

import org.apache.batik.util.gui.DOMViewer;
import org.apache.batik.util.gui.LanguageChangeHandler;
import org.apache.batik.util.gui.LanguageDialog;
import org.apache.batik.util.gui.LocationBar;
import org.apache.batik.util.gui.MemoryMonitor;
import org.apache.batik.util.gui.URIChooser;
import org.apache.batik.util.gui.UserStyleDialog;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.JComponentModifier;
import org.apache.batik.util.gui.resource.MenuFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;
import org.apache.batik.util.gui.resource.ToolBarFactory;

import org.xml.sax.InputSource;

import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGAElement;

/**
 * This class represents a viewer frame.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class ViewerFrame
    extends    JFrame
    implements ActionMap,
               UserAgent,
               LanguageChangeHandler,
               UserStyleDialog.ChangeHandler,
               JSVGCanvas.ZoomHandler,
               DocumentListener {

    // The actions names.
    public final static String OPEN_ACTION        = "OpenAction";
    public final static String OPEN_PAGE_ACTION   = "OpenPageAction";
    public final static String NEW_WINDOW_ACTION  = "NewWindowAction";
    public final static String EXPORT_PNG_ACTION  = "ExportPNGAction";
    public final static String EXPORT_JPG_ACTION  = "ExportJPGAction";
    public final static String RELOAD_ACTION      = "ReloadAction";
    public final static String BACK_ACTION        = "BackAction";
    public final static String FORWARD_ACTION     = "ForwardAction";
    public final static String CLOSE_ACTION       = "CloseAction";
    public final static String EXIT_ACTION        = "ExitAction";
    public final static String FULLSCREEN_ACTION  = "FullscreenAction";
    public final static String SOURCE_ACTION      = "SourceAction";
    public final static String DESCRIPTION_ACTION = "DescriptionAction";
    public final static String TREE_ACTION        = "TreeAction";
    public final static String THUMBNAIL_ACTION   = "ThumbnailAction";
    public final static String STOP_ACTION        = "StopAction";
    public final static String FIXED_SIZE_ACTION  = "FixedSizeAction";
    public final static String PROG_PAINT_ACTION  = "ProgressivePaintAction";
    public final static String LANGUAGE_ACTION    = "LanguageAction";
    public final static String USER_STYLE_ACTION  = "UserStyleAction";
    public final static String MONITOR_ACTION     = "MonitorAction";
    public final static String ABOUT_ACTION       = "AboutAction";
    public final static String PRINT_ACTION       = "PrintAction";

    /**
     * The default cursor.
     */
    protected final static Cursor DEFAULT_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The wait cursor.
     */
    protected final static Cursor WAIT_CURSOR =
        new Cursor(Cursor.WAIT_CURSOR);

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgviewer.resources.GUI";

    /**
     * The memory monitor frame.
     */
    protected static JFrame memoryMonitor;

    /**
     * The about frame.
     */
    protected static JFrame aboutFrame;

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager resources;
    static {
        bundle    = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        resources = new ResourceManager(bundle);
    }

    /**
     * The input buffer
     */
    protected static char [] buffer = new char[4096];

    /**
     * The application.
     */
    protected Application application;

    /**
     * The location bar.
     */
    protected LocationBar locationBar;

    /**
     * The URI of the current document.
     */
    protected String uri;

    /**
     * The current path.
     */
    protected String currentPath = ".";

    /**
     * The current path where to export files.
     */
    protected String currentExportPath = ".";

    /**
     * The uri chooser.
     */
    protected URIChooser uriChooser;

    /**
     * The view panel.
     */
    protected JPanel panel;

    /**
     * The status bar.
     */
    protected StatusBar statusBar;

    /**
     * The SVG canvas.
     */
    protected JSVGCanvas canvas;

    /**
     * The current processing thread
     */
    protected Thread thread;

    /**
     * The tree view panel.
     */
    protected DOMViewer domViewer = new DOMViewer();

    /**
     * The language dialog. It is private now to allow
     * dynamic building (very long to build). You can
     * get it from subclasses with getLanguageDialog().
     */
    private LanguageDialog languageDialog;

    /**
     * The user style dialog.
     */
    protected UserStyleDialog userStyleDialog;

    /**
     * The thumbnail frame.
     */
    protected JFrame thumbnailFrame;

    /**
     * The reload action
     */
    protected ReloadAction reloadAction = new ReloadAction();

    /**
     * The back action
     */
    protected BackAction backAction = new BackAction();

    /**
     * The forward action
     */
    protected ForwardAction forwardAction = new ForwardAction();

    /**
     * The stop action
     */
    protected StopAction stopAction = new StopAction();

    /**
     * The document description.
     */
    protected String description = "";

    /**
     * Has the windows a fixed size?
     */
    protected boolean fixedSize;

    /**
     * Is incremental painting of the offscreen buffer enabled?
     */
    protected boolean progressivePaintEnabled;

    /**
     * The event dispatcher.
     */
    private AWTEventDispatcher eventDispatcher;

    /**
     * The user languages.
     */
    protected String userLanguages = "en";

    /**
     * The user style sheet URI.
     */
    protected String userStyleSheetURI;

    /**
     * The documents loaded with this viewer.
     */
    protected List loadedDocuments = new ArrayList();

    /**
     * The current document index.
     */
    protected int loadedDocument = -1;

    /**
     * The loaded documents count.
     */
    protected int loadedDocumentsCount;


    /**
     * The factory that creates new SVG Document instances.
     */
    private SVGDocumentFactory df;

    /**
     * Creates a new ViewerFrame object.
     * @param a The current application.
     */
    public ViewerFrame(Application a) {
        application = a;

        if (!Locale.getDefault().getLanguage().equals(userLanguages))
            userLanguages=Locale.getDefault().getLanguage()+","+
                userLanguages;

        setTitle(resources.getString("Frame.title"));
        setSize(resources.getInteger("Frame.width"),
                resources.getInteger("Frame.height"));
        URL url = getClass().getResource(resources.getString("Frame.icon"));
        setIconImage(new ImageIcon(url).getImage());
        df = new SAXSVGDocumentFactory(application.getXMLParserClassName());

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    close();
                }
            });

        uriChooser = new URIChooser(this, new URIChooserOKAction());
        uriChooser.setFileFilter(new SVGFileFilter());

        // Create the SVG canvas.
        canvas = new JSVGCanvas(this);

        eventDispatcher =
            new AWTEventDispatcher(
                canvas.getRendererFactory().getRenderContext());

        listeners.put(OPEN_ACTION,        new OpenAction());
        listeners.put(OPEN_PAGE_ACTION,   new OpenPageAction());
        listeners.put(NEW_WINDOW_ACTION,  new NewWindowAction());
        listeners.put(EXPORT_PNG_ACTION,  new ExportPNGAction());
        listeners.put(EXPORT_JPG_ACTION,  new ExportJPGAction());
        listeners.put(RELOAD_ACTION,      reloadAction);
        listeners.put(BACK_ACTION,        backAction);
        listeners.put(FORWARD_ACTION,     forwardAction);
        listeners.put(CLOSE_ACTION,       application.createCloseAction(this));
        listeners.put(EXIT_ACTION,        application.createExitAction());
        listeners.put(FULLSCREEN_ACTION,  new FullscreenAction());
        listeners.put(SOURCE_ACTION,      new SourceAction());
        listeners.put(DESCRIPTION_ACTION, new DescriptionAction());
        listeners.put(TREE_ACTION,        new TreeAction());
        listeners.put(THUMBNAIL_ACTION,   new ThumbnailAction());
        listeners.put(STOP_ACTION,        stopAction);
        listeners.put(PROG_PAINT_ACTION,  new ProgressivePaintAction());
        listeners.put(FIXED_SIZE_ACTION,  new FixedSizeAction());
        listeners.put(LANGUAGE_ACTION,    new LanguageAction());
        listeners.put(USER_STYLE_ACTION,  new UserStyleAction());
        listeners.put(MONITOR_ACTION,     new MonitorAction());
        listeners.put(ABOUT_ACTION,       new AboutAction());
        listeners.put(PRINT_ACTION,       new PrintAction());

        JPanel p = null;
        try {
            // Create the menu
            MenuFactory mf = new MenuFactory(bundle, this);
            setJMenuBar(mf.createJMenuBar("MenuBar"));

            // Create the toolbar
            p = new JPanel(new BorderLayout());
            ToolBarFactory tbf = new ToolBarFactory(bundle, this);
            JToolBar tb = tbf.createJToolBar("ToolBar");
            tb.setFloatable(false);
            getContentPane().add(p, BorderLayout.NORTH);
            p.add(tb, BorderLayout.NORTH);
        } catch (MissingResourceException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // Create the location bar
        locationBar = new LocationBar();
        locationBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 1));
        locationBar.addActionListener(new LocationBarAction());

        p.add(locationBar, BorderLayout.SOUTH);
        p.add(new JSeparator(), BorderLayout.CENTER);

        // Create the view panel
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        getContentPane().add(panel, BorderLayout.CENTER);
        p = new JPanel(new BorderLayout());
        panel.add(p, BorderLayout.CENTER);

        // Create the status bar
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        // Create the language dialog
        /**
         * The dialog takes a very long time to be build
         * we speed up launching by doing it elsewhere
         * languageDialog = new LanguageDialog(this);
         * languageDialog.setLanguageChangeHandler(this);
         */
        // we want to do it only after init. As we don't
        // know if a file will be loaded just after init
        // the only simple means to be pretty sure to be
        // after is to put a timer.
        javax.swing.Timer timer =
            new javax.swing.Timer(15000,
                                  new InitLanguageDialog());
        timer.setRepeats(false);
        timer.start();
        timer = null;

        // Create the user style dialog
        userStyleDialog = new UserStyleDialog(this);
        userStyleDialog.setChangeHandler(this);

        panel.add(canvas, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        canvas.setZoomHandler(this);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    statusBar.setXPosition(e.getX());
                    statusBar.setYPosition(e.getY());
                }
            });
        canvas.addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    Dimension dim = canvas.getSize();
                    statusBar.setWidth(dim.width);
                    statusBar.setHeight(dim.height);
                }
            });
        canvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    Dimension dim = canvas.getSize();
                    statusBar.setWidth(dim.width);
                    statusBar.setHeight(dim.height);
                }
            });

            }

    /**
     * Utility class for deferred initialization of
     * LanguageDialog instance.
     */
    private static class InitLanguageDialog
        implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            Thread t = new Thread(new Runnable() {
                    public void run() {
                        LanguageDialog.Panel.
                            initCountryIcons();
                    }
                });
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            ((javax.swing.Timer)e.getSource()).stop();
        }
    }

    /**
     * Returns the instance of <code>LanguageDialog</code>
     * used by the <code>ViewerFrame</code>.
     */
    protected LanguageDialog getLanguageDialog()
    {
        if (languageDialog == null) {
            languageDialog = new LanguageDialog(this);
            languageDialog.setLanguageChangeHandler(this);
        }
        return languageDialog;
    }

    /**
     * Tells the viewer whether or not it must be set to the size
     * of the loaded documents.
     */
    public void setFixedSize(boolean b) {
        fixedSize = b;
        // !!! TODO
    }

    /**
     * Returns the fixedSize field value.
     */
    public boolean isFixedSize() {
        return fixedSize;
    }

    /**
     * Returns the current loading thread if one.
     */
    public Thread getLoadingThread() {
        return thread;
    }

    /**
     * Called when the language settings change.
     */
    public void languageChanged(String lang) {
        userLanguages = lang;
    }

    /**
     * Called when the user stylesheet has changed.
     */
    public void userStyleSheetURIChanged(String s) {
        userStyleSheetURI = s;
    }

    public void zoomChanged(float f) {
        statusBar.setZoom(f);
    }

    // UserAgent ///////////////////////////////////////////////////

    /**
     * Returns the default size of the viewport of this user agent.
     */
    public Dimension2D getViewportSize() {
        return canvas.getSize();
    }

    /**
     * Returns the <code>EventDispatcher</code> used by the
     * <code>UserAgent</code> to dispatch events on GVT.
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Displays an error message in the User Agent interface.
     */
    public void displayError(final String msg) {
        Runnable r = new Runnable() {
            public void run() {
                JOptionPane pane =
                    new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
                JDialog dialog = pane.createDialog(ViewerFrame.this, "ERROR");
                dialog.show();
            }
        };
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(r);
        } else {
            r.run();
        }
    }

    /**
     * Displays an error resulting from the specified Exception.
     */
    public void displayError(final Exception ex) {
        displayError(ex.getMessage());
    }

    /**
     * Displays a message in the User Agent interface.
     */
    public void displayMessage(String message) {
        statusBar.setMainMessage(message);
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        // return 0.3528f; // 72 dpi
        return 0.26458333333333333333333333333333f; // 96dpi
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
     * Opens a link.
     * @param elt The activated link element.
     */
    public void openLink(SVGAElement elt) {
        application.openLink(this, elt);
    }

    /**
     * Informs the user agent to change the cursor.
     * @param cursor the new cursor
     */
    public void setSVGCursor(Cursor cursor) {
        // XXX: immediate set,  but may not update "requested" value
        canvas.setCursor(cursor);
    }

    /**
     * Runs the given thread.
     */
    public synchronized void runThread(Thread t) {
        stopAction.update(true);
        reloadAction.update(false);
        thread = t;
        thread.start();
    }

    /**
     * Returns the class name of the XML parser.
     */
    public String getXMLParserClassName() {
        return application.getXMLParserClassName();
    }

    /**
     * Returns the <code>AffineTransform</code> currently
     * applied to the drawing by the UserAgent.
     */
    public AffineTransform getTransform() {
        return canvas.getTransform();
    }

    /**
     * Returns the location on the screen of the
     * client area in the UserAgent.
     */
    public Point getClientAreaLocationOnScreen() {
        return canvas.getLocationOnScreen();
    }

    /**
     * Loads the given document.
     * @param s The document name.
     */
    public void loadDocument(String s) {
        String old = uri;
        uri = s;
        File f = new File(uri);
        if (f.exists()) {
            if (f.isDirectory()) {
                uri = null;
            } else {
                uri = "file:" + uri;
            }
        }
        if (uri != null) {
            if (old != null) {
                URL prev = null;
                try {
                    prev = new URL(old);
                    if (prev.sameFile(new URL(uri))) {
                        manageFragmentIdentifier();
                        return;
                    }
                } catch (java.net.MalformedURLException e) {
                }
            }

            // interrupt any document load already underway
            if ((thread != null) && thread.isAlive()) {
                thread.interrupt();
            }
            loadedDocument++;
            if (loadedDocument == loadedDocuments.size()) {
                loadedDocuments.add(uri);
            } else {
                Object o = loadedDocuments.get(loadedDocument);
                if (!o.equals(uri)) {
                    loadedDocumentsCount = loadedDocument + 1;
                }
                loadedDocuments.set(loadedDocument, uri);
            }
            if (loadedDocumentsCount == loadedDocument) {
                loadedDocumentsCount++;
            }
            backAction.update();
            forwardAction.update();

            locationBar.setText(uri);
            Thread t = DocumentLoadRunnable.createLoaderThread(uri,
                                                               this,
                                                               df);
            runThread(t);
        }
    }

    protected void manageFragmentIdentifier() {
        URL u = null;
        try {
            u = new URL(uri);
        } catch (java.net.MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        String ref = u.getRef();
        if (ref == null) {
            canvas.setTransform(new java.awt.geom.AffineTransform());
        } else {
            org.apache.batik.parser.FragmentIdentifierParser p;
            p = new org.apache.batik.parser.FragmentIdentifierParser();
            FragmentIdentifierHandler h = new FragmentIdentifierHandler();
            p.setFragmentIdentifierHandler(h);

            try {
                p.parse(new java.io.StringReader(ref));

                canvas.setViewBox(h.x, h.y, h.width, h.height);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

    }


    /**
     * To manage fragment identifiers.
     */
    protected static class FragmentIdentifierHandler
        extends org.apache.batik.parser.DefaultFragmentIdentifierHandler {
        float x, y, width, height;
        public void viewBox(float x, float y, float width, float height)
            throws org.apache.batik.parser.ParseException {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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
        if (result == null) {
            result = canvas.getAction(key);
        }
        return result;
    }

    /**
     * Closes the frame.
     */
    public void close() {
        if (application.closeFrame(this)) {
            setVisible(false);
        }
    }

    /**
     * To manage the actions associated with the application.
     */
    public interface Application {
        /**
         * Called when a new viewer frame needs to be created.
         */
        void createAndShowViewerFrame();

        /**
         * Creates a close action.
         */
        Action createCloseAction(ViewerFrame vf);

        /**
         * Creates a exit action.
         */
        Action createExitAction();

        /**
         * Returns the XML parser class name.
         */
        String getXMLParserClassName();

        /**
         * Called when the frame will be closed.
         * @return true if the frame must be closed.
         */
        boolean closeFrame(ViewerFrame f);

        /**
         * Opens the given link.
         */
        void openLink(ViewerFrame f, SVGAElement elt);
    }

    // Actions //////////////////////////////////////////////////////////

    /**
     * The action associated with the 'OK' button of the URI chooser.
     */
    public class URIChooserOKAction extends AbstractAction {
        public URIChooserOKAction() {}
        public void actionPerformed(ActionEvent e) {
            loadDocument(uriChooser.getText());
        }
    }

    /**
     * To open a new document
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

            int choice = fileChooser.showOpenDialog(ViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    loadDocument(currentPath = f.getCanonicalPath());
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * To open a new document
     */
    public class OpenPageAction extends AbstractAction {
        public OpenPageAction() {}
        public void actionPerformed(ActionEvent e) {
            uriChooser.pack();

            Rectangle fr = getBounds();
            Dimension ud = uriChooser.getSize();
            uriChooser.setLocation(fr.x + (fr.width  - ud.width) / 2,
                                   fr.y + (fr.height - ud.height) / 2);

            uriChooser.show();
        }
    }

    /**
     * To create a new frame
     */
    public class NewWindowAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            application.createAndShowViewerFrame();
        }
    }

    /**
     * To save the current document as PNG.
     */
    public class ExportPNGAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(currentExportPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);

            int choice = fileChooser.showSaveDialog(ViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                BufferedImage buffer = canvas.getBuffer();
                // create a BufferedImage of the appropriate type
                int w = buffer.getWidth();
                int h = buffer.getHeight();
                final ImageTranscoder trans = new PNGTranscoder();
                trans.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                                         "org.apache.crimson.parser.XMLReaderImpl");
                final BufferedImage img = trans.createImage(w, h);
                // paint the buffer to the image
                Graphics2D g2d = img.createGraphics();
                g2d.drawImage(buffer, null, 0, 0);
                new Thread() {
                    public void run() {
                        try {
                            currentExportPath = f.getCanonicalPath();
                            OutputStream ostream =
                              new BufferedOutputStream(new FileOutputStream(f));
                            trans.writeImage(img,
                                             new TranscoderOutput(ostream));
                            ostream.flush();
                            ostream.close();
                            statusBar.setMessage(
                                resources.getString("Document.export"));
                        } catch (Exception ex) { }
                    }
                }.start();
            }
        }
    }

    /**
     * To save the current document as JPG.
     */
    public class ExportJPGAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(currentExportPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);

            int choice = fileChooser.showSaveDialog(ViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                BufferedImage buffer = canvas.getBuffer();
                // create a BufferedImage of the appropriate type
                int w = buffer.getWidth();
                int h = buffer.getHeight();
                final ImageTranscoder trans = new JPEGTranscoder();
                trans.addTranscodingHint(JPEGTranscoder.KEY_XML_PARSER_CLASSNAME,
                                         "org.apache.crimson.parser.XMLReaderImpl");
                final BufferedImage img = trans.createImage(w, h);
                // paint the buffer to the image
                Graphics2D g2d = img.createGraphics();
                g2d.drawImage(buffer, null, 0, 0);
                new Thread() {
                    public void run() {
                        try {
                            currentExportPath = f.getCanonicalPath();
                            OutputStream ostream =
                              new BufferedOutputStream(new FileOutputStream(f));
                            trans.writeImage(img, new TranscoderOutput(ostream));
                            ostream.flush();
                            ostream.close();
                            statusBar.setMessage(
                                resources.getString("Document.export"));
                        } catch (Exception ex) { }
                    }
                }.start();
            }
        }
    }

    /**
     * To reload the current document
     */
    public class ReloadAction extends    AbstractAction
                              implements JComponentModifier {
        java.util.List components = new LinkedList();
        public ReloadAction() {}
        public void actionPerformed(ActionEvent e) {
            if (uri != null) {
                Thread t =
                    DocumentLoadRunnable.createLoaderThread(uri,
                                                            ViewerFrame.this,
                                                            df);
                runThread(t);
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(true);
        }

        protected void update(boolean isLoadComplete) {
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(isLoadComplete);
            }
        }
    }

    /**
     * To go back to the previous document
     */
    public class BackAction extends    AbstractAction
                            implements JComponentModifier {
        java.util.List components = new LinkedList();
        public BackAction() {}
        public void actionPerformed(ActionEvent e) {
            loadedDocument-=2;
            loadDocument((String)loadedDocuments.get(loadedDocument+1));
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = loadedDocument > 0;
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(b);
            }
        }
    }

    /**
     * To go forward to the previous document
     */
    public class ForwardAction extends    AbstractAction
                               implements JComponentModifier {
        java.util.List components = new LinkedList();
        public ForwardAction() {}
        public void actionPerformed(ActionEvent e) {
            loadDocument((String)loadedDocuments.get(loadedDocument+1));
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = loadedDocument < loadedDocumentsCount - 1;
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(b);
            }
        }
    }

    private JWindow fullscreenWindow;
    private boolean isFullscreen = false;

    /**
     * Toggle the view of the current document in fullscreen.
     */
    protected class FullscreenAction extends AbstractAction
        implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {}

        public void keyPressed(KeyEvent e) {
            if (((e.getModifiers() & e.CTRL_MASK) != 0)){
                if (e.getKeyCode() == KeyEvent.VK_X) {
                    fullscreenWindow.dispose();
                    fullscreenWindow.getContentPane().remove(canvas);
                    panel.add(canvas, BorderLayout.CENTER);
                    show();
                    isFullscreen = false;
                }
                else if (e.getKeyCode() == KeyEvent.VK_1) {
                    canvas.getAction(JSVGCanvas.UNZOOM_ACTION).actionPerformed(null);
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            // create the fullscreen window
            if (fullscreenWindow == null) {
                fullscreenWindow = new JWindow();
                Toolkit tk = Toolkit.getDefaultToolkit();
                fullscreenWindow.setSize(tk.getScreenSize());
                //fullscreenWindow.setSize(new Dimension(400, 400));
                fullscreenWindow.addKeyListener(this);
            }
            panel.remove(canvas);
            fullscreenWindow.getContentPane().add(canvas,
                                                  BorderLayout.CENTER);
            dispose();
            fullscreenWindow.show();
            isFullscreen = true;
        }
    }


    /**
     * To view the current document source.
     */
    protected class SourceAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (uri == null) {
                return;
            }
            JFrame fr = new JFrame(uri);
            fr.setSize(resources.getInteger("Source.width"),
                       resources.getInteger("Source.height"));
            JTextArea ta  = new JTextArea();
            ta.setLineWrap(true);
            ta.setBackground(Color.lightGray);
            ta.setFont(new Font("monospaced", Font.PLAIN, 11));

            JScrollPane scroll = new JScrollPane();
            scroll.getViewport().add(ta);
            scroll.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            fr.getContentPane().add(scroll, BorderLayout.CENTER);

            Document  doc = new PlainDocument();
            InputStream is = null;
            try {
                URL    u  = new URL(uri);
                is = u.openStream();
                try {
                    is = new GZIPInputStream(is);
                } catch (IOException ex) {
                    is.close();
                    is = u.openStream();
                }

                try {
                    Reader in = new InputStreamReader(is, "Unicode");
                    int nch;
                    while ((nch = in.read(buffer, 0, buffer.length)) != -1) {
                        doc.insertString(doc.getLength(),
                                     new String(buffer, 0, nch), null);
                    }
                } catch (java.io.CharConversionException ioce) {
                    // try default encoding...
                    doc = new PlainDocument();
                    // because of a problem with mark() and reset(),
                    // we must re-open the stream :-(

                    is = u.openStream();
                    try {
                        is = new GZIPInputStream(is);
                    } catch (IOException ex) {
                        is.close();
                        is = u.openStream();
                    }

                    try {
                        Reader in = new InputStreamReader(is);
                        int nch;
                        while ((nch = in.read(buffer, 0, buffer.length))!=-1){
                            doc.insertString(doc.getLength(),
                                     new String(buffer, 0, nch), null);
                        }
                    } catch (Exception ex) {
                        // !!! TODO : dialog
                        System.err.println(ex.toString());
                    }
                }
                ta.setDocument(doc);
                ta.setEditable(false);
                fr.show();
            } catch (Exception ex) {
                // !!! TODO : dialog
                System.err.println(ex.toString());
            }
        }

    }

    /**
     * To display the description of the document
     */
    public class DescriptionAction extends AbstractAction {
        public DescriptionAction() {}
        public void actionPerformed(ActionEvent e) {
            if (description == null) {
                return;
            }
            JFrame    fr  = new JFrame(uri);
            fr.setSize(resources.getInteger("Description.width"),
                       resources.getInteger("Description.height"));
            JTextArea ta  = new JTextArea();
            ta.setLineWrap(true);
            ta.setBackground(Color.lightGray);
            ta.setFont(new Font("monospaced", Font.PLAIN, 10));

            JScrollPane scroll = new JScrollPane();
            scroll.getViewport().add(ta);
            scroll.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            fr.getContentPane().add(scroll, BorderLayout.CENTER);

            ta.setText(description);
            ta.setEditable(false);
            fr.show();
        }
    }

    /**
     * To display the tree view of the document
     */
    public class TreeAction extends AbstractAction {
        public TreeAction() {}
        public void actionPerformed(ActionEvent e) {
            Rectangle fr = getBounds();
            Dimension td = domViewer.getSize();
            domViewer.setLocation(fr.x + (fr.width  - td.width) / 2,
                                  fr.y + (fr.height - td.height) / 2);
            domViewer.show();
        }
    }

    /**
     * To display the thumbnail view of the document
     */
    public class ThumbnailAction extends AbstractAction {
        public ThumbnailAction() {}
        public void actionPerformed(ActionEvent e) {
            if (thumbnailFrame == null) {
                thumbnailFrame =
                    new JFrame(resources.getString("Thumbnail.title"));
                thumbnailFrame.setSize
                    (resources.getInteger("Thumbnail.width"),
                     resources.getInteger("Thumbnail.height"));

                thumbnailFrame.getContentPane().add(canvas.getThumbnail());

                URL url =
                    getClass().getResource(resources.getString("Frame.icon"));
                thumbnailFrame.setIconImage(new ImageIcon(url).getImage());
            }
            Rectangle fr = getBounds();
            Dimension td = thumbnailFrame.getSize();
            thumbnailFrame.setLocation(fr.x + (fr.width  - td.width) / 2,
                                       fr.y + (fr.height - td.height) / 2);
            thumbnailFrame.show();
        }
    }

    /**
     * To stop the current processing
     */
    public class StopAction extends    AbstractAction
                            implements JComponentModifier {
        java.util.List components = new LinkedList();
        public StopAction() {}
        public void actionPerformed(ActionEvent e) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
            update(false);
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update(boolean isRunning) {
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(isRunning);
            }
        }
    }

    /**
     * To print the current SVG document
     */
    public class PrintAction extends AbstractAction {
        public PrintAction() {}
        public void actionPerformed(ActionEvent e) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new Printable() {
                public int print(Graphics g, PageFormat pf, int i) {
                    if (i > 0) {
                        return Printable.NO_SUCH_PAGE;
                    } else {

                        canvas.paintComponent(g);
                        return Printable.PAGE_EXISTS;
                    }
                }});
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException pe) {;}
            }
        }
    }

    /**
     * To make the frame fit the SVG viewport
     */
    public class FixedSizeAction extends AbstractAction {
        public FixedSizeAction() {}
        public void actionPerformed(ActionEvent e) {
            fixedSize = ((JCheckBoxMenuItem)e.getSource()).isSelected();
        }
    }

    /**
     * To turn progressive rendering of the offscreen buffer on and off.
     */
    public class ProgressivePaintAction extends AbstractAction {
        public ProgressivePaintAction() {}
        public void actionPerformed(ActionEvent e) {
            progressivePaintEnabled = ((JCheckBoxMenuItem)e.getSource()).
                isSelected();
            canvas.setProgressiveRenderingEnabled(progressivePaintEnabled);
        }
    }

    /**
     * To show the language dialog.
     */
    public class LanguageAction extends AbstractAction {
        public LanguageAction() {}
        public void actionPerformed(ActionEvent e) {
            Rectangle fr = getBounds();
            LanguageDialog dialog = getLanguageDialog();
            Dimension ld = dialog.getSize();
            dialog.setLocation(fr.x + (fr.width  - ld.width) / 2,
                                       fr.y + (fr.height - ld.height) / 2);
            dialog.setLanguages(userLanguages);
            dialog.show();
        }
    }

    /**
     * To display the user style options dialog.
     */
    public class UserStyleAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            Rectangle fr = getBounds();
            Dimension sd = userStyleDialog.getSize();
            userStyleDialog.setLocation(fr.x + (fr.width  - sd.width) / 2,
                                        fr.y + (fr.height - sd.height) / 2);
            userStyleDialog.pack();
            userStyleDialog.show();
        }
    }

    /**
     * To display the memory monitor.
     */
    public class MonitorAction extends AbstractAction {
        public MonitorAction() {}
        public void actionPerformed(ActionEvent e) {
            if (memoryMonitor == null) {
                memoryMonitor = new MemoryMonitor();
            }
            memoryMonitor.show();
        }
    }

    /**
     * To display the about dialog.
     */
    public class AboutAction extends AbstractAction
        implements DocumentListener {
        JSVGCanvas canvas;
        public AboutAction() {}
        public void actionPerformed(ActionEvent e) {
            if (aboutFrame == null) {
                aboutFrame = new JFrame(resources.getString("About.title"));
                aboutFrame.setSize(resources.getInteger("About.width"),
                                   resources.getInteger("About.height"));
                JPanel panel = new JPanel(new BorderLayout());

                aboutFrame.getContentPane().add(panel);
                panel.setBorder(BorderFactory.createCompoundBorder
                                (BorderFactory.createTitledBorder
                                 (BorderFactory.createEtchedBorder(),
                                  resources.getString("About.border_title"),
                                  TitledBorder.CENTER,
                                  TitledBorder.DEFAULT_POSITION),
                                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));

                JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                ButtonFactory bf = new ButtonFactory(bundle, null);
                JButton button;
                p.add(button = bf.createJButton("AboutCloseButton"));
                button.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        aboutFrame.setVisible(false);
                    }
                });
                aboutFrame.getContentPane().add(p, BorderLayout.SOUTH);
                canvas = new JSVGCanvas(ViewerFrame.this);
                canvas.setBorder(BorderFactory.createLoweredBevelBorder());
                panel.add(canvas);

                String uri =
                    getClass().getResource("resources/authors.svg").toString();
                Thread t = DocumentLoadRunnable.createLoaderThread(uri,
                                                                   this,
                                                                   df);

                t.start();
            }
            Rectangle fr = getBounds();
            Dimension sd = aboutFrame.getSize();
            aboutFrame.setLocation(fr.x + (fr.width  - sd.width) / 2,
                                   fr.y + (fr.height - sd.height) / 2);
            aboutFrame.show();
        }

        /**
         * Take action on receipt of a DocumentEvent.
         */
        public void processDocumentEvent(DocumentEvent e) {
            if (e.classid == DocumentEvent.LOADING) {
                if (e.type == DocumentLoadingEvent.DONE) {
                    DefaultSVGContext dc = new DefaultSVGContext() {
                            public float getPixelToMM() {
                                return ViewerFrame.this.getPixelToMM();
                            }
                            public float getViewportWidth() {
                                return (float)canvas.getSize().getWidth();
                            }
                            public float getViewportHeight() {
                                return (float)canvas.getSize().getHeight();
                            }
                        };
                    SVGOMDocument doc = (SVGOMDocument)e.getValue();
                    doc.setSVGContext(dc);
                    canvas.setSVGDocument(doc);
                }
            } else if (e.classid == DocumentEvent.PROPERTY) {
                //
            }
        }
    }

    /**
     * To manage the location bar action
     */
    public class LocationBarAction extends AbstractAction {
        public LocationBarAction() {}
        public void actionPerformed(ActionEvent e) {
            String s = locationBar.getText().trim();
            if (!s.equals("") && !s.equals(uri)) {
                uri = s;
                File f = new File(uri);
                if (f.exists()) {
                    if (f.isDirectory()) {
                        uri = null;
                    } else {
                        uri = "file:" + uri;
                    }
                }
                if (uri != null) {
                    locationBar.setText(uri);
                    locationBar.addToHistory(uri);
                    Thread t =
                      DocumentLoadRunnable.createLoaderThread(uri,
                                                              ViewerFrame.this,
                                                              df);
                    runThread(t);
                }
            }
        }
    }

    /**
     * Take action on receipt of a document event.
     */
    public void processDocumentEvent(DocumentEvent e) {

        // perhaps keying on public field is faster than instanceof ?

        if (e.classid == DocumentEvent.LOADING) {
            processDocumentLoadingEvent((DocumentLoadingEvent)e);
        } else if (e.classid == DocumentEvent.PROPERTY) {
            processDocumentPropertyEvent((DocumentPropertyEvent)e);
        }
    }

    /**
     * Take action on receipt of a document loading event.
     */
    public void processDocumentLoadingEvent(DocumentLoadingEvent e) {
        switch (e.type) {
        case (DocumentLoadingEvent.START_LOADING):
            setCursor(WAIT_CURSOR);
            setSVGCursor(WAIT_CURSOR);
            canvas.setSVGDocument(null);
            reloadAction.update(false);
            stopAction.update(true);
            statusBar.setMainMessage(resources.getString("Document.loading"));
            break;
        case (DocumentLoadingEvent.LOADED):
            // New doc has been loaded, prepare for new view
            DefaultSVGContext dc = new DefaultSVGContext() {
                    public float getPixelToMM() {
                        return ViewerFrame.this.getPixelToMM();
                    }
                    public float getViewportWidth() {
                        return (float)canvas.getSize().getWidth();
                    }
                    public float getViewportHeight() {
                        return (float)canvas.getSize().getHeight();
                    }
                };
            SVGOMDocument doc = (SVGOMDocument) e.getValue();
            dc.setUserStyleSheetURI(userStyleSheetURI);
            doc.setSVGContext(dc);
            domViewer.setDocument(doc, (ViewCSS)doc.getDocumentElement());
            statusBar.setMainMessage(resources.getString("Document.creating"));
            break;
        case (DocumentLoadingEvent.DONE):
            doc = (SVGOMDocument) e.getValue();
            canvas.setSVGDocument(doc);
            setCursor(DEFAULT_CURSOR);
            stopAction.update(false);
            reloadAction.update(true);
            statusBar.setMainMessage("");
            statusBar.setMessage(resources.getString("Document.done"));
            break;
        case (DocumentLoadingEvent.LOAD_CANCELLED):
            setCursor(DEFAULT_CURSOR);
            stopAction.update(false);
            reloadAction.update(true); // in case we want to try again
            statusBar.setMainMessage("");
            statusBar.setMessage(resources.getString("Document.cancelled"));
            break;
        case (DocumentLoadingEvent.LOAD_FAILED):
            setCursor(DEFAULT_CURSOR);
            stopAction.update(false);
            reloadAction.update(true); // in case we want to try again
            statusBar.setMainMessage("");
            statusBar.setMessage(resources.getString("Document.failed"));
            displayError(e.getException());
        }
    }

    /**
     * Take action on receipt of a document property change.
     */
    public void processDocumentPropertyEvent(DocumentPropertyEvent e) {
        switch (e.type) {
        case (DocumentPropertyEvent.TITLE) :
            String title = (String) e.getValue();
            if (title.equals("")) {
                setTitle(resources.getString("Frame.title") + ": " +
                         resources.getString("Frame.no_title"));
            } else {
                setTitle(resources.getString("Frame.title") + ": " + title);
            }
            break;
        case (DocumentPropertyEvent.SIZE) :
            Dimension size = (Dimension) e.getValue();
            if (!canvas.getSize().equals(size)) {
                canvas.setPreferredSize(size);
                panel.invalidate();
                if (!fixedSize) {
                    pack();
                }
            }
            break;
        case (DocumentPropertyEvent.DESCRIPTION) :
            String desc = (String) e.getValue();
            if (desc.equals("")) {
                desc = resources.getString("Description.no_description");
            }
            description = desc;
            break;
        }
    }
}
