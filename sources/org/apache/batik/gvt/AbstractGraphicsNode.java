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
import java.awt.Graphics2D;
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
import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

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
     * The listeners list.
     */
    protected EventListenerList listeners;

    /**
     * The hit detector used to filter mouse events.
     */
    protected GraphicsNodeHitDetector hitDetector;

    /**
     * The transform of this graphics node.
     */
    protected AffineTransform transform;

    /**
     * The inverse transform for this node, i.e., from parent node
     * to this node.
     */
    protected AffineTransform inverseTransform;

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
    protected ClipRable clip;

    /**
     * The rendering hints that control the quality to use when rendering
     * this graphics node.
     */
    protected RenderingHints hints;

    /**
     * The parent of this graphics node.
     */
    protected CompositeGraphicsNode parent;

    /**
     * The root of the GVT tree.
     */
    protected RootGraphicsNode root;

    /**
     * The mask of this graphics node.
     */
    protected Mask mask;

    /**
     * The filter of this graphics node.
     */
    protected Filter filter;

    /**
     * .The GraphicsNodeRable for this node.
     */
    protected WeakReference graphicsNodeRable;

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
     * Sets the transform of this node.
     *
     * @param newTransform the new transform of this node
     */
    public void setTransform(AffineTransform newTransform) {
        invalidateGeometryCache();
        this.transform = newTransform;
        if(transform.getDeterminant() != 0){
            try{
                inverseTransform = transform.createInverse();
            }catch(NoninvertibleTransformException e){
                // Should never happen.
                throw new Error();
            }
        }
        else{
            // The transform is not invertible. Use the same
            // transform.
            inverseTransform = transform;
        }
    }

    /**
     * Returns the transform of this node or null if any.
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * Returns the inverse transform for this node.
     */
    public AffineTransform getInverseTransform(){
        return inverseTransform;
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
     *
     * @param composite the composite of this node
     */
    public void setComposite(Composite newComposite) {
        invalidateGeometryCache();
        this.composite = newComposite;
    }

    /**
     * Returns the composite of this node or null if any.
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Sets if this node is visible or not depending on the specified value.
     *
     * @param isVisible If true this node is visible
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Returns true if this node is visible, false otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    public void setClip(ClipRable newClipper) {
        invalidateGeometryCache();
        this.clip = newClipper;
    }

    /**
     * Returns the clipping filter of this node or null if any.
     */
    public ClipRable getClip() {
        return clip;
    }

    /**
     * Maps the specified key to the specified value in the rendering hints of
     * this node.
     *
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
     *
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
     *
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
     *
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
     *
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

    /**
     * Returns the GraphicsNodeRable for this node.
     * The GraphicsNodeRable is the Renderable (Filter) before any
     * of the filter operations have been applied.
     */
    public GraphicsNodeRable getGraphicsNodeRable() {
        GraphicsNodeRable ret = null;
        if (graphicsNodeRable != null) {
            ret = (GraphicsNodeRable)graphicsNodeRable.get();
            if (ret != null) return ret;
        }
        ret = new GraphicsNodeRable8Bit(this);
        graphicsNodeRable = new WeakReference(ret);
        return ret;
    }


    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d){

        // first, make sure we haven't been interrupted
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        if ((composite != null) &&
            (composite instanceof AlphaComposite)) {
            AlphaComposite ac = (AlphaComposite)composite;
            if (ac.getAlpha() < 0.001)
                return;         // No point in drawing
        }

        // Set up graphic context. It is important to setup the transform first,
        // because the clip is defined in this node's user space.
        Shape defaultClip = g2d.getClip();
        Composite defaultComposite = g2d.getComposite();
        AffineTransform defaultTransform = g2d.getTransform();
        RenderingHints defaultHints = null;

        if (hints != null) {
            defaultHints = g2d.getRenderingHints();
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

        // Check if any painting is needed at all. Get the clip (in user space)
        // and see if it intersects with this node's bounds (in user space).
        boolean paintNeeded = true;
        Rectangle2D bounds = getBounds();
        Shape g2dClip = g2d.getClip();
        if (g2dClip != null) {
            Rectangle2D clipBounds = g2dClip.getBounds2D();
            if(bounds != null && !bounds.intersects(clipBounds.getX(),
                                                    clipBounds.getY(),
                                                    clipBounds.getWidth(),
                                                    clipBounds.getHeight())){
                paintNeeded = false;
            }
        }

        // Only paint if needed.
        if (paintNeeded){
            AffineTransform txf = g2d.getTransform();
            boolean antialiasedClip = false;
            if(clip != null){
                antialiasedClip =
                    isAntialiasedClip(g2d.getTransform(),
                                      g2d.getRenderingHints(),
                                      clip.getClipPath());
            }

            boolean useOffscreen = isOffscreenBufferNeeded();

            useOffscreen |= antialiasedClip;

            if (!useOffscreen) {
                // Render on this canvas.
                primitivePaint(g2d);
            } else {
                Filter filteredImage = null;

                if(filter == null){
                    filteredImage = getGraphicsNodeRable();
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
        if (defaultHints != null) {
            g2d.setRenderingHints(defaultHints);
        }
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
     * Returns true of an offscreen buffer is needed to render this node, false
     * otherwise.  
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
     *
     * @param evt the event to dispatch
     */
    public void dispatchEvent(GraphicsNodeEvent evt) {
        switch(evt.getID()) {
        case GraphicsNodeMouseEvent.MOUSE_PRESSED:
        case GraphicsNodeMouseEvent.MOUSE_RELEASED:
        case GraphicsNodeMouseEvent.MOUSE_MOVED:
        case GraphicsNodeMouseEvent.MOUSE_ENTERED:
        case GraphicsNodeMouseEvent.MOUSE_EXITED:
        case GraphicsNodeMouseEvent.MOUSE_DRAGGED:
            processMouseEvent((GraphicsNodeMouseEvent)evt);
            break;
        case GraphicsNodeKeyEvent.KEY_TYPED:
        case GraphicsNodeKeyEvent.KEY_PRESSED:
        case GraphicsNodeKeyEvent.KEY_RELEASED:
            processKeyEvent((GraphicsNodeKeyEvent)evt);
            break;
        default:
            break;
        }
    }

    /**
     * Adds the specified graphics node mouse listener to receive graphics node
     * mouse events from this node.
     *
     * @param l the graphics node mouse listener to add 
     */
    public void addGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeMouseListener.class, l);
    }

    /**
     * Removes the specified graphics node mouse listener so that it no longer
     * receives graphics node mouse events from this node.
     *
     * @param l the graphics node mouse listener to remove 
     */
    public void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeMouseListener.class, l);
        }
    }

    /**
     * Adds the specified graphics node key listener to receive graphics node
     * key events from this node.
     *
     * @param l the graphics node key listener to add 
     */
    public void addGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodeKeyListener.class, l);
    }

    /**
     * Removes the specified graphics node key listener so that it no longer
     * receives graphics node key events from this node.
     *
     * @param l the graphics node key listener to remove 
     */
    public void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodeKeyListener.class, l);
        }
    }

    /**
     * Sets the hit detector for this node.
     *
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
     * Dispatches a graphics node mouse event to this node or one of its child.
     *
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
                    listeners[i].mouseMoved(evt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_DRAGGED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseDragged(evt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_ENTERED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseEntered(evt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_EXITED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseExited(evt);
                }
                    break;
            case GraphicsNodeMouseEvent.MOUSE_CLICKED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseClicked(evt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_PRESSED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mousePressed(evt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_RELEASED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].mouseReleased(evt);
                }
                break;
            default:
                throw new Error("Unknown Mouse Event type: "+evt.getID());
            }
        }
        evt.consume();
    }

    /**
     * Dispatches a graphics node key event to this node or one of its child.
     *
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
                    listeners[i].keyPressed(evt);
                }
                break;
            case GraphicsNodeKeyEvent.KEY_RELEASED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].keyReleased(evt);
                }
                break;
            case GraphicsNodeKeyEvent.KEY_TYPED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].keyTyped(evt);
                }
                break;
            default:
                throw new Error("Unknown Key Event type: "+evt.getID());
            }
        }
        evt.consume();
    }

    /**
     * Returns an array of listeners that were added to this node and of the
     * specified type.
     *
     * @param listenerType the type of the listeners to return 
     */
    public EventListener [] getListeners(Class listenerType) {
        return listeners.getListeners(listenerType);
    }

    /**
     * Returns true is this node accepts the specified event, false otherwise.
     *
     * @param evt the event to check
     * @return always true at this time
     */
    protected boolean acceptEvent(GraphicsNodeEvent evt) {
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
     * Returns the root of the GVT tree or null if the node is not part of a GVT
     * tree.  
     */
    public RootGraphicsNode getRoot() {
        return root;
    }

    /**
     * Sets the root node of this graphics node.
     *
     * @param newRoot the new root node of this node
     */
    protected void setRoot(RootGraphicsNode newRoot) {
        this.root = newRoot;
    }

    /**
     * Sets the parent node of this graphics node.
     *
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
     * Returns the bounds of this node in user space. This includes primitive
     * paint, filtering, clipping and masking.
     */
    public Rectangle2D getBounds(){
        // Get the primitive bounds
        // Rectangle2D bounds = null;
        if (bounds == null) {
            // The painted region, before cliping, masking and compositing is
            // either the area painted by the primitive paint or the area
            // painted by the filter.
            if(filter == null){
                bounds = getPrimitiveBounds();
            } else {
                bounds = filter.getBounds2D();
            }
            // Factor in the clipping area, if any
            if(bounds != null){
                if (clip != null) {
                    Rectangle2D clipR = clip.getClipPath().getBounds2D();
                    if (clipR.intersects(bounds))
                        Rectangle2D.intersect(bounds, clipR, bounds);
                }
                // Factor in the mask, if any
                if (mask != null) {
                    Rectangle2D maskR = mask.getBounds2D();
                    if (maskR.intersects(bounds))
                        Rectangle2D.intersect(bounds, maskR, bounds);
                }
            }

            // Make sure we haven't been interrupted
            if (Thread.currentThread().isInterrupted()) {
                // The Thread has been interrupted. Invalidate
                // any cached values and proceed.
                invalidateGeometryCache();
            }
        }

        return bounds;
    }

    /**
     * Returns the bounds of this node after applying the input transform
     * (if any), concatenated with this node's transform (if any).
     *
     * @param txf the affine transform with which this node's transform should
     *        be concatenated. Should not be null.
     */
    public Rectangle2D getTransformedBounds(AffineTransform txf){
        AffineTransform t = txf;
        if (transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }

        // The painted region, before cliping, masking and compositing is either
        // the area painted by the primitive paint or the area painted by the
        // filter.
        Rectangle2D tBounds = null;
        if (filter == null) {
	    // Use txf, not t
            tBounds = getTransformedPrimitiveBounds(txf);
        } else {
            tBounds = t.createTransformedShape
		(filter.getBounds2D()).getBounds2D();
        }
        // Factor in the clipping area, if any
        if (tBounds != null) {
            if (clip != null) {
                tBounds.intersect
		    (tBounds,
		     t.createTransformedShape(clip.getClipPath()).getBounds2D(),
		     tBounds);
            }

            // Factor in the mask, if any
            if(mask != null) {
                tBounds.intersect
		    (tBounds,
		     t.createTransformedShape(mask.getBounds2D()).getBounds2D(),
		     tBounds);
            }
        }

        return tBounds;
    }

    /**
     * Returns the bounds of this node's primitivePaint after applying the input
     * transform (if any), concatenated with this node's transform (if any).
     *
     * @param txf the affine transform with which this node's transform should
     *        be concatenated. Should not be null.
     */
    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform txf) {
        Rectangle2D tpBounds = getPrimitiveBounds();
        if (tpBounds == null) {
            return null;
        }
        AffineTransform t = txf;
        if (transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }

        return t.createTransformedShape(tpBounds).getBounds2D();
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getTransformedGeometryBounds(AffineTransform txf) {
        Rectangle2D tpBounds = getGeometryBounds();
        if (tpBounds == null) {
            return null;
        }
        AffineTransform t = txf;
        if (transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }

        return t.createTransformedShape(tpBounds).getBounds2D();
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        return getBounds().contains(p);
    }

    /**
     * Returns true if the interior of this node intersects the interior of a
     * specified Rectangle2D, false otherwise.
     *
     * @param r the specified Rectangle2D in the user node space
     */
    public boolean intersects(Rectangle2D r) {
        return getBounds().intersects(r);
    }

    /**
     * Returns the GraphicsNode containing point p if this node or one of its
     * children is sensitive to mouse events at p.
     *
     * @param p the specified Point2D in the user space
     */
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
}
