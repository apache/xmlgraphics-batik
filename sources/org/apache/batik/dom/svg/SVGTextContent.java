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

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import org.w3c.dom.DOMException;

/**
 * This class provides the interface for the SVGTextContentElement
 * for the bridge to implement.
 *
 * @author nicolas.socheleau@bitflash.com
 * @version $Id$
 */
public interface SVGTextContent
{
    /**
     * Returns the total number of characters to be 
     * rendered within the current element. 
     * Includes characters which are included 
     * via a &lt;tref&gt; reference. 
     *
     * @return Total number of characters.
     */
    public int getNumberOfChars();

    /**
     * Returns a tightest rectangle which defines the 
     * minimum and maximum X and Y values in the user 
     * coordinate system for rendering the glyph(s) 
     * that correspond to the specified character. 
     * The calculations assume that  all glyphs occupy 
     * the full standard glyph cell for the font. If 
     * multiple consecutive characters are rendered 
     * inseparably (e.g., as a single glyph or a 
     * sequence of glyphs), then each of the inseparable 
     * characters will return the same extent. 
     * 
     * @param charnum The index of the character, where the 
     *    first character has an index of 0.
     * @return The rectangle which encloses all of 
     *    the rendered glyph(s).
     */
    public Rectangle2D getExtentOfChar(int charnum );

    /**
     * Returns the current text position before rendering 
     * the character in the user coordinate system for 
     * rendering the glyph(s) that correspond to the 
     * specified character. The current text position has 
     * already taken into account the effects of any inter- 
     * character adjustments due to properties 'kerning', 
     * 'letter-spacing' and 'word-spacing' and adjustments 
     * due to attributes x, y, dx and dy. If multiple 
     * consecutive characters are rendered inseparably 
     * (e.g., as a single glyph or a sequence of glyphs), 
     * then each of the inseparable characters will return
     * the start position for the first glyph. 
     * 
     * @param charnum The index of the character, where the 
     *    first character has an index of 0.
     * @return The character's start position.
     */
    public Point2D getStartPositionOfChar(int charnum);

    /**
     * Returns the current text position after rendering 
     * the character in the user coordinate system for 
     * rendering the glyph(s) that correspond to the 
     * specified character. This current text position 
     * does not take into account the effects of any inter-
     * character adjustments to prepare for the next 
     * character, such as properties 'kerning', 
     * 'letter-spacing' and 'word-spacing' and adjustments 
     * due to attributes x, y, dx and dy. If multiple 
     * consecutive characters are rendered inseparably 
     * (e.g., as a single glyph or a sequence of glyphs), 
     * then each of the inseparable characters will return 
     * the end position for the last glyph. 
     * 
     * @param charnum The index of the character, where the 
     *    first character has an index of 0.
     * @return The character's end position.
     */
    public Point2D getEndPositionOfChar(int charnum);

    /**
     * Returns the rotation value relative to the current 
     * user coordinate system used to render the glyph(s) 
     * corresponding to the specified character. If 
     * multiple glyph(s) are used to render the given 
     * character and the glyphs each have different 
     * rotations (e.g., due to text-on-a-path), the user 
     * agent shall return an average value (e.g., the 
     * rotation angle at the midpoint along the path for 
     * all glyphs used to render this character). The 
     * rotation value represents the rotation that is 
     * supplemental to any rotation due to properties 
     * 'glyph-orientation-horizontal' and 
     * 'glyph-orientation-vertical'; thus, any glyph 
     * rotations due to these properties are not included 
     * into the returned rotation value. If multiple 
     * consecutive characters are rendered inseparably 
     * (e.g., as a single glyph or a sequence of glyphs), 
     * then each of the inseparable characters will 
     * return the same rotation value. 
     *
     * @param charnum The index of the character, where the 
     *    first character has an index of 0.
     * @return The character's rotation angle.
     */
    public float getRotationOfChar(int charnum);
    /**
     * Causes the specified substring to be selected 
     * just as if the user selected the substring interactively. 
     *
     * @param charnum : The index of the start character 
     *   which is at the given point, where the first 
     *   character has an index of 0.
     * @param nchars : The number of characters in the 
     *   substring. If nchars specifies more characters 
     *   than are available, then the substring will 
     *   consist of all characters starting with charnum 
     *   until the end of the list of characters.
     */
    public void selectSubString(int charnum, int nchars);

    public float getComputedTextLength();

    public float getSubStringLength(int charnum, int nchars);

    public int getCharNumAtPosition(float x, float y);
}
