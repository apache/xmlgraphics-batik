/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.io.Serializable;

import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.DOMImplementation;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation},
 * {@link org.w3c.dom.css.DOMImplementationCSS} interfaces.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractDOMImplementation
    implements DOMImplementation,
               Serializable {

    /**
     * The supported features.
     */
    protected final HashTable features = new HashTable();
    {
	registerFeature("XML",            new String[] { "1.0", "2.0" });
	registerFeature("Events",         "2.0");
	registerFeature("MouseEvents",    "2.0");
	registerFeature("MutationEvents", "2.0");
	registerFeature("Traversal",      "2.0");
	registerFeature("UIEvents",       "2.0");
    }
    
    /**
     * Registers a DOM feature.
     */
    protected void registerFeature(String name, Object value) {
        features.put(name.toLowerCase(), value);
    }

    /**
     * Creates a new AbstractDOMImplementation object.
     */
    protected AbstractDOMImplementation() {
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.DOMImplementation#hasFeature(String,String)}.
     */
    public boolean hasFeature(String feature, String version) {
	Object v = features.get(feature.toLowerCase());
	if (v == null) {
	    return false;
	}
	if (version == null || version.length() == 0) {
	    return true;
	}
        if (v instanceof String) {
            return version.equals(v);
        } else {
            String[] va = (String[])v;
            for (int i = 0; i < va.length; i++) {
                if (version.equals(va[i])) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Creates an DocumentEventSupport object suitable for use with this implementation.
     */
    public DocumentEventSupport createDocumentEventSupport() {
        return new DocumentEventSupport();
    }
}
