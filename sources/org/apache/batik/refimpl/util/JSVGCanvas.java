/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.util;

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

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.NoninvertibleTransformException;

import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.refimpl.bridge.DefaultUserAgent;
import org.apache.batik.refimpl.bridge.SVGBridgeContext;

import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;

import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;
import org.apache.batik.refimpl.gvt.renderer.StaticRendererFactory;

import org.apache.batik.refimpl.parser.ParserFactory;

import org.apache.batik.util.SVGUtilities;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.MissingListenerException;

import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class represents a JComponent which is able to represents
 * a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGCanvas
    extends    JComponent
    implements ActionMap {
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
        new Cursor(Cursor.SE_RESIZE_CURSOR);

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
     */
    protected Renderer renderer;

    /**
     * The renderer factory.
     */
    protected RendererFactory rendererFactory;

    /**
     * The GVT builder.
     */
    protected GVTBuilder builder;

    /**
     * The bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * The SVG document to render.
     */
    protected SVGDocument document;

    /**
     * Must the buffer be updated?
     */
    protected boolean repaint;

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * The tranform to apply to the graphics object.
     */
    protected AffineTransform transform = new AffineTransform();

    /**
     * The tranform representing the pan tranlate.
     */
    protected AffineTransform panTransform;

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
     * The repaint thread.
     */
    protected Thread repaintThread;

   /**
     * Used to draw marker
     */
    protected BasicStroke markerStroke 
        = new BasicStroke(1, BasicStroke.CAP_SQUARE, 
                          BasicStroke.JOIN_MITER,
                          10,
                          new float[]{4, 4}, 0);
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
        rendererFactory = new StaticRendererFactory();
 
        builder = new ConcreteGVTBuilder();
        bridgeContext = new SVGBridgeContext();
        bridgeContext.setGVTFactory
            (ConcreteGVTFactory.getGVTFactoryImplementation());
        bridgeContext.setParserFactory(new ParserFactory());
        bridgeContext.setUserAgent(userAgent);
        bridgeContext.setGraphicsNodeRableFactory
            (new ConcreteGraphicsNodeRableFactory());

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
     * Returns the current renderer factory.
     */
    public RendererFactory getRendererFactory() {
        return rendererFactory;
    }

    /**
     * Sets the SVG document to display.
     * @param doc if null, clears the canvas.
     */
    public void setSVGDocument(SVGDocument doc) {
        document = doc;
        if (document == null) {
            gvtRoot = null;
        } else {
            bridgeContext.setViewCSS((ViewCSS)doc.getDocumentElement());
            gvtRoot = builder.build(bridgeContext, document);

            computeTransform();
        }

        repaint = true;
        repaint();
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
    * @return the area of interest displayed in the viewer, in usr space.
    */
    private Shape getAreaOfInterest(Rectangle devAOI){
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
     * Paints this component.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        int w = size.width;
        int h = size.height;

        if (w < 1 || h < 1) {
            return;
        }
        
        updateBuffer(w, h);
        if (repaint) {
            renderer = rendererFactory.createRenderer(buffer);
            renderer.setTransform(transform);
        }
        if (renderer != null && gvtRoot != null &&
            renderer.getTree() != gvtRoot) {
            renderer.setTree(gvtRoot);
            repaint = true;
        }
        if (repaint) {
            clearBuffer(w, h);
            renderer.setTransform(transform);
       
            if (repaintThread != null) {
                repaintThread.stop();
            }
            repaintThread = new RepaintThread();
            repaintThread.start();
        }
        repaint = false;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        if (panTransform != null) {
            g2d.transform(panTransform);
        }
        g2d.drawImage(buffer, null, 0, 0);
        if (markerTop != null) {
            g2d.setColor(Color.black);
            g2d.setStroke(markerStroke);
            g2d.draw(markerTop);
            g2d.draw(markerLeft);
            g2d.draw(markerBottom);
            g2d.draw(markerRight);
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
            globalBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            buffer = globalBuffer;
            repaint = true;
        } else if (buffer.getWidth() != w ||
                   buffer.getHeight() != h) {
            buffer = globalBuffer.getSubimage(0, 0, w, h);
            repaint = true;
        }
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
            (elt, w, h, bridgeContext.getParserFactory());
    }

    /**
     * To repaint the buffer.
     */
    protected class RepaintThread extends Thread {
        /**
         * Creates a new thread.
         */
        public RepaintThread() {
            setPriority(Thread.MIN_PRIORITY);
        }
        
        /**
         * The thread main method.
         */
        public void run() {
            Dimension size = getSize();
            renderer.repaint(getAreaOfInterest
                             (new Rectangle(0, 0, size.width, size.height)));
            repaint();
            repaintThread = null;
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
        }
    }

    /**
     * To reset the zoom.
     */
    public class UnzoomAction extends AbstractAction {
        public UnzoomAction() {}
        public void actionPerformed(ActionEvent e) {
            computeTransform();
            repaint = true;
            repaint();
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
            repaint = true;
            repaint();
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
            repaint = true;
            repaint();
        }
    }
    
    /**
     * To handle the mouse events.
     */
    protected class MouseListener
        extends    MouseAdapter
        implements MouseMotionListener {

        protected Cursor cursor;
        protected int sx;
        protected int sy;

        public MouseListener() {}
        public void mousePressed(MouseEvent e) {
            int mods = e.getModifiers();

            if ((mods & e.BUTTON1_MASK) != 0) {
                sx = e.getX();
                sy = e.getY();
                if ((mods & e.SHIFT_MASK) != 0) {
                    if (cursor == null) {
                        cursor = getCursor();
                    }
                    setCursor(PAN_CURSOR);
                    panTransform = new AffineTransform();
                } else if ((mods & e.CTRL_MASK) != 0) {
                    if (cursor == null) {
                        cursor = getCursor();
                    }
                    setCursor(ZOOM_CURSOR);
                }
            }
        }
        public void mouseDragged(MouseEvent e) {
            int mods = e.getModifiers();

            if ((mods & e.BUTTON1_MASK) != 0) {
                if ((mods & e.SHIFT_MASK) != 0 && panTransform != null) {
                    int x = e.getX();
                    int y = e.getY();
                    panTransform.translate(x - sx, y - sy);
                    sx = x;
                    sy = y;
                    repaint();
                } else if ((mods & e.CTRL_MASK) != 0) {
                    paintZoomMarker(e.getX(), e.getY());
                } else {
                    endOperation(e.getX(), e.getY());
                }
            }
        }
        public void mouseReleased(MouseEvent e) {
            int mods = e.getModifiers();

            if ((mods & e.BUTTON1_MASK) != 0) {
                if (cursor != null) {
                    endOperation(e.getX(), e.getY());
                }
            }
        }
        public void mouseMoved(MouseEvent e) {
        }
        protected void endOperation(int x, int y) {
            setCursor(cursor);
            cursor = null;
            if (panTransform != null) {
                panTransform.translate(x - sx, y - sy);
                transform.preConcatenate(panTransform);
                panTransform = null;
                repaint = true;
                repaint();
            } else if (markerTop != null) {
                markerTop = null;
                markerLeft = null;
                markerBottom = null;
                markerRight = null;
                Dimension size = getSize();

                // Process zoom factor
                float scaleX = size.width / (float)(x - sx);
                float scaleY = size.height / (float)(y - sy);
                float scale = (scaleX < scaleY) ? scaleX : scaleY;

                // Process zoom transform
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                at.translate(-sx, -sy);

                transform.preConcatenate(at);
                repaint = true;
                repaint();
            }
        }
        protected void paintZoomMarker(int x, int y) {
            if (markerTop != null) {
                Rectangle r;
                r = markerStroke.createStrokedShape(markerTop).getBounds();
                markerTop = null;
                paintImmediately(r.x, r.y, r.width, r.height);

                r = markerStroke.createStrokedShape(markerLeft).getBounds();
                markerLeft = null;
                paintImmediately(r.x, r.y, r.width, r.height);

                r = markerStroke.createStrokedShape(markerBottom).getBounds();
                markerBottom = null;
                paintImmediately(r.x, r.y, r.width, r.height);

                r = markerStroke.createStrokedShape(markerRight).getBounds();
                markerRight = null;
                paintImmediately(r.x, r.y, r.width, r.height);
            }

            if (x - sx > 0 && y - sy > 0) {
                markerTop    = new Line2D.Float(sx, sy, x,  sy);
                markerLeft   = new Line2D.Float(sx, sy, sx, y);
                markerBottom = new Line2D.Float(sx, y,  x,  y);
                markerRight  = new Line2D.Float(x,  y,  x,  sy);

                Rectangle r;
                r = markerStroke.createStrokedShape(markerTop).getBounds();
                paintImmediately(r.x, r.y, r.width, r.height);

                r = markerStroke.createStrokedShape(markerLeft).getBounds();
                paintImmediately(r.x, r.y, r.width, r.height);

                r = markerStroke.createStrokedShape(markerBottom).getBounds();
                paintImmediately(r.x, r.y, r.width, r.height);
                
                r = markerStroke.createStrokedShape(markerRight).getBounds();
                paintImmediately(r.x, r.y, r.width, r.height);
            }
        }
    }
}
