/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * This interface defines constants for CSS.
 * Important: Constants must not contain uppercase characters.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSConstants {
    //
    // The CSS property names.
    //
    String CSS_ALIGNMENT_BASELINE_PROPERTY = "alignment-baseline";
    String CSS_BASELINE_SHIFT_PROPERTY = "baseline-shift";
    String CSS_CLIP_PROPERTY = "clip";
    String CSS_CLIP_PATH_PROPERTY = "clip-path";
    String CSS_CLIP_RULE_PROPERTY = "clip-rule";
    String CSS_COLOR_PROPERTY = "color";
    String CSS_COLOR_INTERPOLATION_PROPERTY = "color-interpolation";
    String CSS_COLOR_RENDERING_PROPERTY = "color-rendering";
    String CSS_CURSOR_PROPERTY = "cursor";
    String CSS_DIRECTION_PROPERTY = "direction";
    String CSS_DISPLAY_PROPERTY = "display";
    String CSS_DOMINANT_BASELINE_PROPERTY = "dominant-baseline";
    String CSS_ENABLE_BACKGROUND_PROPERTY = "enable-background";
    String CSS_FILL_OPACITY_PROPERTY = "fill-opacity";
    String CSS_FILL_PROPERTY = "fill";
    String CSS_FILL_RULE_PROPERTY = "fill-rule";
    String CSS_FILTER_PROPERTY = "filter";
    String CSS_FLOOD_COLOR_PROPERTY = "flood-color";
    String CSS_FLOOD_OPACITY_PROPERTY = "flood-opacity";
    String CSS_FONT_FAMILY_PROPERTY = "font-family";
    String CSS_FONT_SIZE_PROPERTY = "font-size";
    String CSS_FONT_SIZE_ADJUST_PROPERTY = "font-size-adjust";
    String CSS_FONT_STRETCH_PROPERTY = "font-stretch";
    String CSS_FONT_STYLE_PROPERTY = "font-style";
    String CSS_FONT_VARIANT_PROPERTY = "font-variant";
    String CSS_FONT_WEIGHT_PROPERTY = "font-weight";
    String CSS_GLYPH_ORIENTATION_HORIZONTAL_PROPERTY = "glyph-orientation-horizontal";
    String CSS_GLYPH_ORIENTATION_VERTICAL_PROPERTY = "glyph-orientation-vertical";
    String CSS_IMAGE_RENDERING_PROPERTY = "image-rendering";
    String CSS_LETTER_SPACING_PROPERTY = "letter-spacing";
    String CSS_LIGHTING_COLOR_PROPERTY = "lighting-color";
    String CSS_MARKER_PROPERTY = "marker";
    String CSS_MARKER_END_PROPERTY = "marker-end";
    String CSS_MARKER_MID_PROPERTY = "marker-mid";
    String CSS_MARKER_START_PROPERTY = "marker-start";
    String CSS_MASK_PROPERTY = "mask";
    String CSS_OPACITY_PROPERTY = "opacity";
    String CSS_OVERFLOW_PROPERTY = "overflow";
    String CSS_POINTER_EVENTS_PROPERTY = "pointer-events";
    String CSS_SHAPE_RENDERING_PROPERTY = "shape-rendering";
    String CSS_STOP_COLOR_PROPERTY = "stop-color";
    String CSS_STOP_OPACITY_PROPERTY = "stop-opacity";
    String CSS_STROKE_PROPERTY = "stroke";
    String CSS_STROKE_DASHARRAY_PROPERTY = "stroke-dasharray";
    String CSS_STROKE_DASHOFFSET_PROPERTY = "stroke-dashoffset";
    String CSS_STROKE_LINECAP_PROPERTY = "stroke-linecap";
    String CSS_STROKE_LINEJOIN_PROPERTY = "stroke-linejoin";
    String CSS_STROKE_MITERLIMIT_PROPERTY = "stroke-miterlimit";
    String CSS_STROKE_OPACITY_PROPERTY = "stroke-opacity";
    String CSS_STROKE_WIDTH_PROPERTY = "stroke-width";
    String CSS_TEXT_ANCHOR_PROPERTY = "text-anchor";
    String CSS_TEXT_DECORATION_PROPERTY = "text-decoration";
    String CSS_TEXT_RENDERING_PROPERTY = "text-rendering";
    String CSS_UNICODE_BIDI_PROPERTY = "unicode-bidi";
    String CSS_VISIBILITY_PROPERTY = "visibility";
    String CSS_WORD_SPACING_PROPERTY = "word-spacing";
    String CSS_WRITING_MODE_PROPERTY = "writing-mode";

    //
    // The CSS property values.
    //
    String CSS_ACCUMULATE_VALUE = "accumulate";
    String CSS_ACTIVEBORDER_VALUE = "activeborder";
    String CSS_ACTIVECAPTION_VALUE = "activecaption";
    String CSS_AFTER_EDGE_VALUE = "after-edge";
    String CSS_ALL_VALUE = "all";
    String CSS_APPWORKSPACE_VALUE = "appworkspace";
    String CSS_AUTO_VALUE = "auto";
    String CSS_AUTOSENSE_SCRIPT_VALUE = "autosense-script";
    String CSS_BACKGROUND_VALUE = "background";
    String CSS_BASELINE_VALUE = "baseline";
    String CSS_BEFORE_EDGE_VALUE = "before-edge";
    String CSS_BEVEL_VALUE = "bevel";
    String CSS_BIDI_OVERRIDE_VALUE = "bidi-override";
    String CSS_BLINK_VALUE = "blink";
    String CSS_BLOCK_VALUE = "block";
    String CSS_BOLD_VALUE = "bold";
    String CSS_BOLDER_VALUE = "bolder";
    String CSS_BOTTOM_VALUE = "bottom";
    String CSS_BUTT_VALUE = "butt";
    String CSS_BUTTONFACE_VALUE = "buttonface";
    String CSS_BUTTONHIGHLIGHT_VALUE = "buttonhighlight";
    String CSS_BUTTONSHADOW_VALUE = "buttonshadow";
    String CSS_BUTTONTEXT_VALUE = "buttontext";
    String CSS_CAPTIONTEXT_VALUE = "captiontext";
    String CSS_COLLAPSE_VALUE = "collapse";
    String CSS_COMPACT_VALUE = "compact";
    String CSS_CONDENSED_VALUE = "condensed";
    String CSS_CRISPEDGES_VALUE = "crispedges";
    String CSS_CROSSHAIR_VALUE = "crosshair";
    String CSS_CURRENTCOLOR_VALUE = "currentcolor";
    String CSS_CURSIVE_VALUE = "cursive";
    String CSS_DEFAULT_VALUE = "default";
    String CSS_E_RESIZE_VALUE = "e-resize";
    String CSS_EMBED_VALUE = "embed";
    String CSS_END_VALUE = "end";
    String CSS_EVENODD_VALUE = "evenodd";
    String CSS_EXPANDED_VALUE = "expanded";
    String CSS_EXTRA_CONDENSED_VALUE = "extra-condensed";
    String CSS_EXTRA_EXPANDED_VALUE = "extra-expanded";
    String CSS_FANTASY_VALUE = "fantasy";
    String CSS_FILL_VALUE = "fill";
    String CSS_FILLSTROKE_VALUE = "fillstroke";
    String CSS_GEOMETRICPRECISION_VALUE = "geometricprecision";
    String CSS_GRAYTEXT_VALUE = "graytext";
    String CSS_HANGING_VALUE = "hanging";
    String CSS_HELP_VALUE = "help";
    String CSS_HIDDEN_VALUE = "hidden";
    String CSS_HIGHLIGHT_VALUE = "highlight";
    String CSS_HIGHLIGHTTEXT_VALUE = "highlighttext";
    String CSS_IDEOGRAPHIC_VALUE = "ideographic";
    String CSS_INACTIVEBORDER_VALUE = "inactiveborder";
    String CSS_INACTIVECAPTION_VALUE = "inactivecaption";
    String CSS_INACTIVECAPTIONTEXT_VALUE = "inactivecaptiontext";
    String CSS_INFOBACKGROUND_VALUE = "infobackground";
    String CSS_INFOTEXT_VALUE = "infotext";
    String CSS_INLINE_VALUE = "inline";
    String CSS_INLINE_TABLE_VALUE = "inline-table";
    String CSS_ITALIC_VALUE = "italic";
    String CSS_LARGE_VALUE = "large";
    String CSS_LARGER_VALUE = "larger";
    String CSS_LINE_THROUGH_VALUE = "line-through";
    String CSS_LINEARRGB_VALUE = "linearrgb";
    String CSS_LIGHTER_VALUE = "lighter";
    String CSS_LIST_ITEM_VALUE = "list-item";
    String CSS_LOWER_VALUE = "lower";
    String CSS_LR_VALUE = "lr";
    String CSS_LR_TB_VALUE = "lr-tb";
    String CSS_LTR_VALUE = "ltr";
    String CSS_MARKER_VALUE = "marker";
    String CSS_MATHEMATICAL_VALUE = "mathematical";
    String CSS_MEDIUM_VALUE = "medium";
    String CSS_MENU_VALUE = "menu";
    String CSS_MENUTEXT_VALUE = "menutext";
    String CSS_MIDDLE_VALUE = "middle";
    String CSS_MITER_VALUE = "miter";
    String CSS_MONOSPACED_VALUE = "monospaced";
    String CSS_MOVE_VALUE = "move";
    String CSS_N_RESIZE_VALUE = "n-resize";
    String CSS_NARROWER_VALUE = "narrower";
    String CSS_NE_RESIZE_VALUE = "ne-resize";
    String CSS_NEW_VALUE = "new";
    String CSS_NO_CHANGE_VALUE = "no-change";
    String CSS_NONZERO_VALUE = "nonzero";
    String CSS_NONE_VALUE = "none";
    String CSS_NORMAL_VALUE = "normal";
    String CSS_NW_RESIZE_VALUE = "nw-resize";
    String CSS_OBLIQUE_VALUE = "oblique";
    String CSS_OPTIMIZELEGIBILITY_VALUE = "optimizelegibility";
    String CSS_OPTIMIZEQUALITY_VALUE = "optimizequality";
    String CSS_OPTIMIZESPEED_VALUE = "optimizespeed";
    String CSS_OVERLINE_VALUE = "overline";
    String CSS_PAINTED_VALUE = "painted";
    String CSS_POINTER_VALUE = "pointer";
    String CSS_RESET_VALUE = "reset";
    String CSS_RL_VALUE = "rl";
    String CSS_RL_TB_VALUE = "rl-tb";
    String CSS_ROUND_VALUE = "round";
    String CSS_RTL_VALUE = "rtl";
    String CSS_RUN_IN_VALUE = "run-in";
    String CSS_S_RESIZE_VALUE = "s-resize";
    String CSS_SANS_SERIF_VALUE = "sans-serif";
    String CSS_SCROLL_VALUE = "scroll";
    String CSS_SCROLLBAR_VALUE = "scrollbar";
    String CSS_SE_RESIZE_VALUE = "se-resize";
    String CSS_SEMI_CONDENSED_VALUE = "semi-condensed";
    String CSS_SEMI_EXPANDED_VALUE = "semi-expanded";
    String CSS_SERIF_VALUE = "serif";
    String CSS_SMALL_VALUE = "small";
    String CSS_SMALL_CAPS_VALUE = "small-caps";
    String CSS_SMALLER_VALUE = "smaller";
    String CSS_SQUARE_VALUE = "square";
    String CSS_SRGB_VALUE = "srgb";
    String CSS_START_VALUE = "start";
    String CSS_STROKE_VALUE = "stroke";
    String CSS_SUB_VALUE = "sub";
    String CSS_SUPER_VALUE = "super";
    String CSS_SW_RESIZE_VALUE = "sw-resize";
    String CSS_TABLE_VALUE = "table";
    String CSS_TABLE_CAPTION_VALUE = "table-caption";
    String CSS_TABLE_CELL_VALUE = "table-cell";
    String CSS_TABLE_COLUMN_VALUE = "table-column";
    String CSS_TABLE_COLUMN_GROUP_VALUE = "table-column-group";
    String CSS_TABLE_FOOTER_GROUP_VALUE = "table-footer-group";
    String CSS_TABLE_HEADER_GROUP_VALUE = "table-header-group";
    String CSS_TABLE_ROW_VALUE = "table-row";
    String CSS_TABLE_ROW_GROUP_VALUE = "table-row-group";
    String CSS_TB_VALUE = "tb";
    String CSS_TB_RL_VALUE = "tb-rl";
    String CSS_TEXT_VALUE = "text";
    String CSS_TEXT_AFTER_EDGE_VALUE = "text-after-edge";
    String CSS_TEXT_BEFORE_EDGE_VALUE = "text-before-edge";
    String CSS_TEXT_BOTTOM_VALUE = "text-bottom";
    String CSS_TEXT_TOP_VALUE = "text-top";
    String CSS_THREEDDARKSHADOW_VALUE = "threeddarkshadow";
    String CSS_THREEDFACE_VALUE = "threedface";
    String CSS_THREEDHIGHLIGHT_VALUE = "threedhighlight";
    String CSS_THREEDLIGHTSHADOW_VALUE = "threedlightshadow";
    String CSS_THREEDSHADOW_VALUE = "threedshadow";
    String CSS_TOP_VALUE = "top";
    String CSS_ULTRA_CONDENSED_VALUE = "ultra-condensed";
    String CSS_ULTRA_EXPANDED_VALUE = "ultra-expanded";
    String CSS_UNDERLINE_VALUE = "underline";
    String CSS_VISIBLE_VALUE = "visible";
    String CSS_VISIBLEFILL_VALUE = "visiblefill";
    String CSS_VISIBLEFILLSTROKE_VALUE = "visiblefillstroke";
    String CSS_VISIBLEPAINTED_VALUE = "visiblepainted";
    String CSS_VISIBLESTROKE_VALUE = "visiblestroke";
    String CSS_W_RESIZE_VALUE = "w-resize";
    String CSS_WAIT_VALUE = "wait";
    String CSS_WIDER_VALUE = "wider";
    String CSS_WINDOW_VALUE = "window";
    String CSS_WINDOWFRAME_VALUE = "windowframe";
    String CSS_WINDOWTEXT_VALUE = "windowtext";
    String CSS_X_LARGE_VALUE = "x-large";
    String CSS_X_SMALL_VALUE = "x-small";
    String CSS_XX_LARGE_VALUE = "xx-large";
    String CSS_XX_SMALL_VALUE = "xx-small";

}
