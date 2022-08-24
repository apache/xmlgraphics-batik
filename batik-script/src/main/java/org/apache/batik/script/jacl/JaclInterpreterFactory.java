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
package org.apache.batik.script.jacl;

import java.net.URL;

import org.apache.batik.ext.awt.image.spi.RegistryEntry;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;
import org.apache.batik.script.jpython.JPythonInterpreterFactory;

import aQute.bnd.annotation.spi.ServiceProvider;

/**
 * Allows to create instances of <code>JaclInterpreter</code> class.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
@ServiceProvider(value = InterpreterFactory.class, attribute = { "mimeTypes:List<String>='"
		+ JaclInterpreterFactory.MIMETYPE_TEXT_PYTHON + "'" })
public class JaclInterpreterFactory implements InterpreterFactory {

    private static final String MIMETYPE_TEXT_TCL = "text/tcl";
	/**
     * The MIME types that jacl can handle.
     */
    public static final String[] JACL_MIMETYPES = { MIMETYPE_TEXT_TCL };

    /**
     * Builds a <code>JaclInterpreterFactory</code>.
     */
    public JaclInterpreterFactory() {
    }

    /**
     * Returns the mime-types to register this interpereter with.
     */
    public String[] getMimeTypes() {
        return JACL_MIMETYPES;
    }

    /**
     * Creates an instance of <code>JaclInterpreter</code> class.
     *
     * @param documentURL the url for the document which will be scripted
     * @param svg12 whether the document is an SVG 1.2 document
     */
    public Interpreter createInterpreter(URL documentURL, boolean svg12) {
        return new JaclInterpreter();
    }

    /**
     * Creates an instance of <code>JaclInterpreter</code> class.
     *
     * @param documentURL the url for the document which will be scripted
     * @param svg12 whether the document is an SVG 1.2 document
     * @param imports The set of classes/packages to import (if
     *                the interpreter supports that), may be null.
     */
    public Interpreter createInterpreter(URL documentURL, boolean svg12,
                                         ImportInfo imports) {
        return new JaclInterpreter();
    }
}
