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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

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
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderListener;
import org.apache.batik.swing.svg.SVGFileFilter;
import org.apache.batik.swing.svg.SVGUserAgent;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.batik.transcoder.print.PrintTranscoder;

import org.apache.batik.util.gui.DOMViewer;
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
    public final static String OPEN_LOCATION_ACTION = "OpenLocationAction";
    public final static String NEW_WINDOW_ACTION = "NewWindowAction";
    public final static String RELOAD_ACTION = "ReloadAction";
    public final static String BACK_ACTION = "BackAction";
    public final static String FORWARD_ACTION = "ForwardAction";
    public final static String PRINT_ACTION = "PrintAction";
    public final static String EXPORT_AS_PNG_ACTION = "ExportAsPNGAction";
    public final static String EXPORT_AS_JPG_ACTION = "ExportAsJPGAction";
    public final static String CLOSE_ACTION = "CloseAction";
    public final static String VIEW_SOURCE_ACTION = "ViewSourceAction";
    public final static String EXIT_ACTION = "ExitAction";
    public final static String RESET_TRANSFORM_ACTION = "ResetTransformAction";
    public final static String ZOOM_IN_ACTION = "ZoomInAction";
    public final static String ZOOM_OUT_ACTION = "ZoomOutAction";
    public final static String STOP_ACTION = "StopAction";
    public final static String DOUBLE_BUFFER_ACTION = "DoubleBufferAction";
    public final static String AUTO_ADJUST_ACTION = "AutoAdjustAction";
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
    protected String currentPath = ".";

    /**
     * The current export path.
     */
    protected String currentExportPath = ".";

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
     * The local history.
     */
    protected LocalHistory localHistory;

    /**
     * the ShowRenderingAction.
     */
    protected ShowRenderingAction showRenderingAction = new ShowRenderingAction();

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
        listeners.put(OPEN_LOCATION_ACTION, new OpenLocationAction());
        listeners.put(NEW_WINDOW_ACTION, new NewWindowAction());
        listeners.put(RELOAD_ACTION, new ReloadAction());
        listeners.put(BACK_ACTION, backAction);
        listeners.put(FORWARD_ACTION, forwardAction);
        listeners.put(PRINT_ACTION, new PrintAction());
        listeners.put(EXPORT_AS_PNG_ACTION, new ExportAsPNGAction());
        listeners.put(EXPORT_AS_JPG_ACTION, new ExportAsJPGAction());
        listeners.put(CLOSE_ACTION, new CloseAction());
        listeners.put(EXIT_ACTION, application.createExitAction(this));
        listeners.put(VIEW_SOURCE_ACTION, new ViewSourceAction());
        listeners.put(RESET_TRANSFORM_ACTION, new ResetTransformAction());
        listeners.put(ZOOM_IN_ACTION, new ZoomInAction());
        listeners.put(ZOOM_OUT_ACTION, new ZoomOutAction());
        listeners.put(STOP_ACTION, stopAction);
        listeners.put(DOUBLE_BUFFER_ACTION, new DoubleBufferAction());
        listeners.put(AUTO_ADJUST_ACTION, new AutoAdjustAction());
        listeners.put(SHOW_DEBUG_ACTION, new ShowDebugAction());
        listeners.put(SHOW_RENDERING_ACTION, showRenderingAction);
        listeners.put(LANGUAGE_ACTION, new LanguageAction());
        listeners.put(STYLE_SHEET_ACTION, new StyleSheetAction());
        listeners.put(MONITOR_ACTION, new MonitorAction());
        listeners.put(DOM_VIEWER_ACTION, new DOMViewerAction());

        svgCanvas = new JSVGCanvas(userAgent, true, true);

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
                String s = locationBar.getText().trim();
                if (!s.equals("")) {
                    File f = new File(s);
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            s = null;
                        } else {
                            try {
                                s = "file:" + f.getCanonicalPath();
                            } catch (IOException ex) {
                            }
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
                        svgCanvas.loadSVGDocument(s);
                    }
                }
            }
        });

        // Interactors initialization ///////////////////////////////////////
        svgCanvas.setEnableZoomInteractor(true);
        svgCanvas.setEnableImageZoomInteractor(true);
        svgCanvas.setEnablePanInteractor(true);
        svgCanvas.setEnableRotateInteractor(true);
    }

    /**
     * Returns the main JSVGCanvas of this frame.
     */
    public JSVGCanvas getJSVGCanvas() {
        return svgCanvas;
    }

    /**
     * To open a new file.
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
                String text = uriChooser.getText();
                try {
                    File f = new File(text);
                    URL u = null;
                    if (f.exists() && !f.isDirectory()) {
                        u = f.toURL();
                    } else {
                        u = new URL(text);
                    }
                    svgCanvas.loadSVGDocument(u.toString());
                } catch (Exception ex) {
                    userAgent.displayError(ex);
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
            AutoAdjustAction aaa;
            aaa = (AutoAdjustAction)vf.listeners.get(AUTO_ADJUST_ACTION);
            aaa.menuItem.setSelected(autoAdjust);

            vf.debug = debug;
            ShowDebugAction sda;
            sda = (ShowDebugAction)vf.listeners.get(SHOW_DEBUG_ACTION);
            sda.menuItem.setSelected(debug);

            vf.svgCanvas.setProgressivePaint(svgCanvas.getProgressivePaint());
            ShowRenderingAction sra;
            sra = (ShowRenderingAction)vf.listeners.get(SHOW_RENDERING_ACTION);
            sra.menuItem.setSelected(svgCanvas.getProgressivePaint());

            vf.svgCanvas.setDoubleBufferedRendering
                (svgCanvas.getDoubleBufferedRendering());
            vf.showRenderingAction.update(!svgCanvas.getDoubleBufferedRendering());
            DoubleBufferAction dba;
            dba = (DoubleBufferAction)vf.listeners.get(DOUBLE_BUFFER_ACTION);
            dba.menuItem.setSelected(svgCanvas.getDoubleBufferedRendering());

            vf.userLanguages = userLanguages;
            vf.userStyleSheetURI = userStyleSheetURI;
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
     * To save the current document as PNG.
     */
    public class ExportAsPNGAction extends AbstractAction {
        public ExportAsPNGAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(currentExportPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);

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
     * To save the current document as JPG.
     */
    public class ExportAsJPGAction extends AbstractAction {
        public ExportAsJPGAction() {}
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser =
                new JFileChooser(currentExportPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);

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
     * To view the source of the current document.
     */
    public class ViewSourceAction extends AbstractAction {
        public ViewSourceAction() {}
        public void actionPerformed(ActionEvent e) {
            if (svgDocument == null) {
                return;
            }

            URL tu = null;
            try {
                tu = new URL(((SVGOMDocument)svgDocument).getURLObject(), "");
            } catch (MalformedURLException ex) {
                // Can't happen
                throw new InternalError(ex.getMessage());
            }
            final URL u = tu;

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

                        InputStream is = u.openStream();

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
                            is = u.openStream();

                            Reader in = new InputStreamReader(is);
                            int nch;
                            while ((nch = in.read(buffer, 0, buffer.length))!=-1){
                                doc.insertString(doc.getLength(),
                                                 new String(buffer, 0, nch), null);
                            }
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
     * To reset the document transform.
     */
    public class ResetTransformAction extends AbstractAction {
        public ResetTransformAction() {}
        public void actionPerformed(ActionEvent e) {
            svgCanvas.setRenderingTransform(initialTransform);
        }
    }

    /**
     * To zoom in.
     */
    public class ZoomInAction extends AbstractAction {
        public ZoomInAction() {}
        public void actionPerformed(ActionEvent e) {
            AffineTransform at = svgCanvas.getRenderingTransform();
            if (at != null) {
                Dimension dim = getSize();
                int x = dim.width / 2;
                int y = dim.height / 2;
                AffineTransform t = AffineTransform.getTranslateInstance(x, y);
                t.scale(2, 2);
                t.translate(-x, -y);
                t.concatenate(at);
                svgCanvas.setRenderingTransform(t);
            }
        }
    }

    /**
     * To zoom out.
     */
    public class ZoomOutAction extends AbstractAction {
        public ZoomOutAction() {}
        public void actionPerformed(ActionEvent e) {
            AffineTransform at = svgCanvas.getRenderingTransform();
            if (at != null) {
                Dimension dim = getSize();
                int x = dim.width / 2;
                int y = dim.height / 2;
                AffineTransform t = AffineTransform.getTranslateInstance(x, y);
                t.scale(0.5, 0.5);
                t.translate(-x, -y);
                t.concatenate(at);
                svgCanvas.setRenderingTransform(t);
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
     * To enable the double buffering.
     */
    public class DoubleBufferAction
        extends AbstractAction
        implements JComponentModifier {
        public JCheckBoxMenuItem menuItem;
        public DoubleBufferAction() {}
        public void actionPerformed(ActionEvent e) {
            boolean b = menuItem.isSelected();
            showRenderingAction.update(!b);
            svgCanvas.setDoubleBufferedRendering(b);
        }

        public void addJComponent(JComponent c) {
            menuItem = (JCheckBoxMenuItem)c;
        }
    }

    /**
     * To adjust the window size on load.
     */
    public class AutoAdjustAction
        extends AbstractAction
        implements JComponentModifier {
        public JCheckBoxMenuItem menuItem;
        public AutoAdjustAction() {}
        public void actionPerformed(ActionEvent e) {
            autoAdjust = menuItem.isSelected();
        }

        public void addJComponent(JComponent c) {
            menuItem = (JCheckBoxMenuItem)c;
        }
    }

    /**
     * To enable the debug traces.
     */
    public class ShowDebugAction
        extends AbstractAction
        implements JComponentModifier {
        public JCheckBoxMenuItem menuItem;
        public ShowDebugAction() {}
        public void actionPerformed(ActionEvent e) {
            debug = menuItem.isSelected();
            time = System.currentTimeMillis();
        }

        public void addJComponent(JComponent c) {
            menuItem = (JCheckBoxMenuItem)c;
        }
    }

    /**
     * To enable progressive rendering.
     */
    public class ShowRenderingAction
        extends AbstractAction
        implements JComponentModifier {
        public JCheckBoxMenuItem menuItem;
        public ShowRenderingAction() {}
        public void actionPerformed(ActionEvent e) {
            svgCanvas.setProgressivePaint(menuItem.isSelected());
        }

        public void addJComponent(JComponent c) {
            menuItem = (JCheckBoxMenuItem)c;
        }

        public void update(boolean enabled) {
            menuItem.setEnabled(enabled);
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

                Rectangle fr = getBounds();
                Dimension ld = languageDialog.getSize();
                languageDialog.setLocation(fr.x + (fr.width  - ld.width) / 2,
                                           fr.y + (fr.height - ld.height) / 2);
                languageDialog.setLanguages(userLanguages);
            }
            if (languageDialog.showDialog() == LanguageDialog.OK_OPTION) {
                userLanguages = languageDialog.getLanguages();
            }
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
                styleSheetDialog.pack();
                Rectangle fr = getBounds();
                Dimension sd = styleSheetDialog.getSize();
                styleSheetDialog.setLocation(fr.x + (fr.width  - sd.width) / 2,
                                             fr.y + (fr.height - sd.height) / 2);
                if (userStyleSheetURI != null) {
                    styleSheetDialog.setPath(userStyleSheetURI);
                }
            }
            if (styleSheetDialog.showDialog() == UserStyleDialog.OK_OPTION) {
                userStyleSheetURI = styleSheetDialog.getPath();
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
        initialTransform = null;
        stopAction.update(false);
        svgCanvas.setCursor(DEFAULT_CURSOR);
        String s = ((SVGOMDocument)svgDocument).getURLObject().toString();
        locationBar.setText(s);
        if (title == null) {
            title = getTitle();
        }
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

        localHistory.update(s);
        backAction.update();
        forwardAction.update();
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
        if (initialTransform == null) {
            initialTransform = svgCanvas.getRenderingTransform();
        }
        stopAction.update(false);
        svgCanvas.setCursor(DEFAULT_CURSOR);
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
