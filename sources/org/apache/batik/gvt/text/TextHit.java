/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

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

    private int charIndex;
    private boolean leadingEdge;
    private TextNode textNode;
    private FontRenderContext frc;

    public TextHit(int charIndex, boolean leadingEdge) {
        this.charIndex = charIndex;
        this.leadingEdge = leadingEdge;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public boolean isLeadingEdge() {
        return leadingEdge;
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

