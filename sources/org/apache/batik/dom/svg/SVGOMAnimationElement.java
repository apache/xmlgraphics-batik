/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.DOMException;

import org.w3c.dom.smil.ElementTimeControl;

import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class provides an implementation of the SVGAnimationElement interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMAnimationElement
    extends SVGOMElement
    implements SVGAnimationElement {
    
    /**
     * Creates a new SVGOMAnimationElement.
     */
    protected SVGOMAnimationElement() {
    }

    /**
     * Creates a new SVGOMAnimationElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMAnimationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getTargetElement()}.
     */
    public SVGElement getTargetElement() {
        throw new RuntimeException("!!! TODO: getTargetElement()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getStartTime()}.
     */
    public float getStartTime() {
        throw new RuntimeException("!!! TODO: getStartTime()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getCurrentTime()}.
     */
    public float getCurrentTime() {
        throw new RuntimeException("!!! TODO: getCurrentTime()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getSimpleDuration()}.
     */
    public float getSimpleDuration() throws DOMException {
        throw new RuntimeException("!!! TODO: getSimpleDuration()");
    }

    // ElementTimeControl ////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElement()}.
     */
    public boolean beginElement() throws DOMException {
        throw new RuntimeException("!!! TODO: beginElement()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElementAt(float)}.
     */
    public boolean beginElementAt(float offset) throws DOMException {
        throw new RuntimeException("!!! TODO: beginElementAt()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElement()}.
     */
    public boolean endElement() throws DOMException {
        throw new RuntimeException("!!! TODO: endElement()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElementAt(float)}.
     */
    public boolean endElementAt(float offset) throws DOMException {
        throw new RuntimeException("!!! TODO: endElementAt(float)");
    }

    // SVGExternalResourcesRequired support /////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired() {
	return SVGExternalResourcesRequiredSupport.
            getExternalResourcesRequired(this);
    }

    // SVGTests support ///////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public SVGStringList getRequiredFeatures() {
	return SVGTestsSupport.getRequiredFeatures(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGStringList getRequiredExtensions() {
	return SVGTestsSupport.getRequiredExtensions(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGStringList getSystemLanguage() {
	return SVGTestsSupport.getSystemLanguage(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension) {
	return SVGTestsSupport.hasExtension(extension, this);
    }
}
