/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

/**
 * Interface that defines the functionnality
 * of a list handler for the parser of 
 * attributes that are list.
 *
 * The attribute parser should use this interface 
 * in order to create the list representing
 * the values of the attribute.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public interface ListHandler {

    /**
     * Indicates that the parser starts
     * generating the list
     */
    void startList();

    /**
     * Indicates a new item to add to the list.
     *
     * @param item the new item to be added
     */
    void item(SVGItem item);

    /**
     * Indicates that the parser ends 
     * generating the list
     */
    void endList();

}
