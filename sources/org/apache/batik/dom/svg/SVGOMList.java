/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGList;

/**
 * This class provides an implementation of the {@link org.w3c.dom.svg.SVGList}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMList implements SVGList {
    /**
     * The list.
     */
    protected List list = new ArrayList();

    /**
     * The attribute modifier.
     */
    protected AttributeModifier attributeModifier;

    /**
     * Sets the associated attribute modifier.
     */
    public void setAttributeModifier(AttributeModifier am) {
	attributeModifier = am;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#getNumberOfItems()}.
     */
    public int getNumberOfItems() {
        return list.size();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#clear()}.
     */
    public void clear() {
        list.clear();
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#initialize(Object)}.
     */
    public Object initialize(Object newItem) throws SVGException {
        checkItem(newItem);
        list.clear();
        list.add(newItem);
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
        return newItem;
     }

    /**
     * <b>DOM</b>: Implements {@link SVGList#getItem(int)}.
     */
    public Object getItem(int i) throws DOMException {
        i--;
        if (i < 0 || i >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(i) });
        }
        return list.get(i);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#insertItemBefore(Object,int)}.
     */
    public Object insertItemBefore(Object newItem, int index)
        throws SVGException {
        checkItem(newItem);
        index--;
        if (index < 0) {
            list.add(0, newItem);
        } else if (index > list.size()) {
            list.add(list.size(), newItem);
        } else {
            list.add(index, newItem);
        }
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
        return newItem;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#replaceItem(Object,int)}.
     */
    public Object replaceItem(Object newItem, int index)
        throws DOMException, SVGException {
        checkItem(newItem);
        index--;
        if (index < 0 || index >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(index) });
        }
        list.set(index, newItem);
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
        return newItem;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#removeItem(int)}.
     */
    public Object removeItem(int index) throws DOMException {
        index--;
        if (index < 0 || index >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(index) });
        }
        Object result = list.remove(index);
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
        return result;
    }
    
    /**
     * <b>DOM</b>: Implements {@link SVGList#appendItem(Object)}.
     */
    public Object appendItem(Object np) throws SVGException {
        checkItem(np);
        list.add(np);
        if (attributeModifier != null) {
            attributeModifier.setAttributeValue(toString());
        }
        return np;
    }

    /**
     * Returns a string representation of this list.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            result.append(it.next().toString());
        }
        while (it.hasNext()) {
            result.append(getSeparator());
            result.append(it.next().toString());
        }
        return result.toString();
    }

    /**
     * Returns the list separator.
     */
    protected abstract String getSeparator();

    /**
     * Creates a localized DOM exception.
     */
    protected DOMException createDOMException(short type, String key,
                                              Object[] args) {
        return attributeModifier.createDOMException(type, key, args);
    }

    /**
     * Checks the validity of an item.
     */
    protected abstract void checkItem(Object item) throws SVGException;
}
