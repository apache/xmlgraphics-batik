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
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107/java-binding.zip
 */

package org.apache.batik.dom.dom3;

/**
 *  The <code>TypeInfo</code> interface represents a type referenced from 
 * <code>Element</code> or <code>Attr</code> nodes, specified in the schemas 
 * associated with the document. The type is a pair of a namespace URI and 
 * name properties, and depends on the document's schema. 
 * <p> If the document's schema is an XML DTD [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>], the values 
 * are computed as follows: 
 * <ul>
 * <li> If this type is referenced from an 
 * <code>Attr</code> node, <code>typeNamespace</code> is 
 * <code>"http://www.w3.org/TR/REC-xml"</code> and <code>typeName</code> 
 * represents the <b>[attribute type]</b> property in the [<a href='http://www.w3.org/TR/2004/REC-xml-infoset-20040204/'>XML Information Set</a>]
 * . If there is no declaration for the attribute, <code>typeNamespace</code>
 *  and <code>typeName</code> are <code>null</code>. 
 * </li>
 * <li> If this type is 
 * referenced from an <code>Element</code> node, <code>typeNamespace</code> 
 * and <code>typeName</code> are <code>null</code>. 
 * </li>
 * </ul>
 * <p> If the document's schema is an XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
 * , the values are computed as follows using the post-schema-validation 
 * infoset contributions (also called PSVI contributions): 
 * <ul>
 * <li> If the <b>[validity]</b> property exists AND is <em>"invalid"</em> or <em>"notKnown"</em>: the {target namespace} and {name} properties of the declared type if 
 * available, otherwise <code>null</code>. 
 * <p ><b>Note:</b>  At the time of writing, the XML Schema specification does 
 * not require exposing the declared type. Thus, DOM implementations might 
 * choose not to provide type information if validity is not valid. 
 * </li>
 * <li> If the <b>[validity]</b> property exists and is <em>"valid"</em>: 
 * <ol>
 * <li> If <b>[member type definition]</b> exists: 
 * <ol>
 * <li>If {name} is not absent, then expose {name} and {target 
 * namespace} properties of the <b>[member type definition]</b> property;
 * </li>
 * <li>Otherwise, expose the namespace and local name of the 
 * corresponding anonymous type name.
 * </li>
 * </ol>
 * </li>
 * <li> If the <b>[type definition]</b> property exists: 
 * <ol>
 * <li>If {name} is not absent, then expose {name} and {target 
 * namespace} properties of the <b>[type definition]</b> property;
 * </li>
 * <li>Otherwise, expose the namespace and local name of the 
 * corresponding anonymous type name.
 * </li>
 * </ol> 
 * </li>
 * <li> If the <b>[member type definition anonymous]</b> exists: 
 * <ol>
 * <li>If it is false, then expose <b>[member type definition name]</b> and <b>[member type definition namespace]</b> properties;
 * </li>
 * <li>Otherwise, expose the namespace and local name of the 
 * corresponding anonymous type name.
 * </li>
 * </ol> 
 * </li>
 * <li> If the <b>[type definition anonymous]</b> exists: 
 * <ol>
 * <li>If it is false, then expose <b>[type definition name]</b> and <b>[type definition namespace]</b> properties;
 * </li>
 * <li>Otherwise, expose the namespace and local name of the 
 * corresponding anonymous type name.
 * </li>
 * </ol> 
 * </li>
 * </ol>
 * </li>
 * </ul>
 * <p ><b>Note:</b>  Other schema languages are outside the scope of the W3C 
 * and therefore should define how to represent their type systems using 
 * <code>TypeInfo</code>. 
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * @since DOM Level 3
 */
public interface TypeInfo extends org.w3c.dom.TypeInfo {
}
