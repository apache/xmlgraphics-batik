/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSOMValue;

import org.apache.batik.css.value.ColorFactory;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.SystemColorResolver;

import org.w3c.css.sac.Parser;

/**
 * This class provides a factory for values of color type.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGColorFactory extends ColorFactory {
    {
	factories.put("aliceblue",        new SimpleRGBColorFactory(240, 248, 255));
	factories.put("antiquewhite",     new SimpleRGBColorFactory(250, 235, 215));
	factories.put("aquamarine",       new SimpleRGBColorFactory(127, 255, 212));
	factories.put("azure",            new SimpleRGBColorFactory(240, 255, 255));
	factories.put("beige",            new SimpleRGBColorFactory(245, 245, 220));
	factories.put("bisque",           new SimpleRGBColorFactory(255, 228, 196));
	factories.put("blanchedalmond",   new SimpleRGBColorFactory(255, 235, 205));
	factories.put("blueviolet",       new SimpleRGBColorFactory(138,  43, 226));
	factories.put("brown",            new SimpleRGBColorFactory(165,  42,  42));
	factories.put("burlywood",        new SimpleRGBColorFactory(222, 184, 135));
	factories.put("cadetblue",        new SimpleRGBColorFactory( 95, 158, 160));
	factories.put("chartreuse",       new SimpleRGBColorFactory(127, 255,   0));
	factories.put("chocolate",        new SimpleRGBColorFactory(210, 105,  30));
	factories.put("coral",            new SimpleRGBColorFactory(255, 127,  80));
	factories.put("cornflowerblue",   new SimpleRGBColorFactory(100, 149, 237));
	factories.put("cornsilk",         new SimpleRGBColorFactory(255, 248, 220));
	factories.put("crimson",          new SimpleRGBColorFactory(220,  20,  60));
	factories.put("cyan",             new SimpleRGBColorFactory(  0, 255, 255));
	factories.put("darkblue",         new SimpleRGBColorFactory(  0,   0, 139));
	factories.put("darkcyan",         new SimpleRGBColorFactory(  0, 139, 139));
	factories.put("darkgoldenrod",    new SimpleRGBColorFactory(184, 134,  11));
	factories.put("darkgray",         new SimpleRGBColorFactory(169, 169, 169));
	factories.put("darkgreen",        new SimpleRGBColorFactory(  0, 100,   0));
	factories.put("darkgrey",         new SimpleRGBColorFactory(169, 169, 169));
	factories.put("darkkhaki",        new SimpleRGBColorFactory(189, 183, 107));
	factories.put("darkmagenta",      new SimpleRGBColorFactory(139,   0, 139));
	factories.put("darkolivegreen",   new SimpleRGBColorFactory( 85, 107,  47));
	factories.put("darkorange",       new SimpleRGBColorFactory(255, 140,   0));
	factories.put("darkorchid",       new SimpleRGBColorFactory(153,  50, 204));
	factories.put("darkred",          new SimpleRGBColorFactory(139,   0,   0));
	factories.put("darksalmon",       new SimpleRGBColorFactory(233, 150, 122));
	factories.put("darkseagreen",     new SimpleRGBColorFactory(143, 188, 143));
	factories.put("darkslateblue",    new SimpleRGBColorFactory( 72,  61, 139));
	factories.put("darkslategray",    new SimpleRGBColorFactory( 47,  79,  79));
	factories.put("darkslategrey",    new SimpleRGBColorFactory( 47,  79,  79));
	factories.put("darkturquoise",    new SimpleRGBColorFactory(  0, 206, 209));
	factories.put("darkviolet",       new SimpleRGBColorFactory(148,   0, 211));
	factories.put("deeppink",         new SimpleRGBColorFactory(255,  20, 147));
	factories.put("deepskyblue",      new SimpleRGBColorFactory(  0, 191, 255));
	factories.put("dimgray",          new SimpleRGBColorFactory(105, 105, 105));
	factories.put("dimgrey",          new SimpleRGBColorFactory(105, 105, 105));
	factories.put("dodgerblue",       new SimpleRGBColorFactory( 30, 144, 255));
	factories.put("firebrick",        new SimpleRGBColorFactory(178,  34,  34));
	factories.put("floralwhite",      new SimpleRGBColorFactory(255, 250, 240));
	factories.put("forestgreen",      new SimpleRGBColorFactory( 34, 139,  34));
	factories.put("gainsboro",        new SimpleRGBColorFactory(220, 200, 200));
	factories.put("ghostwhite",       new SimpleRGBColorFactory(248, 248, 255));
	factories.put("gold",             new SimpleRGBColorFactory(255, 215,   0));
	factories.put("goldenrod",        new SimpleRGBColorFactory(218, 165,  32));
	factories.put("grey",             new SimpleRGBColorFactory(128, 128, 128));
	factories.put("greenyellow",      new SimpleRGBColorFactory(173, 255,  47));
	factories.put("honeydew",         new SimpleRGBColorFactory(240, 255, 240));
	factories.put("hotpink",          new SimpleRGBColorFactory(255, 105, 180));
	factories.put("indianred",        new SimpleRGBColorFactory(205,  92,  92));
	factories.put("indigo",           new SimpleRGBColorFactory( 75,   0, 130));
	factories.put("ivory",            new SimpleRGBColorFactory(255, 255, 240));
	factories.put("khaki",            new SimpleRGBColorFactory(240, 230, 140));
	factories.put("lavender",         new SimpleRGBColorFactory(230, 230, 250));
	factories.put("lavenderblush",    new SimpleRGBColorFactory(255, 240, 255));
	factories.put("lawngreen",        new SimpleRGBColorFactory(124, 252,   0));
	factories.put("lemonchiffon",     new SimpleRGBColorFactory(255, 250, 205));
	factories.put("lightblue",        new SimpleRGBColorFactory(173, 216, 230));
	factories.put("lightcoral",       new SimpleRGBColorFactory(240, 128, 128));
	factories.put("lightcyan",        new SimpleRGBColorFactory(224, 255, 255));
	factories.put("lightgoldenrodyellow",
                      new SimpleRGBColorFactory(250, 250, 210));
	factories.put("lightgray",        new SimpleRGBColorFactory(211, 211, 211));
	factories.put("lightgreen",       new SimpleRGBColorFactory(144, 238, 144));
	factories.put("lightgrey",        new SimpleRGBColorFactory(211, 211, 211));
	factories.put("lightpink",        new SimpleRGBColorFactory(255, 182, 193));
	factories.put("lightsalmon",      new SimpleRGBColorFactory(255, 160, 122));
	factories.put("lightseagreen",    new SimpleRGBColorFactory( 32, 178, 170));
	factories.put("lightskyblue",     new SimpleRGBColorFactory(135, 206, 250));
	factories.put("lightslategray",   new SimpleRGBColorFactory(119, 136, 153));
	factories.put("lightslategrey",   new SimpleRGBColorFactory(119, 136, 153));
	factories.put("lightsteelblue",   new SimpleRGBColorFactory(176, 196, 222));
	factories.put("lightyellow",      new SimpleRGBColorFactory(255, 255, 224));
	factories.put("limegreen",        new SimpleRGBColorFactory( 50, 205,  50));
	factories.put("linen",            new SimpleRGBColorFactory(250, 240, 230));
	factories.put("magenta",          new SimpleRGBColorFactory(255,   0, 255));
	factories.put("mediumaquamarine", new SimpleRGBColorFactory(102, 205, 170));
	factories.put("mediumblue",       new SimpleRGBColorFactory(  0,   0, 205));
	factories.put("mediumorchid",     new SimpleRGBColorFactory(186,  85, 211));
	factories.put("mediumpurple",     new SimpleRGBColorFactory(147, 112, 219));
	factories.put("mediumseagreen",   new SimpleRGBColorFactory( 60, 179, 113));
	factories.put("mediumslateblue",  new SimpleRGBColorFactory(123, 104, 238));
	factories.put("mediumspringgreen",new SimpleRGBColorFactory(  0, 250, 154));
	factories.put("mediumturquoise",  new SimpleRGBColorFactory( 72, 209, 204));
	factories.put("mediumvioletred",  new SimpleRGBColorFactory(199,  21, 133));
	factories.put("midnightblue",     new SimpleRGBColorFactory( 25,  25, 112));
	factories.put("mintcream",        new SimpleRGBColorFactory(245, 255, 250));
	factories.put("mistyrose",        new SimpleRGBColorFactory(255, 228, 225));
	factories.put("moccasin",         new SimpleRGBColorFactory(255, 228, 181));
	factories.put("navajowhite",      new SimpleRGBColorFactory(255, 222, 173));
	factories.put("oldlace",          new SimpleRGBColorFactory(253, 245, 230));
	factories.put("olivedrab",        new SimpleRGBColorFactory(107, 142,  35));
	factories.put("orange",           new SimpleRGBColorFactory(255, 165,   0));
	factories.put("orangered",        new SimpleRGBColorFactory(255,  69,   0));
	factories.put("orchid",           new SimpleRGBColorFactory(218, 112, 214));
	factories.put("palegoldenrod",    new SimpleRGBColorFactory(238, 232, 170));
	factories.put("palegreen",        new SimpleRGBColorFactory(152, 251, 152));
	factories.put("paleturquoise",    new SimpleRGBColorFactory(175, 238, 238));
	factories.put("palevioletred",    new SimpleRGBColorFactory(219, 112, 147));
	factories.put("papayawhip",       new SimpleRGBColorFactory(255, 239, 213));
	factories.put("peachpuff",        new SimpleRGBColorFactory(255, 218, 185));
	factories.put("peru",             new SimpleRGBColorFactory(205, 133,  63));
	factories.put("pink",             new SimpleRGBColorFactory(255, 192, 203));
	factories.put("plum",             new SimpleRGBColorFactory(221, 160, 221));
	factories.put("powderblue",       new SimpleRGBColorFactory(176, 224, 230));
	factories.put("purple",           new SimpleRGBColorFactory(128,   0, 128));
	factories.put("rosybrown",        new SimpleRGBColorFactory(188, 143, 143));
	factories.put("royalblue",        new SimpleRGBColorFactory( 65, 105, 225));
	factories.put("saddlebrown",      new SimpleRGBColorFactory(139,  69,  19));
	factories.put("salmon",           new SimpleRGBColorFactory(250,  69, 114));
	factories.put("sandybrown",       new SimpleRGBColorFactory(244, 164,  96));
	factories.put("seagreen",         new SimpleRGBColorFactory( 46, 139,  87));
	factories.put("seashell",         new SimpleRGBColorFactory(255, 245, 238));
	factories.put("sienna",           new SimpleRGBColorFactory(160,  82,  45));
	factories.put("skyblue",          new SimpleRGBColorFactory(135, 206, 235));
	factories.put("slateblue",        new SimpleRGBColorFactory(106,  90, 205));
	factories.put("slategray",        new SimpleRGBColorFactory(112, 128, 144));
	factories.put("slategrey",        new SimpleRGBColorFactory(112, 128, 144));
	factories.put("snow",             new SimpleRGBColorFactory(255, 250, 250));
	factories.put("springgreen",      new SimpleRGBColorFactory(  0, 255, 127));
	factories.put("steelblue",        new SimpleRGBColorFactory( 70, 130, 180));
	factories.put("tan",              new SimpleRGBColorFactory(210, 180, 140));
	factories.put("thistle",          new SimpleRGBColorFactory(216,  91, 216));
	factories.put("tomato",           new SimpleRGBColorFactory(255,  99,  71));
	factories.put("turquoise",        new SimpleRGBColorFactory( 64, 224, 208));
	factories.put("violet",           new SimpleRGBColorFactory(238, 130, 238));
	factories.put("wheat",            new SimpleRGBColorFactory(245, 222, 179));
	factories.put("whitesmoke",       new SimpleRGBColorFactory(245, 245, 245));
	factories.put("yellowgreen",      new SimpleRGBColorFactory(154, 205,  50));
    }

    /**
     * Creates a new SVGColorFactory object.
     */
    public SVGColorFactory(Parser p, String prop, SystemColorResolver scr) {
	super(p, prop, scr);
    }

    /**
     * Creates a new CSSValue.
     */
    protected CSSOMValue createCSSValue(ImmutableValue v) {
        return new SVGCSSValue(this, v);
    }

}
