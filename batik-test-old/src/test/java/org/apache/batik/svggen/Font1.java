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
package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;

/**
 * This test validates the conversion of Java 2D Fonts into
 * SVG font attributes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Font1 implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Set default font
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));

        // Colors used for labels and test output
        Color labelColor = new Color(0x666699);
        Color fontColor = Color.black;

        //
        // First, font size
        //
        java.awt.geom.AffineTransform defaultTransform = g.getTransform();
        java.awt.Font defaultFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 16);
        g.setFont(defaultFont);
        FontRenderContext frc = g.getFontRenderContext();
        g.setPaint(labelColor);

        g.drawString("Font size", 10, 30);
        g.setPaint(fontColor);
        g.translate(0, 20);
        int[] fontSizes = { 6, 8, 10, 12, 18, 36, 48 };
        for (int fontSize : fontSizes) {
            Font font = new Font(defaultFont.getFamily(),
                    Font.PLAIN,
                    fontSize);
            g.setFont(font);
            g.drawString("aA", 10, 40);
            double width = font.createGlyphVector(frc, "aA").getVisualBounds().getWidth();
            g.translate(width * 1.2, 0);
        }

        g.setTransform(defaultTransform);
        g.translate(0, 60);

        //
        // Font style
        //
        int[] fontStyles = { java.awt.Font.PLAIN,
                             java.awt.Font.BOLD,
                             java.awt.Font.ITALIC,
                             java.awt.Font.BOLD | java.awt.Font.ITALIC };
        String[] fontStyleStrings = { "Plain", "Bold", "Italic", "Bold Italic" };

        g.setFont(defaultFont);
        g.setPaint(labelColor);
        g.drawString("Font Styles", 10, 30);
        g.translate(0, 20);
        g.setPaint(fontColor);

        for(int i=0; i<fontStyles.length; i++){
            java.awt.Font font = new java.awt.Font(defaultFont.getFamily(),
                                 fontStyles[i], 20);
            g.setFont(font);
            g.drawString(fontStyleStrings[i], 10, 40);
            double width = font.createGlyphVector(frc, fontStyleStrings[i]).getVisualBounds().getWidth();
            g.translate(width*1.2, 0);
        }

        g.setTransform(defaultTransform);
        g.translate(0, 120);

        //
        // Font families
        //
        String[] fontFamilies = { "Arial",
                                  "Times New Roman",
                                  "Courier New",
                                  "Verdana" };

        g.setFont(defaultFont);
        g.setPaint(labelColor);
        g.drawString("Font Families", 10, 30);
        g.setPaint(fontColor);

        for (String fontFamily : fontFamilies) {
            Font font = new Font(fontFamily, Font.PLAIN, 18);
            g.setFont(font);
            double height = font.createGlyphVector(frc, fontFamily).getVisualBounds().getHeight();
            g.translate(0, height * 1.4);
            g.drawString(fontFamily, 10, 40);
        }

        //
        // Logical fonts
        //
        Font[] logicalFonts = { new java.awt.Font("dialog", java.awt.Font.PLAIN, 14),
                                new java.awt.Font("dialoginput", java.awt.Font.BOLD, 14),
                                new java.awt.Font("monospaced", java.awt.Font.ITALIC, 14),
                                new java.awt.Font("serif", java.awt.Font.PLAIN, 14),
                                new java.awt.Font("sansserif", java.awt.Font.BOLD, 14)};

        g.translate(0, 70);
        g.setFont(defaultFont);
        g.setPaint(labelColor);
        g.drawString("Logical Fonts", 10, 0);
        g.setPaint(fontColor);

        for (Font font : logicalFonts) {
            g.setFont(font);
            double height = font.createGlyphVector(frc, font.getName()).getVisualBounds().getHeight();
            g.translate(0, height * 1.4);
            g.drawString(font.getName(), 10, 0);
        }
    }
}
