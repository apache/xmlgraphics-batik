/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.StringCharacterIterator;
import java.text.AttributedString;

import org.apache.batik.gvt.TextNode;

/**
 * GVTAttributedCharacterIterator
 *
 * Used to implement SVG &lt;tspan&gt; and &lt;text&gt;
 * attributes.  This implementation is designed for efficient support
 * of per-character attributes (i.e. single character attribute spans).
 * It supports an extended set of TextAttributes, via inner class
 * SVGAttributedCharacterIterator.TextAttributes.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public interface GVTAttributedCharacterIterator extends
                                   AttributedCharacterIterator {

    /**
     * Sets this iterator's contents to an unattributed copy of String s.
     */
    public void setString(String s);

    /**
     * Assigns this iterator's contents to be equivalent to AttributedString s.
     */
    public void setString(AttributedString s);

    /**
     * Sets values of a per-character attribute associated with the content
     *     string.
     * Characters from <tt>beginIndex</tt> to <tt>endIndex</tt>
     *     (zero-offset) are assigned values for attribute key <tt>attr</tt>
     *     from the array <tt>attValues.</tt>
     * If the length of attValues is less than character span
     *     <tt>(endIndex-beginIndex)</tt> the last value is duplicated;
     *     if attValues is longer than the character span
     *     the extra values are ignored.
     * Note that if either beginIndex or endIndex are outside the bounds
     *     of the current character array they are clipped accordingly.
     */
    public void setAttributeArray(TextAttribute attr,
                        Object[] attValues, int beginIndex, int endIndex);

    //From java.text.AttributedCharacterIterator

    /**
     * Get the keys of all attributes defined on the iterator's text range.
     */
    public Set getAllAttributeKeys();

    /**
     * Get the value of the named attribute for the current
     *     character.
     */
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute);

    /**
     * Returns a map with the attributes defined on the current
     * character.
     */
    public Map getAttributes();

    /**
     * Get the index of the first character following the
     *     run with respect to all attributes containing the current
     *     character.
     */
    public int getRunLimit();

    /**
     * Get the index of the first character following the
     *      run with respect to the given attribute containing the current
     *      character.
     */
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute);

    /**
     * Get the index of the first character following the
     *     run with respect to the given attributes containing the current
     *     character.
     */
    public int getRunLimit(Set attributes);

    /**
     * Get the index of the first character of the run with
     *    respect to all attributes containing the current character.
     */
    public int getRunStart();

    /**
     * Get the index of the first character of the run with
     *      respect to the given attribute containing the current character.
     * @param attribute The attribute for whose appearance the first offset
     *      is requested.
     */
    public int getRunStart(AttributedCharacterIterator.Attribute attribute);

    /**
     * Get the index of the first character of the run with
     *      respect to the given attributes containing the current character.
     * @param attributes the Set of attributes which begins at the returned index.
     */
    public int getRunStart(Set attributes);

    //From CharacterIterator

    /**
     * Create a copy of this iterator
     */
    public Object clone();

    /**
     * Get the character at the current position (as returned
     *      by getIndex()).
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char current();

    /**
     * Sets the position to getBeginIndex().
     * @return the character at the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char first();

    /**
     * Get the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getBeginIndex();

    /**
     * Get the end index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getEndIndex();

    /**
     * Get the current index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getIndex();

    /**
     * Sets the position to getEndIndex()-1 (getEndIndex() if
     * the text is empty) and returns the character at that position.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char last();

    /**
     * Increments the iterator's index by one, returning the next character.
     * @return the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char next();

    /**
     * Decrements the iterator's index by one and returns
     * the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char previous();

    /**
     * Sets the position to the specified position in the text.
     * @param position The new (current) index into the text.
     * @return the character at new index <em>position</em>.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char setIndex(int position);

    //Inner classes:

    /**
     * Attribute keys that identify SVG text attributes.  Anchor point for
     * attribute values of X, Y, and ROTATION is determined by the character's
     * font and other attributes.
     * We duplicate the features of java.awt.font.TextAttribute rather than
     * subclassing because java.awt.font.TextAttribute is <em>final</em>.
     */
    public static class TextAttribute extends
                                                                            AttributedCharacterIterator.Attribute {

        /** Construct a TextAttribute key with name s */
        public TextAttribute(String s) {
            super(s);
        }

        /** Attribute span delimiter - new tspan, tref, or textelement.*/
        public final static TextAttribute TEXT_COMPOUND_DELIMITER =
                              new TextAttribute("TEXT_COMPOUND_DELIMITER");

        /** Anchor type.*/
        public final static TextAttribute ANCHOR_TYPE =
                              new TextAttribute("ANCHOR_TYPE");

        /** Marker attribute indicating explicit glyph layout.*/
        public final static TextAttribute EXPLICIT_LAYOUT =
                              new TextAttribute("EXPLICIT_LAYOUT");

        /** User-space X coordinate for character.*/
        public final static TextAttribute X = new TextAttribute("X");

        /** User-space Y coordinate for character.*/
        public final static TextAttribute Y = new TextAttribute("Y");

        /** User-space relative X coordinate for character.*/
        public final static TextAttribute DX = new TextAttribute("DX");

        /** User-space relative Y coordinate for character.*/
        public final static TextAttribute DY = new TextAttribute("DY");

        /** Rotation for character, in degrees.*/
        public final static TextAttribute ROTATION =
                                          new TextAttribute("ROTATION");

        /** Overall opacity of rendered text.*/
        public final static TextAttribute OPACITY =
                                          new TextAttribute("OPACITY");

        /** Stroke used to paint character outline.*/
        public final static TextAttribute STROKE =
                                          new TextAttribute("STROKE");

        /** Paint used to stroke character outline */
        public final static TextAttribute STROKE_PAINT =
                                          new TextAttribute("STROKE_PAINT");

        /** Underline flag for character.*/
        public final static TextAttribute UNDERLINE =
                                          new TextAttribute("UNDERLINE");

        /** Overline flag for character.*/
        public final static TextAttribute OVERLINE =
                                      new TextAttribute("OVERLINE");

        /** Stroke used to paint character underline.*/
        public final static TextAttribute UNDERLINE_STROKE =
                                      new TextAttribute("UNDERLINE_STROKE");

        /** Paint used to fill character underline.*/
        public final static TextAttribute UNDERLINE_PAINT =
                                      new TextAttribute("UNDERLINE_PAINT");

        /** Paint used to stroke character outline for underline.*/
        public final static TextAttribute UNDERLINE_STROKE_PAINT =
                                  new TextAttribute("UNDERLINE_STROKE_PAINT");

        /** Flag indicating that chars are to be "struck through" */
        public final static TextAttribute STRIKETHROUGH =
                                          new TextAttribute("STRIKETHROUGH");

        /** Author-expected width for bounding box containing
         *  all text string glyphs.
         */
        public final static TextAttribute BBOX_WIDTH =
                                          new TextAttribute("BBOX_WIDTH");

        /** Method specified for adjusting text element layout size.
         */
        public final static TextAttribute LENGTH_ADJUST =
                                          new TextAttribute("LENGTH_ADJUST");

        /** Convenience flag indicating that non-default glyph spacing is needed.
         */
        public final static TextAttribute CUSTOM_SPACING =
                                          new TextAttribute("CUSTOM_SPACING");

        /** User-specified inter-glyph kerning value.
         */
        public final static TextAttribute KERNING =
                                          new TextAttribute("KERNING");

        /** User-specified inter-glyph spacing value.
         */
        public final static TextAttribute LETTER_SPACING =
                                          new TextAttribute("LETTER_SPACING");

        /** User-specified width for whitespace characters.
         */
        public final static TextAttribute WORD_SPACING =
                                          new TextAttribute("WORD_SPACING");

        /** Font variant to be used for this character span.
         * @see org.apache.batik.gvt.text.GVTAttributedCharacterIterator.TextAttribute#SMALL_CAPS
         */
        public final static TextAttribute FONT_VARIANT =
                                          new TextAttribute("FONT_VARIANT");

        /** Baseline adjustment to be applied to this character span.
         */
        public final static TextAttribute BASELINE_SHIFT =
                                          new TextAttribute("BASELINE_SHIFT");

        /** Directional writing mode applied to this character span.
         */
        public final static TextAttribute WRITING_MODE =
                                          new TextAttribute("WRITING_MODE");



        // VALUES

        /** Value for WRITING_MODE indicating left-to-right */
        public final static Integer WRITING_MODE_LTR = new Integer(0x1);

        /** Value for WRITING_MODE indicating right-to-left */
        public final static Integer WRITING_MODE_RTL = new Integer(0x2);

        /** Value for FONT_VARIANT specifying small caps */
        public final static Integer SMALL_CAPS = new Integer(0x10);

        /** Value for UNDERLINE specifying underlining-on */
        public final static Integer UNDERLINE_ON =
                            java.awt.font.TextAttribute.UNDERLINE_ON;

        /** Value for OVERLINE specifying overlining-on */
        public final static Boolean OVERLINE_ON = new Boolean(true);

        /** Value for STRIKETHROUGH specifying strikethrough-on */
        public final static Boolean STRIKETHROUGH_ON =
                            java.awt.font.TextAttribute.STRIKETHROUGH_ON;

        /** Value for LENGTH_ADJUST specifying adjustment to inter-glyph spacing */
        public final static Integer ADJUST_SPACING =
                            new Integer(0x0);

        /** Value for LENGTH_ADJUST specifying overall scaling of layout outlines */
        public final static Integer ADJUST_ALL =
                            new Integer(0x01);

    }

    /**
     * Interface for helper class which mutates the attributes of an
     * AttributedCharacterIterator.
     * Typically used to convert location and rotation attributes to
     * TextAttribute.TRANSFORM attributes, or convert between implementations
     * of AttributedCharacterIterator.Attribute.
     */
    public interface AttributeFilter {

        /**
         * Modify an AttributedCharacterIterator's attributes systematically.
         * Usually returns a copy since AttributedCharacterIterator instances
         * are often immutable.  The effect of the attribute modification
         * is implementation dependent.
         * @param aci an AttributedCharacterIterator whose attributes are
         *     to be modified.
         * @return an instance of AttributedCharacterIterator with mutated
         *     attributes.
         */
        public AttributedCharacterIterator
            mutateAttributes(AttributedCharacterIterator aci);

    }
}









