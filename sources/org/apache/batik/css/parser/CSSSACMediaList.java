/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.css.sac.SACMediaList;

/**
 * This class implements the {@link SACMediaList} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSSACMediaList implements SACMediaList {
    /**
     * The list.
     */
    protected List list = new ArrayList(3);

    /**
     * <b>SAC</b>: Returns the length of this selector list
     */    
    public int getLength() {
        return list.size();
    }

    /**
     * <b>SAC</b>: Returns the selector at the specified index, or
     * <code>null</code> if this is not a valid index.  
     */
    public String item(int index) {
        return (String)list.get(index);
    }

    /**
     * Appends an item to the list.
     */
    public void append(String item) {
        list.add(item);
    }
}
