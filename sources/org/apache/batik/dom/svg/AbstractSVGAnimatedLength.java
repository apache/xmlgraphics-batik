/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;

import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;

/**
 * This class provides an implementation of the {@link
 * SVGAnimatedLength} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractSVGAnimatedLength
    implements SVGAnimatedLength,
               LiveAttributeValue {
    
    /**
     * This constant represents horizontal lengths.
     */
    public final static short HORIZONTAL_LENGTH =
        UnitProcessor.HORIZONTAL_LENGTH;

    /**
     * This constant represents vertical lengths.
     */
    public final static short VERTICAL_LENGTH =
        UnitProcessor.VERTICAL_LENGTH;

    /**
     * This constant represents other lengths.
     */
    public final static short OTHER_LENGTH =
        UnitProcessor.OTHER_LENGTH;

    /**
     * The unit string representations.
     */
    protected final static String[] UNITS = {
        "", "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc"
    };

    /**
     * The associated element.
     */
    protected AbstractElement element;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * This length's direction.
     */
    protected short direction;

    /**
     * The base value.
     */
    protected BaseSVGLength baseVal;

    /**
     * Whether the value is changing.
     */
    protected boolean changing;

    /**
     * Creates a new SVGAnimatedLength.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param dir The length's direction.
     */
    protected AbstractSVGAnimatedLength(AbstractElement elt,
                                        String ns,
                                        String ln,
                                        short dir) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
        direction = dir;
    }

    /**
     * Returns the default value to use when the associated attribute
     * was not specified.
     */
    protected abstract String getDefaultValue();

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedLength#getBaseVal()}.
     */
    public SVGLength getBaseVal() {
        if (baseVal == null) {
            baseVal = new BaseSVGLength();
        }
        return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedLength#getAnimVal()}.
     */
    public SVGLength getAnimVal() {
        throw new RuntimeException("!!! TODO: getAnimVal()");
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * This class represents the SVGLength returned by getBaseVal().
     */
    protected class BaseSVGLength implements SVGLength {

        /**
         * The type of this length.
         */
        protected short unitType;

        /**
         * The value of this length.
         */
        protected float value;

        /**
         * Whether this length is valid.
         */
        protected boolean valid;

        /**
         * The context used to resolve the units.
         */
        protected UnitProcessor.Context context;
        
        /**
         * Creates a new BaseSVGLength.
         */
        public BaseSVGLength() {
            context = new DefaultContext();
        }

        /**
         * Invalidates this length.
         */
        public void invalidate() {
            valid = false;
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#getUnitType()}.
         */
        public short getUnitType() {
            revalidate();
            return unitType;
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#getValue()}.
         */
        public float getValue() {
            revalidate();
            return UnitProcessor.svgToUserSpace(value, unitType,
                                                direction, context);
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#setValue(float)}.
         */
        public void setValue(float value) throws DOMException {
            revalidate();
            this.value = UnitProcessor.userSpaceToSVG(value, unitType,
                                                      direction, context);
            resetAttribute();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#getValueInSpecifiedUnits()}.
         */
        public float getValueInSpecifiedUnits() {
            revalidate();
            return value;
        }

        /**
         * <b>DOM</b>: Implements {@link
         * SVGLength#setValueInSpecifiedUnits(float)}.
         */
        public void setValueInSpecifiedUnits(float value) throws DOMException {
            revalidate();
            this.value = value;
            resetAttribute();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#getValueAsString()}.
         */
        public String getValueAsString() {
            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
            if (attr == null) {
                return getDefaultValue();
            }
            return attr.getValue();
        }

        /**
         * <b>DOM</b>: Implements {@link SVGLength#setValueAsString(String)}.
         */
        public void setValueAsString(String value) throws DOMException {
            element.setAttributeNS(namespaceURI, localName, value);
        }
        
        /**
         * <b>DOM</b>: Implements {@link
         * SVGLength#newValueSpecifiedUnits(short,float)}.
         */
        public void newValueSpecifiedUnits(short unit, float value) {
            unitType = unit;
            this.value = value;
            resetAttribute();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * SVGLength#convertToSpecifiedUnits(short)}.
         */
        public void convertToSpecifiedUnits(short unit) {
            float v = getValue();
            unitType = unit;
            setValue(v);
        }

        /**
         * Resets the value of the associated attribute.
         */
        protected void resetAttribute() {
            try {
                changing = true;
                setValueAsString(value + UNITS[unitType]);
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

            String s = getValueAsString();
            try {
                LengthParser lengthParser = new LengthParser();
                UnitProcessor.UnitResolver ur =
                    new UnitProcessor.UnitResolver();
                lengthParser.setLengthHandler(ur);
                lengthParser.parse(s);
                unitType = ur.unit;
                value = ur.value;
            } catch (ParseException e) {
                unitType = SVG_LENGTHTYPE_UNKNOWN;
                value = 0;
            }
            valid = true;
        }

        /**
         * To resolve the units.
         */
        protected class DefaultContext implements UnitProcessor.Context {

            /**
             * Returns the element.
             */
            public Element getElement() {
                return element;
            }

            /**
             * Returns the size of a px CSS unit in millimeters.
             */
            public float getPixelUnitToMillimeter() {
                SVGContext ctx = ((SVGOMElement)element).getSVGContext();
                return ctx.getPixelUnitToMillimeter();
            }

            /**
             * Returns the size of a px CSS unit in millimeters.
             * This will be removed after next release.
             * @see #getPixelUnitToMillimeter();
             */
            public float getPixelToMM() {
                return getPixelUnitToMillimeter();
            
            }

            /**
             * Returns the font-size value.
             */
            public float getFontSize() {
                return ((SVGOMElement)element).getSVGContext().getFontSize();
            }

            /**
             * Returns the x-height value.
             */
            public float getXHeight() {
                return 0.5f;
            }

            /**
             * Returns the viewport width used to compute units.
             */
            public float getViewportWidth() {
                return ((SVGOMElement)element).getSVGContext().
                    getViewportWidth();
            }

            /**
             * Returns the viewport height used to compute units.
             */
            public float getViewportHeight() {
                return ((SVGOMElement)element).getSVGContext().
                    getViewportHeight();
            }
        }
    }
}
