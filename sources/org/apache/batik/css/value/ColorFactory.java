/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * This class provides a factory for the 'color'-like CSS properties.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorFactory
    extends    AbstractRGBColorFactory
    implements ValueConstants {

    /**
     * The color component factory.
     */
    public final ValueFactory RGB_FACTORY =
	new ColorComponentFactory(getParser());

    /**
     * The property name.
     */
    public String property;

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_ACTIVEBORDER_VALUE,        ACTIVEBORDER_VALUE);
	values.put(CSS_ACTIVECAPTION_VALUE,       ACTIVECAPTION_VALUE);
	values.put(CSS_APPWORKSPACE_VALUE,        APPWORKSPACE_VALUE);
	values.put(CSS_BACKGROUND_VALUE,          BACKGROUND_VALUE);
	values.put(CSS_BUTTONFACE_VALUE,          BUTTONFACE_VALUE);
	values.put(CSS_BUTTONHIGHLIGHT_VALUE,     BUTTONHIGHLIGHT_VALUE);
	values.put(CSS_BUTTONSHADOW_VALUE,        BUTTONSHADOW_VALUE);
	values.put(CSS_BUTTONTEXT_VALUE,          BUTTONTEXT_VALUE);
	values.put(CSS_CAPTIONTEXT_VALUE,         CAPTIONTEXT_VALUE);
	values.put(CSS_GRAYTEXT_VALUE,            GRAYTEXT_VALUE);
	values.put(CSS_HIGHLIGHT_VALUE,           HIGHLIGHT_VALUE);
	values.put(CSS_HIGHLIGHTTEXT_VALUE,       HIGHLIGHTTEXT_VALUE);
	values.put(CSS_INACTIVEBORDER_VALUE,      INACTIVEBORDER_VALUE);
	values.put(CSS_INACTIVECAPTION_VALUE,     INACTIVECAPTION_VALUE);
	values.put(CSS_INACTIVECAPTIONTEXT_VALUE, INACTIVECAPTIONTEXT_VALUE);
	values.put(CSS_INFOBACKGROUND_VALUE,      INFOBACKGROUND_VALUE);
	values.put(CSS_INFOTEXT_VALUE,            INFOTEXT_VALUE);
	values.put(CSS_MENU_VALUE,                MENU_VALUE);
	values.put(CSS_MENUTEXT_VALUE,            MENUTEXT_VALUE);
	values.put(CSS_SCROLLBAR_VALUE,           SCROLLBAR_VALUE);
	values.put(CSS_THREEDDARKSHADOW_VALUE,    THREEDDARKSHADOW_VALUE);
	values.put(CSS_THREEDFACE_VALUE,          THREEDFACE_VALUE);
	values.put(CSS_THREEDHIGHLIGHT_VALUE,     THREEDHIGHLIGHT_VALUE);
	values.put(CSS_THREEDLIGHTSHADOW_VALUE,   THREEDLIGHTSHADOW_VALUE);
	values.put(CSS_THREEDSHADOW_VALUE,        THREEDSHADOW_VALUE);
	values.put(CSS_WINDOW_VALUE,              WINDOW_VALUE);
	values.put(CSS_WINDOWFRAME_VALUE,         WINDOWFRAME_VALUE);
	values.put(CSS_WINDOWTEXT_VALUE,          WINDOWTEXT_VALUE);
    }

    /**
     * The identifier factories.
     */
    protected final PropertyMap factories = new PropertyMap();
    {
	factories.put("black",   new RGBColorFactory(  0,   0,   0));
	factories.put("silver",  new RGBColorFactory(192, 192, 192));
	factories.put("gray",    new RGBColorFactory(128, 128, 128));
	factories.put("white",   new RGBColorFactory(255, 255, 255));
	factories.put("maroon",  new RGBColorFactory(128,   0,   0));
	factories.put("red",     new RGBColorFactory(255,   0,   0));
	factories.put("purple",  new RGBColorFactory(128,   0, 128));
	factories.put("fuchsia", new RGBColorFactory(255,   0, 255));
	factories.put("green",   new RGBColorFactory(  0, 128,   0));
	factories.put("lime",    new RGBColorFactory(  0, 255,   0));
	factories.put("olive",   new RGBColorFactory(128, 128,   0));
	factories.put("yellow",  new RGBColorFactory(255, 255,   0));
	factories.put("navy",    new RGBColorFactory(  0,   0, 128));
	factories.put("blue",    new RGBColorFactory(  0,   0, 255));
	factories.put("teal",    new RGBColorFactory(  0, 128, 128));
	factories.put("aqua",    new RGBColorFactory(  0, 255, 255));
    }

    /**
     * Creates a new ColorFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     * @param prop The handled property name.
     */
    public ColorFactory(Parser p, String prop) {
	super(p);
	property = prop;
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return property;
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
	    String s = lu.getStringValue().toLowerCase().intern();
	    Object v = factories.get(s);
	    if (v != null) {
		return ((RGBColorFactory)v).create();
	    }
	    v = values.get(s);
	    if (v != null) {
		return (ImmutableValue)v;
	    }
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { lu.getStringValue() });
	default:
	    return super.createValue(lu);
	}
    }

    /**
     * Creates and returns a new string value.
     * @param type  A string code as defined in CSSPrimitiveValue. The string
     *   code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	String s = value.toLowerCase().intern();
	Object v = factories.get(s);
	if (v != null) {
	    return ((RGBColorFactory)v).create();
	}
	v = values.get(s);
	if (v == null) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return (ImmutableValue)v;
    }

    /**
     * Creates an ImmutableRGBColor from rgb values.
     */
    protected ImmutableRGBColor createImmutableRGBColor(float r, float g,
                                                        float b) {
	ImmutableValue rv;
        rv = new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, r);
	ImmutableValue gv;
        gv = new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, g);
	ImmutableValue bv;
        bv = new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, b);
	return new ImmutableRGBColor(new CSSOMValue(RGB_FACTORY, rv),
				     new CSSOMValue(RGB_FACTORY, gv),
				     new CSSOMValue(RGB_FACTORY, bv));
    }

    /**
     * The rgb color factory.
     */
    protected class RGBColorFactory {
	/**
	 * The red component.
	 */
	protected float red;

	/**
	 * The green component.
	 */
	protected float green;

	/**
	 * The blue component.
	 */
	protected float blue;

	/**
	 * Creates a new factory.
	 */
	public RGBColorFactory(float r, float g, float b) {
	    red = r;
	    green = g;
	    blue = b;
	}

        /**
         * Creates the color.
         */
        public ImmutableValue create() {
            return createImmutableRGBColor(red, green, blue);
        }
    }
}
