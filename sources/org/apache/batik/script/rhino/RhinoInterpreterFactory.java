/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;

/**
 * Allows to create instances of <code>RhinoInterpreter</code> class.
 * 
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class RhinoInterpreterFactory implements InterpreterFactory {
    /**
     * Builds a <code>RhinoInterpreterFactory</code>.
     */
    public RhinoInterpreterFactory() {
    }

    /**
     * Creates an instance of <code>RhinoInterpreter</code> class.
     * 
     * @param documentURL the url for the document which will be scripted
     */
    public Interpreter createInterpreter(URL documentURL) {
        return new RhinoInterpreter(documentURL);
    }
}
