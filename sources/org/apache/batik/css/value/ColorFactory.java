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
public class ColorFactory extends AbstractRGBColorFactory {
    /**
     * The 'activeborder' string.
     */
    public final static String ACTIVEBORDER = "activeborder";

    /**
     * The 'activeborder' identifier value.
     */
    public final static ImmutableValue ACTIVEBORDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ACTIVEBORDER);

    /**
     * The 'activecaption' string.
     */
    public final static String ACTIVECAPTION = "activecaption";

    /**
     * The 'activecaption' identifier value.
     */
    public final static ImmutableValue ACTIVECAPTION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ACTIVECAPTION);

    /**
     * The 'appworkspace' string.
     */
    public final static String APPWORKSPACE = "appworkspace";

    /**
     * The 'appworkspace' identifier value.
     */
    public final static ImmutableValue APPWORKSPACE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, APPWORKSPACE);

    /**
     * The 'background' string.
     */
    public final static String BACKGROUND = "background";

    /**
     * The 'background' identifier value.
     */
    public final static ImmutableValue BACKGROUND_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BACKGROUND);

    /**
     * The 'buttonface' string.
     */
    public final static String BUTTONFACE = "buttonface";

    /**
     * The 'buttonface' identifier value.
     */
    public final static ImmutableValue BUTTONFACE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BUTTONFACE);

    /**
     * The 'buttonhighlight' string.
     */
    public final static String BUTTONHIGHLIGHT = "buttonhighlight";

    /**
     * The 'buttonhighlight' identifier value.
     */
    public final static ImmutableValue BUTTONHIGHLIGHT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BUTTONHIGHLIGHT);

    /**
     * The 'buttonshadow' string.
     */
    public final static String BUTTONSHADOW = "buttonshadow";

    /**
     * The 'buttonshadow' identifier value.
     */
    public final static ImmutableValue BUTTONSHADOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BUTTONSHADOW);

    /**
     * The 'buttontext' string.
     */
    public final static String BUTTONTEXT = "buttontext";

    /**
     * The 'buttontext' identifier value.
     */
    public final static ImmutableValue BUTTONTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BUTTONTEXT);

    /**
     * The 'captiontext' string.
     */
    public final static String CAPTIONTEXT = "captiontext";

    /**
     * The 'captiontext' identifier value.
     */
    public final static ImmutableValue CAPTIONTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, CAPTIONTEXT);

    /**
     * The 'graytext' string.
     */
    public final static String GRAYTEXT = "graytext";

    /**
     * The 'graytext' identifier value.
     */
    public final static ImmutableValue GRAYTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, GRAYTEXT);

    /**
     * The 'highlight' string.
     */
    public final static String HIGHLIGHT = "highlight";

    /**
     * The 'highlight' identifier value.
     */
    public final static ImmutableValue HIGHLIGHT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, HIGHLIGHT);

    /**
     * The 'highlighttext' string.
     */
    public final static String HIGHLIGHTTEXT = "highlighttext";

    /**
     * The 'highlighttext' identifier value.
     */
    public final static ImmutableValue HIGHLIGHTTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, HIGHLIGHTTEXT);

    /**
     * The 'inactiveborder' string.
     */
    public final static String INACTIVEBORDER = "inactiveborder";

    /**
     * The 'inactiveborder' identifier value.
     */
    public final static ImmutableValue INACTIVEBORDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INACTIVEBORDER);

    /**
     * The 'inactivecaption' string.
     */
    public final static String INACTIVECAPTION = "inactivecaption";

    /**
     * The 'inactivecaption' identifier value.
     */
    public final static ImmutableValue INACTIVECAPTION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INACTIVECAPTION);

    /**
     * The 'inactivecaptiontext' string.
     */
    public final static String INACTIVECAPTIONTEXT = "inactivecaptiontext";

    /**
     * The 'inactivecaptiontext' identifier value.
     */
    public final static ImmutableValue INACTIVECAPTIONTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INACTIVECAPTIONTEXT);

    /**
     * The 'infobackground' string.
     */
    public final static String INFOBACKGROUND = "infobackground";

    /**
     * The 'infobackground' identifier value.
     */
    public final static ImmutableValue INFOBACKGROUND_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INFOBACKGROUND);

    /**
     * The 'infotext' string.
     */
    public final static String INFOTEXT = "infotext";

    /**
     * The 'infotext' identifier value.
     */
    public final static ImmutableValue INFOTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INFOTEXT);

    /**
     * The 'menu' string.
     */
    public final static String MENU = "menu";

    /**
     * The 'menu' identifier value.
     */
    public final static ImmutableValue MENU_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MENU);

    /**
     * The 'menutext' string.
     */
    public final static String MENUTEXT = "menutext";

    /**
     * The 'menutext' identifier value.
     */
    public final static ImmutableValue MENUTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MENUTEXT);

    /**
     * The 'scrollbar' string.
     */
    public final static String SCROLLBAR = "scrollbar";

    /**
     * The 'scrollbar' identifier value.
     */
    public final static ImmutableValue SCROLLBAR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, "scrollbar");

    /**
     * The 'threeddarkshadow' string.
     */
    public final static String THREEDDARKSHADOW = "threeddarkshadow";

    /**
     * The 'threeddarkshadow' identifier value.
     */
    public final static ImmutableValue THREEDDARKSHADOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, THREEDDARKSHADOW);

    /**
     * The 'threedface' string.
     */
    public final static String THREEDFACE = "threedface";

    /**
     * The 'threedface' identifier value.
     */
    public final static ImmutableValue THREEDFACE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, THREEDFACE);

    /**
     * The 'threedhighlight' string.
     */
    public final static String THREEDHIGHLIGHT = "threedhighlight";

    /**
     * The 'threedhighlight' identifier value.
     */
    public final static ImmutableValue THREEDHIGHLIGHT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, THREEDHIGHLIGHT);

    /**
     * The 'threedlightshadow' string.
     */
    public final static String THREEDLIGHTSHADOW = "threedlightshadow";

    /**
     * The 'threedlightshadow' identifier value.
     */
    public final static ImmutableValue THREEDLIGHTSHADOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, THREEDLIGHTSHADOW);

    /**
     * The 'threedshadow' string.
     */
    public final static String THREEDSHADOW = "threedshadow";

    /**
     * The 'threedshadow' identifier value.
     */
    public final static ImmutableValue THREEDSHADOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, THREEDSHADOW);

    /**
     * The 'window' string.
     */
    public final static String WINDOW = "window";

    /**
     * The 'window' identifier value.
     */
    public final static ImmutableValue WINDOW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, WINDOW);

    /**
     * The 'windowframe' string.
     */
    public final static String WINDOWFRAME = "windowframe";

    /**
     * The 'windowframe' identifier value.
     */
    public final static ImmutableValue WINDOWFRAME_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, WINDOWFRAME);

    /**
     * The 'windowtext' string.
     */
    public final static String WINDOWTEXT = "windowtext";

    /**
     * The 'windowtext' identifier value.
     */
    public final static ImmutableValue WINDOWTEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, WINDOWTEXT);

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
	values.put(ACTIVEBORDER,        ACTIVEBORDER_VALUE);
	values.put(ACTIVECAPTION,       ACTIVECAPTION_VALUE);
	values.put(APPWORKSPACE,        APPWORKSPACE_VALUE);
	values.put(BACKGROUND,          BACKGROUND_VALUE);
	values.put(BUTTONFACE,          BUTTONFACE_VALUE);
	values.put(BUTTONHIGHLIGHT,     BUTTONHIGHLIGHT_VALUE);
	values.put(BUTTONSHADOW,        BUTTONSHADOW_VALUE);
	values.put(BUTTONTEXT,          BUTTONTEXT_VALUE);
	values.put(CAPTIONTEXT,         CAPTIONTEXT_VALUE);
	values.put(GRAYTEXT,            GRAYTEXT_VALUE);
	values.put(HIGHLIGHT,           HIGHLIGHT_VALUE);
	values.put(HIGHLIGHTTEXT,       HIGHLIGHTTEXT_VALUE);
	values.put(INACTIVEBORDER,      INACTIVEBORDER_VALUE);
	values.put(INACTIVECAPTION,     INACTIVECAPTION_VALUE);
	values.put(INACTIVECAPTIONTEXT, INACTIVECAPTIONTEXT_VALUE);
	values.put(INFOBACKGROUND,      INFOBACKGROUND_VALUE);
	values.put(INFOTEXT,            INFOTEXT_VALUE);
	values.put(MENU,                MENU_VALUE);
	values.put(MENUTEXT,            MENUTEXT_VALUE);
	values.put(SCROLLBAR,           SCROLLBAR_VALUE);
	values.put(THREEDDARKSHADOW,    THREEDDARKSHADOW_VALUE);
	values.put(THREEDFACE,          THREEDFACE_VALUE);
	values.put(THREEDHIGHLIGHT,     THREEDHIGHLIGHT_VALUE);
	values.put(THREEDLIGHTSHADOW,   THREEDLIGHTSHADOW_VALUE);
	values.put(THREEDSHADOW,        THREEDSHADOW_VALUE);
	values.put(WINDOW,              WINDOW_VALUE);
	values.put(WINDOWFRAME,         WINDOWFRAME_VALUE);
	values.put(WINDOWTEXT,          WINDOWTEXT_VALUE);
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
