/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.text;

import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.TextLayoutFactory;

/**
 * Factory instance that returns
 * TextSpanLayouts appropriate to AttributedCharacterIterator
 * instances.
 *
 * @see org.apache.batik.gvt.text.TextSpanLayout
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class ConcreteTextLayoutFactory implements TextLayoutFactory {

    /**
     * Returns an instance of TextSpanLayout suitable for rendering the
     * AttributedCharacterIterator.
     * @param aci the character iterator to be laid out
     */
    public TextSpanLayout createTextLayout(AttributedCharacterIterator aci, 
                                                FontRenderContext frc) {
        return new TextLayoutAdapter(new TextLayout(aci, frc));
    }
}
