/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * The error code.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public interface ErrorConstants {

    /**
     * The error code when a required attribute is missing.
     * {0} = the name of the attribute
     */
    public static final String ERR_ATTRIBUTE_MISSING
        = "attribute.missing";

    /**
     * The error code when an attribute has a syntax error.
     * {0} = the name of the attribute
     * {1} = the wrong value
     */
    public static final String ERR_ATTRIBUTE_VALUE_MALFORMED
        = "attribute.malformed";

    /**
     * The error code when a length, which must be positive, is negative.
     * {0} = the name of the attribute
     */
    public static final String ERR_LENGTH_NEGATIVE
        = "length.negative";

    /**
     * The error code when a CSS length is negative.
     * {0} = property
     */
    public static final String ERR_CSS_LENGTH_NEGATIVE
        = "css.length.negative";

    /**
     * The error code when a URI specified in a CSS property
     * referenced a bad element.
     * {0} = the uri
     */
    public static final String ERR_CSS_URI_BAD_TARGET
        = "css.uri.badTarget";

    /**
     * The error code when a specified URI references a bad element.
     * {0} = the uri
     */
    public static final String ERR_URI_BAD_TARGET
        = "uri.badTarget";

    /**
     * The error code when the bridge detected circular dependencies
     * while resolving a list of URI.
     * {0} = the uri
     */
    public static final String ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES
        = "xlink.href.circularDependencies";

    /**
     * The error code when the bridge try to load a URI
     * {0} = the uri
     */
    public static final String ERR_URI_MALFORMED
        = "uri.malformed";

    /**
     * The error code when the bridge encoutered an I/O error while
     * loading a URI.
     * {0} = the uri
     */
    public static final String ERR_URI_IO
        = "uri.io";

    /**
     * The error code when the bridge encountered a SecurityException
     * while loading a URI
     * {0} = the uri
     */
    public static final String ERR_URI_UNSECURE
        = "uri.unsecure";

    /**
     * The error code when the bridge tries to referenced an invalid
     * node inside a document.
     * {0} = the uri
     */
    public static final String ERR_URI_REFERENCE_A_DOCUMENT
        = "uri.referenceDocument";

    /**
     * The error code when the bridge tries to an image and the image
     * format is not supported.
     * {0} = the uri
     */
    public static final String ERR_URI_IMAGE_INVALID
        = "uri.image.invalid";

    /**
     * The resource that contains the title for the Broken Link message
     */
    public static final String MSG_BROKEN_LINK_TITLE
        = "broken.link.title";
}
