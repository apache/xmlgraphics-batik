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
     * The identifier factories.
     */
    protected final PropertyMap factories = new PropertyMap();
    {
	factories.put("black",   new SimpleRGBColorFactory(  0,   0,   0));
	factories.put("silver",  new SimpleRGBColorFactory(192, 192, 192));
	factories.put("gray",    new SimpleRGBColorFactory(128, 128, 128));
	factories.put("white",   new SimpleRGBColorFactory(255, 255, 255));
	factories.put("maroon",  new SimpleRGBColorFactory(128,   0,   0));
	factories.put("red",     new SimpleRGBColorFactory(255,   0,   0));
	factories.put("purple",  new SimpleRGBColorFactory(128,   0, 128));
	factories.put("fuchsia", new SimpleRGBColorFactory(255,   0, 255));
	factories.put("green",   new SimpleRGBColorFactory(  0, 128,   0));
	factories.put("lime",    new SimpleRGBColorFactory(  0, 255,   0));
	factories.put("olive",   new SimpleRGBColorFactory(128, 128,   0));
	factories.put("yellow",  new SimpleRGBColorFactory(255, 255,   0));
	factories.put("navy",    new SimpleRGBColorFactory(  0,   0, 128));
	factories.put("blue",    new SimpleRGBColorFactory(  0,   0, 255));
	factories.put("teal",    new SimpleRGBColorFactory(  0, 128, 128));
	factories.put("aqua",    new SimpleRGBColorFactory(  0, 255, 255));
    }

    /**
     * Creates a new ColorFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     * @param prop The handled property name.
     * @param scr The resolver for system colors.
     */
    public ColorFactory(Parser p, String prop, SystemColorResolver scr) {
	super(p);
	property = prop;

        factories.put(CSS_ACTIVEBORDER_VALUE,
                      new SystemRGBColorFactory(scr.activeBorder()));

        factories.put(CSS_ACTIVECAPTION_VALUE,
                      new SystemRGBColorFactory(scr.activeCaption()));

        factories.put(CSS_APPWORKSPACE_VALUE,
                      new SystemRGBColorFactory(scr.appWorkspace()));

        factories.put(CSS_BACKGROUND_VALUE,
                      new SystemRGBColorFactory(scr.background()));

        factories.put(CSS_BUTTONFACE_VALUE,
                      new SystemRGBColorFactory(scr.buttonFace()));

        factories.put(CSS_BUTTONHIGHLIGHT_VALUE,
                      new SystemRGBColorFactory(scr.buttonHighlight()));

        factories.put(CSS_BUTTONSHADOW_VALUE,
                      new SystemRGBColorFactory(scr.buttonShadow()));

        factories.put(CSS_BUTTONTEXT_VALUE,
                      new SystemRGBColorFactory(scr.buttonText()));

        factories.put(CSS_CAPTIONTEXT_VALUE,
                      new SystemRGBColorFactory(scr.captionText()));

        factories.put(CSS_GRAYTEXT_VALUE,
                      new SystemRGBColorFactory(scr.grayText()));

        factories.put(CSS_HIGHLIGHT_VALUE,
                      new SystemRGBColorFactory(scr.highlight()));

        factories.put(CSS_HIGHLIGHTTEXT_VALUE,
                      new SystemRGBColorFactory(scr.highlightText()));

        factories.put(CSS_INACTIVEBORDER_VALUE,
                      new SystemRGBColorFactory(scr.inactiveBorder()));

        factories.put(CSS_INACTIVECAPTION_VALUE,
                      new SystemRGBColorFactory(scr.inactiveCaption()));

        factories.put(CSS_INACTIVECAPTIONTEXT_VALUE,
                      new SystemRGBColorFactory(scr.inactiveCaptionText()));

        factories.put(CSS_INFOBACKGROUND_VALUE,
                      new SystemRGBColorFactory(scr.infoBackground()));

        factories.put(CSS_INFOTEXT_VALUE,
                      new SystemRGBColorFactory(scr.infoText()));

        factories.put(CSS_MENU_VALUE,
                      new SystemRGBColorFactory(scr.menu()));

        factories.put(CSS_MENUTEXT_VALUE,
                      new SystemRGBColorFactory(scr.menuText()));

        factories.put(CSS_SCROLLBAR_VALUE,
                      new SystemRGBColorFactory(scr.scrollbar()));

        factories.put(CSS_THREEDDARKSHADOW_VALUE,
                      new SystemRGBColorFactory(scr.threeDDarkShadow()));

        factories.put(CSS_THREEDFACE_VALUE,
                      new SystemRGBColorFactory(scr.threeDFace()));

        factories.put(CSS_THREEDHIGHLIGHT_VALUE,
                      new SystemRGBColorFactory(scr.threeDHighlight()));

        factories.put(CSS_THREEDLIGHTSHADOW_VALUE,
                      new SystemRGBColorFactory(scr.threeDLightShadow()));

        factories.put(CSS_THREEDSHADOW_VALUE,
                      new SystemRGBColorFactory(scr.threeDShadow()));

        factories.put(CSS_WINDOW_VALUE,
                      new SystemRGBColorFactory(scr.window()));

        factories.put(CSS_WINDOWFRAME_VALUE,
                      new SystemRGBColorFactory(scr.windowFrame()));

        factories.put(CSS_WINDOWTEXT_VALUE,
                      new SystemRGBColorFactory(scr.windowText()));
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
        throw CSSDOMExceptionFactory.createDOMException
            (DOMException.INVALID_ACCESS_ERR,
             "invalid.identifier",
             new Object[] { value });
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
    protected interface RGBColorFactory {

        /**
         * Creates the color.
         */
        ImmutableValue create();
    }

    /**
     * The simple rgb color factory.
     */
    protected class SimpleRGBColorFactory implements RGBColorFactory {

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
	public SimpleRGBColorFactory(float r, float g, float b) {
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

    /**
     * The simple rgb color factory.
     */
    protected class SystemRGBColorFactory implements RGBColorFactory {

        /**
         * The system color.
         */
        protected SystemColorResolver.Color color;

	/**
	 * Creates a new factory.
	 */
	public SystemRGBColorFactory(SystemColorResolver.Color c) {
            color = c;
	}

        /**
         * Creates the color.
         */
        public ImmutableValue create() {
            return createImmutableRGBColor(color.getRed(),
                                           color.getGreen(),
                                           color.getBlue());
        }
    }
}
