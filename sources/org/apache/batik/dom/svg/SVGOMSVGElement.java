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
import org.apache.batik.dom.util.XMLSupport;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGViewSpec;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

/**
 * This class implements {@link org.w3c.dom.svg.SVGSVGElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMSVGElement
    extends    SVGOMElement
    implements SVGSVGElement,
	       OverrideStyleElement,
	       ExtendedElementCSSInlineStyle,
	       ElementNonCSSPresentationalHints {
    /**
     * The contentScriptType attribute name.
     */
    public final static String CONTENT_SCRIPT_TYPE = "contentScriptType";

    /**
     * The contentStyleType attribute name.
     */
    public final static String CONTENT_STYLE_TYPE = "contentStyleType";

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(7);
        values.put("contentScriptType",    "text/ecmascript");
        values.put("contentStyleType",     "text/css");
        values.put("preserveAspectRatio",  "xMidYMid meet");
        values.put("zoomAndPan",           "magnify");
        attributeValues.put(null, values);

        values = new HashMap(2);
        values.put("xmlns", SVGDOMImplementation.SVG_NAMESPACE_URI);
        attributeValues.put(XMLSupport.XMLNS_NAMESPACE_URI, values);
    }

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
     * The parent element.
     */
    protected Element parentElement;

    /**
     * Creates a new SVGOMSVGElement object.
     */
    public SVGOMSVGElement() {
    }

    /**
     * Creates a new SVGOMSVGElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMSVGElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "svg";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGSVGElement#getX()}.
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGSVGElement#getY()}.
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGSVGElement#getWidth()}.
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
     * org.w3c.dom.svg.SVGSVGElement#getHeight()}.
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

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGSVGElement#getContentScriptType()}.
     */
    public String getContentScriptType() {
	return getAttribute(CONTENT_SCRIPT_TYPE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGSVGElement#setContentScriptType(String)}.
     */
    public void setContentScriptType(String type) {
	setAttribute(CONTENT_SCRIPT_TYPE, type);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGSVGElement#getContentStyleType()}.
     */
    public String getContentStyleType() {
	return getAttribute(CONTENT_STYLE_TYPE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGSVGElement#setContentStyleType(String)}.
     */
    public void setContentStyleType(String type) {
	setAttribute(CONTENT_STYLE_TYPE, type);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGSVGElement#getViewport()}.
     */
    public SVGRect getViewport() {
	throw new RuntimeException(" !!! TODO: SVGOMSVGElement.getViewport()");
    }

    public float getPixelUnitToMillimeterX( ) {
        throw new Error();
    }
    public float getPixelUnitToMillimeterY( ) {
        throw new Error();
    }
    public float getScreenPixelToMillimeterX( ) {
        throw new Error();
    }
    public float getScreenPixelToMillimeterY( ) {
        throw new Error();
    }
    public boolean getUseCurrentView( ) {
        throw new Error();
    }
    public void      setUseCurrentView( boolean useCurrentView )
        throws DOMException {
        throw new Error();
    }
    public SVGViewSpec getCurrentView( ) {
        throw new Error();
    }
    public float getCurrentScale( ) {
        throw new Error();
    }
    public void      setCurrentScale( float currentScale )
        throws DOMException {
        throw new Error();
    }
    public SVGPoint getCurrentTranslate( ) {
        throw new Error();
    }
    public int          suspendRedraw ( int max_wait_milliseconds ) {
        throw new Error();
    }
    public void          unsuspendRedraw ( int suspend_handle_id )
        throws DOMException {
        throw new Error();
    }
    public void          unsuspendRedrawAll (  ) {
        throw new Error();
    }
    public void          forceRedraw (  ) {
        throw new Error();
    }
    public void          pauseAnimations (  ) {
        throw new Error();
    }
    public void          unpauseAnimations (  ) {
        throw new Error();
    }
    public boolean       animationsPaused (  ) {
        throw new Error();
    }
    public float         getCurrentTime (  ) {
        throw new Error();
    }
    public void          setCurrentTime ( float seconds ) {
        throw new Error();
    }
    public NodeList      getIntersectionList ( SVGRect rect,
                                               SVGElement referenceElement ) {
        throw new Error();
    }
    public NodeList      getEnclosureList ( SVGRect rect,
                                            SVGElement referenceElement ) {
        throw new Error();
    }
    public boolean       checkIntersection ( SVGElement element,
                                             SVGRect rect ) {
        throw new Error();
    }
    public boolean       checkEnclosure ( SVGElement element, SVGRect rect ) {
        throw new Error();
    }
    public void          deSelectAll (  ) {
        throw new Error();
    }
    public SVGNumber              createSVGNumber (  ) {
        throw new Error();
    }
    public SVGLength              createSVGLength (  ) {
        throw new Error();
    }
    public SVGAngle               createSVGAngle (  ) {
        throw new Error();
    }
    public SVGPoint               createSVGPoint (  ) {
        throw new Error();
    }
    public SVGMatrix              createSVGMatrix (  ) {
        throw new Error();
    }
    public SVGRect                createSVGRect (  ) {
        throw new Error();
    }
    public SVGTransform           createSVGTransform (  ) {
        throw new Error();
    }
    public SVGTransform     createSVGTransformFromMatrix ( SVGMatrix matrix ) {
        throw new Error();
    }
    public String              createSVGString (  ) {
        throw new Error();
    }
    public Element         getElementById ( String elementId ) {
        throw new Error();
    }
    
    /**
     * Returns the default attribute values in a map.
     * @return null if this element has no attribute with a default value.
     */
    protected Map getDefaultAttributeValues() {
        return attributeValues;
    }

    // SVGLocatable ///////////////////////////////////////////////////////

    public SVGElement getNearestViewportElement( ) {
        throw new Error();
    }
    public SVGElement getFarthestViewportElement( ) {
        throw new Error();
    }
    public SVGRect   getBBox (  ) {
        throw new Error();
    }
    public SVGMatrix getCTM (  ) {
        throw new Error();
    }
    public SVGMatrix getScreenCTM (  ) {
        throw new Error();
    }
    public SVGMatrix getTransformToElement ( SVGElement element )
        throws SVGException {
        throw new Error();
    }

    // ElementNonCSSPresentationalHints ////////////////////////////////////

    /**
     * Returns the translation of the non-CSS hints to the corresponding
     * CSS rules. The result can be null.
     */
    public CSSStyleDeclaration getNonCSSPresentationalHints() {
	return ElementNonCSSPresentationalHintsSupport
	    .getNonCSSPresentationalHints(this);
    }

    // ViewCSS ////////////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.views.AbstractView#getDocument()}.
     */
    public DocumentView getDocument() {
	return (DocumentView)getOwnerDocument();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.ViewCSS#getComputedStyle(Element,String)}.
     */
    public CSSStyleDeclaration getComputedStyle(Element elt,
                                                String pseudoElt) {
	AbstractView av = ((DocumentView)getOwnerDocument()).getDefaultView();
        return ((ViewCSS)av).getComputedStyle(elt, pseudoElt);
    }

    // DocumentEvent /////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.events.DocumentEvent#createEvent(String)}.
     */
    public Event createEvent(String eventType) throws DOMException {
	return ((DocumentEvent)getOwnerDocument()).createEvent(eventType);
    }

    // DocumentCSS ////////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.DocumentStyle#getStyleSheets()}.
     */
    public StyleSheetList getStyleSheets() {
        return ((DocumentStyle)getOwnerDocument()).getStyleSheets();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.DocumentCSS#getOverrideStyle(Element,String)}.
     */
    public CSSStyleDeclaration getOverrideStyle(Element elt,
                                                String pseudoElt) {
	return ((DocumentCSS)getOwnerDocument()).getOverrideStyle(elt,
                                                                  pseudoElt);
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

    // SVGZoomAndPan support ///////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGZoomAndPan#getZoomAndPan()}.
     */
    public short getZoomAndPan() {
	return SVGZoomAndPanSupport.getZoomAndPan(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGZoomAndPan#getZoomAndPan()}.
     */
    public void setZoomAndPan(short val) {
	SVGZoomAndPanSupport.setZoomAndPan(this, val);
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
     * org.w3c.dom.svg.SVGExternalResourcesRequired}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired() {
	return getExternalResourcesRequiredSupport().
            getExternalResourcesRequired(this);
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
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGStringList getRequiredExtensions() {
	return getTestsSupport().getRequiredExtensions(this);
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
     * org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension) {
	return getTestsSupport().hasExtension(extension, this);
    }
}
