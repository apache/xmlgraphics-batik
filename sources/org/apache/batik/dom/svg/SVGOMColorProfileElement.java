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
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGColorProfileElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGColorProfileElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMColorProfileElement
    extends    SVGOMURIReferenceElement
    implements SVGColorProfileElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(5);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_RENDERING_INTENT_ATTRIBUTE,
                                          SVG_AUTO_VALUE);
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
     * Creates a new SVGOMColorProfileElement object.
     */
    protected SVGOMColorProfileElement() {
    }

    /**
     * Creates a new SVGOMColorProfileElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMColorProfileElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_COLOR_PROFILE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGColorProfileElement#getLocal()}.
     */
    public String getLocal() {
        return getAttributeNS(null, SVG_LOCAL_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGColorProfileElement#setLocal(String)}.
     */
    public void setLocal(String local) throws DOMException {
        setAttributeNS(null, SVG_LOCAL_ATTRIBUTE, local);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGColorProfileElement#getName()}.
     */
    public String getName() {
        return getAttributeNS(null, SVG_NAME_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGColorProfileElement#setName(String)}.
     */
    public void setName(String name) throws DOMException {
        setAttributeNS(null, SVG_NAME_ATTRIBUTE, name);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGColorProfileElement#getRenderingIntent()}.
     */
    public short getRenderingIntent() {
        Attr attr = getAttributeNodeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE);
        if (attr == null) {
            return RENDERING_INTENT_AUTO;
        }
        String val = attr.getValue();
        switch (val.length()) {
        case 4:
            if (val.equals(SVG_AUTO_VALUE)) {
                return RENDERING_INTENT_AUTO;
            }
            break;

        case 10:
            if (val.equals(SVG_PERCEPTUAL_VALUE)) {
                return RENDERING_INTENT_PERCEPTUAL;
            }
            if (val.equals(SVG_SATURATE_VALUE)) {
                return RENDERING_INTENT_SATURATION;
            }
            break;

        case 21:
            if (val.equals(SVG_ABSOLUTE_COLORIMETRIC_VALUE)) {
                return RENDERING_INTENT_ABSOLUTE_COLORIMETRIC;
            }
            if (val.equals(SVG_RELATIVE_COLORIMETRIC_VALUE)) {
                return RENDERING_INTENT_RELATIVE_COLORIMETRIC;
            }
        }
        return RENDERING_INTENT_UNKNOWN;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGColorProfileElement#setRenderingIntent(short)}.
     */
    public void setRenderingIntent(short renderingIntent) throws DOMException {
        switch (renderingIntent) {
        case RENDERING_INTENT_AUTO:
            setAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE,
                           SVG_AUTO_VALUE);
            break;

        case RENDERING_INTENT_PERCEPTUAL:
            setAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE,
                           SVG_PERCEPTUAL_VALUE);
            break;

        case RENDERING_INTENT_RELATIVE_COLORIMETRIC:
            setAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE,
                           SVG_RELATIVE_COLORIMETRIC_VALUE);
            break;

        case RENDERING_INTENT_SATURATION:
            setAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE,
                           SVG_SATURATE_VALUE);
            break;

        case RENDERING_INTENT_ABSOLUTE_COLORIMETRIC:
            setAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE,
                           SVG_ABSOLUTE_COLORIMETRIC_VALUE);
        }
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
        return new SVGOMColorProfileElement();
    }
}
