/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.util;

import java.awt.Component;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.NoninvertibleTransformException;

import java.awt.image.BufferedImage;

import java.text.CharacterIterator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentViewport;

import org.apache.batik.dom.svg.SVGDocumentLoader;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeTreeIterator;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.Selector;

import org.apache.batik.gvt.event.AbstractEventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.SelectionListener;
import org.apache.batik.gvt.event.SelectionEvent;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

import org.apache.batik.refimpl.bridge.BufferedDocumentLoader;
import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.refimpl.bridge.DefaultUserAgent;
import org.apache.batik.refimpl.bridge.SVGBridgeContext;

import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;

import org.apache.batik.refimpl.gvt.text.ConcreteTextSelector;

import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.renderer.DynamicRenderer;
import org.apache.batik.refimpl.gvt.renderer.DynamicRendererFactory;

import org.apache.batik.refimpl.parser.ParserFactory;

import org.apache.batik.refimpl.script.ConcreteInterpreterPool;

import org.apache.batik.util.SVGUtilities;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.MissingListenerException;

import org.w3c.dom.Element;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;


/**
 * This class represents a JComponent which is able to represents
 * a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class JSVGCanvas
    extends    JComponent
    implements ActionMap,
               DynamicRenderer.RepaintHandler,
               SelectionListener {
    // The actions names.
    public final static String UNZOOM_ACTION = "UnzoomAction";
    public final static String ZOOM_IN_ACTION = "ZoomInAction";
    public final static String ZOOM_OUT_ACTION = "ZoomOutAction";

    /**
     * The cursor for panning.
     */
    public final static Cursor PAN_CURSOR =
        new Cursor(Cursor.MOVE_CURSOR);

    /**
     * The cursor for zooming.
     */
    public final static Cursor ZOOM_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The cursor for rotating.
     */
    public final static Cursor ROTATE_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The default cursor.
     */
    public final static Cursor NORMAL_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The cursor indicating that an operation is pending.
     */
    public final static Cursor WAIT_CURSOR =
        new Cursor(Cursor.WAIT_CURSOR);

    /**
     * The cursor which has been most recently requested by a background thread.
     */
    public Cursor requestedCursor = NORMAL_CURSOR;

    /**
     * The global offscreen buffer.
     */
    protected BufferedImage globalBuffer;

    /**
     * The current offscreen buffer.
     */
    protected BufferedImage buffer;

    /**
     * Root of the GVT tree displayed by this viewer
     */
    protected GraphicsNode gvtRoot;

    /**
     * The current renderer.
     *
     * protected Renderer renderer;
     * NOTE: Removed,  no longer a state variable!
     */

    /**
     * The renderer factory.
     */
    protected RendererFactory rendererFactory;

    /**
     * The GVT builder.
     */
    protected GVTBuilder builder;

    /**
     * The SVG document to render.
     */
    protected SVGDocument document;

    /**
     * Must the buffer be updated?
     */
    protected boolean bufferNeedsRendering;

    /**
     * Is there a document load pending?
     */
    protected boolean isLoadPending = false;

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * The transform to apply to the graphics object.
     */
    protected AffineTransform transform = new AffineTransform();

    /**
     * The transform representing the pan tranlate.
     */
    protected AffineTransform panTransform;

    /**
     * The transform representing the rotation.
     */
    protected AffineTransform rotateTransform;

    /**
     * The transform representing the previous rotation.
     */
    protected AffineTransform previousRotateTransform;

    /**
     * The pan bounding box.
     */
    protected Shape docBBox = null;

    /**
     * The zoom marker top line.
     */
    protected Line2D markerTop;

    /**
     * The zoom marker left line.
     */
    protected Line2D markerLeft;

    /**
     * The zoom marker bottom line.
     */
    protected Line2D markerBottom;

    /**
     * The zoom marker right line.
     */
    protected Line2D markerRight;

    /**
     * The rotate marker.
     */
    protected Shape rotateMarker;

    /**
     * The selection highlight shape.
     */
    protected Shape selectionHighlightShape = null;

    /**
     * The selection highlight shape, in canvas space coords.
     */
    private Shape canvasSpaceHighlightShape = null;

    /**
     * The thumbnail canvas.
     */
    protected ThumbnailCanvas thumbnailCanvas;

    /**
     * The parser factory.
     */
    protected ParserFactory parserFactory = new ParserFactory();

    /**
     * The text selector.
     */
    protected Selector textSelector;

    /**
     * The zoom handler.
     */
    protected ZoomHandler zoomHandler;

    /**
     * The rotation angle
     */
    protected double rotateAngle;

    /**
     * The rotation cosinus
     */
    protected double rotateCos = 1;

    /**
     * The initial scale factor.
     */
    protected double initialScale = 1;

    /**
     * Fires periodic repaints while buffer is rendered
     */
    protected boolean progressiveRenderingEnabled = true;

    /**
     * Used to draw marker
     */
    protected BasicStroke markerStroke
        = new BasicStroke(1, BasicStroke.CAP_SQUARE,
                          BasicStroke.JOIN_MITER,
                          10,
                          new float[]{4, 4}, 0);

    /**
     * An object used to synchronize access to loadPending flag.
     */
    protected Object loadPendingLock = new Object();


    /**
     * The background rendering thread instance - should be only one alive.
     */
    private Thread backgroundRenderThread = null;

    /**
     * The background gvt building thread instance - should be only one alive.
     */
    private Thread backgroundBuilderThread = null;

    /**
     * Creates a new SVG canvas.
     */
    public JSVGCanvas() {
        this(new DefaultUserAgent());
    }

    /**
     * Creates a new SVG canvas.
     */
    public JSVGCanvas(UserAgent ua) {
        userAgent = ua;

        // for event dispatching inside GVT
        AbstractEventDispatcher dispatcher =
            (AbstractEventDispatcher)userAgent.getEventDispatcher();
        if (dispatcher != null) {
            //addMouseListener(dispatcher);
            //addMouseMotionListener(dispatcher);
            addKeyListener(dispatcher);
        }

        rendererFactory = new DynamicRendererFactory();

        builder = new ConcreteGVTBuilder();

        addComponentListener(new CanvasListener());
        MouseListener ml = new MouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);

        listeners.put(UNZOOM_ACTION, new UnzoomAction());
        listeners.put(ZOOM_IN_ACTION, new ZoomInAction());
        listeners.put(ZOOM_OUT_ACTION, new ZoomOutAction());
    }

    /**
     * Sets the renderer factory to use to create the renderer.
     */
    public void setRendererFactory(RendererFactory rf) {
        rendererFactory = rf;
        repaint();
    }

    /**
     * Sets the value of the progressive rendering flag:
     * true enables progressive rendering, false disables it.
     */
    public void setProgressiveRenderingEnabled(boolean value) {
        progressiveRenderingEnabled = value;
    }

    /**
     * Returns boolean indicating whether progressive rendering is enabled.
     */
    public boolean isProgressiveRenderingEnabled() {
        return progressiveRenderingEnabled;
    }

    /**
     * Sets the value of a flag indicating whether there is a load pending:
     */
    public void setIsLoadPending(boolean value) {
        synchronized (loadPendingLock) {
            isLoadPending = value;
        }
    }

    /**
     * Returns boolean indicating whether there is a document load pending.
     */
    public boolean isLoadPending() {
        synchronized (loadPendingLock) {
            return isLoadPending;
        }
    }

    /**
     * Sets the zoom handler.
     */
    public void setZoomHandler(ZoomHandler h) {
        zoomHandler = h;
    }

    public interface ZoomHandler {
        public void zoomChanged(float f);
    }

    /**
     * Returns the current renderer factory.
     */
    public RendererFactory getRendererFactory() {
        return rendererFactory;
    }

    /**
     * Sets the SVG document to display.
     * @param doc if null, clears the canvas.
     */
    public void setSVGDocument(final SVGDocument doc) {

        if ((backgroundRenderThread != null) &&
                       (backgroundRenderThread.isAlive())) {
                backgroundRenderThread.interrupt();
        }
        if ((backgroundBuilderThread != null) &&
                       (backgroundBuilderThread.isAlive())) {
                backgroundBuilderThread.interrupt();
        }

        if (document != null) {
            // fire the unload event
            Event evt = document.createEvent("SVGEvents");
            evt.initEvent("SVGUnload", false, false);
            ((EventTarget)(document.getRootElement())).dispatchEvent(evt);
        }
        if (doc == null) {
            document = doc;
            setRootNode(null, null, null);
            clearSelection();
        } else {
            setIsLoadPending(true);
            requestCursor(WAIT_CURSOR);
            // HACK: we do both in case there is a pending paint
            setCursor(WAIT_CURSOR);
            backgroundBuilderThread = new Thread() {
                public void run() {
                    try {
                        GraphicsNode root;
                        BridgeContext bridgeContext = createBridgeContext(doc);
                        bridgeContext.setViewCSS(
                           (ViewCSS)((SVGOMDocument)doc).getDefaultView());
                        bridgeContext.setGVTBuilder(builder);
                        long t1 = System.currentTimeMillis();
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        root = builder.build(bridgeContext, doc);
                        initSelectors(root);
                        bridgeContext.getDocumentLoader().dispose();
                        long t2 = System.currentTimeMillis();

                        System.out.println("---- GVT tree construction ---- " +
                                       (t2 - t1) + " ms");

                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        } else {
                            EventQueue.invokeAndWait
                                (new RootNodeChangedRunnable
                                     (root, bridgeContext, doc));
                        }
                    } catch (InterruptedException ie) {
                    } catch (java.lang.reflect.InvocationTargetException ite) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //requestCursor(NORMAL_CURSOR); Don't reset until buffer paint completes.
                }
            };
            backgroundBuilderThread.setPriority(Thread.MIN_PRIORITY);
            backgroundBuilderThread.start();
        }

    }

    class RootNodeChangedRunnable implements Runnable {

        GraphicsNode root;
        BridgeContext bridgeContext;
        SVGDocument document;

        public RootNodeChangedRunnable(GraphicsNode newRoot,
                                       BridgeContext bridgeContext,
                                       SVGDocument doc) {
            this.root = newRoot;
            this.bridgeContext = bridgeContext;
            this.document = doc;
        }

        public void run() {
            setRootNode(root, bridgeContext, document);
            setIsLoadPending(false);
            //requestCursor(NORMAL_CURSOR);
        }
    }

    public void setRootNode(GraphicsNode root, BridgeContext bridgeContext,
                            SVGDocument document) {

        gvtRoot = root;  // based on event notification
        JSVGCanvas.this.document = document;

        if (root != null) {
            computeTransform();
            // <!> HACK maybe not the right place to dispatch
            // this event
            // fire the load event
            Event evt = document.createEvent("SVGEvents");
            evt.initEvent("SVGLoad", false, false);
            ((EventTarget)(document.getRootElement())).dispatchEvent(evt);
            ((EventTarget)document).addEventListener("DOMAttrModified",
                                       new MutationListener(bridgeContext), false);
            if (userAgent.getEventDispatcher() != null) {
                userAgent.getEventDispatcher().setRootNode(gvtRoot);
            }
        }
        rotateAngle = 0;
        rotateCos = 1;
        previousRotateTransform = null;
        if (zoomHandler != null) {
            zoomHandler.zoomChanged(1);
        }
        bufferNeedsRendering = true;
        if (thumbnailCanvas != null) {
             thumbnailCanvas.fullRepaint();
        }
        repaint(); // scheduled after the thumbnail repaint
    }

    /**
     * Notifies that the specified area of interest need to be repainted.
     * @param aoi the area of interest to repaint
     */
    public void notifyRepaintedRegion(Shape oldAoi, Shape newAoi,
                                                    Renderer renderer) {

        // XXX: this is not threaded yet, so it will block AWT!

        clearBuffer(oldAoi);
        clearBuffer(newAoi);
        try {
            renderer.repaint(oldAoi);
            renderer.repaint(newAoi);
            Rectangle2D r = transform.createTransformedShape(oldAoi).getBounds();
            repaint((int)r.getX()-1, (int)r.getY()-1,
                    (int)r.getWidth()+2, (int)r.getHeight()+2);
            r = transform.createTransformedShape(newAoi).getBounds();
            repaint((int)r.getX()-1, (int)r.getY()-1,
                    (int)r.getWidth()+2, (int)r.getHeight()+2);
        } catch (InterruptedException ie) {
        }
    }

    /**
     * Sets the value of the requested cursor, but in a thread-safe way.
     * The requested cursor will be set during the next call to paintComponent.
     */
    protected synchronized void requestCursor(Cursor newCursor) {
        this.requestedCursor = newCursor;
    }

    /**
     * Gets the value of the requested cursor, but in a thread-safe way.
     * The requested cursor will be set during the next call to paintComponent.
     */
    protected synchronized Cursor getRequestedCursor() {
        return requestedCursor;
    }

    /**
     * To listener to the DOM mutation events.
     */
    protected class MutationListener implements EventListener {

        BridgeContext bridgeContext;

        public MutationListener(BridgeContext bridgeContext) {
            this.bridgeContext = bridgeContext;
        }

        public void handleEvent(Event evt) {
            BridgeMutationEvent bme;
            Element target = (Element)evt.getTarget();
            bme = new BridgeMutationEvent
                (target,
                 bridgeContext,
                 BridgeMutationEvent.PROPERTY_MUTATION_TYPE);

            MutationEvent me = (MutationEvent)evt;

            bme.setAttrName(me.getAttrName());
            bme.setAttrNewValue(me.getNewValue());

            GraphicsNodeBridge bridge;
            bridge = (GraphicsNodeBridge)bridgeContext.getBridge(target);

            bridge.update(bme);
        }
    }

    /**
     * Creates a new bridge context.
     */
    protected BridgeContext createBridgeContext(SVGDocument doc) {
        BridgeContext result = new SVGBridgeContext();
        result.setDocumentLoader
            (new BufferedDocumentLoader
             (new SVGDocumentLoader(userAgent.getXMLParserClassName())));
        result.setGVTFactory(ConcreteGVTFactory.getGVTFactoryImplementation());
        result.setParserFactory(parserFactory);
        result.setUserAgent(userAgent);
        result.setGraphicsNodeRableFactory
            (new ConcreteGraphicsNodeRableFactory());
        ((SVGBridgeContext)result).setInterpreterPool
            (new ConcreteInterpreterPool(doc));
        result.setCurrentViewport(new UserAgentViewport(userAgent));
        return result;
    }

    /**
     * Returns the thumbnail.
     */
    public JComponent getThumbnail() {
        if (thumbnailCanvas == null) {
            thumbnailCanvas = new ThumbnailCanvas();
        }
        return thumbnailCanvas;
    }

    /**
     * Associate selectable elements in the current tree with
     * Selector instances.
     */
    public void initSelectors(GraphicsNode treeRoot) {
        Iterator nodeIter = new GraphicsNodeTreeIterator(treeRoot);
        if (textSelector == null) {
            textSelector =
                new ConcreteTextSelector(
                    getRendererFactory().getRenderContext());
            textSelector.addSelectionListener(this);
        }
        while (nodeIter.hasNext()) {
            GraphicsNode node = (GraphicsNode) nodeIter.next();
            if (node instanceof Selectable) {
                node.addGraphicsNodeMouseListener(
                             (GraphicsNodeMouseListener) textSelector);
                // should make sure this does not add duplicates
            }
        }
        clearSelection();
    }

    // SelectionListener /////////////////////////////////////////////


    public void selectionChanged(SelectionEvent e) {
        Graphics g = getGraphics();
        paintOverlays(g);
        if (e.getType() == SelectionEvent.SELECTION_CHANGED) {
            selectionHighlightShape = e.getHighlightShape();
            canvasSpaceHighlightShape =
                transform.createTransformedShape(selectionHighlightShape);
            paintOverlays(g);
        } else if (e.getType() == SelectionEvent.SELECTION_DONE) {
            // notify user agent
            selectionHighlightShape = e.getHighlightShape();
            canvasSpaceHighlightShape =
                transform.createTransformedShape(selectionHighlightShape);
            String selectionString = getSelectionDescription(e.getSelection());
            if (selectionString != null) {
                 userAgent.displayMessage("Selection: "+selectionString);
            }
            repaint(); // in case immediate-mode XORs are out of sync
                       // with the regular repaint-on-request repaints
        } else if (e.getType() == SelectionEvent.SELECTION_CLEARED) {
            userAgent.displayMessage("");
            canvasSpaceHighlightShape = null;
            selectionHighlightShape = null;
            // change highlight and notify user agent
        }
    }

    private void clearSelection() {
        selectionChanged(
            new SelectionEvent(null, SelectionEvent.SELECTION_CLEARED, null));
    }

    private String getSelectionDescription(Object o) {
        String label=null;
        if (o instanceof CharacterIterator) {
            CharacterIterator iter = (CharacterIterator) o;
            char[] cbuff = new char[iter.getEndIndex()-iter.getBeginIndex()];
            if (cbuff.length > 0) {
                cbuff[0] = iter.first();
            }
            for (int i=1; i<cbuff.length;++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
        }
        return label;
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
     * Clears the offscreen buffer.
     */
    protected void clearBuffer(int w, int h) {
        Graphics2D g = buffer.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setClip(0, 0, w, h);
        g.setPaint(Color.white);
        g.fillRect(0, 0, w, h);
    }


    /**
     * Clears the offscreen buffer.
     */
    protected void clearBuffer(Shape aoi) {
        Graphics2D g = buffer.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setClip(aoi.getBounds());
        g.setPaint(Color.white);
        g.fill(aoi);
    }

    /**
     * Clears a specific buffer.
     */
    protected void clearBuffer(BufferedImage buffer, int w, int h) {
        Graphics2D g = buffer.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setClip(0, 0, w, h);
        g.setPaint(Color.white);
        g.fillRect(0, 0, w, h);
    }

   /**
    * @return the area of interest displayed in the viewer, in usr space.
    */
    protected Shape getAreaOfInterest(Shape devAOI){
        AffineTransform dev2usr = null;
        try {
            dev2usr = transform.createInverse();
        } catch(NoninvertibleTransformException e){
            // This should not happen. See setTransform
            throw new Error(e.getMessage());
        }

        /*
         * System.out.println("devAOI : " + devAOI);
         * System.out.println("usrAOI : " +
         *    dev2usr.createTransformedShape(devAOI).getBounds2D());
         * System.out.println("devAOI 2: " +
         *   transform.createTransformedShape(dev2usr.createTransformedShape(
         * devAOI)).getBounds2D());
         */

        return dev2usr.createTransformedShape(devAOI);
    }

    /**
     * Paints this component.
     */
    protected void paintComponent(Graphics g) {

        if (!EventQueue.isDispatchThread()) {
            System.err.println(
              "ERROR: JSVGCanvas paintComponent() called outside AWT thread!");
        }

        // TODO: simplify and clean up this code

        super.paintComponent(g);

        Dimension size = getSize();
        int w = size.width;
        int h = size.height;

        if (w < 1 || h < 1) {
            return;
        }

        // Check to see if a cursor request has been queued before a repaint()

        Cursor rCursor = getRequestedCursor();
        if (getCursor() != rCursor) {
            setCursor(rCursor);
        }
        updateBuffer(w, h);

        Renderer renderer = null;

        if (bufferNeedsRendering) {
            renderer = rendererFactory.createRenderer(buffer);
            ((DynamicRenderer)renderer).setRepaintHandler(this);
            renderer.setTransform(transform);
        }
        if (renderer != null && gvtRoot != null &&
            renderer.getTree() != gvtRoot) {
            renderer.setTree(gvtRoot);
            bufferNeedsRendering = true;
        }
        if (bufferNeedsRendering) {
            Graphics2D g2d = (Graphics2D)g;
            if (panTransform != null) {
                int tx = (int)panTransform.getTranslateX();
                int ty = (int)panTransform.getTranslateY();
                paintPanRegions(g2d, tx, ty, w, h);
                g2d.transform(panTransform);
                panTransform = null;
                g2d.drawImage(buffer, null, 0, 0);
            } else {
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setClip(0, 0, w, h);
                g2d.setPaint(Color.white);
                g2d.fillRect(0, 0, w, h);
            }
            clearBuffer(w, h);
            renderer.setTransform(transform);
            repaintAOI(renderer, size, buffer);
            bufferNeedsRendering = false; // repaint is already queued
            return;
        } else { // buffer is current, just transform and draw it

            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
            if (panTransform != null) {
                int tx = (int)panTransform.getTranslateX();
                int ty = (int)panTransform.getTranslateY();
                paintPanRegions(g2d, tx, ty, w, h);
                g2d.transform(panTransform);
            }
            g2d.drawImage(buffer, null, 0, 0);

            // Note that the above assumes that read access to buffer
            // is safe while background threads may still be working...
            // which may be overly optimistic.

            paintOverlays(g);
        }
    }


    protected static Color selectionXORColor = Color.black;

    /**
     * Paints the canvas "overlay" primitives
     * (rotation box, zoom AOI box, highlight [not yet implemented], etc.
     */
    protected void paintOverlays(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setXORMode(Color.white);
        if (markerTop != null) {
            g2d.setColor(Color.black);
            g2d.setStroke(markerStroke);
            g2d.draw(markerTop);
            g2d.draw(markerLeft);
            g2d.draw(markerBottom);
            g2d.draw(markerRight);
        } else if (rotateMarker != null) {
            g2d.setColor(Color.black);
            g2d.setStroke(markerStroke);
            g2d.draw(rotateMarker);
        } else if ((panTransform != null) && (docBBox != null)) {
            g2d.setColor(Color.black);
            g2d.setStroke(markerStroke);
            g2d.draw(docBBox);
        }
        if (canvasSpaceHighlightShape != null) {
            g2d.setColor(selectionXORColor);
            g2d.fill(canvasSpaceHighlightShape);
        }
        g2d.setXORMode(Color.white);
    }


    /**
     * Repaints the pan blank regions.
     */
    protected void paintPanRegions(Graphics2D g2d, int tx, int ty,
                                   int w, int h) {
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setPaint(Color.white);
        if (tx > 0) {
            if (ty > 0) {
                g2d.fillRect(0, 0, w, ty);
                g2d.fillRect(0, ty, tx, h - ty);
            } else {
                g2d.fillRect(0, 0, tx, h);
                g2d.fillRect(tx, h + ty, w - tx, -ty);
            }
        } else {
            if (ty > 0) {
                g2d.fillRect(0, 0, w, ty);
                g2d.fillRect(w + tx, ty, -tx, h - ty);
            } else {
                g2d.fillRect(0, h + ty, w, -ty);
                g2d.fillRect(w + tx, 0, -tx, h + ty);
            }
        }
    }

    /**
     * Updates the offscreen buffer.
     * @param w&nbsp;h The size of the component.
     */
    protected void updateBuffer(int w, int h) {
        // Create a new buffer if needed.
        if (globalBuffer == null ||
            globalBuffer.getWidth() < w ||
            globalBuffer.getHeight() < h) {
            if (globalBuffer != null) {
                globalBuffer.flush();
            }
            globalBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            buffer = globalBuffer;
            bufferNeedsRendering = true;
        } else if (buffer.getWidth() != w ||
                   buffer.getHeight() != h) {
            buffer = globalBuffer.getSubimage(0, 0, w, h);
            bufferNeedsRendering = true;
        }
    }

    /**
     * Returns the current buffer.
     */
    public BufferedImage getBuffer() {
        return buffer;
    }

    /**
     * Computes the value of the transform attribute.
     */
    protected void computeTransform() {
        SVGSVGElement elt = document.getRootElement();
        Dimension size = getSize();
        int w = size.width;
        int h = size.height;

        transform = SVGUtilities.getPreserveAspectRatioTransform
            (elt, w, h, parserFactory);

        initialScale = transform.getScaleX();
        updateBaseTransform();
    }

    protected void updateBaseTransform() {
        canvasSpaceHighlightShape =
                transform.createTransformedShape(selectionHighlightShape);
        // for event dispatching inside GVT with the right transformer
        AbstractEventDispatcher dispatcher =
            (AbstractEventDispatcher)userAgent.getEventDispatcher();
        if (dispatcher != null) {
            try {
                if (gvtRoot != null) {
                    docBBox = transform.createTransformedShape(
                                            gvtRoot.getBounds());
                }
                dispatcher.setBaseTransform(transform.createInverse());
            } catch (NoninvertibleTransformException e) {
                // this should not happen
                throw new Error();
            }
        }
    }

    /**
     * To repaint the buffer.
     */
    protected void repaintAOI(Renderer renderer, Dimension size,
                                                 BufferedImage buffer) {

        Shape aoi = getAreaOfInterest(
                        new Rectangle(0, 0, size.width, size.height));
        Thread t = new Thread(
                   new RenderBufferAOIRunnable(
                            renderer, aoi, buffer, size, JSVGCanvas.this));
        t.setPriority(Thread.MIN_PRIORITY);
        if ((backgroundRenderThread != null) &&
                       (backgroundRenderThread.isAlive())) {
                backgroundRenderThread.interrupt();
        }
        backgroundRenderThread = t;
        t.start();
    }

    private static final int REPAINT_DONE = 0;

    private static final int REPAINT_THUMBNAIL_PENDING = 1;

    /**
     *
     * Methods are not synchronized since access it intended
     * to occur within a synchronized block.
     */
    class RepaintState {
        private int i;
        public RepaintState(int i) {
            this.i = i;
        }
        public void setValue(int i) {
            this.i = i;
        }
        public int getValue() {
            return i;
        }
    }

    private RepaintState repaintState = new RepaintState(REPAINT_DONE);

    class RenderBufferAOIRunnable implements Runnable {

        private Shape aoi;
        private Renderer renderer;
        private BufferedImage buffer;
        private Dimension size;
        private Component component;

        RenderBufferAOIRunnable(Renderer renderer,
                                Shape aoi,
                                BufferedImage buffer,
                                Dimension size,
                                Component component) {
            this.renderer = renderer;
            this.aoi = aoi;
            this.buffer = buffer; // Hack, should be able to get this from renderer
            this.size = size;
            this.component = component;
        }

        public void run() {
            long t1 = System.currentTimeMillis();
            requestCursor(WAIT_CURSOR);
            synchronized (repaintState) {
                if ((repaintState.getValue() == REPAINT_THUMBNAIL_PENDING)
                    && !(component instanceof JSVGCanvas.ThumbnailCanvas)) {
                    try {
                        repaintState.wait(5000);
                        // timeout prevents deadlock if something goes wrong
                    } catch (InterruptedException ie) {
                    }
                }
                // if we don't synchronize, we can accidentlly
                // paint twice after clearing twice
                if (!Thread.currentThread().isInterrupted()) {
                    clearBuffer(buffer, (int) (this.size.getWidth()),
                                    (int) (this.size.getHeight()));
                    RepaintTimer timer = null;
                    if (progressiveRenderingEnabled) {
                        timer = new RepaintTimer(component, 1000);
                        timer.start();
                    }
                    try {
                        renderer.repaint(aoi);
                    } catch (InterruptedException ie) {
                    } catch (NoClassDefFoundError ncdfe) {
                        // BUG: something is not initialized or
                        // got destroyed in the interrupted thread
                        // not a disaster since another repaint will follow
                        System.out.println("(AOI repaint did not complete.)");
                    } catch (Exception e) {
                        // don't quit altogether...
                        e.printStackTrace();
                        // TODO: ? re-try ?
                    } finally {
                        if (timer != null) {
                            timer.interrupt();
                        }
                        repaintState.setValue(REPAINT_DONE);
                        repaintState.notifyAll();
                        // always de-register as a listener, to avoid
                        // memory leak
                        renderer.dispose();
                        renderer = null;
                    }
                }
            }
            if (!Thread.currentThread().isInterrupted()) {
                long t2 = System.currentTimeMillis();
                System.out.println("----------- Rendering --------- " +
                               (t2 - t1) + " ms");
                if (!isLoadPending()) {
                    requestCursor(NORMAL_CURSOR);
                }
                component.repaint();
            }
        }
    }

    /**
     * Timer class which fires periodic repaint request while buffer is being painted.
     */
    protected class RepaintTimer extends Thread {

        Component target;
        long interval;

        public RepaintTimer(Component target, long interval) {
            this.target = target;
            this.interval = interval;
            setPriority(MIN_PRIORITY + 1);
        }

        public void run() {
            try {
                sleep(interval);
                while (!interrupted()) {
                   target.repaint();
                   sleep(interval);
                }
            } catch (InterruptedException ie) {
            }
        }
    }

    /**
     * To correctly resize the view.
     */
    protected class CanvasListener extends ComponentAdapter {
        public CanvasListener() {}
        public void componentResized(ComponentEvent e) {
            if (gvtRoot == null) {
                return;
            }
            computeTransform();
            rotateAngle = 0;
            rotateCos = 1;
            previousRotateTransform = null;
            if (zoomHandler != null) {
                zoomHandler.zoomChanged(1);
            }
            if (thumbnailCanvas != null) {
                thumbnailCanvas.repaint();
            }
        }
    }

    /**
     * To reset the zoom.
     */
    public class UnzoomAction extends AbstractAction {
        public UnzoomAction() {}
        public void actionPerformed(ActionEvent e) {
            computeTransform();
            rotateAngle = 0;
            rotateCos = 1;
            previousRotateTransform = null;
            if (zoomHandler != null) {
                zoomHandler.zoomChanged(1);
            }
            bufferNeedsRendering = true;
            repaint();
            if (thumbnailCanvas != null) {
                thumbnailCanvas.repaint();
            }
        }
    }

    /**
     * To zoom in the document.
     */
    public class ZoomInAction extends AbstractAction {
        public ZoomInAction() {}
        public void actionPerformed(ActionEvent e) {
            transform.preConcatenate
                (AffineTransform.getScaleInstance(2, 2));
            updateBaseTransform();
            if (zoomHandler != null) {
                zoomHandler.zoomChanged((float)(transform.getScaleX() /
                                                rotateCos / initialScale));
            }
            bufferNeedsRendering = true;
            repaint();
            if (thumbnailCanvas != null) {
                thumbnailCanvas.repaint();
            }
        }
    }

    /**
     * To zoom out the document.
     */
    public class ZoomOutAction extends AbstractAction {
        public ZoomOutAction() {}
        public void actionPerformed(ActionEvent e) {
            transform.preConcatenate
                (AffineTransform.getScaleInstance(0.5, 0.5));
            updateBaseTransform();
            if (zoomHandler != null) {
                zoomHandler.zoomChanged((float)(transform.getScaleX() /
                                                rotateCos / initialScale));
            }
            bufferNeedsRendering = true;
            repaint();
            if (thumbnailCanvas != null) {
                thumbnailCanvas.repaint();
            }
        }
    }

    /**
     * To handle the mouse events.
     */
    protected class MouseListener implements java.awt.event.MouseListener,
                                             MouseMotionListener {

        protected Cursor cursor;
        protected int sx;
        protected int sy;
        protected boolean mouseExited;

        public MouseListener() {}

        public void mouseClicked(MouseEvent e) {
            AbstractEventDispatcher dispatcher =
                (AbstractEventDispatcher)userAgent.getEventDispatcher();
            clearSelection();
            dispatcher.mouseClicked(e);
        }

        public void mouseEntered(MouseEvent e) {
            AbstractEventDispatcher dispatcher =
                (AbstractEventDispatcher)userAgent.getEventDispatcher();
            dispatcher.mouseEntered(e);
        }

        public void mousePressed(MouseEvent e) {
            int mods = e.getModifiers();
            boolean operationPerformed = false;
            if ((mods & e.BUTTON1_MASK) != 0) {
                mouseExited = false;
                sx = e.getX();
                sy = e.getY();
                if ((mods & e.SHIFT_MASK) != 0) {
                    if (cursor == null) {
                        cursor = getCursor();
                    }
                    requestCursor(PAN_CURSOR);
                    setCursor(PAN_CURSOR);
                    panTransform = new AffineTransform();
                    operationPerformed = true;
                } else if ((mods & e.CTRL_MASK) != 0) {
                    if (cursor == null) {
                        cursor = getCursor();
                    }
                    requestCursor(ZOOM_CURSOR);
                    setCursor(ZOOM_CURSOR);
                    operationPerformed = true;
                }
            } else if ((mods & e.BUTTON3_MASK) != 0) {
                mouseExited = false;
                sx = e.getX();
                sy = e.getY();
                if ((mods & e.CTRL_MASK) != 0) {
                    if (cursor == null) {
                        cursor = getCursor();
                    }
                    requestCursor(ROTATE_CURSOR);
                    setCursor(ROTATE_CURSOR);
                    previousRotateTransform = rotateTransform;
                    rotateTransform = new AffineTransform();
                    paintRotateMarker(sx, sy);
                    operationPerformed = true;
                }
            }
            if (!operationPerformed) {
                clearSelection();
                // nothing done so forward the event to the dispatcher
                AbstractEventDispatcher dispatcher =
                    (AbstractEventDispatcher)userAgent.getEventDispatcher();
                if (dispatcher != null) {
                    dispatcher.mouseEntered(e);
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            int mods = e.getModifiers();
            boolean operationPerformed = false;
            if ((mods & e.BUTTON1_MASK) != 0) {
                if ((mods & e.SHIFT_MASK) != 0 && panTransform != null) {
                    operationPerformed = true;
                    if (!mouseExited) {
                        int x = e.getX();
                        int y = e.getY();
                        panTransform.translate(x - sx, y - sy);
                        sx = x;
                        sy = y;
                        repaint();
                    }
                } else if ((mods & e.CTRL_MASK) != 0) {
                    operationPerformed = true;
                    paintZoomMarker(e.getX(), e.getY());
                }
            } else if ((mods & e.BUTTON3_MASK) != 0) {
                if ((mods & e.CTRL_MASK) != 0) {
                    operationPerformed = true;
                    paintRotateMarker(e.getX(), e.getY());
                }
            }
            if (!operationPerformed) {
                // nothing done so forward the event to the dispatcher
                AbstractEventDispatcher dispatcher =
                    (AbstractEventDispatcher)userAgent.getEventDispatcher();
                if (dispatcher != null) {
                    dispatcher.mouseEntered(e);
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            int mods = e.getModifiers();
            boolean operationPerformed = false;
            if ((mods & e.BUTTON1_MASK) != 0) {
                if (cursor != null) {
                    operationPerformed = true;
                    endOperation(e.getX(), e.getY());
                }
            } else if ((mods & e.BUTTON3_MASK) != 0) {
                if (cursor != null) {
                    operationPerformed = true;
                    endOperation(e.getX(), e.getY());
                }
            }
            if (!operationPerformed) {
                // nothing done so forward the event to the dispatcher
                AbstractEventDispatcher dispatcher =
                    (AbstractEventDispatcher)userAgent.getEventDispatcher();
                if (dispatcher != null) {
                    dispatcher.mouseEntered(e);
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
            AbstractEventDispatcher dispatcher =
                (AbstractEventDispatcher)userAgent.getEventDispatcher();
            dispatcher.mouseMoved(e);
        }

        public void mouseExited(MouseEvent e) {
            mouseExited = true;
            markerTop = null;
            markerLeft = null;
            markerBottom = null;
            markerRight = null;
            panTransform = null;
            repaint();
            AbstractEventDispatcher dispatcher =
                (AbstractEventDispatcher)userAgent.getEventDispatcher();
            dispatcher.mouseExited(e);
        }

        protected void endOperation(int x, int y) {
            if (cursor != null) {
                requestCursor(cursor);
                setCursor(cursor);
            }
            cursor = null;

            if (mouseExited) {
                repaint();
                return;
            }

            if (panTransform != null) {
                panTransform.translate(x - sx, y - sy);
                transform.preConcatenate(panTransform);
                updateBaseTransform();
                bufferNeedsRendering = true;
                repaint();
                if (thumbnailCanvas != null) {
                    thumbnailCanvas.repaint();
                }
            } else if (markerTop != null) {
                clearZoomMarker();
                Dimension size = getSize();

                if (x < sx) {
                    int tmp = x;
                    x = sx;
                    sx = tmp;
                }

                if (y < sy) {
                    int tmp = y;
                    y = sy;
                    sy = tmp;
                }

                // Process zoom factor
                float scaleX = size.width / (float)(x - sx);
                float scaleY = size.height / (float)(y - sy);
                float scale = (scaleX < scaleY) ? scaleX : scaleY;

                // Process zoom transform
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                at.translate(-sx, -sy);

                transform.preConcatenate(at);
                updateBaseTransform();
                if (zoomHandler != null) {
                    zoomHandler.zoomChanged((float)(transform.getScaleX() /
                                                    rotateCos / initialScale));
                }
                bufferNeedsRendering = true;
                repaint();
                if (thumbnailCanvas != null) {
                    thumbnailCanvas.repaint();
                }
            } else if (rotateMarker != null) {
                clearRotateMarker();
                if (previousRotateTransform != null) {
                    try {
                        transform.preConcatenate(
                                       previousRotateTransform.createInverse());
                    } catch(NoninvertibleTransformException ex) {}
                    previousRotateTransform = null;
                }
                transform.preConcatenate(rotateTransform);
                updateBaseTransform();
                if (zoomHandler != null) {
                    zoomHandler.zoomChanged((float)(transform.getScaleX() /
                                                    rotateCos / initialScale));
                }
                bufferNeedsRendering = true;
                repaint();
                if (thumbnailCanvas != null) {
                    thumbnailCanvas.repaint();
                }
            }
        }

        protected void paintRotateMarker(int x, int y) {
            clearRotateMarker();

            if (mouseExited) {
                rotateMarker = null;
            } else {
                Dimension dim = getSize();
                int w = dim.width / 3;
                int h = dim.height / 3;

                double dx = x - dim.width / 2;
                double dy = y - dim.height / 2;
                double cos = -dy / Math.sqrt(dx * dx + dy * dy);

                float ax = dim.width / 2 - w / 2;
                float ay = dim.height / 2 - h / 2;
                double angle = ((dx > 0) ? Math.acos(cos) : -Math.acos(cos));

                float extent = (float)Math.toDegrees(-angle);
                Arc2D.Float p = new Arc2D.Float(ax, ay, w, h,
                                                90f,
                                                extent,
                                                Arc2D.PIE);

                rotateAngle = angle;
                rotateCos = Math.cos(rotateAngle);

                rotateTransform =
                    AffineTransform.getRotateInstance(
                     angle,
                     dim.width / 2,
                     dim.height / 2);

                rotateMarker = p;

                paintOverlays(getGraphics());
           }
        }

        protected void clearRotateMarker() {
              paintOverlays(getGraphics());
              rotateMarker = null;
        }

        protected void paintZoomMarker(int x, int y) {
            Graphics g = JSVGCanvas.this.getGraphics();
            clearZoomMarker();
            if (mouseExited) {
                markerTop = null;
                markerLeft = null;
                markerBottom = null;
                markerRight = null;
            } else {
                markerTop    = new Line2D.Float(sx, sy, x,  sy);
                markerLeft   = new Line2D.Float(sx, sy, sx, y);
                markerBottom = new Line2D.Float(sx, y,  x,  y);
                markerRight  = new Line2D.Float(x,  y,  x,  sy);
/*
 *               Rectangle r;
 *               r = markerStroke.createStrokedShape(markerTop).getBounds();
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerLeft).getBounds();
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerBottom).getBounds();
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerRight).getBounds();
 *               paintImmediately(r.x, r.y, r.width, r.height);
 */
                paintOverlays(g);
                // NOTE: If overlays do more than XOR, we will need to go back to using
                //       paintImmediately.
            }
        }

        protected void clearZoomMarker() {

            Graphics g = JSVGCanvas.this.getGraphics();
            paintOverlays(g);
            markerTop = null;
/*
 *            if (markerTop != null) {
 *               Rectangle r;
 *               r = markerStroke.createStrokedShape(markerTop).getBounds();
 *               markerTop = null;
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerLeft).getBounds();
 *               markerLeft = null;
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerBottom).getBounds();
 *               markerBottom = null;
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *
 *               r = markerStroke.createStrokedShape(markerRight).getBounds();
 *               markerRight = null;
 *               paintImmediately(r.x, r.y, r.width, r.height);
 *           }
 */
        }
    }

    /**
     * This class represents the thumbnail canvas.
     */
    protected class ThumbnailCanvas
        extends    JComponent
        implements DynamicRenderer.RepaintHandler {

        /**
         * The offscreen buffer.
         */
        protected BufferedImage offscreenBuffer;

        /**
         * The currently visible offscreen sub-buffer.
         */
        protected BufferedImage buffer;

        /**
         * The tranform to apply to the graphics object.
         */
        protected AffineTransform transform = new AffineTransform();

        /**
         * The current renderer.
         */
        protected Renderer renderer;

        /**
         * The repaint thread.
         */
        protected Thread repaintThread;

        /**
         * Must the buffer be updated?
         */
        protected boolean bufferNeedsRendering;

        /**
         * An additional transform for the marker.
         */
        protected AffineTransform markerTransform = new AffineTransform();

        /**
         * The background rendering thread instance - should be only one alive.
         */
        private Thread backgroundRenderThread = null;

         /**
         * Used to draw marker
         */
        protected BasicStroke markerStroke
            = new BasicStroke(1, BasicStroke.CAP_SQUARE,
                              BasicStroke.JOIN_MITER,
                              10,
                              new float[]{2, 2}, 0);
        /**
         * Creates a new ThumbnailCanvas object.
         */
        public ThumbnailCanvas() {
            addComponentListener(new ThumbnailCanvasListener());
            MouseListener ml = new MouseListener();
            addMouseListener(ml);
            addMouseMotionListener(ml);
        }

        /**
         * Recomputes the offscreen buffer and repaint.
         */
        public void fullRepaint() {
            synchronized (repaintState) {
                repaintState.setValue(REPAINT_THUMBNAIL_PENDING);
            }
            bufferNeedsRendering = true;
            computeTransform();
            repaint();
        }


      public void paintComponent(Graphics g) {

            if (!EventQueue.isDispatchThread()) {
                System.err.println(
            "ERROR: Thumbnail paintComponent() called outside AWT thread!");
            }

            // TODO: simplify and clean up this code

            super.paintComponent(g);

            Dimension size = getSize();
            int w = size.width;
            int h = size.height;

            if (w < 1 || h < 1) {
                return;
            }

            Renderer renderer = null;

            if (repaintThread != null) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.drawImage(buffer, null, 0, 0);
                return;
            }

            updateBuffer(w, h);
            if (bufferNeedsRendering) {
                renderer = rendererFactory.createRenderer(buffer);
                ((DynamicRenderer)renderer).setRepaintHandler(this);
                renderer.setTransform(transform);
            }
            if (renderer != null && gvtRoot != null &&
                renderer.getTree() != gvtRoot) {
                renderer.setTree(gvtRoot);
                bufferNeedsRendering = true;
            }
            if (bufferNeedsRendering) {
                clearBuffer(w, h);
                renderer.setTransform(transform);
                repaintAOI(renderer, size, buffer);
                bufferNeedsRendering = false; // repaint is already queued
                return;
            } else { // buffer is current, just transform and draw it

                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(buffer, null, 0, 0);

                paintOverlays(g);
            }
        }

        /**
         * Paints this component.
         */
        protected void paintComponentOld(Graphics g) {
            super.paintComponent(g);

            Dimension size = getSize();
            int w = size.width;
            int h = size.height;

            if (w < 1 || h < 1) {
                return;
            }

            if (repaintThread != null) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.drawImage(buffer, null, 0, 0);
                return;
            }

            updateBuffer(w, h);
            if (bufferNeedsRendering) {
                renderer = rendererFactory.createRenderer(buffer);
                ((DynamicRenderer)renderer).setRepaintHandler(this);
                renderer.setTransform(transform);
            }
            if (renderer != null && gvtRoot != null &&
                renderer.getTree() != gvtRoot) {
                renderer.setTree(gvtRoot);
                bufferNeedsRendering = true;
            }
            if (bufferNeedsRendering) {
                clearBuffer(w, h);
                renderer.setTransform(transform);
                repaintThumbnail();
                bufferNeedsRendering = false;
                return;
            }
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(offscreenBuffer, null, 0, 0);

        }

        public void paintOverlays(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            if (gvtRoot != null) {
                // Paint the marker
                Dimension csize = JSVGCanvas.this.getSize();
                Rectangle rect = new Rectangle(0, 0, csize.width, csize.height);

                GeneralPath p = new GeneralPath();
                p.moveTo(0, 0);
                p.lineTo(csize.width, 0);
                p.lineTo(csize.width, csize.height);
                p.lineTo(0, csize.height);
                p.closePath();
                p.moveTo(0, csize.height/24);
                p.lineTo(csize.width, csize.height/24);

                AffineTransform at = (AffineTransform)transform.clone();
                at.preConcatenate(markerTransform);

                Shape s = JSVGCanvas.this.getAreaOfInterest(p);
                s = at.createTransformedShape(s);

                g2d.setColor(Color.black);
                g2d.setXORMode(Color.white);
                g2d.draw(s);
                g2d.setXORMode(Color.white);
            }
        }

        /**
         * To repaint the buffer.
         */
        protected void repaintAOI(Renderer renderer, Dimension size,
                                                 BufferedImage buffer) {

            Shape aoi = getAreaOfInterest(
                        new Rectangle(0, 0, size.width, size.height));
            Thread t = new Thread(
                   new RenderBufferAOIRunnable(
                         renderer, aoi, buffer, size, ThumbnailCanvas.this));
            t.setPriority(Thread.MIN_PRIORITY);
            if ((backgroundRenderThread != null) &&
                       (backgroundRenderThread.isAlive())) {
                backgroundRenderThread.interrupt();
            }
            backgroundRenderThread = t;
            t.start();
        }

        /**
         * Clears the offscreen buffer.
         */
        protected void clearBuffer(int w, int h) {
            Graphics2D g = buffer.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);
            g.setClip(0, 0, w, h);
            g.setPaint(Color.white);
            g.fillRect(0, 0, w, h);
        }

        /**
         * Clears the offscreen buffer.
         */
        protected void clearBuffer(Shape aoi) {
            Graphics2D g = buffer.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);
            g.setClip(aoi.getBounds());
            g.setPaint(Color.white);
            g.fill(aoi);
        }

        /**
         * Updates the offscreen buffer.
         * @param w&nbsp;h The size of the component.
         */
        protected void updateBuffer(int w, int h) {
            // Create a new buffer if needed.
            if (offscreenBuffer == null ||
                offscreenBuffer.getWidth() < w ||
                offscreenBuffer.getHeight() < h) {
                if (offscreenBuffer != null) {
                    offscreenBuffer.flush();
                }
                offscreenBuffer =
                    new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                buffer = offscreenBuffer;
                bufferNeedsRendering = true;
            } else if (buffer.getWidth() != w ||
                       buffer.getHeight() != h) {
                buffer = offscreenBuffer.getSubimage(0, 0, w, h);
                bufferNeedsRendering = true;
            }
        }

        /**
         * Computes the value of the transform attribute.
         */
        protected void computeTransform() {
            if (document == null) {
                transform = new AffineTransform();
                return;
            }
            SVGSVGElement elt = document.getRootElement();
            Dimension size = getSize();
            int w = size.width;
            int h = size.height;

            transform = SVGUtilities.getPreserveAspectRatioTransform
                (elt, w, h, parserFactory);
            if (transform.isIdentity()) {
                float dw = elt.getWidth().getBaseVal().getValue();
                float dh = elt.getHeight().getBaseVal().getValue();
                float d = Math.max(dw, dh);
                transform = AffineTransform.getScaleInstance(w / d, h / d);
            }
        }

        /**
         * Notifies that the specified area of interest need to be repainted.
         * @param aoi the area of interest to repaint
         */
        public void notifyRepaintedRegion(Shape oldAoi, Shape newAoi,
                                          Renderer renderer) {

            // TODO: thread this (it's used by DynamicRenderer)

            clearBuffer(oldAoi);
            clearBuffer(newAoi);
            try {
                renderer.repaint(oldAoi);
                renderer.repaint(newAoi);
                Rectangle2D r =
                    transform.createTransformedShape(oldAoi).getBounds();
                repaint((int)r.getX()-1, (int)r.getY()-1,
                        (int)r.getWidth()+2, (int)r.getHeight()+2);
                r = transform.createTransformedShape(newAoi).getBounds();
                repaint((int)r.getX()-1, (int)r.getY()-1,
                        (int)r.getWidth()+2, (int)r.getHeight()+2);
            } catch (InterruptedException ie) {
            }
        }

        /**
         * @return the area of interest displayed in the viewer, in usr space.
         */
        protected Shape getAreaOfInterest(Shape devAOI){
            AffineTransform dev2usr = null;
            try {
                dev2usr = transform.createInverse();
            } catch(NoninvertibleTransformException e){
                // This should not happen. See setTransform
                throw new Error();
            }
            return dev2usr.createTransformedShape(devAOI);
        }

        /**
         * Repaint the thumbnail view.
         */
        public void repaintThumbnail() {
            Dimension size = getSize();
            try {
                renderer.repaint(getAreaOfInterest
                               (new Rectangle(0, 0, size.width, size.height)));
                repaint();
            } catch (InterruptedException ie) {
            }
        }

        /**
         * To correctly resize the view.
         */
        protected class ThumbnailCanvasListener extends ComponentAdapter {
            public ThumbnailCanvasListener() {}
            public void componentResized(ComponentEvent e) {
                if (gvtRoot == null) {
                    return;
                }
                computeTransform();
            }
        }

        /**
         * To handle the mouse events.
         */
        protected class MouseListener
            extends    MouseAdapter
            implements MouseMotionListener {
            protected int sx, sy;
            protected boolean in;

            public MouseListener() {}
            public void mousePressed(MouseEvent e) {
                Dimension csize = JSVGCanvas.this.getSize();
                Rectangle rect = new Rectangle(0, 0, csize.width, csize.height);
                Shape s = JSVGCanvas.this.getAreaOfInterest(rect);
                s = transform.createTransformedShape(s);

                sx = e.getX();
                sy = e.getY();
                in = s.contains(sx, sy);
            }
            public void mouseDragged(MouseEvent e) {
                if (in) {
                    markerTransform =
                        AffineTransform.getTranslateInstance(e.getX() - sx,
                                                             e.getY() - sy);
                    Dimension d = getSize();
                    paintImmediately(0, 0, d.width, d.height);
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (in) {
                    in = false;

                    int dx = e.getX() - sx;
                    int dy = e.getY() - sy;

                    markerTransform =
                        AffineTransform.getTranslateInstance(dx, dy);
                    Dimension d = getSize();
                    paintImmediately(0, 0, d.width, d.height);

                    Point2D pt0 = new Point2D.Float(0, 0);
                    Point2D pt = new Point2D.Float(dx, dy);
                    try {
                        transform.inverseTransform(pt0, pt0);
                        transform.inverseTransform(pt, pt);
                    } catch (NoninvertibleTransformException ex) {
                    }
                    markerTransform =
                        AffineTransform.getTranslateInstance
                            (pt0.getX() - pt.getX(),
                             pt0.getY() - pt.getY());
                    JSVGCanvas.this.transform.concatenate(markerTransform);
                    JSVGCanvas.this.updateBaseTransform();
                    JSVGCanvas.this.bufferNeedsRendering = true;
                    JSVGCanvas.this.repaint();

                    markerTransform = new AffineTransform();
                }
            }
            public void mouseMoved(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
                if (in) {
                    in = false;
                    markerTransform = new AffineTransform();

                    Dimension d = getSize();
                    paintImmediately(0, 0, d.width, d.height);
                }
            }
        }
    }

}































