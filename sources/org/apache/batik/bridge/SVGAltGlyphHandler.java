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

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * SVG font altGlyph handler. This class handles the creation of an alternate
 * GVTGlyphVector for the altGlyph element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGAltGlyphHandler implements AltGlyphHandler, SVGConstants {

    private BridgeContext ctx;
    private Element textElement;

    /**
     * Constructs an SVGAltGlyphHandler.
     *
     * @param ctx The bridge context, this is needed during rendering to find
     * any referenced glyph elements.
     * @param textElement The element that contains text to be replaced by the
     * alternate glyphs. This should be an altGlyph element.
     */
    public SVGAltGlyphHandler(BridgeContext ctx, Element textElement) {
        this.ctx = ctx;
        this.textElement = textElement;
    }

    /**
     * Creates a glyph vector containing the alternate glyphs.
     *
     * @param frc The current font render context.
     * @param fontSize The required font size.
     * @return The GVTGlyphVector containing the alternate glyphs, or null if
     * the alternate glyphs could not be found.
     */
    public GVTGlyphVector createGlyphVector
        (FontRenderContext frc, float fontSize,
         AttributedCharacterIterator aci) {
        try {
            if (textElement.getTagName().equals(SVG_ALT_GLYPH_TAG)) {
                SVGAltGlyphElementBridge altGlyphBridge
                    = (SVGAltGlyphElementBridge)ctx.getBridge(textElement);
                Glyph[] glyphArray = altGlyphBridge.createAltGlyphArray
                    (ctx, textElement, fontSize, aci);
                if (glyphArray != null) {
                    return new SVGGVTGlyphVector(null, glyphArray, frc);
                }
            }
        } catch (SecurityException e) {
            ctx.getUserAgent().displayError(e);
            // Throw exception because we do not want to continue
            // processing. In the case of a SecurityException, the 
            // end user would get a lot of exception like this one.
            throw e;
        }

        return null;
    }
}

