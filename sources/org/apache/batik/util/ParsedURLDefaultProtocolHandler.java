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

import java.net.MalformedURLException;
import java.net.URL;


/**
 * The default protocol handler this handles the most common
 * protocols, such as 'file' 'http' 'ftp'.
 * The parsing should be general enought to support most
 * 'normal' URL formats, so in many cases 
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$ 
 */
public class ParsedURLDefaultProtocolHandler 
    extends AbstractParsedURLProtocolHandler {

    /**
     * Default constructor sets no protocol so this becomes
     * default handler.
     */
    public ParsedURLDefaultProtocolHandler() {
        super(null);
    }

    /**
     * Subclass constructor allows subclasses to provide protocol,
     * to be handled.
     */
    protected ParsedURLDefaultProtocolHandler(String protocol) {
        super(protocol);
    }

    /**
     * Subclasses can override these method to construct alternate 
     * subclasses of ParsedURLData.
     */
    protected ParsedURLData constructParsedURLData() {
        return new ParsedURLData();
    }

    /**
     * Subclasses can override these method to construct alternate 
     * subclasses of ParsedURLData.
     * @param the java.net.URL class we reference.
     */
    protected ParsedURLData constructParsedURLData(URL url) {
        return new ParsedURLData(url);
    }

    /**
     * Parses the string and returns the results of parsing in the
     * ParsedURLData object.
     * @param urlStr the string to parse as a URL.
     */
    public ParsedURLData parseURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            // System.err.println("System Parse: " + urlStr);
            return constructParsedURLData(url);
        } catch (MalformedURLException mue) {
            // Built in URL wouldn't take it...
            // mue.printStackTrace();
        }

        // new Exception("Custom Parse: " + urlStr).printStackTrace();
        // System.err.println("Custom Parse: " + urlStr);

        ParsedURLData ret = constructParsedURLData();

        if (urlStr == null) return ret;

        int pidx=0, idx;
        int len = urlStr.length();

        // Pull fragement id off first...
        idx = urlStr.indexOf('#');
        ret.ref = null;
        if (idx != -1) {
            if (idx+1 < len)
                ret.ref = urlStr.substring(idx+1);
            urlStr = urlStr.substring(0,idx);
            len = urlStr.length();
        }

        if (len == 0)
            return ret;

        // Protocol is only allowed to include -+.a-zA-Z
        // So as soon as we hit something else we know we
        // are done (if it is a ':' then we have protocol otherwise
        // we don't.
        idx = 0;
        char ch = urlStr.charAt(idx);
        while ((ch == '-') ||
               (ch == '+') ||
               (ch == '.') ||
               ((ch >= 'a') && (ch <= 'z')) ||
               ((ch >= 'A') && (ch <= 'Z'))) {
            idx++;
            if (idx == len) {
                ch=0;
                break;
            }
            ch = urlStr.charAt(idx);
        }

        if (ch == ':') {
            // Has a protocol spec...
            ret.protocol = urlStr.substring(pidx, idx).toLowerCase();
            pidx = idx+1; // Skip ':'
        }

        // See if we have host/port spec.
        idx = urlStr.indexOf('/');
        if ((idx == -1) || ((pidx+2<len)                   &&
                            (urlStr.charAt(pidx)   == '/') &&
                            (urlStr.charAt(pidx+1) == '/'))) {
            // No slashes (apache.org) or a double slash 
            // (//apache.org/....) so
            // we should have host[:port] before next slash.
            if (idx != -1)
                pidx+=2;  // Skip double slash...

            idx = urlStr.indexOf('/', pidx);  // find end of host:Port spec
            String hostPort;
            if (idx == -1)
                // Just host and port nothing following...
                hostPort = urlStr.substring(pidx);
            else
                // Path spec follows...
                hostPort = urlStr.substring(pidx, idx);

            int hidx = idx;  // Remember location of '/'

            // pull apart host and port number...
            idx = hostPort.indexOf(':');
            ret.port = -1;
            if (idx == -1) {
                // Just Host...
                if (hostPort.length() == 0)
                    ret.host = null;
                else
                    ret.host = hostPort;
            } else {
                // Host and port
                if (idx == 0) ret.host = null;
                else          ret.host = hostPort.substring(0,idx);

                if (idx+1 < hostPort.length()) {
                    String portStr = hostPort.substring(idx+1);
                    try {
                        ret.port = Integer.parseInt(portStr);
                    } catch (NumberFormatException nfe) { 
                        // bad port leave as '-1'
                    }
                }
            }
            if (((ret.host == null) || (ret.host.indexOf('.') == -1)) &&
                (ret.port == -1))
                // no '.' in a host spec??? and no port, probably
                // just a path.
                ret.host = null;
            else
                pidx = hidx;
        }

        if ((pidx == -1) || (pidx >= len)) return ret; // Nothing follows

        ret.path = urlStr.substring(pidx);
        return ret;
    }

    public static String unescapeStr(String str) {
        int idx = str.indexOf('%');
        if (idx == -1) return str; // quick out..

        int prev=0;
        StringBuffer ret = new StringBuffer();
        while (idx != -1) {
            if (idx != prev)
                ret.append(str.substring(prev, idx));

            if (idx+2 >= str.length()) break;
            prev = idx+3;
            idx = str.indexOf('%', prev);

            int ch1 = charToHex(str.charAt(idx+1));
            int ch2 = charToHex(str.charAt(idx+1));
            if ((ch1 == -1) || (ch2==-1)) continue;
            ret.append((char)(ch1<<4 | ch2));
        }

        return ret.toString();
    }

    public static int charToHex(int ch) {
        switch(ch) {
        case '0': case '1': case '2':  case '3':  case '4': 
        case '5': case '6': case '7':  case '8':  case '9': 
            return ch-'0';
        case 'a': case 'A': return 10;
        case 'b': case 'B': return 11;
        case 'c': case 'C': return 12;
        case 'd': case 'D': return 13;
        case 'e': case 'E': return 14;
        case 'f': case 'F': return 15;
        default:            return -1;
        }
    }

    /**
     * Parses the string as a sub URL of baseURL, and returns the
     * results of parsing in the ParsedURLData object.
     * @param baseURL the base url for parsing.
     * @param urlStr the string to parse as a URL.  
     */
    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        // Reference to same document (including fragment, and query).
        if (urlStr.length() == 0) 
            return baseURL.data;

        // System.err.println("Base: " + baseURL + "\n" +
        //                    "Sub:  " + urlStr);

        int idx = 0, len = urlStr.length();
        if (len == 0) return baseURL.data;

        // Protocol is only allowed to include -+.a-zA-Z
        // So as soon as we hit something else we know we
        // are done (if it is a ':' then we have protocol otherwise
        // we don't.
        char ch = urlStr.charAt(idx);
        while ((ch == '-') ||
               (ch == '+') ||
               (ch == '.') ||
               ((ch >= 'a') && (ch <= 'z')) ||
               ((ch >= 'A') && (ch <= 'Z'))) {
            idx++;
            if (idx == len) {
                ch=0;
                break;
            }
            ch = urlStr.charAt(idx);
        }
        String protocol = null;
        if (ch == ':') {
            // Has a protocol spec...
            protocol = urlStr.substring(0, idx).toLowerCase();
        }

        if (protocol != null) {
            // Temporary if we have a protocol then assume absolute
            // URL.  Technically this is the correct handling but much
            // software supports relative URLs with a protocol that
            // matches the base URL's protocol.
            // if (true)
            //     return parseURL(urlStr);
            if (!protocol.equals(baseURL.getProtocol()))
                // Different protocols, assume absolute URL ignore base...
                return parseURL(urlStr);

            // Same protocols, if char after ':' is a '/' then it's
            // still absolute...
            idx++;
            if (idx == urlStr.length()) 
                // Just a Protocol???
                return parseURL(urlStr);

            if (urlStr.charAt(idx) == '/') 
                // Absolute URL...
                return parseURL(urlStr);

            // Still relative just drop the protocol (we will pick it
            // back up from the baseURL later...).
            urlStr = urlStr.substring(idx);
        }

        if (urlStr.startsWith("/")) {
            if ((urlStr.length() > 1) &&
                (urlStr.charAt(1) == '/')) {
                // Relative but only uses protocol from base
                return parseURL(baseURL.getProtocol() + ":" + urlStr);
            }
            // Relative 'absolute' path, uses protocol and authority
            // (host) from base
            return parseURL(baseURL.getPortStr() + urlStr);
        }

        if (urlStr.startsWith("#")) {
            String base = baseURL.getPortStr();
            if (baseURL.getPath()    != null) base += baseURL.getPath();
            return parseURL(base + urlStr);
        }

        String path = baseURL.getPath();
        // No path? well we will treat this as being relative to it's self.
        if (path == null) path = "";
        idx = path.lastIndexOf('/');
        if (idx == -1) 
            // baseURL is just a filename (in current dir) so use current dir
            // as base of new URL.
            path = "";
        else
            path = path.substring(0,idx+1);
        
        // System.err.println("Base Path: " + path);
        // System.err.println("Base PortStr: " + baseURL.getPortStr());
        return parseURL(baseURL.getPortStr() + path + urlStr);
    }
}

