/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.svg.SVGCSSContext;
import org.apache.batik.parser.ParserFactory;

/**
 * This interface is the placeholder for SVG application informations.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGContext extends SVGCSSContext {
    /**
     * Returns the parser factory.
     */
    ParserFactory getParserFactory();

    /**
     * Returns the user agent.
     */
    UserAgent getUserAgent();
}
