/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

/**
 * The built in error codes.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface ErrorConstants {

    /**
     * The error messages bundle class name.
     */
    public final static String RESOURCES =
        "org.apache.batik.ext.awt.image.spi.resources.Messages";


    /**
     * The error code when a stream is unreadable (corrupt or unsupported).
     */
    public static final String ERR_STREAM_UNREADABLE
        = "stream.unreadable";

    /**
     * The error code when a url of a particular format is unreadable 
     * (corrupt).
     * {0} = the format that couldn't be read.
     */
    public static final String ERR_STREAM_FORMAT_UNREADABLE
        = "stream.format.unreadable";

    /**
     * The error code when a url is unreadable (corrupt or unsupported).
     * {0} = the ParsedURL that couldn't be read.
     */
    public static final String ERR_URL_UNREADABLE
        = "url.unreadable";


    /**
     * The error code when a url of a particular format is unreadable 
     * (corrupt).
     * {0} = the format that couldn't be read.
     * {1} = the ParsedURL for file.
     */
    public static final String ERR_URL_FORMAT_UNREADABLE
        = "url.format.unreadable";

}
