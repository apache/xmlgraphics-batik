/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

/**
 * An hight level interface that represents a factory allowing
 * to create instances of <code>Interpreter</code> interface implementation.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface InterpreterFactory {
    /**
     * This method should create an instance of <code>Interpreter</code>
     * interface implementation.
     */
    public Interpreter createInterpreter();
}
