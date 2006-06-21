/*

   Copyright 2000-2003  The Apache Software Foundation 

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

import org.w3c.dom.Element;

/**
 * Thrown when a live attribute cannot parse an attribute's value.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class LiveAttributeException extends RuntimeException {

    /**
     * The element on which the error occured.
     */
    protected Element e;

    /**
     * The attribute name.
     */
    protected String attributeName;

    /**
     * Whether the attribute was missing (true) or malformed (false).
     */
    protected boolean missing;

    /**
     * The malformed attribute value.
     */
    protected String value;

    /**
     * Constructs a new <tt>LiveAttributeException</tt> with the specified
     * parameters.
     *
     * @param e the element on which the error occured
     * @param an the attribute name
     * @param missing whether the attribute was missing or malformed
     * @param val the malformed attribute value
     */
    public LiveAttributeException(Element e, String an, boolean missing,
                                  String val) {
        this.e = e;
        this.attributeName = an;
        this.missing = missing;
        this.value = val;
    }

    /**
     * Returns the element on which the error occurred.
     */
    public Element getElement() {
        return e;
    }

    /**
     * Returns the attribute name.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Returns whether the attribute was missing (true) or malformed (false).
     */
    public boolean isMissing() {
        return missing;
    }

    /**
     * Returns the malformed attribute value.
     */
    public String getValue() {
        return value;
    }
}
