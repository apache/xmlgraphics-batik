/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * Very simple abstract base class for ParsedURLProtocolHandlers.
 * Just handles the 'what protocol part'.
 */
public abstract class AbstractParsedURLProtocolHandler 
 implements ParsedURLProtocolHandler {

    protected String protocol;

    /**
     * Constrcut a ProtocolHandler for <tt>protocol</tt>
     */
    public AbstractParsedURLProtocolHandler(String protocol) {
        this.protocol = protocol;
    }


    /**
     * Returns the protocol to be handled by this class.
     * The protocol must _always_ be the part of the URL before the
     * first ':'.
     */
    public String getProtocolHandled() {
        return protocol;
    }
}

