/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Protocol Handler for the 'jar' protocol.
 * This appears to have the format:
 * jar:<URL for jar file>!<path in jar file>
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$ 
 */
public class ParsedURLJarProtocolHandler 
    extends ParsedURLDefaultProtocolHandler {

    public static final String JAR = "jar";

    public ParsedURLJarProtocolHandler() {
        super(JAR);
    }


    // We mostly use the base class parse methods (that leverage
    // java.net.URL.  But we take care to ignore the baseURL if urlStr
    // is an absolute URL.
    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        String start = urlStr.substring(0, JAR.length()+1).toLowerCase();
        
        // urlStr is absolute...
        if (start.equals(JAR+":"))
            return parseURL(urlStr);

        // It's relative so base it off baseURL.
        try {
            URL context = new URL(baseURL.toString());
            URL url     = new URL(context, urlStr);
            return constructParsedURLData(url);
        } catch (MalformedURLException mue) {
            return super.parseURL(baseURL, urlStr);
        }
    }
}

