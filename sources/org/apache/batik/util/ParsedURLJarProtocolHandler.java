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

    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        System.out.println("In parseURL");
        String start = urlStr.substring(0, JAR.length()+1).toLowerCase();
        
        if (start.equals(JAR+":"))
            return parseURL(urlStr);

        return super.parseURL(baseURL, urlStr);
    }
}

