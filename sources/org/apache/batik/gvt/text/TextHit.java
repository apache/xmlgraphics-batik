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
 * @see org.apache.batik.gvt.text.TextSpanLayout
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class TextHit {

    private int charIndex;
    private boolean leadingEdge;

    /**
     * Constructs a TextHit with the specified values.
     *
     * @param charIndex The index of the character that has been
     * hit. In the case of bidirectional text this will be the logical
     * character index not the visual index. The index is relative to
     * whole text within the selected TextNode.
     * @param leadingEdge Indicates which side of the character has
     * been hit.  
     */
    public TextHit(int charIndex, boolean leadingEdge) {
        this.charIndex = charIndex;
        this.leadingEdge = leadingEdge;
    }

    /**
     * Returns the index of the character that has been hit.
     *
     * @return The character index.
     */
    public int getCharIndex() {
        return charIndex;
    }

    /**
     * Returns whether on not the character has been hit on its leading edge.
     *
     * @return Whether on not the character has been hit on its leading edge.
     */
    public boolean isLeadingEdge() {
        return leadingEdge;
    }
}

