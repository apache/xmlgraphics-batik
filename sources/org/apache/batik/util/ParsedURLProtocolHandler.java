/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * Provider interface for new url protocols, used by the ParsedURL class.
 */
public interface ParsedURLProtocolHandler {
    /**
     * Returns the protocol to be handled by this class.
     * The protocol must _always_ be the part of the URL before the
     * first ':'.
     */
    public String getProtocolHandled();
    /**
     * Parse an absolute url string.
     */
    public ParsedURLData parseURL(String urlStr);
    /**
     * Parse a relative url string of this protocol.
     */
    public ParsedURLData parseURL(ParsedURL basepurl, String urlStr);
}

