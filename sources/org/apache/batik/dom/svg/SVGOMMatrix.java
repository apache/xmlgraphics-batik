/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;

/**
 * This class provides an implementation of the {@link SVGMatrix}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMMatrix extends AbstractSVGMatrix {
    
    /**
     * The AffineTransform used to implement the matrix.
     */
    protected AffineTransform affineTransform;

    /**
     * Creates a new SVGMatrix.
     */
    public SVGOMMatrix(AffineTransform at) {
        affineTransform = at;
    }

    /**
     * Returns the associated AffineTransform.
     */
    protected AffineTransform getAffineTransform() {
        return affineTransform;
    }
}
