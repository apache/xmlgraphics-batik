/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.MalformedURLException;


/**
 * The default protocol handler this handles the most common
 * protocols, such as 'file' 'http' 'ftp'.
 * The parsing should be general enought to support most
 * 'normal' URL formats, so in many cases 
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
            return constructParsedURLData(url);
        } catch (MalformedURLException mue) {
            // Built in URL wouldn't take it...
        }

        ParsedURLData ret = constructParsedURLData();

        int pidx=0, idx;
        int len = urlStr.length();

        idx = urlStr.indexOf(':');
        if (idx != -1) {
            // May have a protocol spec...
            ret.protocol = urlStr.substring(pidx, idx).toLowerCase();
            if (ret.protocol.indexOf('/') == -1)
                pidx = idx+1;
            else {
                // Got a slash in protocol probably means 
                // no protocol given, (host and port?)
                ret.protocol = null;
                pidx = 0;
            }
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

            pidx = idx;  // Remember location of '/'

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
                if (idx == 0) 
                    ret.host = null;
                else
                    ret.host = hostPort.substring(0,idx);

                if (idx+1 < hostPort.length()) {
                    String portStr = hostPort.substring(idx+1);
                    try {
                        ret.port = Integer.parseInt(portStr);
                    } catch (NumberFormatException nfe) { 
                        // bad port leave as '-1'
                    }
                }
            }
        }

        if ((pidx == -1) || (pidx >= len)) return ret; // Nothing follows

        String pathRef = urlStr.substring(pidx);
        idx = pathRef.indexOf('#');
        ret.ref = null;
        if (idx == -1) {
            // No ref (fragment) in URL
            ret.path = pathRef;
        } else {
            ret.path = pathRef.substring(0,idx);
            if (idx+1 < pathRef.length())
                ret.ref = pathRef.substring(idx+1);
        }
        return ret;
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

        int idx = urlStr.indexOf(':');
        if (idx != -1) {
            String protocol = urlStr.substring(0,idx).toLowerCase();
            
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

        if (urlStr.startsWith("#"))
            return parseURL(baseURL.getPortStr() + 
                            baseURL.getPath() + urlStr);

        String path = baseURL.getPath();
        if (path == null) path = "/";
        idx = path.lastIndexOf('/');
        if (idx == -1) 
            path = "/";
        else
            path = path.substring(0,idx+1);
        
        return parseURL(baseURL.getPortStr() + path + urlStr);
    }
}

