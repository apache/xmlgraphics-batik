/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import org.apache.batik.gvt.RootGraphicsNode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
    
    /**
     * Adds the specified property change listener to receive property
     * change events from all elements of the GVT tree.
     * @param l the property change listener to add
     */
    public void addGlobalPropertyChangeListener(PropertyChangeListener l){ 
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
    public void fireGlobalPropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fires a property change event to "global" property change listeners.
     */
    public void fireGlobalPropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fires a property change event to "global" property change listeners.
     */
    public void fireGlobalPropertyChange(String propertyName, int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
