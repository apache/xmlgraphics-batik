/*

   Copyright 2001-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.values.AnimatableAngleOrIdentValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGMarkerElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGMarkerElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMMarkerElement
    extends    SVGStylableElement
    implements SVGMarkerElement {
    
    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                          "xMidYMid meet");
    }

    /**
     * The units values.
     */
    protected final static String[] UNITS_VALUES = {
        "",
        SVG_USER_SPACE_ON_USE_VALUE,
        SVG_STROKE_WIDTH_ATTRIBUTE
    };

    /**
     * The orient type values.
     */
    protected final static String[] ORIENT_TYPE_VALUES = {
        "",
        SVG_AUTO_VALUE,
        ""
    };

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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_MARKER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getRefX()}.
     */
    public SVGAnimatedLength getRefX() {
        return getAnimatedLengthAttribute
            (null, SVG_REF_X_ATTRIBUTE, SVG_MARKER_REF_X_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getRefY()}.
     */
    public SVGAnimatedLength getRefY() {
        return getAnimatedLengthAttribute
            (null, SVG_REF_Y_ATTRIBUTE, SVG_MARKER_REF_Y_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, false);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerUnits()}.
     */
    public SVGAnimatedEnumeration getMarkerUnits() {
        return getAnimatedEnumerationAttribute
            (null, SVG_MARKER_UNITS_ATTRIBUTE, UNITS_VALUES,
             (short)2);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerWidth()}.
     */
    public SVGAnimatedLength getMarkerWidth() {
        return getAnimatedLengthAttribute
            (null, SVG_MARKER_WIDTH_ATTRIBUTE,
             SVG_MARKER_MARKER_WIDTH_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH, true);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getMarkerHeight()}.
     */
    public SVGAnimatedLength getMarkerHeight() {
        return getAnimatedLengthAttribute
            (null, SVG_MARKER_HEIGHT_ATTRIBUTE,
             SVG_MARKER_MARKER_HEIGHT_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH, true);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getOrientType()}.
     */
    public SVGAnimatedEnumeration getOrientType() {
        SVGOMAnimatedMarkerOrientValue orient =
            (SVGOMAnimatedMarkerOrientValue)
            getLiveAttributeValue(null, SVG_ORIENT_ATTRIBUTE);
        if (orient == null) {
            orient = new SVGOMAnimatedMarkerOrientValue(this, null,
                                                        SVG_ORIENT_ATTRIBUTE);
            putLiveAttributeValue(null, SVG_ORIENT_ATTRIBUTE, orient);
        }
        return orient.getAnimatedEnumeration();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#getOrientAngle()}.
     */
    public SVGAnimatedAngle getOrientAngle() {
        SVGOMAnimatedMarkerOrientValue orient =
            (SVGOMAnimatedMarkerOrientValue)
            getLiveAttributeValue(null, SVG_ORIENT_ATTRIBUTE);
        if (orient == null) {
            orient = new SVGOMAnimatedMarkerOrientValue(this, null,
                                                        SVG_ORIENT_ATTRIBUTE);
            putLiveAttributeValue(null, SVG_ORIENT_ATTRIBUTE, orient);
        }
        return orient.getAnimatedAngle();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGMarkerElement#setOrientToAuto()}.
     */
    public void setOrientToAuto() {
        setAttributeNS(null, SVG_ORIENT_ATTRIBUTE, SVG_AUTO_VALUE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGMarkerElement#setOrientToAngle(SVGAngle)}.
     */
    public void setOrientToAngle(SVGAngle angle) {
        setAttributeNS(null, SVG_ORIENT_ATTRIBUTE, angle.getValueAsString());
    }

    // SVGFitToViewBox support ////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getViewBox()}.
     */
    public SVGAnimatedRect getViewBox() {
	throw new RuntimeException(" !!! TODO: getViewBox()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFitToViewBox#getPreserveAspectRatio()}.
     */
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return SVGPreserveAspectRatioSupport.getPreserveAspectRatio(this);
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
        setAttributeNS(XML_NAMESPACE_URI, XML_LANG_QNAME, lang);
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
        setAttributeNS(XML_NAMESPACE_URI, XML_SPACE_QNAME, space);
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
        return new SVGOMMarkerElement();
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)
                    || ln.equals(SVG_REF_X_ATTRIBUTE)
                    || ln.equals(SVG_REF_Y_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_UNITS_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_WIDTH_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_HEIGHT_ATTRIBUTE)
                    || ln.equals(SVG_ORIENT_ATTRIBUTE)
                    || ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
                return true;
            }
        }
        return super.isAttributeAnimatable(ns, ln);
    }

    /**
     * Gets how percentage values are interpreted by the given attribute.
     */
    protected int getAttributePercentageInterpretation(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_REF_X_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_WIDTH_ATTRIBUTE)) {
                return AnimationTarget.PERCENTAGE_VIEWPORT_WIDTH;
            }
            if (ln.equals(SVG_REF_Y_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_HEIGHT_ATTRIBUTE)) {
                return AnimationTarget.PERCENTAGE_VIEWPORT_HEIGHT;
            }
        }
        return super.getAttributePercentageInterpretation(ns, ln);
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_MARKER_HEIGHT_ATTRIBUTE)
                    || ln.equals(SVG_MARKER_WIDTH_ATTRIBUTE)
                    || ln.equals(SVG_REF_X_ATTRIBUTE)
                    || ln.equals(SVG_REF_Y_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
            } else if (ln.equals(SVG_ORIENT_ATTRIBUTE)) {
                return SVGTypes.TYPE_ANGLE_OR_IDENT;
            } else if (ln.equals(SVG_MARKER_UNITS_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_VIEW_BOX_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
            } else if (ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
                return SVGTypes.TYPE_PRESERVE_ASPECT_RATIO_VALUE;
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
            } else if (ln.equals(SVG_REF_X_ATTRIBUTE)) {
                updateLengthAttributeValue(getRefX(), val);
                return;
            } else if (ln.equals(SVG_REF_Y_ATTRIBUTE)) {
                updateLengthAttributeValue(getRefY(), val);
                return;
            } else if (ln.equals(SVG_MARKER_UNITS_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getMarkerUnits(), val);
                return;
            } else if (ln.equals(SVG_MARKER_WIDTH_ATTRIBUTE)) {
                updateLengthAttributeValue(getMarkerWidth(), val);
                return;
            } else if (ln.equals(SVG_MARKER_HEIGHT_ATTRIBUTE)) {
                updateLengthAttributeValue(getMarkerHeight(), val);
                return;
            } else if (ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
                updatePreserveAspectRatioAttributeValue
                    (getPreserveAspectRatio(), val);
                return;
            } else if (ln.equals(SVG_ORIENT_ATTRIBUTE)) {
                // XXX Needs testing.  Esp with the LiveAttributeValues updating
                //     the DOM attributes.
                SVGOMAnimatedMarkerOrientValue orient =
                    (SVGOMAnimatedMarkerOrientValue)
                    getLiveAttributeValue(null, ln);
                if (val == null) {
                    orient.resetAnimatedValue();
                } else {
                    AnimatableAngleOrIdentValue aloiv =
                        (AnimatableAngleOrIdentValue) val;
                    if (aloiv.isIdent()
                            && aloiv.getIdent().equals(SVG_AUTO_VALUE)) {
                        orient.setAnimatedValueToAuto();
                    } else {
                        orient.setAnimatedValueToAngle(aloiv.getUnit(),
                                                       aloiv.getValue());
                    }
                }
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
