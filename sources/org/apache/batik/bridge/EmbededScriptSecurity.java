/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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


    
