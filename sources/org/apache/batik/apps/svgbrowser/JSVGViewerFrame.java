/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
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

import java.awt.image.BufferedImage;

import java.awt.print.PrinterException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import java.net.MalformedURLException;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import javax.swing.filechooser.FileFilter;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.swing.gvt.AbstractImageZoomInteractor;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.AbstractRotateInteractor;
import org.apache.batik.swing.gvt.AbstractZoomInteractor;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;

import org.apache.batik.swing.JSVGCanvas;

import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderListener;
import org.apache.batik.swing.svg.LinkActivationEvent;
import org.apache.batik.swing.svg.LinkActivationListener;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderListener;
import org.apache.batik.swing.svg.SVGFileFilter;
import org.apache.batik.swing.svg.SVGUserAgent;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

import org.apache.batik.transcoder.print.PrintTranscoder;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.util.gui.DOMViewer;
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

import org.apache.batik.ext.swing.JAffineTransformChooser;

import org.apache.batik.xml.XMLUtilities;

import org.w3c.dom.Element;

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
               GVTTreeRendererListener,
               LinkActivationListener {

    /**
     * The gui resources file name
     */
    public final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.GUI";

    // The actions names.
    public final static String ABOUT_ACTION = "AboutAction";
    public final static String OPEN_ACTION = "OpenAction";
    public final static String OPEN_LOCATION_ACTION = "OpenLocationAction";
    public final static String NEW_WINDOW_ACTION = "NewWindowAction";
    public final static String RELOAD_ACTION = "ReloadAction";
    public final static String BACK_ACTION = "BackAction";
    public final static String FORWARD_ACTION = "ForwardAction";
    public final static String PRINT_ACTION = "PrintAction";
    public final static String EXPORT_AS_JPG_ACTION = "ExportAsJPGAction";
    public final static String EXPORT_AS_PNG_ACTION = "ExportAsPNGAction";
    public final static String EXPORT_AS_TIFF_ACTION = "ExportAsTIFFAction";
    public final static String PREFERENCES_ACTION = "PreferencesAction";
    public final static String CLOSE_ACTION = "CloseAction";
    public final static String VIEW_SOURCE_ACTION = "ViewSourceAction";
    public final static String EXIT_ACTION = "ExitAction";
    public final static String RESET_TRANSFORM_ACTION = "ResetTransformAction";
    public final static String ZOOM_IN_ACTION = "ZoomInAction";
    public final static String ZOOM_OUT_ACTION = "ZoomOutAction";
    public final static String PREVIOUS_TRANSFORM_ACTION = "PreviousTransformAction";
    public final static String NEXT_TRANSFORM_ACTION = "NextTransformAction";
    public final static String STOP_ACTION = "StopAction";
    public final static String MONITOR_ACTION = "MonitorAction";
    public final static String DOM_VIEWER_ACTION = "DOMViewerAction";
    public final static String SET_TRANSFORM_ACTION = "SetTransformAction";
    public final static String FIND_DIALOG_ACTION = "FindDialogAction";
    public final static String THUMBNAIL_DIALOG_ACTION = "ThumbnailDialogAction";
    public final static String FLUSH_ACTION = "FlushAction";

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
     * The JSVGCanvas.
     */
    protected JSVGCanvas svgCanvas;

    /**
     * The memory monitor frame.
     */
    protected static JFrame memoryMonitorFrame;

    /**
     * The current path.
     */
    protected File currentPath = new File("");;

    /**
     * The current export path.
     */
    protected File currentExportPath = new File("");

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
     * The previous transform action
     */
    protected PreviousTransformAction previousTransformAction =
        new PreviousTransformAction();

    /**
     * The next transform action
     */
    protected NextTransformAction nextTransformAction =
        new NextTransformAction();

    /**
     * The debug flag.
     */
    protected boolean debug;

    /**
     * The auto adjust flag.
     */
    protected boolean autoAdjust = true;

    /**
     * The SVG user agent.
     */
    protected SVGUserAgent userAgent = new UserAgent();

    /**
     * The current document.
     */
    protected SVGDocument svgDocument;

    /**
     * The URI chooser.
     */
    protected URIChooser uriChooser;

    /**
     * The DOM viewer.
     */
    protected DOMViewer domViewer;

    /**
     * The Find dialog.
     */
    protected FindDialog findDialog;

    /**
     * The Find dialog.
     */
    protected ThumbnailDialog thumbnailDialog;

    /**
     * The transform dialog
     */
    protected JAffineTransformChooser.Dialog transformDialog;

    /**
     * The location bar.
     */
    protected LocationBar locationBar;

    /**
     * The status bar.
     */
    protected StatusBar statusBar;

    /**
     * The initial frame title.
     */
    protected String title;

    /**
     * The local history.
     */
    protected LocalHistory localHistory;

    /**
     * The transform history.
     */
    protected TransformHistory transformHistory = new TransformHistory();

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

        svgCanvas = new JSVGCanvas(userAgent, true, true);
        svgCanvas.setDoubleBufferedRendering(true);

        listeners.put(ABOUT_ACTION, new AboutAction());
        listeners.put(OPEN_ACTION, new OpenAction());
        listeners.put(OPEN_LOCATION_ACTION, new OpenLocationAction());
        listeners.put(NEW_WINDOW_ACTION, new NewWindowAction());
        listeners.put(RELOAD_ACTION, new ReloadAction());
        listeners.put(BACK_ACTION, backAction);
        listeners.put(FORWARD_ACTION, forwardAction);
        listeners.put(PRINT_ACTION, new PrintAction());
        listeners.put(EXPORT_AS_JPG_ACTION, new ExportAsJPGAction());
        listeners.put(EXPORT_AS_PNG_ACTION, new ExportAsPNGAction());
        listeners.put(EXPORT_AS_TIFF_ACTION, new ExportAsTIFFAction());
        listeners.put(PREFERENCES_ACTION, new PreferencesAction());
        listeners.put(CLOSE_ACTION, new CloseAction());
        listeners.put(EXIT_ACTION, application.createExitAction(this));
        listeners.put(VIEW_SOURCE_ACTION, new ViewSourceAction());

	javax.swing.ActionMap cMap = svgCanvas.getActionMap();
        listeners.put(RESET_TRANSFORM_ACTION, 
		      cMap.get(JSVGCanvas.RESET_TRANSFORM_ACTION));
        listeners.put(ZOOM_IN_ACTION, 
		      cMap.get(JSVGCanvas.ZOOM_IN_ACTION));
        listeners.put(ZOOM_OUT_ACTION,
		      cMap.get(JSVGCanvas.ZOOM_OUT_ACTION));
 
        listeners.put(PREVIOUS_TRANSFORM_ACTION, previousTransformAction);
        listeners.put(NEXT_TRANSFORM_ACTION, nextTransformAction);
        listeners.put(STOP_ACTION, stopAction);
        listeners.put(MONITOR_ACTION, new MonitorAction());
        listeners.put(DOM_VIEWER_ACTION, new DOMViewerAction());
        listeners.put(SET_TRANSFORM_ACTION, new SetTransformAction());
        listeners.put(FIND_DIALOG_ACTION, new FindDialogAction());
        listeners.put(THUMBNAIL_DIALOG_ACTION, new ThumbnailDialogAction());
        listeners.put(FLUSH_ACTION, new FlushAction());

        JPanel p = null;
        try {
            // Create the menu
            MenuFactory mf = new MenuFactory(bundle, this);
            JMenuBar mb = mf.createJMenuBar("MenuBar");
            setJMenuBar(mb);

            localHistory = new LocalHistory(mb, svgCanvas);

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

        p2.add(svgCanvas, BorderLayout.CENTER);
        p = new JPanel(new BorderLayout());
        p.add(p2, BorderLayout.CENTER);
        p.add(statusBar = new StatusBar(), BorderLayout.SOUTH);

        getContentPane().add(p, BorderLayout.CENTER);

        svgCanvas.addSVGDocumentLoaderListener(this);
        svgCanvas.addGVTTreeBuilderListener(this);
        svgCanvas.addGVTTreeRendererListener(this);
        svgCanvas.addLinkActivationListener(this);

        svgCanvas.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (svgDocument == null) {
                        statusBar.setXPosition(e.getX());
                        statusBar.setYPosition(e.getY());
                    } else {
                        try {
                            AffineTransform at = svgCanvas.getRenderingTransform();
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
        svgCanvas.addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    Dimension dim = svgCanvas.getSize();
                    if (svgDocument == null) {
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    } else {
                        try {
                            AffineTransform at = svgCanvas.getRenderingTransform();
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
        svgCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    Dimension dim = svgCanvas.getSize();
                    if (svgDocument == null) {
                        statusBar.setWidth(dim.width);
                        statusBar.setHeight(dim.height);
                    } else {
                        try {
                            AffineTransform at = svgCanvas.getRenderingTransform();
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
                String st = locationBar.getText().trim();
                int i = st.indexOf("#");
                String t = "";
                if (i != -1) {
                    t = st.substring(i + 1);
                    st = st.substring(0, i);
                }
                if (!st.equals("")) {
                    try{
                        File f = new File(st);
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                st = null;
                            } else {
                                try {
                                    st = f.getCanonicalPath();
                                    if (st.startsWith("/")) {
                                        st = "file:" + st;
                                    } else {
                                        st = "file:/" + st;
                                    }
                                } catch (IOException ex) {
                                }
                            }
                        }
                    }catch(SecurityException se){
                        // Could not patch the file URI for security reasons (e.g.,
                        // when run as an unsigned JavaWebStart jar): file access is
                        // not allowed. Loading will fail, but there is nothing
                        // more to do at this point.
                    }

                    if (st != null) {
                        if (svgDocument != null) {
                            try {
                                SVGOMDocument doc = (SVGOMDocument)svgDocument;
                                URL docURL = doc.getURLObject();
                                URL url = new URL(docURL, st);
                                String fi = svgCanvas.getFragmentIdentifier();
                                fi = (fi == null) ? "" : fi;
                                if (docURL.equals(url) && t.equals(fi)) {
                                    return;
                                }
                            } catch (MalformedURLException ex) {
                            }
                        }
                        if (t.length() != 0) {
                            st += "#" + t;
                        }
                        locationBar.setText(st);
                        locationBar.addToHistory(st);
                        svgCanvas.loadSVGDocument(st);
                    }
                }
            }
        });

    }

    /**
     * Whether to show the debug traces.
     */
    public void setDebug(boolean b) {
        debug = b;
    }

    /**
     * Whether to auto adjust the canvas to the size of the document.
     */
    public void setAutoAdjust(boolean b) {
        autoAdjust = b;
    }

    /**
     * Returns the main JSVGCanvas of this frame.
     */
    public JSVGCanvas getJSVGCanvas() {
        return svgCanvas;
    }

    /**
     * Needed to work-around JFileChooser bug with abstract Files
     */
    private static File makeAbsolute(File f){
        if(!f.isAbsolute()){
            return f.getAbsoluteFile();
        }
        return f;
    }

    /**
     * To show the about dialog
     */
    public class AboutAction extends AbstractAction {
        public AboutAction(){
        }

        public void actionPerformed(ActionEvent e){
            AboutDialog dlg = new AboutDialog(JSVGViewerFrame.this);
            dlg.setSize(dlg.getPreferredSize()); // Work around pack() bug on some platforms
            dlg.setLocationRelativeTo(JSVGViewerFrame.this);
            dlg.show();
            dlg.toFront();
        }
    }

    /**
     * To open a new file.
     */
    public class OpenAction extends AbstractAction {

        public OpenAction() {
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(makeAbsolute(currentPath));
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new SVGFileFilter());

            int choice = fileChooser.showOpenDialog(JSVGViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();

                try {
                    currentPath = f;
                    svgCanvas.loadSVGDocument(f.toURL().toString());
                } catch (IOException ex) {
                    userAgent.displayError(ex);
                }
            }
        }
    }

    /**
     * To open a new document.
     */
    public class OpenLocationAction extends AbstractAction {
        public OpenLocationAction() {}
        public void actionPerformed(ActionEvent e) {
            if (uriChooser == null) {
                uriChooser = new URIChooser(JSVGViewerFrame.this);
                uriChooser.setFileFilter(new SVGFileFilter());
                uriChooser.pack();
                Rectangle fr = getBounds();
                Dimension sd = uriChooser.getSize();
                uriChooser.setLocation(fr.x + (fr.width  - sd.width) / 2,
                                       fr.y + (fr.height - sd.height) / 2);
            }
            if (uriChooser.showDialog() == URIChooser.OK_OPTION) {
                String s = uriChooser.getText();
                int i = s.indexOf("#");
                String t = "";
                if (i != -1) {
                    t = s.substring(i + 1);
                    s = s.substring(0, i);
                }
                if (!s.equals("")) {
                    File f = new File(s);
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            s = null;
                        } else {
                            try {
                                s = f.getCanonicalPath();
                                if (s.startsWith("/")) {
                                    s = "file:" + s;
                                } else {
                                    s = "file:/" + s;
                                }
                            } catch (IOException ex) {
                            }
                        }
                    }
                    if (s != null) {
                        if (svgDocument != null) {
                            try {
                                SVGOMDocument doc = (SVGOMDocument)svgDocument;
                                URL docURL = doc.getURLObject();
                                URL url = new URL(docURL, s);
                                String fi = svgCanvas.getFragmentIdentifier();
                                if (docURL.equals(url) && t.equals(fi)) {
                                    return;
                                }
                            } catch (MalformedURLException ex) {
                            }
                        }
                        if (t.length() != 0) {
                            s += "#" + t;
                        }
                        svgCanvas.loadSVGDocument(s);
                    }
                }
            }
        }
    }

    /**
     * To open a new window.
     */
    public class NewWindowAction extends AbstractAction {
        public NewWindowAction() {}
        public void actionPerformed(ActionEvent e) {
            JSVGViewerFrame vf = application.createAndShowJSVGViewerFrame();

            // Copy the current settings to the new window.
            vf.autoAdjust = autoAdjust;
            vf.debug = debug;
            vf.svgCanvas.setProgressivePaint(svgCanvas.getProgressivePaint());
            vf.svgCanvas.setDoubleBufferedRendering
                (svgCanvas.getDoubleBufferedRendering());
        }
    }

    /**
     * To show the preferences.
     */
    public class PreferencesAction extends AbstractAction {
        public PreferencesAction() {}
        public void actionPerformed(ActionEvent e) {
            application.showPreferenceDialog(JSVGViewerFrame.this);
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
     * To reload the current document.
     */
    public class ReloadAction extends AbstractAction {
        public ReloadAction() {}
        public void actionPerformed(ActionEvent e) {
            if (svgDocument != null) {
                localHistory.reload();
            }
        }
    }

    /**
     * To go back to the previous document
     */
    public class BackAction extends    AbstractAction
                            implements JComponentModifier {
        List components = new LinkedList();
        public BackAction() {}
        public void actionPerformed(ActionEvent e) {
            if (localHistory.canGoBack()) {
                localHistory.back();
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = localHistory.canGoBack();
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
        List components = new LinkedList();
        public ForwardAction() {}
        public void actionPerformed(ActionEvent e) {
            if (localHistory.canGoForward()) {
                localHistory.forward();
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = localHistory.canGoForward();
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(b);
            }
        }
    }

    /**
     * To print the current document.
     */
    public class PrintAction extends AbstractAction {
        public PrintAction() {}
        public void actionPerformed(ActionEvent e) {
            if (svgDocument != null) {
                final SVGDocument doc = svgDocument;
                new Thread() {
                    public void run(){
                        //
                        // Build a PrintTranscoder to handle printing
                        // of the svgDocument object
                        //
                        PrintTranscoder pt = new PrintTranscoder();

                        //
                        // Set transcoding hints
                        //
                        pt.addTranscodingHint(pt.KEY_XML_PARSER_CLASSNAME,
                                              application.getXMLParserClassName());

                        pt.addTranscodingHint(pt.KEY_SHOW_PAGE_DIALOG,
                                              Boolean.TRUE);


                        pt.addTranscodingHint(pt.KEY_SHOW_PRINTER_DIALOG,
                                              Boolean.TRUE);

                        //
                        // Do transcoding now
                        //
                        pt.transcode(new TranscoderInput(doc), null);

                        //
                        // Print
                        //
                        try {
                            pt.print();
                        } catch (PrinterException ex) {
                            userAgent.displayError(ex);
                        }
                    }
                }.start();
            }
        }
    }

    /**
     * To save the current document as JPG.
     */
    public class ExportAsJPGAction extends AbstractAction {
        public ExportAsJPGAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(makeAbsolute(currentExportPath));
            fileChooser.setDialogTitle(resources.getString("ExportAsJPG.title"));
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new ImageFileFilter(".jpg"));

            int choice = fileChooser.showSaveDialog(JSVGViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                BufferedImage buffer = svgCanvas.getOffScreen();
                if (buffer != null) {
                    statusBar.setMessage
                        (resources.getString("Message.exportAsJPG"));

                    // create a BufferedImage of the appropriate type
                    int w = buffer.getWidth();
                    int h = buffer.getHeight();
                    final ImageTranscoder trans = new JPEGTranscoder();
                    trans.addTranscodingHint(JPEGTranscoder.KEY_XML_PARSER_CLASSNAME,
                                             application.getXMLParserClassName());
                    final BufferedImage img = trans.createImage(w, h);

                    // paint the buffer to the image
                    Graphics2D g2d = img.createGraphics();
                    g2d.setColor(Color.white);
                    g2d.fillRect(0, 0, w, h);
                    g2d.drawImage(buffer, null, 0, 0);
                    new Thread() {
                        public void run() {
                            try {
                                currentExportPath = f;
                                OutputStream ostream =
                                    new BufferedOutputStream(new FileOutputStream(f));
                                trans.writeImage(img, new TranscoderOutput(ostream));
                                ostream.flush();
                                ostream.close();
                            } catch (Exception ex) { }
                            statusBar.setMessage
                                (resources.getString("Message.done"));
                        }
                    }.start();
                }
            }
        }
    }

    /**
     * To save the current document as PNG.
     */
    public class ExportAsPNGAction extends AbstractAction {
        public ExportAsPNGAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(makeAbsolute(currentExportPath));
            fileChooser.setDialogTitle(resources.getString("ExportAsPNG.title"));
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new ImageFileFilter(".png"));

            int choice = fileChooser.showSaveDialog(JSVGViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                BufferedImage buffer = svgCanvas.getOffScreen();
                if (buffer != null) {
                    statusBar.setMessage
                        (resources.getString("Message.exportAsPNG"));

                    // create a BufferedImage of the appropriate type
                    int w = buffer.getWidth();
                    int h = buffer.getHeight();
                    final ImageTranscoder trans = new PNGTranscoder();
                    trans.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                                             application.getXMLParserClassName());
                    trans.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE,
                                             new Boolean(true));
                    final BufferedImage img = trans.createImage(w, h);

                    // paint the buffer to the image
                    Graphics2D g2d = img.createGraphics();
                    g2d.drawImage(buffer, null, 0, 0);
                    new Thread() {
                        public void run() {
                            try {
                                currentExportPath = f;
                                OutputStream ostream =
                                    new BufferedOutputStream(new FileOutputStream(f));
                                trans.writeImage(img,
                                                 new TranscoderOutput(ostream));
                                ostream.flush();
                            } catch (Exception ex) {}
                            statusBar.setMessage
                                (resources.getString("Message.done"));
                        }
                    }.start();
                }
            }
        }
    }

    /**
     * To save the current document as TIFF.
     */
    public class ExportAsTIFFAction extends AbstractAction {
        public ExportAsTIFFAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(makeAbsolute(currentExportPath));
            fileChooser.setDialogTitle(resources.getString("ExportAsTIFF.title"));
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new ImageFileFilter(".tiff"));

            int choice = fileChooser.showSaveDialog(JSVGViewerFrame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                BufferedImage buffer = svgCanvas.getOffScreen();
                if (buffer != null) {
                    statusBar.setMessage
                        (resources.getString("Message.exportAsTIFF"));

                    // create a BufferedImage of the appropriate type
                    int w = buffer.getWidth();
                    int h = buffer.getHeight();
                    final ImageTranscoder trans = new TIFFTranscoder();
                    trans.addTranscodingHint
                        (TIFFTranscoder.KEY_XML_PARSER_CLASSNAME,
                         application.getXMLParserClassName());
                    final BufferedImage img = trans.createImage(w, h);

                    // paint the buffer to the image
                    Graphics2D g2d = img.createGraphics();
                    g2d.drawImage(buffer, null, 0, 0);
                    new Thread() {
                        public void run() {
                            try {
                                currentExportPath = f;
                                OutputStream ostream = new BufferedOutputStream
                                    (new FileOutputStream(f));
                                trans.writeImage
                                    (img, new TranscoderOutput(ostream));
                                ostream.flush();
                            } catch (Exception ex) {}
                            statusBar.setMessage
                                (resources.getString("Message.done"));
                        }
                    }.start();
                }
            }
        }
    }

    /**
     * To view the source of the current document.
     */
    public class ViewSourceAction extends AbstractAction {
        public ViewSourceAction() {}
        public void actionPerformed(ActionEvent e) {
            if (svgDocument == null) {
                return;
            }

            final ParsedURL u
                = new ParsedURL(((SVGOMDocument)svgDocument).getURLObject());

            final JFrame fr = new JFrame(u.toString());
            fr.setSize(resources.getInteger("ViewSource.width"),
                       resources.getInteger("ViewSource.height"));
            final JTextArea ta  = new JTextArea();
            ta.setLineWrap(true);
            ta.setFont(new Font("monospaced", Font.PLAIN, 12));

            JScrollPane scroll = new JScrollPane();
            scroll.getViewport().add(ta);
            scroll.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            fr.getContentPane().add(scroll, BorderLayout.CENTER);

            new Thread() {
                public void run() {
                    char [] buffer = new char[4096];

                    try {
                        Document  doc = new PlainDocument();

                        InputStream is
                            = u.openStream(MimeTypeConstants.MIME_TYPES_SVG);

                        Reader in = XMLUtilities.createXMLDocumentReader(is);
                        int len;
                        while ((len=in.read(buffer, 0, buffer.length)) != -1) {
                            doc.insertString(doc.getLength(),
                                             new String(buffer, 0, len), null);
                        }

                        ta.setDocument(doc);
                        ta.setEditable(false);
                        ta.setBackground(Color.white);
                        fr.show();
                    } catch (Exception ex) {
                        userAgent.displayError(ex);
                    }
                }
            }.start();
        }
    }

    /**
     * To flush image cache (purely for debugging purposes)
     */
    public class FlushAction extends AbstractAction {
        public FlushAction() {}
        public void actionPerformed(ActionEvent e) {
            svgCanvas.flush();
            // Force redraw...
            svgCanvas.setRenderingTransform(svgCanvas.getRenderingTransform());
        }
    }

    /**
     * To go back to the previous transform
     */
    public class PreviousTransformAction extends    AbstractAction
                                         implements JComponentModifier {
        List components = new LinkedList();
        public PreviousTransformAction() {}
        public void actionPerformed(ActionEvent e) {
            if (transformHistory.canGoBack()) {
                transformHistory.back();
                update();
                nextTransformAction.update();
                svgCanvas.setRenderingTransform(transformHistory.currentTransform());
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = transformHistory.canGoBack();
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(b);
            }
        }
    }

    /**
     * To go forward to the next transform
     */
    public class NextTransformAction extends    AbstractAction
                                         implements JComponentModifier {
        List components = new LinkedList();
        public NextTransformAction() {}
        public void actionPerformed(ActionEvent e) {
            if (transformHistory.canGoForward()) {
                transformHistory.forward();
                update();
                previousTransformAction.update();
                svgCanvas.setRenderingTransform(transformHistory.currentTransform());
            }
        }

        public void addJComponent(JComponent c) {
            components.add(c);
            c.setEnabled(false);
        }

        protected void update() {
            boolean b = transformHistory.canGoForward();
            Iterator it = components.iterator();
            while (it.hasNext()) {
                ((JComponent)it.next()).setEnabled(b);
            }
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
            svgCanvas.stopProcessing();
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
     * To show the set transform dialog
     */
    public class SetTransformAction extends AbstractAction {
        public SetTransformAction(){}
        public void actionPerformed(ActionEvent e){
            if (transformDialog == null){
                transformDialog
                    = JAffineTransformChooser.createDialog
                    (JSVGViewerFrame.this,
                     resources.getString("SetTransform.title"));
            }

            AffineTransform txf = transformDialog.showDialog();
            if(txf != null){
                AffineTransform at = svgCanvas.getRenderingTransform();
                if(at == null){
                    at = new AffineTransform();
                }

                txf.concatenate(at);
                svgCanvas.setRenderingTransform(txf);
            }
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
     * To display the Find dialog
     */
    public class FindDialogAction extends AbstractAction {
        public FindDialogAction() {}
        public void actionPerformed(ActionEvent e) {
            if (findDialog == null) {
                findDialog = new FindDialog(JSVGViewerFrame.this, svgCanvas);
                findDialog.setGraphicsNode(svgCanvas.getGraphicsNode());
                findDialog.pack();
                Rectangle fr = getBounds();
                Dimension td = findDialog.getSize();
                findDialog.setLocation(fr.x + (fr.width  - td.width) / 2,
                                       fr.y + (fr.height - td.height) / 2);
            }
            findDialog.show();
        }
    }

    /**
     * To display the Thumbnail dialog
     */
    public class ThumbnailDialogAction extends AbstractAction {
        public ThumbnailDialogAction() {}
        public void actionPerformed(ActionEvent e) {
            if (thumbnailDialog == null) {
                thumbnailDialog
                    = new ThumbnailDialog(JSVGViewerFrame.this, svgCanvas);
                thumbnailDialog.pack();
                Rectangle fr = getBounds();
                Dimension td = thumbnailDialog.getSize();
                thumbnailDialog.setLocation(fr.x + (fr.width  - td.width) / 2,
                                            fr.y + (fr.height - td.height) / 2);
            }
            thumbnailDialog.show();
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
        svgCanvas.setCursor(WAIT_CURSOR);
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
        stopAction.update(false);
        svgCanvas.setCursor(DEFAULT_CURSOR);
        String s = ((SVGOMDocument)svgDocument).getURLObject().toString();
        String t = svgCanvas.getFragmentIdentifier();
        if (t != null) {
            s += "#" + t;
        }

        locationBar.setText(s);
        if (title == null) {
            title = getTitle();
        }

        String dt = svgDocument.getTitle();
        if (dt.length() != 0) {
            setTitle(title + ":" + dt);
        } else {
            int i = s.lastIndexOf("/");
            if (i == -1) {
                i = s.lastIndexOf("\\");
                if (i == -1) {
                    setTitle(title + ":" + s);
                } else {
                    setTitle(title + ":" + s.substring(i + 1));
                }
            } else {
                setTitle(title + ":" + s.substring(i + 1));
            }
        }

        localHistory.update(s);
        backAction.update();
        forwardAction.update();

        transformHistory = new TransformHistory();
        previousTransformAction.update();
        nextTransformAction.update();
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
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
        svgCanvas.setCursor(WAIT_CURSOR);
    }

    /**
     * Called when a build was completed.
     */
    public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
        if (debug) {
            System.out.print("Build completed in ");
            System.out.println((System.currentTimeMillis() - time) + " ms");
        }
        if (findDialog != null) {
            if(findDialog.isVisible()) {
                findDialog.setGraphicsNode(svgCanvas.getGraphicsNode());
            } else {
                findDialog.dispose();
                findDialog = null;
            }
        }
        stopAction.update(false);
        svgCanvas.setCursor(DEFAULT_CURSOR);
        if (autoAdjust) {
            pack();
        }
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
        if (autoAdjust) {
            pack();
        }
    }

    // GVTTreeRendererListener /////////////////////////////////////////////

    /**
     * Called when a rendering is in its preparing phase.
     */
    public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.println("Rendering preparation...");
            time = System.currentTimeMillis();
        }
        stopAction.update(true);
        svgCanvas.setCursor(WAIT_CURSOR);
        statusBar.setMainMessage(resources.getString("Message.treeRendering"));
    }

    /**
     * Called when a rendering started.
     */
    public void gvtRenderingStarted(GVTTreeRendererEvent e) {
        if (debug) {
            System.out.print("Rendering prepared in ");
            System.out.println((System.currentTimeMillis() - time) + " ms");
            time = System.currentTimeMillis();
            System.out.println("Rendering started...");
        }
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

        stopAction.update(false);
        svgCanvas.setCursor(DEFAULT_CURSOR);

        transformHistory.update(svgCanvas.getRenderingTransform());
        previousTransformAction.update();
        nextTransformAction.update();
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
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
        svgCanvas.setCursor(DEFAULT_CURSOR);
    }

    // LinkActivationListener /////////////////////////////////////////

    /**
     * Called when a link was activated.
     */
    public void linkActivated(LinkActivationEvent e) {
        String s = e.getReferencedURI();
        if (svgDocument != null) {
            try {
                SVGOMDocument doc = (SVGOMDocument)svgDocument;
                URL docURL = doc.getURLObject();
                URL url = new URL(docURL, s);
                if (!url.sameFile(docURL)) {
                    return;
                }
            } catch (MalformedURLException ex) {
            }
            if (s.indexOf("#") != -1) {
                localHistory.update(e.getReferencedURI());
                backAction.update();
                forwardAction.update();

                transformHistory = new TransformHistory();
                previousTransformAction.update();
                nextTransformAction.update();
            }
        }
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
            JOptionPane pane;
            pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
            JDialog dialog = pane.createDialog(JSVGViewerFrame.this, "ERROR");
            dialog.setModal(false);
            dialog.show(); // Safe to be called from any thread
        }

        /**
         * Displays an error resulting from the specified Exception.
         */
        public void displayError(Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
            displayError(ex.getMessage());
        }

        /**
         * Displays a message in the User Agent interface.
         * The given message is typically displayed in a status bar.
         */
        public void displayMessage(String message) {
            statusBar.setMessage(message);
        }

        /**
         * Returns a customized the pixel to mm factor.
         */
        public float getPixelToMM() {
            return 0.264583333333333333333f; // 96 dpi
        }

        /**
         * Returns the language settings.
         */
        public String getLanguages() {
            return application.getLanguages();
        }

        /**
         * Returns the user stylesheet uri.
         * @return null if no user style sheet was specified.
         */
        public String getUserStyleSheetURI() {
            return application.getUserStyleSheetURI();
        }

        /**
         * Returns the class name of the XML parser.
         */
        public String getXMLParserClassName() {
            return application.getXMLParserClassName();
        }

        /**
         * Opens a link.
         * @param uri The document URI.
         * @param newc Whether the link should be activated in a new component.
         */
        public void openLink(String uri, boolean newc) {
            if (newc) {
                application.openLink(uri);
            } else {
                svgCanvas.loadSVGDocument(uri);
            }
        }

        /**
         * Tells whether the given extension is supported by this
         * user agent.
         */
        public boolean supportExtension(String s) {
            return false;
        }

        public void handleElement(Element elt, Object data){
        }
    }

    /**
     * A FileFilter used when exporting the SVG document as an image.
     */
    protected static class ImageFileFilter extends FileFilter {

	/** The extension of the image filename. */
	protected String extension;

	public ImageFileFilter(String extension) {
	    this.extension = extension;
	}

	/**
	 * Returns true if <tt>f</tt> is a file with the correct extension,
	 * false otherwise.
	 */
	public boolean accept(File f) {
	    boolean accept = false;
	    String fileName = null;
	    if (f != null) {
		if (f.isDirectory()) {
		    accept = true;
		} else {
		    fileName = f.getPath().toLowerCase();
		    if (fileName.endsWith(extension)) {
			accept = true;
		    }
		}
	    }
	    return accept;
	}
	
	/**
	 * Returns the file description
	 */
	public String getDescription() {
	    return extension;
	}
    }

}
