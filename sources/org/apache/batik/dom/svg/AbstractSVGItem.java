/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

/**
 * Adapter for the SVGItem interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public abstract class AbstractSVGItem 
    implements SVGItem {

    /**
     * List the item belongs to.
     */
    protected AbstractSVGList parent;

    /**
     * String representation of the item.
     *
     * This is a cached representation of the
     * item while it is not changed.
     */
    protected String itemStringValue;

    /**
     * Return the string representation of the item.
     */
    protected abstract String getStringValue();

    /// Default Constructor.
    protected AbstractSVGItem(){
    }

    /**
     * Assign a parent list to this item.
     *
     * @param list : list the item belongs.
     */
    public void setParent(AbstractSVGList list){
        parent = list;
    }

    /**
     * Return the parent list of the item.
     *
     * @return list the item belongs.
     */
    public AbstractSVGList getParent(){
        return parent;
    }

    /**
     * Notifies the parent list that
     * the item has changed.
     *
     * Discard the cached representation
     * of the item.
     */
    protected void resetAttribute(){
        if ( parent != null ){
            itemStringValue = null;
            parent.itemChanged();
        }
    }

    /**
     * Return the cached representation
     * of the item if valid otherwise
     * re-computes the String representation
     * of the item.
     */
    public String getValueAsString(){
        if ( itemStringValue == null ){
            itemStringValue = getStringValue();
        }
        return itemStringValue;
    }
}
