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

package org.apache.batik.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Protocol Handler for the 'data' protocol.
 * RFC: 2397
 * http://www.ietf.org/rfc/rfc2397.txt
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$ 
 */
public class ParsedURLDataProtocolHandler 
    extends AbstractParsedURLProtocolHandler {

    static final String DATA_PROTOCOL = "data";
    static final String BASE64 = "base64";
    static final String CHARSET = "charset";

    public ParsedURLDataProtocolHandler() {
        super(DATA_PROTOCOL);
    }

    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        // No relative form...
        return parseURL(urlStr);
    }

    public ParsedURLData parseURL(String urlStr) {
        DataParsedURLData ret = new DataParsedURLData();

        int pidx=0, idx;
        idx = urlStr.indexOf(':');
        if (idx != -1) {
            // May have a protocol spec...
            ret.protocol = urlStr.substring(pidx, idx);
            if (ret.protocol.indexOf('/') == -1)
                pidx = idx+1;
            else {
                // Got a slash in protocol probably means 
                // no protocol given, (host and port?)
                ret.protocol = null;
                pidx = 0;
            }
        }

        idx = urlStr.indexOf(',',pidx);
        if ((idx != -1) && (idx != pidx)) {
            ret.host = urlStr.substring(pidx, idx);
            pidx = idx+1;

            int aidx = ret.host.lastIndexOf(';');
            if ((aidx == -1) || (aidx==ret.host.length())) {
                ret.contentType = ret.host;
            } else {
                String enc = ret.host.substring(aidx+1);
                idx = enc.indexOf('=');
                if (idx == -1) {
                    // It is an encoding.
                    ret.contentEncoding = enc;
                    ret.contentType = ret.host.substring(0, aidx);
                } else {
                    ret.contentType = ret.host;
                }
                // if theres a charset pull it out.
                aidx = 0;
                idx = ret.contentType.indexOf(';', aidx);
                if (idx != -1) {
                    aidx = idx+1;
                    while (aidx < ret.contentType.length()) {
                        idx = ret.contentType.indexOf(';', aidx);
                        if (idx == -1) idx = ret.contentType.length();
                        String param = ret.contentType.substring(aidx, idx);
                        int eqIdx = param.indexOf('=');
                        if ((eqIdx != -1) &&
                            (CHARSET.equals(param.substring(0,eqIdx)))) 
                            ret.charset = param.substring(eqIdx+1);
                        aidx = idx+1;
                    }
                }
            }
        }
        
        if (pidx != urlStr.length()) 
            ret.path = urlStr.substring(pidx);

        return ret;
    }

    /**
     * Overrides some of the methods to support data protocol weirdness
     */
    static class DataParsedURLData extends ParsedURLData {
        String charset= null;

        public boolean complete() {
            return (path != null);
        }

        public String getPortStr() {
            String portStr ="data:";
            if (host != null) portStr += host;
            portStr += ",";
            return portStr;
        }
                
        public String toString() {
            String ret = getPortStr();
            if (path != null) ret += path;
            return ret;
        }

        /**
         * Returns the content type if available.  This is only available
         * for some protocols.
         */
        public String getContentType(String userAgent) {
            return contentType;
        }

        /**
         * Returns the content encoding if available.  This is only available
         * for some protocols.
         */
        public String getContentEncoding(String userAgent) {
            return contentEncoding;
        }

        protected InputStream openStreamInternal
            (String userAgent, Iterator mimeTypes, Iterator encodingTypes)
            throws IOException {
            if (BASE64.equals(contentEncoding)) {
                byte [] data = path.getBytes();
                stream = new ByteArrayInputStream(data);
                stream = new Base64DecodeStream(stream);
            } else {
                stream = decode(path);
            }
            return stream;
        }

        public static InputStream decode(String s) {
            int len = s.length();
            byte [] data = new byte[len];
            int j=0;
            for(int i=0; i<len; i++) {
                char c = s.charAt(i);
                switch (c) {
                default : data[j++]= (byte)c;   break;
                case '%': {
                    if (i+2 < len) {
                        i += 2;
                        byte b; 
                        char c1 = s.charAt(i-1);
                        if      (c1 >= '0' && c1 <= '9') b=(byte)(c1-'0');
                        else if (c1 >= 'a' && c1 <= 'z') b=(byte)(c1-'a'+10);
                        else if (c1 >= 'A' && c1 <= 'Z') b=(byte)(c1-'A'+10);
                        else break;
                        b*=16;

                        char c2 = s.charAt(i);
                        if      (c2 >= '0' && c2 <= '9') b+=(byte)(c2-'0');
                        else if (c2 >= 'a' && c2 <= 'z') b+=(byte)(c2-'a'+10);
                        else if (c2 >= 'A' && c2 <= 'Z') b+=(byte)(c2-'A'+10);
                        else break;
                        data[j++] = b;
                    }
                }
                break;
                }
            }
            return new ByteArrayInputStream(data, 0, j);
        }
    }
}

