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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
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
    protected final static AttributeInitializer attributeInitializer;
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
                try {
                    String bu = XMLBaseSupport.getCascadedXMLBase(this);
                    if (bu != null) {
                        burl = new URL(bu);
                    }
                } catch (MalformedURLException ex) {
                    // !!! TODO
                    ex.printStackTrace();
                    throw new InternalError();
                }
                String  media = getAttributeNS(null, SVG_MEDIA_ATTRIBUTE);
                styleSheet = e.parseStyleSheet(text, burl, media);
                addEventListener("DOMCharacterDataModified",
                                 domCharacterDataModifiedListener,
                                 false);
            }
        }
        return styleSheet;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.LinkStyle#getSheet()}.
     */
    public org.w3c.dom.stylesheets.StyleSheet getSheet() {
        throw new RuntimeException(" !!! Not implemented.");
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
        setAttributeNS(XMLSupport.XML_NAMESPACE_URI,
                       XMLSupport.XML_SPACE_ATTRIBUTE,
                       space);
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
}
