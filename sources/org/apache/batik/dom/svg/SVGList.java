/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.DOMException;

/**
 * This class is used to implement SVG lists.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGList {
    /**
     * The list implementation.
     */
    protected List list = new LinkedList();

    /**
     * Returns the number of items in the list.
     */
    public int getNumberOfItems() {
        return list.size();
    }

    /**
     * Clears the list.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Clears the list and adds the given item to the list.
     */
    public Object initialize(Object item) {
        list.clear();
        list.add(item);
        return item;
    }

    /**
     * Returns the item at the given index.
     */
    public Object getItem(int index) throws DOMException {
        if (index < 0 || index >= list.size()) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "");
        }
        return list.get(index);
    }

    /**
     * Inserts an item in the list before the item at the given position.
     */
    public Object insertItemBefore(Object item, int index) {
        index = (index < 1) ? 0 : (index > list.size()) ? list.size() : index;
        list.add(index, item);
        return item;
    }

    /**
     * Replaces the item at the given index with the given item.
     */
    public Object replaceItem(Object item, int index) throws DOMException {
        Object result = removeItem(index);
        list.add(index, item);
        return result;
    }

    /**
     * Removes the item at the given index.
     */
    public Object removeItem(int index) throws DOMException {
        if (index < 0 || index >= list.size()) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "");
        }
        return list.remove(index);
    }

    /**
     * Appends the given item to the end of the list.
     */
    public Object appendItem(Object item) {
        list.add(item);
        return item;
    }
}
