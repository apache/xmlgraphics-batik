/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 *
 * Modifications:
 *   February 21, 2005
 *     - Moved interface to org.apache.batik.dom.dom3 package.
 *     - Removed methods and constants present in the DOM 2 interface.
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/java-binding.zip
 */

package org.apache.batik.dom.dom3;

/**
 * Each <code>Document</code> has a <code>doctype</code> attribute whose value 
 * is either <code>null</code> or a <code>DocumentType</code> object. The 
 * <code>DocumentType</code> interface in the DOM Core provides an interface 
 * to the list of entities that are defined for the document, and little 
 * else because the effect of namespaces and the various XML schema efforts 
 * on DTD representation are not clearly understood as of this writing.
 * <p>DOM Level 3 doesn't support editing <code>DocumentType</code> nodes. 
 * <code>DocumentType</code> nodes are read-only.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface DocumentType extends Node, org.w3c.dom.DocumentType {
}
