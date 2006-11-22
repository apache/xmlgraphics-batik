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
package org.apache.batik.script.jpython;

import java.net.URL;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;

/**
 * Allows to create instances of <code>JPythonInterpreter</code> class.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class JPythonInterpreterFactory implements InterpreterFactory {

    static final String TEXT_PYTHON = "text/python";

    /**
     * Builds a <code>JPythonInterpreterFactory</code>.
     */
    public JPythonInterpreterFactory() {
    }

    /**
     * Returns the mime-type to register this interpereter with.
     */
    public String getMimeType() { return TEXT_PYTHON; }

    /**
     * Creates an instance of <code>JPythonInterpreter</code> class.
     *
     * @param documentURL the url for the document which will be scripted
     * @param svg12 whether the document is an SVG 1.2 document
     */
    public Interpreter createInterpreter(URL documentURL, boolean svg12) {
        return new JPythonInterpreter();
    }
}
