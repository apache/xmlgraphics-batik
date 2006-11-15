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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGURIReference;

/**
 * This class implements both {@link org.w3c.dom.svg.SVGTextPositioningElement}
 * and {@link SVGURIReference}..
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGURIReferenceTextPositioningElement
    extends    SVGOMTextPositioningElement
    implements SVGURIReference {

    /**
     * Creates a new SVGURIReferenceTextPositioningElement object.
     */
    protected SVGURIReferenceTextPositioningElement() {
    }

    /**
     * Creates a new SVGURIReferenceTextPositioningElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGURIReferenceTextPositioningElement(String prefix,
                                                    AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
    }
}
