/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.TextNode;
import java.awt.font.FontRenderContext;

/**
 * Class that encapsulates information returned from hit testing
 * a <tt>TextSpanLayout</tt> instance.
 * @see org.apache.batik.gvt.text.TextSpanLayout.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class TextHit {

    private int glyphIndex;
    private boolean leadingEdge;
    private TextNode textNode;
    private FontRenderContext frc;

    public TextHit(int glyphIndex, boolean leadingEdge) {
        this.glyphIndex = glyphIndex;
        this.leadingEdge = leadingEdge;
    }

    public int getGlyphIndex() {
        return glyphIndex;
    }

    public boolean isLeadingEdge() {
        return leadingEdge;
    }

    public int getInsertionIndex() {
        int i = getGlyphIndex();
        if (!leadingEdge) {
            ++i;
        }
        return i;
    }

    public void setTextNode(TextNode textNode) {
        this.textNode = textNode;
    }
    public void setFontRenderContext(FontRenderContext frc) {
        this.frc = frc;
    }

    public TextNode getTextNode() {
        return textNode;
    }
    public FontRenderContext getFontRenderContext() {
        return frc;
    }
}

