/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.DOMImplementation;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation},
 * {@link org.w3c.dom.css.DOMImplementationCSS} interfaces.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractDOMImplementation implements DOMImplementation {
    /**
     * The supported features.
     */
    protected final HashTable features = new HashTable();
    {
	features.put("XML",            "2.0");
	features.put("Events",         "2.0");
	features.put("MouseEvents",    "2.0");
	features.put("MutationEvents", "2.0");
	features.put("Traversal",      "2.0");
	features.put("UIEvents",       "2.0");
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
	String v = (String)features.get(feature);
	if (v == null) {
	    return false;
	}
	if (version == null) {
	    return true;
	}
	return version.equals(v);
    }
}
