/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.util.ParsedURL;

/**
 * Default implementation for the <tt>ExternalResourceSecurity</tt> interface.
 * It allows all types of external resources to be loaded, but only if they
 * come from the same server as the document they are referenced from.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultExternalResourceSecurity implements ExternalResourceSecurity {
    /**
     * Message when trying to load a external resource file and the Document
     * does not have a URL
     */
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL
        = "DefaultExternalResourceSecurity.error.cannot.access.document.url";

    /**
     * Message when trying to load a externalResource file from a server 
     * different than the one of the document.
     */
    public static final String ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL
        = "DefaultExternalResourceSecurity.error.external.resource.from.different.url";

    /**
     * The exception is built in the constructor and thrown if 
     * not null and the checkLoadExternalResource method is called.
     */
    protected SecurityException se;

    /**
     * Controls whether the externalResource should be loaded or not.
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
     * @param externalResourceURL url for the externalResource, as defined in
     *        the externalResource's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        externalResource was found.
     */
    public DefaultExternalResourceSecurity(ParsedURL externalResourceURL,
                                           ParsedURL docURL){
        // Make sure that the archives comes from the same host
        // as the document itself
        if (docURL == null) {
            se = new SecurityException
                (Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL,
                                        new Object[]{externalResourceURL}));
        } else {
            String docHost    = docURL.getHost();
            String externalResourceHost = externalResourceURL.getHost();
            
            if ((docHost != externalResourceHost) &&
                ((docHost == null) || (!docHost.equals(externalResourceHost)))){
                se = new SecurityException
                    (Messages.formatMessage(ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL,
                                            new Object[]{externalResourceURL}));
            }
        }
        
    }
}


    
