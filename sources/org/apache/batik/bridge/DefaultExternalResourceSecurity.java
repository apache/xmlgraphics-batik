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
 * Default implementation for the <tt>ExternalResourceSecurity</tt> interface.
 * It allows all types of external resources to be loaded, but only if they
 * come from the same server as the document they are referenced from.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultExternalResourceSecurity implements ExternalResourceSecurity {
    public static final String DATA_PROTOCOL = "data";
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
                
                if ( externalResourceURL == null
                     ||
                     !DATA_PROTOCOL.equals(externalResourceURL.getProtocol()) ) {
                se = new SecurityException
                    (Messages.formatMessage(ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL,
                                            new Object[]{externalResourceURL}));
                }
                
            }
        }
    }
}


    
