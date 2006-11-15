/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGradientElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMGradientElement
    extends    SVGStylableElement
    implements SVGGradientElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute(XMLSupport.XMLNS_NAMESPACE_URI,
                                          null, "xmlns:xlink",
                                          XLinkSupport.XLINK_NAMESPACE_URI);
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "type", "simple");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "show", "other");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "actuate", "onLoad");
    }

    /**
     * The units values.
     */
    protected final static String[] UNITS_VALUES = {
        "",
        SVG_USER_SPACE_ON_USE_VALUE,
        SVG_OBJECT_BOUNDING_BOX_VALUE
    };

    /**
     * The 'spreadMethod' attribute values.
     */
    protected final static String[] SPREAD_METHOD_VALUES = {
        "",
        SVG_PAD_VALUE,
        SVG_REFLECT_VALUE,
        SVG_REPEAT_VALUE
    };

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
        throw new UnsupportedOperationException
            ("SVGGradientElement.getGradientTransform is not implemented"); // XXX
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientUnits()}.
     */
    public SVGAnimatedEnumeration getGradientUnits() {
        return getAnimatedEnumerationAttribute
            (null, SVG_GRADIENT_UNITS_ATTRIBUTE, UNITS_VALUES,
             (short)2);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getSpreadMethod()}.
     */
    public SVGAnimatedEnumeration getSpreadMethod() {
        return getAnimatedEnumerationAttribute
            (null, SVG_SPREAD_METHOD_ATTRIBUTE, SPREAD_METHOD_VALUES,
             (short)1);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getHref()}.
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
        return new SVGOMAElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)) {
                return true;
            }
        }
        return super.isAttributeAnimatable(ns, ln);
    }

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_GRADIENT_UNITS_ATTRIBUTE)
                    || ln.equals(SVG_SPREAD_METHOD_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_GRADIENT_TRANSFORM_ATTRIBUTE)) {
                return SVGTypes.TYPE_TRANSFORM_LIST;
            } else if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)) {
                return SVGTypes.TYPE_BOOLEAN;
            }
        }
        return super.getAttributeType(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)) {
                updateBooleanAttributeValue(getExternalResourcesRequired(),
                                            val);
                return;
            } else if (ln.equals(SVG_GRADIENT_UNITS_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getGradientUnits(), val);
                return;
            } else if (ln.equals(SVG_GRADIENT_TRANSFORM_ATTRIBUTE)) {
                updateTransformListAttributeValue(getGradientTransform(), val);
                return;
            } else if (ln.equals(SVG_SPREAD_METHOD_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getSpreadMethod(), val);
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }

    /**
     * Returns the underlying value of an animatable XML attribute.
     */
    public AnimatableValue getUnderlyingValue(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)) {
                return getBaseValue(getExternalResourcesRequired());
            } else if (ln.equals(SVG_GRADIENT_UNITS_ATTRIBUTE)) {
                return getBaseValue(getGradientUnits());
            } else if (ln.equals(SVG_GRADIENT_TRANSFORM_ATTRIBUTE)) {
                return getBaseValue(getGradientTransform());
            } else if (ln.equals(SVG_SPREAD_METHOD_ATTRIBUTE)) {
                return getBaseValue(getSpreadMethod());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
