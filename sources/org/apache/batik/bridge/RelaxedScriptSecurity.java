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
 * This implementation for the <tt>ScriptSecurity</tt> interface.
 * allows the script to be loaded and does not impose constraints
 * on the urls.
 * Note that this only means there is no check on the script's
 * origin, not that it will run without security checks. 
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class RelaxedScriptSecurity  implements ScriptSecurity {
    /**
     * Controls whether the script should be loaded or not.
     *
     * @throws SecurityException if the script should not be loaded.
     */
    public void checkLoadScript(){
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
    public RelaxedScriptSecurity(String scriptType,
                                 URL scriptURL,
                                 URL docURL){
    }
}


    
