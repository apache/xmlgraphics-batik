/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id$
 */
package org.w3c.flute.util;

import org.w3c.css.sac.CSSException;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public final class CSSUtil {
    // don't create CSSUtil object.
    /**
     * Creates a new CSSUtil
     */
    private CSSUtil() {}

    /**
     * Used to retrieve a string with quotes.
     */    
    public static String getQuotedString(String s) {
	int index1 = s.indexOf('"');
	int index2 = s.indexOf('\'');

	if ((index1 == -1) && (index2 == -1) || (index2 == -1)) {
	    return '"' + s + '"';
	} else if (index1 == -1) {
	    return  '\'' + s + '\'';
	} else {
	    throw new CSSException("invalid CSS string " + s);
	}
    }
}
