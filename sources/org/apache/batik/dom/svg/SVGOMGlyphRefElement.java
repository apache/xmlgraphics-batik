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

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGGlyphRefElement;

/**
 * This class implements {@link SVGGlyphRefElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMGlyphRefElement
    extends    SVGStylableElement
    implements SVGGlyphRefElement {
    
    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute(XMLSupport.XMLNS_NAMESPACE_URI,
                                          null, "xmlns:xlink",
                                          XLinkSupport.XLINK_NAMESPACE_URI);
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "type", "simple");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "show", "other");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "actuate", "onLoad");
    }

    /**
     * Creates a new SVGOMGlyphRefElement object.
     */
    protected SVGOMGlyphRefElement() {
    }

    /**
     * Creates a new SVGOMGlyphRefElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMGlyphRefElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_GLYPH_REF_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getGlyphRef()}.
     */
    public String getGlyphRef() {
        return getAttributeNS(null, SVG_GLYPH_REF_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setGlyphRef(String)}.
     */
    public void setGlyphRef(String glyphRef) throws DOMException {
        setAttributeNS(null, SVG_GLYPH_REF_ATTRIBUTE, glyphRef);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getFormat()}.
     */
    public String getFormat() {
        return getAttributeNS(null, SVG_FORMAT_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setFormat(String)}.
     */
    public void setFormat(String format) throws DOMException {
        setAttributeNS(null, SVG_FORMAT_ATTRIBUTE, format);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getX()}.
     */
    public float getX() {
        return Float.parseFloat(getAttributeNS(null, SVG_X_ATTRIBUTE));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setX(float)}.
     */
    public void setX(float x) throws DOMException {
        setAttributeNS(null, SVG_X_ATTRIBUTE, String.valueOf(x));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getY()}.
     */
    public float getY() {
        return Float.parseFloat(getAttributeNS(null, SVG_Y_ATTRIBUTE));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setY(float)}.
     */
    public void setY(float y) throws DOMException {
        setAttributeNS(null, SVG_Y_ATTRIBUTE, String.valueOf(y));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getDx()}.
     */
    public float getDx() {
        return Float.parseFloat(getAttributeNS(null, SVG_DX_ATTRIBUTE));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setDx(float)}.
     */
    public void setDx(float dx) throws DOMException {
        setAttributeNS(null, SVG_DX_ATTRIBUTE, String.valueOf(dx)); 
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#getDy()}.
     */
    public float getDy() {
        return Float.parseFloat(getAttributeNS(null, SVG_DY_ATTRIBUTE));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGGlyphRefElement#setDy(float)}.
     */
    public void setDy(float dy) throws DOMException {
        setAttributeNS(null, SVG_DY_ATTRIBUTE, String.valueOf(dy)); 
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
        return new SVGOMGlyphRefElement();
    }
}
