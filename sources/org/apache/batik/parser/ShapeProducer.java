/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.awt.Shape;

/**
 * This interface represents objects which creates Shape objects.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ShapeProducer {
    /**
     * Returns the Shape object initialized during the last parsing.
     * @return the shape or null if this handler has not been used to
     *         parse a path.
     */
    Shape getShape();

    /**
     * Sets the winding rule used to construct the path.
     */
    void setWindingRule(int i);

    /**
     * Returns the current winding rule.
     */
    int getWindingRule();
}
