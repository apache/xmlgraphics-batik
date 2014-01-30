/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.gvt.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The is a utility class that is used for resolving UnresolvedFontFamilies.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class DefaultFontFamilyResolver implements FontFamilyResolver {

    public static final DefaultFontFamilyResolver SINGLETON = new DefaultFontFamilyResolver();

    private DefaultFontFamilyResolver() {
    }

    /**
     * The default font. This will be used when no font families can
     * be resolved for a particular text chunk/run.
     */
    private static final AWTFontFamily DEFAULT_FONT_FAMILY =
            new AWTFontFamily("SansSerif");

    /**
     * List of all available fonts on the current system, plus a few common
     * alternatives.
     */
    protected static final Map fonts = new HashMap();

    protected static final List awtFontFamilies = new ArrayList();

    protected static final List awtFonts = new ArrayList();

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
        String[] fontNames = env.getAvailableFontFamilyNames();

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

        //Also register all font names, not just font families.
        //Example: Font Family: "Univers", but Font Name: "Univers 45 Light"
        //Without this, matching "Univers 45 Light" is not possible.
        Font[] allFonts = env.getAllFonts();
        for (int i = 0; i < allFonts.length; i++) {
            Font f = allFonts[i];
            fonts.put(f.getFontName().toLowerCase(), f.getFontName());
        }

        // first add the default font
        awtFontFamilies.add(DEFAULT_FONT_FAMILY);
        awtFonts.add(new AWTGVTFont(DEFAULT_FONT_FAMILY.getFamilyName(), 0, 12));

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
    protected static final Map resolvedFontFamilies = new HashMap();

    /** {@inheritDoc} */
    public String lookup(String familyName) {
        return (String)fonts.get(familyName.toLowerCase());
    }

    /** {@inheritDoc} */
    public GVTFontFamily resolve(String familyName) {

        familyName = familyName.toLowerCase();

        // first see if this font family has already been resolved
        GVTFontFamily resolvedFF =
                (GVTFontFamily)resolvedFontFamilies.get(familyName);

        if (resolvedFF == null) { // hasn't been resolved yet
            // try to find a matching family name in the list of
            // available fonts
            String awtFamilyName = (String)fonts.get(familyName);
            if (awtFamilyName != null) {
                resolvedFF = new AWTFontFamily(awtFamilyName);
            }

            resolvedFontFamilies.put(familyName, resolvedFF);
        }

        //  if (resolvedFF != null) {
        //      System.out.println("resolved " + fontFamily.getFamilyName() +
        //                         " to " + resolvedFF.getFamilyName());
        //  } else {
        //      System.out.println("could not resolve " +
        //                         fontFamily.getFamilyName());
        //  }
        return resolvedFF;
    }

    /** {@inheritDoc} */
    public GVTFontFamily getFamilyThatCanDisplay(char c) {
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

    /** {@inheritDoc} */
    public GVTFontFamily getDefault() {
        return DEFAULT_FONT_FAMILY;
    }

}
