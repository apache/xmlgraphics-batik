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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.File;
import java.io.Reader;

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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.batik.bridge.UserAgent;

import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.event.EventDispatcher;

import org.apache.batik.refimpl.util.JSVGCanvas;
import org.apache.batik.refimpl.gvt.event.ConcreteEventDispatcher;

import org.apache.batik.util.SVGFileFilter;
import org.apache.batik.util.SVGUtilities;

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
 * @version $Id$
 */
public class ViewerFrame
    extends    JFrame
    implements ActionMap,
               UserAgent,
               LanguageChangeHandler,
               UserStyleDialog.ChangeHandler,
               JSVGCanvas.ZoomHandler {

    // The actions names.
    public final static String OPEN_ACTION        = "OpenAction";
    public final static String OPEN_PAGE_ACTION   = "OpenPageAction";
    public final static String NEW_WINDOW_ACTION  = "NewWindowAction";
    public final static String RELOAD_ACTION      = "ReloadAction";
    public final static String BACK_ACTION        = "BackAction";
    public final static String FORWARD_ACTION     = "ForwardAction";
    public final static String CLOSE_ACTION       = "CloseAction";
    public final static String EXIT_ACTION        = "ExitAction";
    public final static String SOURCE_ACTION      = "SourceAction";
    public final static String DESCRIPTION_ACTION = "DescriptionAction";
    public final static String TREE_ACTION        = "TreeAction";
    public final static String THUMBNAIL_ACTION   = "ThumbnailAction";
    public final static String STOP_ACTION        = "StopAction";
    public final static String FIXED_SIZE_ACTION  = "FixedSizeAction";
    public final static String LANGUAGE_ACTION    = "LanguageAction";
    public final static String USER_STYLE_ACTION  = "UserStyleAction";
    public final static String MONITOR_ACTION     = "MonitorAction";

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
     * The language dialog.
     */
    protected LanguageDialog languageDialog;

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
     * Is the document thread running?
     */
    protected boolean isRunning;

    /**
     * Has the windows a fixed size?
     */
    protected boolean fixedSize;

    /**
     * The event dispatcher.
     */
    private ConcreteEventDispatcher eventDispatcher =
        new ConcreteEventDispatcher();

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
     * Creates a new ViewerFrame object.
     * @param a The current application.
     */
    public ViewerFrame(Application a) {
        application = a;

        setTitle(resources.getString("Frame.title"));
        setSize(resources.getInteger("Frame.width"),
                resources.getInteger("Frame.height"));
        URL url = getClass().getResource(resources.getString("Frame.icon"));
        setIconImage(new ImageIcon(url).getImage());

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    close();
                }
            });

        uriChooser = new URIChooser(this, new URIChooserOKAction());
        uriChooser.setFileFilter(new SVGFileFilter());

        // Create the SVG canvas.
        canvas = new JSVGCanvas(this);

        listeners.put(OPEN_ACTION,        new OpenAction());
        listeners.put(OPEN_PAGE_ACTION,   new OpenPageAction());
        listeners.put(NEW_WINDOW_ACTION,  new NewWindowAction());
        listeners.put(RELOAD_ACTION,      reloadAction);
        listeners.put(BACK_ACTION,        backAction);
        listeners.put(FORWARD_ACTION,     forwardAction);
        listeners.put(CLOSE_ACTION,       application.createCloseAction(this));
        listeners.put(EXIT_ACTION,        application.createExitAction());
        listeners.put(SOURCE_ACTION,      new SourceAction());
        listeners.put(DESCRIPTION_ACTION, new DescriptionAction());
        listeners.put(TREE_ACTION,        new TreeAction());
        listeners.put(THUMBNAIL_ACTION,   new ThumbnailAction());
        listeners.put(STOP_ACTION,        stopAction);
        listeners.put(FIXED_SIZE_ACTION,  new FixedSizeAction());
        listeners.put(LANGUAGE_ACTION,    new LanguageAction());
        listeners.put(USER_STYLE_ACTION,  new UserStyleAction());
        listeners.put(MONITOR_ACTION,     new MonitorAction());

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
            getContentPane().add("North", p);
            p.add("North", tb);
        } catch (MissingResourceException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // Create the location bar
        locationBar = new LocationBar();
        locationBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 1));
        locationBar.addActionListener(new LocationBarAction());

        p.add("South", locationBar);
        p.add("Center", new JSeparator());

        // Create the view panel
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        getContentPane().add("Center", panel);
        p = new JPanel(new BorderLayout());
        panel.add("Center", p);

        // Create the status bar
        statusBar = new StatusBar();
        getContentPane().add("South", statusBar);

        // Create the language dialog
        languageDialog = new LanguageDialog(this);
        languageDialog.setLanguageChangeHandler(this);

        // Create the user style dialog
        userStyleDialog = new UserStyleDialog(this);
        userStyleDialog.setChangeHandler(this);

        panel.add("Center", canvas);
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
     * Returns the <code>EventDispatcher</code> used by the
     * <code>UserAgent</code> to dispatch events on GVT.
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Displays an error message in the User Agent interface.
     */
    public void displayError(String message) {
        System.err.println(message);
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
        return 0.33f;
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
        canvas.setCursor(cursor);
    }

    /**
     * Runs the given thread.
     */
    public void runThread(Thread t) {
        t.start();
    }

    /**
     * Loads the given document.
     * @param s The document name.
     */
    public void loadDocument(String s) {
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
            thread = new DocumentThread(uri);
            thread.start();
        }
    }

    // ActionMap /////////////////////////////////////////////////////

    /**
     * The map that contains the listeners
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
            hide();
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
     * To reload the current document
     */
    public class ReloadAction extends    AbstractAction
                              implements JComponentModifier {
        java.util.List components = new LinkedList();
        public ReloadAction() {}
        public void actionPerformed(ActionEvent e) {
            if (uri != null) {
                thread = new DocumentThread(uri);
                thread.start();
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(true);
        }

        protected void update() {
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(!isRunning);
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
            fr.getContentPane().add("Center", scroll);

            Document  doc = new PlainDocument();
            try {
                URL    u  = new URL(uri);
                InputStream is = u.openStream();
                try {
                    is = new GZIPInputStream(is);
                } catch (IOException ex) {
                    is.close();
                    is = u.openStream();
                }
                Reader in = new InputStreamReader(is);
                int nch;
                while ((nch = in.read(buffer, 0, buffer.length)) != -1) {
                    doc.insertString(doc.getLength(),
                                     new String(buffer, 0, nch), null);
                }
            } catch (Exception ex) {
                // !!! TODO : dialog
                System.err.println(ex.toString());
            }

            ta.setDocument(doc);
            ta.setEditable(false);
            fr.show();
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
            fr.getContentPane().add("Center", scroll);

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
	    /*
	     * Lines below removed because they are unsafe,
	     * (and were causing hangs);
	     * thread should stop itself by checking a
	     * flag instead - or, better, using Thread.interrupt()
	     * and calls to isInterrupted().
	     */
	    //thread.stop();
            //canvas.stopDocumentViewThread();
	    // TODO: replace class-scope flag with
	    // thread.setIsRunning(false); (etc.)
	    //thread.interrupt();
            isRunning = false;
            update();
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(isRunning);
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
     * To show the language dialog.
     */
    public class LanguageAction extends AbstractAction {
        public LanguageAction() {}
        public void actionPerformed(ActionEvent e) {
            Rectangle fr = getBounds();
            Dimension ld = languageDialog.getSize();
            languageDialog.setLocation(fr.x + (fr.width  - ld.width) / 2,
                                       fr.y + (fr.height - ld.height) / 2);
            languageDialog.setLanguages(userLanguages);
            languageDialog.show();
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
                    thread = new DocumentThread(uri);
                    thread.start();
                }
            }
        }
    }

    /**
     * The document loading thread.
     */
    protected class DocumentThread extends Thread {
        /**
         * The document URI.
         */
        protected String documentURI;

        /**
         * Creates a new thread.
         */
        public DocumentThread(String uri) {
            setPriority(Thread.MIN_PRIORITY);
            documentURI = uri;
        }

        /**
         * The thread main method.
         */
        public void run() {
            InterruptedException ie = null;
            try {
                statusBar.setMainMessage
                    (resources.getString("Document.loading"));
		sleep(0); // allows swap, checks for interrupt
                setCursor(WAIT_CURSOR);

                isRunning = true;
                reloadAction.update();
                stopAction.update();

                // Load the requested document.
                SVGOMDocument doc;

                long t1 = System.currentTimeMillis();

                SVGDocumentFactory df = new SVGDocumentFactory
                    (application.getXMLParserClassName());
		checkInterrupt(); 
                URL url = new URL(uri);
                InputStream is = url.openStream();
		checkInterrupt();
                try {
                    is = new GZIPInputStream(is);
                } catch (IOException e) {
                    is.close();
                    is = url.openStream();
                }
                Reader r = new InputStreamReader(is);
                
		checkInterrupt();
                doc = df.createDocument(documentURI, new InputSource(r));
                
                DefaultSVGContext dc = new DefaultSVGContext();
                dc.setUserAgent(ViewerFrame.this);
                dc.setUserStyleSheetURI(userStyleSheetURI);
                doc.setSVGContext(dc);

                long t2 = System.currentTimeMillis();
                System.out.println("--------------------------------");
                System.out.println(" Document loading time: " +
                                   (t2 - t1) + " ms");
                System.out.println("--------------------------------");

                String title = doc.getTitle();
                if (title.equals("")) {
                    setTitle(resources.getString("Frame.title") + ": " +
                             resources.getString("Frame.no_title"));
                } else {
                    setTitle(resources.getString("Frame.title") + ": " + title);
                }

		checkInterrupt();
                domViewer.setDocument(doc, (ViewCSS)doc.getDocumentElement());

                statusBar.setMainMessage(resources.getString
                                         ("Document.creating"));

                canvas.setSVGDocument(null);

                // Set the panel preferred size.
                SVGSVGElement elt = doc.getRootElement();
                float w = elt.getWidth().getBaseVal().getValue();
                float h = elt.getHeight().getBaseVal().getValue();
                canvas.setPreferredSize(new Dimension((int)w, (int)h));
                panel.invalidate();
                if (!fixedSize) {
                    pack();
                }

		checkInterrupt();
                canvas.setSVGDocument(doc);

                t1 = System.currentTimeMillis();
                System.out.println("--------------------------------");
                System.out.println(" Tree construction time: " +
                                   (t1 - t2) + " ms");
                System.out.println("--------------------------------");

                description
                    =  SVGUtilities.getDescription(doc.getRootElement());
                if (description.equals("")) {
                    description
                        = resources.getString("Description.no_description");
                }

            } catch (InterruptedException e) {
	        System.out.println("Document loading thread interrupted.");
                ie = e;
	    } catch (InterruptedIOException iioe) {
	        System.out.println("Interrupted during document I/O.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            setCursor(DEFAULT_CURSOR);
            isRunning = false;
            stopAction.update();
            reloadAction.update();
            statusBar.setMainMessage("");
            statusBar.setMessage(resources.getString("Document.done"));

            if (ie != null) {
	      // throw ie; Doesn't need to be rethrown, thread dies here.
            }
        }

      private void checkInterrupt() throws InterruptedException {
	  // TODO: use Thread.interrupt(), etc. instead of this flag.
	  // if (isInterrupted()) {
	  if (!isRunning) {
	    throw new InterruptedException();
	  }
      }
    }
}









