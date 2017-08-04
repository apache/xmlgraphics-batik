/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.XMLConstants;
import org.apache.batik.xml.XMLUtilities;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A collection of utility functions for the DOM.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DOMUtilities extends XMLUtilities implements XMLConstants {

    /**
     * Does not need to be instantiated.
     */
    protected DOMUtilities() {
    }
 
    /**
     * A node in a linked list of prefix to namespace name mappings.
     */
    private static class NSMap {

        /**
         * The prefix to map.
         */
        private String prefix;

        /**
         * The namespace name that the prefix maps to.
         * The empty string is used to represent no namespace.
         */
        private String ns;

        /**
         * The next namespace prefix mapping in the list.
         */
        private NSMap next;

        /**
         * The next number to use when generating prefixes.
         * A prefix of the form <code>"a" + number</code> is generated when
         * serializing a node whose namespace URI does not correspond to
         * a prefix in scope.
         */
        private int nextPrefixNumber;

        /**
         * Constructs a new namespace prefix mapping object with the
         * XML and XMLNS namespaces predeclared.
         */
        public static NSMap create() {
            return new NSMap().declare(XMLConstants.XML_PREFIX,
                                       XMLConstants.XML_NAMESPACE_URI)
                              .declare(XMLConstants.XMLNS_PREFIX,
                                       XMLConstants.XMLNS_NAMESPACE_URI);
        }

        /**
         * Creates a new <code>NSMap</code> object.
         */
        private NSMap() {
        }

        /**
         * Declares a new prefix mapping by returning a new
         * <code>NSMap</code> object that links to this one.
         */
        public NSMap declare(String prefix, String ns) {
            NSMap m = new NSMap();
            m.prefix = prefix;
            m.ns = ns;
            m.next = this;
            m.nextPrefixNumber = this.nextPrefixNumber;
            return m;
        }

        /**
         * Returns a new, generated namespace prefix.
         */
        public String getNewPrefix() {
            String prefix;
            do {
                prefix = "a" + nextPrefixNumber++;
            } while (getNamespace(prefix) != null);
            return prefix;
        }

        /**
         * Returns the namespace URI that the specified prefix
         * maps to, or <code>null</code> if the prefix has not
         * been declared.
         */
        public String getNamespace(String prefix) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (m.prefix.equals(prefix)) {
                    return m.ns;
                }
            }
            return null;
        }

        /**
         * Returns the prefix appropriate for an element that maps to specified
         * namespace URI.  If the specified namespace is the default namespace
         * (i.e., it has an empty string prefix mapping to it), then the empty
         * string is returned.  If there is no appropriate prefix,
         * <code>null</code> is returned.
         */
        public String getPrefixForElement(String ns) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (ns.equals(m.ns)) {
                    return m.prefix;
                }
            }
            return null;
        }

        /**
         * Returns the prefix appropriate for an attribute that maps to
         * specified namespace URI.  If there is no appropriate prefix,
         * <code>null</code> is returned.
         */
        public String getPrefixForAttr(String ns) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (ns.equals(m.ns) && !m.prefix.equals("")) {
                    return m.prefix;
                }
            }
            return null;
        }
    }

    /**
     * Serializes the specified <code>Document</code>, writing it to the given
     * <code>Writer</code>.
     */
    public static void writeDocument(Document doc, Writer w) throws IOException {
        AbstractDocument d = (AbstractDocument) doc;
        if (doc.getDocumentElement() == null) {
            throw new IOException("No document element");
        }
        NSMap m = NSMap.create();
        for (Node n = doc.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            writeNode(n, w, m, "1.1".equals(d.getXmlVersion()));
        }
    }

    protected static void writeNode(Node n, Writer w, NSMap m, boolean isXML11)
            throws IOException {
        switch (n.getNodeType()) {
        case Node.ELEMENT_NODE: {
            if (n.hasAttributes()) {
                NamedNodeMap attr = n.getAttributes();
                int len = attr.getLength();
                for (int i = 0; i < len; i++) {
                    Attr a = (Attr)attr.item(i);
                    String name = a.getNodeName();
                    if (name.startsWith("xmlns")) {
                        if (name.length() == 5) {
                            m = m.declare("", a.getNodeValue());
                        } else {
                            String prefix = name.substring(6);
                            m = m.declare(prefix, a.getNodeValue());
                        }
                    }
                }
            }

            w.write('<');
            String ns = n.getNamespaceURI();
            String tagName;
            if (ns == null) {
                tagName = n.getNodeName();
                w.write(tagName);
                if (!"".equals(m.getNamespace(""))) {
                    w.write(" xmlns=\"\"");
                    m = m.declare("", "");
                }
            } else {
                String prefix = n.getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                if (ns.equals(m.getNamespace(prefix))) {
                    tagName = n.getNodeName();
                    w.write(tagName);
                } else {
                    prefix = m.getPrefixForElement(ns);
                    if (prefix == null) {
                        prefix = m.getNewPrefix();
                        tagName = prefix + ':' + n.getLocalName();
                        w.write(tagName + " xmlns:" + prefix + "=\""
                                 + contentToString(ns, isXML11) + '"');
                        m = m.declare(prefix, ns);
                    } else {
                        if (prefix.equals("")) {
                            tagName = n.getLocalName();
                        } else {
                            tagName = prefix + ':' + n.getLocalName();
                        }
                        w.write(tagName);
                    }
                }
            }

            if (n.hasAttributes()) {
                NamedNodeMap attr = n.getAttributes();
                int len = attr.getLength();
                for (int i = 0; i < len; i++) {
                    Attr a = (Attr)attr.item(i);
                    String name = a.getNodeName();
                    String prefix = a.getPrefix();
                    String ans = a.getNamespaceURI();
                    if (ans != null &&
                            !("xmlns".equals(prefix) || name.equals("xmlns"))) {
                        if (prefix != null
                                && !ans.equals(m.getNamespace(prefix))
                                || prefix == null) {
                            prefix = m.getPrefixForAttr(ans);
                            if (prefix == null) {
                                prefix = m.getNewPrefix();
                                m = m.declare(prefix, ans);
                                w.write(" xmlns:" + prefix + "=\""
                                         + contentToString(ans, isXML11) + '"');
                            }
                            name = prefix + ':' + a.getLocalName();
                        }
                    }
                    w.write(' ' + name + "=\""
                             + contentToString(a.getNodeValue(), isXML11)
                             + '"');
                }
            }

            Node c = n.getFirstChild();
            if (c != null) {
                w.write('>');
                do {
                    writeNode(c, w, m, isXML11);
                    c = c.getNextSibling();
                } while (c != null);
                w.write("</" + tagName + '>');
            } else {
                w.write("/>");
            }
            break;
        }
        case Node.TEXT_NODE:
            w.write(contentToString(n.getNodeValue(), isXML11));
            break;
        case Node.CDATA_SECTION_NODE: {
            String data = n.getNodeValue();
            if (data.indexOf("]]>") != -1) {
                throw new IOException("Unserializable CDATA section node");
            }
            w.write("<![CDATA["
                     + assertValidCharacters(data, isXML11)
                     + "]]>");
            break;
        }
        case Node.ENTITY_REFERENCE_NODE:
            w.write('&' + n.getNodeName() + ';');
            break;
        case Node.PROCESSING_INSTRUCTION_NODE: {
            String target = n.getNodeName();
            String data = n.getNodeValue();
            if (target.equalsIgnoreCase("xml")
                    || target.indexOf(':') != -1
                    || data.indexOf("?>") != -1) {
                throw new
                    IOException("Unserializable processing instruction node");
            }
            w.write("<?" + target + ' ' + data + "?>");
            break;
        }
        case Node.COMMENT_NODE: {
            w.write("<!--");
            String data = n.getNodeValue();
            int len = data.length();
            if (len != 0 && data.charAt(len - 1) == '-'
                    || data.indexOf("--") != -1) {
                throw new IOException("Unserializable comment node");
            }
            w.write(data);
            w.write("-->");
            break;
        }
        case Node.DOCUMENT_TYPE_NODE: {
            DocumentType dt = (DocumentType)n;
            w.write("<!DOCTYPE "
                     + n.getOwnerDocument().getDocumentElement().getNodeName());
            String pubID = dt.getPublicId();
            if (pubID != null) {
                char q = getUsableQuote(pubID);
                if (q == 0) {
                    throw new IOException("Unserializable DOCTYPE node");
                }
                w.write(" PUBLIC " + q + pubID + q);
            }
            String sysID = dt.getSystemId();
            if (sysID != null) {
                char q = getUsableQuote(sysID);
                if (q == 0) {
                    throw new IOException("Unserializable DOCTYPE node");
                }
                if (pubID == null) {
                    w.write(" SYSTEM");
                }
                w.write(" " + q + sysID + q);
            }
            String subset = dt.getInternalSubset();
            if (subset != null) {
                w.write('[' + subset + ']');
            }
            w.write('>');
            break;
        }
        default:
            throw new IOException("Unknown DOM node type " + n.getNodeType());
        }
    }

    /**
     * Writes a node using the given writer.
     */
    public static void writeNode(Node n, Writer w) throws IOException {
        if (n.getNodeType() == Node.DOCUMENT_NODE) {
            writeDocument((Document) n, w);
        } else {
            AbstractDocument d = (AbstractDocument) n.getOwnerDocument();
            writeNode(n, w, NSMap.create(),
                      d == null ? false : "1.1".equals(d.getXmlVersion()));
        }
    }

    /**
     * Returns the quote character to use when quoting the specified string.
     * If the string contains both single and double quotes, then 0 will be
     * returned.
     */
    private static char getUsableQuote(String s) {
        char ret = 0;
        int i = s.length() - 1;
        while (i >= 0) {
            char c = s.charAt(i);
            if (c == '"') {
                if (ret == 0) {
                    ret = '\'';
                } else {
                    return 0;
                }
            } else if (c == '\'') {
                if (ret == 0) {
                    ret = '"';
                } else {
                    return 0;
                }
            }
            i--;
        }
        return ret == 0 ? '"' : ret;
    }

    /**
     * Serializes the given DOM node using {@link #writeNode(Node,Writer)}
     * and returns the XML as a String.
     *
     * @param n The Node to serialize.
     * @return A String containing the XML serialization of the Node, or an
     *   empty String if there was a problem during serialization.
     */
    public static String getXML(Node n) {
        Writer writer = new StringWriter();
        try {
            DOMUtilities.writeNode(n, writer);
            writer.close();
        } catch (IOException ex) {
            return "";
        }
        return writer.toString();
    }

    protected static String assertValidCharacters(String s, boolean isXML11)
            throws IOException {

        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!isXML11 && !isXMLCharacter(c)
                    || isXML11 && !isXML11Character(c)) {
                throw new IOException("Invalid character");
            }
        }
        return s;
    }

    /**
     * Returns the given content value transformed to replace invalid
     * characters with entities.
     */
    public static String contentToString(String s, boolean isXML11)
            throws IOException {

        StringBuffer result = new StringBuffer(s.length());

        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!isXML11 && !isXMLCharacter(c)
                    || isXML11 && !isXML11Character(c)) {
                throw new IOException("Invalid character");
            }

            switch (c) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            case '\'':
                result.append("&apos;");
                break;
            default:
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Finds and returns the index of child node in the given parent's children
     * array
     *
     * @param child
     *            The child node
     * @param parent
     *            The parent node
     * @return the index
     */
    public static int getChildIndex(Node child, Node parent) {
        if (child == null || child.getParentNode() != parent
                || child.getParentNode() == null) {
            return -1;
        }
        return getChildIndex(child);
    }

    /**
     * Finds and returns the index of child node in its parent's children array
     *
     * @param child
     *            The child node
     * @return the index in children array
     */
    public static int getChildIndex(Node child) {
        NodeList children = child.getParentNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node currentChild = children.item(i);
            if (currentChild == child) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if any of from the given list of nodes is an ancestor to another
     * node
     *
     * @param ancestorNodes
     *            The potential ancestor nodes
     * @param node
     *            The potential descendant node
     * @return True if at least one node is ancestor of the given node
     */
    public static boolean isAnyNodeAncestorOf(ArrayList ancestorNodes, Node node) {
        int n = ancestorNodes.size();
        for (Object ancestorNode : ancestorNodes) {
            Node ancestor = (Node) ancestorNode;
            if (isAncestorOf(ancestor, node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a node is ancestor of another node.
     *
     * @param node
     *            The potential ancestor node
     * @param descendant
     *            The potential descendant node
     * @return True if node is ancestor of the descendant node
     */
    public static boolean isAncestorOf(Node node, Node descendant) {
        if (node == null || descendant == null) {
            return false;
        }
        for (Node currentNode = descendant.getParentNode(); currentNode != null; currentNode = currentNode
                .getParentNode()) {
            if (currentNode == node) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether the given node is a child of the given parent node.
     *
     * @param node
     *            The potential child node
     * @param parentNode
     *            Parent node
     * @return True if a node is a child of the given parent node
     */
    public static boolean isParentOf(Node node, Node parentNode) {
        if (node == null || parentNode == null
                || node.getParentNode() != parentNode) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the node can be appended on the given parent node
     *
     * @param node
     *            The given node
     * @param parentNode
     *            The given parent node
     * @return True if the given node can be appended on the parent node
     */
    public static boolean canAppend(Node node, Node parentNode) {
        if (node == null || parentNode == null || node == parentNode
                || isAncestorOf(node, parentNode)) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether any of the nodes from the list can be appended to a given
     * parentNode.
     *
     * @param children
     *            The given node list
     * @param parentNode
     *            The potential parent node
     * @return true if at least one node from a list can be appended
     */
    public static boolean canAppendAny(ArrayList children, Node parentNode) {
        if (!canHaveChildren(parentNode)) {
            return false;
        }
        int n = children.size();
        for (Object aChildren : children) {
            Node child = (Node) aChildren;
            if (canAppend(child, parentNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given Node can have children.
     *
     * @param parentNode The Node to test
     * @return <code>true</code> if the node can have children,
     *   <code>false</code> otherwise
     */
    public static boolean canHaveChildren(Node parentNode) {
        if (parentNode == null) {
            return false;
        }
        switch (parentNode.getNodeType()) {
            case Node.DOCUMENT_NODE:
            case Node.TEXT_NODE:
            case Node.COMMENT_NODE:
            case Node.CDATA_SECTION_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
                return false;
            default:
                return true;
        }
    }

    /**
     * Parses the given XML string into a DocumentFragment of the given document
     * or a new document if 'doc' is null.
     *
     * @param text
     *            The given XML string
     * @param doc
     *            The given document
     * @param uri
     *            The document URI
     * @param prefixes
     *            The prefixes map with (prefix, namespaceURI) pairs
     * @param wrapperElementName
     *            null: Ignore the wrapper element and prefixes map and try to
     *            parse the text as a whole document otherwise: Wrap the given
     *            text with the wrapper element with prefixes specified from the
     *            prefixes map
     * @param documentFactory
     *            What document factory to use when parsing the text
     * @return The document fragment or null on error.
     */
    public static Node parseXML(String text, Document doc, String uri,
            Map prefixes, String wrapperElementName,
            SAXDocumentFactory documentFactory) {

        // Create the wrapper element prefix and suffix, copying the (prefix,
        // namespaceURI) pairs from the prefixes map
        String wrapperElementPrefix = "";
        String wrapperElementSuffix = "";
        if (wrapperElementName != null) {
            wrapperElementPrefix = "<" + wrapperElementName;
            // Copy the prefixes from the prefixes map to the wrapper element
            if (prefixes != null) {
                wrapperElementPrefix += " ";
                for (Object o : prefixes.entrySet()) {
                    Map.Entry e = (Map.Entry) o;
                    String currentKey = (String) e.getKey();
                    String currentValue = (String) e.getValue();
                    wrapperElementPrefix += currentKey + "=\"" + currentValue
                            + "\" ";
                }
            }
            wrapperElementPrefix += ">";
            wrapperElementSuffix += "</" + wrapperElementName + '>';
        }

        // Try and parse as a whole document, if no wrapper element is specified
        if (wrapperElementPrefix.trim().length() == 0
                && wrapperElementSuffix.trim().length() == 0) {
            try {
                Document d = documentFactory.createDocument(uri,
                        new StringReader(text));
                if (doc == null) {
                    return d;
                }
                Node result = doc.createDocumentFragment();
                result
                        .appendChild(doc.importNode(d.getDocumentElement(),
                                true));
                return result;
            } catch (Exception ex) {

            }
        }

        // Try and parse as a document fragment
        StringBuffer sb = new StringBuffer(wrapperElementPrefix.length()
                + text.length() + wrapperElementSuffix.length());
        sb.append(wrapperElementPrefix);
        sb.append(text);
        sb.append(wrapperElementSuffix);
        String newText = sb.toString();
        try {
            Document d = documentFactory.createDocument(uri, new StringReader(
                    newText));
            if (doc == null) {
                return d;
            }
            for (Node node = d.getDocumentElement().getFirstChild(); node != null;
                    node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    node = doc.importNode(node, true);
                    Node result = doc.createDocumentFragment();
                    result.appendChild(node);
                    return result;
                }
            }
        } catch (Exception exc) {

        }
        return null;
    }

    /**
     * Deep clones a document using the given DOM implementation.
     */
    public static Document deepCloneDocument(Document doc, DOMImplementation impl) {
        Element root = doc.getDocumentElement();
        Document result = impl.createDocument(root.getNamespaceURI(),
                                              root.getNodeName(),
                                              null);
        Element rroot = result.getDocumentElement();
        boolean before = true;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == root) {
                before = false;
                if (root.hasAttributes()) {
                    NamedNodeMap attr = root.getAttributes();
                    int len = attr.getLength();
                    for (int i = 0; i < len; i++) {
                        rroot.setAttributeNode((Attr)result.importNode(attr.item(i),
                                                                       true));
                    }
                }
                for (Node c = root.getFirstChild();
                     c != null;
                     c = c.getNextSibling()) {
                    rroot.appendChild(result.importNode(c, true));
                }
            } else {
                if (n.getNodeType() != Node.DOCUMENT_TYPE_NODE) {
                    if (before) {
                        result.insertBefore(result.importNode(n, true), rroot);
                    } else {
                        result.appendChild(result.importNode(n, true));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Tests whether the given string is a valid name.
     */
    public static boolean isValidName(String s) {
        int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / 32;
        int m = c % 32;
        if ((NAME_FIRST_CHARACTER[d] & (1 << m)) == 0) {
            return false;
        }
        for (int i = 1; i < len; i++) {
            c = s.charAt(i);
            d = c / 32;
            m = c % 32;
            if ((NAME_CHARACTER[d] & (1 << m)) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether the given string is a valid XML 1.1 name.
     */
    public static boolean isValidName11(String s) {
        int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / 32;
        int m = c % 32;
        if ((NAME11_FIRST_CHARACTER[d] & (1 << m)) == 0) {
            return false;
        }
        for (int i = 1; i < len; i++) {
            c = s.charAt(i);
            d = c / 32;
            m = c % 32;
            if ((NAME11_CHARACTER[d] & (1 << m)) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether the given string is a valid prefix.
     * This method assume that isValidName(s) is true.
     */
    public static boolean isValidPrefix(String s) {
        return s.indexOf(':') == -1;
    }

    /**
     * Gets the prefix from the given qualified name.
     * This method assume that isValidName(s) is true.
     */
    public static String getPrefix(String s) {
        int i = s.indexOf(':');
        return (i == -1 || i == s.length()-1)
            ? null
            : s.substring(0, i);
    }

    /**
     * Gets the local name from the given qualified name.
     * This method assume that isValidName(s) is true.
     */
    public static String getLocalName(String s) {
        int i = s.indexOf(':');
        return (i == -1 || i == s.length()-1)
            ? s
            : s.substring(i + 1);
    }

    /**
     * Parses a 'xml-stylesheet' processing instruction data section and
     * puts the pseudo attributes in the given table.
     */
    public static void parseStyleSheetPIData(String data, HashTable table) {
        // !!! Internationalization
        char c;
        int i = 0;
        // Skip leading whitespaces
        while (i < data.length()) {
            c = data.charAt(i);
            if (!XMLUtilities.isXMLSpace(c)) {
                break;
            }
            i++;
        }
        while (i < data.length()) {
            // Parse the pseudo attribute name
            c = data.charAt(i);
            int d = c / 32;
            int m = c % 32;
            if ((NAME_FIRST_CHARACTER[d] & (1 << m)) == 0) {
                throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
                                       "Wrong name initial:  " + c);
            }
            StringBuffer ident = new StringBuffer();
            ident.append(c);
            while (++i < data.length()) {
                c = data.charAt(i);
                d = c / 32;
                m = c % 32;
                if ((NAME_CHARACTER[d] & (1 << m)) == 0) {
                    break;
                }
                ident.append(c);
            }
            if (i >= data.length()) {
                throw new DOMException(DOMException.SYNTAX_ERR,
                                       "Wrong xml-stylesheet data: " + data);
            }
            // Skip whitespaces
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                i++;
            }
            if (i >= data.length()) {
                throw new DOMException(DOMException.SYNTAX_ERR,
                                       "Wrong xml-stylesheet data: " + data);
            }
            // The next char must be '='
            if (data.charAt(i) != '=') {
                throw new DOMException(DOMException.SYNTAX_ERR,
                                       "Wrong xml-stylesheet data: " + data);
            }
            i++;
            // Skip whitespaces
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                i++;
            }
            if (i >= data.length()) {
                throw new DOMException(DOMException.SYNTAX_ERR,
                                       "Wrong xml-stylesheet data: " + data);
            }
            // The next char must be '\'' or '"'
            c = data.charAt(i);
            i++;
            StringBuffer value = new StringBuffer();
            if (c == '\'') {
                while (i < data.length()) {
                    c = data.charAt(i);
                    if (c == '\'') {
                        break;
                    }
                    value.append(c);
                    i++;
                }
                if (i >= data.length()) {
                    throw new DOMException(DOMException.SYNTAX_ERR,
                                           "Wrong xml-stylesheet data: " +
                                           data);
                }
            } else if (c == '"') {
                while (i < data.length()) {
                    c = data.charAt(i);
                    if (c == '"') {
                        break;
                    }
                    value.append(c);
                    i++;
                }
                if (i >= data.length()) {
                    throw new DOMException(DOMException.SYNTAX_ERR,
                                           "Wrong xml-stylesheet data: " +
                                           data);
                }
            } else {
                throw new DOMException(DOMException.SYNTAX_ERR,
                                       "Wrong xml-stylesheet data: " + data);
            }
            table.put(ident.toString().intern(), value.toString());
            i++;
            // Skip whitespaces
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                i++;
            }
        }
    }

    /**
     * String constants representing DOM modifier strings for various all
     * key lock combinations.
     */
    protected static final String[] LOCK_STRINGS = {
        "",
        "CapsLock",
        "NumLock",
        "NumLock CapsLock",
        "Scroll",
        "Scroll CapsLock",
        "Scroll NumLock",
        "Scroll NumLock CapsLock",
        "KanaMode",
        "KanaMode CapsLock",
        "KanaMode NumLock",
        "KanaMode NumLock CapsLock",
        "KanaMode Scroll",
        "KanaMode Scroll CapsLock",
        "KanaMode Scroll NumLock",
        "KanaMode Scroll NumLock CapsLock"
    };

    /**
     * String constants representing DOM modifier strings for various all
     * shift modifier combinations.
     */
    protected static final String[] MODIFIER_STRINGS = {
        "",
        "Shift",
        "Control",
        "Control Shift",
        "Meta",
        "Meta Shift",
        "Control Meta",
        "Control Meta Shift",
        "Alt",
        "Alt Shift",
        "Alt Control",
        "Alt Control Shift",
        "Alt Meta",
        "Alt Meta Shift",
        "Alt Control Meta",
        "Alt Control Meta Shift",
        "AltGraph",
        "AltGraph Shift",
        "AltGraph Control",
        "AltGraph Control Shift",
        "AltGraph Meta",
        "AltGraph Meta Shift",
        "AltGraph Control Meta",
        "AltGraph Control Meta Shift",
        "Alt AltGraph",
        "Alt AltGraph Shift",
        "Alt AltGraph Control",
        "Alt AltGraph Control Shift",
        "Alt AltGraph Meta",
        "Alt AltGraph Meta Shift",
        "Alt AltGraph Control Meta",
        "Alt AltGraph Control Meta Shift"
    };

    /**
     * Gets a DOM 3 modifiers string from the given lock and
     * shift bitmasks.
     */
    public static String getModifiersList(int lockState, int modifiersEx) {
        if ((modifiersEx & (1 << 13)) != 0) {
            modifiersEx = 0x10 | ((modifiersEx >> 6) & 0x0f);
        } else {
            modifiersEx = (modifiersEx >> 6) & 0x0f;
        }
        String s = LOCK_STRINGS[lockState & 0x0f];
        if (s.length() != 0) {
            return s + ' ' + MODIFIER_STRINGS[modifiersEx];
        }
        return MODIFIER_STRINGS[modifiersEx];
    }

    /**
     * Returns whether the given element has a particular attribute and that
     * it exists due to being specified explicitly, rather than being defaulted
     * from a DTD.
     */
    public static boolean isAttributeSpecifiedNS(Element e,
                                                 String namespaceURI,
                                                 String localName) {
        Attr a = e.getAttributeNodeNS(namespaceURI, localName);
        return a != null && a.getSpecified();
    }
}
