/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 * This interface represents a StyleSheet factory.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface StyleSheetFactory {
    /**
     * Creates a stylesheet from the data of the xml-stylesheet
     * processing instruction or return null when it is not possible
     * to create the given stylesheet.
     */
    StyleSheet createStyleSheet(Node node, String data);
}
