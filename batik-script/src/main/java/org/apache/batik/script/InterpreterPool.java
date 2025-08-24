/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.script;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.util.Service;

import org.w3c.dom.Document;

/**
 * A class allowing to create/query an {@link
 * org.apache.batik.script.Interpreter} corresponding to a particular
 * <code>Document</code> and scripting language.
 *
 * <p>By default, it is able to create interpreters for ECMAScript,
 * Python and Tcl scripting languages if you provide the right jar
 * files in your CLASSPATH (i.e.  Rhino and JPython jar files).</p>
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class InterpreterPool {

    /**
     * Name of the "document" object when referenced by scripts
     */
    public static final String BIND_NAME_DOCUMENT = "document";

    /**
     * The default InterpreterFactory map.
     */
    protected static Map defaultFactories = new HashMap(7);

    /**
     * The InterpreterFactory map.
     */
    protected Map factories = new HashMap(7);

    static {
        Iterator iter = Service.providers(InterpreterFactory.class);
        while (iter.hasNext()) {
            InterpreterFactory factory = null;
            factory = (InterpreterFactory)iter.next();
            String[] mimeTypes = factory.getMimeTypes();
            for (String mimeType : mimeTypes) {
                defaultFactories.put(mimeType, factory);
            }
        }
    }

    /**
     * Constructs a new <code>InterpreterPool</code>.
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
    public Interpreter createInterpreter(Document document, 
                                         String language) {
        return createInterpreter(document, language, null);
    }

    /**
     * Creates a new interpreter for the specified document and
     * according to the specified language. This method can return
     * null if no interpreter has been found for the specified
     * language.
     *
     * @param document the document that needs the interpreter
     * @param language the scripting language
     * @param imports The set of classes/packages to import (if
     *                the interpreter supports that).
     */
    public Interpreter createInterpreter(Document document, 
                                         String language,
                                         ImportInfo imports) {
        InterpreterFactory factory;
        factory = (InterpreterFactory)factories.get(language);

        if (factory == null) return null;

        if (imports == null)
            imports = ImportInfo.getImports();

        Interpreter interpreter = null;
        SVGOMDocument svgDoc = (SVGOMDocument) document;
        URL url = null;
        try {
            url = new URL(svgDoc.getDocumentURI());
        } catch (MalformedURLException e) {
        }
        interpreter = factory.createInterpreter(url, svgDoc.isSVG12(),
                                                imports);

        if (interpreter == null) return null;

        if (document != null)
            interpreter.bindObject(BIND_NAME_DOCUMENT, document);

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
