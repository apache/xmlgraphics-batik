/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.apache.batik.css.engine.value.Value;

import org.w3c.dom.Element;

/**
 * This interface allows the user of a CSSEngine to provide contextual
 * informations.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSContext {
    
    /**
     * Returns the Value corresponding to the given system color.
     */
    Value getSystemColor(String ident);

    /**
     * Returns a lighter font-weight.
     */
    float getLighterFontWeight(float f);

    /**
     * Returns a bolder font-weight.
     */
    float getBolderFontWeight(float f);

    /**
     * Returns the pixel to millimeters conversion factor.
     */
    float getPixelToMillimeters();

    /**
     * Returns the medium font size.
     */
    float getMediumFontSize();

    /**
     * Returns the width of the block which directly contains the
     * given element.
     */
    float getBlockWidth(Element elt);

    /**
     * Returns the height of the block which directly contains the
     * given element.
     */
    float getBlockHeight(Element elt);

}
