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

package org.apache.batik.bridge;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.batik.util.ParsedURL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.AWTFontFamily;
import org.apache.batik.util.SVGConstants;

/**
 * This class represents a &lt;font-face> element or @font-face rule
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public abstract class FontFace extends GVTFontFace {

    static Set fontSet;
    static {
        GraphicsEnvironment ge;
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String [] fonts = ge.getAvailableFontFamilyNames();
        fontSet = new HashSet(fonts.length);
        for(int i=0; i<fonts.length; i++) {
            fontSet.add(fonts[i]);
        }
    }

    /**
     * List of ParsedURL's referencing SVGFonts or TrueType fonts,
     * or Strings naming locally installed fonts.
     */
    List srcs;

    /**
     * Constructes an SVGFontFace with the specfied font-face attributes.
     */
    public FontFace
        (List srcs,
         String familyName, float unitsPerEm, String fontWeight,
         String fontStyle, String fontVariant, String fontStretch,
         float slope, String panose1, float ascent, float descent,
         float strikethroughPosition, float strikethroughThickness,
         float underlinePosition,     float underlineThickness,
         float overlinePosition,      float overlineThickness) {
        super(familyName, unitsPerEm, fontWeight,
              fontStyle, fontVariant, fontStretch,
              slope, panose1, ascent, descent,
              strikethroughPosition, strikethroughThickness,
              underlinePosition, underlineThickness,
              overlinePosition, overlineThickness);
        this.srcs = srcs;
    }

    /**
     * Constructes an SVGFontFace with the specfied fontName.
     */
    protected FontFace
        (String familyName) {
        super(familyName);
    }

    public static CSSFontFace createFontFace(String familyName,
                                             FontFace src) {
        return new CSSFontFace
            (new LinkedList(src.srcs), 
             familyName, src.unitsPerEm, src.fontWeight,
             src.fontStyle, src.fontVariant, src.fontStretch,
             src.slope, src.panose1, src.ascent, src.descent,
             src.strikethroughPosition, src.strikethroughThickness,
             src.underlinePosition, src.underlineThickness,
             src.overlinePosition, src.overlineThickness);
    }
    
    /**
     * Returns the font associated with this rule or element.
     */
    public GVTFontFamily getFontFamily(BridgeContext ctx) {
        if (fontSet.contains(familyName)) {
            return new AWTFontFamily(this);
        }

        Iterator iter = srcs.iterator(); 
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof String) {
                String s= (String)o;
                if (fontSet.contains(o))
                    return new AWTFontFamily(createFontFace((String)o, this));

            } else if (o instanceof ParsedURL) {
                try {
                    GVTFontFamily ff = getFontFamily(ctx, (ParsedURL)o);
                    if (ff != null)
                        return ff;
                } catch (Throwable t) {
                    // Do nothing couldn't get Referenced URL.
                }
            }   
        }

        return new AWTFontFamily(this);
    }

    /**
     * Tries to build a GVTFontFamily from a URL reference
     */
    protected GVTFontFamily getFontFamily(BridgeContext ctx,
                                          ParsedURL purl) {
        String purlStr = purl.toString();
        if (purl.getRef() != null) {
            // Reference must be to a SVGFont.
            Element e = getBaseElement(ctx);
            Element ref = ctx.getReferencedElement(e, purlStr);
            if (!ref.getNamespaceURI().equals(SVG_NAMESPACE_URI) ||
                !ref.getLocalName().equals(SVG_FONT_TAG)) {
                return null;
            }

            SVGDocument doc  = (SVGDocument)e.getOwnerDocument();
            SVGDocument rdoc = (SVGDocument)ref.getOwnerDocument();

            Element fontElt = ref;
            if (doc != rdoc) {
                fontElt = (Element)doc.importNode(ref, true);
                String base = XMLBaseSupport.getCascadedXMLBase(ref);
                Element g = doc.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
                g.appendChild(fontElt);
                g.setAttributeNS(XMLBaseSupport.XML_NAMESPACE_URI,
                                 "xml:base", base);
                CSSUtilities.computeStyleAndURIs(ref, fontElt, purlStr);
            }
            
            GVTFontFace gvtFontFace = this;
            // Search for a font-face element
            Element fontFaceElt = null;
            for (Node n = fontElt.getFirstChild();
                 n != null;
                 n = n.getNextSibling()) {
                if ((n.getNodeType() == n.ELEMENT_NODE) &&
                    n.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
                    n.getLocalName().equals(SVG_FONT_FACE_TAG)) {
                    fontFaceElt = (Element)n;
                    break;
                }
            }
            
            SVGFontFaceElementBridge fontFaceBridge;
            fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge
                (SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);
            GVTFontFace gff = fontFaceBridge.createFontFace(ctx, fontFaceElt);
            

            return new SVGFontFamily(gff, fontElt, ctx);
        }
        // Must be a reference to a 'Web Font'.
        // Let's see if JDK can parse it.
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                                        purl.openStream());
            return new AWTFontFamily(this, font);
        } catch (Throwable t) {
        }
        return null;
    }

    /**
     * Default implementation uses the root element of the document 
     * associated with BridgeContext.  This is useful for CSS case.
     */
    protected Element getBaseElement(BridgeContext ctx) {
        SVGDocument d = (SVGDocument)ctx.getDocument();
        return d.getRootElement();
    }

}
