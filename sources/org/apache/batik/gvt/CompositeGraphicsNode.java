/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.apache.batik.gvt.event.CompositeGraphicsNodeEvent;
import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;
import org.apache.batik.gvt.event.GraphicsNodeEvent;

/**
 * A CompositeGraphicsNode is a graphics node that can contain graphics nodes.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeGraphicsNode extends AbstractGraphicsNode 
    implements List {

    public static final Rectangle2D VIEWPORT = new Rectangle(0, 0, 0, 0);

    /**
     * The children of this composite graphics node.
     */
    protected GraphicsNode [] children;

    /**
     * The number of children of this composite graphics node.
     */
    protected int count;

    /**
     * The number of times the children list has been structurally modified.
     */
    protected int modCount;

    /**
     * This flag indicates if this node has BackgroundEnable = 'new'.
     * If so traversal of the gvt tree can halt here.
     */
    protected Rectangle2D backgroundEnableRgn = null;

    /**
     * Internal Cache: Geometry bounds for this node, not taking into
     * account any of its children rendering attributes into account
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: Primitive bounds.
     */
    private Rectangle2D primitiveBounds;

    /**
     * Internal Cache: the outline.
     */
    private Shape outline;

    /**
     * Constructs a new empty <tt>CompositeGraphicsNode</tt>.
     */
    public CompositeGraphicsNode() {}

    //
    // Structural methods
    //

    /**
     * Returns the list of children.
     */
    public List getChildren() {
        return this;
    }

    /**
     * Sets the enable background property to the specified rectangle.
     *
     * @param bgRgn the region that defines the background enable property
     */
    public void setBackgroundEnable(Rectangle2D bgRgn) {
        backgroundEnableRgn = bgRgn;
    }

    /**
     * Returns the region defining the background enable property.
     */
    public Rectangle2D getBackgroundEnable() {
        return backgroundEnableRgn;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (count == 0) {
            return;
        }

        // Paint children
        for (int i=0; i < count; ++i) {
            GraphicsNode node = children[i];
            if (node == null) {
                continue;
            }
            node.paint(g2d, rc);
        }
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
        super.dispatchEvent(evt);
        switch(evt.getID()) {
        case CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED:
        case CompositeGraphicsNodeEvent.GRAPHICS_NODE_REMOVED:
            processCompositeEvent((CompositeGraphicsNodeEvent)evt);
            break;
        default:
            break;
        }
    }

    /**
     * Adds the specified composite graphics node listener to receive composite
     * graphics node events from this node.
     *
     * @param l the composite graphics node listener to add 
     */
    public void addCompositeGraphicsNodeListener
	(CompositeGraphicsNodeListener l) {

        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(CompositeGraphicsNodeListener.class, l);
    }

    /**
     * Removes the specified composite graphics node listener so that it no
     * longer receives composite graphics node events from this node.
     *
     * @param l the composite graphics node listener to remove 
     */
    public void removeCompositeGraphicsNodeListener
	(CompositeGraphicsNodeListener l) {

        if (listeners != null) {
            listeners.remove(CompositeGraphicsNodeListener.class, l);
        }
    }

    /**
     * Processes a composite event occuring on this graphics node.
     *
     * @param evt the event to process
     */
   public void processCompositeEvent(CompositeGraphicsNodeEvent evt) {
        if ((listeners != null) && acceptEvent(evt)) {
            CompositeGraphicsNodeListener [] listeners =
                (CompositeGraphicsNodeListener[])
                getListeners(CompositeGraphicsNodeListener.class);

            switch (evt.getID()) {
            case CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].graphicsNodeAdded(evt);
                }
                break;
            case CompositeGraphicsNodeEvent.GRAPHICS_NODE_REMOVED:
                for (int i=0; i<listeners.length; ++i) {
                    listeners[i].graphicsNodeRemoved(evt);
                }
                break;
            default:
                throw new Error("Unknown Composite Event type: "+evt.getID());
            }
        }
        evt.consume();
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
        super.invalidateGeometryCache();
        geometryBounds = null;
        primitiveBounds = null;
        outline = null;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (primitiveBounds == null) {
            int i=0;
            while (primitiveBounds == null && i < count) {
                primitiveBounds = children[i++].getTransformedBounds
                    (IDENTITY, rc);
            }

            Rectangle2D ctb = null;
            while (i < count) {
                ctb = children[i++].getTransformedBounds(IDENTITY, rc);
                if (ctb != null) {
                    if (primitiveBounds == null) {
                        // another thread has set the primitive bounds to null,
                        // need to recall this function
                        return getPrimitiveBounds(rc);
                    } else {
                        primitiveBounds.add(ctb);
                    }
                }
            }

            // Make sure we haven't been interrupted
            if (Thread.currentThread().isInterrupted()) {
                // The Thread has been interrupted.
		// Invalidate any cached values and proceed.
                invalidateGeometryCache();
            }
        }
        return primitiveBounds;
    }

    /**
     * Returns the bounds of this node's primitivePaint after applying the input
     * transform (if any), concatenated with this node's transform (if any).
     *
     * @param txf the affine transform with which this node's transform should
     *        be concatenated. Should not be null.
     */
    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform txf,
                                                     GraphicsNodeRenderContext rc) {
        AffineTransform t = txf;
        if(transform != null){
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }

        int i = 0;
        Rectangle2D tpb = null;
        while( tpb == null && i < count){
            tpb = children[i++].getTransformedBounds(t, rc);
        }

        Rectangle2D ctb = null;
        while(i < count){
            ctb = children[i++].getTransformedBounds(t, rc);
            if(ctb != null){
                tpb.add(ctb);
            }
        }

        return tpb;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc) {
        if(geometryBounds == null){
            // System.out.println("geometryBounds are null");
            int i=0;
            while(geometryBounds == null && i < count){
                geometryBounds
                    = children[i++].getTransformedGeometryBounds (IDENTITY, rc);
            }

            Rectangle2D cgb = null;
            while(i<count){
                cgb = children[i++].getTransformedGeometryBounds(IDENTITY, rc);
                if(cgb != null){
                    if (geometryBounds == null) {
                        // another thread has set the geometry bounds to null,
                        // need to recall this function
                        return getGeometryBounds(rc);
                    } else {
                        geometryBounds.add(cgb);
                    }
                }
            }
        }
        return geometryBounds;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into accoun. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example. The returned value is
     * transformed by the concatenation of the input transform and this node's
     * transform.
     *
     * @param txf the affine transform with which this node's transform should
     *        be concatenated. Should not be null.
     */
    public Rectangle2D getTransformedGeometryBounds
        (AffineTransform txf, GraphicsNodeRenderContext rc) {

        AffineTransform t = txf;
        if(transform != null){
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }

        Rectangle2D gb = null;
        int i=0;
        while(gb == null && i < count){
            gb = children[i++].getTransformedGeometryBounds(t, rc);
        }

        Rectangle2D cgb = null;
        while(i < count){
            cgb = children[i++].getTransformedGeometryBounds(t, rc);
            if(cgb != null){
                gb.add(cgb);
            }
        }
        return gb;
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        if (count > 0 && getBounds(rc).contains(p)) {
            Point2D pt = null;
            Point2D cp = null; // Propagated to children
            for (int i=0; i < count; ++i) {
                AffineTransform t = children[i].getInverseTransform();
                if(t != null){
                    pt = t.transform(p, pt);
                    cp = pt;
                }
                else{
                    cp = p;
                }

                if (children[i].contains(cp, rc)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns the GraphicsNode containing point p if this node or one of its
     * children is sensitive to mouse events at p.
     *
     * @param p the specified Point2D in the user space
     */
    public GraphicsNode nodeHitAt(Point2D p, GraphicsNodeRenderContext rc) {
        Rectangle2D bounds = getBounds(rc);
        if (count > 0 && bounds != null && bounds.contains(p)) {
            //
            // Go backward because the children are in rendering order
            //
            Point2D pt = null;
            Point2D cp = null; // Propagated to children
            for (int i=count-1; i >= 0; --i) {
                AffineTransform t = children[i].getInverseTransform();
                if(t != null){
                    pt = t.transform(p, pt);
                    cp = pt;
                }
                else{
                    cp = p;
                }
                GraphicsNode node = children[i].nodeHitAt(cp, rc);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        if (outline == null) {
            outline = new GeneralPath();
            for (int i = 0; i < count; i++) {
                Shape childOutline = children[i].getOutline(rc);
                if (childOutline != null) {
                    AffineTransform tr = children[i].getTransform();
                    if (tr != null) {
                        ((GeneralPath)outline).append(tr.createTransformedShape(childOutline), false);
                    } else {
                        ((GeneralPath)outline).append(childOutline, false);
                    }
                }
            }
        }
        return outline;
    }

    //
    // Structural info
    //

    /**
     * Sets the root node of this grahics node and modify all its children.
     */
    protected void setRoot(RootGraphicsNode newRoot) {
        super.setRoot(newRoot);
        for (int i=0; i < count; ++i) {
            GraphicsNode node = children[i];
            ((AbstractGraphicsNode)node).setRoot(newRoot);
        }
    }

    //
    // List implementation
    //

    /**
     * Returns the number of children of this composite graphics node.
     */
    public int size() {
        return count;
    }

    /**
     * Returns true if this composite graphics node does not contain
     * graphics node, false otherwise.
     */
    public boolean isEmpty() {
        return (count == 0);
    }

    /**
     * Returns true if this composite graphics node contains the
     * specified graphics node, false otherwise.
     * @param node the node to check
     */
    public boolean contains(Object node) {
        return (indexOf(node) >= 0);
    }

    /**
     * Returns an iterator over the children of this graphics node.
     */
    public Iterator iterator() {
        return new Itr();
    }

    /**
     * Returns an array containing all of the graphics node in the
     * children list of this composite graphics node in the correct
     * order. If the children list fits in the specified array, it is
     * returned therein. Otherwise, a new array is allocated.
     */
    public Object [] toArray() {
        GraphicsNode [] result = new GraphicsNode[count];
        for (int i=0; i < count; ++i) {
            result[i] = children[i];
        }
        return result;
    }

    /**
     * Returns an array containing all of the graphics node in the children list
     * of this composite graphics node in the correct order.
     *
     * @param a the array to fit if possible 
     */
    public Object[] toArray(Object [] a) {
        if (a.length < count) {
            a = new GraphicsNode[count];
        }
        System.arraycopy(children, 0, a, 0, count);
        if (a.length > count) {
            a[count] = null;
        }
        return a;
    }

    /**
     * Returns the graphics node at the specified position in the children list.
     *
     * @param index the index of the graphics node to return
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public Object get(int index) {
        checkRange(index);
        return children[index];
    }

    // Modification Operations

    /**
     * Replaces the graphics node at the specified position in the children list
     * with the specified graphics node.
     *
     * @param index the index of the graphics node to replace
     * @param o the graphics node to be stored at the specified position
     * @return the graphics node previously  at the specified position
     * @exception IndexOutOfBoundsException if the index is out of range
     * @exception IllegalArgumentException if the node is not an
     * instance of GraphicsNode 
     */
    public Object set(int index, Object o) {
        // Check for correct arguments
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o+" is not a GraphicsNode");
        }
        checkRange(index);
        GraphicsNode node = (GraphicsNode) o;
        // Reparent the graphics node and tidy up the tree's state
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        // Replace the node to the children list
        GraphicsNode oldNode = children[index];
        children[index] = node;
        // Set the parents of the graphics nodes
        ((AbstractGraphicsNode) node).setParent(this);
        ((AbstractGraphicsNode) oldNode).setParent(null);
        // Set the root of the graphics node
        ((AbstractGraphicsNode) node).setRoot(this.getRoot());
        ((AbstractGraphicsNode) oldNode).setRoot(null);
        // Invalidates cached values
        invalidateGeometryCache();
        // Create and dispatch events
        int id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_REMOVED;
        dispatchEvent(new CompositeGraphicsNodeEvent(this, id, oldNode));
        id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED;
        dispatchEvent(new CompositeGraphicsNodeEvent(this, id, node));
        return oldNode;
     }

    /**
     * Adds the specified graphics node to this composite graphics node.
     *
     * @param o the graphics node to add
     * @return true (as per the general contract of Collection.add)
     * @exception IllegalArgumentException if the node is not an
     * instance of GraphicsNode
     */
    public boolean add(Object o) {
        // Check for correct argument
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o+" is not a GraphicsNode");
        }
        GraphicsNode node = (GraphicsNode) o;
        // Reparent the graphics node and tidy up the tree's state
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        // Add the graphics node to the children list
        ensureCapacity(count + 1);  // Increments modCount!!
        children[count++] = node;
        // Set the parent of the graphics node
        ((AbstractGraphicsNode) node).setParent(this);
        // Set the root of the graphics node
        ((AbstractGraphicsNode) node).setRoot(this.getRoot());
        // Invalidates cached values
        invalidateGeometryCache();
        // Create and dispatch event
        int id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED;
        dispatchEvent(new CompositeGraphicsNodeEvent(this, id, node));
        return true;
    }

    /**
     * Inserts the specified graphics node at the specified position in this
     * children list. Shifts the graphics node currently at that position (if
     * any) and any subsequent graphics nodes to the right (adds one to their
     * indices).
     *
     * @param index the position at which the specified graphics node is to
     * be inserted.
     * @param o the graphics node to be inserted.
     * @exception IndexOutOfBoundsException if the index is out of range
     * @exception IllegalArgumentException if the node is not an
     * instance of GraphicsNode 
     */
    public void add(int index, Object o) {
        // Check for correct arguments
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o+" is not a GraphicsNode");
        }
        if (index > count || index < 0) {
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+count);
        }
        GraphicsNode node = (GraphicsNode) o;
        // Reparent the graphics node and tidy up the tree's state
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        // Insert the node to the children list
        ensureCapacity(count+1);  // Increments modCount!!
        System.arraycopy(children, index, children, index+1, count-index);
        children[index] = (GraphicsNode) node;
        count++;
        // Set parent of the graphics node
        ((AbstractGraphicsNode) node).setParent(this);
        // Set root of the graphics node
        ((AbstractGraphicsNode) node).setRoot(this.getRoot());
        // Invalidates cached values
        invalidateGeometryCache();
        // Create and dispatch event
        int id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED;
        dispatchEvent(new CompositeGraphicsNodeEvent(this, id, node));
    }

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the specified graphics node from the children list.
     *
     * @param o the node the remove
     * @return true if the children list contains the specified graphics node
     * @exception IllegalArgumentException if the node is not an
     * instance of GraphicsNode
     */
    public boolean remove(Object o) {
        // Check for correct argument
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o+" is not a GraphicsNode");
        }
        GraphicsNode node = (GraphicsNode) o;
        if (node.getParent() != this) {
            return false;
        }
        // Remove the node
        int index = 0;
        for (; node != children[index]; index++);
        remove(index);
        return true;
    }

    /**
     * Removes the graphics node at the specified position in the children list.
     * Shifts any subsequent graphics nodes to the left (subtracts one from
     * their indices).
     *
     * @param index the position of the graphics node to remove
     * @return the graphics node that was removed
     * @exception IndexOutOfBoundsException if index out of range <tt> 
     */
    public Object remove(int index) {
        // Check for correct argument
        checkRange(index);
        // Remove the node at the specified index
        modCount++;
        GraphicsNode oldNode = children[index];
        int numMoved = count - index - 1;
        if (numMoved > 0) {
            System.arraycopy(children, index+1, children, index, numMoved);
        }
        children[--count] = null; // Let gc do its work
        if (count == 0) {
            children = null;
        }
        // Set parent of the node
        ((AbstractGraphicsNode) oldNode).setParent(null);
        // Set root of the node
        ((AbstractGraphicsNode) oldNode).setRoot(null);
        // Invalidates cached values
        invalidateGeometryCache();
        // Create and dispatch event
        int id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_REMOVED;
        dispatchEvent(new CompositeGraphicsNodeEvent(this, id, oldNode));
        return oldNode;
    }

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true if this composite graphics node contains all the graphics
     * node in the specified collection, false otherwise.
     *
     * @param c the collection to be checked for containment 
     */
    public boolean containsAll(Collection c) {
        Iterator i = c.iterator();
        while (i.hasNext()) {
            if (!contains(i.next())) {
                    return false;
            }
        }
        return true;
    }

    // Search Operations

    /**
     * Returns the index in the children list of the specified graphics node or
     * -1 if the children list does not contain this graphics node.
     *
     * @param node the graphics node to search for 
     */
    public int indexOf(Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode) node).getParent() == this) {
            for (int i = 0; i < count; i++) {
                if (node == children[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index in this children list of the last occurence of the
     * specified graphics node, or -1 if the list does not contain this graphics
     * node.
     *
     * @param node the graphics node to search for 
     */
    public int lastIndexOf(Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode) node).getParent() == this) {
            for (int i = count-1; i >= 0; i--) {
                if (node == children[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    // List Iterators

    /**
     * Returns an iterator over the children of this graphics node.
     */
    public ListIterator listIterator() {
        return listIterator(0);
    }

    /**
     * Returns an iterator over the children of this graphics node, starting at
     * the specified position in the children list.
     *
     * @param index the index of the first graphics node to return
     * from the children list 
     */
    public ListIterator listIterator(int index) {
        if (index < 0 || index > count) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
        return new ListItr(index);
    }

    // View

    /**
     * <b>Not supported</b> -
     * Throws <tt>UnsupportedOperationException</tt> exception.
     */
    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.
     *
     * @param index the index to check
     */
    private void checkRange(int index) {
        if (index >= count || index < 0) {
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+count);
        }
    }

    /**
     * Increases the capacity of the children list, if necessary, to ensure that
     * it can hold at least the number of graphics nodes specified by the
     * minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity.
     */
    public void ensureCapacity(int minCapacity) {
        if (children == null) {
            children = new GraphicsNode[4];
        }
        modCount++;
        int oldCapacity = children.length;
        if (minCapacity > oldCapacity) {
            GraphicsNode [] oldData = children;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            children = new GraphicsNode[newCapacity];
            System.arraycopy(oldData, 0, children, 0, count);
        }
    }

    /**
     * An implementation of the java.util.Iterator interface.
     */
    private class Itr implements Iterator {

        /**
         * Index of graphics node to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of graphics node returned by most recent call to next or
         * previous.  Reset to -1 if this graphics node is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != count;
        }

        public Object next() {
            try {
                Object next = get(cursor);
                checkForComodification();
                lastRet = cursor++;
                return next;
            } catch(IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                CompositeGraphicsNode.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }


    /**
     * An implementation of the java.util.ListIterator interface.
     */
    private class ListItr extends Itr implements ListIterator {

        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public Object previous() {
            try {
                Object previous = get(--cursor);
                checkForComodification();
                lastRet = cursor;
                return previous;
            } catch(IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor-1;
        }

        public void set(Object o) {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                CompositeGraphicsNode.this.set(lastRet, o);
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Object o) {
            checkForComodification();
            try {
                CompositeGraphicsNode.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
