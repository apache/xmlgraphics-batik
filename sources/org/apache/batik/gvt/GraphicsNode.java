/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Cursor;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import java.util.Map;
import java.util.EventListener;

import java.beans.PropertyChangeListener;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeEventFilter;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.Clip;

/**
 * The base class for all graphics nodes. A GraphicsNode encapsulates
 * graphical attributes and can perform atomic operations of a complex
 * rendering.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public interface GraphicsNode {

    //
    // Properties methods
    //

    /**
     * Sets the cursor of this node.
     * @param newCursor the new cursor of this node
     */
    void setCursor(Cursor newCursor);

    /**
     * Returns the cursor of this node.
     * @return the cursor of this node
     */
    Cursor getCursor();

    /**
     * Sets the transform of this node.
     * @param newTransform the new transform of this node
     */
    void setTransform(AffineTransform newTransform);

    /**
     * Returns the transform of this node.
     * @return the transform of this node
     */
    AffineTransform getTransform();

    /**
     * Returns the concatenated transform, i.e., this node's 
     * transform preconcatenated with it's parent's transforms.
     */
    AffineTransform getGlobalTransform();

    /**
     * Sets the composite of this node.
     * @param composite the composite of this node
     */
    void setComposite(Composite newComposite);

    /**
     * Returns the composite of this node.
     * @return the composite of this node
     */
    Composite getComposite();

    /**
     * Sets if this node is visible or not depending on the specified value.
     * @param isVisible If true this node is visible
     */
    void setVisible(boolean isVisible);

    /**
     * Determines whether or not this node is visible when its parent
     * is visible. Nodes are initially visible.
     * @return true if this node is visible, false otherwise
     */
    boolean isVisible();

    /**
     * Sets the clipping filter for this node.
     * @param newClipper the new clipping filter of this node
     */
    void setClip(Clip newClipper);

    /**
     * Returns the clipping filter of this node or null if any.
     * @return the clipping filter of this node or null if any
     */
    Clip getClip();

    /**
     * Maps the specified key to the specified value in the rendering
     * hints of this node.
     * @param key the key of the hint to be set
     * @param value the value indicating preferences for the specified
     * hint category.
     */
    void setRenderingHint(RenderingHints.Key key, Object value);

    /**
     * Copies all of the mappings from the specified Map to the
     * rendering hints of this node.
     * @param hints the rendering hints to be set
     */
    void setRenderingHints(Map hints);

    /**
     * Sets the rendering hints of this node.
     * @param newHints the new rendering hints of this node
     */
    void setRenderingHints(RenderingHints newHints);

    /**
     * Returns the rendering hints of this node or null if any.
     * @return the rendering hints of this node or null if any
     */
    RenderingHints getRenderingHints();

    /**
     * Sets the mask of this node.
     * @param newMask the new mask of this node
     */
    void setMask(Mask newMask);

    /**
     * Returns the mask of this node or null if any.
     * @return the mask of this node or null if any
     */
    Mask getMask();

    /**
     * Sets the filter of this node.
     * @param newFilter the new filter of this node
     */
    void setFilter(Filter newFilter);

    /**
     * Returns the filter of this node or null if any.
     * @return the filter of this node or null if any
     */
    Filter getFilter();

    //
    // Drawing methods
    //

    /**
     * Returns true if this node needs a progressive paint, false otherwise.
     * @return true if this node needs a progressive paint
     */
    boolean hasProgressivePaint();

    /**
     * Paints one step of this node rendering operations.
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    void progressivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc);

    /**
     * Paints this node.
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    void paint(Graphics2D g2d, GraphicsNodeRenderContext rc);

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc);

    //
    // Event support methods
    //

    /**
     * Adds the specified graphics node mouse listener to receive
     * graphics node mouse events from this node.
     * @param l the graphics node mouse listener to add
     */
    void addGraphicsNodeMouseListener(GraphicsNodeMouseListener l);

    /**
     * Removes the specified graphics node mouse listener so that it
     * no longer receives graphics node mouse events from this node.
     * @param l the graphics node mouse listener to remove
     */
    void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener l);

    /**
     * Adds the specified graphics node key listener to receive
     * graphics node key events from this node.
     * @param l the graphics node key listener to add
     */
    void addGraphicsNodeKeyListener(GraphicsNodeKeyListener l);

    /**
     * Removes the specified graphics node key listener so that it
     * no longer receives graphics node key events from this node.
     * @param l the graphics node key listener to remove
     */
    void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener l);

    /**
     * Sets the graphics node event filter of this node.
     * @param evtFilter the new graphics node event filter
     */
    void setGraphicsNodeEventFilter(GraphicsNodeEventFilter evtFilter);

    /**
     * Returns the graphics node event filter of this node.
     * @return the graphics node event filter of this node
     */
    GraphicsNodeEventFilter getGraphicsNodeEventFilter();

    /**
     * Sets the hit detector for this node.
     * @param hitDetector the new hit detector
     */
    void setGraphicsNodeHitDetector(GraphicsNodeHitDetector hitDetector);

    /**
     * Returns the hit detector for this node.
     * @return the hit detector for this node
     */
    GraphicsNodeHitDetector getGraphicsNodeHitDetector();

    /**
     * Adds the specified property change listener to receive property
     * change events from this node.
     * @param l the property change listener to add
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Adds the specified property change listener to receive property
     * change events for the specified property name of this node.
     * @param propertyName the name of the property
     * @param l the property change listener to add for the specified property
     */
    void addPropertyChangeListener(String propertyName,
                   PropertyChangeListener l);

    /**
     * Removes the specified property change listener so that it no
     * longer receives property change events from this node.
     * @param l the property change listener to remove
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Dispatches a graphics node event to this node or one of its child.
     * @param evt the evt to dispatch
     */
    void processMouseEvent(GraphicsNodeMouseEvent evt);

    /**
     * Dispatches a graphics node event to this node or one of its child.
     * @param evt the evt to dispatch
     */
    void processKeyEvent(GraphicsNodeKeyEvent evt);

    /**
     * Dispatches a graphics node event to this node or one of its child.
     * @param evt the evt to dispatch
     */
    void dispatch(GraphicsNodeEvent evt);

    /**
     * Returns an array of listeners that were added to this node and
     * of the specified type.
     * @param listenerType the type of the listeners to return
     */
    EventListener [] getListeners(Class listenerType);

    //
    // Structural methods
    //

    /**
     * Returns the parent of this node or null if any.
     * @return the parent of this node or null if any.
     */
    CompositeGraphicsNode getParent();


    /**
     * Returns the root of the GVT tree or <code>null</code> if
     * the node is not part of a GVT tree.
     */
    RootGraphicsNode getRoot();

    /**
     * Associates the specified memento with the specified key in this node.
     * @param key the key with which the specified memento is to be associated
     * @param memento the memento to be associated with the specified key
     */
    void putMemento(Object key, Object mememto);

    /**
     * Returns the value of the memento with the specified key or null if any.
     * @param the key whose associated value is to be returned
     */
    Object getMemento(Object key);

    /**
     * Removes the memento object with the specified key.
     * @param key the key whose mapping is to be removed from the map
     */
    void removeMemento(Object key);

    //
    // Geometric methods
    //

    /**
     * Returns the bounds of this node in user space. This includes
     * primitive paint, filtering, clipping and masking.
     */
    Rectangle2D getBounds();

    /**
     * Returns the bounds of the area covered by this node's
     * primitive paint.
     */
    Rectangle2D getPrimitiveBounds();

    /**
     * Returns the bounds of the area covered by this node, without
     * taking any of its rendering attribute into account, i.e., exclusive
     * of any clipping, masking, filtering or stroking, for example.
     */
    Rectangle2D getGeometryBounds();

    /**
     * Tests if the specified Point2D is inside the boundary of this
     * node.
     * @param p the specified Point2D in the user space
     * @return true if the coordinates are inside, false otherwise
     */
    boolean contains(Point2D p);

    /**
     * Tests if the interior of this node intersects the interior of a
     * specified Rectangle2D.
     * @param r the specified Rectangle2D in the user node space
     * @return true if the rectangle intersects, false otherwise
     */
    boolean intersects(Rectangle2D r);

    /**
     * Returns the GraphicsNode containing point p if this node or one of
     * its children is sensitive to mouse events at p.
     * @param p the specified Point2D in the user space
     * @return the GraphicsNode containing p on this branch of the GVT tree.
     */
    GraphicsNode nodeHitAt(Point2D p);

    /**
     * Returns the outline of this node.
     * @return the outline of this node
     */
    Shape getOutline();
}
