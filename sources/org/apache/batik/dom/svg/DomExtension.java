/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

/**
 * This is a Service interface for classes that want to extend the
 * functionality of the SVGOMDocument, to support new tags in the
 * DOM tree.  
 */
public interface DomExtension {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority();

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor();

    /**
     * This should return a contact address (usually an e-mail address).
     */
    public String getContactAddress();

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL();

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription();

    /**
     * This method should update the DOMImplementation with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * This is called before the DOMImplementation starts.
     *
     * @param di The DOMImplementation instance to be updated
     */
    public void registerTags(ExtensibleSVGDOMImplementation di);
}
