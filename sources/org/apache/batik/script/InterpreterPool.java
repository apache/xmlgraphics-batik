/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import org.apache.batik.dom.svg.*;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

/**
 * A class allowing to create/query an {@link
 * org.apache.batik.script.Interpreter} corresponding to a particular
 * <tt>Document</tt> and scripting language.
 *
 * <p>By default, it is able to create interpreters for ECMAScript,
 * Python and Tcl scripting languages if you provide the right jar
 * files in your CLASSPATH (i.e.  Rhino, JPython and Jacl jar
 * files).</p>
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class InterpreterPool {

    /** The InterpreterFactory classname for Rhino. */
    private static final String RHINO =
        "org.apache.batik.script.rhino.RhinoInterpreterFactory";

    /** The InterpreterFactory classname for JPython. */
    private static final String JPYTHON =
        "org.apache.batik.script.jpython.JPythonInterpreterFactory";

    /** The InterpreterFactory classname for Jacl. */
    private static final String JACL =
        "org.apache.batik.script.jacl.JaclInterpreterFactory";

    /**
     * The default InterpreterFactory map.
     */
    protected static Map defaultFactories = new HashMap(7);

    /**
     * The InterpreterFactory map.
     */
    protected Map factories = new HashMap(7);

    static {
        InterpreterFactory factory = null;
        try {
            factory =
                (InterpreterFactory)Class.forName(RHINO).newInstance();
            defaultFactories.put("text/ecmascript", factory);
        } catch (Throwable t1) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JPYTHON).newInstance();
            defaultFactories.put("text/python", factory);
        } catch (Throwable t2) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JACL).newInstance();
            defaultFactories.put("text/tcl", factory);
        } catch (Throwable t3) {
            // may append if the class is not in the CLASSPATH
        }
    }

    /**
     * Constructs a new <tt>InterpreterPool</tt>.
     */
    public InterpreterPool() {
        factories.putAll(defaultFactories);
    }

    /**
     * Creates a new interpreter for the specified document and
     * according to the specified language. This method can return
     * null if no interpreter has been found for the specified
     * language.
     *
     * @param document the document that needs the interpreter
     * @param language the scripting language
     */
    public Interpreter createInterpreter(Document document, String language) {
        InterpreterFactory factory = (InterpreterFactory)factories.get(language);
        Interpreter interpreter = null;
        if (factory != null)
            try {
                interpreter = factory.createInterpreter();
                if (document != null) {
                    interpreter.bindObject("document", document);
                }
            } catch (Throwable t) {
                // may append if the batik interpreters class is here but
                // not the scripting engine jar
            }
        return interpreter;
    }

    /**
     * Adds for the specified language, the specified Interpreter factory.
     *
     * @param language the language for which the factory is registered
     * @param factory the <code>InterpreterFactory</code> to register
     */
    public void putInterpreterFactory(String language, 
                                      InterpreterFactory factory) {
        factories.put(language, factory);
    }

    /**
     * Removes the InterpreterFactory associated to the specified language.
     *
     * @param language the language for which the factory should be removed.
     */
    public void removeInterpreterFactory(String language) {
        factories.remove(language);
    }
}

