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

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class implements the {@link SVGElement} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMElement
    extends    AbstractElement
    implements SVGElement,
               SVGConstants {

    /**
     * Is this element immutable?
     */
    protected transient boolean readonly;

    /**
     * The element prefix.
     */
    protected String prefix;

    /**
     * The SVG context to get SVG specific informations.
     */
    protected transient SVGContext svgContext;

    /**
     * Creates a new Element object.
     */
    protected SVGOMElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected SVGOMElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getId()}.
     */
    public String getId() {
        return getAttributeNS(null, "id");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setId(String)}.
     */
    public void setId(String id) {
        setAttributeNS(null, "id", id);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getXMLbase()}.
     */
    public String getXMLbase() {
        return XMLBaseSupport.getXMLBase(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setXMLbase(String)}.
     */
    public void setXMLbase(String xmlbase) throws DOMException {
        setAttributeNS(XMLConstants.XML_NAMESPACE_URI, "xml:base", xmlbase);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getOwnerSVGElement()}.
     */
    public SVGSVGElement getOwnerSVGElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this);
             e != null;
             e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGSVGElement) {
                return (SVGSVGElement)e;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getViewportElement()}.
     */
    public SVGElement getViewportElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this);
             e != null;
             e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGFitToViewBox) {
                return (SVGElement)e;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getNodeName()}.
     */
    public String getNodeName() {
        if (prefix == null || prefix.equals("")) {
            return getLocalName();
        }
        String ln = getLocalName();
        StringBuffer sb = new StringBuffer(prefix.length() + ln.length() + 1);
        sb.append(prefix).append(':').append(ln);
        return sb.toString();
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#setPrefix(String)}.
     */
    public void setPrefix(String prefix) throws DOMException {
        if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
        }
        if (prefix != null &&
            !prefix.equals("") &&
            !DOMUtilities.isValidName(prefix)) {
	    throw createDOMException(DOMException.INVALID_CHARACTER_ERR,
				     "prefix",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName(),
						    prefix });
        }
        this.prefix = prefix;
    }

    // SVGContext ////////////////////////////////////////////////////

    /**
     * Sets the SVG context to use to get SVG specific informations.
     *
     * @param ctx the SVG context
     */
    public void setSVGContext(SVGContext ctx) {
        svgContext = ctx;
    }

    /**
     * Returns the SVG context used to get SVG specific informations.
     */
    public SVGContext getSVGContext() {
        return svgContext;
    }

    // ExtendedNode //////////////////////////////////////////////////

    /**
     * Creates an SVGException with the appropriate error message.
     */
    public SVGException createSVGException(short type, 
                                           String key, 
                                           Object [] args) {
        try {
            return new SVGOMException
                (type, getCurrentDocument().formatMessage(key, args));
        } catch (Exception e) {
            return new SVGOMException(type, key);
        }
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
        readonly = v;
    }

    /**
     * Manages the query of an SVGAnimatedString.
     * @param ns The namespace of the attribute.
     * @param ln The local name of the attribute.
     */
    protected SVGAnimatedString getAnimatedStringAttribute(String ns,
                                                           String ln) {
        SVGAnimatedString result =
            (SVGAnimatedString)getLiveAttributeValue(ns, ln);
        if (result == null) {
            result = new SVGOMAnimatedString(this, ns, ln);
            putLiveAttributeValue(ns, ln, (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * Manages the query of an SVGAnimatedNumber.
     * @param ns The namespace of the attribute.
     * @param ln The local name of the attribute.
     * @param val The value if the attribute is not specified.
     */
    protected SVGAnimatedNumber getAnimatedNumberAttribute(String ns,
                                                           String ln,
                                                           float  val) {
        SVGAnimatedNumber result =
            (SVGAnimatedNumber)getLiveAttributeValue(ns, ln);
        if (result == null) {
            result = new SVGOMAnimatedNumber(this, ns, ln, val);
            putLiveAttributeValue(ns, ln, (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * Manages the query of an SVGAnimatedInteger.
     * @param ns The namespace of the attribute.
     * @param ln The local name of the attribute.
     * @param val The value if the attribute is not specified.
     */
    protected SVGAnimatedInteger getAnimatedIntegerAttribute(String ns,
                                                             String ln,
                                                             int    val) {
        SVGAnimatedInteger result =
            (SVGAnimatedInteger)getLiveAttributeValue(ns, ln);
        if (result == null) {
            result = new SVGOMAnimatedInteger(this, ns, ln, val);
            putLiveAttributeValue(ns, ln, (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * Manages the query of an SVGAnimatedEnumeration.
     * @param ns The namespace of the attribute.
     * @param ln The local name of the attribute.
     * @param val The values in the enumeration.
     * @param def The value if the attribute is not specified.
     */
    protected SVGAnimatedEnumeration
        getAnimatedEnumerationAttribute(String ns, String ln,
                                        String[] val, short def) {
        SVGAnimatedEnumeration result =
            (SVGAnimatedEnumeration)getLiveAttributeValue(ns, ln);
        if (result == null) {
            result = new SVGOMAnimatedEnumeration(this, ns, ln, val, def);
            putLiveAttributeValue(ns, ln, (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * Manages the query of an SVGAnimatedNumber.
     * @param ns The namespace of the attribute.
     * @param ln The local name of the attribute.
     * @param val The value if the attribute is not specified.
     * @param dir The length direction.
     */
    protected SVGAnimatedLength getAnimatedLengthAttribute(String ns,
                                                           String ln,
                                                           String val,
                                                           short  dir) {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue(ns, ln);
        if (result == null) {
            result = new SVGOMAnimatedLength(this, ns, ln, val, dir);
            putLiveAttributeValue(ns, ln, (LiveAttributeValue)result);
        }
        return result;
    }

    // Importation/Cloning ///////////////////////////////////////////

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }
}
