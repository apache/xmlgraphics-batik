/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DocumentFragment;

/**
 * This class implements a wrapper for a DocumentFragment. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentFragmentWrapper extends NodeWrapper implements DocumentFragment {
    
    /**
     * Creates a new DocumentFragmentWrapper object.
     */
    public DocumentFragmentWrapper(DocumentWrapper dw, DocumentFragment df) {
        super(dw, df);
    }
}
