/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;
import org.apache.batik.gvt.TextNode;

/**
 * Marker interface, mostly, that encapsulates information about a
 * selection gesture.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$ 
 */
public interface Mark {
    /*
     * Return the TextNode this Mark is associated with 
     */
    TextNode getTextNode();
}
