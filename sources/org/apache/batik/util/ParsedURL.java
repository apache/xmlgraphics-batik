/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import org.apache.batik.util.Service;

/**
 * This class is used as a replacement for java.net.URL.  This is done
 * for several reasons.  First unlike java.net.URL this class will
 * accept and parse as much of a URL as possible, without throwing a
 * MalformedURL exception.  This  makes it extreamly useful for simply
 * parsing a URL string (hence it's name).
 *
 * Second it allows for extension of the protocols supported by the
 * URL parser.  Batik uses this to support the 'Data' protocol.
 * 
 * Third by default it checks the streams that it opens to see if they
 * are GZIP compressed, if so it automatically uncompresses them
 * (avoiding opening the stream twice in the processes).
 *
 * It is worth noting that most real work is defered to the
 * ParsedURLData class to which most methods are forwarded.  This is
 * done because it allows a constructor interface to ParsedURL (mostly
 * for compatability with core URL), in spite of the fact that the
 * real implemenation uses the protocol handlers as factories for
 * protocol specific instances of the ParsedURLData class.
 */
public class ParsedURL {

    /** 
     * The data class we defer most things to.
     */
    ParsedURLData data;

    /**
     * This maps between protocol names and ParsedURLProtocolHandler instances.
     */
    private static Map handlersMap = null; 

    /**
     * The default protocol handler.  This handler is used when
     * other handlers fail or no match for a protocol can be
     * found.
     */
    private static ParsedURLProtocolHandler defaultHandler 
        = new ParsedURLDefaultProtocolHandler();

    /**
     * Returns the shared instance of HandlersMap.  This method is
     * also responsible for initializing the handler map if this is
     * the first time it has been requested since the class was
     * loaded.
     */
    private static synchronized Map getHandlersMap() { 
        if (handlersMap != null) return handlersMap;

        handlersMap = new HashMap();
        registerHandler(new ParsedURLDataProtocolHandler());

        Iterator iter = Service.providers(ParsedURLProtocolHandler.class);
        while (iter.hasNext()) {
            ParsedURLProtocolHandler handler;
            handler = (ParsedURLProtocolHandler)iter.next();

            // System.out.println("Handler: " + handler);
            registerHandler(handler);
        }


        return handlersMap;
        
    }

    /**
     *  Returns the handler for a particular protocol.  If protocol is
     * <tt>null</tt> or no match is found in the handlers map it
     * returns the default protocol handler.  
     * @param protocol The protocol to get a handler for.
     */
    public static synchronized ParsedURLProtocolHandler getHandler
        (String protocol) {
        if (protocol == null)
            return defaultHandler;

        Map handlers = getHandlersMap();
        ParsedURLProtocolHandler ret;
        ret = (ParsedURLProtocolHandler)handlers.get(protocol);
        if (ret == null)
            ret = defaultHandler;
        return ret;
    }

    /**
     * Registers a Protocol handler by adding it to the handlers map.
     * If the given protocol handler returns <tt>null</tt> as it's
     * supported protocol then it is registered as the default
     * protocol handler.
     * @param handler the new Protocol Handler to register 
     */
    public static synchronized void registerHandler
        (ParsedURLProtocolHandler handler) {
        if (handler.getProtocolHandled() == null) {
            defaultHandler = handler;
            return;
        }

        Map handlers = getHandlersMap();
        handlers.put(handler.getProtocolHandled(), handler);
    }

    /**
     * This is a utility function others can call that checks if
     * is is a GZIP stream if so it returns a GZIPInputStream that
     * will decode the contents, otherwise it returns (or a
     * buffered version of is) untouched.
     * @param is Stream that may potentially be a GZIP stream.
     */
    public static InputStream checkGZIP(InputStream is) 
        throws IOException {
        return ParsedURLData.checkGZIP(is);
    }

    /**
     * Construct a ParsedURL from the given url string.
     * @param urlStr The string to try and parse as a URL 
     */
    public ParsedURL(String urlStr) {
        data = parseURL(urlStr);
    }

    /**
     * Construct a ParsedURL from the given java.net.URL instance.
     * This is useful if you already have a valid java.net.URL
     * instance.  This bypasses most of the parsing and hence is
     * quicker and less prone to reinterpretation than converting the
     * URL to a string before construction.
     * @param url The URL to "mimic".  
     */
    public ParsedURL(URL url) {
        data = new ParsedURLData(url);
    }

    /**
     * Construct a sub URL from two strings.
     * @param baseStr The 'parent' URL.  Should be complete.
     * @param urlStr The 'sub' URL may be complete or partial.
     *               the missing pieces will be taken from the baseStr.
     */
    public ParsedURL(String baseStr, String urlStr) {
        if (baseStr != null)
            data = parseURL(baseStr, urlStr);
        else
            data = parseURL(urlStr);
    }

    /**
     * Construct a sub URL from a base URL and a string for the sub url.
     * @param baseURL The 'parent' URL.
     * @param urlStr The 'sub' URL may be complete or partial.
     *               the missing pieces will be taken from the baseURL.
     */
    public ParsedURL(URL baseURL, String urlStr) {
        if (baseURL != null)
            data = parseURL(new ParsedURL(baseURL), urlStr);
        else
            data = parseURL(urlStr);
    }

    /**
     * Construct a sub URL from a base ParsedURL and a string for the sub url.
     * @param baseURL The 'parent' URL.
     * @param urlStr The 'sub' URL may be complete or partial.
     *               the missing pieces will be taken from the baseURL.
     */
    public ParsedURL(ParsedURL baseURL, String urlStr) {
        if (baseURL != null)
            data = parseURL(baseURL, urlStr);
        else
            data = parseURL(urlStr);
    }

    /**
     * Return a string rep of the URL (can be passed back into the 
     * constructor if desired).
     */
    public String toString() {
        return data.toString();
    }

    /**
     * Implement Object.equals.
     * Relies heavily on the contained ParsedURLData's implementation
     * of equals.
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof ParsedURL)) 
            return false;
        ParsedURL purl = (ParsedURL)obj;
        return data.equals(purl.data);
    }

    /**
     * Implement Object.hashCode.
     * Relies on the contained ParsedURLData's implementation
     * of hashCode.
     */
    public int hashCode() {
        return data.hashCode();
    }
        
    /**
     * Returns true if the URL looks well formed and complete.
     * This does not garuntee that the stream can be opened but
     * is a good indication that things aren't totally messed up.
     */
    public boolean complete() {
        return data.complete();
    }

    /**
     * Returns the protocol for this URL.
     * The protocol is everything upto the first ':'.
     */
    public String getProtocol() { 
        if (data.protocol == null) return null;
        return new String(data.protocol); 
    }
    
    /**
     * Returns the host for this URL, if any, <tt>null</tt> if there isn't
     * one or it doesn't make sense for the protocol.
     */
    public String getHost()     { 
        if (data.host == null) return null;
        return new String(data.host); 
    }

    /**
     * Returns the port on the host to connect to, if it was specified
     * in the url that was parsed, otherwise returns -1.
     */
    public int    getPort()     { return data.port; }

    /**
     * Returns the path for this URL, if any (where appropriate for
     * the protocol this also includes the file, not just directory.) 
     */
    public String getPath()     { 
        if (data.path == null) return null;
        return new String(data.path); 
    }

    /**
     * Returns the 'fragment' reference in the URL.
     */
    public String getRef()      { 
        if (data.ref == null) return null;
        return new String(data.ref); 
    }
    

    /**
     * Returns the URL up to and include the port number on
     * the host.  Does not include the path or fragment pieces.
     */
    public String getPortStr() {
        return data.getPortStr();
    }

    /**
     * Attempt to open the stream checking for common compression
     * types, and automatically decompressing them if found.  
     */
    public InputStream openStream() throws IOException {
        return data.openStream();
    }

    /**
     * Attempt to open the stream.
     */
    public InputStream openStreamRaw() throws IOException {
        return data.openStreamRaw();
    }

    /**
     * Parse out the protocol from a url string. Used internally to
     * select the proper handler, all other parsing is done by
     * the selected protocol handler.
     */
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

    
    /**
     * Factory method to construct an appropriate subclass of  ParsedURLData
     * @param urlStr the string to parse.
     */
    public static ParsedURLData parseURL(String urlStr) {
        ParsedURLProtocolHandler handler = getHandler(getProtocol(urlStr));
        return handler.parseURL(urlStr);        
    }

    /**
     * Factory method to construct an appropriate subclass of  ParsedURLData,
     * for a sub url.
     * @param baseStr The base URL string to parse.
     * @param urlStr the sub URL string to parse.
     */
    public static ParsedURLData parseURL(String baseStr, String urlStr) {
        String protocol = getProtocol(urlStr);
        if (protocol != null)
            // Protocol given, ignore base...
            return parseURL(urlStr);

        ParsedURL purl = new ParsedURL(baseStr);
        return parseURL(purl, urlStr);
    }

    /**
     * Factory method to construct an appropriate subclass of  ParsedURLData,
     * for a sub url.
     * @param baseURL The base ParsedURL to parse.
     * @param urlStr the sub URL string to parse.
     */
    public static ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        String protocol = getProtocol(urlStr);
        if (protocol != null)
            // Protocol given, ignore base...
            return parseURL(urlStr);

        ParsedURLProtocolHandler handler = getHandler(protocol);
        return handler.parseURL(baseURL, urlStr);        
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
