/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

/**
 * This interface represents an object which is transformed to a
 * function when binded to an interpreter.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface JavaFunction {

    /**
     * Returns the know parameter types.
     */
    Class[] getParameterTypes();

    /**
     * Called by the interpreter. Arguments are casted to the types
     * returned by <code>getParameterTypes()</code>
     */
    Object call(Object[] arguments);
}
