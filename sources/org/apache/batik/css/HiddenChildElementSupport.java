/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides utility methods for HiddenChildElement support.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class HiddenChildElementSupport {
    /**
     * This class does not need to be instanciated.
     */
    protected HiddenChildElementSupport() {
    }

    /**
     * Returns the parent element.
     */
    public final static Element getParentElement(Element e) {
        Node n = e.getParentNode();
        if (n == null) {
            if (e instanceof HiddenChildElement) {
                return ((HiddenChildElement)e).getParentElement();
            }
            return null;
        }
        do {
            if (n.getNodeType() == n.ELEMENT_NODE) {
                return (Element)n;
            }
            n = n.getParentNode();
        } while (n != null); 
        return null;
    }
}
