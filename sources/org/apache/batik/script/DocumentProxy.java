/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import java.lang.ref.WeakReference;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Proxy to a <code>Document</code> using a <code>WeakReference</code> to
 * allow the document to be discared when not used outside of the intepreter.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class DocumentProxy implements Document {
    protected WeakReference ref = null;

    public DocumentProxy(Document document)
    {
        ref = new WeakReference(document);
    }

    private Document ref()
    {
        return (Document)ref.get();
    }

    // Node implementation

    /*
     * The name of this node, depending on its type; see the table above.
     */
    public String getNodeName()
    {
        return ref().getNodeName();
    }

    /**
     * The value of this node, depending on its type; see the table above.
     * When it is defined to be <code>null</code>, setting it has no effect.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than
     *   fit in a <code>DOMString</code> variable on the implementation
     *   platform.
     */
    public String getNodeValue()
        throws DOMException
    {
        return ref().getNodeValue();
    }

    public void setNodeValue(String nodeValue)
        throws DOMException
    {
        ref().setNodeValue(nodeValue);
    }

    /**
     * A code representing the type of the underlying object, as defined above.
     */
    public short getNodeType()
    {
        return ref().getNodeType();
    }

    /**
     * The parent of this node. All nodes, except <code>Attr</code>,
     * <code>Document</code>, <code>DocumentFragment</code>,
     * <code>Entity</code>, and <code>Notation</code> may have a parent.
     * However, if a node has just been created and not yet added to the
     * tree, or if it has been removed from the tree, this is
     * <code>null</code>.
     */
    public Node getParentNode()
    {
        return ref().getParentNode();
    }

    /**
     * A <code>NodeList</code> that contains all children of this node. If
     * there are no children, this is a <code>NodeList</code> containing no
     * nodes.
     */
    public NodeList getChildNodes()
    {
        return ref().getChildNodes();
    }

    /**
     * The first child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getFirstChild()
    {
        return ref().getFirstChild();
    }

    /**
     * The last child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getLastChild()
    {
        return ref().getLastChild();
    }

    /**
     * The node immediately preceding this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getPreviousSibling()
    {
        return ref().getPreviousSibling();
    }

    /**
     * The node immediately following this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getNextSibling()
    {
        return ref().getNextSibling();
    }

    /**
     * A <code>NamedNodeMap</code> containing the attributes of this node (if
     * it is an <code>Element</code>) or <code>null</code> otherwise.
     */
    public NamedNodeMap getAttributes()
    {
        return ref().getAttributes();
    }

    /**
     * The <code>Document</code> object associated with this node. This is
     * also the <code>Document</code> object used to create new nodes. When
     * this node is a <code>Document</code> or a <code>DocumentType</code>
     * which is not used with any <code>Document</code> yet, this is
     * <code>null</code>.
     * @version DOM Level 2
     */
    public Document getOwnerDocument()
    {
        return ref().getOwnerDocument();
    }

    /**
     * Inserts the node <code>newChild</code> before the existing child node
     * <code>refChild</code>. If <code>refChild</code> is <code>null</code>,
     * insert <code>newChild</code> at the end of the list of children.
     * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
     * all of its children are inserted, in the same order, before
     * <code>refChild</code>. If the <code>newChild</code> is already in the
     * tree, it is first removed.
     * @param newChildThe node to insert.
     * @param refChildThe reference node, i.e., the node before which the new
     *   node must be inserted.
     * @return The node being inserted.
     * @exception DOMException
     *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *   allow children of the type of the <code>newChild</code> node, or if
     *   the node to insert is one of this node's ancestors.
     *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *   from a different document than the one that created this node.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
     *   if the parent of the node being inserted is readonly.
     *   <br>NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child of
     *   this node.
     */
    public Node insertBefore(Node newChild,
                             Node refChild)
        throws DOMException
    {
        return ref().insertBefore(newChild, refChild);
    }

    /**
     * Replaces the child node <code>oldChild</code> with <code>newChild</code>
     *  in the list of children, and returns the <code>oldChild</code> node.
     * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
     * <code>oldChild</code> is replaced by all of the
     * <code>DocumentFragment</code> children, which are inserted in the
     * same order. If the <code>newChild</code> is already in the tree, it
     * is first removed.
     * @param newChildThe new node to put in the child list.
     * @param oldChildThe node being replaced in the list.
     * @return The node replaced.
     * @exception DOMException
     *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *   allow children of the type of the <code>newChild</code> node, or if
     *   the node to put in is one of this node's ancestors.
     *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *   from a different document than the one that created this node.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node or the parent of
     *   the new node is readonly.
     *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
     *   this node.
     */
    public Node replaceChild(Node newChild,
                             Node oldChild)
        throws DOMException
    {
        return ref().replaceChild(newChild, oldChild);
    }

    /**
     * Removes the child node indicated by <code>oldChild</code> from the list
     * of children, and returns it.
     * @param oldChildThe node being removed.
     * @return The node removed.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
     *   this node.
     */
    public Node removeChild(Node oldChild)
        throws DOMException
    {
        return ref().removeChild(oldChild);
    }

    /**
     * Adds the node <code>newChild</code> to the end of the list of children
     * of this node. If the <code>newChild</code> is already in the tree, it
     * is first removed.
     * @param newChildThe node to add.If it is a <code>DocumentFragment</code>
     *    object, the entire contents of the document fragment are moved
     *   into the child list of this node
     * @return The node added.
     * @exception DOMException
     *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *   allow children of the type of the <code>newChild</code> node, or if
     *   the node to append is one of this node's ancestors.
     *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *   from a different document than the one that created this node.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public Node appendChild(Node newChild)
        throws DOMException
    {
        return ref().appendChild(newChild);
    }

    /**
     * Returns whether this node has any children.
     * @return  <code>true</code> if this node has any children,
     *   <code>false</code> otherwise.
     */
    public boolean hasChildNodes()
    {
        return ref().hasChildNodes();
    }

    /**
     * Returns a duplicate of this node, i.e., serves as a generic copy
     * constructor for nodes. The duplicate node has no parent; (
     * <code>parentNode</code> is <code>null</code>.).
     * <br>Cloning an <code>Element</code> copies all attributes and their
     * values, including those generated by the XML processor to represent
     * defaulted attributes, but this method does not copy any text it
     * contains unless it is a deep clone, since the text is contained in a
     * child <code>Text</code> node. Cloning an <code>Attribute</code>
     * directly, as opposed to be cloned as part of an <code>Element</code>
     * cloning operation, returns a specified attribute (
     * <code>specified</code> is <code>true</code>). Cloning any other type
     * of node simply returns a copy of this node.
     * <br>Note that cloning an immutable subtree results in a mutable copy,
     * but the children of an <code>EntityReference</code> clone are readonly
     * . In addition, clones of unspecified <code>Attr</code> nodes are
     * specified. And, cloning <code>Document</code>,
     * <code>DocumentType</code>, <code>Entity</code>, and
     * <code>Notation</code> nodes is implementation dependent.
     * @param deepIf <code>true</code>, recursively clone the subtree under
     *   the specified node; if <code>false</code>, clone only the node
     *   itself (and its attributes, if it is an <code>Element</code>).
     * @return The duplicate node.
     */
    public Node cloneNode(boolean deep)
    {
        return ref().cloneNode(deep);
    }

    /**
     * Puts all <code>Text</code> nodes in the full depth of the sub-tree
     * underneath this <code>Node</code>, including attribute nodes, into a
     * "normal" form where only structure (e.g., elements, comments,
     * processing instructions, CDATA sections, and entity references)
     * separates <code>Text</code> nodes, i.e., there are neither adjacent
     * <code>Text</code> nodes nor empty <code>Text</code> nodes. This can
     * be used to ensure that the DOM view of a document is the same as if
     * it were saved and re-loaded, and is useful when operations (such as
     * XPointer  lookups) that depend on a particular document tree
     * structure are to be used.In cases where the document contains
     * <code>CDATASections</code>, the normalize operation alone may not be
     * sufficient, since XPointers do not differentiate between
     * <code>Text</code> nodes and <code>CDATASection</code> nodes.
     * @version DOM Level 2
     */
    public void normalize()
    {
        ref().normalize();
    }

    /**
     * Tests whether the DOM implementation implements a specific feature and
     * that feature is supported by this node.
     * @param featureThe name of the feature to test. This is the same name
     *   which can be passed to the method <code>hasFeature</code> on
     *   <code>DOMImplementation</code>.
     * @param versionThis is the version number of the feature to test. In
     *   Level 2, version 1, this is the string "2.0". If the version is not
     *   specified, supporting any version of the feature will cause the
     *   method to return <code>true</code>.
     * @return Returns <code>true</code> if the specified feature is
     *   supported on this node, <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean isSupported(String feature,
                               String version)
    {
        return ref().isSupported(feature, version);
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.
     * <br>This is not a computed value that is the result of a namespace
     * lookup based on an examination of the namespace declarations in
     * scope. It is merely the namespace URI given at creation time.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.Per
     * the Namespaces in XML Specification  an attribute does not inherit
     * its namespace from the element it is attached to. If an attribute is
     * not explicitly given a namespace, it simply has no namespace.
     * @since DOM Level 2
     */
    public String getNamespaceURI()
    {
        return ref().getNamespaceURI();
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     * <br>Note that setting this attribute, when permitted, changes the
     * <code>nodeName</code> attribute, which holds the qualified name, as
     * well as the <code>tagName</code> and <code>name</code> attributes of
     * the <code>Element</code> and <code>Attr</code> interfaces, when
     * applicable.
     * <br>Note also that changing the prefix of an attribute that is known to
     * have a default value, does not make a new attribute with the default
     * value and the original prefix appear, since the
     * <code>namespaceURI</code> and <code>localName</code> do not change.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified prefix contains an
     *   illegal character.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br>NAMESPACE_ERR: Raised if the specified <code>prefix</code> is
     *   malformed, if the <code>namespaceURI</code> of this node is
     *   <code>null</code>, if the specified prefix is "xml" and the
     *   <code>namespaceURI</code> of this node is different from "
     *   http://www.w3.org/XML/1998/namespace", if this node is an attribute
     *   and the specified prefix is "xmlns" and the
     *   <code>namespaceURI</code> of this node is different from "
     *   http://www.w3.org/2000/xmlns/", or if this node is an attribute and
     *   the <code>qualifiedName</code> of this node is "xmlns" .
     * @since DOM Level 2
     */
    public String getPrefix()
    {
        return ref().getPrefix();
    }

    public void setPrefix(String prefix)
        throws DOMException
    {
        ref().setPrefix(prefix);
    }

    /**
     * Returns the local part of the qualified name of this node.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.
     * @since DOM Level 2
     */
    public String getLocalName()
    {
        return ref().getLocalName();
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     * @return <code>true</code> if this node has any attributes,
     *   <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean hasAttributes()
    {
        return ref().hasAttributes();
    }

    /**
     * The Document Type Declaration (see <code>DocumentType</code>)
     * associated with this document. For HTML documents as well as XML
     * documents without a document type declaration this returns
     * <code>null</code>. The DOM Level 2 does not support editing the
     * Document Type Declaration. <code>docType</code> cannot be altered in
     * any way, including through the use of methods inherited from the
     * <code>Node</code> interface, such as <code>insertNode</code> or
     * <code>removeNode</code>.
     */
    public DocumentType getDoctype()
    {
        return ref().getDoctype();
    }

    /**
     * The <code>DOMImplementation</code> object that handles this document. A
     * DOM application may use objects from multiple implementations.
     */
    public DOMImplementation getImplementation()
    {
        return ref().getImplementation();
    }

    /**
     * This is a convenience attribute that allows direct access to the child
     * node that is the root element of the document. For HTML documents,
     * this is the element with the tagName "HTML".
     */
    public Element getDocumentElement()
    {
        return ref().getDocumentElement();
    }

    /**
     * Creates an element of the type specified. Note that the instance
     * returned implements the <code>Element</code> interface, so attributes
     * can be specified directly on the returned object.
     * <br>In addition, if there are known attributes with default values,
     * <code>Attr</code> nodes representing them are automatically created
     * and attached to the element.
     * <br>To create an element with a qualified name and namespace URI, use
     * the <code>createElementNS</code> method.
     * @param tagNameThe name of the element type to instantiate. For XML,
     *   this is case-sensitive. For HTML, the <code>tagName</code>
     *   parameter may be provided in any case, but it must be mapped to the
     *   canonical uppercase form by the DOM implementation.
     * @return A new <code>Element</code> object with the
     *   <code>nodeName</code> attribute set to <code>tagName</code>, and
     *   <code>localName</code>, <code>prefix</code>, and
     *   <code>namespaceURI</code> set to <code>null</code>.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *   illegal character.
     */
    public Element createElement(String tagName)
        throws DOMException
    {
        return ref().createElement(tagName);
    }

    /**
     * Creates an empty <code>DocumentFragment</code> object.
     * @return A new <code>DocumentFragment</code>.
     */
    public DocumentFragment createDocumentFragment()
    {
        return ref().createDocumentFragment();
    }

    /**
     * Creates a <code>Text</code> node given the specified string.
     * @param dataThe data for the node.
     * @return The new <code>Text</code> object.
     */
    public Text createTextNode(String data)
    {
        return ref().createTextNode(data);
    }

    /**
     * Creates a <code>Comment</code> node given the specified string.
     * @param dataThe data for the node.
     * @return The new <code>Comment</code> object.
     */
    public Comment createComment(String data)
    {
        return ref().createComment(data);
    }

    /**
     * Creates a <code>CDATASection</code> node whose value is the specified
     * string.
     * @param dataThe data for the <code>CDATASection</code> contents.
     * @return The new <code>CDATASection</code> object.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
     */
    public CDATASection createCDATASection(String data)
        throws DOMException
    {
        return ref().createCDATASection(data);
    }

    /**
     * Creates a <code>ProcessingInstruction</code> node given the specified
     * name and data strings.
     * @param targetThe target part of the processing instruction.
     * @param dataThe data for the node.
     * @return The new <code>ProcessingInstruction</code> object.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified target contains an
     *   illegal character.
     *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
     */
    public ProcessingInstruction createProcessingInstruction(String target,
                                                             String data)
        throws DOMException
    {
        return ref().createProcessingInstruction(target, data);
    }

    /**
     * Creates an <code>Attr</code> of the given name. Note that the
     * <code>Attr</code> instance can then be set on an <code>Element</code>
     * using the <code>setAttributeNode</code> method.
     * <br>To create an attribute with a qualified name and namespace URI, use
     * the <code>createAttributeNS</code> method.
     * @param nameThe name of the attribute.
     * @return A new <code>Attr</code> object with the <code>nodeName</code>
     *   attribute set to <code>name</code>, and <code>localName</code>,
     *   <code>prefix</code>, and <code>namespaceURI</code> set to
     *   <code>null</code>. The value of the attribute is the empty string.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *   illegal character.
     */
    public Attr createAttribute(String name)
        throws DOMException
    {
        return ref().createAttribute(name);
    }

    /**
     * Creates an <code>EntityReference</code> object. In addition, if the
     * referenced entity is known, the child list of the
     * <code>EntityReference</code> node is made the same as that of the
     * corresponding <code>Entity</code> node.If any descendant of the
     * <code>Entity</code> node has an unbound namespace prefix, the
     * corresponding descendant of the created <code>EntityReference</code>
     * node is also unbound; (its <code>namespaceURI</code> is
     * <code>null</code>). The DOM Level 2 does not support any mechanism to
     * resolve namespace prefixes.
     * @param nameThe name of the entity to reference.
     * @return The new <code>EntityReference</code> object.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *   illegal character.
     *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
     */
    public EntityReference createEntityReference(String name)
        throws DOMException
    {
        return ref().createEntityReference(name);
    }

    /**
     * Returns a <code>NodeList</code> of all the <code>Elements</code> with a
     * given tag name in the order in which they are encountered in a
     * preorder traversal of the <code>Document</code> tree.
     * @param tagnameThe name of the tag to match on. The special value "*"
     *   matches all tags.
     * @return A new <code>NodeList</code> object containing all the matched
     *   <code>Elements</code>.
     */
    public NodeList getElementsByTagName(String tagname)
    {
        return ref().getElementsByTagName(tagname);
    }

    /**
     * Imports a node from another document to this document. The returned
     * node has no parent; (<code>parentNode</code> is <code>null</code>).
     * The source node is not altered or removed from the original document;
     * this method creates a new copy of the source node.
     * <br>For all nodes, importing a node creates a node object owned by the
     * importing document, with attribute values identical to the source
     * node's <code>nodeName</code> and <code>nodeType</code>, plus the
     * attributes related to namespaces (<code>prefix</code>,
     * <code>localName</code>, and <code>namespaceURI</code>). As in the
     * <code>cloneNode</code> operation on a <code>Node</code>, the source
     * node is not altered.
     * <br>Additional information is copied as appropriate to the
     * <code>nodeType</code>, attempting to mirror the behavior expected if
     * a fragment of XML or HTML source was copied from one document to
     * another, recognizing that the two documents may have different DTDs
     * in the XML case. The following list describes the specifics for each
     * type of node.
     * <dl>
     * <dt>ATTRIBUTE_NODE</dt>
     * <dd>The <code>ownerElement</code> attribute
     * is set to <code>null</code> and the <code>specified</code> flag is
     * set to <code>true</code> on the generated <code>Attr</code>. The
     * descendants of the source <code>Attr</code> are recursively imported
     * and the resulting nodes reassembled to form the corresponding subtree.
     * Note that the <code>deep</code> parameter has no effect on
     * <code>Attr</code> nodes; they always carry their children with them
     * when imported.</dd>
     * <dt>DOCUMENT_FRAGMENT_NODE</dt>
     * <dd>If the <code>deep</code> option
     * was set to <code>true</code>, the descendants of the source element
     * are recursively imported and the resulting nodes reassembled to form
     * the corresponding subtree. Otherwise, this simply generates an empty
     * <code>DocumentFragment</code>.</dd>
     * <dt>DOCUMENT_NODE</dt>
     * <dd><code>Document</code>
     * nodes cannot be imported.</dd>
     * <dt>DOCUMENT_TYPE_NODE</dt>
     * <dd><code>DocumentType</code>
     * nodes cannot be imported.</dd>
     * <dt>ELEMENT_NODE</dt>
     * <dd>Specified attribute nodes of the
     * source element are imported, and the generated <code>Attr</code>
     * nodes are attached to the generated <code>Element</code>. Default
     * attributes are not copied, though if the document being imported into
     * defines default attributes for this element name, those are assigned.
     * If the <code>importNode</code> <code>deep</code> parameter was set to
     * <code>true</code>, the descendants of the source element are
     * recursively imported and the resulting nodes reassembled to form the
     * corresponding subtree.</dd>
     * <dt>ENTITY_NODE</dt>
     * <dd><code>Entity</code> nodes can be
     * imported, however in the current release of the DOM the
     * <code>DocumentType</code> is readonly. Ability to add these imported
     * nodes to a <code>DocumentType</code> will be considered for addition
     * to a future release of the DOM.On import, the <code>publicId</code>,
     * <code>systemId</code>, and <code>notationName</code> attributes are
     * copied. If a <code>deep</code> import is requested, the descendants
     * of the the source <code>Entity</code> are recursively imported and
     * the resulting nodes reassembled to form the corresponding subtree.</dd>
     * <dt>
     * ENTITY_REFERENCE_NODE</dt>
     * <dd>Only the <code>EntityReference</code> itself is
     * copied, even if a <code>deep</code> import is requested, since the
     * source and destination documents might have defined the entity
     * differently. If the document being imported into provides a
     * definition for this entity name, its value is assigned.</dd>
     * <dt>NOTATION_NODE</dt>
     * <dd>
     * <code>Notation</code> nodes can be imported, however in the current
     * release of the DOM the <code>DocumentType</code> is readonly. Ability
     * to add these imported nodes to a <code>DocumentType</code> will be
     * considered for addition to a future release of the DOM.On import, the
     * <code>publicId</code> and <code>systemId</code> attributes are copied.
     * Note that the <code>deep</code> parameter has no effect on
     * <code>Notation</code> nodes since they never have any children.</dd>
     * <dt>
     * PROCESSING_INSTRUCTION_NODE</dt>
     * <dd>The imported node copies its
     * <code>target</code> and <code>data</code> values from those of the
     * source node.</dd>
     * <dt>TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE</dt>
     * <dd>These three
     * types of nodes inheriting from <code>CharacterData</code> copy their
     * <code>data</code> and <code>length</code> attributes from those of
     * the source node.</dd>
     *
     * @param importedNodeThe node to import.
     * @param deepIf <code>true</code>, recursively import the subtree under
     *   the specified node; if <code>false</code>, import only the node
     *   itself, as explained above. This has no effect on <code>Attr</code>
     *   , <code>EntityReference</code>, and <code>Notation</code> nodes.
     * @return The imported node that belongs to this <code>Document</code>.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the type of node being imported is not
     *   supported.
     * @since DOM Level 2
     */
    public Node importNode(Node importedNode,
                           boolean deep)
        throws DOMException
    {
        return ref().importNode(importedNode, deep);
    }

    /**
     * Creates an element of the given qualified name and namespace URI.
     * HTML-only DOM implementations do not need to implement this method.
     * @param namespaceURIThe namespace URI of the element to create.
     * @param qualifiedNameThe qualified name of the element type to
     *   instantiate.
     * @return A new <code>Element</code> object with the following
     *   attributes:AttributeValue<code>Node.nodeName</code>
     *   <code>qualifiedName</code><code>Node.namespaceURI</code>
     *   <code>namespaceURI</code><code>Node.prefix</code>prefix, extracted
     *   from <code>qualifiedName</code>, or <code>null</code> if there is
     *   no prefix<code>Node.localName</code>local name, extracted from
     *   <code>qualifiedName</code><code>Element.tagName</code>
     *   <code>qualifiedName</code>
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified qualified name
     *   contains an illegal character.
     *   <br>NAMESPACE_ERR: Raised if the <code>qualifiedName</code> is
     *   malformed, if the <code>qualifiedName</code> has a prefix and the
     *   <code>namespaceURI</code> is <code>null</code>, or if the
     *   <code>qualifiedName</code> has a prefix that is "xml" and the
     *   <code>namespaceURI</code> is different from "
     *   http://www.w3.org/XML/1998/namespace" .
     * @since DOM Level 2
     */
    public Element createElementNS(String namespaceURI,
                                   String qualifiedName)
        throws DOMException
    {
        return ref().createElementNS(namespaceURI, qualifiedName);
    }

    /**
     * Creates an attribute of the given qualified name and namespace URI.
     * HTML-only DOM implementations do not need to implement this method.
     * @param namespaceURIThe namespace URI of the attribute to create.
     * @param qualifiedNameThe qualified name of the attribute to instantiate.
     * @return A new <code>Attr</code> object with the following attributes:
     *   AttributeValue<code>Node.nodeName</code>qualifiedName
     *   <code>Node.namespaceURI</code><code>namespaceURI</code>
     *   <code>Node.prefix</code>prefix, extracted from
     *   <code>qualifiedName</code>, or <code>null</code> if there is no
     *   prefix<code>Node.localName</code>local name, extracted from
     *   <code>qualifiedName</code><code>Attr.name</code>
     *   <code>qualifiedName</code><code>Node.nodeValue</code>the empty
     *   string
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified qualified name
     *   contains an illegal character.
     *   <br>NAMESPACE_ERR: Raised if the <code>qualifiedName</code> is
     *   malformed, if the <code>qualifiedName</code> has a prefix and the
     *   <code>namespaceURI</code> is <code>null</code>, if the
     *   <code>qualifiedName</code> has a prefix that is "xml" and the
     *   <code>namespaceURI</code> is different from "
     *   http://www.w3.org/XML/1998/namespace", or if the
     *   <code>qualifiedName</code> is "xmlns" and the
     *   <code>namespaceURI</code> is different from "
     *   http://www.w3.org/2000/xmlns/".
     * @since DOM Level 2
     */
    public Attr createAttributeNS(String namespaceURI,
                                  String qualifiedName)
                                  throws DOMException
    {
        return ref().createAttributeNS(namespaceURI, qualifiedName);
    }

    /**
     * Returns a <code>NodeList</code> of all the <code>Elements</code> with a
     * given local name and namespace URI in the order in which they are
     * encountered in a preorder traversal of the <code>Document</code> tree.
     * @param namespaceURIThe namespace URI of the elements to match on. The
     *   special value "*" matches all namespaces.
     * @param localNameThe local name of the elements to match on. The
     *   special value "*" matches all local names.
     * @return A new <code>NodeList</code> object containing all the matched
     *   <code>Elements</code>.
     * @since DOM Level 2
     */
    public NodeList getElementsByTagNameNS(String namespaceURI,
                                           String localName)
    {
        return ref().getElementsByTagNameNS(namespaceURI, localName);
    }

    /**
     * Returns the <code>Element</code> whose <code>ID</code> is given by
     * <code>elementId</code>. If no such element exists, returns
     * <code>null</code>. Behavior is not defined if more than one element
     * has this <code>ID</code>. The DOM implementation must have
     * information that says which attributes are of type ID. Attributes
     * with the name "ID" are not of type ID unless so defined.
     * Implementations that do not know whether attributes are of type ID or
     * not are expected to return <code>null</code>.
     * @param elementIdThe unique <code>id</code> value for an element.
     * @return The matching element.
     * @since DOM Level 2
     */
    public Element getElementById(String elementId)
    {
        return ref().getElementById(elementId);
    }
}
