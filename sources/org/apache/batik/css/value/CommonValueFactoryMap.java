/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.Parser;

/**
 * This class represents a map of ValueFactory objects initialized
 * to contains factories for CSS values common to CSS2 and SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CommonValueFactoryMap implements ValueFactoryMap, ValueConstants {
    /**
     * The implementation of the map.
     */
    protected PropertyMap table = new PropertyMap();

    /**
     * Creates a new ValueFactoryMap object.
     */
    public CommonValueFactoryMap(Parser p) {
	put(CSS_CLIP_PROPERTY,               new ClipFactory(p));
	put(CSS_COLOR_PROPERTY,              new ColorFactory(p, CSS_COLOR_PROPERTY));
	put(CSS_CURSOR_PROPERTY,             new CursorFactory(p));
	put(CSS_DIRECTION_PROPERTY,          new DirectionFactory(p));
	put(CSS_DISPLAY_PROPERTY,            new DisplayFactory(p));
	put(CSS_FONT_FAMILY_PROPERTY,        new FontFamilyFactory(p));
	put(CSS_FONT_SIZE_PROPERTY,          new FontSizeFactory(p));
	put(CSS_FONT_SIZE_ADJUST_PROPERTY,   new FontSizeAdjustFactory(p));
	put(CSS_FONT_STRETCH_PROPERTY,       new FontStretchFactory(p));
	put(CSS_FONT_STYLE_PROPERTY,         new FontStyleFactory(p));
	put(CSS_FONT_VARIANT_PROPERTY,       new FontVariantFactory(p));
	put(CSS_FONT_WEIGHT_PROPERTY,        new FontWeightFactory(p));
	put(CSS_LETTER_SPACING_PROPERTY,     new SpacingFactory(p,
                                                       CSS_LETTER_SPACING_PROPERTY));
	put(CSS_OVERFLOW_PROPERTY,           new OverflowFactory(p));
	put(CSS_TEXT_DECORATION_PROPERTY,    new TextDecorationFactory(p));
	put(CSS_UNICODE_BIDI_PROPERTY,       new UnicodeBidiFactory(p));
	put(CSS_VISIBILITY_PROPERTY,         new VisibilityFactory(p));
	put(CSS_WORD_SPACING_PROPERTY,       new SpacingFactory(p,
                                                         CSS_WORD_SPACING_PROPERTY));
    }

    /**
     * Returns the PropertyHandler object associated with the given property.
     */
    public ValueFactory get(String property) {
	return (ValueFactory)table.get(property.toLowerCase().intern());
    }
    
    /**
     * Associates the given the ValueFactory with the given property.
     */
    public void put(String property, ValueFactory factory) {
	table.put(property.toLowerCase().intern(), factory);
    }
}
