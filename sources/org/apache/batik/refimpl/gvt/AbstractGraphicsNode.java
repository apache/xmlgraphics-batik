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
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
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
import org.apache.batik.gvt.Mask;
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

/**
 * A partial implementation of the <tt>GraphicsNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public abstract class AbstractGraphicsNode implements GraphicsNode {
    /**
     * Used to draw renderable images
     */
    private static final AffineTransform IDENTITY = new AffineTransform();

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
     * The clipping area of this graphics node.
     */
    protected Shape clip;
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
     * Constructs a new graphics node.
     */
    public AbstractGraphicsNode() {}

    //
    // fire property change methods.
    //
    protected void firePropertyChange(String propertyName, boolean oldValue,
                                      boolean newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        RootGraphicsNode node = getRoot();
        if (root != null)
            ((ConcreteRootGraphicsNode)root).fireGlobalPropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue,
                                      int newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        RootGraphicsNode node = getRoot();
        if (root != null)
            ((ConcreteRootGraphicsNode)root).fireGlobalPropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
                                      Object newValue){
        // First fire to local listeners
        pcs.firePropertyChange(propertyName, oldValue, newValue);
        // fire to root node listeners if possible.
        RootGraphicsNode node = getRoot();
        if (root != null)
            ((ConcreteRootGraphicsNode)root).fireGlobalPropertyChange(propertyName, oldValue, newValue);
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
        AffineTransform oldTransform = transform;
        this.transform = newTransform;
        firePropertyChange("transform", oldTransform, newTransform);
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void setComposite(Composite newComposite) {
        Composite oldComposite = composite;
        this.composite = newComposite;
        firePropertyChange("composite", oldComposite, newComposite);
    }

    public Composite getComposite() {
        return composite;
    }

    public void setVisible(boolean isVisible) {
        boolean oldIsVisible = this.isVisible;
        this.isVisible = isVisible;
        firePropertyChange("visible", oldIsVisible, isVisible);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setClippingArea(Shape newClippingArea) {
        Shape oldClip = clip;
        this.clip = newClippingArea;
        firePropertyChange("clippingArea", oldClip, newClippingArea);
    }

    public Shape getClippingArea() {
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
        Mask oldMask = mask;
        this.mask = newMask;
        firePropertyChange("mask", oldMask, newMask);
    }

    public Mask getMask() {
        return mask;
    }

    public void setFilter(Filter newFilter) {
        Filter oldFilter = filter;
        this.filter = newFilter;
        firePropertyChange("filter", oldFilter, newFilter);
    }

    public Filter getFilter() {
        return filter;
    }

    //
    // Drawing methods
    //

    public void paint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (!isOffscreenBufferNeeded()) {
            Shape defaultClip = g2d.getClip();
            Composite defaultComposite = g2d.getComposite();
            if (clip != null) {
                g2d.clip(clip);
            }
            if (composite != null) {
                g2d.setComposite(composite);
            }
            primitivePaint(g2d, rc);
            g2d.setClip(defaultClip);
            g2d.setComposite(defaultComposite);
        } else {
            Shape defaultClip = g2d.getClip();
            Composite defaultComposite = g2d.getComposite();

            RenderableImage nodeImage = rc.getGraphicsNodeRableFactory().createGraphicsNodeRable(this);

            RenderableImage filteredImage = nodeImage;

            if(filter != null){
                traceFilter(filter, "=====>> ");
                filteredImage = filter;
            }

            if(clip != null){
                g2d.clip(clip);
            }
            if(composite != null){
                g2d.setComposite(composite);
            }

            // Create the render context for drawing this 
            // node.
            
            RenderContext context = new RenderContext(g2d.getTransform(), g2d.getClip(), g2d.getRenderingHints());
            RenderedImage renderedNodeImage = filteredImage.createRendering(context);

            if(renderedNodeImage != null){
                AffineTransform defaultTransform = g2d.getTransform();
                g2d.setTransform(IDENTITY);
                System.out.println("rend X: " + renderedNodeImage.getMinX() +
                                   " rend Y: " + renderedNodeImage.getMinY());

                // THIS WORKS, BUT BREAKS WITH FILTER-RES ON (PROBABLY A SEPERATE
                // PROBLEM.
                g2d.drawRenderedImage(renderedNodeImage, IDENTITY);

                // THIS WORKS FOR PAN, BUT SCALING IS BROKEN
                // g2d.drawRenderableImage(filteredImage, 
                //                        AffineTransform.getTranslateInstance(renderedNodeImage.getMinX(), 
                //                                                             renderedNodeImage.getMinY()));
                g2d.setTransform(defaultTransform);
            }
                
            g2d.setClip(defaultClip);
            g2d.setComposite(defaultComposite);
            // throw new Error("Not yet implemented");
            /*
            Shape defaultClip = g2d.getClip();
            Composite defaultComposite = g2d.getComposite();
            RenderableImage nodeImage = rc.getSourceGraphicsNode(this);
            if(clip != null){
                g2d.clip(clip);
            }
            if(composite != null){
                g2d.setComposite(composite);
            }

            // Create the render context for drawing this 
            // node.
            RenderContext context = new RenderContext(g2d.getTransform(),
                                                      g2d.getRenderingHints());
            RenderedImage renderedNodeImage =
                nodeImage.createRendering(context);
            AffineTransform defaultTransform = g2d.getTransform();
            g2d.setTransform(IDENTITY);
            g2d.translate(renderedNodeImage.getMinX(),
                          renderedNodeImage.getMinY());
            g2d.drawRenderedImage(renderedNodeImage, IDENTITY);

            g2d.setTransform(defaultTransform);
            g2d.setClip(defaultClip);
            g2d.setComposite(defaultComposite);
            */
        }
            /* Offscreen rendering required. The process is the following:
             *
             * 1. Filter.
             *    If filtering required, ask the filter to create a
             *    RenderableImage.  Else render the node in an
             *    offscreen image (the size of the image is computed
             *    using the bounds of the node in device space).  At
             *    the end of this step, get a RenderedImage that is
             *    sized and positioned in device space (this is
             *    important to get resolution independant filter
             *    results).
             *
             * 2. Mask.
             *    Create an offscreen buffer to perform masking (i.e.,
             *    paint the mask into the result of 1., using
             *    AlphaComposite.SrcIn compositing rule and extract
             *    subraster corresponding to the intersection of the
             *    filter area and the mask.). This operation happens in
             *    device space.
             *
             * 3. Compositing and Clipping.
             *    Paint the result of 2. after setting the proper clip
             *    and composition rule on the destination
             *    Graphics2D. Clip has to be converted to device space
             *    for that operation.
             */
            // <!> FIXME : TODO
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

    public boolean contains(Point2D p) {
        //System.out.println("Bounds "+getBounds()+"point "+p);
        if (getBounds().contains(p)) {
            return getOutline().contains(p);
        } else {
            return false;
        }
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
        return getOutline().intersects(r);
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
