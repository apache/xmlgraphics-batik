/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.dom;

import org.w3c.dom.*;

/**
 * Utilities for DOM debug.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Util {

    public static int nodes(Document doc) {
	Element e = doc.getDocumentElement();
	if (e != null) {
	    return 1 + nodes(e);
	} else {
	    return 1;
	}
    }

    public static int nodes(Node n) {
	Node c = n.getFirstChild();
	int result = 1;
	while (c != null) {
	    result += nodes(c);
	    c = c.getNextSibling();
	}
	NamedNodeMap map = n.getAttributes();
	if (map != null) {
	    for (int i = map.getLength()-1; i >= 0; i--) {
		result += nodes(map.item(i));
	    }
	}
	return result;
    }

    public static void display(Document doc) {
	System.out.println("------------------------------");
        System.out.println(doc);
        System.out.println(doc.getImplementation());

	for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
	    display(n, 0);
	}
	System.out.println("------------------------------");
    }

    public static void display(Node n, int level) {
	String tab = "";
	for (int i = 0; i < level; i++) {
	    tab += " | ";
	}
	short t = n.getNodeType();
	switch (t) {
	case Node.ELEMENT_NODE:
	    System.out.print(tab + "<" + n.getNodeName() +
			     ":uri(" + n.getNamespaceURI() + ")");
	
	    NamedNodeMap map = n.getAttributes();
	    for (int i = 0; i < map.getLength(); i++) {
		Attr attr = (Attr)map.item(i);
		System.out.print(" " + attr.getName() +
				 ":uri(" + attr.getNamespaceURI() + ")" +
				 "=\"" + attr.getValue() + "\"");
	    }

	    Node o = n.getFirstChild();
	    
	    if (o == null) {
		System.out.println("/>");
	    } else {
		System.out.println(">");
		while (o != null) {
		    display(o, level + 1);
		    o = o.getNextSibling();
		}
		System.out.println(tab + "</" + n.getNodeName() + ">");
	    }
	    break;
	case Node.TEXT_NODE:
	    System.out.println(tab + n.getNodeValue());
	    break;
	case Node.PROCESSING_INSTRUCTION_NODE:
	    System.out.println(tab + "<?" + n.getNodeName() +
			       " " + n.getNodeValue() + "?>");
	    break;
	case Node.CDATA_SECTION_NODE:
	    System.out.println(tab + "<![CDATA[" + n.getNodeValue() + "]]>");
	    break;
	case Node.COMMENT_NODE:
	    System.out.println(tab + "<!--" + n.getNodeValue() + "-->");
	}
    }
}
