/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.util.List;

/**
 * The base interface for all image tag registry entries.  To be
 * useful you probably need to implement on of the flavors of registry
 * entries (such as @see StreamRegistryEntry or
 * @see URLRegistryEntry).
 */
public interface RegistryEntry {

    /**
     * Return a List of the common extensions for this file format.
     * The first entry in the list may be used as the default
     * extension for writing files in this format (when we add support
     * for writing that is).  This may also be used to build a
     * selection expression for finding files of this type.  
     */
    public List   getStandardExtensions();

    /**
     * Returns the name of the format. For example "JPEG", "PNG", ...
     */
    public String getFormatName();

    /**
     * Returns a search priority for this entry.  For most formats
     * this is not important, but in some cases it is important that
     * some entries occure before or after others.
     */
    public float  getPriority();
}
