/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.util.Map;
import java.util.HashMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.MalformedURLException;

public class ParsedURL {

    static class URLData {
        public String protocol = null;
        public String host     = null;
        public int    port     = -1;
        public String path     = null;
        public String ref      = null;

        protected URL buildURL() throws MalformedURLException {
            String file = "";
            if (path != null) 
                file = path;
            if (ref != null)
                file += "#" + ref;

            if (port == -1)
                return new URL(protocol, host, file);

            return new URL(protocol, host, port, file);
        }
        
        public boolean complete() {
            try {
                URL url = buildURL();
            } catch (MalformedURLException mue) {
                return false;
            }
            return true;
        }

        public InputStream openStream() throws IOException {
            URL url = null;
            try {
                url = buildURL();
            } catch (MalformedURLException mue) {
                throw new IOException
                    ("Unable to make sense of URL for connection");
            }

            if (url != null)
                return url.openStream();

            return null;
        }

        public String getPortStr() {
            String portStr ="";
            if (protocol != null)
                portStr += protocol + ":";

            if ((host != null) || (port != -1)) {
                portStr += "//";
                if (host != null) portStr += host;
                if (port != -1)   portStr += ":" + port;
            }

            return portStr;
        }

        public String toString() {
            String ret = getPortStr();
            if (path != null)
                ret += path;

            if (ref != null) 
                ret += "#" + ref;

            return ret;
        }
    };

    URLData data;

    static Map            parsers       = new HashMap();
    static ProtocolParser defaultParser = new DefaultProtocolParser();

    static void registerParser(ProtocolParser pp) {
        if (pp.getProtocolHandled() == null) {
            defaultParser = pp;
            return;
        }

        parsers.put(pp.getProtocolHandled(), pp);
    }

    static {
        registerParser(new DataProtocolParser());
    }

    public ParsedURL(String urlStr) {
        data = parseURL(urlStr);
    }

    public ParsedURL(String baseStr, String urlStr) {
        if (baseStr != null)
            data = parseURL(baseStr, urlStr);
        else
            data = parseURL(urlStr);
    }

    public ParsedURL(ParsedURL baseURL, String urlStr) {
        if (baseURL != null)
            data = parseURL(baseURL, urlStr);
        else
            data = parseURL(urlStr);
    }

    public boolean complete() {
        return data.complete();
    }

    public String getProtocol() { 
        if (data.protocol == null) return null;
        return new String(data.protocol); 
    }
    public String getHost()     { 
        if (data.host == null) return null;
        return new String(data.host); 
    }
    public String getPath()     { 
        if (data.path == null) return null;
        return new String(data.path); 
    }
    public String getRef()      { 
        if (data.ref == null) return null;
        return new String(data.ref); 
    }
    public int    getPort()     { return data.port; }


    public String getPortStr() {
        return data.getPortStr();
    }

    public String toString() {
        return data.toString();
    }

    public InputStream openStream() throws IOException {
        return data.openStream();
    }

    protected static String getProtocol(String urlStr) {
        int idx = urlStr.indexOf(':');
        if (idx == -1) 
            return null;

        // May have a protocol spec...
        String protocol = urlStr.substring(0, idx);
        if (protocol.indexOf('/') != -1)
            // Got a slash in protocol probably means 
            // no protocol given.
            return null;

        return protocol;
    }


    public static URLData parseURL(String urlStr) {
        String protocol = getProtocol(urlStr);
        if (protocol == null)
            // no protocol given, use default parser.
            return defaultParser.parseURL(urlStr);

        Object o = parsers.get(protocol);
        if (o==null)
            return defaultParser.parseURL(urlStr);

        return ((ProtocolParser)o).parseURL(urlStr);
    }

    public static URLData parseURL(String baseStr, String urlStr) {
        String protocol = getProtocol(urlStr);
        if (protocol != null)
            // Protocol given, ignore base...
            return parseURL(urlStr);

        ParsedURL purl = new ParsedURL(baseStr);
        return parseURL(purl, urlStr);
    }

    public static URLData parseURL(ParsedURL baseURL, String urlStr) {
        String protocol = getProtocol(urlStr);
        if (protocol != null)
            // Protocol given, ignore base...
            return parseURL(urlStr);

        Object o = parsers.get(protocol);
        if (o==null)
            return defaultParser.parseURL(baseURL, urlStr);

        return ((ProtocolParser)o).parseURL(baseURL, urlStr);        
    }



    static public abstract class ProtocolParser {
        String protocol;
        public ProtocolParser(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocolHandled() {
            return protocol;
        }

        abstract public URLData parseURL(String urlStr);
        abstract public URLData parseURL(ParsedURL basepurl, String urlStr);
    }

    static public class DefaultProtocolParser extends ProtocolParser {

        public DefaultProtocolParser() {
            super(null);
        }

        public URLData parseURL(String urlStr) {
            URLData ret = new URLData();

            try {
                URL url = new URL(urlStr);
                ret.protocol = url.getProtocol();
                if ((ret.protocol != null) && (ret.protocol.length() == 0)) 
                    ret.protocol = null;

                ret.host = url.getHost();
                if ((ret.host != null) && (ret.host.length() == 0)) 
                    ret.host = null;

                ret.port     = url.getPort();

                ret.path     = url.getPath();
                if ((ret.path != null) && (ret.path.length() == 0)) 
                    ret.path = null;

                ret.ref      = url.getRef();
                if ((ret.ref != null) && (ret.ref.length() == 0))  
                    ret.ref = null;
            
                return ret;
            } catch (MalformedURLException mue) {
                // Built in URL wouldn't take it...
            }

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

        public URLData parseURL(ParsedURL baseURL, String urlStr) {
            int idx = urlStr.indexOf(':');
            if (idx != -1)
                // Absolute URL ignore base...
                return parseURL(urlStr);

            if (urlStr.startsWith("/"))
                // Absolute path.
                return parseURL(baseURL.getPortStr() + urlStr);

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

    static public class DataProtocolParser extends ProtocolParser {

        public DataProtocolParser() {
            super("data");
        }

        static class DataURLData extends URLData {

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

            public InputStream openStream() throws IOException {
                byte [] data = path.getBytes();
                InputStream is = new ByteArrayInputStream(data);
                return new Base64DecodeStream(is);
            }
        }

        public URLData parseURL(String urlStr) {
            URLData ret = new DataURLData();

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

        public URLData parseURL(ParsedURL baseURL, String urlStr) {
            // No relative form...
            return parseURL(urlStr);
        }
    }    

    static public void main1(String []args) {
        for (int i=0; i<args.length; i++) {
            ParsedURL purl = new ParsedURL(args[i]);

            System.out.println("URL:        \"" + args[i] + "\"");
            System.out.println("  Str:      \"" + purl + "\"");
            System.out.println("  Protocol: \"" + purl.getProtocol()+"\"");
            System.out.println("  Host:     \"" + purl.getHost()    +"\"");
            System.out.println("  Port:     \"" + purl.getPort()    +"\"");
            System.out.println("  Path:     \"" + purl.getPath()    +"\"");
            System.out.println("  Ref:      \"" + purl.getRef()     +"\"");
        }
    }

    // This is a little test harness that takes the arguments and uses
    // them to construct 'relative' URLS (so it always uses the
    // previous url to construct the next url).
    //
    // Example input:
    // "xml.apache.org ~deweese 
    // file:///home/deweese/.cshrc 
    // http://xml.apache.org:8080/~deweese/index.html#1234asdb 
    // file:/home/deweese/tools/src/foo.cc 
    // fooz:/home/deweese/tools/src/foo.cc 
    // fooz:///home/deweese/tools/src/foo.cc 
    // fooz://www.www.www:1234/home/deweese/src/foo.cc#abcd 
    //      xzy.html#lalsjdf 
    // /xyz.svg#alskd

    static public void main(String []args) {
        ParsedURL purl = null;
        for (int i=0; i<args.length; i++) {
            purl = new ParsedURL(purl, args[i]);

            System.out.println("URL:        \"" + args[i] + "\"");
            System.out.println("  Str:      \"" + purl + "\"");
            System.out.println("  Protocol: \"" + purl.getProtocol()+"\"");
            System.out.println("  Host:     \"" + purl.getHost()    +"\"");
            System.out.println("  Port:     \"" + purl.getPort()    +"\"");
            System.out.println("  Path:     \"" + purl.getPath()    +"\"");
            System.out.println("  Ref:      \"" + purl.getRef()     +"\"");
        }
    }
}
