/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGList;

/**
 * This class provides support for SVGTests features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTestsSupport {
    /**
     * The requiredFeatures attribute name.
     */
    public final static String REQUIRED_FEATURES = "requiredFeatures";

    /**
     * The requiredExtensions attribute name.
     */
    public final static String REQUIRED_EXTENSIONS = "requiredExtensions";

    /**
     * The systemLanguage attribute name.
     */
    public final static String SYSTEM_LANGUAGE = "systemLanguage";

    /**
     * Creates a new SVGTestsSupport object.
     */
    public SVGTestsSupport() {
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public SVGList getRequiredFeatures(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.getRequiredFeatures()");
    }

    /**
     * To implements {@link
     * org.w3c.dom.svg.SVGTests#setRequiredFeatures(org.w3c.dom.svg.SVGList)}.
     */
    public void setRequiredFeatures(SVGList requiredFeatures, Element elt)
	throws DOMException {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.setRequiredFeatures()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGList getRequiredExtensions(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.getRequiredExtensions()");
    }

    /**
     * To implements {@link
     * org.w3c.dom.svg.SVGTests#setRequiredExtensions(org.w3c.dom.svg.SVGList)}.
     */
    public void setRequiredExtensions(SVGList requiredExtensions, Element elt)
	throws DOMException {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.setRequiredExtensions()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGList getSystemLanguage(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.getSystemLanguage()");
    }

    /**
     * To implements {@link
     * org.w3c.dom.svg.SVGTests#setRequiredExtensions(org.w3c.dom.svg.SVGList)}.
     */
    public void setSystemLanguage(SVGList systemLanguage, Element elt)
	throws DOMException {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.setSystemLanguage()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension, Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTestsSupport.hasExtension()");	
    }
}
