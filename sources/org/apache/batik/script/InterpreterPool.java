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
import java.util.WeakHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

/**
 * A class allowing to create/query an {@link org.apache.batik.script.Interpreter}
 * corresponding to a particular <code>Document</code> and scripting language.
 * By default, it is able to create interpreters for ECMAScript, Python and Tcl
 * scripting languages if you provide the right jar files in your CLASSPATH (i.e.
 * Rhino, JPython and Jacl jar files).
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class InterpreterPool {

    // by default we have 3 languages a few chances that other
    // languages are added...
    private HashMap factories = new HashMap(3);
    // as we use a WeakHashMap, beeing referenced here will not
    // prevent a Document from beeing discared
    // by default we have one document a time.
    private WeakHashMap documentsMap = new WeakHashMap(1);

    private static final String RHINO =
        "org.apache.batik.script.rhino.RhinoInterpreterFactory";

    private static final String JPYTHON =
        "org.apache.batik.script.jpython.JPythonInterpreterFactory";

    private static final String JACL =
        "org.apache.batik.script.jacl.JaclInterpreterFactory";

    private static HashMap defaultFactories = null;

    /**
     * Builds an instance of <code>InterpreterPool</code> that
     * will create <code>Interpreter</code> instances. Initializes
     * the <code>InterpreterPool</code> to recognize the default
     * scripting languages.
     */
    public InterpreterPool() {
        initDefaultFactories();
        factories.putAll(defaultFactories);
    }

    /**
     * We init the default factories only once whatever is the number
     * of <code>IntepreterPool</code> instances because it is sufficient.
     */
    private static void initDefaultFactories() {
        if (defaultFactories == null) {
            defaultFactories = new HashMap(3);
            InterpreterFactory factory = null;
            try {
                factory =
                    (InterpreterFactory)Class.forName(RHINO).newInstance();
                defaultFactories.put("text/ecmascript",
                                      factory);
            } catch (Throwable t1) {
                // may append if the class is not in the CLASSPATH
        }
            try {
                factory =
                    (InterpreterFactory)Class.forName(JPYTHON).newInstance();
                defaultFactories.put("text/python",
                                      factory);
            } catch (Throwable t2) {
                // may append if the class is not in the CLASSPATH
            }
            try {
                factory =
                    (InterpreterFactory)Class.forName(JACL).newInstance();
                defaultFactories.put("text/tcl",
                                      factory);
            } catch (Throwable t3) {
                // may append if the class is not in the CLASSPATH
            }
        }
    }

    /**
     * We use Proxies that use WeakReference to prevent from referencing
     * directly the document, otherwhise, the WeakHashMap will be useless
     * as the document will never be in situation of beeing discarded.
     */
    private static Document createDocumentProxy(Document document)
    {
        if (document instanceof SVGDocument)
            return new SVGDocumentProxy((SVGDocument)document);
        else
            return new DocumentProxy(document);
    }

    /**
     * Returns a unique instance of an implementation of
     * <code>Interpreter</code> interface that matches the given language and
     * the given <code>Document</code>.
     * It returns <code>null</code> if the interpreter cannot be build (for
     * example the language is not recognized by this
     * <code>InterpreterPool</code>). If the document is not <code>null</code>,
     * the variable "document" in the returned interpreter will give access
     * to an instance of <code>Document</code> or <code>SVGDocument</code>
     * depending on the type of the document. The interpreter will
     * automatically be released
     * when the <code>Document</code> will no longer be referenced elsewhere.
     * @param document a DOM <code>Document</code> instance.
     * @param language a mimeType like string describing the language to use
     * (i.e. "text/ecmascript" for ECMAScript interpreter, "text/tcl" for Tcl
     * interpreter...).
     */
    public synchronized Interpreter getInterpreter(Document document,
                                                   String language) {
        // document maybe null HashMap supports null key.
        HashMap interpretersMap = (HashMap)documentsMap.get(document);
        if (interpretersMap == null) {
            // by default we use only one language for a particular document
            interpretersMap = new HashMap(1);
            documentsMap.put(document, interpretersMap);
        }
        Interpreter interpreter = (Interpreter)interpretersMap.get(language);
        if (interpreter == null) {
            InterpreterFactory factory =
                (InterpreterFactory)factories.get(language);
            if (factory != null)
                try {
                    interpreter = factory.createInterpreter();
                    if (document != null) {
                        interpreter.bindObject("document",
                                               createDocumentProxy(document));
                    }
                    interpretersMap.put(language, interpreter);
                } catch (Throwable t) {
                    // may append if the batik interpreters class is here but
                    // not the scripting engine jar
                }
        }
        return interpreter;
    }

    /**
     * Registers an <code>InterpreterFactory</code> for the given
     * language. This allows you to add other languages to the default
     * ones or to replace the <code>InterpreterFactory</code> usually used
     * to create an <code>Interpreter</code> instance for a particular language
     * by your own to be able to have your own interpreter.
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
     * language. It will then be impossible to create new <code>Interpreter</code>
     * instances for the given language.
     * @param language the language for which the factory should
     * be unregistered.
     */
    public void removeInterpreterFactory(String language) {
        factories.remove(language);
    }
}

