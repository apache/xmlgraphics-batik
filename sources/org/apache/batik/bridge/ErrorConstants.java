/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
