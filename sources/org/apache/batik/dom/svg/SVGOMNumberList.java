/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

/**
 * This class implements the {@link SVGNumberList} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMNumberList
    implements SVGNumberList,
               LiveAttributeValue,
               ModificationHandler {
    
    /**
     * The implementation of the list.
     */
    protected SVGList list = new SVGList();

    /**
     * The modification handler.
     */
    protected ModificationHandler modificationHandler;

    /**
     * Whether or not the current change is due to an internal change.
     */
    protected boolean internalChange;

    /**
     * Sets the modification handler.
     */
    public void setModificationHandler(ModificationHandler mh) {
	modificationHandler = mh;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#getNumberOfItems()}.
     */
    public int getNumberOfItems() {
        return list.getNumberOfItems();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#getNumberOfItems()}.
     */
    public void clear() throws DOMException {
        list.clear();
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, "");
            internalChange = false;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#initialize(SVGNumber)}.
     */
    public SVGNumber initialize(SVGNumber newItem)
        throws DOMException, SVGException {
        SVGOMNumber result = (SVGOMNumber)list.initialize(newItem);
        result.setModificationHandler(this);
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, Float.toString(newItem.getValue()));
            internalChange = false;
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#getItem(int)}.
     */
    public SVGNumber getItem(int index) throws DOMException {
        return (SVGNumber)list.getItem(index);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#insertItemBefore(SVGNumber,int)}.
     */
    public SVGNumber insertItemBefore(SVGNumber newItem, int index)
        throws DOMException, SVGException {
        SVGOMNumber result = (SVGOMNumber)list.insertItemBefore(newItem, index);
        result.setModificationHandler(this);
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, getStringRepresentation());
            internalChange = false;
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#replaceItem(SVGNumber,int)}.
     */
    public SVGNumber replaceItem(SVGNumber newItem, int index)
        throws DOMException, SVGException {
        SVGOMNumber result = (SVGOMNumber)list.replaceItem(newItem, index);
        result.setModificationHandler(this);
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, getStringRepresentation());
            internalChange = false;
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#removeItem(int)}.
     */
    public SVGNumber removeItem(int index) throws DOMException {
        SVGOMNumber result = (SVGOMNumber)list.removeItem(index);
        result.setModificationHandler(this);
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, getStringRepresentation());
            internalChange = false;
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumberList#appendItem(SVGNumber)}.
     */
    public SVGNumber appendItem(SVGNumber newItem)
        throws DOMException, SVGException {
        SVGOMNumber result = (SVGOMNumber)list.appendItem(newItem);
        result.setModificationHandler(this);
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, getStringRepresentation());
            internalChange = false;
        }
        return result;
    }

    /**
     * Returns the string representation of the list.
     */
    public String getStringRepresentation() {
        StringBuffer result = new StringBuffer();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            result.append(((SVGNumber)it.next()).getValue());
        }
        while (it.hasNext()) {
            result.append(' ');
            result.append(((SVGNumber)it.next()).getValue());
        }
        return result.toString();
    }

    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
        if (!internalChange) {
            if (oldValue == null) {
                parseValue(newValue.getValue());
            } else {
                parseValue(oldValue.getValue(), newValue.getValue());
            }
        }
    }

    /**
     * Parses the given value and initializes the list.
     */
    public void parseValue(String val) {
        parseValue("", val);
    }

    /**
     * Parses the old and new values and modifies the list.
     */
    protected void parseValue(String oldVal, String newVal) {
        if (!oldVal.equals(newVal)) {
            list.clear();
            StringTokenizer st = new StringTokenizer(newVal, " ,");
            while (st.hasMoreTokens()) {
                SVGOMNumber n = new SVGOMNumber();
                n.parseValue(st.nextToken());
                n.setModificationHandler(this);
                list.appendItem(n);
            }
        }
    }

    // ModificationHandler ///////////////////////////////////////////////

    /**
     * Implements {@link ModificationHandler#valueChanged(Object,String)}.
     */
    public void valueChanged(Object object, String value) {
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, getStringRepresentation());
            internalChange = false;
        }
    }

    /**
     * Implements {@link ModificationHandler#getObject(Object,String)}.
     */
    public Object getObject(Object key) {
        return null;
    }
}
