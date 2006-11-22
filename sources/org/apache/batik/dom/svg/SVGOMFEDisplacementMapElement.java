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
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEDisplacementMapElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEDisplacementMapElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDisplacementMapElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEDisplacementMapElement {

    /**
     * The 'xChannelSelector' and 'yChannelSelector' attributes values.
     */
    protected static final String[] CHANNEL_SELECTOR_VALUES = {
        "",
        SVG_R_VALUE,
        SVG_G_VALUE,
        SVG_B_VALUE,
        SVG_A_VALUE
    };

    /**
     * Creates a new SVGOMFEDisplacementMap object.
     */
    protected SVGOMFEDisplacementMapElement() {
    }

    /**
     * Creates a new SVGOMFEDisplacementMapElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEDisplacementMapElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_DISPLACEMENT_MAP_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        return getAnimatedStringAttribute(null, SVG_IN2_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDisplacementMapElement#getScale()}.
     */
    public SVGAnimatedNumber getScale() {
        return getAnimatedNumberAttribute(null, SVG_SCALE_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getXChannelSelector()}.
     */
    public SVGAnimatedEnumeration getXChannelSelector() {
        return getAnimatedEnumerationAttribute
            (null, SVG_X_CHANNEL_SELECTOR_ATTRIBUTE,
             CHANNEL_SELECTOR_VALUES, (short)4);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getYChannelSelector()}.
     */
    public SVGAnimatedEnumeration getYChannelSelector() {
        return getAnimatedEnumerationAttribute
            (null, SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE,
             CHANNEL_SELECTOR_VALUES, (short)4);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDisplacementMapElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_IN2_ATTRIBUTE)
                    || ln.equals(SVG_SCALE_ATTRIBUTE)
                    || ln.equals(SVG_X_CHANNEL_SELECTOR_ATTRIBUTE)
                    || ln.equals(SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE)) {
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
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_IN2_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_X_CHANNEL_SELECTOR_ATTRIBUTE)
                    || ln.equals(SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_SCALE_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                updateStringAttributeValue(getIn1(), val);
                return;
            } else if (ln.equals(SVG_IN2_ATTRIBUTE)) {
                updateStringAttributeValue(getIn2(), val);
                return;
            } else if (ln.equals(SVG_SCALE_ATTRIBUTE)) {
                updateNumberAttributeValue(getScale(), val);
                return;
            } else if (ln.equals(SVG_X_CHANNEL_SELECTOR_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getXChannelSelector(), val);
                return;
            } else if (ln.equals(SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getYChannelSelector(), val);
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return getBaseValue(getIn1());
            } else if (ln.equals(SVG_IN2_ATTRIBUTE)) {
                return getBaseValue(getIn2());
            } else if (ln.equals(SVG_SCALE_ATTRIBUTE)) {
                return getBaseValue(getScale());
            } else if (ln.equals(SVG_X_CHANNEL_SELECTOR_ATTRIBUTE)) {
                return getBaseValue(getXChannelSelector());
            } else if (ln.equals(SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE)) {
                return getBaseValue(getYChannelSelector());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
