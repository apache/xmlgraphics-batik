/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * An interface that allows UserAgents to describe the security 
 * constraints desired for external resources.
 *
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface ExternalResourceSecurity {
    /**
     * Controls whether the external resource should be loaded or not.
     *
     * @throws SecurityException if the resource should not be loaded.
     */
    void checkLoadExternalResource();
}


    
