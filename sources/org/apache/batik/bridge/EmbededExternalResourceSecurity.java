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
 * This implementation of the <tt>ExternalResourceSecurity</tt> interface only
 * allows external resources embeded in the document, i.e., externalResources
 * embeded with the data protocol.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class EmbededExternalResourceSecurity implements ExternalResourceSecurity {
    public static final String DATA_PROTOCOL = "data";

    /**
     * Message when trying to load a external resource that is not embeded
     * in the document.
     */
    public static final String ERROR_EXTERNAL_RESOURCE_NOT_EMBEDED
        = "EmbededExternalResourceSecurity.error.external.esource.not.embeded";

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
            throw se;
        }
    }

    /**
     * @param externalResourceURL url for the externalResource, as defined in
     *        the externalResource's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     */
    public EmbededExternalResourceSecurity(ParsedURL externalResourceURL){
        if ( externalResourceURL == null
             ||
             !DATA_PROTOCOL.equals(externalResourceURL.getProtocol()) ) {
            se = new SecurityException
                (Messages.formatMessage(ERROR_EXTERNAL_RESOURCE_NOT_EMBEDED,
                                        new Object[]{externalResourceURL}));
            
            
        }
    }
}


    
