/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.ArabicTextHandler;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.util.Vector;


/**
 * A Glyph describes a graphics node with some specific glyph rendering
 * attributes.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class Glyph {

    private GraphicsNode glyphNode;
    private String unicode;
    private Vector names;
    private String orientation;
    private String arabicForm;
    private String lang;
    private Point2D horizOrigin;
    private Point2D vertOrigin;
    private float horizAdvX;
    private float vertAdvY;
    private int glyphCode;
    private AffineTransform transform;
    private Point2D position;
    private GVTGlyphMetrics metrics;
    private float kernScale;

    private Shape outline; // cache the glyph outline


    /**
     * Constructs a Glyph with the specified parameters.
     *
     * @param glyphNode The graphics node for this glyph.
     * @param unicode The unicode char or chars that this glpyh represents.
     * @param names The list of names for this glyph.
     * @param orientation Indicates what inline-progression-direction this glyph
     * can be used in. Should be either "h" for horizontal only, "v" for vertical
     * only, or empty which indicates that the glpyh can be use in both.
     * @param arabicForm
     * @param lang
     * @param horizOrigin
     * @param vertOrigin
     * @param horizAdvX
     * @param vertAdvY
     * @param glyphCode
     * @param kernScale
     */
    public Glyph(GraphicsNode glyphNode, String unicode, Vector names,
                 String orientation, String arabicForm, String lang,
                 Point2D horizOrigin, Point2D vertOrigin, float horizAdvX,
                 float vertAdvY, int glyphCode, float kernScale) {

        if (glyphNode == null) {
            throw new IllegalArgumentException();
        }
        if (unicode == null) {
            throw new IllegalArgumentException();
        }
        if (horizOrigin == null) {
            throw new IllegalArgumentException();
        }
        if (vertOrigin == null) {
            throw new IllegalArgumentException();
        }

        this.glyphNode = glyphNode;
        this.unicode = unicode;
        this.names = names;
        this.orientation = orientation;
        this.arabicForm = arabicForm;
        this.lang = lang;
        this.horizOrigin = horizOrigin;
        this.vertOrigin = vertOrigin;
        this.horizAdvX = horizAdvX;
        this.vertAdvY = vertAdvY;
        if (this.unicode != null) {
            if (this.unicode.length() > 0
                && ArabicTextHandler.arabicCharTransparent(this.unicode.charAt(0))) {
                // if this glyph is arabic and transparent,
                // then it doesn't cause any advance
                this.horizAdvX = 0;
                this.vertAdvY = 0;
            }
        }
        this.kernScale = kernScale;
        this.glyphCode = glyphCode;
        this.position = new Point2D.Float(0,0);
        this.outline = null;
    }

    /**
     * Returns the graphics node associated with this glyph.
     *
     * @return The glyph graphics node.
     */
    public GraphicsNode getGlyphNode() {
        return glyphNode;
    }

    /**
     * Returns the unicode char or chars this glyph represents.
     *
     * @return The glyphs unicode value.
     */
    public String getUnicode() {
        return unicode;
    }

    /**
     * Returns the names of this glyph.
     *
     * @return The glyph names.
     */
    public Vector getNames() {
        return names;
    }

    /**
     * Returns the orientation of this glyph.
     * Indicates what inline-progression-direction this glyph
     * can be used in. Should be either "h" for horizontal only, "v" for vertical
     * only, or empty which indicates that the glpyh can be use in both.
     *
     * @return The glyph orientation.
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Returns which of the four possible arabic forms this glyph represents.
     * This is only used for arabic glyphs.
     *
     * @return The glyphs arabic form.
     */
    public String getArabicForm() {
        return arabicForm;
    }

    /**
     * Returns a comma separated list of languages this glyph can be used in.
     *
     * @return The glyph languages.
     */
    public String getLang() {
        return lang;
    }

    /**
     * Returns the horizontal origin of this glyph.
     *
     * @return The horizontal origin.
     */
    public Point2D getHorizOrigin() {
        return horizOrigin;
    }

    /**
     * Returns the vertical origin of this glyph.
     *
     * @return The vertical origin.
     */
    public Point2D getVertOrigin() {
        return vertOrigin;
    }

    /**
     * Returns the horizontal advance value.
     *
     * @return This glyph's horizontal advance.
     */
    public float getHorizAdvX() {
        return horizAdvX;
    }

    /**
     * Returns the vertical advance value.
     *
     * @return the glyph's vertical advance.
     */
    public float getVertAdvY() {
        return vertAdvY;
    }

    /**
     * Returns the glyphs unique code with resect to its font. This will be
     * the index into the font's list of glyphs.
     *
     * @return The glyph's unique code.
     */
    public int getGlyphCode() {
        return glyphCode;
    }

    /**
     * Returns the glpyh's transform.
     *
     * @return The glyph's transform.
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * Sets the transform to be applied to this glyph.
     *
     * @param The transform to set.
     */
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
        outline = null;
    }

    /**
     * Returns the position of this glyph.
     *
     * @return The glyph's position.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Sets the position of the glyph.
     *
     * @param position The new glyph position.
     */
    public void setPosition(Point2D position) {
        this.position = position;
        outline = null;
    }

    /**
     * Returns the metrics of this Glyph if it is used in a horizontal layout.
     *
     * @return The glyph metrics.
     */
    public GVTGlyphMetrics getGlyphMetrics() {
        if (metrics == null) {
            metrics = new GVTGlyphMetrics(getHorizAdvX(), getVertAdvY(),
                                          glyphNode.getOutline(null).getBounds2D(),
                                          GlyphMetrics.COMPONENT);
        }
        return metrics;
    }


    /**
     * Returns the metics of this Glyph with the specified kerning value applied.
     *
     * @param kern The kerning value to apply when calculating the glyph metrics.
     * @return The kerned glyph metics.
     */
    public GVTGlyphMetrics getGlyphMetrics(float hkern, float vkern) {
        return new GVTGlyphMetrics(getHorizAdvX() - (hkern * kernScale),
                                   getVertAdvY() - (vkern * kernScale),
                                   glyphNode.getOutline(null).getBounds2D(),
                                   GlyphMetrics.COMPONENT);

    }


    /**
     * Returns the outline of this glyph. This will be positioned correctly and
     * any glyph transforms will have been applied.
     *
     * @return the outline of this glyph.
     */
    public Shape getOutline() {
        if (outline == null) {
            AffineTransform tr = AffineTransform.getTranslateInstance(position.getX(), position.getY());
            if (transform != null) {
                tr.concatenate(transform);
            }
            Shape glyphOutline = glyphNode.getOutline(null);
            AffineTransform glyphNodeTransform = glyphNode.getTransform();
            if (glyphNodeTransform != null) {
                glyphOutline = glyphNodeTransform.createTransformedShape(glyphOutline);
            }
            outline = tr.createTransformedShape(glyphOutline);
        }
        return outline;
    }

    /**
     * Draws this glyph.
     *
     * @param graphics2D The Graphics2D object to draw to.
     * @param context The current rendering context.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context) {
        AffineTransform tr = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        if (transform != null) {
            tr.concatenate(transform);
        }
        glyphNode.setTransform(tr);
        glyphNode.paint(graphics2D, context);
    }
}

