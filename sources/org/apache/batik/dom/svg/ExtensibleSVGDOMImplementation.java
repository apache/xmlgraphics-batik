/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.GenericElementNS;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;

import org.apache.batik.util.Service;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation} interface.
 * It allows the user to extend the set of elements supported by a
 * Document, directly or through the Service API (see
 * {@link org.apache.batik.util.Service}).
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ExtensibleSVGDOMImplementation extends SVGDOMImplementation {
    
    /**
     * The default instance of this class.
     */
    protected final static DOMImplementation DOM_IMPLEMENTATION =
        new ExtensibleSVGDOMImplementation();

    /**
     * The custom elements factories.
     */
    protected HashTable customFactories;

    /**
     * Returns the default instance of this class.
     */
    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    /**
     * Creates a new DOMImplementation.
     */
    public ExtensibleSVGDOMImplementation() {
        Iterator iter = getDomExtensions().iterator();

        while(iter.hasNext()) {
            DomExtension de = (DomExtension)iter.next();
            de.registerTags(this);
        }
    }

    /**
     * Allows the user to register a new element factory.
     */
    public void registerCustomElementFactory(String namespaceURI,
                                             String localName,
                                             ElementFactory factory) {
        if (customFactories == null) {
            customFactories = new HashTable();
        }
        HashTable ht = (HashTable)customFactories.get(namespaceURI);
        if (ht == null) {
            customFactories.put(namespaceURI, ht = new HashTable());
        }
        ht.put(localName, factory);
    }

    /**
     * Implements the behavior of Document.createElementNS() for this
     * DOM implementation.
     */
    public Element createElementNS(AbstractDocument document,
                                   String           namespaceURI,
                                   String           qualifiedName) {
        if (SVG_NAMESPACE_URI.equals(namespaceURI)) {
            String name = DOMUtilities.getLocalName(qualifiedName);
            ElementFactory ef = (ElementFactory)factories.get(name);
            if (ef == null) {
                throw document.createDOMException(DOMException.NOT_FOUND_ERR,
                                                  "invalid.element",
                                                  new Object[] { namespaceURI,
                                                                 qualifiedName });
            }
            return ef.create(DOMUtilities.getPrefix(qualifiedName), document);
        }
        if (namespaceURI != null) {
            if (customFactories != null) {
                HashTable ht = (HashTable)customFactories.get(namespaceURI);
                if (ht != null) {
                    String name = DOMUtilities.getLocalName(qualifiedName);
                    ElementFactory cef = (ElementFactory)ht.get(name);
                    if (cef != null) {
                        return cef.create(DOMUtilities.getPrefix(qualifiedName),
                                          document);
                    }
                }
            }
            return new GenericElementNS(namespaceURI.intern(),
                                        qualifiedName.intern(),
                                        document);
        } else {
            return new GenericElement(qualifiedName.intern(), document);
        }
    }

    // Service /////////////////////////////////////////////////////////

    protected static List extensions = null;

    protected synchronized static List getDomExtensions() {
        if (extensions != null)
            return extensions;

        extensions = new LinkedList();

        Iterator iter = Service.providers(DomExtension.class);

        while (iter.hasNext()) {
            DomExtension de = (DomExtension)iter.next();
            float priority  = de.getPriority();
            ListIterator li = extensions.listIterator();
            for (;;) {
                if (!li.hasNext()) {
                    li.add(de);
                    break;
                }
                DomExtension lde = (DomExtension)li.next();
                if (lde.getPriority() > priority) {
                    li.previous();
                    li.add(de);
                    break;
                }
            }
        }

        return extensions;
    }

}
