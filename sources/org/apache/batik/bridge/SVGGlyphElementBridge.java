/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import java.io.StringReader;
import org.apache.batik.gvt.ShapePainter;


/**
 * Bridge class for the &lt;glyph> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGGlyphElementBridge implements Bridge, SVGConstants, ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;glyph> element.
     */
    protected SVGGlyphElementBridge() {}

    /**
     * Constructs a new Glyph that represents the specified &lt;glyph> element
     * at the requested size.
     *
     * @param ctx The current bridge context.
     * @param glyphElement The glyph element to base the glyph construction on.
     * @param textElement The textElement the glyph will be used for.
     * @param glyphCode The unique id to give to the new glyph.
     * @param fontSize The font size used to determine the size of the glyph.
     * @param fontFace The font face object that contains the font attributes.
     *
     * @return The new Glyph.
     */
    public Glyph createGlyph(BridgeContext ctx, Element glyphElement,
                             Element textElement, int glyphCode, float fontSize,
                             SVGFontFace fontFace) {

        // build the GVT tree that represents the glyph

        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode glyphContentNode
            = new CompositeGraphicsNode();

        float fontHeight = fontFace.getUnitsPerEm();
        float scale = fontSize/fontHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, -scale);

        // create a shape node that represents the d attribute
        String d = glyphElement.getAttributeNS(null, SVG_D_ATTRIBUTE);
        if (d.length() != 0) {
            ShapeNode shapeNode = new ShapeNode();
            AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(glyphElement));
            try {
                PathParser pathParser = new PathParser();
                pathParser.setPathHandler(app);
                pathParser.parse(new StringReader(d));
            } catch (ParseException ex) {
                throw new BridgeException(glyphElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                          new Object [] {SVG_D_ATTRIBUTE});
            } finally {

                // transform the shape into the correct coord system
                Shape shape = app.getShape();
                Shape transformedShape = scaleTransform.createTransformedShape(shape);
                shapeNode.setShape(transformedShape);

                // set up the painter for the d part of the glyph
                ShapePainter painter = PaintServer.convertFillAndStroke(
                                      textElement, shapeNode, ctx);
                shapeNode.setShapePainter(painter);

                glyphContentNode.add(shapeNode);
            }
        }

        // process any glyph children

        // first see if there are any, because don't want to do the following
        // bit of code if we can avoid it

        NodeList glyphChildren = glyphElement.getChildNodes();
        int numChildren = glyphChildren.getLength();
        int numGlyphChildren = 0;
        for (int i = 0; i < numChildren; i++) {
            Node childNode = glyphChildren.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                numGlyphChildren++;
            }
        }

        if (numGlyphChildren > 0) {  // the glyph has child elements

            //
            // need to clone the parent font element and glyph element
            // this is so that the glyph doesn't inherit anything past the font element
            //
            Element fontElementClone = (Element)glyphElement.getParentNode().cloneNode(false);
            // copy all font attributes over
            NamedNodeMap fontAttributes = glyphElement.getParentNode().getAttributes();
            int numAttributes = fontAttributes.getLength();
            for (int i = 0; i < numAttributes; i++) {
                fontElementClone.setAttributeNode((Attr)fontAttributes.item(i));
            }
            Element clonedGlyphElement = (Element)glyphElement.cloneNode(true);
            fontElementClone.appendChild(clonedGlyphElement);

            textElement.appendChild(fontElementClone);

            CompositeGraphicsNode glyphChildrenNode
                = new CompositeGraphicsNode();

            glyphChildrenNode.setTransform(scaleTransform);

            NodeList clonedGlyphChildren = clonedGlyphElement.getChildNodes();
            int numClonedChildren = clonedGlyphChildren.getLength();
            for (int i = 0; i < numClonedChildren; i++) {
                Node childNode = clonedGlyphChildren.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element)childNode;
                    GraphicsNode childGraphicsNode = builder.build(ctx, childElement);
                    glyphChildrenNode.add(childGraphicsNode);
                }
            }
            glyphContentNode.add(glyphChildrenNode);
            textElement.removeChild(fontElementClone);
        }

        // set up glyph attributes

        // unicode
        String unicode = glyphElement.getAttributeNS(null, SVG_UNICODE_ATTRIBUTE);

        // glyph-name
        String nameList = glyphElement.getAttributeNS(null, SVG_GLYPH_NAME_ATTRIBUTE);
        Vector names = new Vector();
        StringTokenizer st = new StringTokenizer(nameList, " ,");
        while (st.hasMoreTokens()) {
            names.add(st.nextToken());
        }

        // orientation
        String orientation = glyphElement.getAttributeNS(null, SVG_ORIENTATION_ATTRIBUTE);

        // arabicForm
        String arabicForm = glyphElement.getAttributeNS(null, SVG_ARABIC_FORM_ATTRIBUTE);

        // lang
        String lang = glyphElement.getAttributeNS(null, SVG_LANG_ATTRIBUTE);

        Element parentFontElement = (Element)glyphElement.getParentNode();

        // horz-adv-x
        String s = glyphElement.getAttributeNS(null, SVG_HORIZ_ADV_X_ATTRIBUTE);
        if (s.length() == 0) {
            // look for attribute on parent font element
            s = parentFontElement.getAttributeNS(null, SVG_HORIZ_ADV_X_ATTRIBUTE);
            if (s.length() == 0) {
                // not specified on parent either, use one em
                s = String.valueOf(fontFace.getUnitsPerEm());
            }
        }
        float horizAdvX;
        try {
            horizAdvX = SVGUtilities.convertSVGNumber(s) * scale;
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (glyphElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_HORIZ_ADV_X_ATTRIBUTE, s});
        }

        // vert-adv-y
        s = glyphElement.getAttributeNS(null, SVG_VERT_ADV_Y_ATTRIBUTE);
        if (s.length() == 0) {
            // look for attribute on parent font element
            s = parentFontElement.getAttributeNS(null, SVG_VERT_ADV_Y_ATTRIBUTE);
            if (s.length() == 0) {
                // not specified on parent either, use one em
                s = String.valueOf(fontFace.getUnitsPerEm());
            }
        }
        float vertAdvY;
        try {
            vertAdvY = SVGUtilities.convertSVGNumber(s) * -scale;
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (glyphElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_VERT_ADV_Y_ATTRIBUTE, s});
        }

        // vert-origin-x
        s = glyphElement.getAttributeNS(null, SVG_VERT_ORIGIN_X_ATTRIBUTE);
        if (s.length() == 0) {
            // look for attribute on parent font element
            s = parentFontElement.getAttributeNS(null, SVG_VERT_ORIGIN_X_ATTRIBUTE);
            if (s.length() == 0) {
                // not specified so use the default value which is font.horzAdvX/2
                s = parentFontElement.getAttributeNS(null, SVG_HORIZ_ADV_X_ATTRIBUTE);
                if (s.length() == 0) {
                    // not specified on parent either, use one em/2
                    s = String.valueOf(fontFace.getUnitsPerEm()/2);
                } else {
                    // need to divide by 2
                    s = String.valueOf(Float.parseFloat(s)/2);
                }
            }
        }
        float vertOriginX;
        try {
            vertOriginX = SVGUtilities.convertSVGNumber(s) * scale;
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (glyphElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_VERT_ORIGIN_X_ATTRIBUTE, s});
        }

        // vert-origin-y
        s = glyphElement.getAttributeNS(null, SVG_VERT_ORIGIN_Y_ATTRIBUTE);
        if (s.length() == 0) {
            // look for attribute on parent font element
            s = parentFontElement.getAttributeNS(null, SVG_VERT_ORIGIN_Y_ATTRIBUTE);
            if (s.length() == 0) {
                // not specified so use the default value which is the fonts ascent
                s = String.valueOf(fontFace.getAscent());
            }
        }
        float vertOriginY;
        try {
            vertOriginY = SVGUtilities.convertSVGNumber(s) * -scale;
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (glyphElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_VERT_ORIGIN_Y_ATTRIBUTE, s});
        }

        Point2D vertOrigin = new Point2D.Float(vertOriginX, vertOriginY);

        // return a new Glyph
        return new Glyph(glyphContentNode, unicode, names,
                                orientation, arabicForm, lang, vertOrigin,
                                horizAdvX, vertAdvY, glyphCode, scale);
    }
}
