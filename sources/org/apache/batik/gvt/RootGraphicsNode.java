/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.util.List;
import java.util.LinkedList;

import org.apache.batik.gvt.event.GraphicsNodeChangeListener;

/**
 * The top-level graphics node of the GVT tree.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class RootGraphicsNode extends CompositeGraphicsNode {

    List treeGraphicsNodeChangeListeners = null;

   /**
     * Constructs a new empty <tt>RootGraphicsNode</tt>.
     */
    public RootGraphicsNode() {}

    /**
     * Returns the root of the GVT tree or null if the node is not
     * part of a GVT tree.  
     */
    public RootGraphicsNode getRoot() {
        return this;
    }

    public List getTreeGraphicsNodeChangeListeners() {
        if (treeGraphicsNodeChangeListeners == null) {
            treeGraphicsNodeChangeListeners = new LinkedList();
        }
        return treeGraphicsNodeChangeListeners;
    }

    public void addTreeGraphicsNodeChangeListener
        (GraphicsNodeChangeListener l) {
        getTreeGraphicsNodeChangeListeners().add(l);
    }

    public void removeTreeGraphicsNodeChangeListener
        (GraphicsNodeChangeListener l) {
        getTreeGraphicsNodeChangeListeners().remove(l);
    }

}
