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
package org.apache.batik.dom.svg;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.XMLConstants;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.svg.SVGStyleElement;

/**
 * This class implements {@link SVGStyleElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMStyleElement
    extends    SVGOMElement
    implements CSSStyleSheetNode,
               SVGStyleElement,
               LinkStyle {

    /**
     * The attribute initializer.
     */
    protected static final AttributeInitializer attributeInitializer;

    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(XMLSupport.XML_NAMESPACE_URI,
                                          "xml", "space", "preserve");
    }

    /**
     * The style sheet.
     */
    protected transient org.w3c.dom.stylesheets.StyleSheet sheet;

    /**
     * The DOM CSS style-sheet.
     */
    protected transient StyleSheet styleSheet;

    /**
     * The listener used to track the content changes.
     */
    protected transient EventListener domCharacterDataModifiedListener =
        new DOMCharacterDataModifiedListener();

    /**
     * Creates a new SVGOMStyleElement object.
     */
    protected SVGOMStyleElement() {
    }

    /**
     * Creates a new SVGOMStyleElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMStyleElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_STYLE_TAG;
    }

    /**
     * Returns the associated style-sheet.
     */
    public StyleSheet getCSSStyleSheet() {
        if (styleSheet == null) {
            if (getType().equals("text/css")) {
                SVGOMDocument doc = (SVGOMDocument)getOwnerDocument();
                CSSEngine e = doc.getCSSEngine();
                String text = "";
                Node n = getFirstChild();
                if (n != null) {
                    StringBuffer sb = new StringBuffer();
                    while (n != null) {
                        if (n.getNodeType() == Node.CDATA_SECTION_NODE
                            || n.getNodeType() == Node.TEXT_NODE)
                            sb.append(n.getNodeValue());
                        n = n.getNextSibling();
                    }
                    text = sb.toString();
                }
                URL burl = null;
                String bu= "";
                try {
                    bu = getBaseURI();
                    if (bu != null) {
                        burl = new URL(bu);
                    }
                } catch (MalformedURLException ex) {
                    String msg = "MalformedURLException:" + ex.getMessage() + ':' + bu;
                    throw new IllegalArgumentException( msg );
                }
                String  media = getAttributeNS(null, SVG_MEDIA_ATTRIBUTE);
                styleSheet = e.parseStyleSheet(text, burl, media);
                addEventListenerNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                                   "DOMCharacterDataModified",
                                   domCharacterDataModifiedListener,
                                   false,
                                   null);
            }
        }
        return styleSheet;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.LinkStyle#getSheet()}.
     */
    public org.w3c.dom.stylesheets.StyleSheet getSheet() {
        throw new UnsupportedOperationException
            ("LinkStyle.getSheet() is not implemented"); // XXX
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#getXMLspace()}.
     */
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#setXMLspace(String)}.
     */
    public void setXMLspace(String space) throws DOMException {
        setAttributeNS(XML_NAMESPACE_URI, XML_SPACE_QNAME, space);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#getType()}.
     */
    public String getType() {
        return getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#setType(String)}.
     */
    public void setType(String type) throws DOMException {
        setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#getMedia()}.
     */
    public String getMedia() {
        return getAttribute(SVG_MEDIA_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#setMedia(String)}.
     */
    public void setMedia(String media) throws DOMException {
        setAttribute(SVG_MEDIA_ATTRIBUTE, media);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#getTitle()}.
     */
    public String getTitle() {
        return getAttribute(SVG_TITLE_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGStyleElement#setTitle(String)}.
     */
    public void setTitle(String title) throws DOMException {
        setAttribute(SVG_TITLE_ATTRIBUTE, title);
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMStyleElement();
    }

    /**
     * The DOMCharacterDataModified listener.
     */
    protected class DOMCharacterDataModifiedListener
        implements EventListener {
        public void handleEvent(Event evt) {
            styleSheet = null;
        }
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_MEDIA_ATTRIBUTE)
                    || ln.equals(SVG_TITLE_ATTRIBUTE)
                    || ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            }
        }
        return super.getAttributeType(ns, ln);
    }
}
