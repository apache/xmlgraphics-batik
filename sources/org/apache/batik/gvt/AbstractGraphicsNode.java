/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.gvt.event.CompositeGraphicsNodeEvent;
import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeEventFilter;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.filter.Mask;

/**
 * A partial implementation of the <tt>GraphicsNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public abstract class AbstractGraphicsNode implements GraphicsNode {

    /**
     * Used to draw renderable images
     */
    static final AffineTransform IDENTITY = new AffineTransform();

    /**
     * The listeners list.
     */
    protected EventListenerList listeners;

    /**
     * The filter used to filter graphics node events.
     */
    protected GraphicsNodeEventFilter eventFilter;

    /**
     * The hit detector used to filter mouse events.
     */
    protected GraphicsNodeHitDetector hitDetector;

    /**
     * The cursor attached to this graphics node.
     */
    protected Cursor cursor;

    /**
     * The transform of this graphics node.
     */
    protected AffineTransform transform;

    /**
     * The compositing operation to be used when a graphics node is
     * painted on top of another one.
     */
    protected Composite composite;

    /**
     * This flag bit indicates whether or not this graphics node is visible.
     */
    protected boolean isVisible = true;

    /**
     * The clipping filter for this graphics node.
     */
    protected Clip clip;

    /**
     * The rendering hints that control the quality to use when rendering
     * this graphics node.
     */
    protected RenderingHints hints;

    /**
     * The mask of this graphics node.
     */
    protected Mask mask;

    /**
     * The parent of this graphics node.
     */
    protected CompositeGraphicsNode parent;

    /**
     * The root of the GVT tree.
     */
    protected RootGraphicsNode root;

    /**
     * The filter of this graphics node.
     */
    protected Filter filter;

    /**
     * Internal Cache: node bounds
     */
    private Rectangle2D bounds;

    /**
     * Constructs a new graphics node.
     */
    protected AbstractGraphicsNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the cursor of this node.
     * @param newCursor the new cursor of this node
     */
    public void setCursor(Cursor newCursor) {
        this.cursor = newCursor;
    }

    /**
     * Returns the cursor of this node.
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Sets the transform of this node.
     * @param newTransform the new transform of this node
     */
    public void setTransform(AffineTransform newTransform) {
        invalidateGeometryCache();
        this.transform = newTransform;
    }

    /**
     * Returns the transform of this node.
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * Returns the concatenated transform of this node. i.e., this
     * node's transform preconcatenated with it's parent's transforms.
     */
    public AffineTransform getGlobalTransform(){
        AffineTransform ctm = new AffineTransform();
        GraphicsNode node = this;
        while (node != null) {
            if(node.getTransform() != null){
                ctm.preConcatenate(node.getTransform());
            }
            node = node.getParent();
        }
        return ctm;
    }

    /**
     * Sets the composite of this node.
     * @param composite the composite of this node
     */
    public void setComposite(Composite newComposite) {
        invalidateGeometryCache();
        this.composite = newComposite;
    }

    /**
     * Returns the composite of this node.
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Sets if this node is visible or not depending on the specified value.
     * @param isVisible If true this node is visible
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Determines whether or not this node is visible when its parent
     * is visible. Nodes are initially visible.
     * @return true if this node is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets the clipping filter for this node.
     * @param newClipper the new clipping filter of this node
     */
    public void setClip(Clip newClipper) {
        invalidateGeometryCache();
        this.clip = newClipper;
    }

    /**
     * Returns the clipping filter of this node or null if any.
     */
    public Clip getClip() {
        return clip;
    }

    /**
     * Maps the specified key to the specified value in the rendering
     * hints of this node.
     * @param key the key of the hint to be set
     * @param value the value indicating preferences for the specified
     * hint category.
     */
    public void setRenderingHint(RenderingHints.Key key, Object value) {
        if (this.hints == null) {
            this.hints = new RenderingHints(key, value);
        } else {
            hints.put(key, value);
        }
    }

    /**
     * Copies all of the mappings from the specified Map to the
     * rendering hints of this node.
     * @param hints the rendering hints to be set
     */
    public void setRenderingHints(Map hints) {
        if (this.hints == null) {
            this.hints = new RenderingHints(hints);
        } else {
            this.hints.putAll(hints);
        }
    }

    /**
     * Sets the rendering hints of this node.
     * @param newHints the new rendering hints of this node
     */
    public void setRenderingHints(RenderingHints newHints) {
        this.hints = newHints;
    }

    /**
     * Returns the rendering hints of this node or null if any.
     */
    public RenderingHints getRenderingHints() {
        return hints;
    }

    /**
     * Sets the mask of this node.
     * @param newMask the new mask of this node
     */
    public void setMask(Mask newMask) {
        invalidateGeometryCache();
        this.mask = newMask;
    }

    /**
     * Returns the mask of this node or null if any.
     */
    public Mask getMask() {
        return mask;
    }

    /**
     * Sets the filter of this node.
     * @param newFilter the new filter of this node
     */
    public void setFilter(Filter newFilter) {
        invalidateGeometryCache();
        this.filter = newFilter;
    }

    /**
     * Returns the filter of this node or null if any.
     */
    public Filter getFilter() {
        return filter;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     * @exception InterruptedException thrown if the current thread
     * was interrupted during paint
     */
    public void paint(Graphics2D g2d, GraphicsNodeRenderContext rc)
            throws InterruptedException {

        // first, make sure we haven't been interrupted
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        //
        // Set up graphic context. It is important to setup the
        // transform first, because the clip is defined in this
        // node's user space.
        //
        Shape defaultClip = g2d.getClip();
        Composite defaultComposite = g2d.getComposite();
        AffineTransform defaultTransform = g2d.getTransform();
        RenderingHints defaultHints = g2d.getRenderingHints();

        if (hints != null) {
            g2d.addRenderingHints(hints);
        }
        if (transform != null) {
            g2d.transform(transform);
        }
        if (composite != null) {
            g2d.setComposite(composite);
        }
        if (clip != null){
            g2d.clip(clip.getClipPath());
        }

        Shape curClip = g2d.getClip();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST,
                             curClip);
        rc.setTransform(g2d.getTransform());
        rc.setRenderingHints(g2d.getRenderingHints());
        rc.setAreaOfInterest(curClip);

        //
        // Check if any painting is needed at all. Get the clip (in user space)
        // and see if it intersects with this node's bounds (in user space).
        //
        boolean paintNeeded = true;
        Rectangle2D bounds = getBounds(rc);
        Shape g2dClip = g2d.getClip();
        if(g2dClip != null){
            Rectangle2D clipBounds = g2dClip.getBounds2D();
            if(!bounds.intersects(clipBounds.getX(),
                                  clipBounds.getY(),
                                  clipBounds.getWidth(),
                                  clipBounds.getHeight())){
                paintNeeded = false;
            }
        }

        //
        // Only paint if needed.
        //
        // paintNeeded = true;
        if (paintNeeded){
            AffineTransform txf = g2d.getTransform();
            boolean antialiasedClip = false;
            if(clip != null){
                antialiasedClip =
                    isAntialiasedClip(rc.getTransform(),
                                      rc.getRenderingHints(),
                                      clip.getClipPath());
            }

            boolean useOffscreen = isOffscreenBufferNeeded();

            useOffscreen |= antialiasedClip;

            if (!useOffscreen) {

              /* Render directly on the canvas
               * Note: this operation is not interruptable,
               * since InterruptedExceptions are caught and ignored
               * by primitivePaint().
               */

                primitivePaint(g2d, rc);
            } else {
                Filter filteredImage = null;

                if(filter == null){
                    filteredImage = rc.getGraphicsNodeRableFactory().
                        createGraphicsNodeRable(this, rc);
                }
                else {
                    // traceFilter(filter, "=====>> ");
                    filteredImage = filter;
                }

                if (mask != null) {
                    if (mask.getSource() != filteredImage){
                        mask.setSource(filteredImage);
                    }
                    filteredImage = mask;
                }

                if (clip != null && antialiasedClip) {
                    if (clip.getSource() != filteredImage){
                        clip.setSource(filteredImage);
                    }
                    filteredImage = clip;
                }

                if(antialiasedClip){
                    // Remove hard edged clip
                    g2d.setClip(null);
                }

                Rectangle2D filterBounds = filteredImage.getBounds2D();
                g2d.clip(filterBounds);

                org.apache.batik.ext.awt.image.GraphicsUtil.drawImage
                    (g2d, filteredImage);
            }
        }

        // Restore default rendering attributes
        g2d.setRenderingHints(defaultHints);
        g2d.setTransform(defaultTransform);
        g2d.setClip(defaultClip);
        g2d.setComposite(defaultComposite);
    }

    /**
     * DEBUG: Trace filter chain
     */
    private void traceFilter(Filter filter, String prefix){
        System.out.println(prefix + filter.getClass().getName());
        System.out.println(prefix + filter.getBounds2D());
        java.util.Vector sources = filter.getSources();
        int nSources = sources != null ? sources.size() : 0;
        prefix += "\t";
        for(int i=0; i<nSources; i++){
            Filter source = (Filter)sources.elementAt(i);
            traceFilter(source, prefix);
        }

        System.out.flush();
    }

    /**
     * Returns true of an offscreen buffer is needed to render this
     * node, false otherwise.
     */
    protected boolean isOffscreenBufferNeeded() {
        return ((filter != null) ||
                (mask != null) ||
                (composite != null &&
                 !AlphaComposite.SrcOver.equals(composite)));
    }

    /**
     * Returns true if there is a clip and it should be antialiased
     */
    protected boolean isAntialiasedClip(AffineTransform usr2dev,
                                        RenderingHints hints,
                                        Shape clip){
        boolean antialiased = false;
        //
        // Antialias clip if:
        // + Antialiasing is on *or* rendering quality is on
        // *and*
        // + clip is not null
        // *and*
        // + clip is not a rectangle in device space.
        //
        // This leaves out the case where the node clip is a
        // rectangle and the current clip (i.e., the intersection
        // of the current Graphics2D's clip and this node's clip)
        // is not a rectangle.
        //
        if((hints.get(RenderingHints.KEY_ANTIALIASING) ==
            RenderingHints.VALUE_ANTIALIAS_ON) ||
           (hints.get(RenderingHints.KEY_RENDERING) ==
            RenderingHints.VALUE_RENDER_QUALITY)){
            if(!(clip instanceof Rectangle2D &&
                 usr2dev.getShearX() == 0 &&
                 usr2dev.getShearY() == 0)){
                antialiased = true;
            }
        }
        // return antialiased;
        return false;
    }

    //
    // Event support methods
    //

    /**
     * Dispatches the specified event to the interested registered listeners.
     * @param evt the event to dispatch
     */
    public void dispatch(GraphicsNodeEvent evt) {
        // <!> FIXME : TODO
    }

    /**
     * Adds the specified graphics node mouse listener to receive
     * graphics node mouse events from this node.
     * @param l the graphics node mouse listener to add
     */
    public void addGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeMouseListener.class, l);
    }

    /**
     * Removes the specified graphics node mouse listener so that it
     * no longer receives graphics node mouse events from this node.
     * @param l the graphics node mouse listener to remove
     */
    public void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeMouseListener.class, l);
        }
    }

    /**
     * Adds the specified graphics node key listener to receive
     * graphics node key events from this node.
     * @param l the graphics node key listener to add
     */
    public void addGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeKeyListener.class, l);
    }

    /**
     * Removes the specified graphics node key listener so that it
     * no longer receives graphics node key events from this node.
     * @param l the graphics node key listener to remove
     */
    public void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeKeyListener.class, l);
        }
    }

    /**
     * Sets the graphics node event filter of this node.
     * @param evtFilter the new graphics node event filter
     */
    public void setGraphicsNodeEventFilter(GraphicsNodeEventFilter evtFilter) {
        this.eventFilter = evtFilter;
    }

    /**
     * Returns the graphics node event filter of this node.
     */
    public GraphicsNodeEventFilter getGraphicsNodeEventFilter() {
        return eventFilter;
    }

    /**
     * Sets the hit detector for this node.
     * @param hitDetector the new hit detector
     */
    public void setGraphicsNodeHitDetector(GraphicsNodeHitDetector hitDetector){
        this.hitDetector = hitDetector;
    }

    /**
     * Returns the hit detector for this node.
     */
    public GraphicsNodeHitDetector getGraphicsNodeHitDetector() {
        return hitDetector;
    }

    /**
     * Returns an array of listeners that were added to this node and
     * of the specified type.
     * @param listenerType the type of the listeners to return
     */
    public EventListener [] getListeners(Class listenerType) {
        Object array =
            Array.newInstance(listenerType,
                              listeners.getListenerCount(listenerType));
        Object[] pairElements = listeners.getListenerList();
        for (int i=0, j=0;i<pairElements.length-1;i+=2) {
            if (pairElements[i].equals(listenerType)) {
                Array.set(array, j, pairElements[i+1]);
                ++j;
            }
        }
        return (EventListener[]) array;
        // XXX: Code below is a jdk 1.3 dependency!  Should be removed.
        //return listeners.getListeners(listenerType);
    }

    /**
     * Dispatches a graphics node event to this node or one of its child.
     * @param evt the evt to dispatch
     */
    public void processMouseEvent(GraphicsNodeMouseEvent evt) {
        if ((listeners != null) && acceptEvent(evt)) {
            GraphicsNodeMouseListener[] listeners =
                (GraphicsNodeMouseListener[])
                getListeners(GraphicsNodeMouseListener.class);

            switch (evt.getID()) {
            case GraphicsNodeMouseEvent.MOUSE_MOVED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseMoved((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_DRAGGED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseDragged((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_ENTERED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseEntered((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_EXITED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseExited((GraphicsNodeMouseEvent)evt.clone());
                }
                    break;
            case GraphicsNodeMouseEvent.MOUSE_CLICKED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseClicked((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_PRESSED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mousePressed((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_RELEASED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseReleased((GraphicsNodeMouseEvent)evt.clone());
                }
                break;
                default:
                    throw new Error("Unknown Mouse Event type: "+evt.getID());
            }
        }
        evt.consume();
    }


    /**
     * Dispatches a graphics node event to this node or one of its child.
     * @param evt the evt to dispatch
     */
   public void processKeyEvent(GraphicsNodeKeyEvent evt) {
        if ((listeners != null) && acceptEvent(evt)) {
            GraphicsNodeKeyListener[] listeners =
                (GraphicsNodeKeyListener[])
                getListeners(GraphicsNodeKeyListener.class);

            switch (evt.getID()) {
            case GraphicsNodeKeyEvent.KEY_PRESSED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].keyPressed((GraphicsNodeKeyEvent) evt.clone());
                }
            case GraphicsNodeKeyEvent.KEY_RELEASED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].keyReleased((GraphicsNodeKeyEvent) evt.clone());
                }
                break;
            case GraphicsNodeKeyEvent.KEY_TYPED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].keyTyped((GraphicsNodeKeyEvent) evt.clone());
                }
                break;
            default:
                throw new Error("Unknown Key Event type: "+evt.getID());
            }
        }
        evt.consume();
    }

    /**
     * Returns true is this node accepts the specified event, false otherwise.
     * @param evt the event to check
     */
    protected boolean acceptEvent(GraphicsNodeEvent evt) {
        if (eventFilter != null) {
            return eventFilter.accept(this, evt);
        }
        return true;
    }


    //
    // Structural methods
    //

    /**
     * Returns the parent of this node or null if any.
     */
    public CompositeGraphicsNode getParent() {
        return parent;
    }

    /**
     * Returns the root of the GVT tree or <code>null</code> if
     * the node is not part of a GVT tree.
     */
    public RootGraphicsNode getRoot() {
        return root;
    }

    /**
     * Sets the root node of this graphics node.
     * @param newRoot the new root node of this node
     */
    protected void setRoot(RootGraphicsNode newRoot) {
        this.root = newRoot;
    }

    /**
     * Sets the parent node of this graphics node.
     * @param newParent the new parent node of this node
     */
    protected void setParent(CompositeGraphicsNode newParent) {
        this. parent = newParent;
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates the cached geometric bounds. This method is called
     * each time an attribute that affects the bounds of this node
     * changed.
     */
    protected void invalidateGeometryCache() {
        if (parent != null) {
            ((AbstractGraphicsNode) parent).invalidateGeometryCache();
        }
        bounds = null;
    }

    /**
     * Compute the rendered bounds of this node based on it's
     * renderBounds. i.e., the area painted by its primitivePaint
     * method. This is used in addition to the mask, clip and filter
     * to compute the area actually rendered by this node.
     */
    public Rectangle2D getBounds(GraphicsNodeRenderContext rc){
        // Get the primitive bounds
        // Rectangle2D bounds = null;
        if(bounds == null){
            // The painted region, before cliping, masking
            // and compositing is either the area painted
            // by the primitive paint or the area painted
            // by the filter.
            if(filter == null){
                bounds = getPrimitiveBounds(rc);
            } else {
                bounds = filter.getBounds2D();
            }
            // Factor in the clipping area, if any
            if(clip != null) {
                bounds.intersect(bounds,
                                 clip.getClipPath().getBounds2D(),
                                 bounds);
            }
            // Factor in the mask, if any
            if(mask != null) {
                bounds.intersect(bounds,
                                 mask.getBounds2D(),
                                 bounds);
            }
        }
        return bounds;
    }

    /**
     * Returns true if the specified coordinates are inside the
     * interior of the bounds of this node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the coordinates are inside, false otherwise
     */
    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        return getBounds(rc).contains(p);
    }

    /**
     * Returns the GraphicsNode containing point p if this node or one of
     * its children is sensitive to mouse events at p.
     *
     * @param p the specified Point2D in the user space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return the GraphicsNode containing p on this branch of the GVT tree.
     */
    public GraphicsNode nodeHitAt(Point2D p, GraphicsNodeRenderContext rc) {
        if (hitDetector != null) {
            if (hitDetector.isHit(this, p)) {
                return this;
            } else {
                return null;
            }
        } else {
            return (contains(p, rc) ? this : null);
        }
    }

    /**
     * Tests if the bounds of this node intersects the interior of a
     * specified Rectangle2D.
     *
     * @param r the specified Rectangle2D in the user node space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the rectangle intersects, false otherwise
     */
    public boolean intersects(Rectangle2D r, GraphicsNodeRenderContext rc) {
        return getBounds(rc).intersects(r);
    }
}
