/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.css.ElementNonCSSPresentationalHints;
import org.apache.batik.css.ExtendedElementCSSInlineStyle;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.OverrideStyleElement;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGList;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

/**
 * This class provides a common superclass for all graphics elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGGraphicsElement
    extends    SVGOMElement
    implements OverrideStyleElement,
	       ExtendedElementCSSInlineStyle,
	       ElementNonCSSPresentationalHints {
    /**
     * Creates a new SVGGraphicsElement.
     */
    protected SVGGraphicsElement() {
    }

    /**
     * Creates a new SVGGraphicsElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGGraphicsElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    // ElementNonCSSPresentationalHints ////////////////////////////////////

    /**
     * Returns the translation of the non-CSS hints to the corresponding
     * CSS rules. The result can be null.
     */
    public CSSStyleDeclaration getNonCSSPresentationalHints() {
	return ElementNonCSSPresentationalHintsSupport.
            getNonCSSPresentationalHints(this);
    }

    // SVGTransformable support /////////////////////////////////////////////

    /**
     * The transformable support.
     */
    protected SVGTransformableSupport transformableSupport;

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getNearestViewportElement()}.
     */
    public SVGElement getNearestViewportElement() {
	return transformableSupport.getNearestViewportElement(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getFarthestViewportElement()}.
     */
    public SVGElement getFarthestViewportElement() {
	return transformableSupport.getFarthestViewportElement(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getTransform()}.
     */
    public SVGAnimatedTransformList getTransform() {
	return transformableSupport.getTransform(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getBBox()}.
     */
    public SVGRect getBBox() {
	return transformableSupport.getBBox(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getCTM()}.
     */
    public SVGMatrix getCTM() {
	return transformableSupport.getCTM(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getScreenCTM()}.
     */
    public SVGMatrix getScreenCTM() {
	return transformableSupport.getScreenCTM(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTransformable#getTransformToElement(SVGElement)}.
     */
    public SVGMatrix getTransformToElement(SVGElement element)
	throws SVGException {
	return transformableSupport.getTransformToElement(element, this);
    }
    
    // SVGStylable support ///////////////////////////////////////////////////

    /**
     * The stylable support.
     */
    protected SVGStylableSupport stylableSupport;

    /**
     * Returns stylableSupport different from null.
     */
    protected final SVGStylableSupport getStylableSupport() {
	if (stylableSupport == null) {
	    stylableSupport = new SVGStylableSupport();
	}
	return stylableSupport;
    }

    /**
     * Implements {@link
     * org.apache.batik.css.ExtendedElementCSSInlineStyle#hasStyle()}.
     */
    public boolean hasStyle() {
        return SVGStylableSupport.hasStyle(this);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGStylable#getStyle()}.
     */
    public CSSStyleDeclaration getStyle() {
        return getStylableSupport().getStyle(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getPresentationAttribute(String)}.
     */
    public CSSValue getPresentationAttribute(String name) {
        return getStylableSupport().getPresentationAttribute(name, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getAnimatedPresentationAttribute(String)}.
     */
    public CSSValue getAnimatedPresentationAttribute(String name) {
        return getStylableSupport().getAnimatedPresentationAttribute(name,
                                                                     this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getClassName()}.
     */
    public SVGAnimatedString getClassName() {
        return getStylableSupport().getClassName(this);
    }

    // OverrideStyleElement ///////////////////////////////////////////

    /**
     * Implements {@link
     * OverrideStyleElement#hasOverrideStyle(String)}.
     */
    public boolean hasOverrideStyle(String pseudoElt) {
	return getStylableSupport().hasOverrideStyle(pseudoElt);
    }    

    /**
     * Implements {@link
     * OverrideStyleElement#getOverrideStyle(String)}.
     */
    public CSSStyleDeclaration getOverrideStyle(String pseudoElt) {
	return getStylableSupport().getOverrideStyle(pseudoElt, this);
    }

    // SVGExternalResourcesRequired support /////////////////////////////

    /**
     * The SVGExternalResourcesRequired support.
     */
    protected SVGExternalResourcesRequiredSupport
        externalResourcesRequiredSupport;

    /**
     * Returns testsSupport different from null.
     */
    protected final SVGExternalResourcesRequiredSupport
	getExternalResourcesRequiredSupport() {
	if (externalResourcesRequiredSupport == null) {
	    externalResourcesRequiredSupport =
                new SVGExternalResourcesRequiredSupport();
	}
	return externalResourcesRequiredSupport;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired() {
	return getExternalResourcesRequiredSupport().
            getExternalResourcesRequired(this);
    }

    // SVGLangSpace support //////////////////////////////////////////////////
    
    /**
     * <b>DOM</b>: Returns the xml:lang attribute value.
     */
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }

    /**
     * <b>DOM</b>: Sets the xml:lang attribute value.
     */
    public void setXMLlang(String lang) {
        XMLSupport.setXMLLang(this, lang);
    }
    
    /**
     * <b>DOM</b>: Returns the xml:space attribute value.
     */
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }

    /**
     * <b>DOM</b>: Sets the xml:space attribute value.
     */
    public void setXMLspace(String space) {
        XMLSupport.setXMLSpace(this, space);
    }

    // SVGTests support ///////////////////////////////////////////////////

    /**
     * The tests support.
     */
    protected SVGTestsSupport testsSupport;

    /**
     * Returns testsSupport different from null.
     */
    protected final SVGTestsSupport getTestsSupport() {
	if (testsSupport == null) {
	    testsSupport = new SVGTestsSupport();
	}
	return testsSupport;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public SVGList getRequiredFeatures() {
	return getTestsSupport().getRequiredFeatures(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#setRequiredFeatures(org.w3c.dom.svg.SVGList)}.
     */
    public void setRequiredFeatures(SVGList requiredFeatures)
	throws DOMException {
	getTestsSupport().setRequiredFeatures(requiredFeatures, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGList getRequiredExtensions() {
	return getTestsSupport().getRequiredExtensions(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     *org.w3c.dom.svg.SVGTests#setRequiredExtensions(org.w3c.dom.svg.SVGList)}.
     */
    public void setRequiredExtensions(SVGList requiredExtensions)
	throws DOMException {
	getTestsSupport().setRequiredExtensions(requiredExtensions, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGList getSystemLanguage() {
	return getTestsSupport().getSystemLanguage(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     *org.w3c.dom.svg.SVGTests#setRequiredExtensions(org.w3c.dom.svg.SVGList)}.
     */
    public void setSystemLanguage(SVGList systemLanguage)
	throws DOMException {
	getTestsSupport().setSystemLanguage(systemLanguage, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension) {
	return getTestsSupport().hasExtension(extension, this);
    }
}
