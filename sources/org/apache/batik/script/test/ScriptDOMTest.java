/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.test;

import java.io.*;
import org.apache.batik.script.*;
import org.apache.batik.dom.*;
import org.apache.batik.dom.svg.*;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;
import org.w3c.dom.events.*;

/**
 * A simple test for three wrappers on scripting engines (ECMAScript,
 * TCL & Python).
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class ScriptDOMTest {
    final static private String SCRIPT_LOC =
        "resources/org/apache/batik/test/script/";

    final static private String POOL_CLASS =
        "org.apache.batik.refimpl.script.ConcreteInterpreterPool";

    public static void main(String[] arg) {
        System.out.println("--- Scripting the DOM test ---");
        System.out.println("+++ Creating an InterpreterPool instance +++");
        InterpreterPool pool = null;
        try {
            pool = (InterpreterPool)Class.forName(POOL_CLASS).newInstance();
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            System.exit(1);
        }

        System.out.println("--- Creating an text/ecmascript interpreter ---");
        final Interpreter interpreter1 = pool.getInterpreter("text/ecmascript");
        if (interpreter1 == null) {
            System.err.println("*** Can't find a text/ecmascript interpret ***");
        } else {
            System.out.println("+++ Testing the text/ecmascript interpreter DOM access +++");
            SVGOMDocument doc = new SVGOMDocument(null, SVGDOMImplementation.getDOMImplementation());
            Element svgelmt = new SVGOMSVGElement("svg", doc);
            doc.appendChild(svgelmt);
            SVGElement elmt = new SVGOMGElement("svg", doc);
            ((EventTarget)elmt).addEventListener("DOMNodeInserted", new EventListener() {
                public void handleEvent(Event evt) {
                    interpreter1.bindObject("event", evt);
                    try {
                        interpreter1.evaluate(new StringReader("test(event)"));
                    } catch (InterpreterException e) {
                        System.err.println("*** "+e.getMessage()+" ***");
                        System.exit(1);
                    } catch (IOException io) {
                        System.err.println("*** "+io.getMessage()+" ***");
                        System.exit(1);
                    }
                }
            }, true);
            svgelmt.appendChild(elmt);
            try {
                Object value = interpreter1.
                    evaluate(new BufferedReader(new FileReader(SCRIPT_LOC+"test.js")));
            } catch (InterpreterException e) {
                System.err.println("*** Can't load file "+e.getMessage()+" ***");
                System.exit(1);
            } catch (IOException io) {
                System.err.println("*** Can't load file "+io.getMessage()+" ***");
                System.exit(1);
            }
            elmt.appendChild(elmt = new SVGOMRectElement("svg", doc));
            if (!elmt.getAttribute("x").equals("10")) {
                System.err.println("*** Script incorrectly update the DOM");
                System.exit(1);
            } else
                elmt.setAttribute("x", "0");
        }

        System.out.println("--- Creating an text/python interpreter ---");
        final Interpreter interpreter2 = pool.getInterpreter("text/python");
        if (interpreter2 == null) {
            System.err.println("*** Can't find a text/python interpret ***");
        } else {
            System.out.println("+++ Testing the text/python interpreter DOM access +++");
            SVGOMDocument doc = new SVGOMDocument(null, SVGDOMImplementation.getDOMImplementation());
            Element svgelmt = new SVGOMSVGElement("svg", doc);
            doc.appendChild(svgelmt);
            SVGElement elmt = new SVGOMGElement("svg", doc);
            ((EventTarget)elmt).addEventListener("DOMNodeInserted", new EventListener() {
                public void handleEvent(Event evt) {
                    interpreter2.bindObject("event", evt);
                    try {
                        interpreter2.evaluate(new StringReader("test(event)"));
                    } catch (InterpreterException e) {
                        System.err.println("*** "+e.getMessage()+" ***");
                        System.exit(1);
                    } catch (IOException io) {
                        System.err.println("*** "+io.getMessage()+" ***");
                        System.exit(1);
                    }
                }
            }, true);
            svgelmt.appendChild(elmt);
            try {
                Object value = interpreter2.
                    evaluate(new BufferedReader(new FileReader(SCRIPT_LOC+"test.py")));
            } catch (InterpreterException e) {
                System.err.println("*** "+e.getMessage()+" ***");
                System.exit(1);
            } catch (IOException io) {
                System.err.println("*** "+io.getMessage()+" ***");
                System.exit(1);
            }
            elmt.appendChild(elmt = new SVGOMRectElement("svg", doc));
            if (!elmt.getAttribute("x").equals("10")) {
                System.err.println("*** Script incorrectly update the DOM");
                System.exit(1);
            } else
                elmt.setAttribute("x", "0");
        }

        System.out.println("--- Creating an text/tcl interpreter ---");
        final Interpreter interpreter3 = pool.getInterpreter("text/tcl");
        if (interpreter3 == null) {
            System.err.println("*** Can't find a text/tcl interpret ***");
        } else {
            System.out.println("+++ Testing the text/tcl interpreter DOM access +++");
            SVGOMDocument doc = new SVGOMDocument(null, SVGDOMImplementation.getDOMImplementation());
            Element svgelmt = new SVGOMSVGElement("svg", doc);
            doc.appendChild(svgelmt);
            SVGElement elmt = new SVGOMGElement("svg", doc);
            ((EventTarget)elmt).addEventListener("DOMNodeInserted", new EventListener() {
                public void handleEvent(Event evt) {
                    interpreter3.bindObject("event", evt);
                    try {
                        interpreter3.evaluate(new StringReader("test $event"));
                    } catch (InterpreterException e) {
                        System.err.println("*** "+e.getMessage()+" ***");
                        System.exit(1);
                    } catch (IOException io) {
                        System.err.println("*** "+io.getMessage()+" ***");
                        System.exit(1);
                    }
                }
            }, true);
            svgelmt.appendChild(elmt);
            try {
                Object value = interpreter3.
                    evaluate(new BufferedReader(new FileReader(SCRIPT_LOC+"test.tcl")));
            } catch (InterpreterException e) {
                System.err.println("*** "+e.getMessage()+" ***");
                System.exit(1);
            } catch (IOException io) {
                System.err.println("*** "+io.getMessage()+" ***");
                System.exit(1);
            }
            elmt.appendChild(elmt = new SVGOMRectElement("svg", doc));
            if (!elmt.getAttribute("x").equals("10")) {
                System.err.println("*** Script incorrectly update the DOM");
                System.exit(1);
            } else
                elmt.setAttribute("x", "0");
        }

        System.out.println("--- Test done and ok ---");
    }
}
