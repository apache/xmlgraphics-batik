/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.ElementSelector;

/**
 * This class provides an abstract implementation of the ElementSelector
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractElementSelector
    implements ElementSelector,
	       ExtendedSelector {

    /**
     * The namespace URI.
     */
    protected String namespaceURI;

    /**
     * The local name.
     */
    protected String localName;

    /**
     * Creates a new ElementSelector object.
     */
    protected AbstractElementSelector(String uri, String name) {
	namespaceURI = uri;
	localName    = name;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	AbstractElementSelector s = (AbstractElementSelector)obj;
	return s.namespaceURI.equals(namespaceURI) &&
	       s.localName.equals(localName);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ElementSelector#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
	return namespaceURI;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ElementSelector#getLocalName()}.
     */
    public String getLocalName() {
	return localName;
    }
}
