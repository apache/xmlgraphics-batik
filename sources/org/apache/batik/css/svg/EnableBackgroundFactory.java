/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractLengthFactory;
import org.apache.batik.css.value.AbstractValueFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.ImmutableValueList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'enable-background' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class EnableBackgroundFactory
    extends    AbstractValueFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_ACCUMULATE_VALUE, ACCUMULATE_VALUE);
	values.put(CSS_NEW_VALUE,        NEW_VALUE);
    }

    /**
     * The length factory.
     */
    protected LengthFactory lengthFactory = new LengthFactory(getParser());

    /**
     * Creates a new EnableBackgroundFactory object.
     */
    public EnableBackgroundFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_ENABLE_BACKGROUND_PROPERTY;
    }

    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
	    Object v = values.get(lu.getStringValue().toLowerCase().intern());
	    if (v == null) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.INVALID_ACCESS_ERR,
		     "invalid.identifier",
		     new Object[] { lu.getStringValue() });
	    }

	    if (v == ACCUMULATE_VALUE) {
            return (ImmutableValue)v;
	    }
	    ImmutableValueList list = new ImmutableValueList(' ');
	    list.append(new CSSOMValue(this, (ImmutableValue)v));
	    for (int i = 0; i < 4; i++) {
		lu = lu.getNextLexicalUnit();
                if (lu == null){
                    if (i == 0) {
                        return list;
                    }
                    throw CSSDOMExceptionFactory.createDOMException
                        (DOMException.INVALID_ACCESS_ERR,
                         "unexpected.end.of.list", null);
                }
		list.append(new CSSOMValue(lengthFactory,
					   lengthFactory.createValue(lu)));
	    }
	    return list;
	default:
            throw CSSDOMExceptionFactory.createDOMException
                (DOMException.INVALID_ACCESS_ERR,
                 "invalid.lexical.unit",
                 new Object[] { new Integer(lu.getLexicalUnitType()) });
	}
    }

    /**
     * Creates and returns a new string value.
     * @param type  A string code as defined in CSSPrimitiveValue. The string
     *   code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	if (!value.equalsIgnoreCase(CSS_ACCUMULATE_VALUE)) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return ACCUMULATE_VALUE;
    }

    /**
     * This class provides a factory for the lengths of the list.
     */
    protected class LengthFactory extends AbstractLengthFactory {
	/**
	 * Creates a new ident factory.
	 */
	public LengthFactory(Parser p) {
	    super(p);
	}

	/**
	 * Returns the name of the property handled.
	 */
	public String getPropertyName() {
	    return "";
	}
    }
}
