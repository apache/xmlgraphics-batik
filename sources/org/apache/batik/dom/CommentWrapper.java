/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Comment;

/**
 * This class implements a wrapper for a Comment. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CommentWrapper extends CharacterDataWrapper implements Comment {
    
    /**
     * Creates a new CommentWrapper object.
     */
    public CommentWrapper(DocumentWrapper dw, Comment c) {
        super(dw, c);
    }
}
