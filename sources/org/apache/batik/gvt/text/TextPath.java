/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.geom.PathLength;

/**
 * A text path describes a path along which some text will be rendered.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class TextPath {

    private PathLength pathLength;
    private float startOffset;

    /**
     * Constructs a TextPath based on the specified path.
     *
     * @param path The general path along which text is to be laid.
     */
    public TextPath(GeneralPath path) {
        pathLength = new PathLength(path);
        startOffset = 0;
    }

    /**
     * Sets the offset along the path where the first glyph should be rendered.
     *
     * @param startOffset An offset from the start of the path.
     */
    public void setStartOffset(float startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * Returns the start offset of this text path.
     *
     * @return The start offset of this text path.
     */
    public float getStartOffset() {
        return startOffset;
    }

    /**
     * Returns the total length of the path.
     *
     * @return The lenght of the path.
     */
    public float lengthOfPath() {
        return pathLength.lengthOfPath();
    }

    /**
     * Returns the angle at the specified length
     * along the path.
     *
     * @param length The length along the path.
     * @return The angle.
     */
    public float angleAtLength(float length) {
        return pathLength.angleAtLength(length);
    }

    /**
     * Returns the point that is at the specified length
     * along the path.
     *
     * @param length The length along the path.
     * @return The point.
     */
    public Point2D pointAtLength(float length) {
        return pathLength.pointAtLength(length);
    }
}
