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
 * The <code>Document</code> interface represents the entire HTML or XML 
 * document. Conceptually, it is the root of the document tree, and provides 
 * the primary access to the document's data.
 * <p>Since elements, text nodes, comments, processing instructions, etc. 
 * cannot exist outside the context of a <code>Document</code>, the 
 * <code>Document</code> interface also contains the factory methods needed 
 * to create these objects. The <code>Node</code> objects created have a 
 * <code>ownerDocument</code> attribute which associates them with the 
 * <code>Document</code> within whose context they were created.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface Document extends Node, org.w3c.dom.Document {

    /**
     * An attribute specifying the encoding used for this document at the time 
     * of the parsing. This is <code>null</code> when it is not known, such 
     * as when the <code>Document</code> was created in memory.
     * @since DOM Level 3
     */
    public String getInputEncoding();

    /**
     * An attribute specifying, as part of the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#NT-XMLDecl'>XML declaration</a>, the encoding of this document. This is <code>null</code> when 
     * unspecified or when it is not known, such as when the 
     * <code>Document</code> was created in memory.
     * @since DOM Level 3
     */
    public String getXmlEncoding();

    /**
     * An attribute specifying, as part of the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#NT-XMLDecl'>XML declaration</a>, whether this document is standalone. This is <code>false</code> when 
     * unspecified.
     * <p ><b>Note:</b>  No verification is done on the value when setting 
     * this attribute. Applications should use 
     * <code>Document.normalizeDocument()</code> with the "validate" 
     * parameter to verify if the value matches the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#sec-rmd'>validity 
     * constraint for standalone document declaration</a> as defined in [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]. 
     * @since DOM Level 3
     */
    public boolean getXmlStandalone();

    /**
     * An attribute specifying, as part of the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#NT-XMLDecl'>XML declaration</a>, whether this document is standalone. This is <code>false</code> when 
     * unspecified.
     * <p ><b>Note:</b>  No verification is done on the value when setting 
     * this attribute. Applications should use 
     * <code>Document.normalizeDocument()</code> with the "validate" 
     * parameter to verify if the value matches the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#sec-rmd'>validity 
     * constraint for standalone document declaration</a> as defined in [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]. 
     * @exception DOMException
     *    NOT_SUPPORTED_ERR: Raised if this document does not support the 
     *   "XML" feature. 
     * @since DOM Level 3
     */
    public void setXmlStandalone(boolean xmlStandalone)
        throws org.w3c.dom.DOMException;

    /**
     *  An attribute specifying, as part of the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#NT-XMLDecl'>XML declaration</a>, the version number of this document. If there is no declaration and if 
     * this document supports the "XML" feature, the value is 
     * <code>"1.0"</code>. If this document does not support the "XML" 
     * feature, the value is always <code>null</code>. Changing this 
     * attribute will affect methods that check for invalid characters in 
     * XML names. Application should invoke 
     * <code>Document.normalizeDocument()</code> in order to check for 
     * invalid characters in the <code>Node</code>s that are already part of 
     * this <code>Document</code>. 
     * <br> DOM applications may use the 
     * <code>DOMImplementation.hasFeature(feature, version)</code> method 
     * with parameter values "XMLVersion" and "1.0" (respectively) to 
     * determine if an implementation supports [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]. DOM 
     * applications may use the same method with parameter values 
     * "XMLVersion" and "1.1" (respectively) to determine if an 
     * implementation supports [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>]. In both 
     * cases, in order to support XML, an implementation must also support 
     * the "XML" feature defined in this specification. <code>Document</code>
     *  objects supporting a version of the "XMLVersion" feature must not 
     * raise a <code>NOT_SUPPORTED_ERR</code> exception for the same version 
     * number when using <code>Document.xmlVersion</code>. 
     * @since DOM Level 3
     */
    public String getXmlVersion();

    /**
     *  An attribute specifying, as part of the <a href='http://www.w3.org/TR/2004/REC-xml-20040204#NT-XMLDecl'>XML declaration</a>, the version number of this document. If there is no declaration and if 
     * this document supports the "XML" feature, the value is 
     * <code>"1.0"</code>. If this document does not support the "XML" 
     * feature, the value is always <code>null</code>. Changing this 
     * attribute will affect methods that check for invalid characters in 
     * XML names. Application should invoke 
     * <code>Document.normalizeDocument()</code> in order to check for 
     * invalid characters in the <code>Node</code>s that are already part of 
     * this <code>Document</code>. 
     * <br> DOM applications may use the 
     * <code>DOMImplementation.hasFeature(feature, version)</code> method 
     * with parameter values "XMLVersion" and "1.0" (respectively) to 
     * determine if an implementation supports [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]. DOM 
     * applications may use the same method with parameter values 
     * "XMLVersion" and "1.1" (respectively) to determine if an 
     * implementation supports [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>]. In both 
     * cases, in order to support XML, an implementation must also support 
     * the "XML" feature defined in this specification. <code>Document</code>
     *  objects supporting a version of the "XMLVersion" feature must not 
     * raise a <code>NOT_SUPPORTED_ERR</code> exception for the same version 
     * number when using <code>Document.xmlVersion</code>. 
     * @exception DOMException
     *    NOT_SUPPORTED_ERR: Raised if the version is set to a value that is 
     *   not supported by this <code>Document</code> or if this document 
     *   does not support the "XML" feature. 
     * @since DOM Level 3
     */
    public void setXmlVersion(String xmlVersion)
        throws org.w3c.dom.DOMException;

    /**
     * An attribute specifying whether error checking is enforced or not. When 
     * set to <code>false</code>, the implementation is free to not test 
     * every possible error case normally defined on DOM operations, and not 
     * raise any <code>DOMException</code> on DOM operations or report 
     * errors while using <code>Document.normalizeDocument()</code>. In case 
     * of error, the behavior is undefined. This attribute is 
     * <code>true</code> by default.
     * @since DOM Level 3
     */
    public boolean getStrictErrorChecking();

    /**
     * An attribute specifying whether error checking is enforced or not. When 
     * set to <code>false</code>, the implementation is free to not test 
     * every possible error case normally defined on DOM operations, and not 
     * raise any <code>DOMException</code> on DOM operations or report 
     * errors while using <code>Document.normalizeDocument()</code>. In case 
     * of error, the behavior is undefined. This attribute is 
     * <code>true</code> by default.
     * @since DOM Level 3
     */
    public void setStrictErrorChecking(boolean strictErrorChecking);

    /**
     *  The location of the document or <code>null</code> if undefined or if 
     * the <code>Document</code> was created using 
     * <code>DOMImplementation.createDocument</code>. No lexical checking is 
     * performed when setting this attribute; this could result in a 
     * <code>null</code> value returned when using <code>Node.baseURI</code>
     * . 
     * <br> Beware that when the <code>Document</code> supports the feature 
     * "HTML" [<a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>DOM Level 2 HTML</a>]
     * , the href attribute of the HTML BASE element takes precedence over 
     * this attribute when computing <code>Node.baseURI</code>. 
     * @since DOM Level 3
     */
    public String getDocumentURI();

    /**
     *  The location of the document or <code>null</code> if undefined or if 
     * the <code>Document</code> was created using 
     * <code>DOMImplementation.createDocument</code>. No lexical checking is 
     * performed when setting this attribute; this could result in a 
     * <code>null</code> value returned when using <code>Node.baseURI</code>
     * . 
     * <br> Beware that when the <code>Document</code> supports the feature 
     * "HTML" [<a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>DOM Level 2 HTML</a>]
     * , the href attribute of the HTML BASE element takes precedence over 
     * this attribute when computing <code>Node.baseURI</code>. 
     * @since DOM Level 3
     */
    public void setDocumentURI(String documentURI);

    /**
     *  Attempts to adopt a node from another document to this document. If 
     * supported, it changes the <code>ownerDocument</code> of the source 
     * node, its children, as well as the attached attribute nodes if there 
     * are any. If the source node has a parent it is first removed from the 
     * child list of its parent. This effectively allows moving a subtree 
     * from one document to another (unlike <code>importNode()</code> which 
     * create a copy of the source node instead of moving it). When it 
     * fails, applications should use <code>Document.importNode()</code> 
     * instead. Note that if the adopted node is already part of this 
     * document (i.e. the source and target document are the same), this 
     * method still has the effect of removing the source node from the 
     * child list of its parent, if any. The following list describes the 
     * specifics for each type of node. 
     * <dl>
     * <dt>ATTRIBUTE_NODE</dt>
     * <dd>The 
     * <code>ownerElement</code> attribute is set to <code>null</code> and 
     * the <code>specified</code> flag is set to <code>true</code> on the 
     * adopted <code>Attr</code>. The descendants of the source 
     * <code>Attr</code> are recursively adopted.</dd>
     * <dt>DOCUMENT_FRAGMENT_NODE</dt>
     * <dd>The 
     * descendants of the source node are recursively adopted.</dd>
     * <dt>DOCUMENT_NODE</dt>
     * <dd>
     * <code>Document</code> nodes cannot be adopted.</dd>
     * <dt>DOCUMENT_TYPE_NODE</dt>
     * <dd>
     * <code>DocumentType</code> nodes cannot be adopted.</dd>
     * <dt>ELEMENT_NODE</dt>
     * <dd><em>Specified</em> attribute nodes of the source element are adopted. Default attributes 
     * are discarded, though if the document being adopted into defines 
     * default attributes for this element name, those are assigned. The 
     * descendants of the source element are recursively adopted.</dd>
     * <dt>ENTITY_NODE</dt>
     * <dd>
     * <code>Entity</code> nodes cannot be adopted.</dd>
     * <dt>ENTITY_REFERENCE_NODE</dt>
     * <dd>Only 
     * the <code>EntityReference</code> node itself is adopted, the 
     * descendants are discarded, since the source and destination documents 
     * might have defined the entity differently. If the document being 
     * imported into provides a definition for this entity name, its value 
     * is assigned.</dd>
     * <dt>NOTATION_NODE</dt>
     * <dd><code>Notation</code> nodes cannot be 
     * adopted.</dd>
     * <dt>PROCESSING_INSTRUCTION_NODE, TEXT_NODE, CDATA_SECTION_NODE, 
     * COMMENT_NODE</dt>
     * <dd>These nodes can all be adopted. No specifics.</dd>
     * </dl> 
     * <p ><b>Note:</b>  Since it does not create new nodes unlike the 
     * <code>Document.importNode()</code> method, this method does not raise 
     * an <code>INVALID_CHARACTER_ERR</code> exception, and applications 
     * should use the <code>Document.normalizeDocument()</code> method to 
     * check if an imported name is not an XML name according to the XML 
     * version in use. 
     * @param source The node to move into this document.
     * @return The adopted node, or <code>null</code> if this operation 
     *   fails, such as when the source node comes from a different 
     *   implementation.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the source node is of type 
     *   <code>DOCUMENT</code>, <code>DOCUMENT_TYPE</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised when the source node is 
     *   readonly.
     * @since DOM Level 3
     */
    public org.w3c.dom.Node adoptNode(org.w3c.dom.Node source)
        throws org.w3c.dom.DOMException;

    /**
     *  The configuration used when <code>Document.normalizeDocument()</code> 
     * is invoked. 
     * @since DOM Level 3
     */
    public org.w3c.dom.DOMConfiguration getDomConfig();

    /**
     *  This method acts as if the document was going through a save and load 
     * cycle, putting the document in a "normal" form. As a consequence, 
     * this method updates the replacement tree of 
     * <code>EntityReference</code> nodes and normalizes <code>Text</code> 
     * nodes, as defined in the method <code>Node.normalize()</code>. 
     * <br> Otherwise, the actual result depends on the features being set on 
     * the <code>Document.domConfig</code> object and governing what 
     * operations actually take place. Noticeably this method could also 
     * make the document namespace well-formed according to the algorithm 
     * described in , check the character normalization, remove the 
     * <code>CDATASection</code> nodes, etc. See 
     * <code>DOMConfiguration</code> for details. 
     * <pre>// Keep in the document 
     * the information defined // in the XML Information Set (Java example) 
     * DOMConfiguration docConfig = myDocument.getDomConfig(); 
     * docConfig.setParameter("infoset", Boolean.TRUE); 
     * myDocument.normalizeDocument();</pre>
     * 
     * <br>Mutation events, when supported, are generated to reflect the 
     * changes occurring on the document.
     * <br> If errors occur during the invocation of this method, such as an 
     * attempt to update a read-only node or a <code>Node.nodeName</code> 
     * contains an invalid character according to the XML version in use, 
     * errors or warnings (<code>DOMError.SEVERITY_ERROR</code> or 
     * <code>DOMError.SEVERITY_WARNING</code>) will be reported using the 
     * <code>DOMErrorHandler</code> object associated with the "error-handler
     * " parameter. Note this method might also report fatal errors (
     * <code>DOMError.SEVERITY_FATAL_ERROR</code>) if an implementation 
     * cannot recover from an error. 
     * @since DOM Level 3
     */
    public void normalizeDocument();

    /**
     * Rename an existing node of type <code>ELEMENT_NODE</code> or 
     * <code>ATTRIBUTE_NODE</code>.
     * <br>When possible this simply changes the name of the given node, 
     * otherwise this creates a new node with the specified name and 
     * replaces the existing node with the new node as described below.
     * <br>If simply changing the name of the given node is not possible, the 
     * following operations are performed: a new node is created, any 
     * registered event listener is registered on the new node, any user 
     * data attached to the old node is removed from that node, the old node 
     * is removed from its parent if it has one, the children are moved to 
     * the new node, if the renamed node is an <code>Element</code> its 
     * attributes are moved to the new node, the new node is inserted at the 
     * position the old node used to have in its parent's child nodes list 
     * if it has one, the user data that was attached to the old node is 
     * attached to the new node.
     * <br>When the node being renamed is an <code>Element</code> only the 
     * specified attributes are moved, default attributes originated from 
     * the DTD are updated according to the new element name. In addition, 
     * the implementation may update default attributes from other schemas. 
     * Applications should use <code>Document.normalizeDocument()</code> to 
     * guarantee these attributes are up-to-date.
     * <br>When the node being renamed is an <code>Attr</code> that is 
     * attached to an <code>Element</code>, the node is first removed from 
     * the <code>Element</code> attributes map. Then, once renamed, either 
     * by modifying the existing node or creating a new one as described 
     * above, it is put back.
     * <br>In addition,
     * <ul>
     * <li> a user data event <code>NODE_RENAMED</code> is fired, 
     * </li>
     * <li> 
     * when the implementation supports the feature "MutationNameEvents", 
     * each mutation operation involved in this method fires the appropriate 
     * event, and in the end the event {
     * <code>http://www.w3.org/2001/xml-events</code>, 
     * <code>DOMElementNameChanged</code>} or {
     * <code>http://www.w3.org/2001/xml-events</code>, 
     * <code>DOMAttributeNameChanged</code>} is fired. 
     * </li>
     * </ul>
     * @param n The node to rename.
     * @param namespaceURI The new namespace URI.
     * @param qualifiedName The new qualified name.
     * @return The renamed node. This is either the specified node or the new 
     *   node that was created to replace the specified node.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised when the type of the specified node is 
     *   neither <code>ELEMENT_NODE</code> nor <code>ATTRIBUTE_NODE</code>, 
     *   or if the implementation does not support the renaming of the 
     *   document element.
     *   <br>INVALID_CHARACTER_ERR: Raised if the new qualified name is not an 
     *   XML name according to the XML version in use specified in the 
     *   <code>Document.xmlVersion</code> attribute.
     *   <br>WRONG_DOCUMENT_ERR: Raised when the specified node was created 
     *   from a different document than this document.
     *   <br>NAMESPACE_ERR: Raised if the <code>qualifiedName</code> is a 
     *   malformed qualified name, if the <code>qualifiedName</code> has a 
     *   prefix and the <code>namespaceURI</code> is <code>null</code>, or 
     *   if the <code>qualifiedName</code> has a prefix that is "xml" and 
     *   the <code>namespaceURI</code> is different from "<a href='http://www.w3.org/XML/1998/namespace'>
     *   http://www.w3.org/XML/1998/namespace</a>" [<a href='http://www.w3.org/TR/1999/REC-xml-names-19990114/'>XML Namespaces</a>]
     *   . Also raised, when the node being renamed is an attribute, if the 
     *   <code>qualifiedName</code>, or its prefix, is "xmlns" and the 
     *   <code>namespaceURI</code> is different from "<a href='http://www.w3.org/2000/xmlns/'>http://www.w3.org/2000/xmlns/</a>".
     * @since DOM Level 3
     */
    public org.w3c.dom.Node renameNode(org.w3c.dom.Node n, 
                                       String namespaceURI, 
                                       String qualifiedName)
        throws org.w3c.dom.DOMException;
}
