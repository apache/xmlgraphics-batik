/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.css.ElementNonCSSPresentationalHints;
import org.apache.batik.css.ExtendedElementCSSInlineStyle;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.OverrideStyleElement;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.dom.util.XLinkSupport;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGPatternElement;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class implements {@link org.w3c.dom.svg.SVGStopElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMPatternElement
    extends    SVGOMElement
    implements SVGPatternElement,
               OverrideStyleElement,
	       ExtendedElementCSSInlineStyle,
	       ElementNonCSSPresentationalHints {
    
    /**
     * The reference to the x attribute.
     */
    protected WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected WeakReference yReference;

    /**
     * The reference to the width attribute.
     */
    protected WeakReference widthReference;

    /**
     * The reference to the height attribute.
     */
    protected WeakReference heightReference;

    /**
     * Creates a new SVGOMPatternElement object.
     */
    public SVGOMPatternElement() {
    }

    /**
     * Creates a new SVGOMPatternElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMPatternElement(String prefix,
                               AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_PATTERN;
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGPatternElement#getPatternTransform()}.
     */
    public SVGAnimatedTransformList getPatternTransform() {
	throw new RuntimeException(" !!! TODO: SVGOMPatternElement.getPatternTransform()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPatternElement#getPatternUnits()}.
     */
    public SVGAnimatedEnumeration getPatternUnits() {
	throw new RuntimeException(" !!! TODO: SVGOMPatternElement.getPatternUnits()");
    }
 
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGPatternElement#getX()}.
     */
    public SVGAnimatedLength getX() {
	SVGAnimatedLength result;
	if (xReference == null ||
	    (result = (SVGAnimatedLength)xReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "x");
	    xReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGPatternElement#getY()}.
     */
    public SVGAnimatedLength getY() {
	SVGAnimatedLength result;
	if (yReference == null ||
	    (result = (SVGAnimatedLength)yReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "y");
	    yReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPatternElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
	SVGAnimatedLength result;
	if (widthReference == null ||
	    (result = (SVGAnimatedLength)widthReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "width");
	    widthReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPatternElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
	SVGAnimatedLength result;
	if (heightReference == null ||
	    (result = (SVGAnimatedLength)heightReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "height");
	    heightReference = new WeakReference(result);
	}
	return result;
    } 

    // XLink support //////////////////////////////////////////////////////

    /**
     * The SVGURIReference support.
     */
    protected SVGURIReferenceSupport uriReferenceSupport;

    /**
     * Returns uriReferenceSupport different from null.
     */
    protected final SVGURIReferenceSupport getSVGURIReferenceSupport() {
	if (uriReferenceSupport == null) {
	    uriReferenceSupport = new SVGURIReferenceSupport();
	}
	return uriReferenceSupport;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return getSVGURIReferenceSupport().getHref(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkType()}.
     */
    public String getXlinkType() {
        return XLinkSupport.getXLinkType(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkType(String)}.
     */
    public void setXlinkType(String str) {
        XLinkSupport.setXLinkType(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkRole()}.
     */
    public String getXlinkRole() {
        return XLinkSupport.getXLinkRole(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkRole(String)}.
     */
    public void setXlinkRole(String str) {
        XLinkSupport.setXLinkRole(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkArcRole()}.
     */
    public String getXlinkArcRole() {
        return XLinkSupport.getXLinkArcRole(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkArcRole(String)}.
     */
    public void setXlinkArcRole(String str) {
        XLinkSupport.setXLinkArcRole(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkTitle()}.
     */
    public String getXlinkTitle() {
        return XLinkSupport.getXLinkTitle(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkTitle(String)}.
     */
    public void setXlinkTitle(String str) {
        XLinkSupport.setXLinkTitle(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkShow()}.
     */
    public String getXlinkShow() {
        return XLinkSupport.getXLinkShow(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkShow(String)}.
     */
    public void setXlinkShow(String str) {
        XLinkSupport.setXLinkShow(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkActuate()}.
     */
    public String getXlinkActuate() {
        return XLinkSupport.getXLinkActuate(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkActuate(String)}.
     */
    public void setXlinkActuate(String str) {
        XLinkSupport.setXLinkActuate(this, str);
    }

    /**
     * Returns the value of the 'xlink:href' attribute of the given element.
     */
    public String getXlinkHref() {
        return XLinkSupport.getXLinkHref(this);
    }

    /**
     * Sets the value of the 'xlink:href' attribute of the given element.
     */
    public void setXlinkHref(String str) {
        XLinkSupport.setXLinkHref(this, str);
    }

    // SVGFitToViewBox support ////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getViewBox()}.
     */
    public SVGAnimatedRect getViewBox() {
	throw new RuntimeException(" !!! TODO: SVGOMSVGElement.getViewBox()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getPreserveAspectRatio()}.
     */
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
	throw new RuntimeException
	    (" !!! TODO: SVGOMSVGElement.getPreserveAspectRatio()");
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
    public SVGStringList getRequiredFeatures() {
	return getTestsSupport().getRequiredFeatures(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#setRequiredFeatures(SVGStringList)}.
     */
    public void setRequiredFeatures(SVGStringList requiredFeatures)
	throws DOMException {
	getTestsSupport().setRequiredFeatures(requiredFeatures, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGStringList getRequiredExtensions() {
	return getTestsSupport().getRequiredExtensions(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     *org.w3c.dom.svg.SVGTests#setRequiredExtensions(SVGStringList)}.
     */
    public void setRequiredExtensions(SVGStringList requiredExtensions)
	throws DOMException {
	getTestsSupport().setRequiredExtensions(requiredExtensions, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGStringList getSystemLanguage() {
	return getTestsSupport().getSystemLanguage(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     *org.w3c.dom.svg.SVGTests#setRequiredExtensions(SVGStringList)}.
     */
    public void setSystemLanguage(SVGStringList systemLanguage)
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

}
