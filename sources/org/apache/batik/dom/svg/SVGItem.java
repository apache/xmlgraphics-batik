/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.dom.svg;

/**
 * This interface represents an item in an SVGXXXList.
 *
 * The item is required to hold a reference to its parent 
 * list so that an item can be moved from one list to another.
 *
 * A string representation of the item is also required in order
 * to update the value of the attribute the list containing 
 * the item represents.
 *
 * If the value of the item is changed, it is required 
 * to notify the list it belongs to in order to synchronized
 * the list and the attribute the list represents.
 *
 * @see AbstractSVGList#itemChanged()
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public interface SVGItem {

    /**
     * Associates an item to an SVGXXXList
     *
     * @param list list the item belongs to.
     */
    void setParent(AbstractSVGList list);

    /**
     * Return the list the item belongs to.
     *
     * @return list the item belongs to. This
     *   could be if the item belongs to no list.
     */
    AbstractSVGList getParent();

    /**
     * Return the String representation of the item.
     *
     * @return textual representation of the item
     *  to be inserted in the attribute value 
     *  representing the list.
     */
    String getValueAsString();
}
