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

import org.apache.batik.dom.util.XLinkSupport;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGGradientElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMGradientElement
    extends    SVGOMElement
    implements SVGGradientElement,
               OverrideStyleElement,
	       ExtendedElementCSSInlineStyle,
	       ElementNonCSSPresentationalHints {

    /**
     * Creates a new SVGOMGradientElement object.
     */
    protected SVGOMGradientElement() {
    }

    /**
     * Creates a new SVGOMGradientElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientTransform()}.
     */
    public SVGAnimatedTransformList getGradientTransform() {
	throw new RuntimeException(" !!! TODO: SVGOMGradientElement.getGradientTransform()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientUnits()}.
     */
    public SVGAnimatedEnumeration getGradientUnits() {
	throw new RuntimeException(" !!! TODO: SVGOMGradientElement.getGradientUnits()");
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getSpreadMethod()}.
     */
    public SVGAnimatedEnumeration getSpreadMethod() {
	throw new RuntimeException(" !!! TODO: SVGOMGradientElement.getSpreadMethod()");
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
}
