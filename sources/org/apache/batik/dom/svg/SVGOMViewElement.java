/*

   Copyright 2001-2003,2006  The Apache Software Foundation 

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

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGViewElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGViewElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMViewElement
    extends SVGOMElement
    implements SVGViewElement {
    
    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(2);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                          "xMidYMid meet");
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_ZOOM_AND_PAN_ATTRIBUTE,
                                          SVG_MAGNIFY_VALUE);
    }

    /**
     * Creates a new SVGOMViewElement object.
     */
    protected SVGOMViewElement() {
    }

    /**
     * Creates a new SVGOMSVGElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMViewElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_VIEW_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGViewElement#getViewTarget()}.
     */
    public SVGStringList getViewTarget() {
        throw new RuntimeException("!!! TODO: getViewTarget()");
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
        return new SVGOMViewElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE)
                    || ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)
                    || ln.equals(SVG_VIEW_TARGET_ATTRIBUTE)) {
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
            if (ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
                return SVGTypes.TYPE_PRESERVE_ASPECT_RATIO_VALUE;
            } else if (ln.equals(SVG_VIEW_BOX_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH_LIST;
            } else if (ln.equals(SVG_VIEW_TARGET_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_ZOOM_AND_PAN_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
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
            } else if (ln.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
                updatePreserveAspectRatioAttributeValue
                    (getPreserveAspectRatio(), val);
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
