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
 * The <code>CharacterData</code> interface extends Node with a set of 
 * attributes and methods for accessing character data in the DOM. For 
 * clarity this set is defined here rather than on each object that uses 
 * these attributes and methods. No DOM objects correspond directly to 
 * <code>CharacterData</code>, though <code>Text</code> and others do 
 * inherit the interface from it. All <code>offsets</code> in this interface 
 * start from <code>0</code>.
 * <p>As explained in the <code>DOMString</code> interface, text strings in 
 * the DOM are represented in UTF-16, i.e. as a sequence of 16-bit units. In 
 * the following, the term 16-bit units is used whenever necessary to 
 * indicate that indexing on CharacterData is done in 16-bit units.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface CharacterData extends Node, org.w3c.dom.CharacterData {
}
