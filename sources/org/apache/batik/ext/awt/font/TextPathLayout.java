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

package org.apache.batik.ext.awt.font;

import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.geom.PathLength;

/**
 * PathLayout can layout text along a Shape, usually a Path object.
 * <p>
 * There are a number of improvements that could be made to this class.
 * I'll try to list some of them:
 * <ul>
 * <li> The layout should really only modify the GlyphVector, rather
 *      than converting to a Shape.
 * <li> Maybe the functions should take a AttributedCharacterIterator
 *      or something? Should this class do the entire layout?
 * <li> The layout code works, but it's definitely not perfect.
 * </ul>
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */

public class TextPathLayout {

    /**
     * Align the text at the start of the path.
     */
    static public final int ALIGN_START = 0;
    /**
     * Align the text at the middle of the path.
     */
    static public final int ALIGN_MIDDLE = 1;
    /**
     * Align the text at the end of the path.
     */
    static public final int ALIGN_END = 2;

    /**
     * Use the spacing between the glyphs to adjust for textLength.
     */
    static public final int ADJUST_SPACING = 0;
    /**
     * Use the entire glyph to adjust for textLength.
     */
    static public final int ADJUST_GLYPHS = 1;

    /**
     * Wraps the GlyphVector around the given path. The results
     * are mostly quite nice but you need to be careful choosing
     * the size of the font that created the GlyphVector, as
     * well as the "curvyness" of the path (really dynamic curves
     * don't look so great, abrupt changes/vertices look worse).
     * 
     * @param glyphs The GlyphVector to layout.
     * @param path The path (or shape) to wrap around
     * @param align The text alignment to use. Should be one
     *              of ALIGN_START, ALIGN_MIDDLE or ALIGN_END.
     * @param startOffset The offset from the start of the path for the initial
     *              text position.
     * @param textLength The length that the text should fill.
     * @param lengthAdjustMode The method used to expand or contract
     *                         the text to meet the textLength.
     * @return A shape that is the outline of the glyph vector
     * wrapped along the path
     */


    static public Shape layoutGlyphVector(GlyphVector glyphs, 
					  Shape path, int align,
					  float startOffset,
					  float textLength,
					  int lengthAdjustMode) {

	GeneralPath newPath = new GeneralPath();
	PathLength pl = new PathLength(path);
	float pathLength = pl.lengthOfPath();
	float glyphsLength = (float) glyphs.getVisualBounds().getWidth();

	// return from the ugly cases
	if (path == null ||
	    glyphs == null ||
	    glyphs.getNumGlyphs() == 0 ||
	    pl.lengthOfPath() == 0f ||
	    glyphsLength == 0f) {
	    return newPath;
	}

	// work out the expansion/contraction per character
	float lengthRatio = textLength / glyphsLength;

	// the current start point of the character on the path
	float currentPosition = startOffset;

	// if align is START then a currentPosition of 0f
	// is correct. 
	// if align is END then the currentPosition should
	// be enough to place the last character on the end
	// of the path
	// if align is MIDDLE then the currentPosition should
	// be enough to center the glyphs on the path

	if (align == ALIGN_END) {
	    currentPosition += pathLength - textLength;
	} else if (align == ALIGN_MIDDLE) {
	    currentPosition += (pathLength - textLength) / 2;
	}

	// iterate through the GlyphVector placing each glyph

	for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
	    
	    GlyphMetrics gm = glyphs.getGlyphMetrics(i);

	    float charAdvance = gm.getAdvance();

	    Shape glyph = glyphs.getGlyphOutline(i);

	    // if lengthAdjust was GLYPHS, then scale the glyph
	    // by the lengthRatio in the X direction
	    // FIXME: for vertical text this will be the Y direction
	    if (lengthAdjustMode == ADJUST_GLYPHS) {
		AffineTransform scale = AffineTransform.getScaleInstance(lengthRatio, 1f);
		glyph = scale.createTransformedShape(glyph);

		// charAdvance has to scale accordingly
		charAdvance *= lengthRatio;
	    }

	    float glyphWidth = (float) glyph.getBounds2D().getWidth();
	    
	    // Use either of these to calculate the mid point
	    // of the character along the path.
	    // If you change this, you must also change the 
	    // transform on the glyph down below
	    // In some case this gives better layout, but
	    // the way it is at the moment is a closer match
	    // to the textPath layout from the SVG spec

	    //float charMidPos = currentPosition + charAdvance / 2f;
	    float charMidPos = currentPosition + glyphWidth / 2f;

	    // Calculate the actual point to place the glyph around
	    Point2D charMidPoint = pl.pointAtLength(charMidPos);

	    // Check if the glyph is actually on the path

	    if (charMidPoint != null) {

		// Calculate the normal to the path (midline of glyph)
		float angle = pl.angleAtLength(charMidPos);

		// Define the transform of the glyph
		AffineTransform glyphTrans = new AffineTransform();

		// translate to the point on the path
		glyphTrans.translate(charMidPoint.getX(), charMidPoint.getY());

		// rotate midline of glyph to be normal to path
		glyphTrans.rotate(angle);

		// translate glyph backwards so we rotate about the 
		// center of the glyph
		// Choose one of these translations - see the comments
		// in the charMidPos calculation above
		glyphTrans.translate(charAdvance / -2f, 0f);
		//glyphTrans.translate(glyphWidth / -2f, 0f);

		glyph = glyphTrans.createTransformedShape(glyph);
		newPath.append(glyph, false);

	    }
	    
	    // move along by the advance value
	    // if the lengthAdjustMode was SPACING then
	    // we have to take this into account here
	    if (lengthAdjustMode == ADJUST_SPACING) {
		currentPosition += (charAdvance * lengthRatio);
	    } else {
		currentPosition += charAdvance;
	    }
	    
	}

	return newPath;
    }

    /**
     * Wraps the GlyphVector around the given path. 
     * 
     * @param glyphs The GlyphVector to layout.
     * @param path The path (or shape) to wrap around
     * @param align The text alignment to use. Should be one
     *              of ALIGN_START, ALIGN_MIDDLE or ALIGN_END.
     * @return A shape that is the outline of the glyph vector
     * wrapped along the path
     */

    static public Shape layoutGlyphVector(GlyphVector glyphs, 
					  Shape path, int align) {

	return layoutGlyphVector(glyphs, path, align, 0f,
				 (float) glyphs.getVisualBounds().getWidth(),
				 ADJUST_SPACING);
    }

    /**
     * Wraps the GlyphVector around the given path. 
     * 
     * @param glyphs The GlyphVector to layout.
     * @param path The path (or shape) to wrap around
     * @return A shape that is the outline of the glyph vector
     * wrapped along the path
     */

    static public Shape layoutGlyphVector(GlyphVector glyphs, 
					  Shape path) {

	return layoutGlyphVector(glyphs, path, ALIGN_START);
    }


} // TextPathLayout
