/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.net.URL;

/**
 * Default implementation for the <tt>ScriptSecurity</tt> interface.
 * It allows all types of scripts to be loaded, but only if they
 * come from the same server as the document they are included into.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultScriptSecurity implements ScriptSecurity {
    /**
     * Message when trying to load a script file and the Document
     * does not have a URL
     */
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL
        = "DefaultScriptSecurity.error.cannot.access.document.url";

    /**
     * Message when trying to load a script file from a server 
     * different than the one of the document.
     */
    public static final String ERROR_SCRIPT_FROM_DIFFERENT_URL
        = "DefaultScriptSecurity.error.script.from.different.url";

    /**
     * The exception is built in the constructor and thrown if 
     * not null and the checkLoadScript method is called.
     */
    protected SecurityException se;

    /**
     * Controls whether the script should be loaded or not.
     *
     * @throws SecurityException if the script should not be loaded.
     */
    public void checkLoadScript(){
        if (se != null) {
            throw se;
        }
    }

    /**
     * @param scriptType type of script, as found in the 
     *        type attribute of the &lt;script&gt; element.
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    public DefaultScriptSecurity(String scriptType,
                                 URL scriptURL,
                                 URL docURL){
        // Make sure that the archives comes from the same host
        // as the document itself
        if (docURL == null) {
            se = new SecurityException
                (Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL,
                                        new Object[]{scriptURL}));
        } else {
            String docHost = docURL.getHost();
            String scriptHost = scriptURL.getHost();
            
            if ( !(
                   (docHost == null && scriptHost == null)
                   ||
                   (docHost.equals(scriptHost))
                   ) ){
                se = new SecurityException
                    (Messages.formatMessage(ERROR_SCRIPT_FROM_DIFFERENT_URL,
                                            new Object[]{scriptURL}));
            }
        }
        
    }
}


    
