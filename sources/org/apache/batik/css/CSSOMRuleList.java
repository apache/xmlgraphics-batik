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
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;

/**
 * This class provides an implementation of the
 * {@link org.w3c.dom.css.CSSRuleList} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMRuleList implements CSSRuleList {
    /**
     * The list.
     */
    protected List list = new LinkedList();

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRuleList#getLength()}.
     */
    public int getLength() {
	return list.size();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRuleList#item(int)}.
     */
    public CSSRule item(int index) {
	if (index < 0 || index >= list.size()) {
	    return null;
	}
	return (CSSRule)list.get(index);
    }

    /**
     * Inserts a rule in the list.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified index is not a valid 
     *   insertion point.
     */
    public int insert(CSSRule rule, int index) throws DOMException {
	if (index < 0 || index > list.size()) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "out.of.bounds.index",
		 new Object[] { new Integer(index) });
	}
	list.add(index, rule);
	return index;
    }

    /**
     * Removes a rule from the list.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified index is not a valid 
     *   insertion point.
     */
    public CSSRule delete(int index) {
	if (index < 0 || index >= list.size()) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "out.of.bounds.index",
		 new Object[] { new Integer(index) });
	}
	return (CSSRule)list.remove(index);
    }

    /**
     * Appends a rule to the list.
     */
    public void append(CSSRule rule) {
	list.add(rule);
    }
}
