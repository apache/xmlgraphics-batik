/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.DefaultSystemColorResolver;
import org.apache.batik.css.value.CommonValueFactoryMap;
import org.apache.batik.css.value.SystemColorResolver;

import org.w3c.css.sac.Parser;

/**
 * This class represents a map of ValueFactory objects initialized
 * to contains factories for SVG CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGValueFactoryMap
    extends    CommonValueFactoryMap
    implements SVGValueConstants {

    /**
     * Creates a new ValueFactoryMap object.
     */
    public SVGValueFactoryMap(Parser p) {
        this(p, new DefaultSystemColorResolver());
    }

    /**
     * Creates a new ValueFactoryMap object.
     */
    public SVGValueFactoryMap(Parser p, SystemColorResolver scr) {
	super(p, scr);

	put(CSS_ALIGNMENT_BASELINE_PROPERTY,
            new AlignmentBaselineFactory(p));

	put(CSS_BASELINE_SHIFT_PROPERTY,
            new BaselineShiftFactory(p));

	put(CSS_CLIP_PATH_PROPERTY,
            new ClipPathFactory(p));

	put(CSS_CLIP_RULE_PROPERTY,
            new ClipRuleFactory(p));

	put(CSS_COLOR_PROFILE_PROPERTY,
            new ColorProfileFactory(p));

	put(CSS_COLOR_PROPERTY,
            new SVGColorFactory(p, CSS_COLOR_PROPERTY, scr));

	put(CSS_COLOR_INTERPOLATION_PROPERTY,
            new ColorInterpolationFactory(p, CSS_COLOR_INTERPOLATION_PROPERTY));

	put(CSS_COLOR_INTERPOLATION_FILTERS_PROPERTY,
            new ColorInterpolationFactory(p, CSS_COLOR_INTERPOLATION_FILTERS_PROPERTY));

	put(CSS_COLOR_RENDERING_PROPERTY,
            new ColorRenderingFactory(p));

	put(CSS_DOMINANT_BASELINE_PROPERTY,
            new DominantBaselineFactory(p));

	put(CSS_ENABLE_BACKGROUND_PROPERTY,
            new EnableBackgroundFactory(p));

	put(CSS_FILL_PROPERTY,
            new PaintFactory(p, CSS_FILL_PROPERTY, scr));

	put(CSS_FILL_OPACITY_PROPERTY,
            new OpacityFactory(p, CSS_FILL_OPACITY_PROPERTY));

	put(CSS_FILL_RULE_PROPERTY,
            new FillRuleFactory(p));

	put(CSS_FILTER_PROPERTY,
            new FilterFactory(p));

	put(CSS_FLOOD_COLOR_PROPERTY,
            new SimpleColorFactory(p, CSS_FLOOD_COLOR_PROPERTY, scr));

	put(CSS_FLOOD_OPACITY_PROPERTY,
            new OpacityFactory(p, CSS_FLOOD_OPACITY_PROPERTY));

	put(CSS_FONT_SIZE_PROPERTY,
            new SVGFontSizeFactory(p));

	put(CSS_GLYPH_ORIENTATION_HORIZONTAL_PROPERTY,
            new GlyphOrientationHorizontalFactory(p));
        
	put(CSS_GLYPH_ORIENTATION_VERTICAL_PROPERTY,
            new GlyphOrientationVerticalFactory(p));

	put(CSS_IMAGE_RENDERING_PROPERTY,
            new ImageRenderingFactory(p));

	put(CSS_LIGHTING_COLOR_PROPERTY,
            new SimpleColorFactory(p, CSS_LIGHTING_COLOR_PROPERTY, scr));

	put(CSS_MARKER_PROPERTY,
            new MarkerShorthandFactory(p));

	put(CSS_MARKER_END_PROPERTY,
            new MarkerFactory(p, CSS_MARKER_END_PROPERTY));

	put(CSS_MARKER_MID_PROPERTY,
            new MarkerFactory(p, CSS_MARKER_MID_PROPERTY));

	put(CSS_MARKER_START_PROPERTY,
            new MarkerFactory(p, CSS_MARKER_START_PROPERTY));

	put(CSS_MASK_PROPERTY,
            new MaskFactory(p));

	put(CSS_OPACITY_PROPERTY,
            new OpacityFactory(p, CSS_OPACITY_PROPERTY));

	put(CSS_POINTER_EVENTS_PROPERTY,
            new PointerEventsFactory(p));

	put(CSS_SHAPE_RENDERING_PROPERTY,
            new ShapeRenderingFactory(p));

	put(CSS_STOP_COLOR_PROPERTY,
            new SimpleColorFactory(p, CSS_STOP_COLOR_PROPERTY, scr));

	put(CSS_STOP_OPACITY_PROPERTY,
            new OpacityFactory(p, CSS_STOP_OPACITY_PROPERTY));

	put(CSS_STROKE_PROPERTY,
            new PaintFactory(p, CSS_STROKE_PROPERTY, scr));

	put(CSS_STROKE_DASHARRAY_PROPERTY,
            new StrokeDasharrayFactory(p));

	put(CSS_STROKE_DASHOFFSET_PROPERTY,
            new StrokeDashoffsetFactory(p));

	put(CSS_STROKE_LINECAP_PROPERTY,
            new StrokeLinecapFactory(p));

	put(CSS_STROKE_LINEJOIN_PROPERTY,
            new StrokeLinejoinFactory(p));

	put(CSS_STROKE_MITERLIMIT_PROPERTY,
            new StrokeMiterlimitFactory(p));

	put(CSS_STROKE_OPACITY_PROPERTY,
            new OpacityFactory(p, CSS_STROKE_OPACITY_PROPERTY));

	put(CSS_STROKE_WIDTH_PROPERTY,
            new StrokeWidthFactory(p));

	put(CSS_TEXT_ANCHOR_PROPERTY,
            new TextAnchorFactory(p));

	put(CSS_TEXT_RENDERING_PROPERTY,
            new TextRenderingFactory(p));

	put(CSS_WRITING_MODE_PROPERTY,
            new WritingModeFactory(p));
    }
}
