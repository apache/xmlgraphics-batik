/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;

/**
 * This test validates the convertion of Java 2D Fonts into
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
        int fontSizes[] = { 6, 8, 10, 12, 18, 36, 48 };
        for(int i=0; i<fontSizes.length; i++){
            java.awt.Font font = new java.awt.Font(defaultFont.getFamily(),
                                 java.awt.Font.PLAIN,
                                 fontSizes[i]);
            g.setFont(font);
            g.drawString("aA", 10, 40);
            double width = font.createGlyphVector(frc, "aA").getVisualBounds().getWidth();
            g.translate(width*1.2, 0);
        }

        g.setTransform(defaultTransform);
        g.translate(0, 60);

        //
        // Font style
        //
        int fontStyles[] = { java.awt.Font.PLAIN,
                             java.awt.Font.BOLD,
                             java.awt.Font.ITALIC,
                             java.awt.Font.BOLD | java.awt.Font.ITALIC };
        String fontStyleStrings[] = { "Plain", "Bold", "Italic", "Bold Italic" };

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
        String fontFamilies[] = { "Arial",
                                  "Times New Roman",
                                  "Courier New",
                                  "Verdana" };

        g.setFont(defaultFont);
        g.setPaint(labelColor);
        g.drawString("Font Families", 10, 30);
        g.setPaint(fontColor);

        for(int i=0; i<fontFamilies.length; i++){
            java.awt.Font font = new java.awt.Font(fontFamilies[i], java.awt.Font.PLAIN, 18);
            g.setFont(font);
            double height = font.createGlyphVector(frc, fontFamilies[i]).getVisualBounds().getHeight();
            g.translate(0, height*1.4);
            g.drawString(fontFamilies[i], 10, 40);
        }

        //
        // Logical fonts
        //
        Font logicalFonts[] = { new java.awt.Font("dialog", java.awt.Font.PLAIN, 14),
                                new java.awt.Font("dialoginput", java.awt.Font.BOLD, 14),
                                new java.awt.Font("monospaced", java.awt.Font.ITALIC, 14),
                                new java.awt.Font("serif", java.awt.Font.PLAIN, 14),
                                new java.awt.Font("sansserif", java.awt.Font.BOLD, 14)};

        g.translate(0, 70);
        g.setFont(defaultFont);
        g.setPaint(labelColor);
        g.drawString("Logical Fonts", 10, 0);
        g.setPaint(fontColor);

        for(int i=0; i<logicalFonts.length; i++){
            Font font = logicalFonts[i];
            g.setFont(font);
            double height = font.createGlyphVector(frc, font.getName()).getVisualBounds().getHeight();
            g.translate(0, height*1.4);
            g.drawString(font.getName(), 10, 0);
        }
    }
}
