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
import java.io.File;
import java.io.Reader;

import java.net.URL;

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

import org.apache.batik.util.SVGFileFilter;
import org.apache.batik.util.SVGUtilities;

import org.apache.batik.util.gui.DOMViewer;
import org.apache.batik.util.gui.LocationBar;
import org.apache.batik.util.gui.MemoryMonitor;
import org.apache.batik.util.gui.URIChooser;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.JComponentModifier;
import org.apache.batik.util.gui.resource.MenuFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;
import org.apache.batik.util.gui.resource.ToolBarFactory;

import org.xml.sax.InputSource;

import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class represents a viewer frame.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ViewerFrame
    extends    JFrame
    implements ActionMap,
               UserAgent {
    // The actions names.
    public final static String OPEN_ACTION        = "OpenAction";
    public final static String OPEN_PAGE_ACTION   = "OpenPageAction";
    public final static String NEW_WINDOW_ACTION  = "NewWindowAction";
    public final static String RELOAD_ACTION      = "ReloadAction";
    public final static String CLOSE_ACTION       = "CloseAction";
    public final static String EXIT_ACTION        = "ExitAction";
    public final static String SOURCE_ACTION      = "SourceAction";
    public final static String DESCRIPTION_ACTION = "DescriptionAction";
    public final static String TREE_ACTION        = "TreeAction";
    public final static String STOP_ACTION        = "StopAction";
    public final static String FIXED_SIZE_ACTION  = "FixedSizeAction";
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
     * The reload action
     */
    protected ReloadAction reloadAction = new ReloadAction();

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
     * Is the windows has a fixed size?
     */
    protected boolean fixedSize;

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

        listeners.put(OPEN_ACTION,        new OpenAction());
        listeners.put(OPEN_PAGE_ACTION,   new OpenPageAction());
        listeners.put(NEW_WINDOW_ACTION,  new NewWindowAction());
        listeners.put(RELOAD_ACTION,      reloadAction);
        listeners.put(CLOSE_ACTION,       application.createCloseAction(this));
        listeners.put(EXIT_ACTION,        application.createExitAction());
        listeners.put(SOURCE_ACTION,      new SourceAction());
        listeners.put(DESCRIPTION_ACTION, new DescriptionAction());
        listeners.put(TREE_ACTION,        new TreeAction());
        listeners.put(STOP_ACTION,        stopAction);
        listeners.put(FIXED_SIZE_ACTION,  new FixedSizeAction());
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

        // Create the SVG canvas.
        canvas = new JSVGCanvas();
        //canvas.setSVGUserAgent(this);
        panel.add("Center", canvas);
        panel.revalidate();
        panel.repaint();
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

    // UserAgent ///////////////////////////////////////////////////

    /**
     * Returns the <code>EventDispatcher</code> used by the
     * <code>UserAgent</code> to dispatch events on GVT.
     */
    public EventDispatcher getEventDispatcher() {
        return null;
    }
    
    /**
     * Displays an error message in the User Agent interface.
     */
    public void displayError(String message) {
        System.out.println(message);
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        return 0.33f;
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
        return (Action)listeners.get(key);
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
                new JFileChooser((uri == null) ? "." : uri);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileFilter(new SVGFileFilter());

            int choice = fileChooser.showOpenDialog(ViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    loadDocument(f.getCanonicalPath());
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
     * To stop the current processing
     */
    public class StopAction extends    AbstractAction
                            implements JComponentModifier {
        java.util.List components = new LinkedList();
        public StopAction() {}
        public void actionPerformed(ActionEvent e) {
            thread.stop();
            //canvas.stopDocumentViewThread();
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
            ThreadDeath td = null;
            try {
                statusBar.setMainMessage
                    (resources.getString("Document.loading"));
                setCursor(WAIT_CURSOR);

                isRunning = true;
                reloadAction.update();
                stopAction.update();

                // Load the requested document.
                SVGOMDocument doc;

                long t1 = System.currentTimeMillis();
                
                SVGDocumentFactory df = new SVGDocumentFactory
                    (application.getXMLParserClassName());
                URL url = new URL(uri);
                InputStream is = url.openStream();
                try {
                    is = new GZIPInputStream(is);
                } catch (IOException e) {
                    is.close();
                    is = url.openStream();
                }
                Reader r = new InputStreamReader(is);

                doc = df.createDocument(documentURI, new InputSource(r));
                // !!! ???
                DefaultSVGContext dc = new DefaultSVGContext();
                dc.setUserAgent(ViewerFrame.this);
                doc.setSVGContext(dc);

                long t2 = System.currentTimeMillis();
                System.out.println("--------------------------------");
                System.out.println(" Document loading time: " +
                                   (t2 - t1) + " ms");
                
                String title = doc.getTitle();
                if (title.equals("")) {
                    setTitle(resources.getString("Frame.title") + ": " +
                             resources.getString("Frame.no_title"));
                } else {
                    setTitle(resources.getString("Frame.title") + ": " + title);
                }

                domViewer.setDocument(doc, (ViewCSS)doc.getDocumentElement());

                statusBar.setMainMessage(resources.getString
                                         ("Document.creating"));

                // Set the panel preferred size.
                SVGSVGElement elt = doc.getRootElement();
                float w = elt.getWidth().getBaseVal().getValue();
                float h = elt.getHeight().getBaseVal().getValue();
                canvas.setPreferredSize(new Dimension((int)w, (int)h));
                panel.invalidate();
                if (!fixedSize) {
                    pack();
                }

                canvas.setSVGDocument(doc);

                t1 = System.currentTimeMillis();
                System.out.println("--------------------------------");
                System.out.println(" Tree construction time: " +
                                   (t1 - t2) + " ms");
                
                description
                    =  SVGUtilities.getDescription(doc.getRootElement());
                if (description.equals("")) {
                    description
                        = resources.getString("Description.no_description");
                }
                
            } catch (ThreadDeath e) {
                td = e;
            } catch (Exception e) {
                e.printStackTrace();
            }

            setCursor(DEFAULT_CURSOR);
            isRunning = false;
            stopAction.update();
            reloadAction.update();
            statusBar.setMainMessage("");
            statusBar.setMessage(resources.getString("Document.done"));

            if (td != null) {
                throw td;
            }
        }
    }
}
