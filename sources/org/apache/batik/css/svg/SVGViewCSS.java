/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.CommonViewCSS;
import org.w3c.dom.views.DocumentView;

/**
 * This class represents a ViewCSS object initialized to manage
 * the SVG CSS.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class SVGViewCSS
    extends    CommonViewCSS
    implements SVGValueConstants {
    /**
     * Creates a new ViewCSS object.
     * @param doc The document view associated with this abstract view.
     * @param ctx The application context.
     */
    public SVGViewCSS(DocumentView doc, SVGCSSContext ctx) {
	super(doc, ctx);
	addRelativeValueResolver(new AlignmentBaselineResolver());
	addRelativeValueResolver(new BaselineShiftResolver());
	addRelativeValueResolver(new ClipPathResolver());
	addRelativeValueResolver(new ClipRuleResolver());
	addRelativeValueResolver(new ColorInterpolationResolver());
	addRelativeValueResolver(new ColorInterpolationFiltersResolver());
	addRelativeValueResolver(new ColorProfileResolver());
	addRelativeValueResolver(new ColorRenderingResolver());
	addRelativeValueResolver(new DominantBaselineResolver());
	addRelativeValueResolver(new EnableBackgroundResolver());
	addRelativeValueResolver(new FillResolver());
	addRelativeValueResolver(new FillRuleResolver());
	addRelativeValueResolver(new FilterResolver());
	addRelativeValueResolver(new OpacityResolver(CSS_FILL_OPACITY_PROPERTY, true));
	addRelativeValueResolver(new SimpleColorResolver(CSS_FLOOD_COLOR_PROPERTY));
	addRelativeValueResolver(new OpacityResolver(CSS_FLOOD_OPACITY_PROPERTY, false));
	addRelativeValueResolver(new GlyphOrientationHorizontalResolver());
	addRelativeValueResolver(new GlyphOrientationVerticalResolver());
	addRelativeValueResolver(new ImageRenderingResolver());
	addRelativeValueResolver(new LightingColorResolver());
	addRelativeValueResolver(new MarkerResolver(CSS_MARKER_END_PROPERTY));
	addRelativeValueResolver(new MarkerResolver(CSS_MARKER_MID_PROPERTY));
	addRelativeValueResolver(new MarkerResolver(CSS_MARKER_START_PROPERTY));
	addRelativeValueResolver(new MaskResolver());
	addRelativeValueResolver(new OpacityResolver(CSS_OPACITY_PROPERTY, false));
	addRelativeValueResolver(new PointerEventsResolver());
	addRelativeValueResolver(new ShapeRenderingResolver());
	addRelativeValueResolver(new SimpleColorResolver(CSS_STOP_COLOR_PROPERTY));
	addRelativeValueResolver(new OpacityResolver(CSS_STOP_OPACITY_PROPERTY, false));
	addRelativeValueResolver(new StrokeResolver());
	addRelativeValueResolver(new StrokeDasharrayResolver());
	addRelativeValueResolver(new StrokeDashoffsetResolver());
	addRelativeValueResolver(new StrokeLinecapResolver());
	addRelativeValueResolver(new StrokeLinejoinResolver());
	addRelativeValueResolver(new StrokeMiterlimitResolver());
	addRelativeValueResolver(new StrokeWidthResolver(ctx));
	addRelativeValueResolver(new OpacityResolver(CSS_STROKE_OPACITY_PROPERTY, true));
	addRelativeValueResolver(new TextAnchorResolver());
	addRelativeValueResolver(new TextRenderingResolver());
	addRelativeValueResolver(new WritingModeResolver());
    }
}
