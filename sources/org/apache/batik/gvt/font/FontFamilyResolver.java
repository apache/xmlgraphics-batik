/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.awt.GraphicsEnvironment;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;

/**
 * The is a utility class that is used for resolving UnresolvedFontFamilies.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class FontFamilyResolver {

    /**
     * The default font. This will be used when no font families can be resolved
     * for a particular text chunck/run.
     */
    public final static AWTFontFamily defaultFont = new AWTFontFamily("SansSerif");

    /**
     * List of all available fonts on the current system, plus a few common
     * alternatives.
     */
    protected final static Map fonts = new HashMap();

    protected final static Vector awtFontFamilies = new Vector();
    protected final static Vector awtFonts = new Vector();

    /**
     * This sets up the list of available fonts.
     */
    static {
        fonts.put("sans-serif",      "SansSerif");
        fonts.put("serif",           "Serif");
        fonts.put("times",           "Serif");
        fonts.put("times new roman", "Serif");
        fonts.put("cursive",         "Dialog");
        fonts.put("fantasy",         "Symbol");
        fonts.put("monospace",       "Monospaced");
        fonts.put("monospaced",      "Monospaced");
        fonts.put("courier",         "Monospaced");

        //
        // Load all fonts. Work around
        //

        GraphicsEnvironment env;
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontNames[] = env.getAvailableFontFamilyNames();
        int nFonts = fontNames != null ? fontNames.length : 0;
        for(int i=0; i<nFonts; i++){
            fonts.put(fontNames[i].toLowerCase(), fontNames[i]);

            // also add the font name with the spaces removed
            StringTokenizer st = new StringTokenizer(fontNames[i]);
            String fontNameWithoutSpaces = "";
            while (st.hasMoreTokens()) {
                fontNameWithoutSpaces += st.nextToken();
            }
            fonts.put(fontNameWithoutSpaces.toLowerCase(), fontNames[i]);

            // also add the font name with spaces replaced by dashes
            String fontNameWithDashes = fontNames[i].replace(' ', '-');
            if (!fontNameWithDashes.equals(fontNames[i])) {
               fonts.put(fontNameWithDashes.toLowerCase(), fontNames[i]);
            }
        }

        // first add the default font
        awtFontFamilies.add(defaultFont);
        awtFonts.add(new AWTGVTFont(defaultFont.getFamilyName(), 0, 12));

        Collection fontValues = fonts.values();
        Iterator iter = fontValues.iterator();
        while(iter.hasNext()) {
            String fontFamily = (String)iter.next();
            AWTFontFamily awtFontFamily = new AWTFontFamily(fontFamily);
            awtFontFamilies.add(awtFontFamily);
            AWTGVTFont font = new AWTGVTFont(fontFamily, 0, 12);
            awtFonts.add(font);
        }

    }

    /**
     * This keeps track of all the resolved font families. This is to hopefully
     * reduce the number of font family objects used.
     */
    protected static Map resolvedFontFamilies;


    /**
     * Resolves an UnresolvedFontFamily into a GVTFontFamily. If the font
     * family cannot be resolved then null will be returned.
     *
     * @param fontFamily The UnresolvedFontFamily to resolve
     * @param textAttributes The attributes of the font that will be derived
     * from the resolved font family.
     *
     * @return A resolved GVTFontFamily or null if the font family could not
     * be resolved.
     */
    public static GVTFontFamily resolve(UnresolvedFontFamily fontFamily) {

        if (resolvedFontFamilies == null) {
            resolvedFontFamilies = new HashMap();
        }

        // first see if this font family has already been resolved
        String familyName = fontFamily.getFamilyName();
        GVTFontFamily resolvedFontFamily = (GVTFontFamily)resolvedFontFamilies.get(familyName.toLowerCase());

        if (resolvedFontFamily == null) { // hasn't been resolved yet
            // try to find a matching family name in the list of available fonts
            String awtFamilyName = (String) fonts.get(familyName.toLowerCase());
            if (awtFamilyName != null) {
                resolvedFontFamily = new AWTFontFamily(awtFamilyName);
            }

            resolvedFontFamilies.put(familyName.toLowerCase(), resolvedFontFamily);
        }
      //  if (resolvedFontFamily != null) {
      //      System.out.println("resolved " + fontFamily.getFamilyName() + " to " + resolvedFontFamily.getFamilyName());
      //  } else {
      //      System.out.println("could not resolve " + fontFamily.getFamilyName());
      //  }
        return resolvedFontFamily;
    }

    public static GVTFontFamily getFamilyThatCanDisplay(char c) {
        for (int i = 0; i < awtFontFamilies.size(); i++) {
            AWTFontFamily fontFamily = (AWTFontFamily)awtFontFamilies.get(i);
            AWTGVTFont font = (AWTGVTFont)awtFonts.get(i);
            if (font.canDisplay(c) && fontFamily.getFamilyName().indexOf("Song") == -1) {
                // the awt font for "MS Song" doesn't display chinese glyphs correctly
                return fontFamily;
            }
        }

        return null;
    }

}
