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
 * This implementation of the <tt>ScriptSecurity</tt> interface only
 * allows scripts embeded in the document, i.e., scripts whith either
 * the same URL as the document (as for event attributes) or scripts
 * embeded with the data protocol.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class EmbededScriptSecurity implements ScriptSecurity {
    public static final String DATA_PROTOCOL = "data";

    /**
     * Message when trying to load a script file and the Document
     * does not have a URL
     */
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL
        = "DefaultScriptSecurity.error.cannot.access.document.url";

    /**
     * Message when trying to load a script that is not embeded
     * in the document.
     */
    public static final String ERROR_SCRIPT_NOT_EMBEDED
        = "EmbededScriptSecurity.error.script.not.embeded";

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
    public EmbededScriptSecurity(String scriptType,
                                 ParsedURL scriptURL,
                                 ParsedURL docURL){
        // Make sure that the archives comes from the same host
        // as the document itself
        if (docURL == null) {
            se = new SecurityException
                (Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL,
                                        new Object[]{scriptURL}));
        } else {
            if ( !docURL.equals(scriptURL)
                 &&
                 (scriptURL == null
                  ||
                  !DATA_PROTOCOL.equals(scriptURL.getProtocol()) )) {
                se = new SecurityException
                    (Messages.formatMessage(ERROR_SCRIPT_NOT_EMBEDED,
                                            new Object[]{scriptURL}));
            }
        }
    }
}


    
