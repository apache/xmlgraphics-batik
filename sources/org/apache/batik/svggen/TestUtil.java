/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.io.*;
import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;

/**
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TestUtil{
    public static Document getDocumentPrototype(){
        return new SVGOMDocument(null, SVGDOMImplementation.getDOMImplementation());
    }

    public static void trace(Element element, OutputStream out)
        throws IOException{
        Writer writer = new OutputStreamWriter(out);
        XmlWriter.writeXml(element, writer);
        writer.flush();
    }

}
