/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.dom.traversal;

import java.io.*;

import org.apache.batik.dom.*;
import org.apache.batik.test.dom.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

/**
 * To test TreeWalkers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TreeWalkerTest {
    /**
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();

        // Create a document
        Document doc = impl.createDocument(null, "root", null);
        Element elt = doc.getDocumentElement();
        
        Text text = doc.createTextNode("Text");
        elt.appendChild(text);
        elt.appendChild(doc.createElementNS("ELT-NS", "eltType1"));

        elt.setAttributeNS("ATTR-NS", "attr1", "123");

        TreeWalker ni =
            ((DocumentTraversal)doc).createTreeWalker(doc,
                                                      NodeFilter.SHOW_ALL,
                                                      null,
                                                      true);

        Node n;
        while ((n = ni.nextNode()) != null) {
            System.out.println("---> " + n);
        }

        ni = ((DocumentTraversal)doc).createTreeWalker(doc,
                                                       NodeFilter.SHOW_ALL,
                                                       null,
                                                       true);
        for (int i = 0; i < 4; i++) {
            n = ni.nextNode();
            System.out.println("next ---> " + n);
        }
        System.out.println("current ------> " + ni.getCurrentNode());

        for (int i = 0; i < 4; i++) {
            n = ni.previousNode();
            System.out.println("prev ---> " + n);
        }
        ni.nextNode();
        ni.nextNode();
        System.out.println("txt ---> " + ni.nextNode());
        elt.removeChild(text);
        System.out.println("prev after remove ---> " + ni.previousNode());
       
    }

}
