/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.script;

import org.apache.batik.script.*;
import org.w3c.dom.Document;
import java.util.HashMap;

/**
 * The reference implementation of the <code>InterpreterPool</code> interface.
 * It is able to create <code>Interpreter</code> instances for ECMAScript,
 * Python and Tcl scripting languages if you provide the right jar files in
 * your CLASSPATH (i.e. Rhino, JPython and Jacl jar files).
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class ConcreteInterpreterPool implements InterpreterPool {
    private Document document;
    private HashMap factories = new HashMap(3);
    private HashMap interpreters = new HashMap(1);

    private static final String RHINO =
        "org.apache.batik.refimpl.script.rhino.RhinoInterpreterFactory";

    private static final String JPYTHON =
        "org.apache.batik.refimpl.script.jpython.JPythonInterpreterFactory";

    private static final String JACL =
        "org.apache.batik.refimpl.script.jacl.JaclInterpreterFactory";

    /**
     * Builds an instance of <code>ConcreteInterpreterPool</code>.
     */
    public ConcreteInterpreterPool() {
    }

    /**
     * Builds an instance of <code>ConcreteInterpreterPool</code> that
     * will create <code>Interpreter</code> instances for the the given
     * <code>Document</code>.
     */
    public ConcreteInterpreterPool(Document doc) {
        document = doc;
        InterpreterFactory factory = null;
        try {
            factory =
                (InterpreterFactory)Class.forName(RHINO).newInstance();
            putInterpreterFactory("text/ecmascript",
                                  factory);
        } catch (Throwable t1) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JPYTHON).newInstance();
            putInterpreterFactory("text/python",
                                  factory);
        } catch (Throwable t2) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JACL).newInstance();
            putInterpreterFactory("text/tcl",
                                  factory);
        } catch (Throwable t3) {
            // may append if the class is not in the CLASSPATH
        }
    }

    /**
     * Returns a unique instance of an implementation of
     * <code>Interpreter</code> interface that match the given language.
     * It returns <code>null</code> if the interpreter cannot be build (for
     * example the language is not recognized by this
     * <code>InterpreterPool</code>). The variable "document" in the returned
     * interpreter will reference the instance of <code>Document</code>
     * to which the <code>InterpreterPool</code> is linked.
     * @param language a mimeType like string describing the language to use
     * (i.e. "text/ecmascript" for ECMAScript interpreter, "text/tcl" for Tcl
     * interpreter...).
     */
    public Interpreter getInterpreter(String language) {
        Interpreter interpreter = (Interpreter)interpreters.get(language);
        if (interpreter == null) {
            InterpreterFactory factory = (InterpreterFactory)factories.
                get(language);
            if (factory != null)
                try {
                    interpreter = factory.createInterpreter();
                    if (document != null) {
                        interpreter.bindObject("document", document);
                    }
                    interpreters.put(language, interpreter);
                } catch (Throwable t) {
                    // may append if the class is here but
                    // not the scripting engine jar
                }
        }
        return interpreter;
    }

    /**
     * Registers an <code>InterpreterFactory</code> for the given
     * language. This allows you to add other languages to the default
     * ones or to replace the <code>InterpreterFactory</code> used to create
     * an <code>Interpreter</code> instance for a particular language to
     * be able to have your own interpreter.
     * @param language the language for which the factory is registered.
     * @parma factory the <code>InterpreterFactory</code> that will allow to
     * create a interpreter for the language.
     */
    public void putInterpreterFactory(String language,
                                      InterpreterFactory factory) {
        factories.put(language, factory);
    }

    /**
     * Unregisters the <code>InterpreterFactory</code> of the given
     * language.
     * @param language the language for which the factory should
     * be unregistered.
     */
    public void removeInterpreterFactory(String language) {
        factories.remove(language);
    }
}
