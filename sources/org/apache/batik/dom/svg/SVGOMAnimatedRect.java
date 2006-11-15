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

import org.apache.batik.parser.DefaultNumberListHandler;
import org.apache.batik.parser.NumberListParser;
import org.apache.batik.parser.ParseException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGRect;

/**
 * Implementation of {@link SVGAnimatedRect}.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class SVGOMAnimatedRect
        extends AbstractSVGAnimatedValue
        implements SVGAnimatedRect {

    /**
     * The base value.
     */
    protected BaseSVGRect baseVal;

    /**
     * The animated value.
     */
    protected AnimSVGRect animVal;

    /**
     * Whether the value is changing.
     */
    protected boolean changing;

    /**
     * Creates a new SVGOMAnimatedRect.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     */
    public SVGOMAnimatedRect(AbstractElement elt, String ns, String ln) {
        super(elt, ns, ln);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedRect#getBaseVal()}.
     */
    public SVGRect getBaseVal() {
        if (baseVal == null) {
            baseVal = new BaseSVGRect();
        }
        return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedRect#getAnimVal()}.
     */
    public SVGRect getAnimVal() {
        if (animVal == null) {
            animVal = new AnimSVGRect();
        }
        return animVal;
    }

    /**
     * Sets the animated value.
     */
    public void setAnimatedValue(float x, float y, float w, float h) {
        if (animVal == null) {
            animVal = new AnimSVGRect();
        }
        hasAnimVal = true;
        animVal.setAnimatedValue(x, y, w, h);
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
        fireBaseAttributeListeners();
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
        fireBaseAttributeListeners();
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
        fireBaseAttributeListeners();
        if (!hasAnimVal) {
            fireAnimatedAttributeListeners();
        }
    }

    /**
     * This class represents the SVGRect returned by getBaseVal().
     */
    protected class BaseSVGRect extends SVGOMRect {

        /**
         * Whether this rect is valid.
         */
        protected boolean valid;
        
        /**
         * Invalidates this length.
         */
        public void invalidate() {
            valid = false;
        }

        /**
         * Resets the value of the associated attribute.
         */
        protected void reset() {
            try {
                changing = true;
                element.setAttributeNS
                    (namespaceURI, localName,
                     Float.toString(x) + ' ' + y + ' ' + w + ' ' + h);
            } finally {
                changing = false;
            }
        }

        /**
         * Initializes the length, if needed.
         */
        protected void revalidate() {
            if (valid) {
                return;
            }

            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);

            if (attr == null) {
                // XXX What defaults?
                x = 0;
                y = 0;
                w = 100;
                h = 100;
            } else {
                final String s = attr.getValue();
                final float[] numbers = new float[4];
                NumberListParser p = new NumberListParser();
                p.setNumberListHandler(new DefaultNumberListHandler() {
                    protected int count;
                    public void endNumberList() {
                        if (count != 4) {
                            throw new LiveAttributeException
                                (element, localName,
                                 LiveAttributeException.ERR_ATTRIBUTE_MALFORMED,
                                 s);
                        }
                    }
                    public void numberValue(float v) throws ParseException {
                        if (count < 4) {
                            numbers[count] = v;
                        }
                        if (v < 0 && (count == 2 || count == 3)) {
                            throw new LiveAttributeException
                                (element, localName,
                                 LiveAttributeException.ERR_ATTRIBUTE_MALFORMED,
                                 s);
                        }
                        count++;
                    }
                });
                p.parse(s);
                x = numbers[0];
                y = numbers[1];
                w = numbers[2];
                h = numbers[3];
            }

            valid = true;
        }
    }

    /**
     * This class represents the SVGRect returned by getAnimVal().
     */
    protected class AnimSVGRect extends SVGOMRect {

        /**
         * <b>DOM</b>: Implements {@link SVGRect#getX()}.
         */
        public float getX() {
            if (hasAnimVal) {
                return super.getX();
            }
            return getBaseVal().getX();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#getY()}.
         */
        public float getY() {
            if (hasAnimVal) {
                return super.getY();
            }
            return getBaseVal().getY();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#getWidth()}.
         */
        public float getWidth() {
            if (hasAnimVal) {
                return super.getWidth();
            }
            return getBaseVal().getWidth();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#getHeight()}.
         */
        public float getHeight() {
            if (hasAnimVal) {
                return super.getHeight();
            }
            return getBaseVal().getHeight();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#setX(float)}.
         */
        public void setX(float value) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "readonly.length",
                 null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#setY(float)}.
         */
        public void setY(float value) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "readonly.length",
                 null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#setWidth(float)}.
         */
        public void setWidth(float value) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "readonly.length",
                 null);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGRect#setHeight(float)}.
         */
        public void setHeight(float value) throws DOMException {
            throw element.createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "readonly.length",
                 null);
        }

        /**
         * Sets the animated value.
         */
        protected void setAnimatedValue(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
