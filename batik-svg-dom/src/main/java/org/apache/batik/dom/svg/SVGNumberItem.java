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

import org.w3c.dom.svg.SVGNumberList;

/**
 * Representation of the item SVGNumber.
 */
public class SVGNumberItem extends AbstractSVGNumber implements SVGItem {

    /**
     * The {@link SVGNumberList} this item belongs to.
     */
    protected AbstractSVGList parentList;

    /**
     * Creates a new SVGNumberItem.
     */
    public SVGNumberItem(float value) {
        this.value = value;
    }

    /**
     * Returns a String representation of the number.
     */
    public String getValueAsString() {
        return Float.toString(value);
    }

    /**
     * Associates this item to the given {@link SVGNumberList}.
     */
    public void setParent(AbstractSVGList list) {
        parentList = list;
    }

    /**
     * Returns the list the item belongs to.
     */
    public AbstractSVGList getParent() {
        return parentList;
    }

    /**
     * Notifies the parent {@link SVGNumberList} that this item's value
     * has changed.
     */
    protected void reset() {
        if (parentList != null) {
            parentList.itemChanged();
        }
    }
}
