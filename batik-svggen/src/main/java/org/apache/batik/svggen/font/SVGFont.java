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

package org.apache.batik.svggen.font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.batik.svggen.font.table.CmapFormat;
import org.apache.batik.svggen.font.table.Feature;
import org.apache.batik.svggen.font.table.FeatureTags;
import org.apache.batik.svggen.font.table.GsubTable;
import org.apache.batik.svggen.font.table.KernSubtable;
import org.apache.batik.svggen.font.table.KernTable;
import org.apache.batik.svggen.font.table.KerningPair;
import org.apache.batik.svggen.font.table.LangSys;
import org.apache.batik.svggen.font.table.PostTable;
import org.apache.batik.svggen.font.table.Script;
import org.apache.batik.svggen.font.table.ScriptTags;
import org.apache.batik.svggen.font.table.SingleSubst;
import org.apache.batik.svggen.font.table.Table;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.constants.XMLConstants;

/**
 * Converts a TrueType font to an SVG embedded font.
 *
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class SVGFont implements XMLConstants, SVGConstants, ScriptTags, FeatureTags {
    static final String EOL;

    static final String PROPERTY_LINE_SEPARATOR = "line.separator";
    static final String PROPERTY_LINE_SEPARATOR_DEFAULT = "\n";

    static final int DEFAULT_FIRST = 32;
    static final int DEFAULT_LAST = 126;

    static {
        String  temp;
        try {
            temp = System.getProperty (PROPERTY_LINE_SEPARATOR,
                                       PROPERTY_LINE_SEPARATOR_DEFAULT);
        } catch (SecurityException e) {
            temp = PROPERTY_LINE_SEPARATOR_DEFAULT;
        }
        EOL = temp;
    }

    private static String QUOT_EOL = XML_CHAR_QUOT + EOL;

    /**
     * Defines the application arguments.
     */
    private static String CONFIG_USAGE =
        "SVGFont.config.usage";

    /**
     * Defines the start of the generated SVG document
     * {0} SVG public ID
     * {1} SVG system ID
     */
    private static String CONFIG_SVG_BEGIN =
        "SVGFont.config.svg.begin";

    /**
     * Defines the SVG start fragment that exercise the generated
     * Font.
     */
    private static String CONFIG_SVG_TEST_CARD_START =
        "SVGFont.config.svg.test.card.start";

    /**
     * Defines the end of the SVG fragment that exercise the generated
     * Font.
     */
    private static String CONFIG_SVG_TEST_CARD_END =
        "SVGFont.config.svg.test.card.end";

    protected static String encodeEntities(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == XML_CHAR_LT) {
                sb.append(XML_ENTITY_LT);
            } else if (s.charAt(i) == XML_CHAR_GT) {
                sb.append(XML_ENTITY_GT);
            } else if (s.charAt(i) == XML_CHAR_AMP) {
                sb.append(XML_ENTITY_AMP);
            } else if (s.charAt(i) == XML_CHAR_APOS) {
                sb.append(XML_ENTITY_APOS);
            } else if(s.charAt(i) == XML_CHAR_QUOT) {
                sb.append(XML_ENTITY_QUOT);
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    protected static String getContourAsSVGPathData(Glyph glyph, int startIndex, int count) {

        // If this is a single point on it's own, we can't do anything with it
        if (glyph.getPoint(startIndex).endOfContour) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        int offset = 0;

        while (offset < count) {
            Point point = glyph.getPoint(startIndex + offset%count);
            Point point_plus1 = glyph.getPoint(startIndex + (offset+1)%count);
            Point point_plus2 = glyph.getPoint(startIndex + (offset+2)%count);

            if (offset == 0) {
                sb.append(PATH_MOVE)
                .append(String.valueOf(point.x))
                .append(XML_SPACE)
                .append(String.valueOf(point.y));
            }

            if (point.onCurve && point_plus1.onCurve) {
                if (point_plus1.x == point.x) { // This is a vertical line
                    sb.append(PATH_VERTICAL_LINE_TO)
                    .append(String.valueOf(point_plus1.y));
                } else if (point_plus1.y == point.y) { // This is a horizontal line
                    sb.append(PATH_HORIZONTAL_LINE_TO)
                    .append(String.valueOf(point_plus1.x));
                } else {
                    sb.append(PATH_LINE_TO)
                    .append(String.valueOf(point_plus1.x))
                    .append(XML_SPACE)
                    .append(String.valueOf(point_plus1.y));
                }
                offset++;
            } else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
                // This is a curve with no implied points
                sb.append(PATH_QUAD_TO)
                .append(String.valueOf(point_plus1.x))
                .append(XML_SPACE)
                .append(String.valueOf(point_plus1.y))
                .append(XML_SPACE)
                .append(String.valueOf(point_plus2.x))
                .append(XML_SPACE)
                .append(String.valueOf(point_plus2.y));
                offset+=2;
            } else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
                // This is a curve with one implied point
                sb.append(PATH_QUAD_TO)
                .append(String.valueOf(point_plus1.x))
                .append(XML_SPACE)
                .append(String.valueOf(point_plus1.y))
                .append(XML_SPACE)
                .append(String.valueOf(midValue(point_plus1.x, point_plus2.x)))
                .append(XML_SPACE)
                .append(String.valueOf(midValue(point_plus1.y, point_plus2.y)));
                offset+=2;
            } else if (!point.onCurve && !point_plus1.onCurve) {
                // This is a curve with two implied points
                sb.append(PATH_SMOOTH_QUAD_TO)
                .append(String.valueOf(midValue(point.x, point_plus1.x)))
                .append(XML_SPACE)
                .append(String.valueOf(midValue(point.y, point_plus1.y)));
                offset++;
            } else if (!point.onCurve && point_plus1.onCurve) {
                sb.append(PATH_SMOOTH_QUAD_TO)
                .append(String.valueOf(point_plus1.x))
                .append(XML_SPACE)
                .append(String.valueOf(point_plus1.y));
                offset++;
            } else {
                System.out.println("drawGlyph case not catered for!!");
                break;
            }
        }
        sb.append(PATH_CLOSE);

        return sb.toString();
    }

    protected static String getSVGFontFaceElement(Font font) {
        StringBuffer sb = new StringBuffer();
        String fontFamily = font.getNameTable().getRecord(Table.nameFontFamilyName);
        short unitsPerEm = font.getHeadTable().getUnitsPerEm();
        String panose = font.getOS2Table().getPanose().toString();
        short ascent = font.getHheaTable().getAscender();
        short descent = font.getHheaTable().getDescender();
        int baseline = 0; // bit 0 of head.flags will indicate if this is true

        //      <!ELEMENT font-face (%descTitleMetadata;,font-face-src?,definition-src?) >
        //           <!ATTLIST font-face
        //             %stdAttrs;
        //             font-family CDATA #IMPLIED
        //             font-style CDATA #IMPLIED
        //             font-variant CDATA #IMPLIED
        //             font-weight CDATA #IMPLIED
        //             font-stretch CDATA #IMPLIED
        //             font-size CDATA #IMPLIED
        //             unicode-range CDATA #IMPLIED
        //             units-per-em %Number; #IMPLIED
        //             panose-1 CDATA #IMPLIED
        //             stemv %Number; #IMPLIED
        //             stemh %Number; #IMPLIED
        //             slope %Number; #IMPLIED
        //             cap-height %Number; #IMPLIED
        //             x-height %Number; #IMPLIED
        //             accent-height %Number; #IMPLIED
        //             ascent %Number; #IMPLIED
        //             descent %Number; #IMPLIED
        //             widths CDATA #IMPLIED
        //             bbox CDATA #IMPLIED
        //             ideographic %Number; #IMPLIED
        //             alphabetic %Number; #IMPLIED
        //             mathematical %Number; #IMPLIED
        //             hanging %Number; #IMPLIED
        //             v-ideographic %Number; #IMPLIED
        //             v-alphabetic %Number; #IMPLIED
        //             v-mathematical %Number; #IMPLIED
        //             v-hanging %Number; #IMPLIED
        //             underline-position %Number; #IMPLIED
        //             underline-thickness %Number; #IMPLIED
        //             strikethrough-position %Number; #IMPLIED
        //             strikethrough-thickness %Number; #IMPLIED
        //             overline-position %Number; #IMPLIED
        //             overline-thickness %Number; #IMPLIED >

        sb.append(XML_OPEN_TAG_START).append(SVG_FONT_FACE_TAG).append(EOL)
            .append(XML_TAB).append(SVG_FONT_FAMILY_ATTRIBUTE).append(XML_EQUAL_QUOT).append(fontFamily).append(QUOT_EOL)
            // .append("  font-family=\"").append(fontFamily).append("\"\r\n")
            .append(XML_TAB).append(SVG_UNITS_PER_EM_ATTRIBUTE).append(XML_EQUAL_QUOT).append(unitsPerEm).append(QUOT_EOL)
            //.append("  units-per-em=\"").append(unitsPerEm).append("\"\r\n")
            .append(XML_TAB).append(SVG_PANOSE_1_ATTRIBUTE).append(XML_EQUAL_QUOT).append(panose).append(QUOT_EOL)
            // .append("  panose-1=\"").append(panose).append("\"\r\n")
            .append(XML_TAB).append(SVG_ASCENT_ATTRIBUTE).append(XML_EQUAL_QUOT).append(ascent).append(QUOT_EOL)
            // .append("  ascent=\"").append(ascent).append("\"\r\n")
            .append(XML_TAB).append(SVG_DESCENT_ATTRIBUTE).append(XML_EQUAL_QUOT).append(descent).append(QUOT_EOL)
            // .append("  descent=\"").append(descent).append("\"\r\n")
            .append(XML_TAB).append(SVG_ALPHABETIC_ATTRIBUTE).append(XML_EQUAL_QUOT).append(baseline).append(XML_CHAR_QUOT)
            .append(XML_OPEN_TAG_END_NO_CHILDREN).append(EOL);
            //.append("  baseline=\"").append(baseline).append("\"/>\r\n");

        return sb.toString();
    }

    /**
     * Implements an Iterator interface to select required glyphs based on user
     *
     * @version $Id$
     * @author <a href="mailto:nilam.gaikwad@nxp.com">Nilam Gaikwad</a>
     */
    public static class GlyphSelector implements Iterator<Integer> {

	    static final int GLYPH_SELECTOR_LINEAR_RANGE     = 1;
	    static final int GLYPH_SELECTOR_NON_LINEAR_RANGE = 2;

        int type;
        int startIndex, endIndex;
        int currentIndex = 0;
        List<Integer> requiredGlyphIndexes = null;

        /**
         * GlyphSelector Default constructore
         */
        public GlyphSelector() {
            init(-1,-1);
        }

        /**
         * GlyphSelector constructor which prepare list of unicode glyph number
         * to extract from TTF file.
         *
         * @param fileName A file containing unicode glyph number list.
         */
        public GlyphSelector(String fileName) {
            init(fileName);
        }

        /**
         * Initializes glyphSelector list from given disk file.
         *
         * @param fileName File which contains list of unicode number of glyphs
         *                    to extract from TTF file.
         */
        protected void init(String fileName) {
            /* This is non-linear glyph selector */
            type = GLYPH_SELECTOR_NON_LINEAR_RANGE;

            /* Populate array */
            requiredGlyphIndexes = new ArrayList<Integer>();
            requiredGlyphIndexes.clear();
            currentIndex = 0;

            try {
                Scanner intList = new Scanner(new File(fileName));
                while (intList.hasNext()) {
                    String s = intList.next();

                    if (s.length() > 0) {
                        int radix, p0, v;
                        boolean intParsingDone = false;
                        char[] cl = s.toCharArray();

                        if (cl[0] == '0' && (cl[1] == 'x' || cl[1] == 'X')) {
                            radix = 16;
                            p0 = 2;
                        } else {
                            radix = 10;
                            p0 = 0;
                        }
                        v = 0;
                        for (int i = p0; i < cl.length; i++) {
                            char c = cl[i];
                            if (c >= 'A' && c <= 'F')
                                v = v * radix + c - 'A' + 10;
                            else if (c >= 'a' && c <= 'f')
                                v = v * radix + c - 'a' + 10;
                            else if (c >= '0' && c <= '9')
                                v = v * radix + c - '0';
                            else
                                break;
                            intParsingDone = true;
                        }
                        cl = null;

                        if (intParsingDone == false) {
                            System.err.printf("WARNING: Not-parsed string [%s]\n", s);
                        } else if (requiredGlyphIndexes.contains(v) == false) {
                            requiredGlyphIndexes.add(v);
                        } else {
                            System.err.printf("WARNING: Duplicate string [%s]\n", s);
                        }
                    }
                }
                intList.close();
            } catch (FileNotFoundException e) {
                System.err.println(e);
            }
        }

        /**
         * Initializes Unicode number extractor for given start-end range.
         *
         * @param start The starting unicode number from required glyphs.
         * @param end The last unicode number from required glyphs.
         */
        protected void init(int start, int end) {
            type = GLYPH_SELECTOR_LINEAR_RANGE;
            startIndex = start;
            endIndex = end;
            currentIndex = startIndex;
        }

        /**
         * Returns true if there are additional unicode number to extract from TTF font
         * Return false otherwise
         */
        public boolean hasNext() {
            boolean result = false;
            if (type == GLYPH_SELECTOR_LINEAR_RANGE) {
                if (currentIndex >= startIndex && currentIndex < endIndex)
                    result = true;
            } else if (type == GLYPH_SELECTOR_NON_LINEAR_RANGE) {
                if (currentIndex < requiredGlyphIndexes.size())
                    result = true;
            }
            return result;
        }

        /**
         * Returns a unicode character number which user has required for glyph extraction
         */
        public Integer next() {
            Integer iVal = 0;
            if (type == GLYPH_SELECTOR_LINEAR_RANGE) {
                iVal = currentIndex;
            } else if (type == GLYPH_SELECTOR_NON_LINEAR_RANGE) {
                iVal = requiredGlyphIndexes.get(currentIndex);
            }
            currentIndex++;
            return iVal;
        }
    }

    /**
     * Returns a &lt;font&gt;&#x2e;&#x2e;&#x2e;&lt;/font&gt; block,
     * defining the specified font.
     *
     * @param font The TrueType font to be converted to SVG
     * @param id An XML id attribute for the font element
     * @param first The first character in the output range
     * @param last The last character in the output range
     * @param forceAscii Force the use of the ASCII character map
     * @param glyphSelector Iterator object which contains list of glyph indexs for extraction
     */
    protected static void writeFontAsSVGFragment(PrintStream ps, Font font, String id, int first, int last, boolean autoRange, boolean forceAscii, GlyphSelector glyphSelector)
    throws Exception {
        //    StringBuffer sb = new StringBuffer();
        //    int horiz_advance_x = font.getHmtxTable().getAdvanceWidth(
        //      font.getHheaTable().getNumberOfHMetrics() - 1);
        int horiz_advance_x = font.getOS2Table().getAvgCharWidth();

        ps.print(XML_OPEN_TAG_START);
        ps.print(SVG_FONT_TAG);
        ps.print(XML_SPACE);
        // ps.print("<font ");
        if (id != null) {
            ps.print(SVG_ID_ATTRIBUTE);
            ps.print(XML_EQUAL_QUOT);
            // ps.print("id=\"");
            ps.print(id);
            ps.print(XML_CHAR_QUOT);
            ps.print(XML_SPACE);
            // ps.print("\" ");
        }

        ps.print(SVG_HORIZ_ADV_X_ATTRIBUTE);
        ps.print(XML_EQUAL_QUOT);
        // ps.print("horiz-adv-x=\"");
        ps.print(horiz_advance_x);
        ps.print(XML_CHAR_QUOT);
        ps.print(XML_OPEN_TAG_END_CHILDREN);
        // ps.println("\">");

        ps.print(getSVGFontFaceElement(font));

        // Decide upon a cmap table to use for our character to glyph look-up
        CmapFormat cmapFmt = null;
        if (forceAscii) {
            // We've been asked to use the ASCII/Macintosh cmap format
            cmapFmt = font.getCmapTable().getCmapFormat(
                Table.platformMacintosh,
                Table.encodingRoman );
        } else {
            // The default behaviour is to use the Unicode cmap encoding
            cmapFmt = font.getCmapTable().getCmapFormat(
                Table.platformMicrosoft,
                Table.encodingUGL );
            if (cmapFmt == null) {
                // This might be a symbol font, so we'll look for an "undefined" encoding
                cmapFmt = font.getCmapTable().getCmapFormat(
                    Table.platformMicrosoft,
                    Table.encodingUndefined );
            }
        }
        if (cmapFmt == null) {
            throw new Exception("Cannot find a suitable cmap table");
        }

        // If this font includes arabic script, we want to specify
        // substitutions for initial, medial, terminal & isolated
        // cases.
        GsubTable gsub = (GsubTable) font.getTable(Table.GSUB);
        SingleSubst initialSubst = null;
        SingleSubst medialSubst = null;
        SingleSubst terminalSubst = null;
        if (gsub != null) {
            Script s = gsub.getScriptList().findScript(SCRIPT_TAG_ARAB);
            if (s != null) {
                LangSys ls = s.getDefaultLangSys();
                if (ls != null) {
                    Feature init = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_INIT);
                    Feature medi = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_MEDI);
                    Feature fina = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_FINA);

                    if (init != null) {
                        initialSubst = (SingleSubst)
                            gsub.getLookupList().getLookup(init, 0).getSubtable(0);
                    }
                    if (medi != null) {
                        medialSubst = (SingleSubst)
                            gsub.getLookupList().getLookup(medi, 0).getSubtable(0);
                    }
                    if (fina != null) {
                        terminalSubst = (SingleSubst)
                            gsub.getLookupList().getLookup(fina, 0).getSubtable(0);
                    }
                }
            }
        }

        // Include the missing glyph
        ps.println(getGlyphAsSVG(font, font.getGlyph(0), 0, horiz_advance_x,
            initialSubst, medialSubst, terminalSubst, ""));

        try {
            boolean glyphSelectorReInitRequired = (autoRange == true || ( first != -1 && last != -1));
            if (first == -1) {
                if (!autoRange) first = DEFAULT_FIRST;
                else            first = cmapFmt.getFirst();
            }
            if (last == -1) {
                if (!autoRange) last = DEFAULT_LAST;
                else            last = cmapFmt.getLast();
            }

            // Include our requested characters
            Set glyphSet = new HashSet();
            if (glyphSelectorReInitRequired) {
                glyphSelector.init(first, last);
            }

            while (glyphSelector.hasNext()) {
                int i = glyphSelector.next().intValue();
                int glyphIndex = cmapFmt.mapCharCode(i);
                //        ps.println(String.valueOf(i) + " -> " + String.valueOf(glyphIndex));
                //      if (font.getGlyphs()[glyphIndex] != null)
                //        sb.append(font.getGlyphs()[glyphIndex].toString() + "\n");

                if (glyphIndex > 0) {
                    // add glyph ID to set so we can filter later
                    glyphSet.add(glyphIndex);

                    ps.println(getGlyphAsSVG(
                        font,
                        font.getGlyph(glyphIndex),
                        glyphIndex,
                        horiz_advance_x,
                        initialSubst, medialSubst, terminalSubst,
                        (32 <= i && i <= 127) ?
                        encodeEntities( String.valueOf( (char)i ) ) :
                        XML_CHAR_REF_PREFIX + Integer.toHexString(i) + XML_CHAR_REF_SUFFIX));
                }

            }

            // Output kerning pairs from the requested range
            KernTable kern = (KernTable) font.getTable(Table.kern);
            if (kern != null) {
                KernSubtable kst = kern.getSubtable(0);
                PostTable post = (PostTable) font.getTable(Table.post);
                for (int i = 0; i < kst.getKerningPairCount(); i++) {
                    KerningPair kpair = kst.getKerningPair(i);
                    // check if left and right are both in our glyph set
                    if (glyphSet.contains(kpair.getLeft()) && glyphSet.contains(kpair.getRight())) {
                        ps.println(getKerningPairAsSVG(kpair, post));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        ps.print(XML_CLOSE_TAG_START);
        ps.print(SVG_FONT_TAG);
        ps.println(XML_CLOSE_TAG_END);
        // ps.println("</font>");
    }

    protected static String getGlyphAsSVG(
            Font font,
            Glyph glyph,
            int glyphIndex,
            int defaultHorizAdvanceX,
            String attrib,
            String code) {

        StringBuffer sb = new StringBuffer();
        int firstIndex = 0;
        int count = 0;
        int i;
        int horiz_advance_x;

        horiz_advance_x = font.getHmtxTable().getAdvanceWidth(glyphIndex);

        if (glyphIndex == 0) {
            sb.append(XML_OPEN_TAG_START);
            sb.append(SVG_MISSING_GLYPH_TAG);
            // sb.append("<missing-glyph");
        } else {

            // Unicode value
            sb.append(XML_OPEN_TAG_START)
                .append(SVG_GLYPH_TAG).append(XML_SPACE).append(SVG_UNICODE_ATTRIBUTE)
                .append(XML_EQUAL_QUOT).append(code).append(XML_CHAR_QUOT);
            // sb.append("<glyph unicode=\"").append(code).append("\"");

            // Glyph name
            String glyphName = font.getPostTable().getGlyphName(glyphIndex);
            if (glyphName != null) {
                sb.append(XML_SPACE).append(SVG_GLYPH_NAME_ATTRIBUTE).append(XML_EQUAL_QUOT)
                    // sb.append(" glyph-name=\"")
                    .append(glyphName)
                    // .append("\"");
                    .append(XML_CHAR_QUOT);
            }
        }
        if (horiz_advance_x != defaultHorizAdvanceX) {
            sb.append(XML_SPACE).append(SVG_HORIZ_ADV_X_ATTRIBUTE).append(XML_EQUAL_QUOT)
                .append(horiz_advance_x).append(XML_CHAR_QUOT);
            // sb.append(" horiz-adv-x=\"").append(horiz_advance_x).append("\"");
        }

        if (attrib != null) {
            sb.append(attrib);
        }

        if (glyph != null) {
            // sb.append(" d=\"");
            sb.append(XML_SPACE).append(SVG_D_ATTRIBUTE).append(XML_EQUAL_QUOT);
            for (i = 0; i < glyph.getPointCount(); i++) {
                count++;
                if (glyph.getPoint(i).endOfContour) {
                    sb.append(getContourAsSVGPathData(glyph, firstIndex, count));
                    firstIndex = i + 1;
                    count = 0;
                }
            }
            // sb.append("\"");
            sb.append(XML_CHAR_QUOT);
        }

        sb.append(XML_OPEN_TAG_END_NO_CHILDREN);
        // sb.append("/>");

        // Chop-up the string into 255 character lines
        chopUpStringBuffer(sb);

        return sb.toString();
    }

    protected static String getGlyphAsSVG(
            Font font,
            Glyph glyph,
            int glyphIndex,
            int defaultHorizAdvanceX,
            SingleSubst arabInitSubst,
            SingleSubst arabMediSubst,
            SingleSubst arabTermSubst,
            String code) {

        StringBuffer sb = new StringBuffer();
        boolean substituted = false;

        // arabic = "initial | medial | terminal | isolated"
        int arabInitGlyphIndex = glyphIndex;
        int arabMediGlyphIndex = glyphIndex;
        int arabTermGlyphIndex = glyphIndex;
        if (arabInitSubst != null) {
            arabInitGlyphIndex = arabInitSubst.substitute(glyphIndex);
        }
        if (arabMediSubst != null) {
            arabMediGlyphIndex = arabMediSubst.substitute(glyphIndex);
        }
        if (arabTermSubst != null) {
            arabTermGlyphIndex = arabTermSubst.substitute(glyphIndex);
        }

        if (arabInitGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabInitGlyphIndex),
                arabInitGlyphIndex,
                defaultHorizAdvanceX,
                // " arabic-form=\"initial\"",
                (XML_SPACE + SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT +
                 SVG_INITIAL_VALUE + XML_CHAR_QUOT),
                code));
            // sb.append("\r\n");
            sb.append(EOL);
            substituted = true;
        }

        if (arabMediGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabMediGlyphIndex),
                arabMediGlyphIndex,
                defaultHorizAdvanceX,
                // " arabic-form=\"medial\"",
                (XML_SPACE + SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT +
                 SVG_MEDIAL_VALUE + XML_CHAR_QUOT),
                code));
            // sb.append("\r\n");
            sb.append(EOL);
            substituted = true;
        }

        if (arabTermGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabTermGlyphIndex),
                arabTermGlyphIndex,
                defaultHorizAdvanceX,
                // " arabic-form=\"terminal\"",
                (XML_SPACE + SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT +
                 SVG_TERMINAL_VALUE + XML_CHAR_QUOT),
                code));
            // sb.append("\r\n");
            sb.append(EOL);
            substituted = true;
        }

        if (substituted) {
            sb.append(getGlyphAsSVG(
                font,
                glyph,
                glyphIndex,
                defaultHorizAdvanceX,
                // " arabic-form=\"isolated\"",
                (XML_SPACE + SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT +
                 SVG_ISOLATED_VALUE + XML_CHAR_QUOT),
                code));
        } else {
            sb.append(getGlyphAsSVG(
                font,
                glyph,
                glyphIndex,
                defaultHorizAdvanceX,
                null,
                code));
        }

        return sb.toString();
    }

    protected static String getKerningPairAsSVG(KerningPair kp, PostTable post) {
        String leftGlyphName = post.getGlyphName(kp.getLeft());
        String rightGlyphName = post.getGlyphName(kp.getRight());

        StringBuffer sb = new StringBuffer();
        // sb.append("<hkern ");
        sb.append(XML_OPEN_TAG_START).append(SVG_HKERN_TAG).append(XML_SPACE);

        if (leftGlyphName == null) {
            sb.append(SVG_U1_ATTRIBUTE).append(XML_EQUAL_QUOT);

            sb.append(kp.getLeft());
        } else {
            // sb.append("g1=\"");
            sb.append(SVG_G1_ATTRIBUTE).append(XML_EQUAL_QUOT);

            sb.append(leftGlyphName);
        }

        // sb.append("\" ");
        sb.append(XML_CHAR_QUOT).append(XML_SPACE);

        if (rightGlyphName == null) {
            // sb.append("u2=\"");
            sb.append(SVG_U2_ATTRIBUTE).append(XML_EQUAL_QUOT);

            sb.append(kp.getRight());
        } else {
            // sb.append("g2=\"");
            sb.append(SVG_G2_ATTRIBUTE).append(XML_EQUAL_QUOT);

            sb.append(rightGlyphName);
        }

        // sb.append("\" k=\"");
        sb.append(XML_CHAR_QUOT).append(XML_SPACE).append(SVG_K_ATTRIBUTE).append(XML_EQUAL_QUOT);

        // SVG kerning values are inverted from TrueType's.
        sb.append(-kp.getValue());
        // sb.append("\"/>");
        sb.append(XML_CHAR_QUOT).append(XML_OPEN_TAG_END_NO_CHILDREN);

        return sb.toString();
    }
/*
    protected static String getGlyphAsPath(Glyph glyph) {
        StringBuffer sb = new StringBuffer();
        int firstIndex = 0;
        int count = 0;
        int i;

        for (i = 0; i < glyph.getPointCount(); i++) {
            count++;
            if (glyph.getPoint(i).endOfContour) {
                sb.append(getContourAsSVGPathData(glyph, firstIndex, count));
                firstIndex = i + 1;
                count = 0;
            }
        }
        return sb.toString();
    }

    protected static void writeTextAsSVGFragment(PrintStream ps, Font f, int size, String text) {
        CmapFormat cmapFmt = f.getCmapTable().getCmapFormat(Table.platformMicrosoft, Table.encodingUGL);
        int x = 0;
        for (short i = 0; i < text.length(); i++) {
            int glyphIndex = cmapFmt.mapCharCode((short)text.charAt(i));
            Glyph glyph = f.getGlyph(glyphIndex);
            if (glyph != null) {
                ps.println(translateSVG(x, 0, getGlyphAsSVGPath(glyph)));
                x += glyph.getAdvanceWidth();
            }
        }
    }
*/
    protected static void writeSvgBegin(PrintStream ps) {
        // ps.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        // ps.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\"");
        // ps.println("\"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\" >");
        // ps.println("<svg width=\"100%\" height=\"100%\">");
        ps.println(Messages.formatMessage(CONFIG_SVG_BEGIN,
                                          new Object[]{SVG_PUBLIC_ID, SVG_SYSTEM_ID}));

    }

    protected static void writeSvgDefsBegin(PrintStream ps) {
        // ps.println("<defs>");
        ps.println(XML_OPEN_TAG_START + SVG_DEFS_TAG + XML_OPEN_TAG_END_CHILDREN);
    }

    protected static void writeSvgDefsEnd(PrintStream ps) {
        // ps.println("</defs>");
        ps.println(XML_CLOSE_TAG_START + SVG_DEFS_TAG + XML_CLOSE_TAG_END);
    }

    protected static void writeSvgEnd(PrintStream ps) {
        // ps.println("</svg>");
        ps.println(XML_CLOSE_TAG_START + SVG_SVG_TAG + XML_CLOSE_TAG_END);
    }

    protected static void writeSvgTestCard(PrintStream ps, String fontFamily) {
        ps.println(Messages.formatMessage(CONFIG_SVG_TEST_CARD_START, null));
        ps.println(fontFamily);
        ps.println(Messages.formatMessage(CONFIG_SVG_TEST_CARD_END, null));

        /*ps.println("<g style=\"font-family: '" + fontFamily + "'; font-size:18;fill:black\">");
        ps.println("<text x=\"20\" y=\"60\"> !&quot;#$%&amp;&apos;()*+,-./0123456789:;&lt;&gt;?</text>");
        ps.println("<text x=\"20\" y=\"120\">@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_</text>");
        ps.println("<text x=\"20\" y=\"180\">`abcdefghijklmnopqrstuvwxyz{|}~</text>");
        ps.println("<text x=\"20\" y=\"240\">&#x80;&#x81;&#x82;&#x83;&#x84;&#x85;&#x86;&#x87;&#x88;&#x89;&#x8a;&#x8b;
        &#x8c;&#x8d;&#x8e;&#x8f;&#x90;&#x91;&#x92;&#x93;&#x94;&#x95;&#x96;&#x97;&#x98;&#x99;&#x9a;&#x9b;&#x9c;&#x9d;
        &#x9e;&#x9f;</text>");
        ps.println("<text x=\"20\" y=\"300\">&#xa0;&#xa1;&#xa2;&#xa3;&#xa4;&#xa5;&#xa6;&#xa7;&#xa8;&#xa9;&#xaa;&#xab;
        &#xac;&#xad;&#xae;&#xaf;&#xb0;&#xb1;&#xb2;&#xb3;&#xb4;&#xb5;&#xb6;&#xb7;&#xb8;&#xb9;&#xba;&#xbb;&#xbc;&#xbd;
        &#xbe;&#xbf;</text>");
        ps.println("<text x=\"20\" y=\"360\">&#xc0;&#xc1;&#xc2;&#xc3;&#xc4;&#xc5;&#xc6;&#xc7;&#xc8;&#xc9;&#xca;&#xcb;
        &#xcc;&#xcd;&#xce;&#xcf;&#xd0;&#xd1;&#xd2;&#xd3;&#xd4;&#xd5;&#xd6;&#xd7;&#xd8;&#xd9;&#xda;&#xdb;&#xdc;&#xdd;
        &#xde;&#xdf;</text>");
        ps.println("<text x=\"20\" y=\"420\">&#xe0;&#xe1;&#xe2;&#xe3;&#xe4;&#xe5;&#xe6;&#xe7;&#xe8;&#xe9;&#xea;&#xeb;
        &#xec;&#xed;&#xee;&#xef;&#xf0;&#xf1;&#xf2;&#xf3;&#xf4;&#xf5;&#xf6;&#xf7;&#xf8;&#xf9;&#xfa;&#xfb;&#xfc;&#xfd;
        &#xfe;&#xff;</text>");
        ps.println("</g>");*/
    }

    public static final char   ARG_KEY_START_CHAR = '-';
    public static final String ARG_KEY_CHAR_RANGE_LOW = "-l";
    public static final String ARG_REQUIRED_UNICODE_LIST = "-u";
    public static final String ARG_KEY_CHAR_RANGE_HIGH = "-h";
    public static final String ARG_KEY_ID = "-id";
    public static final String ARG_KEY_ASCII = "-ascii";
    public static final String ARG_KEY_TESTCARD = "-testcard";
    public static final String ARG_KEY_AUTO_RANGE = "-autorange";
    public static final String ARG_KEY_OUTPUT_PATH = "-o";

    /**
     * Starts the application.
     * @param args an array of command-line arguments
     */
    public static void main(String[] args) {
        try {
            String path = parseArgs(args, null);
            String low = parseArgs(args, ARG_KEY_CHAR_RANGE_LOW);
            String high = parseArgs(args, ARG_KEY_CHAR_RANGE_HIGH);
            String unicodeCharListFile = parseArgs(args, ARG_REQUIRED_UNICODE_LIST);
            String id = parseArgs(args, ARG_KEY_ID);
            String ascii = parseArgs(args, ARG_KEY_ASCII);
            String testCard = parseArgs(args, ARG_KEY_TESTCARD);
            String outPath = parseArgs(args, ARG_KEY_OUTPUT_PATH);
            String autoRange = parseArgs(args, ARG_KEY_AUTO_RANGE);
            PrintStream ps = null;
            FileOutputStream fos = null;
            GlyphSelector gs = new GlyphSelector();

            // What are we outputting to?
            if (outPath != null) {
                // If an output path was specified, write to a file
                fos = new FileOutputStream(outPath);
                ps = new PrintStream(fos);
            } else {
                // Otherwise we'll just put it to stdout
                ps = System.out;
            }

            // The font path is the only required argument
            if (path != null) {
                Font font = Font.create(path);

                if (unicodeCharListFile != null) {
                    gs.init(unicodeCharListFile);
                }

                // Write the various parts of the SVG file
                writeSvgBegin(ps);
                writeSvgDefsBegin(ps);
                writeFontAsSVGFragment(
                    ps,
                    font,
                    id,
                    (low != null ? Integer.parseInt(low) : -1),
                    (high != null ? Integer.parseInt(high) : -1),
                    (autoRange != null),
                    (ascii != null),
                    gs);
                writeSvgDefsEnd(ps);
                if (testCard != null) {
                    String fontFamily = font.getNameTable().getRecord(Table.nameFontFamilyName);
                    writeSvgTestCard(ps, fontFamily);
                }
                writeSvgEnd(ps);

                // Close the output stream (if we have one)
                if (fos != null) {
                    fos.close();
                }
            } else {
                usage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            usage();
        }
    }

    private static void chopUpStringBuffer(StringBuffer sb) {
        if (sb.length() < 256) {
            return;
        } else {
            // Being rather simplistic about it, for now we'll insert a newline after
            // 240 chars
            for (int i = 240; i < sb.length(); i++) {
                if (sb.charAt(i) == ' ') {
                    sb.setCharAt(i, '\n');
                    i += 240;
                }
            }
        }
    }

    private static int midValue(int a, int b) {
        return a + (b - a)/2;
    }

    /*private static String translateSVG(int x, int y, String svgText) {
        StringBuffer sb = new StringBuffer();
        sb.append("<g transform=\"translate(")
            .append(String.valueOf(x))
            .append(" ")
            .append(String.valueOf(y))
            .append(")\">")
            .append(svgText)
            .append("</g>");
        return sb.toString();
        }*/

    private static String parseArgs(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (name == null) {
                if (args[i].charAt(0) != ARG_KEY_START_CHAR) {
                    return args[i];
                }
            } else if (name.equalsIgnoreCase(args[i])) {
                if ((i < args.length - 1) && (args[i+1].charAt(0) != ARG_KEY_START_CHAR)) {
                    return args[i+1];
                } else {
                    return args[i];
                }
            }
        }
        return null;
    }

    private static void usage() {
        System.err.println(Messages.formatMessage(CONFIG_USAGE, null));
    }
}
