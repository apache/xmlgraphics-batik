/*

   Copyright 2000-2001,2003,2006  The Apache Software Foundation 

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

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.batik.parser.ParseException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

/**
 * This class is the implementation of the {@link SVGAnimatedNumberList}
 * interface.
 *
 * @author <a href="mailto:tonny@kiyut.com">Tonny Kohar</a>
 */
public class SVGOMAnimatedNumberList 
    extends AbstractSVGAnimatedValue
    implements SVGAnimatedNumberList {
    
    /**
     * The base value.
     */
    protected BaseSVGNumberList baseVal;

    /**
     * The animated value.
     */
    protected AnimSVGNumberList animVal;

    /**
     * Whether the list is changing.
     */
    protected boolean changing;

    /**
     * Default value for the number list.
     */
    protected String defaultValue;

    /**
     * Creates a new SVGOMAnimatedNumberList.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param defaultValue The default value if the attribute is not specified.
     */
    public SVGOMAnimatedNumberList(AbstractElement elt,
                                   String ns,
                                   String ln,
                                   String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }
    
    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedNumberList#getBaseVal()}.
     */
    public SVGNumberList getBaseVal() {
        if (baseVal == null) {
            baseVal = new BaseSVGNumberList();
        }
        return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedNumberList#getAnimVal()}.
     */
    public SVGNumberList getAnimVal() {
        if (animVal == null) {
            animVal = new AnimSVGNumberList();
        }
        return animVal;
    }

    /**
     * Sets the animated value.
     */
    public void setAnimatedValue(float[] values) {
        if (animVal == null) {
            animVal = new AnimSVGNumberList();
        }
        hasAnimVal = true;
        animVal.setAnimatedValue(values);
        fireAnimatedAttributeListeners();
    }

    /**
     * Resets the animated value.
     */
    public void resetAnimatedValue() {
        hasAnimVal = false;
        fireAnimatedAttributeListeners();
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
        // XXX Notify baseVal listeners (if we need them).
        if (!hasAnimVal) {
            fireAnimatedAttributeListeners();
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
        // XXX Notify baseVal listeners (if we need them).
        if (!hasAnimVal) {
            fireAnimatedAttributeListeners();
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
        // XXX Notify baseVal listeners (if we need them).
        if (!hasAnimVal) {
            fireAnimatedAttributeListeners();
        }
    }

    /**
     * {@link SVGNumberList} implementation for the base number list value.
     */
    public class BaseSVGNumberList extends AbstractSVGNumberList {

        /**
         * Create a DOMException.
         */
        protected DOMException createDOMException(short type, String key,
                                                  Object[] args) {
            return element.createDOMException(type, key, args);
        }

        /**
         * Create a SVGException.
         */
        protected SVGException createSVGException(short type, String key,
                                                  Object[] args) {

            return ((SVGOMElement)element).createSVGException(type, key, args);
        }

        /**
         * Returns the element owning the attribute with which this length
         * list is associated.
         */
        protected Element getElement() {
            return element;
        }

        /**
         * Returns the value of the DOM attribute containing the number list.
         */
        protected String getValueAsString() {
            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
            if (attr == null) {
                return defaultValue;
            }
            return attr.getValue();
        }

        /**
         * Sets the DOM attribute value containing the number list.
         */
        protected void setAttributeValue(String value) {
            try {
                changing = true;
                element.setAttributeNS(namespaceURI, localName, value);
            } finally {
                changing = false;
            }
        }

        /**
         * Initializes the list, if needed.
         */
        protected void revalidate() {
            if (valid) {
                return;
            }

            String s = getValueAsString();
            if (s == null) {
                throw new LiveAttributeException(element, localName, true,
                                                 null);
            }
            try {
                ListBuilder builder = new ListBuilder();

                doParse(s, builder);

                if (builder.getList() != null) {
                    clear(itemList);
                }
                itemList = builder.getList();
            } catch (ParseException e) {
                itemList = new ArrayList(1);
                valid = true;
                throw new LiveAttributeException(element, localName, false, s);
            }
            valid = true;
        }
    }

    /**
     * {@link SVGNumberList} implementation for the base point list value.
     */
    protected class AnimSVGNumberList extends AbstractSVGNumberList {

        /**
         * Creates a new AnimSVGNumberList.
         */
        public AnimSVGNumberList() {
            itemList = new ArrayList(1);
        }

        /**
         * Create a DOMException.
         */
        protected DOMException createDOMException(short type, String key,
                                                  Object[] args) {
            return element.createDOMException(type, key, args);
        }

        /**
         * Create a SVGException.
         */
        protected SVGException createSVGException(short type, String key,
                                                  Object[] args) {

            return ((SVGOMElement)element).createSVGException(type, key, args);
        }

        /**
         * Returns the element owning this SVGNumberList.
         */
        protected Element getElement() {
            return element;
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#getNumberOfItems()}.
         */
        public int getNumberOfItems() {
            if (hasAnimVal) {
                return super.getNumberOfItems();
            }
            return getBaseVal().getNumberOfItems();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#getItem(int)}.
         */
        public SVGNumber getItem(int index) throws DOMException {
            if (hasAnimVal) {
                return super.getItem(index);
            }
            return getBaseVal().getItem(index);
        }

        /**
         * Returns the value of the DOM attribute containing the point list.
         */
        protected String getValueAsString() {
            if (itemList.size() == 0) {
                return "";
            }
            StringBuffer sb = new StringBuffer();
            Iterator i = itemList.iterator();
            if (i.hasNext()) {
                sb.append(((SVGItem) i.next()).getValueAsString());
            }
            while (i.hasNext()) {
                sb.append(getItemSeparator());
                sb.append(((SVGItem) i.next()).getValueAsString());
            }
            return sb.toString();
        }

        /**
         * Sets the DOM attribute value containing the point list.
         */
        protected void setAttributeValue(String value) {
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#clear()}.
         */
        public void clear() throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#initialize(SVGNumber)}.
         */
        public SVGNumber initialize(SVGNumber newItem)
                throws DOMException, SVGException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * <b>DOM</b>: Implements {@link
         * SVGNumberList#insertItemBefore(SVGNumber, int)}.
         */
        public SVGNumber insertItemBefore(SVGNumber newItem, int index)
                throws DOMException, SVGException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * <b>DOM</b>: Implements {@link
         * SVGNumberList#replaceItem(SVGNumber, int)}.
         */
        public SVGNumber replaceItem(SVGNumber newItem, int index)
                throws DOMException, SVGException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#removeItem(int)}.
         */
        public SVGNumber removeItem(int index) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGNumberList#appendItem(SVGNumber)}.
         */
        public SVGNumber appendItem(SVGNumber newItem) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                 "readonly.number.list", null);
        }

        /**
         * Sets the animated value.
         */
        protected void setAnimatedValue(float[] values) {
            int size = itemList.size();
            int i = 0;
            while (i < size && i < values.length) {
                SVGNumberItem n = (SVGNumberItem) itemList.get(i);
                n.value = values[i];
                i++;
            }
            while (i < values.length) {
                appendItemImpl(new SVGNumberItem(values[i]));
                i++;
            }
            while (size > values.length) {
                removeItemImpl(--size);
            }
        }

        /**
         * Resets the value of the associated attribute.  Does nothing, since
         * there is no attribute for an animated value.
         */
        protected void resetAttribute() {
        }

        /**
         * Resets the value of the associated attribute.  Does nothing, since
         * there is no attribute for an animated value.
         */
        protected void resetAttribute(SVGItem item) {
        }

        /**
         * Initializes the list, if needed.  Does nothing, since there is no
         * attribute to read the list from.
         */
        protected void revalidate() {
            valid = true;
        }
    }
}
