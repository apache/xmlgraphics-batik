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
package org.apache.batik.bridge;

import java.net.URL;

import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;

import aQute.bnd.annotation.spi.ServiceProvider;

/**
 * Allows to create instances of <code>RhinoInterpreter</code> class.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
@ServiceProvider(value = InterpreterFactory.class, attribute = { "mimeTypes:List<String>='"
		+ RhinoInterpreterFactory.MIMETYPE_TEXT_JAVASCRIPT + ","
		+ RhinoInterpreterFactory.MIMETYPE_TEXT_ECMASCRIPT + ","
		+ RhinoInterpreterFactory.MIMETYPE_APPLICATION_JAVASCRIPT + ","
		+ RhinoInterpreterFactory.MIMETYPE_APPLICATION_ECMASCRIPT + "'" })
public class RhinoInterpreterFactory implements InterpreterFactory {

    protected static final String MIMETYPE_TEXT_JAVASCRIPT = "text/javascript";
	protected static final String MIMETYPE_TEXT_ECMASCRIPT = "text/ecmascript";
	protected static final String MIMETYPE_APPLICATION_JAVASCRIPT = "application/javascript";
	protected static final String MIMETYPE_APPLICATION_ECMASCRIPT = "application/ecmascript";
	/**
     * The MIME types that Rhino can handle.
     */
    public static final String[] RHINO_MIMETYPES = {
        MIMETYPE_APPLICATION_ECMASCRIPT,
        MIMETYPE_APPLICATION_JAVASCRIPT,
        MIMETYPE_TEXT_ECMASCRIPT,
        MIMETYPE_TEXT_JAVASCRIPT,
    };

    /**
     * Builds a <code>RhinoInterpreterFactory</code>.
     */
    public RhinoInterpreterFactory() {
    }

    /**
     * Returns the mime-types to register this interpereter with.
     */
    public String[] getMimeTypes() {
        return RHINO_MIMETYPES;
    }

    /**
     * Creates an instance of <code>RhinoInterpreter</code> class.
     *
     * @param documentURL the url for the document which will be scripted
     * @param svg12 whether the document is an SVG 1.2 document
     */
    public Interpreter createInterpreter(URL documentURL, boolean svg12) {
        return createInterpreter(documentURL, svg12, null);
    }

    /**
     * Creates an instance of <code>RhinoInterpreter</code> class.
     *
     * @param documentURL the url for the document which will be scripted
     * @param svg12 whether the document is an SVG 1.2 document
     * @param imports The set of classes/packages to import (if
     *                the interpreter supports that), may be null.
     */
    public Interpreter createInterpreter(URL documentURL, boolean svg12,
                                         ImportInfo imports) {
        if (svg12) {
            return new SVG12RhinoInterpreter(documentURL, imports);
        }
        return new RhinoInterpreter(documentURL, imports);
    }
}
