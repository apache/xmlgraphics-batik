/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Bridge class for the &lt;missing-glyph> element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGMissingGlyphElementBridge extends SVGGlyphElementBridge {

    /**
     * Returns 'missing-glyph'.
     */
    public String getLocalName() {
        return SVG_MISSING_GLYPH_TAG;
    }
}
