/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Cursor;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.EventListener;
import java.lang.reflect.Array;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.EventListenerList;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.GraphicsNodeHitDetector;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeEventFilter;
import org.apache.batik.gvt.event.CompositeGraphicsNodeEvent;
import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.PadMode;

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
     * The Map used to store mememto objects.
     */
    protected Map mememtos;

    /**
     * The listeners list.
     */
    protected EventListenerList listeners;
    /**
     * Used to manage and fire property change listeners.
     */
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
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
     * Cache: node bounds
     */
    private Rectangle2D bounds;

    /**
     * Constructs a new graphics node.
     */
    public AbstractGraphicsNode() {}

    //
    // fire property change methods.
    //
    protected void firePropertyChange(String propertyName,
                                      boolean oldValue, boolean newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        if (root != null) {
            ConcreteRootGraphicsNode node = (ConcreteRootGraphicsNode) root;
            node.fireGlobalPropertyChange(this, propertyName,
                                          oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName,
                                      int oldValue, int newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        if (root != null) {
            ConcreteRootGraphicsNode node = (ConcreteRootGraphicsNode) root;
            node.fireGlobalPropertyChange(this, propertyName,
                                          oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName,
                                      Object oldValue, Object newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        if (root != null) {
            ConcreteRootGraphicsNode node = (ConcreteRootGraphicsNode) root;
            node.fireGlobalPropertyChange(this, propertyName,
                                          oldValue, newValue);
        }
    }

    /**
     * Fires the paint listener.
     * @param oldBounds the old bounds of this node in renderer space.
     */
    protected void fireGraphicsNodePaintListener(Rectangle2D oldBounds) {
        if (root != null) {
            ConcreteRootGraphicsNode node = (ConcreteRootGraphicsNode) root;
            node.fireGraphicsNodePaintListener(this, oldBounds);
        }
    }

    protected Rectangle2D getGlobalBounds() {
        if (root == null) {
            return null;
        }
        Rectangle2D r = getBounds();
        if (r == null) {
            return null;
        } else {
            return getGlobalTransform().createTransformedShape(r).getBounds();
        }
    }

    //
    // Properties methods
    //

    public void setCursor(Cursor newCursor) {
        Cursor oldCursor = cursor;
        this.cursor = newCursor;
        firePropertyChange("cursor", oldCursor, newCursor);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setTransform(AffineTransform newTransform) {
        Rectangle2D oldBounds = getGlobalBounds();
        invalidateGeometryCache();
        AffineTransform oldTransform = transform;
        this.transform = newTransform;
        firePropertyChange("transform", oldTransform, newTransform);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public AffineTransform getTransform() {
        return transform;
    }

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

    public void setComposite(Composite newComposite) {
        Rectangle2D oldBounds = getGlobalBounds();
        invalidateGeometryCache();
        Composite oldComposite = composite;
        this.composite = newComposite;
        firePropertyChange("composite", oldComposite, newComposite);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public Composite getComposite() {
        return composite;
    }

    public void setVisible(boolean isVisible) {
        Rectangle2D oldBounds = getGlobalBounds();
        boolean oldIsVisible = this.isVisible;
        this.isVisible = isVisible;
        firePropertyChange("visible", oldIsVisible, isVisible);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setClip(Clip newClipper) {
        Rectangle2D oldBounds = getGlobalBounds();
        invalidateGeometryCache();
        Clip oldClip = clip;
        this.clip = newClipper;
        firePropertyChange("clippingArea", oldClip, newClipper);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public Clip getClip() {
        return clip;
    }

    public void setRenderingHint(RenderingHints.Key key, Object value) {
        if (this.hints == null) {
            this.hints = new RenderingHints(key, value);
        } else {
            hints.put(key, value);
        }
    }

    public void setRenderingHints(Map hints) {
        if (this.hints == null) {
            this.hints = new RenderingHints(hints);
        } else {
            this.hints.putAll(hints);
        }
    }

    public void setRenderingHints(RenderingHints newHints) {
        this.hints = newHints;
    }

    public RenderingHints getRenderingHints() {
        return hints;
    }

    public void setMask(Mask newMask) {
        Rectangle2D oldBounds = getGlobalBounds();
        invalidateGeometryCache();
        Mask oldMask = mask;
        this.mask = newMask;
        firePropertyChange("mask", oldMask, newMask);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public Mask getMask() {
        return mask;
    }

    public void setFilter(Filter newFilter) {
        Rectangle2D oldBounds = getGlobalBounds();
        invalidateGeometryCache();
        Filter oldFilter = filter;
        this.filter = newFilter;
        firePropertyChange("filter", oldFilter, newFilter);
        fireGraphicsNodePaintListener(oldBounds);
    }

    public Filter getFilter() {
        return filter;
    }

    //
    // Drawing methods
    //

    public void paint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
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
        g2d.setRenderingHint(KEY_AREA_OF_INTEREST, curClip);
        rc.setTransform(g2d.getTransform());
        rc.setRenderingHints(g2d.getRenderingHints());
        rc.setAreaOfInterest(curClip);

        //
        // Check if any painting is needed at all. Get the clip (in user space)
        // and see if it intersects with this node's bounds (in user space).
        //
        boolean paintNeeded = true;
        Rectangle2D bounds = getBounds();
        Shape g2dClip = g2d.getClip();
        if(g2dClip != null){
            Rectangle2D clipBounds = g2dClip.getBounds();
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
                  // Render directly on the canvas
                primitivePaint(g2d, rc);
            } else{
                Filter filteredImage = null;

                if(filter == null){
                    filteredImage = rc.getGraphicsNodeRableFactory().
                        createGraphicsNodeRable(this);
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

                RenderedImage renderedNodeImage = filteredImage.createRendering(rc);
                if(renderedNodeImage != null){
                      // Draw RenderedImage has problems....
                      // This works around them...
                    WritableRaster wr;
                    wr = (WritableRaster)renderedNodeImage.getData();
                    wr = wr.createWritableTranslatedChild(0,0);
                    ColorModel cm = renderedNodeImage.getColorModel();
                    BufferedImage bi;
                    bi = new BufferedImage(cm, wr,
                                           cm.isAlphaPremultiplied(), null);

                    if(antialiasedClip){
                        // Remove hard edged clip
                        g2d.setClip(null);
                    }

                    Rectangle2D filterBounds = filteredImage.getBounds2D();
                    g2d.clip(filterBounds);

                    g2d.setTransform(IDENTITY);
                    g2d.drawImage(bi, null,
                                  renderedNodeImage.getMinX(),
                                  renderedNodeImage.getMinY());
                }
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
                // System.out.println("Using antialiased clip" + clip.getClass().getName());
            }
        }
        // return antialiased;
        return false;
    }

    //
    // Event support methods
    //

    public void addGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeMouseListener.class, l);
    }

    public void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeMouseListener.class, l);
        }
    }

    public void addGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeKeyListener.class, l);
    }

    public void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeKeyListener.class, l);
        }
    }

    public void setGraphicsNodeEventFilter(GraphicsNodeEventFilter evtFilter) {
        GraphicsNodeEventFilter oldFilter = eventFilter;
        this.eventFilter = evtFilter;
        firePropertyChange("graphicsNodeEventFilter", oldFilter, evtFilter);
    }

    public GraphicsNodeEventFilter getGraphicsNodeEventFilter() {
        return eventFilter;
    }

    public void setGraphicsNodeHitDetector(
            GraphicsNodeHitDetector hitDetector) {
        GraphicsNodeHitDetector oldDetector = this.hitDetector;
        this.hitDetector = hitDetector;
        firePropertyChange("graphicsNodeHitDetector", oldDetector, hitDetector);
    }

    public GraphicsNodeHitDetector getGraphicsNodeHitDetector() {
        return hitDetector;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public void dispatch(GraphicsNodeEvent evt) {
        // <!> FIXME : TO DO
    }

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

    //
    // Structural methods
    //

    public CompositeGraphicsNode getParent() {
        return parent;
    }

    public RootGraphicsNode getRoot() {
        return root;
    }

    protected void setRoot(RootGraphicsNode newRoot) {
        this.root = newRoot;
    }

    void setParent(CompositeGraphicsNode newParent) {
        this. parent = newParent;
    }

    public void putMemento(Object key, Object mememto) {
        if (mememtos == null) {
            mememtos = new HashMap();
        }
        mememtos.put(key, mememto);
    }

    public Object getMemento(Object key) {
        return (mememtos != null)? mememtos.get(key) : null;
    }

    public void removeMemento(Object key) {
        if (mememtos != null) {
            mememtos.remove(key);
            if (mememtos.isEmpty()) {
                mememtos = null;
            }
        }
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
     * renderBounds, i.e., the area painted by its primitivePaint
     * method. This is used in addition to the mask, clip and filter
     * to compute the area actually rendered by this node.
     */
    public Rectangle2D getBounds(){
        // Get the primitive bounds
        // Rectangle2D bounds = null;
        if(bounds == null){
            // The painted region, before cliping, masking
            // and compositing is either the area painted
            // by the primitive paint or the area painted
            // by the filter.
            if(filter == null){
                bounds = getPrimitiveBounds();
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

    public boolean contains(Point2D p) {
        return getBounds().contains(p);
    }

    public GraphicsNode nodeHitAt(Point2D p) {
        if (hitDetector != null) {
            if (hitDetector.isHit(this, p)) {
                return this;
            } else {
                return null;
            }
        } else {
            return (contains(p) ? this : null);
        }
    }

    public boolean intersects(Rectangle2D r) {
        return getBounds().intersects(r);
    }

    public void processMouseEvent(GraphicsNodeMouseEvent evt) {
        if ((listeners != null) && acceptEvent(evt)) {
            GraphicsNodeMouseListener[] listeners =
                (GraphicsNodeMouseListener[])
                getListeners(GraphicsNodeMouseListener.class);

            switch (evt.getID()) {
            case GraphicsNodeMouseEvent.MOUSE_MOVED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseMoved((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_DRAGGED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseDragged((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_ENTERED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseEntered((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_EXITED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseExited((GraphicsNodeMouseEvent) evt.clone());
                }
                    break;
            case GraphicsNodeMouseEvent.MOUSE_CLICKED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseClicked((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_PRESSED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mousePressed((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_RELEASED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseReleased((GraphicsNodeMouseEvent) evt.clone());
                }
                break;
                default:
                    throw new Error("Unknown Mouse Event type: "+evt.getID());
            }
        }
        evt.consume();
    }


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

    public void processChangeEvent(CompositeGraphicsNodeEvent evt) {
        if ((listeners != null) && acceptEvent(evt)) {
            CompositeGraphicsNodeListener[] listeners =
                (CompositeGraphicsNodeListener[])
                getListeners(CompositeGraphicsNodeListener.class);

            switch (evt.getID()) {
            case CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].graphicsNodeAdded((CompositeGraphicsNodeEvent) evt.clone());
                }
            case CompositeGraphicsNodeEvent.GRAPHICS_NODE_REMOVED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].graphicsNodeRemoved((CompositeGraphicsNodeEvent) evt.clone());
                }
                break;
            default:
                throw new Error("Unknown Key Event type: "+evt.getID());
            }
        }
        evt.consume();
    }

    protected boolean acceptEvent(GraphicsNodeEvent evt) {
        if (eventFilter != null) {
            return eventFilter.accept(this, evt);
        }
        return true;
    }
}
