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
import org.w3c.dom.svg.SVGStringList;

/**
 * This class provides support for SVGTests features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTestsSupport {

    /**
     * Creates a new SVGTestsSupport object.
     */
    public SVGTestsSupport() {
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public static SVGStringList getRequiredFeatures(Element elt) {
	throw new RuntimeException(" !!! TODO: getRequiredFeatures()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public static SVGStringList getRequiredExtensions(Element elt) {
	throw new RuntimeException(" !!! TODO: getRequiredExtensions()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public static SVGStringList getSystemLanguage(Element elt) {
	throw new RuntimeException(" !!! TODO: getSystemLanguage()");
    }

    /**
     * To implements {@link org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public static boolean hasExtension(String extension, Element elt) {
	throw new RuntimeException(" !!! TODO: hasExtension()");	
    }
}
