/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * This class offers convenience methods to decode
 * resource bundle entries
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ResourceManager {
    /**
     * The managed resource bundle
     */
    protected ResourceBundle bundle;

    /**
     * Creates a new resource manager
     * @param rb a resource bundle
     */
    public ResourceManager(ResourceBundle rb) {
	bundle = rb;
    }

    /**
     * Returns the string that is mapped with the given key
     * @param  key a key in the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     */
    public String getString(String key)
	throws MissingResourceException {
	return bundle.getString(key);
    }

    /**
     * Returns the tokens that compose the string mapped
     * with the given key. Delimiters (" \t\n\r\f") are not returned.
     * @param  key          a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(String key)
	throws MissingResourceException {
        return getStringList(key, " \t\n\r\f", false);
    }
    /**
     * Returns the tokens that compose the string mapped
     * with the given key. Delimiters are not returned.
     * @param  key          a key of the resource bundle
     * @param  delim        the delimiters of the tokens
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(String key, String delim)
	throws MissingResourceException {
        return getStringList(key, delim, false);
    }

    /**
     * Returns the tokens that compose the string mapped
     * with the given key
     * @param  key          a key of the resource bundle
     * @param  delim        the delimiters of the tokens
     * @param  returnDelims if true, the delimiters are returned in the list
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(String key, String delim, boolean returnDelims) 
	throws MissingResourceException {
        List            result = new ArrayList();
        StringTokenizer st     = new StringTokenizer(getString(key),
                                                     delim,
                                                     returnDelims);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    /**
     * Returns the boolean mapped with the given key
     * @param  key a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     * @throws ResourceFormatException if the resource is malformed
     */
    public boolean getBoolean(String key)
	throws MissingResourceException, ResourceFormatException {
	String b = getString(key);

	if (b.equals("true")) {
	    return true;
	} else if (b.equals("false")) {
	    return false;
	} else {
	    throw new ResourceFormatException("Malformed boolean",
                                              bundle.getClass().getName(),
                                              key);
	}
    }

    /**
     * Returns the integer mapped with the given string
     * @param key a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     * @throws ResourceFormatException if the resource is malformed
     */
    public int getInteger(String key)
	throws MissingResourceException, ResourceFormatException {
	String i = getString(key);
	
	try {
	    return Integer.parseInt(i);
	} catch (NumberFormatException e) {
	    throw new ResourceFormatException("Malformed integer",
                                              bundle.getClass().getName(),
                                              key);
	}
    }
}
