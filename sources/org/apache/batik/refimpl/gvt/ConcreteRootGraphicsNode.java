/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.EventListenerList;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.event.GraphicsNodePaintEvent;
import org.apache.batik.gvt.event.GraphicsNodePaintListener;

/**
 * An implementation of the <tt>RootGraphicsNode</tt> interface.
 *
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class ConcreteRootGraphicsNode extends ConcreteCompositeGraphicsNode
    implements RootGraphicsNode {

    /**
     * Used to manage and fire property change listeners.
     */
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructs a new empty canvas graphics node.
     */
    public ConcreteRootGraphicsNode() {}

    //
    // Properties methods
    //

    public RootGraphicsNode getRoot() {
        return this;
    }

    //////////////////////////////////////////////////////////////////////
    // Global Paint Listener
    //////////////////////////////////////////////////////////////////////

    /**
     * Adds the specified graphics node paint listener to receive graphics
     * node paint events from all elements of the GVT tree.
     * @param l the graphics node paint listener to add
     */
    public void addGraphicsNodePaintListener(GraphicsNodePaintListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(GraphicsNodePaintListener.class, l);
    }

    /**
     * Removes the specified graphics node paint listener so that it no
     * longer receives graphics node paint change events from all nodes of
     * the GVT tree.
     * @param l the graphics node paint listener to remove
     */
    public void removeGraphicsNodePaintListener(GraphicsNodePaintListener l) {
        if (listeners != null) {
            listeners.remove(GraphicsNodePaintListener.class, l);
        }
    }

    /**
     * Fires a graphics node paint event.
     */
    protected void fireGraphicsNodePaintListener(GraphicsNode source,
                                                 Rectangle2D oldBounds) {
        if (listeners != null) {
            GraphicsNodePaintListener[] listeners =
                (GraphicsNodePaintListener[])
                getListeners(GraphicsNodePaintListener.class);
            GraphicsNodePaintEvent evt =
                new GraphicsNodePaintEvent(source,
                                  GraphicsNodePaintEvent.GRAPHICS_NODE_MODIFIED,
                                           oldBounds);
            for (int i=0; i < listeners.length; ++i) {
                listeners[i].graphicsNodeModified(evt);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Global Property Change Listener
    //////////////////////////////////////////////////////////////////////

    /**
     * Adds the specified property change listener to receive property
     * change events from all elements of the GVT tree.
     * @param l the property change listener to add
     */
    public void addGlobalPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Adds the specified property change listener to receive property
     * change events for the specified property name of all nodes
     * of the GVT tree.
     * @param propertyName the name of the property
     * @param l the property change listener to add for the specified property
     */
    public void addGlobalPropertyChangeListener(String propertyName,
                                                PropertyChangeListener l){
        pcs.addPropertyChangeListener(propertyName, l);
    }

    /**
     * Removes the specified property change listener so that it no
     * longer receives property change events from all nodes of
     * the GVT tree.
     * @param l the property change listener to remove
     */
    public void removeGlobalPropertyChangeListener(PropertyChangeListener l){
        pcs.removePropertyChangeListener(l);
    }

    /**
     * Fires a property change event to "global" property change listeners.
     */
    public void fireGlobalPropertyChange(Object source,
                                         String propertyName,
                                         Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
                                                          propertyName,
                                                          oldValue,
                                                          newValue);
        pcs.firePropertyChange(evt);
    }

    /**
     * Fires a property change event to "global" property change listeners.
     */
    public void fireGlobalPropertyChange(Object source,
                                         String propertyName,
                                         boolean oldValue, boolean newValue) {
        Object oldV = (oldValue) ? Boolean.TRUE : Boolean.FALSE;
        Object newV = (newValue) ? Boolean.TRUE : Boolean.FALSE;
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
                                                          propertyName,
                                                          oldV,
                                                          newV);
        pcs.firePropertyChange(evt);
    }

    /**
     * Fires a property change event to "global" property change listeners.
     */
    public void fireGlobalPropertyChange(Object source,
                                         String propertyName,
                                         int oldValue, int newValue) {
        Object oldV = new Integer(oldValue);
        Object newV = new Integer(newValue);
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
                                                          propertyName,
                                                          oldV,
                                                          newV);
        pcs.firePropertyChange(evt);
    }
}
