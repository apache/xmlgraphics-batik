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
public class CommonValueFactoryMap implements ValueFactoryMap {
    /**
     * The implementation of the map.
     */
    protected PropertyMap table = new PropertyMap();

    /**
     * Creates a new ValueFactoryMap object.
     */
    public CommonValueFactoryMap(Parser p) {
	put("clip",                  new ClipFactory(p));
	put("color",                 new ColorFactory(p, "color"));
	put("cursor",                new CursorFactory(p));
	put("direction",             new DirectionFactory(p));
	put("display",               new DisplayFactory(p));
	put("font-family",           new FontFamilyFactory(p));
	put("font-size",             new FontSizeFactory(p));
	put("font-size-adjust",      new FontSizeAdjustFactory(p));
	put("font-stretch",          new FontStretchFactory(p));
	put("font-style",            new FontStyleFactory(p));
	put("font-variant",          new FontVariantFactory(p));
	put("font-weight",           new FontWeightFactory(p));
	put("letter-spacing",        new SpacingFactory(p, "letter-spacing"));
	put("overflow",              new OverflowFactory(p));
	put("text-decoration",       new TextDecorationFactory(p));
	put("unicode-bidi",          new UnicodeBidiFactory(p));
	put("visibility",            new VisibilityFactory(p));
	put("word-spacing",          new SpacingFactory(p, "word-spacing"));
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
