/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.ValueConstants;

import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This interface defines the CSS values constants for SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGValueConstants extends ValueConstants {
    /**
     * 0.
     */
    ImmutableValue NUMBER_0 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 0);

    /**
     * 1.
     */
    ImmutableValue NUMBER_1 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 1);

    /**
     * 4.
     */
    ImmutableValue NUMBER_4 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 4);

    /**
     * 90.
     */
    ImmutableValue NUMBER_90 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 90);

    /**
     * 180.
     */
    ImmutableValue NUMBER_180 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 180);

    /**
     * 255.
     */
    ImmutableValue NUMBER_255 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 255);

    /**
     * 270.
     */
    ImmutableValue NUMBER_270 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 270);

    /**
     * 360.
     */
    ImmutableValue NUMBER_360 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 360);

    /**
     * -90.
     */
    ImmutableValue NUMBER_MINUS_90 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, -90);

    /**
     * -180.
     */
    ImmutableValue NUMBER_MINUS_180 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, -180);

    /**
     * -270.
     */
    ImmutableValue NUMBER_MINUS_270 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, -270);

    /**
     * -360.
     */
    ImmutableValue NUMBER_MINUS_360 =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, -360);

    /**
     * The 'accumulate' keyword.
     */
    ImmutableValue ACCUMULATE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ACCUMULATE_VALUE);

    /**
     * The 'after-edge' keyword.
     */
    ImmutableValue AFTER_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_AFTER_EDGE_VALUE);
    
    /**
     * The 'all' keyword.
     */
    ImmutableValue ALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ALL_VALUE);

    /**
     * The 'autosense-script' keyword.
     */
    ImmutableValue AUTOSENSE_SCRIPT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_AUTOSENSE_SCRIPT_VALUE);

    /**
     * The 'baseline' keyword.
     */
    ImmutableValue BASELINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BASELINE_VALUE);

    /**
     * The 'before-edge' keyword.
     */
    ImmutableValue BEFORE_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BEFORE_EDGE_VALUE);

    /**
     * The 'bevel' keyword.
     */
    ImmutableValue BEVEL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BEVEL_VALUE);

    /**
     * The 'bottom' keyword.
     */
    ImmutableValue BOTTOM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BOTTOM_VALUE);

    /**
     * The 'butt' keyword.
     */
    ImmutableValue BUTT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_BUTT_VALUE);

    /**
     * The 'crispEdges' keyword.
     */
    ImmutableValue CRISPEDGES_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_CRISPEDGES_VALUE);

    /**
     * The 'currentColor' keyword.
     */
    ImmutableValue CURRENTCOLOR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_CURRENTCOLOR_VALUE);

    /**
     * The 'end' keyword.
     */
    ImmutableValue END_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_END_VALUE);

    /**
     * The 'evenodd' keyword.
     */
    ImmutableValue EVENODD_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_EVENODD_VALUE);

    /**
     * The 'fill' keyword.
     */
    ImmutableValue FILL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_FILL_VALUE);

    /**
     * The 'fillstroke' keyword.
     */
    ImmutableValue FILLSTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_FILLSTROKE_VALUE);

    /**
     * The 'geometricPrecision' keyword.
     */
    ImmutableValue GEOMETRICPRECISION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_GEOMETRICPRECISION_VALUE);

    /**
     * The 'hanging' keyword.
     */
    ImmutableValue HANGING_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_HANGING_VALUE);

    /**
     * The 'ideographic' keyword.
     */
    ImmutableValue IDEOGRAPHIC_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_IDEOGRAPHIC_VALUE);

    /**
     * The 'linearRGB' keyword.
     */
    ImmutableValue LINEARRGB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LINEARRGB_VALUE);

    /**
     * The 'lower' keyword.
     */
    ImmutableValue LOWER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LOWER_VALUE);

    /**
     * The 'lr' keyword.
     */
    ImmutableValue LR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LR_VALUE);

    /**
     * The 'lr-tb' keyword.
     */
    ImmutableValue LR_TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_LR_TB_VALUE);

    /**
     * The 'mathematical' keyword.
     */
    ImmutableValue MATHEMATICAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MATHEMATICAL_VALUE);

    /**
     * The 'middle' keyword.
     */
    ImmutableValue MIDDLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MIDDLE_VALUE);

    /**
     * The 'miter' keyword.
     */
    ImmutableValue MITER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_MITER_VALUE);

    /**
     * The 'new' keyword.
     */
    ImmutableValue NEW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NEW_VALUE);

    /**
     * The 'no-change' keyword.
     */
    ImmutableValue NO_CHANGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NO_CHANGE_VALUE);

    /**
     * The 'nonzero' keyword.
     */
    ImmutableValue NONZERO_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_NONZERO_VALUE);

    /**
     * The 'optimizeLegibility' identifier value.
     */
    ImmutableValue OPTIMIZELEGIBILITY_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_OPTIMIZELEGIBILITY_VALUE);

    /**
     * The 'optimizeQuality' keyword.
     */
    ImmutableValue OPTIMIZEQUALITY_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_OPTIMIZEQUALITY_VALUE);

    /**
     * The 'optimizeSpeed' keyword.
     */
    ImmutableValue OPTIMIZESPEED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_OPTIMIZESPEED_VALUE);

    /**
     * The 'painted' keyword.
     */
    ImmutableValue PAINTED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_PAINTED_VALUE);

    /**
     * The 'reset' keyword.
     */
    ImmutableValue RESET_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_RESET_VALUE);

    /**
     * The 'rl' keyword.
     */
    ImmutableValue RL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_RL_VALUE);

    /**
     * The 'rl-tb' keyword.
     */
    ImmutableValue RL_TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_RL_TB_VALUE);

    /**
     * The 'round' keyword.
     */
    ImmutableValue ROUND_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_ROUND_VALUE);

    /**
     * The 'square' keyword.
     */
    ImmutableValue SQUARE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SQUARE_VALUE);

    /**
     * The 'sRGB' keyword.
     */
    ImmutableValue SRGB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SRGB_VALUE);

    /**
     * The 'start' keyword.
     */
    ImmutableValue START_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_START_VALUE);

    /**
     * The 'stroke' keyword.
     */
    ImmutableValue STROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_STROKE_VALUE);

    /**
     * The 'sub' keyword.
     */
    ImmutableValue SUB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SUB_VALUE);

    /**
     * The 'super' keyword.
     */
    ImmutableValue SUPER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_SUPER_VALUE);

    /**
     * The 'tb' keyword.
     */
    ImmutableValue TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TB_VALUE);

    /**
     * The 'tb-rl' keyword.
     */
    ImmutableValue TB_RL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TB_RL_VALUE);

    /**
     * The 'text-after-edge' keyword.
     */
    ImmutableValue TEXT_AFTER_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TEXT_AFTER_EDGE_VALUE);

    /**
     * The 'text-before-edge' keyword.
     */
    ImmutableValue TEXT_BEFORE_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TEXT_BEFORE_EDGE_VALUE);

    /**
     * The 'text-bottom' keyword.
     */
    ImmutableValue TEXT_BOTTOM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TEXT_BOTTOM_VALUE);

    /**
     * The 'text-top' keyword.
     */
    ImmutableValue TEXT_TOP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TEXT_TOP_VALUE);

    /**
     * The 'top' keyword.
     */
    ImmutableValue TOP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_TOP_VALUE);

    /**
     * The 'visiblefill' keyword.
     */
    ImmutableValue VISIBLEFILL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_VISIBLEFILL_VALUE);

    /**
     * The 'visiblefillstroke' keyword.
     */
    ImmutableValue VISIBLEFILLSTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_VISIBLEFILLSTROKE_VALUE);

    /**
     * The 'visiblepainted' keyword.
     */
    ImmutableValue VISIBLEPAINTED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_VISIBLEPAINTED_VALUE);

    /**
     * The 'visiblestroke' keyword.
     */
    ImmutableValue VISIBLESTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT,
                            CSS_VISIBLESTROKE_VALUE);


}
