/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;

/**
 * This class provides a base implementation for the value factories.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractValueFactory {
    
    /**
     * Returns the name of the property handled.
     */
    public abstract String getPropertyName();
    
    /**
     * Resolves an URI.
     */
    protected static String resolveURI(URL base, String value) {
        try {
            value = new URL(base, value).toString();
        } catch (MalformedURLException e) {
        }
        return value;
    }

    /**
     * Creates a DOM exception, given an invalid identifier.
     */
    protected DOMException createInvalidIdentifierDOMException(String ident) {
        Object[] p = new Object[] { getPropertyName(), ident };
        String s = Messages.formatMessage("invalid.identifier", p);
        return new DOMException(DOMException.SYNTAX_ERR, s);
    }

    /**
     * Creates a DOM exception, given an invalid lexical unit type.
     */
    protected DOMException createInvalidLexicalUnitDOMException(short type) {
        Object[] p = new Object[] { getPropertyName(),
                                    new Integer(type) };
        String s = Messages.formatMessage("invalid.lexical.unit", p);
        return new DOMException(DOMException.NOT_SUPPORTED_ERR, s);
    }

    /**
     * Creates a DOM exception, given an invalid float type.
     */
    protected DOMException createInvalidFloatTypeDOMException(short t) {
        Object[] p = new Object[] { getPropertyName(), new Integer(t) };
        String s = Messages.formatMessage("invalid.float.type", p);
        return new DOMException(DOMException.INVALID_ACCESS_ERR, s);
    }

    /**
     * Creates a DOM exception, given an invalid float value.
     */
    protected DOMException createInvalidFloatValueDOMException(float f) {
        Object[] p = new Object[] { getPropertyName(), new Float(f) };
        String s = Messages.formatMessage("invalid.float.value", p);
        return new DOMException(DOMException.INVALID_ACCESS_ERR, s);
    }

    /**
     * Creates a DOM exception, given an invalid string type.
     */
    protected DOMException createInvalidStringTypeDOMException(short t) {
        Object[] p = new Object[] { getPropertyName(), new Integer(t) };
        String s = Messages.formatMessage("invalid.string.type", p);
        return new DOMException(DOMException.INVALID_ACCESS_ERR, s);
    }

    protected DOMException createMalformedLexicalUnitDOMException() {
        Object[] p = new Object[] { getPropertyName() };
        String s = Messages.formatMessage("malformed.lexical.unit", p);
        return new DOMException(DOMException.INVALID_ACCESS_ERR, s);
    }

    protected DOMException createDOMException() {
        Object[] p = new Object[] { getPropertyName() };
        String s = Messages.formatMessage("invalid.access", p);
        return new DOMException(DOMException.NOT_SUPPORTED_ERR, s);
    }
}
