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
     * Class for the Rhino interpreter
     */
    public static final String RHINO_INTERPRETER
        = "org.apache.batik.script.rhino.RhinoInterpreter";

    /**
     * Message when an error happens loading the rhino interpreter.
     * This should *never* happen.
     */
    public static final String EXCEPTION_COULD_NOT_FIND_RHINO_INTERPRETER_CLASS
        = Messages.getString("RhinoInterpreterFactory.exception.could.not.find.rhino.interpreter.class");

    /**
     * Message when an error happens while instantiating the
     * Rhino interpreter. This should *never* happen except under
     * extraordinary conditions.
     */
    public static final String EXCEPTION_WHILE_INSTANTIATING_RHINO_INTERPRETER
        = Messages.getString("RhinoInterpreterFactory.exception.while.instantiating.rhino.interpreter");

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
        if (documentURL != null) {
            // Use a URLClassLoader to let the document scripts 
            // access the URL from where the document was loaded
            URLClassLoader cl = new URLClassLoader(new URL[]{documentURL});

            Class rhinoInterpreterClass = null;
            try {
                rhinoInterpreterClass = cl.loadClass(RHINO_INTERPRETER);
            } catch (ClassNotFoundException e){
                // Installation is flawed: should not continue
                throw new Error(EXCEPTION_COULD_NOT_FIND_RHINO_INTERPRETER_CLASS);
            }
            
            try {
                return (Interpreter)rhinoInterpreterClass.newInstance();
            } catch (InstantiationException ie){
                throw new Error(EXCEPTION_WHILE_INSTANTIATING_RHINO_INTERPRETER);
            } catch (IllegalAccessException iae){
                throw new Error(EXCEPTION_WHILE_INSTANTIATING_RHINO_INTERPRETER);
            } catch (ExceptionInInitializerError eii){
                throw new Error(EXCEPTION_WHILE_INSTANTIATING_RHINO_INTERPRETER);
            } catch (SecurityException se){
                throw new Error(EXCEPTION_WHILE_INSTANTIATING_RHINO_INTERPRETER);
            }
        } else {
            // Return a new RhinoInterpreter which will be 
            // limited to the local sandbox with no possible 
            // connection to the network.
            return new RhinoInterpreter();
        }
    }
}
