/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.css.ElementNonCSSPresentationalHints;
import org.apache.batik.css.ExtendedElementCSSInlineStyle;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.OverrideStyleElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGElementInstance;
import org.w3c.dom.svg.SVGFilterElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFilterElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFilterElement
    extends    SVGOMElement
    implements SVGFilterElement,
               OverrideStyleElement,
               ExtendedElementCSSInlineStyle,
               ElementNonCSSPresentationalHints {

    /**
     * The DefaultAttributeValueProducer for filterUnits.
     */
    protected final static DefaultAttributeValueProducer
        FILTER_UNITS_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FILTER_FILTER_UNITS_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for primitiveUnits.
     */
    protected final static DefaultAttributeValueProducer
        PRIMITIVE_UNITS_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FILTER_PRIMITIVE_UNITS_DEFAULT_VALUE;
                }
            };

    /**
     * The reference to the x attribute.
     */
    protected transient WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected transient WeakReference yReference;

    /**
     * The reference to the width attribute.
     */
    protected transient WeakReference widthReference;

    /**
     * The reference to the height attribute.
     */
    protected transient WeakReference heightReference;

    /**
     * The reference to the filterUnits attribute.
     */
    protected transient WeakReference filterUnitsReference;

    /**
     * The reference to the primitiveUnits attribute.
     */
    protected transient WeakReference primitiveUnitsReference;

    // The enumeration maps.
    protected final static Map STRING_TO_SHORT_FILTER_UNITS = new HashMap(5);
    protected final static Map SHORT_TO_STRING_FILTER_UNITS = new HashMap(5);
    protected final static Map STRING_TO_SHORT_PRIMITIVE_UNITS =
        STRING_TO_SHORT_FILTER_UNITS;
    protected final static Map SHORT_TO_STRING_PRIMITIVE_UNITS =
        SHORT_TO_STRING_FILTER_UNITS;
    static {
        STRING_TO_SHORT_FILTER_UNITS.put(SVG_USER_SPACE_ON_USE_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_FILTER_UNITS.put(SVG_OBJECT_BOUNDING_BOX_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)2));

        SHORT_TO_STRING_FILTER_UNITS.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                         SVG_USER_SPACE_ON_USE_VALUE);
        SHORT_TO_STRING_FILTER_UNITS.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                         SVG_OBJECT_BOUNDING_BOX_VALUE);
    }

    /**
     * Creates a new SVGOMFilterElement object.
     */
    protected SVGOMFilterElement() {
    }

    /**
     * Creates a new SVGOMFilterElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFilterElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FILTER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getFilterUnits()}.
     */
    public SVGAnimatedEnumeration getFilterUnits() {
        SVGAnimatedEnumeration result;
        if (filterUnitsReference == null ||
            (result = (SVGAnimatedEnumeration)filterUnitsReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_FILTER_UNITS_ATTRIBUTE,
                                                  STRING_TO_SHORT_FILTER_UNITS,
                                                  SHORT_TO_STRING_FILTER_UNITS,
                                                  FILTER_UNITS_DEFAULT_VALUE_PRODUCER);
            filterUnitsReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getPrimitiveUnits()}.
     */
    public SVGAnimatedEnumeration getPrimitiveUnits() {
        SVGAnimatedEnumeration result;
        if (primitiveUnitsReference == null ||
            (result = (SVGAnimatedEnumeration)primitiveUnitsReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_PRIMITIVE_UNITS_ATTRIBUTE,
                                                  STRING_TO_SHORT_PRIMITIVE_UNITS,
                                                  SHORT_TO_STRING_PRIMITIVE_UNITS,
                                                PRIMITIVE_UNITS_DEFAULT_VALUE_PRODUCER);
            primitiveUnitsReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGFilterElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        SVGAnimatedLength result;
        if (xReference == null ||
            (result = (SVGAnimatedLength)xReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_X_ATTRIBUTE, null);
            xReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGFilterElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        SVGAnimatedLength result;
        if (yReference == null ||
            (result = (SVGAnimatedLength)yReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_Y_ATTRIBUTE, null);
            yReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        SVGAnimatedLength result;
        if (widthReference == null ||
            (result = (SVGAnimatedLength)widthReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, "width", null);
            widthReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        SVGAnimatedLength result;
        if (heightReference == null ||
            (result = (SVGAnimatedLength)heightReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, "height", null);
            heightReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getFilterResX()}.
     */
    public SVGAnimatedInteger getFilterResX() {
        throw new RuntimeException(" !!! TODO: SVGOMFilterElement.getFilterResX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#getFilterResY()}.
     */
    public SVGAnimatedInteger getFilterResY() {
        throw new RuntimeException(" !!! TODO: SVGOMFilterElement.getFilterResY()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterElement#setFilterRes(int,int)}.
     */
    public void setFilterRes(int filterResX, int filterResY) {
        throw new RuntimeException(" !!! TODO: SVGOMFilterElement.setFilterRes()");
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
        return new SVGOMFilterElement();
    }
}
