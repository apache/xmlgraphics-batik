/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

/**
 * This interface defines constants for the possible resource
 * origins.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface ResourceOrigin {
    /**
     * Any origin
     */
    static final int ANY = 1;

    /**
     * Same as document
     */
    static final int DOCUMENT = 2;

    /**
     * Embeded into the document 
     */
    static final int EMBEDED = 4;

    /**
     * No origin is ok
     */
    static final int NONE = 8;
}
