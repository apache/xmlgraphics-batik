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
 * The <code>Element</code> interface represents an element in an HTML or XML 
 * document. Elements may have attributes associated with them; since the 
 * <code>Element</code> interface inherits from <code>Node</code>, the 
 * generic <code>Node</code> interface attribute <code>attributes</code> may 
 * be used to retrieve the set of all attributes for an element. There are 
 * methods on the <code>Element</code> interface to retrieve either an 
 * <code>Attr</code> object by name or an attribute value by name. In XML, 
 * where an attribute value may contain entity references, an 
 * <code>Attr</code> object should be retrieved to examine the possibly 
 * fairly complex sub-tree representing the attribute value. On the other 
 * hand, in HTML, where all attributes have simple string values, methods to 
 * directly access an attribute value can safely be used as a convenience.
 * <p ><b>Note:</b> In DOM Level 2, the method <code>normalize</code> is 
 * inherited from the <code>Node</code> interface where it was moved.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface Element extends Node, org.w3c.dom.Element {

    /**
     *  The type information associated with this element. 
     * @since DOM Level 3
     */
    public org.w3c.dom.TypeInfo getSchemaTypeInfo();

    /**
     *  If the parameter <code>isId</code> is <code>true</code>, this method 
     * declares the specified attribute to be a user-determined ID attribute
     * . This affects the value of <code>Attr.isId</code> and the behavior 
     * of <code>Document.getElementById</code>, but does not change any 
     * schema that may be in use, in particular this does not affect the 
     * <code>Attr.schemaTypeInfo</code> of the specified <code>Attr</code> 
     * node. Use the value <code>false</code> for the parameter 
     * <code>isId</code> to undeclare an attribute for being a 
     * user-determined ID attribute. 
     * <br> To specify an attribute by local name and namespace URI, use the 
     * <code>setIdAttributeNS</code> method. 
     * @param name The name of the attribute.
     * @param isId Whether the attribute is a of type ID.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br>NOT_FOUND_ERR: Raised if the specified node is not an attribute 
     *   of this element.
     * @since DOM Level 3
     */
    public void setIdAttribute(String name, 
                               boolean isId)
                               throws org.w3c.dom.DOMException;

    /**
     *  If the parameter <code>isId</code> is <code>true</code>, this method 
     * declares the specified attribute to be a user-determined ID attribute
     * . This affects the value of <code>Attr.isId</code> and the behavior 
     * of <code>Document.getElementById</code>, but does not change any 
     * schema that may be in use, in particular this does not affect the 
     * <code>Attr.schemaTypeInfo</code> of the specified <code>Attr</code> 
     * node. Use the value <code>false</code> for the parameter 
     * <code>isId</code> to undeclare an attribute for being a 
     * user-determined ID attribute. 
     * @param namespaceURI The namespace URI of the attribute.
     * @param localName The local name of the attribute.
     * @param isId Whether the attribute is a of type ID.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br>NOT_FOUND_ERR: Raised if the specified node is not an attribute 
     *   of this element.
     * @since DOM Level 3
     */
    public void setIdAttributeNS(String namespaceURI, 
                                 String localName, 
                                 boolean isId)
        throws org.w3c.dom.DOMException;

    /**
     *  If the parameter <code>isId</code> is <code>true</code>, this method 
     * declares the specified attribute to be a user-determined ID attribute
     * . This affects the value of <code>Attr.isId</code> and the behavior 
     * of <code>Document.getElementById</code>, but does not change any 
     * schema that may be in use, in particular this does not affect the 
     * <code>Attr.schemaTypeInfo</code> of the specified <code>Attr</code> 
     * node. Use the value <code>false</code> for the parameter 
     * <code>isId</code> to undeclare an attribute for being a 
     * user-determined ID attribute. 
     * @param idAttr The attribute node.
     * @param isId Whether the attribute is a of type ID.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br>NOT_FOUND_ERR: Raised if the specified node is not an attribute 
     *   of this element.
     * @since DOM Level 3
     */
    public void setIdAttributeNode(org.w3c.dom.Attr idAttr, 
                                   boolean isId)
        throws org.w3c.dom.DOMException;
}
