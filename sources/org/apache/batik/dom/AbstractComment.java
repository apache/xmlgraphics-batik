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
 * This class implements the {@link org.w3c.dom.Comment} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractComment
    extends    AbstractCharacterData
    implements Comment {
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return "#comment".
     */
    public String getNodeName() {
	return "#comment";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#COMMENT_NODE}
     */
    public short getNodeType() {
	return COMMENT_NODE;
    }
}
