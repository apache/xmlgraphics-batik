/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeEventFilter;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.CompositeGraphicsNodeEvent;
import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;

/**
 * An implementation of the <tt>CompositeGraphicsNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class ConcreteCompositeGraphicsNode extends AbstractGraphicsNode
        implements CompositeGraphicsNode, List {

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
     * Cache: Geometry bounds for this node, not taking into account any of its
     * children rendering attributes into account
     */
    private Rectangle2D geometryBounds;

    /**
     * Cache: Primitive bounds.
     */
    private Rectangle2D primitiveBounds;

    /**
     * Constructs a new empty composite graphics node.
     */
    public ConcreteCompositeGraphicsNode() {}

    //
    // Structural methods
    //

    public List getChildren() {
        return this;
    }


    public void setBackgroundEnable(Rectangle2D bgRgn) {
        Rectangle2D oldBgRgn = backgroundEnableRgn;
        backgroundEnableRgn = bgRgn;
        firePropertyChange("backgroundEnable", oldBgRgn,
                           backgroundEnableRgn);
    }
    public Rectangle2D getBackgroundEnable() {
        return backgroundEnableRgn;
    }

    //
    // Drawing methods
    //

    public boolean hasProgressivePaint() {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    public void progressivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (count == 0) {
            return;
        }

        // Paint children
        for (int i=0; i < count; ++i) {
            GraphicsNode node = children[i];
            if (node == null || !node.isVisible()) {
                continue;
            }
            try {
               node.paint(g2d, rc);
            } catch (InterruptedException ie) {
            }
        }
    }

    //
    // Event support methods
    //

    public void addCompositeGraphicsNodeListener(
            CompositeGraphicsNodeListener l) {
        listeners.add(CompositeGraphicsNodeListener.class, l);
    }

    public void removeCompositeGraphicsNodeListener(
            CompositeGraphicsNodeListener l) {
        listeners.remove(CompositeGraphicsNodeListener.class, l);
    }

    //
    // Geometric methods
    //

    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        geometryBounds = null;
        primitiveBounds = null;
    }

    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        if (count == 0) {
            return false;
        }
        if (getBounds(rc).contains(p)) {
            for (int i=0; i < count; ++i) {
                AffineTransform t = children[i].getTransform();
                if (t == null) {
                    t = IDENTITY;
                } else {
                    // put the coordinates to children space
                    try {
                        t = t.createInverse();
                    } catch (NoninvertibleTransformException ex) {}
                }
                Point2D pt = t.transform(p, null);
                if (children[i].contains(pt, rc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public GraphicsNode nodeHitAt(Point2D p, GraphicsNodeRenderContext rc) {
        if (count == 0) {
            return null;
        }
        if (getBounds(rc).contains(p)) {
            //
            // Go backward because the children are in rendering order
            //
            for (int i=count-1; i >= 0; --i) {
                AffineTransform t = children[i].getTransform();
                if (t == null) {
                    t = IDENTITY;
                } else {
                    // put the coordinates to children space
                    try {
                        t = t.createInverse();
                    } catch (NoninvertibleTransformException ex) {}
                }
                Point2D pt = t.transform(p, null);
                GraphicsNode node = children[i].nodeHitAt(pt, rc);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    protected Rectangle2D getGlobalBounds(GraphicsNodeRenderContext rc) {
        if (count == 0) {
            return null;
        }
        Rectangle2D r = getBounds(rc);
        if (r == null) {
            return null;
        } else {
            return getGlobalTransform().createTransformedShape(r).getBounds2D();
        }
    }

    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (primitiveBounds == null) {
            Rectangle2D bounds = null, nodeBounds = null;
            AffineTransform txf = null;
            if(count > 0){
                txf = children[0].getTransform();
                nodeBounds = children[0].getBounds(rc);
                bounds = (txf == null)
                    ? nodeBounds
                    : txf.createTransformedShape(nodeBounds).getBounds2D();
            } else {
                // With the following empty groups may have bad side effects.
                return new Rectangle(0, 0, 0, 0);
            }
            for (int i=1; i < count; ++i) {
                GraphicsNode node = children[i];
                nodeBounds = node.getBounds(rc);
                txf = children[i].getTransform();
                if (txf != null) {
                    nodeBounds =
                        txf.createTransformedShape(nodeBounds).getBounds2D();
                }
                bounds.add(nodeBounds);
            }
            primitiveBounds = bounds;
        }
        return primitiveBounds;
    }

    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        Rectangle2D b = null;
        if(geometryBounds == null){
            Rectangle2D nodeBounds = null;
            AffineTransform txf = null;
            if(count > 0){
                txf = children[0].getTransform();
                nodeBounds = children[0].getGeometryBounds(rc);
                geometryBounds = (txf == null)
                    ? nodeBounds
                    : txf.createTransformedShape(nodeBounds).getBounds2D();
                b = geometryBounds;
            } else {
                System.out.println("Group is empty ...");
                Exception e = new Exception();
                e.printStackTrace();
                // With the following empty groups may have bad side effects.
                // Note that we do not cache this value as this does not
                // have a performance impact and it is likely that this
                // group is under construction.
                b = new Rectangle(0, 0, 0, 0);
            }
            for (int i=1; i < count; ++i) {
                GraphicsNode node = children[i];
                nodeBounds = node.getGeometryBounds(rc);
                txf = children[i].getTransform();
                if (txf != null) {
                    nodeBounds =
                        txf.createTransformedShape(nodeBounds).getBounds2D();
                }
                geometryBounds.add(nodeBounds);
            }
        }
        else{
            b = geometryBounds;
        }

        return b;
    }

    public Shape getOutline(GraphicsNodeRenderContext rc) {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    //
    // Structural info
    //

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
        System.arraycopy(children, 0, result, 0, count);
        return result;
    }

    /**
     * Returns an array containing all of the graphics node in the
     * children list of this composite graphics node in the correct
     * order.
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
     * @param index the index of the graphics node to return
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public Object get(int index) {
        checkRange(index);
        return children[index];
    }

    // Modification Operations

    /**
     * Replaces the graphics node at the specified position in the
     * children list with the specified graphics node.
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
        dispatch(new CompositeGraphicsNodeEvent(this, id, oldNode));
        id = CompositeGraphicsNodeEvent.GRAPHICS_NODE_ADDED;
        dispatch(new CompositeGraphicsNodeEvent(this, id, node));
        return oldNode;
     }

    /**
     * Adds the specified graphics node to this composite graphics node.
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
        dispatch(new CompositeGraphicsNodeEvent(this, id, node));
        return true;
    }

    /**
     * Inserts the specified graphics node at the specified position
     * in this children list. Shifts the graphics node currently at
     * that position (if any) and any subsequent graphics nodes to the
     * right (adds one to their indices).
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
        dispatch(new CompositeGraphicsNodeEvent(this, id, node));
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
     * Shifts any subsequent graphics nodes to the left (subtracts one
     * from their indices).
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
        dispatch(new CompositeGraphicsNodeEvent(this, id, oldNode));
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
     * Returns true if this composite graphics node contains all the
     * graphics node in the specified collection, false otherwise.
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
     * Returns the index in the children list of the specified
     * graphics node or -1 if the children list does not contain this
     * graphics node.
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
     * Returns the index in this children list of the last occurence
     * of the specified graphics node, or -1 if the list does not contain
     * this graphics node.
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
     * Returns an iterator over the children of this graphics node,
     * starting at the specified position in the children list.
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
     * @param index the index to check
     */
    private void checkRange(int index) {
        if (index >= count || index < 0) {
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+count);
        }
    }

    /**
     * Increases the capacity of the children list, if necessary, to
     * ensure that it can hold at least the number of graphics nodes
     * specified by the minimum capacity argument.
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
     *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
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
                ConcreteCompositeGraphicsNode.this.remove(lastRet);
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
     *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
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
                ConcreteCompositeGraphicsNode.this.set(lastRet, o);
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Object o) {
            checkForComodification();
            try {
                ConcreteCompositeGraphicsNode.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
