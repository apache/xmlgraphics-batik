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

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGTextPathElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextPathElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMTextPathElement
    extends    SVGOMTextContentElement
    implements SVGTextPathElement {

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
     * The 'method' attribute values.
     */
    protected final static String[] METHOD_VALUES = {
        "",
        SVG_ALIGN_VALUE,
        SVG_STRETCH_VALUE
    };

    /**
     * The 'spacing' attribute values.
     */
    protected final static String[] SPACING_VALUES = {
        "",
        SVG_AUTO_VALUE,
        SVG_EXACT_VALUE
    };

    /**
     * Creates a new SVGOMTextPathElement object.
     */
    protected SVGOMTextPathElement() {
    }

    /**
     * Creates a new SVGOMTextPathElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMTextPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_TEXT_PATH_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPathElement#getStartOffset()}.
     */
    public SVGAnimatedLength getStartOffset() {
        return getAnimatedLengthAttribute
            (null, SVG_START_OFFSET_ATTRIBUTE,
             SVG_TEXT_PATH_START_OFFSET_DEFAULT_VALUE,
             SVGOMAnimatedLength.OTHER_LENGTH, true);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPathElement#getMethod()}.
     */
    public SVGAnimatedEnumeration getMethod() {
        return getAnimatedEnumerationAttribute
            (null, SVG_METHOD_ATTRIBUTE, METHOD_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTextPathElement#getSpacing()}.
     */
    public SVGAnimatedEnumeration getSpacing() {
        return getAnimatedEnumerationAttribute
            (null, SVG_SPACING_ATTRIBUTE, SPACING_VALUES, (short)2);
    }


    // XLink support //////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
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
        return new SVGOMTextPathElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_START_OFFSET_ATTRIBUTE)
                    || ln.equals(SVG_METHOD_ATTRIBUTE)
                    || ln.equals(SVG_SPACING_ATTRIBUTE)) {
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
            if (ln.equals(SVG_METHOD_ATTRIBUTE)
                    || ln.equals(SVG_SPACING_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_START_OFFSET_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
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
            if (ln.equals(SVG_START_OFFSET_ATTRIBUTE)) {
                updateLengthAttributeValue(getStartOffset(), val);
                return;
            } else if (ln.equals(SVG_METHOD_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getMethod(), val);
                return;
            } else if (ln.equals(SVG_SPACING_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getSpacing(), val);
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
            if (ln.equals(SVG_START_OFFSET_ATTRIBUTE)) {
                return getBaseValue
                    (getStartOffset(),
                     PERCENTAGE_VIEWPORT_SIZE);
            } else if (ln.equals(SVG_METHOD_ATTRIBUTE)) {
                return getBaseValue(getMethod());
            } else if (ln.equals(SVG_SPACING_ATTRIBUTE)) {
                return getBaseValue(getSpacing());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
