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
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimateMotionElement;

/**
 * This class implements {@link SVGAnimateMotionElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimateMotionElement
    extends    SVGOMAnimationElement
    implements SVGAnimateMotionElement {

    /**
     * The attribute initializer.
     */
    protected static final AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_CALC_MODE_ATTRIBUTE,
                                          SVG_PACED_VALUE);
    }

    /**
     * Creates a new SVGOMAnimateMotionElement object.
     */
    protected SVGOMAnimateMotionElement() {
    }

    /**
     * Creates a new SVGOMAnimateMotionElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMAnimateMotionElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_ANIMATE_MOTION_TAG;
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
        return new SVGOMAnimateMotionElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_ACCUMULATE_ATTRIBUTE)
                    || ln.equals(SVG_ADDITIVE_ATTRIBUTE)
                    || ln.equals(SVG_ATTRIBUTE_TYPE_ATTRIBUTE)
                    || ln.equals(SVG_CALC_MODE_ATTRIBUTE)
                    || ln.equals(SVG_FILL_ATTRIBUTE)
                    || ln.equals(SVG_ORIGIN_ATTRIBUTE)
                    || ln.equals(SVG_RESTART_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_ATTRIBUTE_NAME_ATTRIBUTE)
                    || ln.equals(SVG_BY_ATTRIBUTE)
                    || ln.equals(SVG_FROM_ATTRIBUTE)
                    || ln.equals(SVG_MAX_ATTRIBUTE)
                    || ln.equals(SVG_MIN_ATTRIBUTE)
                    || ln.equals(SVG_TO_ATTRIBUTE)
                    || ln.equals(SVG_VALUES_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_BEGIN_ATTRIBUTE)
                    || ln.equals(SVG_END_ATTRIBUTE)) {
                return SVGTypes.TYPE_TIMING_SPECIFIER_LIST;
            } else if (ln.equals(SVG_DUR_ATTRIBUTE)
                    || ln.equals(SVG_REPEAT_DUR_ATTRIBUTE)) {
                return SVGTypes.TYPE_TIME;
            } else if (ln.equals(SVG_KEY_SPLINES_ATTRIBUTE)
                    || ln.equals(SVG_KEY_TIMES_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_LIST;
            } else if (ln.equals(SVG_D_ATTRIBUTE)) {
                return SVGTypes.TYPE_PATH_DATA;
            } else if (ln.equals(SVG_ROTATE_ATTRIBUTE)) {
                return SVGTypes.TYPE_ANGLE_OR_IDENT;
            } else if (ln.equals(SVG_REPEAT_COUNT_ATTRIBUTE)) {
                return SVGTypes.TYPE_INTEGER;
            }
        }
        return super.getAttributeType(ns, ln);
    }
}
