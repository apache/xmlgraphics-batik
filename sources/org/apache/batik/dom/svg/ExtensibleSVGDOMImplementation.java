/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

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
