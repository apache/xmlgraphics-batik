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

package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;

/**
 * This type of Image tag registy entry is used for 'odd' URL types.
 * Ussually this means that the URL uses a non-standard protocol.  In
 * these cases you should be aware that in order for the construction
 * of the URL object to succeed you must register a @see
 * URLStreamHandler using one of the methods listed in 
 * @see java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String).
 *  */
public interface URLRegistryEntry extends RegistryEntry {
    /**
     * Check if the URL references an image that can be
     * handled by this format handler.  Generally speaking
     * this should not open the URL.  The decision should
     * be based on the structure of the URL (such as
     * the protocol in use).<p>
     *
     * If you don't care about the structure of the URL and only about
     * the contents of the URL you should register as a
     * StreamRegistryEntry, so the URL "connection" will be made
     * only once.
     *
     * @param url The URL to inspect.
     */
    public boolean isCompatibleURL(ParsedURL url);

    /**
     * Decode the URL into a RenderableImage, here you should feel
     * free to open the URL yourself.<P>
     *
     * This should only return a broken link image if the image
     * is clearly of this format, but is unreadable for some reason.
     * otherwise return null.<p>
     *
     * If all entries refuse the url or return null then the registry
     * will automatically return a broken link image for you.
     *
     * @param url The url that reference the image.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleURL(ParsedURL url, boolean needRawData);
}
