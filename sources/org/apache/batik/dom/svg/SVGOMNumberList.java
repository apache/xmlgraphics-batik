/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

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
               LiveAttributeValue {
    
    /**
     * The implementation of the list.
     */
    protected SVGList list = new SVGList();

    /**
     * The modification handler.
     */
    protected ModificationHandler modificationHandler;

    /**
     * Sets the modification handler.
     */
    public void setModificationHandler(ModificationHandler mh) {
	modificationHandler = mh;
    }

    public int getNumberOfItems() {
        return list.getNumberOfItems();
    }

    public void clear() throws DOMException {
        list.clear();
    }

    public SVGNumber initialize(SVGNumber newItem)
        throws DOMException, SVGException {
        SVGOMNumber result = (SVGOMNumber)list.initialize(newItem);
        
        return result;
    }

    public SVGNumber getItem(int index) throws DOMException {
        return null;
    }

    public SVGNumber insertItemBefore(SVGNumber newItem, int index)
        throws DOMException, SVGException {
        return null;
    }

    public SVGNumber replaceItem(SVGNumber newItem, int index)
        throws DOMException, SVGException {
        return null;
    }

    public SVGNumber removeItem(int index) throws DOMException {
        return null;
    }

    public SVGNumber appendItem(SVGNumber newItem)
        throws DOMException, SVGException {
        return null;
    }

    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
	parseValue(newValue.getValue());
    }

    /**
     * Parses the given value and initializes the list.
     */
    public void parseValue(String val) {

    }
}
