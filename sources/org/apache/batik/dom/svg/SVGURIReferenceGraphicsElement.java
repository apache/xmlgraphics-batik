/*

   Copyright 2000-2001,2003  The Apache Software Foundation 

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

import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides support for Xlink to a graphics element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGURIReferenceGraphicsElement
    extends SVGGraphicsElement {

    /**
     * Creates a new SVGURIReferenceGraphicsElement object.
     */
    protected SVGURIReferenceGraphicsElement() {
    }

    /**
     * Creates a new SVGURIReferenceGraphicsElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGURIReferenceGraphicsElement(String prefix,
                                             AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        return XLINK_NAMESPACE_URI.equals(ns) && XLINK_HREF_ATTRIBUTE.equals(ln)
            || super.isAttributeAnimatable(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        if (XLINK_NAMESPACE_URI.equals(ns)
                && ln.equals(XLINK_HREF_ATTRIBUTE)) {
            SVGOMAnimatedString href = (SVGOMAnimatedString) getHref();
            updateStringAttributeValue(href, val);
        } else {
            super.updateAttributeValue(ns, ln, val);
        }
    }
}
