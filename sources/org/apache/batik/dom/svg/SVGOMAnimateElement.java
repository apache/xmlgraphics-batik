/*

   Copyright 2001,2003  The Apache Software Foundation 

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

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimateElement;

/**
 * This class implements {@link SVGAnimateElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimateElement
    extends    SVGOMAnimationElement
    implements SVGAnimateElement {

    /**
     * Creates a new SVGOMAnimateElement object.
     */
    protected SVGOMAnimateElement() {
    }

    /**
     * Creates a new SVGOMAnimateElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMAnimateElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_ANIMATE_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMAnimateElement();
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
            } else if (ln.equals(SVG_REPEAT_COUNT_ATTRIBUTE)) {
                return SVGTypes.TYPE_INTEGER;
            }
        }
        return super.getAttributeType(ns, ln);
    }
}
