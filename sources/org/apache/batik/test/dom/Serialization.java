/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.dom;

import java.io.*;

import org.apache.batik.dom.*;
import org.w3c.dom.*;

/**
 * This class tests the Java serialization of the DOM.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Serialization {
    /**
     * The program entry point.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();

        // Create a document
        Document doc = impl.createDocument(null, "root", null);
        Element elt = doc.getDocumentElement();

        elt.appendChild(doc.createTextNode("Text"));
        elt.appendChild(doc.createElementNS("ELT-NS", "eltType1"));

        elt.setAttributeNS("ATTR-NS", "attr1", "123");

        Util.display(doc);

        // Write the document
        OutputStream os = new FileOutputStream("dom.ser");
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(doc);
        oos.flush();

        // Read the document
        InputStream is = new FileInputStream("dom.ser");
        ObjectInputStream ois = new ObjectInputStream(is);

        doc = (Document)ois.readObject();

        Util.display(doc);
    }

}
