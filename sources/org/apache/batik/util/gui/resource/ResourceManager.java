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

    public int getCharacter(String key)
        throws MissingResourceException, ResourceFormatException {
        String s = getString(key);
        
        if(s == null || s.length() == 0){
            throw new ResourceFormatException("Malformed character",
                                              bundle.getClass().getName(),
                                              key);
        }

        return s.charAt(0);
    }

}
