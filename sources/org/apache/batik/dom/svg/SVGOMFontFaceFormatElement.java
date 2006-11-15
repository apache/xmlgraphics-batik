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
import org.w3c.dom.svg.SVGFontFaceFormatElement;

/**
 * This class implements {@link SVGFontFaceFormatElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFontFaceFormatElement
    extends    SVGOMElement
    implements SVGFontFaceFormatElement {

    /**
     * Creates a new SVGOMFontFaceFormatElement object.
     */
    protected SVGOMFontFaceFormatElement() {
    }

    /**
     * Creates a new SVGOMFontFaceFormatElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFontFaceFormatElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FONT_FACE_FORMAT_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFontFaceFormatElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_STRING_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            }
        }
        return super.getAttributeType(ns, ln);
    }
}
