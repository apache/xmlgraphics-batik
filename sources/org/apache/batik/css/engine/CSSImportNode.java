/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.w3c.dom.Node;

/**
 * This interface represents a node which imports a subtree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSImportNode extends Node {
    
    /**
     * The CSSImportedElementRoot.
     */
    CSSImportedElementRoot getCSSImportedElementRoot();
}
