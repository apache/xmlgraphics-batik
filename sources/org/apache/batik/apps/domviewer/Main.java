/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.domviewer;

import java.io.File;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.util.gui.DOMViewer;
import org.w3c.dom.Document;
import org.w3c.dom.css.ViewCSS;
import org.xml.sax.InputSource;

/**
 * The DOM viewer main class.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Main {
    /**
     * The program entry point.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
        DOMViewer viewer = new DOMViewer();

        CSSDocumentHandler.setParserClassName("org.w3c.flute.parser.Parser");
        SVGDocumentFactory factory = new SVGDocumentFactory
            ("org.apache.xerces.parsers.SAXParser");
        
        String uri = new File(args[0]).toURL().toString();
        Document doc = factory.createDocument(uri, new InputSource(uri));
        viewer.setDocument(doc, (ViewCSS)doc.getDocumentElement());

        viewer.show();
    }
}
