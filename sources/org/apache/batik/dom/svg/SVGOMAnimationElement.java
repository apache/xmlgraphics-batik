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

import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class provides an implementation of the {@link SVGAnimationElement} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMAnimationElement
    extends SVGOMElement
    implements SVGAnimationElement {
    
    /**
     * Creates a new SVGOMAnimationElement.
     */
    protected SVGOMAnimationElement() {
    }

    /**
     * Creates a new SVGOMAnimationElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMAnimationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getTargetElement()}.
     */
    public SVGElement getTargetElement() {
        return ((SVGAnimationContext) getSVGContext()).getTargetElement();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getStartTime()}.
     */
    public float getStartTime() {
        return ((SVGAnimationContext) getSVGContext()).getStartTime();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getCurrentTime()}.
     */
    public float getCurrentTime() {
        return ((SVGAnimationContext) getSVGContext()).getCurrentTime();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getSimpleDuration()}.
     */
    public float getSimpleDuration() throws DOMException {
        float dur = ((SVGAnimationContext) getSVGContext()).getSimpleDuration();
        if (dur == TimedElement.INDEFINITE) {
            throw createDOMException(DOMException.NOT_SUPPORTED_ERR,
                                     "animation.dur.indefinite",
                                     null);
        }
        return dur;
    }

    // ElementTimeControl ////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElement()}.
     */
    public boolean beginElement() throws DOMException {
        return ((SVGAnimationContext) getSVGContext()).beginElement();
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElementAt(float)}.
     */
    public boolean beginElementAt(float offset) throws DOMException {
        return ((SVGAnimationContext) getSVGContext()).beginElementAt(offset);
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElement()}.
     */
    public boolean endElement() throws DOMException {
        return ((SVGAnimationContext) getSVGContext()).endElement();
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElementAt(float)}.
     */
    public boolean endElementAt(float offset) throws DOMException {
        return ((SVGAnimationContext) getSVGContext()).endElementAt(offset);
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
            if (ln.equals(SVG_TEXT_LENGTH_ATTRIBUTE)) {
                return SVGTypes.TYPE_LENGTH;
            } else if (ln.equals(SVG_LENGTH_ADJUST_ATTRIBUTE)) {
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
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
