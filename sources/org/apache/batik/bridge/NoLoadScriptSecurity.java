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
 * This implementation for the <tt>ScriptSecurity</tt> interface
 * does not allow scripts to be loaded.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class NoLoadScriptSecurity implements ScriptSecurity {
    /**
     * Message when trying to load a script file and the Document
     * does not have a URL
     */
    public static final String ERROR_NO_SCRIPT_OF_TYPE_ALLOWED
        = "NoLoadScriptSecurity.error.no.script.of.type.allowed";

    /**
     * The exception is built in the constructor and thrown if 
     * the checkLoadScript method is called.
     */
    protected SecurityException se;

    /**
     * Controls whether the script should be loaded or not.
     *
     * @throws SecurityException if the script should not be loaded.
     */
    public void checkLoadScript(){
        throw se;
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
    public NoLoadScriptSecurity(String scriptType){

        se = new SecurityException
            (Messages.formatMessage(ERROR_NO_SCRIPT_OF_TYPE_ALLOWED,
                                    new Object[]{scriptType}));
    }
}


    
