/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class represents immutable CSS float values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableFloat extends AbstractImmutablePrimitiveValue {
    /**
     * The unit types representations
     */
    protected final static String[] UNITS = {
        "", "%", "em", "es", "px", "cm", "mm", "in", "pt",
        "pc", "deg", "rad", "grad", "ms", "s", "Hz", "kHz", ""
    };

    /**
     * The float value
     */
    protected float floatValue;

    /**
     * The unit type
     */
    protected short unitType;

    /**
     * Creates a new value.
     */
    public ImmutableFloat(short unitType, float floatValue) {
	this.unitType   = unitType;
	this.floatValue = floatValue;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableFloat)) {
	    return false;
	}
	ImmutableFloat v = (ImmutableFloat)obj;
	return floatValue == v.getFloatValue(unitType);
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return this;
    }

    /**
     * The type of the value as defined by the constants specified in
     * CSSPrimitiveValue.
     */
    public short getPrimitiveType() {
	return unitType;
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
        String s = "" + floatValue;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
	return s + getUnitRepresentation(unitType);
    }

    /**
     *  This method is used to get a float value in a specified unit.
     */
    public float getFloatValue(short unitType) throws DOMException {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_NUMBER:
	case CSSPrimitiveValue.CSS_PERCENTAGE:
	case CSSPrimitiveValue.CSS_EMS:
	case CSSPrimitiveValue.CSS_EXS:
	case CSSPrimitiveValue.CSS_DIMENSION:
	case CSSPrimitiveValue.CSS_PX:
	    if (this.unitType == unitType) {
		return floatValue;
	    }
	    break;
	case CSSPrimitiveValue.CSS_CM:
	    return toCentimeters();
	case CSSPrimitiveValue.CSS_MM:
	    return toMillimeters();
	case CSSPrimitiveValue.CSS_IN:
	    return toInches();
	case CSSPrimitiveValue.CSS_PT:
	    return toPoints();
	case CSSPrimitiveValue.CSS_PC:
	    return toPicas();
	case CSSPrimitiveValue.CSS_DEG:
	    return toDegrees();
	case CSSPrimitiveValue.CSS_RAD:
	    return toRadians();
	case CSSPrimitiveValue.CSS_GRAD:
	    return toGradians();
	case CSSPrimitiveValue.CSS_MS:
	    return toMilliseconds();
	case CSSPrimitiveValue.CSS_S:
	    return toSeconds();
	case CSSPrimitiveValue.CSS_HZ:
	    return toHertz();
	case CSSPrimitiveValue.CSS_KHZ:
	    return tokHertz();
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.float.unit",
	     new Object[] { new Integer(unitType) });
    }
    
    /**
     * Returns the representation for the given unit type.
     * @param unitType The unit type like specified in the CSSPrimitiveValue
     *                 interface.
     */
    protected static String getUnitRepresentation(short unitType)
        throws DOMException {
        if (unitType < CSSPrimitiveValue.CSS_NUMBER &&
            unitType > CSSPrimitiveValue.CSS_DIMENSION) {
            throw CSSDOMExceptionFactory.createDOMException
                (DOMException.INVALID_ACCESS_ERR,
                 "out.of.range.float.type",
                 new Object[] { new Integer(unitType) });
        }
        return UNITS[unitType - CSSPrimitiveValue.CSS_NUMBER];
    }

    /**
     * Converts the current value into centimeters.
     */
    protected float toCentimeters() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_CM:
	    return floatValue;
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(floatValue / 10);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(floatValue * 2.54);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(floatValue * 2.54 / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(floatValue * 2.54 / 6);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into inches.
     */
    protected float toInches() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(floatValue / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(floatValue / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return floatValue;
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(floatValue / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float) (floatValue / 6);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into millimeters.
     */
    protected float toMillimeters() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(floatValue * 10);
	case CSSPrimitiveValue.CSS_MM:
	    return floatValue;
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(floatValue * 25.4);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(floatValue * 25.4 / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(floatValue * 25.4 / 6);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into points.
     */
    protected float toPoints() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(floatValue * 72 / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(floatValue * 72 / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(floatValue * 72);
	case CSSPrimitiveValue.CSS_PT:
	    return floatValue;
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(floatValue * 12);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into picas.
     */
    protected float toPicas() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(floatValue * 6 / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(floatValue * 6 / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(floatValue * 6);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(floatValue / 12);
	case CSSPrimitiveValue.CSS_PC:
	    return floatValue;
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into degrees.
     */
    protected float toDegrees() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_DEG:
	    return floatValue;
	case CSSPrimitiveValue.CSS_RAD:
	    return (float)(floatValue * 180 / Math.PI);
	case CSSPrimitiveValue.CSS_GRAD:
	    return (float)(floatValue * 9 / 5);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into radians.
     */
    protected float toRadians() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_DEG:
	    return (float)(floatValue * 5 / 9);
	case CSSPrimitiveValue.CSS_RAD:
	    return floatValue;
	case CSSPrimitiveValue.CSS_GRAD:
	    return (float)(floatValue * 100 / Math.PI);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into gradians.
     */
    protected float toGradians() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_DEG:
	    return (float)(floatValue * Math.PI / 180);
	case CSSPrimitiveValue.CSS_RAD:
	    return (float)(floatValue * Math.PI / 100);
	case CSSPrimitiveValue.CSS_GRAD:
	    return floatValue;
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into milliseconds.
     */
    protected float toMilliseconds() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_MS:
	    return floatValue;
	case CSSPrimitiveValue.CSS_S:
	    return (float)(floatValue * 1000);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }
	
    /**
     * Converts the current value into seconds.
     */
    protected float toSeconds() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_MS:
	    return (float)(floatValue / 1000);
	case CSSPrimitiveValue.CSS_S:
	    return floatValue;
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }
	
    /**
     * Converts the current value into Hertz.
     */
    protected float toHertz() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_HZ:
	    return floatValue;
	case CSSPrimitiveValue.CSS_KHZ:
	    return (float)(floatValue / 1000);
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }

    /**
     * Converts the current value into kHertz.
     */
    protected float tokHertz() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_HZ:
	    return (float)(floatValue * 1000);
	case CSSPrimitiveValue.CSS_KHZ:
	    return floatValue;
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.conversion",
		 new Object[] { new Integer(unitType) });
	}
    }
}
