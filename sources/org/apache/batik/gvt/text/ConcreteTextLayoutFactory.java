/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.awt.geom.Point2D;
import java.awt.font.FontRenderContext;

/**
 * Factory instance that returns TextSpanLayouts appropriate to
 * AttributedCharacterIterator instances.
 *
 * @see org.apache.batik.gvt.text.TextSpanLayout
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class ConcreteTextLayoutFactory implements TextLayoutFactory {

    /**
     * Returns an instance of TextSpanLayout suitable for rendering the
     * AttributedCharacterIterator.
     *
     * @param aci The character iterator to be laid out
     * @param offset The offset position for the text layout.
     * @param frc The font render context to use when creating the text layout.
     */
    public TextSpanLayout createTextLayout(AttributedCharacterIterator aci,
                                           Point2D offset,
                                           FontRenderContext frc) {
        return new GlyphLayout(aci, offset, frc);
    }
}


