/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.util.Set;

/**
 * Defines the interface for classes that are able to convert
 * a BufferedImageOp filter
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see           org.apache.batik.svggen.SVGFilterDescriptor
 */
public interface SVGFilterConverter extends SVGSyntax{
    /**
     * Converts a Java 2D API BufferedImageOp into
     * a set of attribute/value pairs and related definitions
     *
     * @param op BufferedImageOp filter to be converted
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return descriptor of the attributes required to represent
     *         the input filter
     * @see org.apache.batik.svggen.SVGFilterDescriptor
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter, Rectangle filterRect);

    /**
     * @return set of definitions referenced by the attribute
     *         values created by the implementation since its
     *         creation. The return value should never be null.
     *         If no definition is needed, an empty set should be
     *         returned.
     */
    public Set getDefinitionSet();
}
