/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;

/**
 * This class implements the {@link org.w3c.dom.stylesheets.MediaList}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DOMMediaList implements MediaList {
    /**
     * The list implementation
     */
    protected List list = new LinkedList();

    /**
     * Creates a new MediaList object.
     */
    public DOMMediaList() {
    }

    /**
     * Creates a new MediaList object.
     */
    public DOMMediaList(String mediaText) {
	setMediaText(mediaText);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#getMediaText()}.
     */
    public String getMediaText() {
        if (list.size() == 0) {
            return "all";
        } else {
            Iterator it = list.iterator();
            String result = (String)it.next();
            while (it.hasNext()) {
                result = it.next() + ", " + result;
            }
            return result;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#setMediaText(String)}.
     */
    public void setMediaText(String mediaText) throws DOMException {
        list.clear();
        if (!"all".equals(mediaText)) {
            StringTokenizer st = new StringTokenizer(mediaText, " ,");
            while (st.hasMoreTokens()) {
                list.add(st.nextToken());
            }
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#getLength()}.
     */
    public int getLength() {
	return list.size();
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#item(int)}.
     */
    public String item(int index) {
	if (index < 0 || index >= list.size()) {
	    return null;
	}
        return (String)list.get(index);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#deleteMedium(String)}.
     */
    public void deleteMedium(String oldMedium) throws DOMException {
        if (!list.remove(oldMedium)) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "medium.not.found",
		 new Object[] { oldMedium });
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.MediaList#appendMedium(String)}.
     */
    public void appendMedium(String newMedium) throws DOMException {
	list.remove(newMedium);
        list.add(newMedium);
    }
}
