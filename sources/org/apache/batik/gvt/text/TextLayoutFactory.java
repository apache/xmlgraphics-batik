/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.awt.font.FontRenderContext;

/**
 * Interface implemented by factory instances that can return
 * TextSpanLayouts appropriate to AttributedCharacterIterator
 * instances.
 *
 * @see org.apache.batik.gvt.text.TextSpanLayout
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public interface TextLayoutFactory {

    /**
     * Returns an instance of TextSpanLayout suitable for rendering the
     * AttributedCharacterIterator.
     * @param aci the character iterator to be laid out
     * @param frc the rendering context for the fonts used.
     */
    TextSpanLayout createTextLayout(AttributedCharacterIterator aci,
				    Point2D offset,
				    FontRenderContext frc);

}
