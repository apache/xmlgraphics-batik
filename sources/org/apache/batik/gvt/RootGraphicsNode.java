/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.beans.PropertyChangeListener;

/**
 * The top-level graphics node of the GVT tree.
 *
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public interface RootGraphicsNode extends CompositeGraphicsNode {

    /**
     * Adds the specified property change listener to receive property
     * change events from all elements of the GVT tree.
     * @param l the property change listener to add
     */
    void addGlobalPropertyChangeListener(PropertyChangeListener l);

    /**
     * Adds the specified property change listener to receive property
     * change events for the specified property name of all nodes
     * of the GVT tree.
     * @param propertyName the name of the property
     * @param l the property change listener to add for the specified property
     */
    void addGlobalPropertyChangeListener(String propertyName,
                                       PropertyChangeListener l);

    /**
     * Removes the specified property change listener so that it no
     * longer receives property change events from all nodes of
     * the GVT tree.
     * @param l the property change listener to remove
     */
    void removeGlobalPropertyChangeListener(PropertyChangeListener l);

}
