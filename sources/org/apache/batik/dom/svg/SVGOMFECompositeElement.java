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

import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFECompositeElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFECompositeElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFECompositeElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFECompositeElement,
               OverrideStyleElement,
               ExtendedElementCSSInlineStyle,
               ElementNonCSSPresentationalHints {

    /**
     * The DefaultAttributeValueProducer for operator.
     */
    protected final static DefaultAttributeValueProducer
        OPERATOR_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_COMPOSITE_OPERATOR_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for k1.
     */
    protected final static DefaultAttributeValueProducer K1_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_COMPOSITE_K1_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for k2.
     */
    protected final static DefaultAttributeValueProducer K2_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_COMPOSITE_K2_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for k3.
     */
    protected final static DefaultAttributeValueProducer K3_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_COMPOSITE_K3_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for k4.
     */
    protected final static DefaultAttributeValueProducer K4_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_COMPOSITE_K4_DEFAULT_VALUE;
                }
            };

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected transient WeakReference in2Reference;

    /**
     * The reference to the operator attribute.
     */
    protected transient WeakReference operatorReference;

    /**
     * The reference to the k1 attribute.
     */
    protected transient WeakReference k1Reference;

    /**
     * The reference to the k2 attribute.
     */
    protected transient WeakReference k2Reference;

    /**
     * The reference to the k3 attribute.
     */
    protected transient WeakReference k3Reference;

    /**
     * The reference to the k4 attribute.
     */
    protected transient WeakReference k4Reference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(2);
        values.put(SVG_OPERATOR_ATTRIBUTE, SVG_OVER_VALUE);
        attributeValues.put(null, values);
    }

    // The enumeration maps
    protected final static Map STRING_TO_SHORT_OPERATOR = new HashMap(9);
    protected final static Map SHORT_TO_STRING_OPERATOR = new HashMap(9);
    static {
        STRING_TO_SHORT_OPERATOR.put(SVG_OVER_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_OPERATOR.put(SVG_IN_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_OPERATOR.put(SVG_OUT_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)3));
        STRING_TO_SHORT_OPERATOR.put(SVG_ATOP_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)4));
        STRING_TO_SHORT_OPERATOR.put(SVG_XOR_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)5));
        STRING_TO_SHORT_OPERATOR.put(SVG_ARITHMETIC_VALUE,
                                     SVGOMAnimatedEnumeration.createShort((short)6));

        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                     SVG_OVER_VALUE);
        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                     SVG_IN_VALUE);
        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)3),
                                     SVG_OUT_VALUE);
        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)4),
                                     SVG_ATOP_VALUE);
        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)5),
                                     SVG_XOR_VALUE);
        SHORT_TO_STRING_OPERATOR.put(SVGOMAnimatedEnumeration.createShort((short)6),
                                     SVG_ARITHMETIC_VALUE);
    }

    /**
     * Creates a new SVGOMFECompositeElement object.
     */
    protected SVGOMFECompositeElement() {
    }

    /**
     * Creates a new SVGOMFECompositeElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFECompositeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COMPOSITE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFECompositeElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        SVGAnimatedString result;
        if (inReference == null ||
            (result = (SVGAnimatedString)inReference.get()) == null) {
            result = new SVGOMAnimatedString(this, null, SVG_IN_ATTRIBUTE);
            inReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFECompositeElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        SVGAnimatedString result;
        if (in2Reference == null ||
            (result = (SVGAnimatedString)in2Reference.get()) == null) {
            result = new SVGOMAnimatedString(this, null, SVG_IN2_ATTRIBUTE);
            in2Reference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFECompositeElement#getOperator()}.
     */
    public SVGAnimatedEnumeration getOperator() {
        SVGAnimatedEnumeration result;
        if (operatorReference == null ||
            (result = (SVGAnimatedEnumeration)operatorReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_OPERATOR_ATTRIBUTE,
                                                  STRING_TO_SHORT_OPERATOR,
                                                  SHORT_TO_STRING_OPERATOR,
                                                  OPERATOR_DEFAULT_VALUE_PRODUCER);
            operatorReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK1()}.
     */
    public SVGAnimatedNumber getK1() {
        SVGAnimatedNumber result;
        if (k1Reference == null ||
            (result = (SVGAnimatedNumber)k1Reference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_K1_ATTRIBUTE,
                                             K1_DEFAULT_VALUE_PRODUCER);
            k1Reference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK2()}.
     */
    public SVGAnimatedNumber getK2() {
        SVGAnimatedNumber result;
        if (k2Reference == null ||
            (result = (SVGAnimatedNumber)k2Reference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_K2_ATTRIBUTE,
                                             K2_DEFAULT_VALUE_PRODUCER);
            k2Reference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK3()}.
     */
    public SVGAnimatedNumber getK3() {
        SVGAnimatedNumber result;
        if (k3Reference == null ||
            (result = (SVGAnimatedNumber)k3Reference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_K3_ATTRIBUTE,
                                             K3_DEFAULT_VALUE_PRODUCER);
            k3Reference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK4()}.
     */
    public SVGAnimatedNumber getK4() {
        SVGAnimatedNumber result;
        if (k4Reference == null ||
            (result = (SVGAnimatedNumber)k4Reference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_K4_ATTRIBUTE,
                                             K4_DEFAULT_VALUE_PRODUCER);
            k4Reference = new WeakReference(result);
        }
        return result;
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
     * Returns the default attribute values in a map.
     * @return null if this element has no attribute with a default value.
     */
    protected Map getDefaultAttributeValues() {
        return attributeValues;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFECompositeElement();
    }
}
