/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id$
 */
package org.w3c.flute.parser;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
class ThrowedParseException extends RuntimeException {
    ParseException e;

    /**
     * Creates a new ThrowedParseException
     */
    ThrowedParseException(ParseException e) {
        this.e     = e;
    }
}
