/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.w3c.dom.Document;

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
        InterpreterFactory factory = null;
        try {
            factory =
                (InterpreterFactory)Class.forName(RHINO).newInstance();
            defaultFactories.put("text/ecmascript", factory);
        } catch (ThreadDeath td) {
            throw td;
        } catch (Throwable t1) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JPYTHON).newInstance();
            defaultFactories.put("text/python", factory);
        } catch (ThreadDeath td) {
            throw td;
        } catch (Throwable t2) {
            // may append if the class is not in the CLASSPATH
        }
        try {
            factory =
                (InterpreterFactory)Class.forName(JACL).newInstance();
            defaultFactories.put("text/tcl", factory);
        } catch (ThreadDeath td) {
            throw td;
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
            interpreter = factory.createInterpreter
                (((SVGOMDocument)document).getURLObject());
        if (document != null) {
            interpreter.bindObject(BIND_NAME_DOCUMENT, document);
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

