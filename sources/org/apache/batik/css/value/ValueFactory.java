/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMStyleDeclaration;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;

/**
 * This interface represents objects that create the value associated
 * with a managed CSS property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ValueFactory {

    /**
     * Sets the parser used by this factory.
     */
    void setParser(Parser p);

    /**
     * Creates a value from its text representation
     * @param text The text that represents the CSS value to create.
     */
    ImmutableValue createValue(String text) throws DOMException;

    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    ImmutableValue createValue(LexicalUnit lu) throws DOMException;

    /**
     * Returns the name of the property handled.
     */
    String getPropertyName();
    
    /**
     * Creates a CSS value from a lexical unit and a style declaration,
     * and put it in the style declaration.
     * @param lu  The SAC lexical unit used to create the value.
     * @param d   The style declaration in which to add the created value.
     * @param imp The property priority.
     */
    void createCSSValue(LexicalUnit lu,
			CSSOMStyleDeclaration d,
			String imp) throws DOMException;
    
    /**
     * Creates and returns a new float value.
     * @param unitType    A unit code as defined above. The unit code can only 
     *                    be a float unit type
     * @param floatValue  The new float value. 
     */
    ImmutableValue createFloatValue(short unitType, float floatValue)
	throws DOMException;

    /**
     * Creates and returns a new string value.
     * @param type   A string code as defined in CSSPrimitiveValue. The string
     *               code can only be a string unit type.
     * @param value  The new string value. 
     */
    ImmutableValue createStringValue(short type, String value)
        throws DOMException;
}
