/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.border.BevelBorder;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.css.ViewCSS;

// HACK: There should not be a dependency on the GVT DOM Implementation
import org.apache.batik.dom.svg.SVGDocumentFactory;

// HACK: There should not be a dependency on the CSS Implementation
import org.apache.batik.css.CSSDocumentHandler;

import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.SwingInitializer;
import org.apache.batik.util.JGridBagPanel;
import org.apache.batik.util.SVGFileFilter;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.parser.ParserFactory;

import org.apache.batik.refimpl.gvt.renderer.util.resources.Messages;

/**
 * Panel for viewing an SVG file, zooming in and out, selecting area
 * of interest.  The <code>SwingStaticViewerPanel</code class contains
 * two important inner classes:
 * <ul>
 *   <li><code>Model</code>. Describes the content viewed by the
 *       panel and the status of the viewer.</li>
 *   <li><code>Controller</code>. Handles all events in the viewer,
 *       such as requests to load a new document.</li>
 * </ul>
 * The <code>Model</code>'s configuration is made of the SAX Parser
 * class it uses, the <code>GVTBuilder</code> and the
 * <code>BridgeContext</code> to which it delegates creation of
 * a GVT Tree.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
*/
public class SwingStaticViewerPanel extends JPanel {
    /**
     * Icon resource names
     */
    public static final String ICON_VIEWER = "org/apache/batik/refimpl/gvt/renderer/util/resources/viewer.gif";
    public static final String ICON_PREVIOUS = "org/apache/batik/refimpl/gvt/renderer/util/resources/Back16.gif";
    public static final String ICON_NEXT = "org/apache/batik/refimpl/gvt/renderer/util/resources/Forward16.gif";
    public static final String ICON_RELOAD = "org/apache/batik/refimpl/gvt/renderer/util/resources/Redo16.gif";
    public static final String ICON_SEARCH = "org/apache/batik/refimpl/gvt/renderer/util/resources/Search16.gif";

    /**
     * Default size for the viewer panel
     */
    private static final Dimension DEFAULT_VIEWER_SIZE = new Dimension(400, 600);

    /**
     * The controller handles all the events in the Viewer
     * @see #buildMenuBar
     */
    private Controller controller;

    /**
     * The Model is able to load an SVG file and turn it into a
     * GVT tree
     */
    private Model model;

    /**
     * The component which displayes the GVT tree
     */
    private SwingStaticViewer viewer;

    /**
     * Builds a panel with a navigation menu and toolbar.
     */
    public SwingStaticViewerPanel(GVTBuilder builder,
                                  BridgeContext bridgeContext,
                                  SVGDocumentFactory documentFactory){
        model = new Model(builder, bridgeContext, documentFactory);
        controller = new Controller();

        //
        // Create user interface here
        //
        JGridBagPanel c = new JGridBagPanel(JGridBagPanel.ZERO_INSETS);
        c.setBackground(Color.white);
        JMenuBar menuBar = buildMenuBar();
        Container locationBar = buildLocationBar();
        Container toolBar = buildToolBar();
        Container statusBar = buildStatusBar();
        viewer = buildViewer();

        viewer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                            BorderFactory.createEmptyBorder(5,5,5,5)));

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, menuBar);
        add(BorderLayout.CENTER, c);
        add(BorderLayout.SOUTH, statusBar);

        JGridBagPanel topBar = new JGridBagPanel();
        topBar.add(toolBar, 0, 0, 1, 1, c.CENTER, c.HORIZONTAL, 0, 0);
        topBar.add(locationBar, 1, 0, 1, 1, c.CENTER, c.HORIZONTAL, 1, 0);

        c.add(topBar, 0, 1, 1, 1, c.CENTER, c.HORIZONTAL, 1, 0);
        c.add(viewer, 0, 2, 1, 1, c.CENTER, c.BOTH, 1, 1);

        viewer.setPreferredSize(DEFAULT_VIEWER_SIZE);
    }

    /**
     * Builds a viewer component
     */
    private SwingStaticViewer buildViewer(){
        final SwingStaticViewer v = new SwingStaticViewer(null);

        // Add listener to zoom of reference point when the
        // user clicks with the Ctrl key pressed.
        v.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent evt){
                    if(
                       ( (evt.getModifiers() & evt.CTRL_MASK) != 0)
                       &&
                       ( (evt.getModifiers() & evt.BUTTON1_MASK) != 0)
                       )
                    {
                        // User has control-clicked on the viewer
                        controller.onZoomIn(evt.getPoint());
                    }
                }
            });

        // Add listener to let the user select an area of interest
        class AOIListener extends MouseInputAdapter {
            Rectangle aoi = new Rectangle(0, 0, 0, 0);
            int sx, dx, sy, dy;
            boolean dragging = false;

            public void mousePressed(MouseEvent e) {
                if((e.getModifiers() & e.CTRL_MASK) != 0){
                    sx = e.getX();
                    sy = e.getY();
                    dx = sx;
                    dy = sy;
                    updateAOI();
                    viewer.setMarker(aoi);
                    dragging = true;
                }
            }

            public void mouseDragged(MouseEvent e) {
                if((e.getModifiers() & e.CTRL_MASK) != 0){
                    dx = e.getX();
                    dy = e.getY();
                    updateAOI();
                    viewer.setMarker(aoi);
                }
            }

            private void updateAOI(){
                if(sx <= dx){
                    aoi.x = sx;
                    aoi.width = dx - sx;
                }
                else{
                    aoi.x = dx;
                    aoi.width = sx - dx;
                }

                if(sy <= dy){
                    aoi.y = sy;
                    aoi.height = dy - sy;
                }
                else{
                    aoi.y = dy;
                    aoi.height = sy - dy;
                }

            }

            public void mouseReleased(MouseEvent e) {
                if(dragging){
                    viewer.setMarker(null);
                    controller.onZoomIn(aoi);
                    dragging = false;
                }
                else{
                    viewer.setMarker(null);
                }
            }
        };

        AOIListener aoiListener = new AOIListener();

        v.addMouseListener(aoiListener);
        v.addMouseMotionListener(aoiListener);

        //
        // Add listener to let the user pan
        //
        // Add listener to let the user select an area of interest
        class PanListener extends MouseInputAdapter {
            Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

            int sx, dx, sy, dy;

            public void mousePressed(MouseEvent e) {
                if((e.getModifiers() & e.ALT_MASK) != 0){
                    v.setCursor(hand);
                    sx = e.getX();
                    sy = e.getY();
                    dx = sx;
                    dy = sy;
                }
            }

            public void mouseDragged(MouseEvent e) {
                if((e.getModifiers() & e.ALT_MASK) != 0){
                    dx = e.getX();
                    dy = e.getY();
                    viewer.translate(dx - sx, dy - sy);
                    viewer.repaint();
                    sx = dx;
                    sy = dy;
                }
            }

            public void mouseReleased(MouseEvent e) {
                v.setCursor(defaultCursor);
                v.coerceTranslate();
                v.repaint();
            }

            public void mouseExited(MouseEvent e){
                v.setCursor(defaultCursor);
                v.coerceTranslate();
                v.repaint();
            }

            public void mouseEntered(MouseEvent e){
                sx = e.getX();
                sy = e.getY();
                if((e.getModifiers() & e.ALT_MASK) != 0){
                    v.setCursor(hand);
                }
            }
        };

        PanListener panListener = new PanListener();
        v.addMouseListener(panListener);
        v.addMouseMotionListener(panListener);

        return v;
    }

    /**
     * Builds a status bar
     */
    private Container buildStatusBar(){
        JGridBagPanel statusBar = new JGridBagPanel();
        final JLabel statusLabel = new JLabel(Messages.LABEL_STATUS);

        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        statusBar.add(statusLabel, 0, 0, 1, 1,
                      statusBar.CENTER,
                      statusBar.HORIZONTAL, 1, 0);

        //
        // Add Simple Behavior handlers for Next and Previous menu
        // items.
        //
        model.addPropertyChangeListener("entityURI", new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt){
                    String entityURI = model.getEntityURI();
                    if(entityURI == null){
                        entityURI = "";
                    }

                    statusLabel.setText(Messages.LABEL_STATUS + " " + entityURI);

                }
            });

        return statusBar;
    }

    /**
     * Builds a location bar for the viewer
     */
    private Container buildLocationBar(){
        JGridBagPanel locationBar = new JGridBagPanel();
        JLabel locationLabel = new JLabel(Messages.LABEL_LOCATION);
        final JComboBox locationList = new JComboBox(model.getEntityURIs());
        locationList.setEditable(true);
        locationBar.add(locationLabel, 0, 0, 1, 1, locationBar.CENTER, locationBar.HORIZONTAL,
                        0, 0);
        locationBar.add(locationList, 1, 0, 1, 1, locationBar.CENTER, locationBar.HORIZONTAL,
                        1, 0);

        locationList.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    model.setEntityURI(locationList.getSelectedItem().toString());
                }});

        return locationBar;
    }

    /**
     * Builds a toolbar
     */
    private Container buildToolBar(){
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Previous
        URL previousIconURL = ClassLoader.getSystemResource(ICON_PREVIOUS);
        Action previousAction = new AbstractAction(Messages.MENU_ITEM_DOCUMENT_PREVIOUS,
                                                   new ImageIcon(previousIconURL)){
                public void actionPerformed(ActionEvent evt){
                    controller.onPrevious();
                }};
        final JButton previousButton = toolbar.add(previousAction);
        previousButton.setToolTipText(Messages.TOOL_TIP_PREVIOUS);

        // Next
        URL nextIconURL = ClassLoader.getSystemResource(ICON_NEXT);
        Action nextAction = new AbstractAction(Messages.MENU_ITEM_DOCUMENT_NEXT,
                                               new ImageIcon(nextIconURL)){
                public void actionPerformed(ActionEvent evt){
                    controller.onNext();
                }};
        final JButton nextButton = toolbar.add(nextAction);
        nextButton.setToolTipText(Messages.TOOL_TIP_NEXT);

        // Reload
        URL reloadIconURL = ClassLoader.getSystemResource(ICON_RELOAD);
        Action reloadAction = new AbstractAction(Messages.MENU_ITEM_DOCUMENT_RELOAD,
                                                 new ImageIcon(reloadIconURL)){
                public void actionPerformed(ActionEvent evt){
                    controller.onReload();
                }};
        JButton reloadButton = toolbar.add(reloadAction);
        reloadButton.setToolTipText(Messages.TOOL_TIP_RELOAD);

        // Search
        URL searchIconURL = ClassLoader.getSystemResource(ICON_SEARCH);
        Action searchAction = new AbstractAction(Messages.MENU_ITEM_DOCUMENT_SEARCH,
                                                 new ImageIcon(searchIconURL)){
                public void actionPerformed(ActionEvent evt){
                    controller.onSearch();
                }};
        JButton searchButton = toolbar.add(searchAction);
        searchButton.setToolTipText(Messages.TOOL_TIP_SEARCH);

        //
        // Add Simple Behavior handlers
        //
        model.addPropertyChangeListener("entityURI", new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt){
                    String nextEntityURI = model.getNextEntityURI();
                    if(nextEntityURI == null){
                        nextButton.setEnabled(false);
                    }
                    else{
                        nextButton.setEnabled(true);
                    }

                    String prevEntityURI = model.getPreviousEntityURI();
                    if(prevEntityURI == null){
                        previousButton.setEnabled(false);
                    }
                    else{
                        previousButton.setEnabled(true);
                    }
                }
            });

        return toolbar;

    }

    /**
     * Builds a menu bar for the viewer
     */
    private JMenuBar buildMenuBar(){
        //
        // Build a new menu bar with pull down menus
        //
        JMenuBar menuBar = new JMenuBar();

        //
        // File Menu
        //
        JMenu fileMenu = new JMenu(Messages.MENU_TITLE_FILE);
        menuBar.add(fileMenu);
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem fileOpenMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_FILE_OPEN){
                public void actionPerformed(ActionEvent evt){
                    controller.onFileOpen();
                }
            });
        fileOpenMenuItem.setAccelerator(
                                        KeyStroke.getKeyStroke(
                                                               KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        fileOpenMenuItem.setMnemonic(KeyEvent.VK_O);

        JMenuItem fileExitMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_FILE_EXIT){
                public void actionPerformed(ActionEvent evt){
                    controller.onFileExit();
                }
            });
        fileExitMenuItem.setMnemonic(KeyEvent.VK_X);
        fileExitMenuItem.setAccelerator(
                                        KeyStroke.getKeyStroke(
                                                               KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        fileMenu.add(fileOpenMenuItem);
        fileMenu.add(fileExitMenuItem);

        //
        // Document Menu
        //
        JMenu documentMenu = new JMenu(Messages.MENU_TITLE_DOCUMENT);
        menuBar.add(documentMenu);
        documentMenu.setMnemonic(KeyEvent.VK_D);

        JMenuItem zoomInMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_ZOOM_IN){
                public void actionPerformed(ActionEvent evt){
                    controller.onZoomIn();
                }
            });
        zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK));
        zoomInMenuItem.setMnemonic(KeyEvent.VK_I);

        JMenuItem zoomOutMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_ZOOM_OUT){
                public void actionPerformed(ActionEvent evt){
                    controller.onZoomOut();
                }
            });
        zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK));
        zoomOutMenuItem.setMnemonic(KeyEvent.VK_O);

        JMenuItem panMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_PAN){
                public void actionPerformed(ActionEvent evt){
                    controller.onPan();
                }
            });
        panMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        panMenuItem.setMnemonic(KeyEvent.VK_A);

        final JMenuItem previousMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_PREVIOUS){
                public void actionPerformed(ActionEvent evt){
                    controller.onPrevious();
                }
            });
        previousMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
        previousMenuItem.setMnemonic(KeyEvent.VK_P);

        final JMenuItem nextMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_NEXT){
                public void actionPerformed(ActionEvent evt){
                    controller.onNext();
                }
            });
        nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));
        nextMenuItem.setMnemonic(KeyEvent.VK_N);

        JMenuItem searchMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_SEARCH){
                public void actionPerformed(ActionEvent evt){
                    controller.onSearch();
                }
            });
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        searchMenuItem.setMnemonic(KeyEvent.VK_S);

        JMenuItem reloadMenuItem = new JMenuItem(new AbstractAction(Messages.MENU_ITEM_DOCUMENT_RELOAD){
                public void actionPerformed(ActionEvent evt){
                    controller.onReload();
                }
            });
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        reloadMenuItem.setMnemonic(KeyEvent.VK_R);

        documentMenu.add(zoomInMenuItem);
        documentMenu.add(zoomOutMenuItem);
        documentMenu.add(panMenuItem);
        documentMenu.add(previousMenuItem);
        documentMenu.add(nextMenuItem);
        documentMenu.add(searchMenuItem);
        documentMenu.add(reloadMenuItem);

        //
        // Add Simple Behavior handlers for Next and Previous menu
        // items.
        //
        model.addPropertyChangeListener("entityURI", new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt){
                    String nextEntityURI = model.getNextEntityURI();
                    if(nextEntityURI == null){
                        nextMenuItem.setEnabled(false);
                    }
                    else{
                        nextMenuItem.setEnabled(true);
                    }

                    String prevEntityURI = model.getPreviousEntityURI();
                    if(prevEntityURI == null){
                        previousMenuItem.setEnabled(false);
                    }
                    else{
                        previousMenuItem.setEnabled(true);
                    }
                }
            });

        return menuBar;

    }

    class Model {
        private final int MAX_ENTITY_URIS_SIZE = 20;

        /**
         * Used to build a GVT tree from an SVG Document
         */
        private GVTBuilder builder;

        /**
         * Used by the builder to build GVT trees
         */
        private BridgeContext bridgeContext;

        /**
         * Used to build SVG documents
         */
        private SVGDocumentFactory documentFactory;

        /**
         * The URI the tree root was loaded from
         */
        private String entityURI;

        /**
         * The root of the GVT Tree loaded from the URI
         */
        private GraphicsNode treeRoot = null;

        /**
         * Models the list of past and present entityURIs
         */
        private DefaultComboBoxModel entityURIs = new DefaultComboBoxModel();

        /**
         * All listener registration/notification is delegated to the
         * PropertyChangeSupport object.
         */
        private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

        public Model(GVTBuilder builder,
                     BridgeContext bridgeContext,
                     SVGDocumentFactory documentFactory){

            if(builder == null)
                throw new IllegalArgumentException(Messages.ERROR_INTERNAL_ERROR);

            if(bridgeContext == null)
                throw new IllegalArgumentException(Messages.ERROR_INTERNAL_ERROR);

            if(documentFactory == null)
                throw new IllegalArgumentException(Messages.ERROR_INTERNAL_ERROR);

            this.builder = builder;
            this.bridgeContext = bridgeContext;
            this.documentFactory = documentFactory;
        }

        /**
         * Returns the list of entityURIs the model is handling
         */
        public ComboBoxModel getEntityURIs(){
            return entityURIs;
        }

        /**
         * @param entityURI the URI of the SVG document to display.
         */
        public void setEntityURI(String entityURI) {
            System.out.println("this.entityURI : " + this.entityURI);
            System.out.println("entityURI : " + entityURI);
            if( ( (this.entityURI == null) && (entityURI != null) )
                ||
                ( !(this.entityURI.equals(entityURI)) && (entityURI != null) )
                ){
                SVGDocument svgDocument = null;
                viewer.setCursor(viewer.WAIT_CURSOR);

                try{
                    System.out.println("Loading ... : " + entityURI);
                    svgDocument = documentFactory.createDocument(SVGConstants.SVG_NAMESPACE_URI, new InputSource(entityURI));
                    // fix CSS view on the context

                    ViewCSS viewCSS = (ViewCSS)svgDocument.getDocumentElement();
                    bridgeContext.setViewCSS(viewCSS);

                }catch(SAXException e){
                    e.printStackTrace();
                    controller.onParsingError(e);
                    return;
                }catch(ClassCastException e){
                    e.printStackTrace();
                    throw new Error(Messages.ERROR_INTERNAL_ERROR);
                }finally{
                    viewer.setCursor(viewer.DEFAULT_CURSOR);
                }

                try{
                    viewer.setCursor(viewer.WAIT_CURSOR);
                    GraphicsNode oldTreeRoot = this.treeRoot;
                    this.treeRoot = builder.build(bridgeContext,
                                                  svgDocument);

                    String oldEntityURI = this.entityURI;
                    this.entityURI = entityURI;

                    // If the new entityURI is not in the list of entityURIs,
                    // append it to the end.
                    int index = entityURIs.getIndexOf(entityURI);
                    if(index < 0){
                        entityURIs.addElement(entityURI);
                        entityURIs.setSelectedItem(entityURI);

                        // If the list has grown too much, remove first
                        // item
                        if(entityURIs.getSize() > MAX_ENTITY_URIS_SIZE){
                            entityURIs.removeElementAt(0);
                        }
                    }

                    changeSupport.firePropertyChange("treeRoot",
                                                     oldTreeRoot,
                                                     this.treeRoot);

                    changeSupport.firePropertyChange("entityURI",
                                                     oldEntityURI,
                                                     entityURI);

                }catch(Exception e){
                    JOptionPane.showMessageDialog(SwingStaticViewerPanel.this,
                                                  Messages.formatMessage(Messages.KEY_ERROR_COULD_NOT_LOAD_ENTITY, new Object[]{entityURI, e.getMessage()}));
                    e.printStackTrace();
                }finally{
                    viewer.setCursor(viewer.DEFAULT_CURSOR);
                }
            }
            else if(entityURI == null){
                GraphicsNode oldTreeRoot = this.treeRoot;
                this.treeRoot = null;
                changeSupport.firePropertyChange("treeRoot",
                                                 oldTreeRoot,
                                                 this.treeRoot);

                String oldEntityURI = this.entityURI;
                this.entityURI = null;
                changeSupport.firePropertyChange("entityURI",
                                                 oldEntityURI,
                                                 entityURI);
            }
        }

        /**
         *
         * @return
         */
        public String getEntityURI() {
            return entityURI;
        }

        /**
         * Returns the previous URI in the model's list
         */
        public String getPreviousEntityURI(){
            int index = entityURIs.getIndexOf(entityURI);
            int prevIndex = index-1;
            return (String)entityURIs.getElementAt(prevIndex);
        }

        /**
         * Returns the next URI in the model's list
         */
        public String getNextEntityURI(){
            // Get index of current URI
            int index = entityURIs.getIndexOf(entityURI);
            int nextIndex = index+1;
            return (String)entityURIs.getElementAt(nextIndex);
        }

        /**
         * Returns the root of the GVT tree built from the URI
         */
        public GraphicsNode getTreeRoot(){
            return this.treeRoot;
        }

        /**
         * Adds a listener of the model for the given property
         */
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
            changeSupport.addPropertyChangeListener(propertyName, listener);
        }

        /**
         * Removes a listener of the model
         */
        public void removePropertyChangeListener(String propertyName,
                                                 PropertyChangeListener listener){
            changeSupport.removePropertyChangeListener(propertyName, listener);
        }

    }

    class Controller {
        /**
         * Dialog box used to let the user select files
         * to view
         */
        JFileChooser fileChooser;

        /**
         * By default, the open dialog box only shows
         * SVG files
         */
        FileFilter svgFileFilter;

        /**
         * Registers as a listener with the model
         */
        public Controller(){
            //
            // Link model events to controller
            //
            model.addPropertyChangeListener("treeRoot", new PropertyChangeListener(){
                    public void propertyChange(PropertyChangeEvent evt){
                        controller.onTreeRootChanged();
                    }
                });

        }

        /**
         * Handles File->Open requests
         */
        public void onFileOpen(){
            //
            // Lazily create a file choser
            //
            if(fileChooser == null){
                fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);

                // Add a filter for svg files
                svgFileFilter = new SVGFileFilter();
                fileChooser.addChoosableFileFilter(svgFileFilter);
            }

            //
            // Let the user select an SVG file. By default,
            // make the dialog show only SVG files.
            //
            fileChooser.setFileFilter(svgFileFilter);

            //
            // Get user selection
            //
            int choice = fileChooser.showDialog(null, Messages.DIALOG_FILE_OPEN_TITLE);
            if(choice == JFileChooser.APPROVE_OPTION){
                File selectedFile = fileChooser.getSelectedFile();
                if(selectedFile != null){
                    try{
                        onOpenUrl(selectedFile.toURL().toString());
                    }catch(MalformedURLException e){
                        // This should not happen.
                        throw new Error(Messages.ERROR_INTERNAL_ERROR);
                    }
                }
            }
        }

        /**
         * Handles URL Open requests
         */
        public void onOpenUrl(String url){
            model.setEntityURI(url);
        }

        /**
         * Handles File->Exit requests
         */
        public void onFileExit(){
            int choice = JOptionPane.showConfirmDialog(SwingStaticViewerPanel.this,
                                                       Messages.DIALOG_EXIT_CONFIRM);
            if(choice == JOptionPane.OK_OPTION){
                System.exit(0);
            }
        }

        /**
         * Handles Parsing Errors
         */
        public void onParsingError(SAXException e){
        }

        /**
         * Handles a change of the model's treeRoot property
         */
        public void onTreeRootChanged(){
            viewer.setTreeRoot(model.getTreeRoot());
            try{
                viewer.setTransform(new AffineTransform());
            }catch(NoninvertibleTransformException e){
                // Should never happen because identity is invertible
                throw new Error();
            }

            viewer.invalidate();
            validate();
            viewer.repaint();
        }

        /**
         * Handles simple zoom in requests
         */
        public void onZoomIn(){
            AffineTransform usr2dev = new AffineTransform(viewer.getTransform());
            usr2dev.scale(2f, 2f);
            try{
                viewer.setTransform(usr2dev);
                viewer.repaint();
            }catch(NoninvertibleTransformException e){
                JOptionPane.showMessageDialog(SwingStaticViewerPanel.this,
                                              Messages.ERROR_INTERNAL_ERROR);
            }
        }

        /**
         * Handles zoom request about a point
         */
        public void onZoomIn(Point zoomReference){
            AffineTransform usr2dev = new AffineTransform(viewer.getTransform());
            AffineTransform newTxf = new AffineTransform();

            if(zoomReference == null){
                zoomReference = new Point(0, 0);
            }

            newTxf.translate(zoomReference.x, zoomReference.y);
            newTxf.scale(2f, 2f);
            newTxf.translate(-zoomReference.x, -zoomReference.y);
            usr2dev.preConcatenate(newTxf);

            try{
                viewer.setTransform(usr2dev);
                viewer.repaint();
            }catch(NoninvertibleTransformException e){
                JOptionPane.showMessageDialog(SwingStaticViewerPanel.this,
                                              Messages.ERROR_INTERNAL_ERROR);
            }
        }

        /**
         * Handles zoom request on a specific area of interest (AOI)
         */
        public void onZoomIn(Rectangle aoi){
            AffineTransform usr2dev = new AffineTransform(viewer.getTransform());

            Dimension size = viewer.getSize();

            if( (size.width == 0) || (size.height == 0) || (aoi.width == 0)
                || (aoi.height == 0) ){
                return;
            }

            if(aoi == null){
                aoi = new Rectangle(0, 0, size.width, size.height);
            }

            System.out.println("Zooming on area of interest: " + aoi);

            // Process zoom factor
            float scaleX = size.width/(float)aoi.width;
            float scaleY = size.height/(float)aoi.height;
            float scale = scaleX < scaleY ? scaleX : scaleY;

            // Process zoom reference point
            Point zoomReference = new Point(0, 0);
            zoomReference.x = aoi.x;
            zoomReference.y = aoi.y;

            // Process zoom transform
            AffineTransform zoomTransform = new AffineTransform();
            zoomTransform.scale(scale, scale);
            zoomTransform.translate(-zoomReference.x, -zoomReference.y);

            usr2dev.preConcatenate(zoomTransform);
            try{
                viewer.setTransform(usr2dev);
                viewer.repaint();
            }catch(NoninvertibleTransformException e){
                JOptionPane.showMessageDialog(SwingStaticViewerPanel.this,
                                              Messages.ERROR_INTERNAL_ERROR);
            }
        }

        /**
         * Handles zoom out requests
         */
        public void onZoomOut(){
            AffineTransform usr2dev = new AffineTransform(viewer.getTransform());
            AffineTransform newScale = AffineTransform.getScaleInstance(.5f, .5f);
            usr2dev.preConcatenate(newScale);
            try{
                viewer.setTransform(usr2dev);
                viewer.repaint();
            }catch(NoninvertibleTransformException e){
                JOptionPane.showMessageDialog(SwingStaticViewerPanel.this,
                                              Messages.ERROR_INTERNAL_ERROR);
            }
        }

        /**
         * Handles pan requests
         */
        public void onPan(){
            System.out.println("Pan");
        }

        /**
         * Handles request to move to previous document
         */
        public void onPrevious(){
            String previousEntityURI = model.getPreviousEntityURI();
            if(previousEntityURI != null){
                model.setEntityURI(previousEntityURI);
            }
        }

        /**
         * Handles request to move to next document
         */
        public void onNext(){
            String nextEntityURI = model.getNextEntityURI();
            if(nextEntityURI != null){
                model.setEntityURI(nextEntityURI);
            }
        }

        /**
         * Handles requests to search a string in the document
         */
        public void onSearch(){
            System.out.println("Search");
        }

        /**
         * Handles request to reload current document
         */
        public void onReload(){
            String entityURI = model.getEntityURI();
            model.setEntityURI(null);
            model.setEntityURI(entityURI);
        }
    }

    /**
     * SAX Implementation used to parse SVG documents
     */
    private static final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";

    /**
     * SAC Implementation used to parse CSS data
     */
    private static final String SAC_PARSER = "org.w3c.flute.parser.Parser";


    /**
     * Unit testing
     */
    public static void main(String args[]){
        //
        // Initialize Swing Framework with desired Font default
        //
        Font font = new Font("Dialog", Font.PLAIN, 10);
        SwingInitializer.swingDefaultsFontInit(font);

        //
        // Load all fonts
        //
        /** GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        System.out.println("Initializing fonts .... please wait");
        Font fonts[] = env.getAllFonts();
        for(int i=0; i<fonts.length; i++){
            // System.out.println(fontNames[i]);
            System.out.println(fonts[i].getFamily());
        }
        System.out.println("Done initializing " + fonts.length + " fonts");
        */

        //
        // Create a JFrame and add a SwingStaticViewerPanel to it
        //
        javax.swing.JFrame frame = new javax.swing.JFrame(Messages.LABEL_VIEWER_TITLE);
        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);

        // Initialize the CSS implementation we are using with
        // a SAC Parser
        CSSDocumentHandler.setParserClassName(SAC_PARSER);

        // Initialize the DOM SVGDocumentFactory that will be used
        // to build SVG Documents. Use a specific parser
        SVGDocumentFactory documentFactory = new SVGDocumentFactory(SAX_PARSER);

        // GVTFactory. Use Batik's GVT reference implementation.
        GVTFactory gvtFactory
            = org.apache.batik.refimpl.gvt.ConcreteGVTFactory.getGVTFactoryImplementation();

        // BridgeContext
        BridgeContext bridgeContext
            = new org.apache.batik.refimpl.bridge.SVGBridgeContext();
        bridgeContext.setGVTFactory(gvtFactory);

        ParserFactory parserFactory
            = new org.apache.batik.refimpl.parser.ParserFactory();
        bridgeContext.setParserFactory(parserFactory);


        // GVT Builder. Use Batik's Bridge reference implementation builder.
        GVTBuilder gvtBuilder = new org.apache.batik.refimpl.bridge.ConcreteGVTBuilder();

        final SwingStaticViewerPanel panel
            = new SwingStaticViewerPanel(gvtBuilder,
                                         bridgeContext,
                                         documentFactory);

        frame.getContentPane().add(panel);
        frame.pack();

        // If an argument was passed, assume it is a URI
        if(args.length > 0){
            panel.model.setEntityURI(args[0]);
        }

        //
        // By default, make size 80% of screen size
        //
        Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
        Dimension initialSize = Toolkit.getDefaultToolkit().getScreenSize();
        initialSize.width *= 80;
        initialSize.width /= 100;
        initialSize.height *= 80;
        initialSize.height /= 100;

        if(initialSize.width <= 0){
            initialSize.width = 1;
        }
        if(initialSize.height <= 0){
            initialSize.height = 1;
        }

        Point location = new Point(0, 0);
        location.x = (screenSize.width - initialSize.width)/2;
        location.y = (screenSize.height - initialSize.height)/2;

        frame.setSize(initialSize);
        frame.setLocation(location);

        //
        // Make system window exit same as File->Exit
        //
        frame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent evt){
                    panel.controller.onFileExit();
                }
            });


        //
        // Set the frame icon
        //
        URL viewerIconURL = ClassLoader.getSystemResource(ICON_VIEWER);
        frame.setIconImage((new ImageIcon(viewerIconURL)).getImage());

        frame.setVisible(true);
    }
}
