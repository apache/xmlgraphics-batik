/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.dom.stylesheets.LinkStyle;

/**
 * This interface is an extension of the standard LinkStyle interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ExtendedLinkStyle extends LinkStyle {
    
    /**
     * Returns the URI of the referenced stylesheet.
     */
    String getStyleSheetURI();
}
