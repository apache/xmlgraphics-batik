/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import javax.swing.Action;

/**
 * This interface represents an object which maps actions to strings
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ActionMap {
    /**
     * Returns the action associated with the given string
     * or null on error
     * @param key the key mapped with the action to get
     * @throws MissingListenerException if the action is not found
     */
    Action getAction(String key) throws MissingListenerException;
}
