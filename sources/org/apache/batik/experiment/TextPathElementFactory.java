/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.experiment;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is a factory of text path element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextPathElementFactory
    implements SVGOMDocument.CustomElementFactory {
    /**
     * Creates an instance of a custom element.
     */
    public Element create(String prefix, Document doc) {
        return new TextPathElement(prefix, doc);
    }
}
