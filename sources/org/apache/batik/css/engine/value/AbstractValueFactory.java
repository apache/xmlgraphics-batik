/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.css.engine.value;

import java.net.URL;

import org.apache.batik.util.ParsedURL;
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
        return new ParsedURL(base, value).toString();
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
