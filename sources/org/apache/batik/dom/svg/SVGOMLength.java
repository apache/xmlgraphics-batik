/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.IOException;
import java.io.StringReader;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLength;

/**
 * This class implements {@link org.w3c.dom.svg.SVGLength}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMLength
    implements SVGLength,
	       LiveAttributeValue,
               LengthHandler {

    /**
     * The units representations.
     */
    protected final static String[] UNITS = { "", "", "%", "em", "ex", "px",
					      "cm", "mm", "in", "pt", "pc" };

    /**
     * The value in specified units.
     */
    protected float valueInSpecifiedUnits;

    /**
     * The unit type.
     */
    protected short unitType = SVG_LENGTHTYPE_NUMBER;

    /**
     * The associated attribute modifier.
     */
    protected ModificationHandler modificationHandler;

    /**
     * This length direction.
     */
    protected short direction; // default is UnitProcessor.OTHER_LENGTH

    /**
     * Whether or not the current change is due to an internal change.
     */
    protected boolean internalChange;

    /**
     * Sets the associated attribute modifier.
     */
    public void setModificationHandler(ModificationHandler mh) {
        modificationHandler = mh;
    }

    /**
     * Sets the length direction.
     */
    public void setDirection(short dir) {
        direction = dir;
    }

    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
	if (!internalChange) {
            parseLength(newValue.getValue());
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#getUnitType()}.
     */
    public short getUnitType() {
	return unitType;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#getValue()}.
     */
    public float getValue() {
        try {
            if (modificationHandler == null) {
                switch (unitType) {
                case SVGLength.SVG_LENGTHTYPE_NUMBER:
                case SVGLength.SVG_LENGTHTYPE_PX:
                    return valueInSpecifiedUnits;
                default:
                    throw new RuntimeException("Invalid Unit");
                }
            } else {
                SVGElement elt = (SVGElement)modificationHandler.getObject(null);
                SVGOMDocument doc = (SVGOMDocument)elt.getOwnerDocument();
                UnitProcessor.Context ctx;
                ctx = new DefaultUnitProcessorContext(doc.getSVGContext(), elt);
                return UnitProcessor.svgToUserSpace(unitType,
                                                    valueInSpecifiedUnits,
                                                    elt,
                                                    direction,
                                                    ctx);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new DOMException(DOMException.INVALID_STATE_ERR,
                                   e.getMessage());
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#setValue(float)}.
     */
    public void setValue(float value) throws DOMException {
        try {
            if (modificationHandler == null) {
                switch (unitType) {
                case SVGLength.SVG_LENGTHTYPE_NUMBER:
                case SVGLength.SVG_LENGTHTYPE_PX:
                    valueInSpecifiedUnits = value;
                    break;
                default:
                    throw new RuntimeException("Invalid Unit");
                }
            } else {
                SVGElement elt = (SVGElement)modificationHandler.getObject(null);
                SVGOMDocument doc = (SVGOMDocument)elt.getOwnerDocument();
                UnitProcessor.Context ctx;
                ctx = new DefaultUnitProcessorContext(doc.getSVGContext(), elt);
                setValueInSpecifiedUnits(UnitProcessor.userSpaceToSVG(unitType,
                                                                      value,
                                                                      elt,
                                                                      direction,
                                                                      ctx));
            }
        } catch (RuntimeException e) {
            throw new DOMException(DOMException.INVALID_STATE_ERR,
                                   e.getMessage());
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#getValueInSpecifiedUnits()}.
     */
    public float getValueInSpecifiedUnits() {
	return valueInSpecifiedUnits;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#setValueInSpecifiedUnits(float)}.
     */
    public void setValueInSpecifiedUnits(float value)
	throws DOMException {
        valueInSpecifiedUnits = value;
	if (modificationHandler != null) {
            internalChange = true;
	    modificationHandler.valueChanged(this, value + UNITS[unitType]);
            internalChange = false;
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#getValueAsString()}.
     */
    public String getValueAsString() {
	return valueInSpecifiedUnits + UNITS[unitType];
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#setValueAsString(String)}.
     */
    public void setValueAsString(String valueAsString) throws DOMException {
	if (modificationHandler == null) {
	    parseLength(valueAsString);
	} else {
	    modificationHandler.valueChanged(this, valueAsString);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#newValueSpecifiedUnits(short, float)}.
     */
    public void newValueSpecifiedUnits(short unitType,
                                       float valueInSpecifiedUnits) {
        this.unitType = unitType;
        this.valueInSpecifiedUnits = valueInSpecifiedUnits;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this,
                                             valueInSpecifiedUnits + UNITS[unitType]);
            internalChange = false;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGLength#convertToSpecifiedUnits(short)}.
     */
    public void convertToSpecifiedUnits(short unitType) {
	throw new RuntimeException(" !!! TODO: SVGLength.convertToSpecifiedUnits()");
    }

    /**
     * Parses the given length representation.
     */
    public void parseLength(String text) {
	LengthParser lp = new LengthParser();
	lp.setLengthHandler(this);
	try {
	    lp.parse(new StringReader(text));
	} catch (ParseException e) {
	    throw new DOMException(DOMException.SYNTAX_ERR, e.getMessage());
	}
    }

    // LengthHandler //////////////////////////////////////////////////////

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#startLength()}.
     */
    public void startLength() throws ParseException {
	unitType = SVG_LENGTHTYPE_UNKNOWN;
    }

    /**
     * Implements {@link
     * org.apache.batik.parser.LengthHandler#lengthValue(float)}.
     */
    public void lengthValue(float v) throws ParseException {
	valueInSpecifiedUnits = v;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#em()}.
     */
    public void em() throws ParseException {
	unitType = SVG_LENGTHTYPE_EMS;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#ex()}.
     */
    public void ex() throws ParseException {
	unitType = SVG_LENGTHTYPE_EXS;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#in()}.
     */
    public void in() throws ParseException {
	unitType = SVG_LENGTHTYPE_IN;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#cm()}.
     */
    public void cm() throws ParseException {
	unitType = SVG_LENGTHTYPE_CM;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#mm()}.
     */
    public void mm() throws ParseException {
	unitType = SVG_LENGTHTYPE_MM;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#pc()}.
     */
    public void pc() throws ParseException {
	unitType = SVG_LENGTHTYPE_PC;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#pt()}.
     */
    public void pt() throws ParseException {
	unitType = SVG_LENGTHTYPE_PT;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#px()}.
     */
    public void px() throws ParseException {
	unitType = SVG_LENGTHTYPE_PX;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#percentage()}.
     */
    public void percentage() throws ParseException {
	unitType = SVG_LENGTHTYPE_PERCENTAGE;
    }

    /**
     * Implements {@link org.apache.batik.parser.LengthHandler#endLength()}.
     */
    public void endLength() throws ParseException {
	if (unitType == SVG_LENGTHTYPE_UNKNOWN) {
	    unitType = SVG_LENGTHTYPE_NUMBER;
	}
    }
}
