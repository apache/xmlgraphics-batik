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
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGCursorElement;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class implements {@link org.w3c.dom.svg.SVGCursorElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMCursorElement
    extends    SVGOMURIReferenceElement
    implements SVGCursorElement {

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
     * Creates a new SVGOMCursorElement object.
     */
    protected SVGOMCursorElement() {
    }

    /**
     * Creates a new SVGOMCursorElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMCursorElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CURSOR_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCursorElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        return getAnimatedLengthAttribute
            (null, SVG_X_ATTRIBUTE, SVG_CURSOR_X_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCursorElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        return getAnimatedLengthAttribute
            (null, SVG_Y_ATTRIBUTE, SVG_CURSOR_Y_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
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

    // SVGTests support ///////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension) {
        return SVGTestsSupport.hasExtension(this, extension);
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
        return new SVGOMCursorElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)
                    || ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
            } else if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)) {
                return SVGTypes.TYPE_BOOLEAN;
            }
        }
        return super.getAttributeType(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Gets how percentage values are interpreted by the given attribute.
     */
    protected short getAttributePercentageInterpretation(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE)) {
                return PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_Y_ATTRIBUTE)) {
                return PERCENTAGE_VIEWPORT_HEIGHT;
            }
        }
        return super.getAttributePercentageInterpretation(ns, ln);
    }

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
            } else if (ln.equals(SVG_X_ATTRIBUTE)) {
                updateLengthAttributeValue(getX(), val);
                return;
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                updateLengthAttributeValue(getY(), val);
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
            } else if (ln.equals(SVG_X_ATTRIBUTE)) {
                return getBaseValue
                    (getX(), PERCENTAGE_VIEWPORT_WIDTH);
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                return getBaseValue
                    (getY(), PERCENTAGE_VIEWPORT_HEIGHT);
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
