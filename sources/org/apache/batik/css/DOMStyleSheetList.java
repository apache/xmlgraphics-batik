/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 * This class implements the {@link org.w3c.dom.stylesheets.StyleSheetList}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DOMStyleSheetList implements StyleSheetList {
    /**
     * The list implementation
     */
    List list = new LinkedList();

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheetList#getLength()}.
     */
    public int getLength() {
	return list.size();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheetList#item(int)}.
     */
    public StyleSheet item(int index) {
	if (index < 0 || index >= list.size()) {
	    return null;
	}
	return (StyleSheet)list.get(index);
    }

    /**
     * Appends an item to the list.
     */
    public void append(StyleSheet ss) {
	list.add(ss);
    }
}
