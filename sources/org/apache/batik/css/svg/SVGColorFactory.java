/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.ColorFactory;
import org.w3c.css.sac.Parser;

/**
 * This class provides a factory for values of color type.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGColorFactory extends ColorFactory {
    {
	factories.put("aliceblue",        new RGBColorFactory(240, 248, 255));
	factories.put("antiquewhite",     new RGBColorFactory(250, 235, 215));
	factories.put("aquamarine",       new RGBColorFactory(127, 255, 212));
	factories.put("azure",            new RGBColorFactory(240, 255, 255));
	factories.put("beige",            new RGBColorFactory(245, 245, 220));
	factories.put("bisque",           new RGBColorFactory(255, 228, 196));
	factories.put("blanchedalmond",   new RGBColorFactory(255, 235, 205));
	factories.put("blueviolet",       new RGBColorFactory(138,  43, 226));
	factories.put("brown",            new RGBColorFactory(165,  42,  42));
	factories.put("burlywood",        new RGBColorFactory(222, 184, 135));
	factories.put("cadetblue",        new RGBColorFactory( 95, 158, 160));
	factories.put("chartreuse",       new RGBColorFactory(127, 255,   0));
	factories.put("chocolate",        new RGBColorFactory(210, 105,  30));
	factories.put("coral",            new RGBColorFactory(255, 127,  80));
	factories.put("cornflowerblue",   new RGBColorFactory(100, 149, 237));
	factories.put("cornsilk",         new RGBColorFactory(255, 248, 220));
	factories.put("crimson",          new RGBColorFactory(220,  20,  60));
	factories.put("cyan",             new RGBColorFactory(  0, 255, 255));
	factories.put("darkblue",         new RGBColorFactory(  0,   0, 139));
	factories.put("darkcyan",         new RGBColorFactory(  0, 139, 139));
	factories.put("darkgoldenrod",    new RGBColorFactory(184, 134,  11));
	factories.put("darkgray",         new RGBColorFactory(169, 169, 169));
	factories.put("darkgreen",        new RGBColorFactory(  0, 100,   0));
	factories.put("darkgrey",         new RGBColorFactory(169, 169, 169));
	factories.put("darkkhaki",        new RGBColorFactory(189, 183, 107));
	factories.put("darkmagenta",      new RGBColorFactory(139,   0, 139));
	factories.put("darkolivegreen",   new RGBColorFactory( 85, 107,  47));
	factories.put("darkorange",       new RGBColorFactory(255, 140,   0));
	factories.put("darkorchid",       new RGBColorFactory(153,  50, 204));
	factories.put("darkred",          new RGBColorFactory(139,   0,   0));
	factories.put("darksalmon",       new RGBColorFactory(233, 150, 122));
	factories.put("darkseagreen",     new RGBColorFactory(143, 188, 143));
	factories.put("darkslateblue",    new RGBColorFactory( 72,  61, 139));
	factories.put("darkslategray",    new RGBColorFactory( 47,  79,  79));
	factories.put("darkslategrey",    new RGBColorFactory( 47,  79,  79));
	factories.put("darkturquoise",    new RGBColorFactory(  0, 206, 209));
	factories.put("darkviolet",       new RGBColorFactory(148,   0, 211));
	factories.put("deeppink",         new RGBColorFactory(255,  20, 147));
	factories.put("deepskyblue",      new RGBColorFactory(  0, 191, 255));
	factories.put("dimgray",          new RGBColorFactory(105, 105, 105));
	factories.put("dimgrey",          new RGBColorFactory(105, 105, 105));
	factories.put("dodgerblue",       new RGBColorFactory( 30, 144, 255));
	factories.put("firebrick",        new RGBColorFactory(178,  34,  34));
	factories.put("floralwhite",      new RGBColorFactory(255, 250, 240));
	factories.put("forestgreen",      new RGBColorFactory( 34, 139,  34));
	factories.put("gainsboro",        new RGBColorFactory(220, 200, 200));
	factories.put("ghostwhite",       new RGBColorFactory(248, 248, 255));
	factories.put("gold",             new RGBColorFactory(255, 215,   0));
	factories.put("goldenrod",        new RGBColorFactory(218, 165,  32));
	factories.put("grey",             new RGBColorFactory(128, 128, 128));
	factories.put("greenyellow",      new RGBColorFactory(173, 255,  47));
	factories.put("honeydew",         new RGBColorFactory(240, 255, 240));
	factories.put("hotpink",          new RGBColorFactory(255, 105, 180));
	factories.put("indianred",        new RGBColorFactory(205,  92,  92));
	factories.put("indigo",           new RGBColorFactory( 75,   0, 130));
	factories.put("ivory",            new RGBColorFactory(255, 255, 240));
	factories.put("khaki",            new RGBColorFactory(240, 230, 140));
	factories.put("lavender",         new RGBColorFactory(230, 230, 250));
	factories.put("lavenderblush",    new RGBColorFactory(255, 240, 255));
	factories.put("lawngreen",        new RGBColorFactory(124, 252,   0));
	factories.put("lemonchiffon",     new RGBColorFactory(255, 250, 205));
	factories.put("lightblue",        new RGBColorFactory(173, 216, 230));
	factories.put("lightcoral",       new RGBColorFactory(240, 128, 128));
	factories.put("lightcyan",        new RGBColorFactory(224, 255, 255));
	factories.put("lightgoldenrodyellow",
                      new RGBColorFactory(250, 250, 210));
	factories.put("lightgray",        new RGBColorFactory(211, 211, 211));
	factories.put("lightgreen",       new RGBColorFactory(144, 238, 144));
	factories.put("lightgrey",        new RGBColorFactory(211, 211, 211));
	factories.put("lightpink",        new RGBColorFactory(255, 182, 193));
	factories.put("lightsalmon",      new RGBColorFactory(255, 160, 122));
	factories.put("lightseagreen",    new RGBColorFactory( 32, 178, 170));
	factories.put("lightskyblue",     new RGBColorFactory(135, 206, 250));
	factories.put("lightslategray",   new RGBColorFactory(119, 136, 153));
	factories.put("lightslategrey",   new RGBColorFactory(119, 136, 153));
	factories.put("lightsteelblue",   new RGBColorFactory(176, 196, 222));
	factories.put("lightyellow",      new RGBColorFactory(255, 255, 224));
	factories.put("limegreen",        new RGBColorFactory( 50, 205,  50));
	factories.put("linen",            new RGBColorFactory(250, 240, 230));
	factories.put("magenta",          new RGBColorFactory(255,   0, 255));
	factories.put("mediumaquamarine", new RGBColorFactory(102, 205, 170));
	factories.put("mediumblue",       new RGBColorFactory(  0,   0, 205));
	factories.put("mediumorchid",     new RGBColorFactory(186,  85, 211));
	factories.put("mediumpurple",     new RGBColorFactory(147, 112, 219));
	factories.put("mediumseagreen",   new RGBColorFactory( 60, 179, 113));
	factories.put("mediumslateblue",  new RGBColorFactory(123, 104, 238));
	factories.put("mediumspringgreen",new RGBColorFactory(  0, 250, 154));
	factories.put("mediumturquoise",  new RGBColorFactory( 72, 209, 204));
	factories.put("mediumvioletred",  new RGBColorFactory(199,  21, 133));
	factories.put("midnightblue",     new RGBColorFactory( 25,  25, 112));
	factories.put("mintcream",        new RGBColorFactory(245, 255, 250));
	factories.put("mistyrose",        new RGBColorFactory(255, 228, 225));
	factories.put("moccasin",         new RGBColorFactory(255, 228, 181));
	factories.put("navajowhite",      new RGBColorFactory(255, 222, 173));
	factories.put("oldlace",          new RGBColorFactory(253, 245, 230));
	factories.put("olivedrab",        new RGBColorFactory(107, 142,  35));
	factories.put("orange",           new RGBColorFactory(255, 165,   0));
	factories.put("orangered",        new RGBColorFactory(255,  69,   0));
	factories.put("orchid",           new RGBColorFactory(218, 112, 214));
	factories.put("palegoldenrod",    new RGBColorFactory(238, 232, 170));
	factories.put("palegreen",        new RGBColorFactory(152, 251, 152));
	factories.put("paleturquoise",    new RGBColorFactory(175, 238, 238));
	factories.put("palevioletred",    new RGBColorFactory(219, 112, 147));
	factories.put("papayawhip",       new RGBColorFactory(255, 239, 213));
	factories.put("peachpuff",        new RGBColorFactory(255, 218, 185));
	factories.put("peru",             new RGBColorFactory(205, 133,  63));
	factories.put("pink",             new RGBColorFactory(255, 192, 203));
	factories.put("plum",             new RGBColorFactory(221, 160, 221));
	factories.put("powderblue",       new RGBColorFactory(176, 224, 230));
	factories.put("purple",           new RGBColorFactory(128,   0, 128));
	factories.put("rosybrown",        new RGBColorFactory(188, 143, 143));
	factories.put("royalblue",        new RGBColorFactory( 65, 105, 225));
	factories.put("saddlebrown",      new RGBColorFactory(139,  69,  19));
	factories.put("salmon",           new RGBColorFactory(250,  69, 114));
	factories.put("sandybrown",       new RGBColorFactory(244, 164,  96));
	factories.put("seagreen",         new RGBColorFactory( 46, 139,  87));
	factories.put("seashell",         new RGBColorFactory(255, 245, 238));
	factories.put("sienna",           new RGBColorFactory(160,  82,  45));
	factories.put("skyblue",          new RGBColorFactory(135, 206, 235));
	factories.put("slateblue",        new RGBColorFactory(106,  90, 205));
	factories.put("slategray",        new RGBColorFactory(112, 128, 144));
	factories.put("slategrey",        new RGBColorFactory(112, 128, 144));
	factories.put("snow",             new RGBColorFactory(255, 250, 250));
	factories.put("springgreen",      new RGBColorFactory(  0, 255, 127));
	factories.put("steelblue",        new RGBColorFactory( 70, 130, 180));
	factories.put("tan",              new RGBColorFactory(210, 180, 140));
	factories.put("thistle",          new RGBColorFactory(216,  91, 216));
	factories.put("tomato",           new RGBColorFactory(255,  99,  71));
	factories.put("turquoise",        new RGBColorFactory( 64, 224, 208));
	factories.put("violet",           new RGBColorFactory(238, 130, 238));
	factories.put("wheat",            new RGBColorFactory(245, 222, 179));
	factories.put("whitesmoke",       new RGBColorFactory(245, 245, 245));
	factories.put("yellowgreen",      new RGBColorFactory(154, 205,  50));
    }

    /**
     * Creates a new SVGColorFactory object.
     */
    public SVGColorFactory(Parser p, String prop) {
	super(p, prop);
    }
}
