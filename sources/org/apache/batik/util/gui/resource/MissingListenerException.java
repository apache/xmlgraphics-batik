/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

/**
 * Signals a missing listener
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class MissingListenerException extends RuntimeException {
    /**
     * The class name of the listener bundle requested
     * @serial
     */
    private String className;

    /**
     * The name of the specific listener requested by the user
     * @serial
     */
    private String key;

    /**
     * Constructs a MissingListenerException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param classname the name of the listener class
     * @param key the key for the missing listener.
     */
    public MissingListenerException(String s, String className, String key) {
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
