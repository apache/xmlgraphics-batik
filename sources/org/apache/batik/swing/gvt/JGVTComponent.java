/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import java.awt.image.BufferedImage;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.AWTEventDispatcher;

import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.ImageRendererFactory;

import org.apache.batik.gvt.text.Mark;

/**
 * This class represents a component which can display a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JGVTComponent extends JComponent {

    /**
     * The listener.
     */
    protected Listener listener;

    /**
     * The GVT tree renderer.
     */
    protected GVTTreeRenderer gvtTreeRenderer;

    /**
     * The GVT tree root.
     */
    protected GraphicsNode gvtRoot;

    /**
     * The renderer factory.
     */
    protected ImageRendererFactory rendererFactory =
        new ConcreteImageRendererFactory();

    /**
     * The current renderer.
     */
    protected ImageRenderer renderer;

    /**
     * The GVT tree renderer listeners.
     */
    protected List gvtTreeRendererListeners =
        Collections.synchronizedList(new LinkedList());

    /**
     * Whether a render was requested.
     */
    protected boolean needRender;

    /**
     * Whether to allow progressive paint.
     */
    protected boolean progressivePaint;

    /**
     * The progressive paint thread.
     */
    protected Thread progressivePaintThread;

    /**
     * The image to paint.
     */
    protected BufferedImage image;

    /**
     * The initial rendering transform.
     */
    protected AffineTransform initialTransform;

    /**
     * The transform used for rendering.
     */
    protected AffineTransform renderingTransform;

    /**
     * The transform used for painting.
     */
    protected AffineTransform paintingTransform;

    /**
     * The interactor list.
     */
    protected List interactors = new LinkedList();

    /**
     * The current interactor.
     */
    protected Interactor interactor;

    /**
     * The overlays.
     */
    protected List overlays = new LinkedList();

    /**
     * The event dispatcher.
     */
    protected AWTEventDispatcher eventDispatcher;

    /**
     * The text selection manager.
     */
    protected TextSelectionManager textSelectionManager;

    /**
     * Whether the double buffering is enabled.
     */
    protected boolean doubleBufferedRendering;

    /**
     * Whether the GVT tree should be reactive to mouse and key events.
     */
    protected boolean eventsEnabled;

    /**
     * Whether the text should be selectable if eventEnabled is false,
     * this flag is ignored.
     */
    protected boolean selectableText;

    /**
     * Whether to suspend interactions.
     */
    protected boolean suspendInteractions;

    /**
     * Whether to inconditionally disable interactions.
     */
    protected boolean disableInteractions;

    /**
     * Creates a new JGVTComponent.
     */
    public JGVTComponent() {
        this(false, false);
    }

    /**
     * Creates a new JGVTComponent.
     * @param eventEnabled Whether the GVT tree should be reactive
     *        to mouse and key events.
     * @param selectableText Whether the text should be selectable.
     *        if eventEnabled is false, this flag is ignored.
     */
    public JGVTComponent(boolean eventsEnabled, boolean selectableText) {
        setBackground(Color.white);

        this.eventsEnabled = eventsEnabled;
        this.selectableText = selectableText;

        listener = createListener();

        addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);

        addGVTTreeRendererListener(listener);

        addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    updateRenderingTransform();
                    scheduleGVTRendering();
                }
            });

    }

    /**
     * Returns the interactor list.
     */
    public List getInteractors() {
        return interactors;
    }

    /**
     * Returns the overlay list.
     */
    public List getOverlays() {
        return overlays;
    }

    /**
     * Returns the off-screen image, if any.
     */
    public BufferedImage getOffScreen() {
        return image;
    }

    /**
     * Resets the rendering transform to its initial value.
     */
    public void resetRenderingTransform() {
        setRenderingTransform(initialTransform);
    }

    /**
     * Stops the processing of the current tree.
     */
    public void stopProcessing() {
        if (gvtTreeRenderer != null) {
            needRender = false;
            gvtTreeRenderer.interrupt();
            interruptProgressivePaintThread();
        }
    }

    /**
     * Returns the root of the GVT tree displayed by this component, if any.
     */
    public GraphicsNode getGraphicsNode() {
        return gvtRoot;
    }

    /**
     * Sets the GVT tree to display.
     */
    public void setGraphicsNode(GraphicsNode gn) {
        setGraphicsNode(gn, true);
    }

    /**
     * Sets the GVT tree to display.
     */
    protected void setGraphicsNode(GraphicsNode gn, boolean createDispatcher) {
        gvtRoot = gn;
        if (gn != null && createDispatcher) {
            initializeEventHandling();
        }
        if (eventDispatcher != null) {
            eventDispatcher.setRootNode(gn);
        }
        computeRenderingTransform();
    }

    /**
     * Initializes the event handling classes.
     */
    protected void initializeEventHandling() {
        if (eventsEnabled) {
            eventDispatcher = new AWTEventDispatcher();
            if (selectableText) {
                textSelectionManager =
                    new TextSelectionManager(this, eventDispatcher);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // Selection methods
    ////////////////////////////////////////////////////////////////////////

    /**
     * Sets the color of the selection overlay to the specified color.
     *
     * @param color the new color of the selection overlay
     */
    public void setSelectionOverlayColor(Color color) {
        if (textSelectionManager != null) {
            textSelectionManager.setSelectionOverlayColor(color);
        }
    }

    /**
     * Returns the color of the selection overlay.
     */
    public Color getSelectionOverlayColor() {
        if (textSelectionManager != null) {
            return textSelectionManager.getSelectionOverlayColor();
        } else {
            return null;
        }
    }

    /**
     * Sets the color of the outline of the selection overlay to the specified
     * color.
     *
     * @param color the new color of the outline of the selection overlay
     */
    public void setSelectionOverlayStrokeColor(Color color) {
        if (textSelectionManager != null) {
            textSelectionManager.setSelectionOverlayStrokeColor(color);
        }
    }

    /**
     * Returns the color of the outline of the selection overlay.
     */
    public Color getSelectionOverlayStrokeColor() {
        if (textSelectionManager != null) {
            return textSelectionManager.getSelectionOverlayStrokeColor();
        } else {
            return null;
        }
    }

    /**
     * Sets whether or not the selection overlay will be painted in XOR mode,
     * depending on the specified parameter.
     *
     * @param state true implies the selection overlay will be in XOR mode
     */
    public void setSelectionOverlayXORMode(boolean state) {
        if (textSelectionManager != null) {
            textSelectionManager.setSelectionOverlayXORMode(state);
        }
    }

    /**
     * Returns true if the selection overlay is painted in XOR mode, false
     * otherwise.
     */
    public boolean isSelectionOverlayXORMode() {
        if (textSelectionManager != null) {
            return textSelectionManager.isSelectionOverlayXORMode();
        } else {
            return false;
        }
    }

    /**
     * Sets the selection to the specified start and end mark.
     *
     * @param start the mark used to define where the selection starts
     * @param end the mark used to define where the selection ends
     */
    public void select(Mark start, Mark end) {
        if (textSelectionManager != null) {
            textSelectionManager.setSelection(start, end);
        }
    }

    /**
     * Deselects all.
     */
    public void deselectAll() {
        if (textSelectionManager != null) {
            textSelectionManager.clearSelection();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // Painting methods
    ////////////////////////////////////////////////////////////////////////

    /**
     * Whether to enable the progressive paint.
     */
    public void setProgressivePaint(boolean b) {
        if (progressivePaint != b) {
            progressivePaint = b;
            interruptProgressivePaintThread();
        }
    }

    /**
     * Tells whether the progressive paint is enabled.
     */
    public boolean getProgressivePaint() {
        return progressivePaint;
    }

    /**
     * Repaints immediately the component.
     */
    public void immediateRepaint() {
        if (EventQueue.isDispatchThread()) {
            Dimension dim = getSize();
            paintImmediately(0, 0, dim.width, dim.height);
        } else {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        Dimension dim = getSize();
                        paintImmediately(0, 0, dim.width, dim.height);
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    /**
     * Paints this component.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        Dimension d = getSize();
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setPaint(getBackground());
        g2d.fillRect(0, 0, d.width, d.height);

        if (image != null) {
            if (paintingTransform != null) {
                g2d.transform(paintingTransform);
            }
            g2d.drawRenderedImage(image, null);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_OFF);
            Iterator it = overlays.iterator();
            while (it.hasNext()) {
                ((Overlay)it.next()).paint(g);
            }
        }
    }

    /**
     * Sets the painting transform. A null transform is the same as
     * an identity transform.
     * The next repaint will use the given transform.
     */
    public void setPaintingTransform(AffineTransform at) {
        paintingTransform = at;
        immediateRepaint();
    }

    /**
     * Returns the current painting transform.
     */
    public AffineTransform getPaintingTransform() {
        return paintingTransform;
    }

    /**
     * Sets the rendering transform.
     * Calling this method causes a rendering to be performed.
     */
    public void setRenderingTransform(AffineTransform at) {
        renderingTransform = at;
        suspendInteractions = true;
        if (eventDispatcher != null) {
            try {
                eventDispatcher.setBaseTransform
                    (renderingTransform.createInverse());
            } catch (NoninvertibleTransformException e) {
                handleException(e);
            }
        }
        scheduleGVTRendering();
    }

    /**
     * Returns the initial transform.
     */
    public AffineTransform getInitialTransform() {
        return initialTransform;
    }

    /**
     * Returns the current rendering transform.
     */
    public AffineTransform getRenderingTransform() {
        return renderingTransform;
    }

    /**
     * Sets whether this component should use double buffering to render
     * SVG documents. The change will be effective during the next
     * rendering.
     */
    public void setDoubleBufferedRendering(boolean b) {
        doubleBufferedRendering = b;
    }

    /**
     * Tells whether this component use double buffering to render
     * SVG documents.
     */
    public boolean getDoubleBufferedRendering() {
        return doubleBufferedRendering;
    }

    /**
     * Adds a GVTTreeRendererListener to this component.
     */
    public void addGVTTreeRendererListener(GVTTreeRendererListener l) {
        gvtTreeRendererListeners.add(l);
    }

    /**
     * Removes a GVTTreeRendererListener from this component.
     */
    public void removeGVTTreeRendererListener(GVTTreeRendererListener l) {
        gvtTreeRendererListeners.remove(l);
    }

    /**
     * Flush any cached image data (preliminary interface,
     * may be removed or modified in the future).
     */
    public void flush() {
        renderer.flush();
    }

    /**
     * Flush a rectangle of cached image data (preliminary interface,
     * may be removed or modified in the future).
     */
    public void flush(Rectangle r) {
        renderer.flush(r);
    }

    /**
     * Creates a new renderer.
     */
    protected ImageRenderer createImageRenderer() {
        return rendererFactory.createStaticImageRenderer();
    }

    /**
     * Renders the GVT tree.
     */
    protected void renderGVTTree() {
        Dimension d = getSize();
        if (gvtRoot == null || d.width <= 0 || d.height <= 0) {
            return;
        }

        // Renderer setup.
        if (renderer == null || renderer.getTree() != gvtRoot) {
            renderer = createImageRenderer();
            renderer.setTree(gvtRoot);
        }

        // Area of interest computation.
        AffineTransform inv;
        try {
            inv = renderingTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new InternalError(e.getMessage());
        }
        Shape s = inv.createTransformedShape
            (new Rectangle(0, 0, d.width, d.height));

        // Rendering thread setup.
        gvtTreeRenderer = new GVTTreeRenderer(renderer, renderingTransform,
                                              doubleBufferedRendering,
                                              s, d.width, d.height);
        gvtTreeRenderer.setPriority(Thread.MIN_PRIORITY);

        Iterator it = gvtTreeRendererListeners.iterator();
        while (it.hasNext()) {
            gvtTreeRenderer.addGVTTreeRendererListener
                ((GVTTreeRendererListener)it.next());
        }

        // Disable the dispatch during the rendering
        // to avoid concurrent access to the GVT tree.
        if (eventDispatcher != null) {
            eventDispatcher.setRootNode(null);
        }
        gvtTreeRenderer.start();
    }

    /**
     * Computes the initial value of the transform used for rendering.
     */
    protected void computeRenderingTransform() {
        initialTransform = new AffineTransform();
        setRenderingTransform(initialTransform);
    }

    /**
     * Updates the value of the transform used for rendering.
     */
    protected void updateRenderingTransform() {
        // Do nothing.
    }

    /**
     * Handles an exception.
     */
    protected void handleException(Exception e) {
        // Do nothing.
    }

    /**
     * Releases the references to the rendering resources,
     */
    protected void releaseRenderingReferences() {
        eventDispatcher = null;
        if (textSelectionManager != null) {
            overlays.remove(textSelectionManager.getSelectionOverlay());
            textSelectionManager = null;
        }
        renderer = null;
        gvtRoot = null;
    }

    /**
     * Schedules a new GVT rendering.
     */
    protected void scheduleGVTRendering() {
        if (gvtTreeRenderer != null) {
            needRender = true;
            gvtTreeRenderer.interrupt();
        } else {
            renderGVTTree();
        }
    }

    private void interruptProgressivePaintThread() {
        if (progressivePaintThread != null) {
            progressivePaintThread.interrupt();
            progressivePaintThread = null;
        }
    }

    /**
     * Creates an instance of Listener.
     */
    protected Listener createListener() {
        return new Listener();
    }

    /**
     * To hide the listener methods.
     */
    protected class Listener
        implements GVTTreeRendererListener,
                   KeyListener,
                   MouseListener,
                   MouseMotionListener {

        /**
         * Creates a new Listener.
         */
        protected Listener() {
        }

        // GVTTreeRendererListener ///////////////////////////////////////////

        /**
         * Called when a rendering is in its preparing phase.
         */
        public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
            suspendInteractions = true;
            if (!progressivePaint && !doubleBufferedRendering) {
                image = null;
                immediateRepaint();
            }
        }

        /**
         * Called when a rendering started.
         */
        public void gvtRenderingStarted(GVTTreeRendererEvent e) {
            paintingTransform = null;
            if (progressivePaint && !doubleBufferedRendering) {
                image = e.getImage();
                progressivePaintThread = new Thread() {
                    public void run() {
                        final Thread thisThread = this;
                        try {
                            while (!isInterrupted()) {
                                EventQueue.invokeAndWait(new Runnable() {
                                    public void run() {
                                        if (progressivePaintThread ==
                                            thisThread) {
                                            Dimension dim = getSize();
                                            paintImmediately(0, 0,
                                                             dim.width,
                                                             dim.height);
                                        }
                                    }
                                });
                                sleep(200);
                            }
                        } catch (Exception ex) {
                        }
                    }
                };
                progressivePaintThread.setPriority(Thread.MIN_PRIORITY + 1);
                progressivePaintThread.start();
            }
            if (!doubleBufferedRendering) {
                suspendInteractions = false;
            }
        }

        /**
         * Called when a rendering was completed.
         */
        public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            interruptProgressivePaintThread();

            if (doubleBufferedRendering) {
                suspendInteractions = false;
            }

            gvtTreeRenderer = null;
            if (needRender) {
                renderGVTTree();
                needRender = false;
            } else {
                image = e.getImage();
                immediateRepaint();
            }
            if (eventDispatcher != null) {
                eventDispatcher.setRootNode(gvtRoot);
            }
        }

        /**
         * Called when a rendering was cancelled.
         */
        public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
            renderingStopped();
        }

        /**
         * Called when a rendering failed.
         */
        public void gvtRenderingFailed(GVTTreeRendererEvent e) {
            renderingStopped();
        }

        /**
         * The actual implementation of gvtRenderingCancelled() and
         * gvtRenderingFailed().
         */
        private void renderingStopped() {
            interruptProgressivePaintThread();

            if (doubleBufferedRendering) {
                suspendInteractions = false;
            }

            gvtTreeRenderer = null;
            if (needRender) {
                renderGVTTree();
                needRender = false;
            } else {
                immediateRepaint();
            }
        }

        // KeyListener //////////////////////////////////////////////////////

        /**
         * Invoked when a key has been typed.
         * This event occurs when a key press is followed by a key release.
         */
        public void keyTyped(KeyEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.keyTyped(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchKeyTyped(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchKeyTyped(KeyEvent e) {
            eventDispatcher.keyTyped(e);
        }

        /**
         * Invoked when a key has been pressed.
         */
        public void keyPressed(KeyEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.keyPressed(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchKeyPressed(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchKeyPressed(KeyEvent e) {
            eventDispatcher.keyPressed(e);
        }

        /**
         * Invoked when a key has been released.
         */
        public void keyReleased(KeyEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.keyReleased(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchKeyReleased(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchKeyReleased(KeyEvent e) {
            eventDispatcher.keyReleased(e);
        }

        // MouseListener ////////////////////////////////////////////////////

        /**
         * Invoked when the mouse has been clicked on a component.
         */
        public void mouseClicked(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseClicked(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseClicked(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseClicked(MouseEvent e) {
            eventDispatcher.mouseClicked(e);
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mousePressed(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMousePressed(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMousePressed(MouseEvent e) {
            eventDispatcher.mousePressed(e);
        }

        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseReleased(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseReleased(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseReleased(MouseEvent e) {
            eventDispatcher.mouseReleased(e);
        }

        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(MouseEvent e) {
            requestFocus();
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseEntered(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseEntered(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseEntered(MouseEvent e) {
            eventDispatcher.mouseEntered(e);
        }

        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseExited(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseExited(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseExited(MouseEvent e) {
            eventDispatcher.mouseExited(e);
        }

        // MouseMotionListener //////////////////////////////////////////////

        /**
         * Invoked when a mouse button is pressed on a component and then
         * dragged.  Mouse drag events will continue to be delivered to
         * the component where the first originated until the mouse button is
         * released (regardless of whether the mouse position is within the
         * bounds of the component).
         */
        public void mouseDragged(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseDragged(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseDragged(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseDragged(MouseEvent e) {
            eventDispatcher.mouseDragged(e);
        }

        /**
         * Invoked when the mouse button has been moved on a component
         * (with no buttons no down).
         */
        public void mouseMoved(MouseEvent e) {
            selectInteractor(e);
            if (interactor != null) {
                interactor.mouseMoved(e);
                deselectInteractor();
            } else if (eventDispatcher != null) {
                dispatchMouseMoved(e);
            }
        }

        /**
         * Dispatches the event to the GVT tree.
         */
        protected void dispatchMouseMoved(MouseEvent e) {
            eventDispatcher.mouseMoved(e);
        }

        /**
         * Selects an interactor, given an input event.
         */
        protected void selectInteractor(InputEvent ie) {
            if (!disableInteractions &&
                !suspendInteractions &&
                interactor == null &&
                gvtRoot != null) {
                Iterator it = interactors.iterator();
                while (it.hasNext()) {
                    Interactor i = (Interactor)it.next();
                    if (i.startInteraction(ie)) {
                        interactor = i;
                        break;
                    }
                }
            }
        }

        /**
         * Deselects an interactor, if the interaction has finished.
         */
        protected void deselectInteractor() {
            if (interactor.endInteraction()) {
                interactor = null;
            }
        }
    }
}
