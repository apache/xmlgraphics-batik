/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

/**
 * Signals a format error in a resource bundle
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ResourceFormatException extends RuntimeException {
    /**
     * The class name of the resource bundle requested
     * @serial
     */
    protected String className;

    /**
     * The name of the specific resource requested by the user
     * @serial
     */
    protected String key;

    /**
     * Constructs a ResourceFormatException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param classname the name of the resource class
     * @param key the key for the malformed resource.
     */
    public ResourceFormatException(String s, String className, String key) {
        super(s);
        this.className = className;
        this.key = key;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns a printable representation of this object
     */
    public String toString() {
	return super.toString()+" ("+getKey()+", bundle: "+getClassName()+")";
    }
}
