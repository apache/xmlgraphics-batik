/*

   Copyright 2001-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.parser.ExtendedParserWrapper;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.GenericElementNS;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.DoublyIndexedTable;
import org.apache.batik.util.Service;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
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
    protected DoublyIndexedTable customFactories;

    /**
     * The custom value managers.
     */
    protected List customValueManagers;

    /**
     * The custom shorthand value managers.
     */
    protected List customShorthandManagers;

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
            customFactories = new DoublyIndexedTable();
        }
        customFactories.put(namespaceURI, localName, factory);
    }

    /**
     * Allows the user to register a new CSS value manager.
     */
    public void registerCustomCSSValueManager(ValueManager vm) {
        if (customValueManagers == null) {
            customValueManagers = new LinkedList();
        }
        customValueManagers.add(vm);
    }

    /**
     * Allows the user to register a new shorthand CSS value manager.
     */
    public void registerCustomCSSShorthandManager(ShorthandManager sm) {
        if (customShorthandManagers == null) {
            customShorthandManagers = new LinkedList();
        }
        customShorthandManagers.add(sm);
    }

    /**
     * Creates new CSSEngine and attach it to the document.
     */
    public CSSEngine createCSSEngine(SVGOMDocument doc, CSSContext ctx) {
        if (customValueManagers == null && customShorthandManagers == null) {
            return super.createCSSEngine(doc, ctx);
        }

        String pn = XMLResourceDescriptor.getCSSParserClassName();
        Parser p;
        try {
            p = (Parser)Class.forName(pn).newInstance();
        } catch (ClassNotFoundException e) {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                   formatMessage("css.parser.class",
                                                 new Object[] { pn }));
        } catch (InstantiationException e) {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                   formatMessage("css.parser.creation",
                                                 new Object[] { pn }));
        } catch (IllegalAccessException e) {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                   formatMessage("css.parser.access",
                                                 new Object[] { pn }));
        }
        ExtendedParser ep = ExtendedParserWrapper.wrap(p);

        ValueManager[] vms;
        if (customValueManagers == null) {
            vms = new ValueManager[0];
        } else {
            vms = new ValueManager[customValueManagers.size()];
            Iterator it = customValueManagers.iterator();
            int i = 0;
            while (it.hasNext()) {
                vms[i++] = (ValueManager)it.next();
            }
        }

        ShorthandManager[] sms;
        if (customShorthandManagers == null) {
            sms = new ShorthandManager[0];
        } else {
            sms = new ShorthandManager[customShorthandManagers.size()];
            Iterator it = customShorthandManagers.iterator();
            int i = 0;
            while (it.hasNext()) {
                sms[i++] = (ShorthandManager)it.next();
            }
        }

        URL durl = doc.getURLObject();
        CSSEngine result = new SVGCSSEngine(doc,
                                            durl,
                                            ep,
                                            vms,
                                            sms,
                                            ctx);
        URL url = getClass().getResource("resources/UserAgentStyleSheet.css");
        if (url != null) {
            InputSource is = new InputSource(url.toString());
            result.setUserAgentStyleSheet(result.parseStyleSheet(is, url, "all"));
        }
        doc.setCSSEngine(result);
        return result;
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
                throw document.createDOMException
                    (DOMException.NOT_FOUND_ERR,
                     "invalid.element",
                     new Object[] { namespaceURI,
                                    qualifiedName });
            }
            return ef.create(DOMUtilities.getPrefix(qualifiedName), document);
        }
        if (namespaceURI != null) {
            if (customFactories != null) {
                String name = DOMUtilities.getLocalName(qualifiedName);
                ElementFactory cef;
                cef = (ElementFactory)customFactories.get(namespaceURI, name);
                if (cef != null) {
                    return cef.create(DOMUtilities.getPrefix(qualifiedName),
                                      document);
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
