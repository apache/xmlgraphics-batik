/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.CommonValueFactoryMap;
import org.w3c.css.sac.Parser;

/**
 * This class represents a map of ValueFactory objects initialized
 * to contains factories for SVG CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGValueFactoryMap extends CommonValueFactoryMap {
    /**
     * Creates a new ValueFactoryMap object.
     */
    public SVGValueFactoryMap(Parser p) {
	super(p);

	put("alignment-baseline",           new AlignmentBaselineFactory(p));
	put("baseline-shift",               new BaselineShiftFactory(p));
	put("clip-path",                    new ClipPathFactory(p));
	put("clip-rule",                    new ClipRuleFactory(p));
	put("color",                        new SVGColorFactory(p, "color"));
	put("color-interpolation",          new ColorInterpolationFactory(p));
	put("color-rendering",              new ColorRenderingFactory(p));
	put("dominant-baseline",            new DominantBaselineFactory(p));
	put("enable-background",            new EnableBackgroundFactory(p));
	put("fill",
            new PaintFactory(p, "fill"));
	put("fill-opacity",
            new OpacityFactory(p, "fill-opacity"));
	put("fill-rule",                    new FillRuleFactory(p));
	put("filter",                       new FilterFactory(p));
	put("flood-color",
            new SimpleColorFactory(p, "flood-color"));
	put("flood-opacity",
            new OpacityFactory(p, "flood-opacity"));
	put("font-size",                    new SVGFontSizeFactory(p));
	put("glyph-orientation-horizontal",
            new GlyphOrientationHorizontalFactory(p));
	put("glyph-orientation-vertical",
            new GlyphOrientationVerticalFactory(p));
	put("image-rendering",              new ImageRenderingFactory(p));
	put("lighting-color",
            new SimpleColorFactory(p, "lighting-color"));
	put("marker-end",
            new MarkerFactory(p, "marker-end"));
	put("marker-mid",
            new MarkerFactory(p, "marker-mid"));
	put("marker-start",
            new MarkerFactory(p, "marker-start"));
	put("mask",                         new MaskFactory(p));
	put("opacity",                      new OpacityFactory(p, "opacity"));
	put("pointer-events",               new PointerEventsFactory(p));
	put("shape-rendering",              new ShapeRenderingFactory(p));
	put("stop-color",
            new SimpleColorFactory(p, "stop-color"));
	put("stop-opacity",
            new OpacityFactory(p, "stop-opacity"));
	put("stroke",                       new PaintFactory(p, "stroke"));
	put("stroke-dasharray",             new StrokeDasharrayFactory(p));
	put("stroke-dashoffset",            new StrokeDashoffsetFactory(p));
	put("stroke-linecap",               new StrokeLinecapFactory(p));
	put("stroke-linejoin",              new StrokeLinejoinFactory(p));
	put("stroke-miterlimit",            new StrokeMiterlimitFactory(p));
	put("stroke-opacity",
            new OpacityFactory(p, "stroke-opacity"));
	put("stroke-width",                 new StrokeWidthFactory(p));
	put("text-anchor",                  new TextAnchorFactory(p));
	put("text-rendering",               new TextRenderingFactory(p));
	put("writing-mode",                 new WritingModeFactory(p));
    }
}
