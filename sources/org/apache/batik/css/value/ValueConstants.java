/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.util.CSSConstants;

import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This interface defines the CSS values constants.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ValueConstants extends CSSConstants {
    /**
     * The 'inherit' value.
     */
    ImmutableValue INHERIT = ImmutableInherit.INSTANCE;

    /**
     * The '100' float value.
     */
    ImmutableValue NUMBER_100 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 100);

    /**
     * The '200' float value.
     */
    ImmutableValue NUMBER_200 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 200);

     /**
     * The '300' float value.
     */
    ImmutableValue NUMBER_300 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 300);

    /**
     * The '400' float value.
     */
    ImmutableValue NUMBER_400 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 400);

    /**
     * The '500' float value.
     */
    ImmutableValue NUMBER_500 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 500);

    /**
     * The '600' float value.
     */
    ImmutableValue NUMBER_600 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 600);

    /**
     * The '700' float value.
     */
    ImmutableValue NUMBER_700 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 700);

    /**
     * The '800' float value.
     */
    ImmutableValue NUMBER_800 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 800);

    /**
     * The '900' float value.
     */
    ImmutableValue NUMBER_900 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 900);

    /**
     * The 'auto' identifier.
     */
    ImmutableValue AUTO_VALUE =
        new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_AUTO_VALUE);

    /**
     * The 'background' identifier value.
     */
    ImmutableValue BACKGROUND_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BACKGROUND_VALUE);

    /**
     * The 'bidi-override' identifier value.
     */
    ImmutableValue BIDI_OVERRIDE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BIDI_OVERRIDE_VALUE);

    /**
     * The 'blink' identifier value.
     */
    ImmutableValue BLINK_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BLINK_VALUE);

    /**
     * The 'block' identifier value.
     */
    ImmutableValue BLOCK_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BLOCK_VALUE);

    /**
     * The 'bold' identifier value.
     */
    ImmutableValue BOLD_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BOLD_VALUE);

    /**
     * The 'bolder' identifier value.
     */
    ImmutableValue BOLDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BOLDER_VALUE);

    /**
     * The 'collapse' identifier value.
     */
    ImmutableValue COLLAPSE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_COLLAPSE_VALUE);

    /**
     * The 'compact' identifier value.
     */
    ImmutableValue COMPACT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_COMPACT_VALUE);

    /**
     * The 'condensed' identifier value.
     */
    ImmutableValue CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_CONDENSED_VALUE);

    /**
     * The 'crosshair' identifier value.
     */
    ImmutableValue CROSSHAIR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_CROSSHAIR_VALUE);

    /**
     * The 'cursive' keyword.
     */
    ImmutableValue CURSIVE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_CURSIVE_VALUE);

    /**
     * The 'default' identifier value.
     */
    ImmutableValue DEFAULT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_DEFAULT_VALUE);

    /**
     * The 'e-resize' identifier value.
     */
    ImmutableValue E_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_E_RESIZE_VALUE);

    /**
     * The 'embed' identifier value.
     */
    ImmutableValue EMBED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_EMBED_VALUE);

    /**
     * The 'expanded' identifier value.
     */
    ImmutableValue EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_EXPANDED_VALUE);

    /**
     * The 'extra-condensed' identifier value.
     */
    ImmutableValue EXTRA_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_EXTRA_CONDENSED_VALUE);

    /**
     * The 'extra-expanded' identifier value.
     */
    ImmutableValue EXTRA_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_EXTRA_EXPANDED_VALUE);

    /**
     * The 'fantasy' keyword.
     */
    ImmutableValue FANTASY_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_FANTASY_VALUE);

    /**
     * The 'graytext' identifier value.
     */
    ImmutableValue GRAYTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_GRAYTEXT_VALUE);

    /**
     * The 'help' identifier value.
     */
    ImmutableValue HELP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_HELP_VALUE);

    /**
     * The 'hidden' identifier value.
     */
    ImmutableValue HIDDEN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_HIDDEN_VALUE);

    /**
     * The 'inline' identifier value.
     */
    ImmutableValue INLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_INLINE_VALUE);

    /**
     * The 'inline-table' identifier value.
     */
    ImmutableValue INLINE_TABLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_INLINE_TABLE_VALUE);

    /**
     * The 'italic' identifier value.
     */
    ImmutableValue ITALIC_VALUE =
        new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ITALIC_VALUE);

    /**
     * The 'large' identifier value.
     */
    ImmutableValue LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LARGE_VALUE);

    /**
     * The 'larger' identifier value.
     */
    ImmutableValue LARGER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LARGER_VALUE);

    /**
     * The 'lighter' identifier value.
     */
    ImmutableValue LIGHTER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LIGHTER_VALUE);

    /**
     * The 'line-through' identifier value.
     */
    ImmutableValue LINE_THROUGH_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LINE_THROUGH_VALUE);

    /**
     * The 'list-item' identifier value.
     */
    ImmutableValue LIST_ITEM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LIST_ITEM_VALUE);

    /**
     * The 'ltr' keyword.
     */
    ImmutableValue LTR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LTR_VALUE);

    /**
     * The 'marker' identifier value.
     */
    ImmutableValue MARKER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MARKER_VALUE);

    /**
     * The 'medium' identifier value.
     */
    ImmutableValue MEDIUM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MEDIUM_VALUE);

    /**
     * The 'monospaced' keyword.
     */
    ImmutableValue MONOSPACED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MONOSPACED_VALUE);

    /**
     * The 'move' identifier value.
     */
    ImmutableValue MOVE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MOVE_VALUE);

    /**
     * The 'n-resize' identifier value.
     */
    ImmutableValue N_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_N_RESIZE_VALUE);

    /**
     * The 'narrower' identifier value.
     */
    ImmutableValue NARROWER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NARROWER_VALUE);

    /**
     * The 'ne-resize' identifier value.
     */
    ImmutableValue NE_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NE_RESIZE_VALUE);

    /**
     * The 'none' identifier value.
     */
    ImmutableValue NONE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NONE_VALUE);

    /**
     * The 'normal' identifier value.
     */
    ImmutableValue NORMAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NORMAL_VALUE);

    /**
     * The 'nw-resize' identifier value.
     */
    ImmutableValue NW_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NW_RESIZE_VALUE);

    /**
     * The 'oblique' identifier value.
     */
    ImmutableValue OBLIQUE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_OBLIQUE_VALUE);

    /**
     * The 'overline' identifier value.
     */
    ImmutableValue OVERLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_OVERLINE_VALUE);

    /**
     * The 'pointer' identifier value.
     */
    ImmutableValue POINTER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_POINTER_VALUE);

    /**
     * The 'rtl' keyword.
     */
    ImmutableValue RTL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_RTL_VALUE);

    /**
     * The 'run-in' identifier value.
     */
    ImmutableValue RUN_IN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_RUN_IN_VALUE);

    /**
     * The 's-resize' identifier value.
     */
    ImmutableValue S_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_S_RESIZE_VALUE);

    /**
     * The 'sans-serif' keyword.
     */
    ImmutableValue SANS_SERIF_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SANS_SERIF_VALUE);

    /**
     * The 'scroll' identifier value.
     */
    ImmutableValue SCROLL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SCROLLBAR_VALUE);

    /**
     * The 'se-resize' identifier value.
     */
    ImmutableValue SE_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SE_RESIZE_VALUE);

    /**
     * The 'semi-condensed' identifier value.
     */
    ImmutableValue SEMI_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SEMI_CONDENSED_VALUE);

    /**
     * The 'semi-expanded' identifier value.
     */
    ImmutableValue SEMI_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SEMI_EXPANDED_VALUE);

    /**
     * The 'serif' keyword.
     */
    ImmutableValue SERIF_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SERIF_VALUE);

    /**
     * The 'small' identifier value.
     */
    ImmutableValue SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SMALL_VALUE);

    /**
     * The 'small-caps' identifier value.
     */
    ImmutableValue SMALL_CAPS_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SMALL_CAPS_VALUE);

    /**
     * The 'smaller' identifier value.
     */
    ImmutableValue SMALLER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SMALLER_VALUE);

    /**
     * The 'sw-resize' identifier value.
     */
    ImmutableValue SW_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SW_RESIZE_VALUE);

    /**
     * The 'table' identifier value.
     */
    ImmutableValue TABLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_VALUE);

    /**
     * The 'table-caption' identifier value.
     */
    ImmutableValue TABLE_CAPTION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_CAPTION_VALUE);

    /**
     * The 'table-cell' identifier value.
     */
    ImmutableValue TABLE_CELL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_CELL_VALUE);

    /**
     * The 'table-column' identifier value.
     */
    ImmutableValue TABLE_COLUMN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_COLUMN_VALUE);

    /**
     * The 'table-column-group' identifier value.
     */
    ImmutableValue TABLE_COLUMN_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_COLUMN_GROUP_VALUE);

    /**
     * The 'table-footer-group' identifier value.
     */
    ImmutableValue TABLE_FOOTER_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_FOOTER_GROUP_VALUE);

    /**
     * The 'table-header-group' identifier value.
     */
    ImmutableValue TABLE_HEADER_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_HEADER_GROUP_VALUE);

    /**
     * The 'table-row' identifier value.
     */
    ImmutableValue TABLE_ROW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_ROW_VALUE);

    /**
     * The 'table-row-group' identifier value.
     */
    ImmutableValue TABLE_ROW_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TABLE_ROW_GROUP_VALUE);

    /**
     * The 'text' identifier value.
     */
    ImmutableValue TEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TEXT_VALUE);

    /**
     * The 'ultra-condensed' identifier value.
     */
    ImmutableValue ULTRA_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ULTRA_CONDENSED_VALUE);

    /**
     * The 'ultra-expanded' identifier value.
     */
    ImmutableValue ULTRA_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ULTRA_EXPANDED_VALUE);

    /**
     * The 'underline' identifier value.
     */
    ImmutableValue UNDERLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_UNDERLINE_VALUE);

    /**
     * The 'visible' identifier value.
     */
    ImmutableValue VISIBLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_VISIBLE_VALUE);

    /**
     * The 'w-resize' identifier value.
     */
    ImmutableValue W_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_W_RESIZE_VALUE);

    /**
     * The 'wait' identifier value.
     */
    ImmutableValue WAIT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_WAIT_VALUE);

    /**
     * The 'wider' identifier value.
     */
    ImmutableValue WIDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_WIDER_VALUE);

    /**
     * The 'window' identifier value.
     */
    ImmutableValue WINDOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_WINDOW_VALUE);

    /**
     * The 'windowframe' identifier value.
     */
    ImmutableValue WINDOWFRAME_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_WINDOWFRAME_VALUE);

    /**
     * The 'windowtext' identifier value.
     */
    ImmutableValue WINDOWTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_WINDOWTEXT_VALUE);

    /**
     * The 'x-large' identifier value.
     */
    ImmutableValue X_LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_X_LARGE_VALUE);

    /**
     * The 'x-small' identifier value.
     */
    ImmutableValue X_SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_X_SMALL_VALUE);

    /**
     * The 'xx-large' identifier value.
     */
    ImmutableValue XX_LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_XX_LARGE_VALUE);

    /**
     * The 'xx-small' identifier value.
     */
    ImmutableValue XX_SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_XX_SMALL_VALUE);

}
