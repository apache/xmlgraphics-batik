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

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.UnicodeRange;
import org.w3c.dom.Element;

/**
 * A base Bridge class for the kerning elements.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public abstract class SVGKernElementBridge extends AbstractSVGBridge {

    /**
     * Creates a Kern object that repesents the specified kerning element.
     *
     * @param ctx The bridge context.
     * @param kernElement The kerning element. Should be either a &lt;hkern>
     * or &lt;vkern> element.
     * @param font The font the kerning is related to.
     *
     * @return kern The new Kern object
     */
    public Kern createKern(BridgeContext ctx,
                           Element kernElement,
                           SVGGVTFont font) {

        // read all of the kern attributes
        String u1 = kernElement.getAttributeNS(null, SVG_U1_ATTRIBUTE);
        String u2 = kernElement.getAttributeNS(null, SVG_U2_ATTRIBUTE);
        String g1 = kernElement.getAttributeNS(null, SVG_G1_ATTRIBUTE);
        String g2 = kernElement.getAttributeNS(null, SVG_G2_ATTRIBUTE);
        String k = kernElement.getAttributeNS(null, SVG_K_ATTRIBUTE);
        if (k.length() == 0) {
            k = SVG_KERN_K_DEFAULT_VALUE;
        }

        // get the kern float value
        float kernValue = Float.parseFloat(k);

        // set up the first and second glyph sets and unicode ranges
        Vector firstGlyphSet = new Vector();
        Vector secondGlyphSet = new Vector();
        Vector firstUnicodeRanges = new Vector();
        Vector secondUnicodeRanges = new Vector();

        // process the u1 attribute
        StringTokenizer st = new StringTokenizer(u1, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith("U+")) { // its a unicode range
                firstUnicodeRanges.add(new UnicodeRange(token));
            } else {
                int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                for (int i = 0; i < glyphCodes.length; i++) {
                    firstGlyphSet.add(new Integer(glyphCodes[i]));
                }
            }
        }

        // process the u2 attrbute
        st = new StringTokenizer(u2, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith("U+")) { // its a unicode range
                secondUnicodeRanges.add(new UnicodeRange(token));
            } else {
                int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                for (int i = 0; i < glyphCodes.length; i++) {
                    secondGlyphSet.add(new Integer(glyphCodes[i]));
                }
            }
        }

        // process the g1 attribute
        st = new StringTokenizer(g1, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int[] glyphCodes = font.getGlyphCodesForName(token);
            for (int i = 0; i < glyphCodes.length; i++) {
                firstGlyphSet.add(new Integer(glyphCodes[i]));
            }
        }

        // process the g2 attribute
        st = new StringTokenizer(g2, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int[] glyphCodes = font.getGlyphCodesForName(token);
            for (int i = 0; i < glyphCodes.length; i++) {
                secondGlyphSet.add(new Integer(glyphCodes[i]));
            }
        }

        // construct the arrays
        int[] firstGlyphs = new int[firstGlyphSet.size()];
        int[] secondGlyphs = new int[secondGlyphSet.size()];
        UnicodeRange[] firstRanges = new UnicodeRange[firstUnicodeRanges.size()];
        UnicodeRange[] secondRanges = new UnicodeRange[secondUnicodeRanges.size()];
        for (int i = 0; i < firstGlyphSet.size(); i++) {
            firstGlyphs[i] = ((Integer)firstGlyphSet.elementAt(i)).intValue();
        }
        for (int i = 0; i < secondGlyphSet.size(); i++) {
            secondGlyphs[i] = ((Integer)secondGlyphSet.elementAt(i)).intValue();
        }
        for (int i = 0; i < firstUnicodeRanges.size(); i++) {
            firstRanges[i] = (UnicodeRange)firstUnicodeRanges.elementAt(i);
        }
        for (int i = 0; i < secondUnicodeRanges.size(); i++) {
            secondRanges[i] = (UnicodeRange)secondUnicodeRanges.elementAt(i);
        }

        // return the new Kern object
        return new Kern(firstGlyphs, secondGlyphs, firstRanges, secondRanges, kernValue);
    }
}
