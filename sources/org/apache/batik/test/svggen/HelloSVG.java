/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svggen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.event.*;
import java.io.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.batik.css.CSSDocumentHandler;

import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.apache.batik.util.awt.svg.*;


/**
 * Illustrates basic usage of the SVGGraphics2D class.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:spei@cs.uiowa.edu">Sheng Pei</a>
 * @version $Id$
 */
public class HelloSVG extends JComponent{

    /**
     * The CSS parser class name key.
     */
    public final static String CSS_PARSER_CLASS_NAME =
        "org.w3c.flute.parser.Parser";

    private Color colorA = Color.yellow;
    private Color colorB = Color.red;
    private Color textColor = Color.white;
    private Color textShadowColor = new Color(0, 0, 0, 80);
    private Font textFont = new Font("sansserif", Font.BOLD, 40);

    public HelloSVG(){
    }

    public HelloSVG(Color colorA, Color colorB, Font textFont){
        if(colorA == null || colorB == null || textFont == null)
            throw new IllegalArgumentException();

        this.colorA = colorA;
        this.colorB = colorB;
        this.textFont = textFont;
    }

    public Dimension getPreferredSize(){
        return new Dimension(300, 100);
    }

    public void paint(Graphics _g){
        Graphics2D g = (Graphics2D)_g;

        GradientPaint paint = new GradientPaint(0, 0, colorA,
                                                getWidth(), getHeight(), colorB);
        g.setPaint(paint);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw Text
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderContext frc = g.getFontRenderContext();
        Font defaultFont = g.getFont();
        Shape helloShape = textFont.createGlyphVector(frc, "Hello SVG").getOutline();
        Rectangle helloBounds = helloShape.getBounds();

        AffineTransform defaultTransform = g.getTransform();
        int shadowOffset = 4;
        g.translate((getWidth() - helloBounds.width)/2,
                    (getHeight() + helloBounds.height)/2);
        g.setFont(textFont);
        g.setPaint(textShadowColor);
        g.translate(shadowOffset, shadowOffset);
        g.drawString("Hello SVG", 0, 0);
        g.translate(-shadowOffset, -shadowOffset);
        g.setPaint(textColor);
        g.drawString("Hello SVG", 0, 0);
    }

    public static final String USAGE = "java HelloSVG <svgFileName>";

    public static void main(String args[]) throws IOException{
        if(args.length < 1){
            System.out.println(USAGE);
            System.exit(0);
        }

        String fileName = args[0];

        JFrame frame = new JFrame("Hello SVG");
        HelloSVG helloSvg = new HelloSVG();

        frame.getContentPane().add(helloSvg);
        frame.pack();

        // Set CSS Parser. Needed by our DOM implementation.
        CSSDocumentHandler.setParserClassName
            (CSS_PARSER_CLASS_NAME);

        // Stream out SVG
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String namespaceURI = SVGDOMImplementation.SVG_NAMESPACE_URI;
        // Create SVG Document
        Document domFactory = impl.createDocument(namespaceURI, "svg", null);
        SVGGraphics2D svggen = new SVGGraphics2D(domFactory);
        svggen.setSVGCanvasSize(frame.getContentPane().getSize());

        helloSvg.paint(svggen);

        // Find out whether to use XML attributes or
        // CSS properties
        String useCssStr = System.getProperty("useCss", "true");
        boolean useCss = useCssStr.equalsIgnoreCase("true");

        svggen.stream(fileName, useCss);
        System.out.println("Wrote : " + fileName);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent evt){
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }
}
