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
 * Protocol Handler for the 'data' protocol.
 */
public class ParsedURLDataProtocolHandler 
    extends AbstractParsedURLProtocolHandler {

    public ParsedURLDataProtocolHandler() {
        super("data");
    }

    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        // No relative form...
        return parseURL(urlStr);
    }

    public ParsedURLData parseURL(String urlStr) {
        ParsedURLData ret = new DataParsedURLData();

        int pidx=0, idx;
        int len = urlStr.length();

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
        if (idx != -1) {
            ret.host = urlStr.substring(pidx, idx);
            pidx = idx+1;
        }
        if (pidx != urlStr.length()) 
            ret.path = urlStr.substring(pidx);

        return ret;
    }

    /**
     * Overrides some of the methods to support data protocol weirdness
     */
    static class DataParsedURLData extends ParsedURLData {

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

        protected InputStream openStreamInternal
            (String userAgent, Iterator mimeTypes, Iterator encodingTypes)
            throws IOException {
            byte [] data = path.getBytes();
            InputStream is = new ByteArrayInputStream(data);
            return new Base64DecodeStream(is);
        }
    }

}

