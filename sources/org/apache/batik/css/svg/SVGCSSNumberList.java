/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.DOMException;

import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

/**
 * This class provides an implementation of SVGNumberList.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSNumberList implements SVGNumberList {

    /**
     * The underlying list.
     */
    protected List list = new LinkedList();

    /**
     * Creates a readonly copy of this list.
     */
    SVGCSSNumberList createReadOnlyCopy() {
        SVGCSSReadOnlyNumberList res = new SVGCSSReadOnlyNumberList();
        List l = res.getList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            l.add(new SVGCSSReadOnlyNumber(((SVGNumber)it.next()).getValue()));
        }
        return res;
    }

    /**
     * Returns the underlying list.
     */
    public List getList() {
        return list;
    }

    /**
     * returns the number of items in this list.
     */
    public int getNumberOfItems() {
        return list.size();
    }
 
    /**
     * Clears this list.
     */
    public void clear() throws DOMException {
        list.clear();
    }

    /**
     * Initializes this list with the given item.
     */
    public SVGNumber initialize(SVGNumber item)
        throws DOMException, SVGException {
        list.clear();
        list.add(item);
        return item;
    }

    /**
     * Returns the item at the given index.
     */
    public SVGNumber getItem(int index) throws DOMException {
        if (index < 0 || index >= list.size()) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "");
        }
        return (SVGNumber)list.get(index);
    }

    /**
     * Inserts the given item at the given index.
     */
    public SVGNumber insertItemBefore(SVGNumber item, int index)
        throws DOMException, SVGException {
        index = (index < 1) ? 0 : (index > list.size()) ? list.size() : index;
        list.add(index, item);
        return item;
    }

    /**
     * Inserts the item at the given index.
     */
    public SVGNumber replaceItem(SVGNumber item, int index)
        throws DOMException, SVGException {
        Object result = removeItem(index);
        list.add(index, item);
        return (SVGNumber)result;
    }

    /**
     * Removes the item at the given index.
     */
    public SVGNumber removeItem(int index) throws DOMException {
        if (index < 0 || index >= list.size()) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "");
        }
        return (SVGNumber)list.remove(index);
    }
    
    /**
     * Appends the item at the given index.
     */
    public SVGNumber appendItem(SVGNumber item)
        throws DOMException, SVGException {
        list.add(item);
        return item;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SVGCSSNumberList)) {
            return false;
        }
        SVGCSSNumberList nl = (SVGCSSNumberList)obj;
        List l = nl.getList();

        if (list.size() != l.size()) {
            return false;
        }

        Iterator it1 = list.iterator();
        Iterator it2 = l.iterator();
        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a printable representation of this object.
     */
    public String toString() {
        StringBuffer res = new StringBuffer();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            res.append(it.next().toString());
        }
        while (it.hasNext()) {
            res.append(", ");
            res.append(it.next().toString());
        }
        return res.toString();
    }
}
