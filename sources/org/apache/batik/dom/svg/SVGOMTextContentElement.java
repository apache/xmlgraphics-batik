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
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;

/**
 * This class provides a common superclass for all graphics elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMTextContentElement
    extends    SVGOMElement
    implements OverrideStyleElement,
	       ExtendedElementCSSInlineStyle,
	       ElementNonCSSPresentationalHints {
    /**
     * Creates a new SVGOMTextContentElement.
     */
    protected SVGOMTextContentElement() {
    }

    /**
     * Creates a new SVGOMTextContentElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMTextContentElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getTextLength()}.
     */
    public SVGAnimatedLength getTextLength() {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getTextLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getLengthAdjust()}.
     */
    public SVGAnimatedEnumeration getLengthAdjust() {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getLengthAdjust()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getNumberOfChars()}.
     */
    public int getNumberOfChars() {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getNumberOfChars()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getComputedTextLength()}.
     */
    public float getComputedTextLength() {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getComputedTextLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getSubStringLength(int,int)}.
     */
    public float getSubStringLength(int charnum, int nchars)
        throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getSubStringLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getStartPositionOfChar(int)}.
     */
    public SVGPoint getStartPositionOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getStartPositionOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getEndPositionOfChar(int)}.
     */
    public SVGPoint getEndPositionOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getEndPositionOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getExtentOfChar(int)}.
     */
    public SVGRect getExtentOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getExtentOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getRotationOfChar(int)}.
     */
    public float getRotationOfChar(int charnum) throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getRotationOfChar()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getCharNumAtPosition(SVGPoint)}.
     */
    public int getCharNumAtPosition(SVGPoint point) {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getCharNumAtPosition()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int,int)}.
     */
    public void selectSubString(int charnum, int nchars)
        throws DOMException {
        throw new RuntimeException(" !!! SVGOMTextContentElement.getSubStringLength()");
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

    // SVGStylable support ///////////////////////////////////////////////////

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
        return SVGStylableSupport.getStyle(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getPresentationAttribute(String)}.
     */
    public CSSValue getPresentationAttribute(String name) {
        return SVGStylableSupport.getPresentationAttribute(name, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getClassName()}.
     */
    public SVGAnimatedString getClassName() {
        return SVGStylableSupport.getClassName(this);
    }

    // OverrideStyleElement ///////////////////////////////////////////

    /**
     * Implements {@link
     * org.apache.batik.dom.util.OverrideStyleElement#hasOverrideStyle(String)}.
     */
    public boolean hasOverrideStyle(String pseudoElt) {
	return SVGStylableSupport.hasOverrideStyle(pseudoElt);
    }    

    /**
     * Implements {@link
     * org.apache.batik.dom.util.OverrideStyleElement#getOverrideStyle(String)}.
     */
    public CSSStyleDeclaration getOverrideStyle(String pseudoElt) {
	return SVGStylableSupport.getOverrideStyle(pseudoElt, this);
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
        setAttributeNS(XMLSupport.XML_NAMESPACE_URI,
                       XMLSupport.XML_LANG_ATTRIBUTE,
                       lang);
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
        setAttributeNS(XMLSupport.XML_NAMESPACE_URI,
                       XMLSupport.XML_SPACE_ATTRIBUTE,
                       space);
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
