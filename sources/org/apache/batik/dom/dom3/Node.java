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
 * The <code>Node</code> interface is the primary datatype for the entire 
 * Document Object Model. It represents a single node in the document tree. 
 * While all objects implementing the <code>Node</code> interface expose 
 * methods for dealing with children, not all objects implementing the 
 * <code>Node</code> interface may have children. For example, 
 * <code>Text</code> nodes may not have children, and adding children to 
 * such nodes results in a <code>DOMException</code> being raised.
 * <p>The attributes <code>nodeName</code>, <code>nodeValue</code> and 
 * <code>attributes</code> are included as a mechanism to get at node 
 * information without casting down to the specific derived interface. In 
 * cases where there is no obvious mapping of these attributes for a 
 * specific <code>nodeType</code> (e.g., <code>nodeValue</code> for an 
 * <code>Element</code> or <code>attributes</code> for a <code>Comment</code>
 * ), this returns <code>null</code>. Note that the specialized interfaces 
 * may contain additional and more convenient mechanisms to get and set the 
 * relevant information.
 * <p>The values of <code>nodeName</code>, 
 * <code>nodeValue</code>, and <code>attributes</code> vary according to the 
 * node type as follows: 
 * <table border='1' cellpadding='3'>
 * <tr>
 * <th>Interface</th>
 * <th>nodeName</th>
 * <th>nodeValue</th>
 * <th>attributes</th>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>Attr</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as <code>Attr.name</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as 
 * <code>Attr.value</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>CDATASection</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>"#cdata-section"</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as <code>CharacterData.data</code>, the 
 * content of the CDATA Section</td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>Comment</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>"#comment"</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as <code>CharacterData.data</code>, the 
 * content of the comment</td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>Document</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>"#document"</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>DocumentFragment</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>"#document-fragment"</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>DocumentType</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as 
 * <code>DocumentType.name</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>Element</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as <code>Element.tagName</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>NamedNodeMap</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>Entity</code></td>
 * <td valign='top' rowspan='1' colspan='1'>entity name</td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>EntityReference</code></td>
 * <td valign='top' rowspan='1' colspan='1'>name of entity referenced</td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>Notation</code></td>
 * <td valign='top' rowspan='1' colspan='1'>notation name</td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>null</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>ProcessingInstruction</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same 
 * as <code>ProcessingInstruction.target</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as 
 * <code>ProcessingInstruction.data</code></td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * <tr>
 * <td valign='top' rowspan='1' colspan='1'><code>Text</code></td>
 * <td valign='top' rowspan='1' colspan='1'>
 * <code>"#text"</code></td>
 * <td valign='top' rowspan='1' colspan='1'>same as <code>CharacterData.data</code>, the content 
 * of the text node</td>
 * <td valign='top' rowspan='1' colspan='1'><code>null</code></td>
 * </tr>
 * </table> 
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface Node extends org.w3c.dom.Node {

    /**
     * The absolute base URI of this node or <code>null</code> if the 
     * implementation wasn't able to obtain an absolute URI. This value is 
     * computed as described in . However, when the <code>Document</code> 
     * supports the feature "HTML" [<a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>DOM Level 2 HTML</a>]
     * , the base URI is computed using first the value of the href 
     * attribute of the HTML BASE element if any, and the value of the 
     * <code>documentURI</code> attribute from the <code>Document</code> 
     * interface otherwise.
     * @since DOM Level 3
     */
    public String getBaseURI();

    // DocumentPosition
    /**
     * The two nodes are disconnected. Order between disconnected nodes is 
     * always implementation-specific.
     */
    public static final short DOCUMENT_POSITION_DISCONNECTED = 0x01;
    /**
     * The second node precedes the reference node.
     */
    public static final short DOCUMENT_POSITION_PRECEDING = 0x02;
    /**
     * The node follows the reference node.
     */
    public static final short DOCUMENT_POSITION_FOLLOWING = 0x04;
    /**
     * The node contains the reference node. A node which contains is always 
     * preceding, too.
     */
    public static final short DOCUMENT_POSITION_CONTAINS = 0x08;
    /**
     * The node is contained by the reference node. A node which is contained 
     * is always following, too.
     */
    public static final short DOCUMENT_POSITION_CONTAINED_BY = 0x10;
    /**
     * The determination of preceding versus following is 
     * implementation-specific.
     */
    public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 0x20;

    /**
     * Compares the reference node, i.e. the node on which this method is 
     * being called, with a node, i.e. the one passed as a parameter, with 
     * regard to their position in the document and according to the 
     * document order.
     * @param other The node to compare against the reference node.
     * @return Returns how the node is positioned relatively to the reference 
     *   node.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: when the compared nodes are from different DOM 
     *   implementations that do not coordinate to return consistent 
     *   implementation-specific results.
     * @since DOM Level 3
     */
    public short compareDocumentPosition(org.w3c.dom.Node other)
        throws org.w3c.dom.DOMException;

    /**
     * This attribute returns the text content of this node and its 
     * descendants. When it is defined to be <code>null</code>, setting it 
     * has no effect. On setting, any possible children this node may have 
     * are removed and, if it the new string is not empty or 
     * <code>null</code>, replaced by a single <code>Text</code> node 
     * containing the string this attribute is set to. 
     * <br> On getting, no serialization is performed, the returned string 
     * does not contain any markup. No whitespace normalization is performed 
     * and the returned string does not contain the white spaces in element 
     * content (see the attribute 
     * <code>Text.isElementContentWhitespace</code>). Similarly, on setting, 
     * no parsing is performed either, the input string is taken as pure 
     * textual content. 
     * <br>The string returned is made of the text content of this node 
     * depending on its type, as defined below: 
     * <table border='1' cellpadding='3'>
     * <tr>
     * <th>Node type</th>
     * <th>Content</th>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>
     * ELEMENT_NODE, ATTRIBUTE_NODE, ENTITY_NODE, ENTITY_REFERENCE_NODE, 
     * DOCUMENT_FRAGMENT_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'>concatenation of the <code>textContent</code> 
     * attribute value of every child node, excluding COMMENT_NODE and 
     * PROCESSING_INSTRUCTION_NODE nodes. This is the empty string if the 
     * node has no children.</td>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE, 
     * PROCESSING_INSTRUCTION_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'><code>nodeValue</code></td>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>DOCUMENT_NODE, 
     * DOCUMENT_TYPE_NODE, NOTATION_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'><em>null</em></td>
     * </tr>
     * </table>
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than 
     *   fit in a <code>DOMString</code> variable on the implementation 
     *   platform.
     * @since DOM Level 3
     */
    public String getTextContent()
        throws org.w3c.dom.DOMException;
    /**
     * This attribute returns the text content of this node and its 
     * descendants. When it is defined to be <code>null</code>, setting it 
     * has no effect. On setting, any possible children this node may have 
     * are removed and, if it the new string is not empty or 
     * <code>null</code>, replaced by a single <code>Text</code> node 
     * containing the string this attribute is set to. 
     * <br> On getting, no serialization is performed, the returned string 
     * does not contain any markup. No whitespace normalization is performed 
     * and the returned string does not contain the white spaces in element 
     * content (see the attribute 
     * <code>Text.isElementContentWhitespace</code>). Similarly, on setting, 
     * no parsing is performed either, the input string is taken as pure 
     * textual content. 
     * <br>The string returned is made of the text content of this node 
     * depending on its type, as defined below: 
     * <table border='1' cellpadding='3'>
     * <tr>
     * <th>Node type</th>
     * <th>Content</th>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>
     * ELEMENT_NODE, ATTRIBUTE_NODE, ENTITY_NODE, ENTITY_REFERENCE_NODE, 
     * DOCUMENT_FRAGMENT_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'>concatenation of the <code>textContent</code> 
     * attribute value of every child node, excluding COMMENT_NODE and 
     * PROCESSING_INSTRUCTION_NODE nodes. This is the empty string if the 
     * node has no children.</td>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE, 
     * PROCESSING_INSTRUCTION_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'><code>nodeValue</code></td>
     * </tr>
     * <tr>
     * <td valign='top' rowspan='1' colspan='1'>DOCUMENT_NODE, 
     * DOCUMENT_TYPE_NODE, NOTATION_NODE</td>
     * <td valign='top' rowspan='1' colspan='1'><em>null</em></td>
     * </tr>
     * </table>
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @since DOM Level 3
     */
    public void setTextContent(String textContent)
        throws org.w3c.dom.DOMException;

    /**
     * Returns whether this node is the same node as the given one.
     * <br>This method provides a way to determine whether two 
     * <code>Node</code> references returned by the implementation reference 
     * the same object. When two <code>Node</code> references are references 
     * to the same object, even if through a proxy, the references may be 
     * used completely interchangeably, such that all attributes have the 
     * same values and calling the same DOM method on either reference 
     * always has exactly the same effect.
     * @param other The node to test against.
     * @return Returns <code>true</code> if the nodes are the same, 
     *   <code>false</code> otherwise.
     * @since DOM Level 3
     */
    public boolean isSameNode(org.w3c.dom.Node other);

    /**
     * Look up the prefix associated to the given namespace URI, starting from 
     * this node. The default namespace declarations are ignored by this 
     * method.
     * <br>See  for details on the algorithm used by this method.
     * @param namespaceURI The namespace URI to look for.
     * @return Returns an associated namespace prefix if found or 
     *   <code>null</code> if none is found. If more than one prefix are 
     *   associated to the namespace prefix, the returned namespace prefix 
     *   is implementation dependent.
     * @since DOM Level 3
     */
    public String lookupPrefix(String namespaceURI);

    /**
     *  This method checks if the specified <code>namespaceURI</code> is the 
     * default namespace or not. 
     * @param namespaceURI The namespace URI to look for.
     * @return Returns <code>true</code> if the specified 
     *   <code>namespaceURI</code> is the default namespace, 
     *   <code>false</code> otherwise. 
     * @since DOM Level 3
     */
    public boolean isDefaultNamespace(String namespaceURI);

    /**
     * Look up the namespace URI associated to the given prefix, starting from 
     * this node.
     * <br>See  for details on the algorithm used by this method.
     * @param prefix The prefix to look for. If this parameter is 
     *   <code>null</code>, the method will return the default namespace URI 
     *   if any.
     * @return Returns the associated namespace URI or <code>null</code> if 
     *   none is found.
     * @since DOM Level 3
     */
    public String lookupNamespaceURI(String prefix);

    /**
     * Tests whether two nodes are equal.
     * <br>This method tests for equality of nodes, not sameness (i.e., 
     * whether the two nodes are references to the same object) which can be 
     * tested with <code>Node.isSameNode()</code>. All nodes that are the 
     * same will also be equal, though the reverse may not be true.
     * <br>Two nodes are equal if and only if the following conditions are 
     * satisfied: 
     * <ul>
     * <li>The two nodes are of the same type.
     * </li>
     * <li>The following string 
     * attributes are equal: <code>nodeName</code>, <code>localName</code>, 
     * <code>namespaceURI</code>, <code>prefix</code>, <code>nodeValue</code>
     * . This is: they are both <code>null</code>, or they have the same 
     * length and are character for character identical.
     * </li>
     * <li>The 
     * <code>attributes</code> <code>NamedNodeMaps</code> are equal. This 
     * is: they are both <code>null</code>, or they have the same length and 
     * for each node that exists in one map there is a node that exists in 
     * the other map and is equal, although not necessarily at the same 
     * index.
     * </li>
     * <li>The <code>childNodes</code> <code>NodeLists</code> are equal. 
     * This is: they are both <code>null</code>, or they have the same 
     * length and contain equal nodes at the same index. Note that 
     * normalization can affect equality; to avoid this, nodes should be 
     * normalized before being compared.
     * </li>
     * </ul> 
     * <br>For two <code>DocumentType</code> nodes to be equal, the following 
     * conditions must also be satisfied: 
     * <ul>
     * <li>The following string attributes 
     * are equal: <code>publicId</code>, <code>systemId</code>, 
     * <code>internalSubset</code>.
     * </li>
     * <li>The <code>entities</code> 
     * <code>NamedNodeMaps</code> are equal.
     * </li>
     * <li>The <code>notations</code> 
     * <code>NamedNodeMaps</code> are equal.
     * </li>
     * </ul> 
     * <br>On the other hand, the following do not affect equality: the 
     * <code>ownerDocument</code>, <code>baseURI</code>, and 
     * <code>parentNode</code> attributes, the <code>specified</code> 
     * attribute for <code>Attr</code> nodes, the <code>schemaTypeInfo</code>
     *  attribute for <code>Attr</code> and <code>Element</code> nodes, the 
     * <code>Text.isElementContentWhitespace</code> attribute for 
     * <code>Text</code> nodes, as well as any user data or event listeners 
     * registered on the nodes. 
     * <p ><b>Note:</b>  As a general rule, anything not mentioned in the 
     * description above is not significant in consideration of equality 
     * checking. Note that future versions of this specification may take 
     * into account more attributes and implementations conform to this 
     * specification are expected to be updated accordingly. 
     * @param arg The node to compare equality with.
     * @return Returns <code>true</code> if the nodes are equal, 
     *   <code>false</code> otherwise.
     * @since DOM Level 3
     */
    public boolean isEqualNode(org.w3c.dom.Node arg);

    /**
     *  This method returns a specialized object which implements the 
     * specialized APIs of the specified feature and version, as specified 
     * in . The specialized object may also be obtained by using 
     * binding-specific casting methods but is not necessarily expected to, 
     * as discussed in . This method also allow the implementation to 
     * provide specialized objects which do not support the <code>Node</code>
     *  interface. 
     * @param feature  The name of the feature requested. Note that any plus 
     *   sign "+" prepended to the name of the feature will be ignored since 
     *   it is not significant in the context of this method. 
     * @param version  This is the version number of the feature to test. 
     * @return  Returns an object which implements the specialized APIs of 
     *   the specified feature and version, if any, or <code>null</code> if 
     *   there is no object which implements interfaces associated with that 
     *   feature. If the <code>DOMObject</code> returned by this method 
     *   implements the <code>Node</code> interface, it must delegate to the 
     *   primary core <code>Node</code> and not return results inconsistent 
     *   with the primary core <code>Node</code> such as attributes, 
     *   childNodes, etc. 
     * @since DOM Level 3
     */
    public Object getFeature(String feature, 
                             String version);

    /**
     * Associate an object to a key on this node. The object can later be 
     * retrieved from this node by calling <code>getUserData</code> with the 
     * same key.
     * @param key The key to associate the object to.
     * @param data The object to associate to the given key, or 
     *   <code>null</code> to remove any existing association to that key.
     * @param handler The handler to associate to that key, or 
     *   <code>null</code>.
     * @return Returns the <code>DOMUserData</code> previously associated to 
     *   the given key on this node, or <code>null</code> if there was none.
     * @since DOM Level 3
     */
    public Object setUserData(String key, 
                              Object data, 
                              org.w3c.dom.UserDataHandler handler);

    /**
     * Retrieves the object associated to a key on a this node. The object 
     * must first have been set to this node by calling 
     * <code>setUserData</code> with the same key.
     * @param key The key the object is associated to.
     * @return Returns the <code>DOMUserData</code> associated to the given 
     *   key on this node, or <code>null</code> if there was none.
     * @since DOM Level 3
     */
    public Object getUserData(String key);
}
