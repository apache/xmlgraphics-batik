/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
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
 * This class implements {@link SVGFilterElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFilterElement
    extends    SVGStylableElement
    implements SVGFilterElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute(XMLSupport.XMLNS_NAMESPACE_URI,
                                          null,
                                          "xmlns:xlink",
                                          XLinkSupport.XLINK_NAMESPACE_URI);
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink",
                                          "type",
                                          "simple");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink",
                                          "show",
                                          "replace");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink",
                                          "actuate",
                                          "onRequest");
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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FILTER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getFilterUnits()}.
     */
    public SVGAnimatedEnumeration getFilterUnits() {
        throw new RuntimeException(" !!! TODO: getFilterUnits()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getPrimitiveUnits()}.
     */
    public SVGAnimatedEnumeration getPrimitiveUnits() {
        throw new RuntimeException(" !!! TODO: getFilterResX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        throw new RuntimeException(" !!! TODO: getX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        throw new RuntimeException(" !!! TODO: getY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        throw new RuntimeException(" !!! TODO: getWidth()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        throw new RuntimeException(" !!! TODO: getHeight()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getFilterResX()}.
     */
    public SVGAnimatedInteger getFilterResX() {
        throw new RuntimeException(" !!! TODO: getFilterResX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#getFilterResY()}.
     */
    public SVGAnimatedInteger getFilterResY() {
        throw new RuntimeException(" !!! TODO: getFilterResY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFilterElement#setFilterRes(int,int)}.
     */
    public void setFilterRes(int filterResX, int filterResY) {
        throw new RuntimeException(" !!! TODO: setFilterRes()");
    }

    // SVGURIReference support /////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
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
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFilterElement();
    }
}
