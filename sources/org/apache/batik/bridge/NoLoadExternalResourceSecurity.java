/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;


/**
 * This implementation does not allow any external resources to be 
 * referenced from an SVG document.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class NoLoadExternalResourceSecurity implements ExternalResourceSecurity {
    /**
     * Message when trying to load an external resource
     */
    public static final String ERROR_NO_EXTERNAL_RESOURCE_ALLOWED
        = "NoLoadExternalResourceSecurity.error.no.external.resource.allowed";

    /**
     * The exception is built in the constructor and thrown if 
     * not null and the checkLoadExternalResource method is called.
     */
    protected SecurityException se;

    /**
     * Controls whether the external resource should be loaded or not.
     *
     * @throws SecurityException if the externalResource should not be loaded.
     */
    public void checkLoadExternalResource(){
        if (se != null) {
            se.fillInStackTrace();
            throw se;
        }
    }

    /**
     */
    public NoLoadExternalResourceSecurity(){
        se = new SecurityException
            (Messages.formatMessage(ERROR_NO_EXTERNAL_RESOURCE_ALLOWED,
                                    null));
        
    }
}


    
