/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import java.io.StringReader;
import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMStyleDeclaration;
import org.apache.batik.css.CSSOMValue;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a base implementation for every value factories.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractValueFactory
    implements ValueFactory,
               ValueConstants {

    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * To creates a new ValueFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    protected AbstractValueFactory(Parser p) {
	parser = p;
    }

    /**
     * Returns the CSS parser.
     */
    public Parser getParser() {
	return parser;
    }

    /**
     * Creates a value from its text representation
     * @param text The text that represents the CSS value to create.
     */
    public ImmutableValue createValue(String text) throws DOMException {
	try {
	    InputSource is = new InputSource(new StringReader(text));
	    LexicalUnit lu = parser.parsePropertyValue(is);
	    return createValue(lu);
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 e.getMessage(),
		 new Object[] {});
	}
    }

    /**
     * Creates a value from a lexical unit and a style declaration.
     * This method must only be called for null values.
     * @param lu  The SAC lexical unit used to create the value.
     * @param d   The style declaration in which to add the created value.
     * @param imp The property priority.
     */
    public void createCSSValue(LexicalUnit lu,
			       CSSOMStyleDeclaration d,
			       String imp) throws DOMException {
	d.setPropertyCSSValue(getPropertyName(),
			      createCSSValue(createValue(lu)),
			      imp);
    }
    
    /**
     * Creates and returns a new float value.
     * @param unitType    A unit code as defined above. The unit code can only 
     *                    be a float unit type
     * @param floatValue  The new float value. 
     */
    public ImmutableValue createFloatValue(short unitType, float floatValue)
	throws DOMException {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.NOT_SUPPORTED_ERR,
	     "bad.unit.type",
	     new Object[] { new Integer(unitType) });
    }

    /**
     * Creates and returns a new string value.
     * @param type   A string code as defined in CSSPrimitiveValue. The string
     *               code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.NOT_SUPPORTED_ERR,
	     "bad.unit.type",
	     new Object[] { new Integer(type) });
    }

    /**
     * Creates a new CSSValue.
     */
    protected CSSOMValue createCSSValue(ImmutableValue v) {
        return new CSSOMValue(this, v);
    }

    /**
     * To create string values.
     */
    protected class StringFactory extends AbstractValueFactory {
	/**
	 * Creates a new StringFactory object.
	 * @param p The CSS parser used to parse the CSS texts.
	 */
	public StringFactory(Parser p) {
	    super(p);
	}

	/**
	 * Returns the name of the property handled.
	 */
	public String getPropertyName() {
	    return null;
	}
    
	/**
	 * Creates a value from a lexical unit.
	 * @param lu The SAC lexical unit used to create the value.
	 */
	public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	    if (lu.getLexicalUnitType() != LexicalUnit.SAC_STRING_VALUE) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.INVALID_ACCESS_ERR,
		     "invalid.lexical.unit",
		     new Object[] { new Integer(lu.getLexicalUnitType()) });
	    }
	    return new ImmutableString(CSSPrimitiveValue.CSS_STRING,
				       lu.getStringValue());
	}

	/**
	 * Creates and returns a new string value.
	 * @param type  A string code as defined in CSSPrimitiveValue.
         *              The string code can only be a string unit type.
	 * @param value  The new string value. 
	 */
	public ImmutableValue createStringValue(short type, String value)
	    throws DOMException {
	    if (type != CSSPrimitiveValue.CSS_STRING) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.NOT_SUPPORTED_ERR,
		     "bad.unit.type",
		     new Object[] { new Integer(type) });
	    }
	    return new ImmutableString(type, value);
	}

    }

    /**
     * To manage the uri values.
     */
    protected class URIFactory extends AbstractValueFactory {
	/**
	 * Creates a new URIFactory object.
	 * @param p The CSS parser used to parse the CSS texts.
	 */
	public URIFactory(Parser p) {
	    super(p);
	}

	/**
	 * Returns the name of the property handled.
	 */
	public String getPropertyName() {
	    return null;
	}
    
	/**
	 * Creates a value from a lexical unit.
	 * @param lu The SAC lexical unit used to create the value.
	 */
	public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	    if (lu.getLexicalUnitType() != LexicalUnit.SAC_URI) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.INVALID_ACCESS_ERR,
		     "invalid.lexical.unit",
		     new Object[] { new Integer(lu.getLexicalUnitType()) });
	    }
	    return new ImmutableString(CSSPrimitiveValue.CSS_URI,
				       lu.getStringValue());
	}

	/**
	 * Creates and returns a new string value.
	 * @param type   A string code as defined in CSSPrimitiveValue.
         *               The string code can only be a URI unit type.
	 * @param value  The new string value. 
	 */
	public ImmutableValue createStringValue(short type, String value)
	    throws DOMException {
	    if (type != CSSPrimitiveValue.CSS_URI) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.NOT_SUPPORTED_ERR,
		     "bad.unit.type",
		     new Object[] { new Integer(type) });
	    }
	    return new ImmutableString(type, value);
	}
    }
}
