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

import org.w3c.dom.Node;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGMarkerElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGMarkerElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMMarkerElement
    extends    SVGOMElement
    implements SVGMarkerElement,
               OverrideStyleElement,
               ExtendedElementCSSInlineStyle,
               ElementNonCSSPresentationalHints {
    
    /**
     * The reference to the refX attribute.
     */
    protected transient WeakReference refXReference;

    /**
     * The reference to the refY attribute.
     */
    protected transient WeakReference refYReference;

    /**
     * The reference to the markerWidth attribute.
     */
    protected transient WeakReference markerWidthReference;

    /**
     * The reference to the markerHeight attribute.
     */
    protected transient WeakReference markerHeightReference;

    /**
     * Creates a new SVGOMMarkerElement object.
     */
    protected SVGOMMarkerElement() {
    }

    /**
     * Creates a new SVGOMMarkerElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMMarkerElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_MARKER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getRefX()}.
     */
    public SVGAnimatedLength getRefX() {
        SVGAnimatedLength result;
        if (refXReference == null ||
            (result = (SVGAnimatedLength)refXReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_REF_X_ATTRIBUTE, null);
            refXReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getRefY()}.
     */
    public SVGAnimatedLength getRefY() {
        SVGAnimatedLength result;
        if (refYReference == null ||
            (result = (SVGAnimatedLength)refYReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_REF_Y_ATTRIBUTE, null);
            refYReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerUnits()}.
     */
    public SVGAnimatedEnumeration getMarkerUnits() {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.getMarkerUnits()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerWidth()}.
     */
    public SVGAnimatedLength getMarkerWidth() {
        SVGAnimatedLength result;
        if (markerWidthReference == null ||
            (result = (SVGAnimatedLength)markerWidthReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_MARKER_WIDTH_ATTRIBUTE,
                                             null);
            markerWidthReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerHeight()}.
     */
    public SVGAnimatedLength getMarkerHeight() {
        SVGAnimatedLength result;
        if (markerHeightReference == null ||
            (result = (SVGAnimatedLength)markerHeightReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_MARKER_HEIGHT_ATTRIBUTE,
                                             null);
            markerHeightReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getOrientType()}.
     */
    public SVGAnimatedEnumeration getOrientType() {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.getOrientType()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getOrientAngle()}.
     */
    public SVGAnimatedAngle getOrientAngle() {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.getOrientAngle()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#setOrientToAuto()}.
     */
    public void setOrientToAuto() {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.setOrientToAuto()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#setOrientToAngle(SVGAngle)}.
     */
    public void setOrientToAngle(SVGAngle angle) {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.setOrientToAngle()");
    }

    // SVGFitToViewBox support ////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getViewBox()}.
     */
    public SVGAnimatedRect getViewBox() {
	throw new RuntimeException(" !!! TODO: SVGOMMarkerElement.getViewBox()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getPreserveAspectRatio()}.
     */
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
	throw new RuntimeException
	    (" !!! TODO: SVGOMMarkerElement.getPreserveAspectRatio()");
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

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMMarkerElement();
    }
}
