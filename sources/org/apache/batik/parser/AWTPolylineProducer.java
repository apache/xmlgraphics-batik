/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.Reader;

/**
 * This class produces a polyline shape from a reader.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AWTPolylineProducer implements PointsHandler, ShapeProducer {
    /**
     * The current path.
     */
    protected GeneralPath path;

    /**
     * Is the current path a new one?
     */
    protected boolean newPath;

    /**
     * The winding rule to use to construct the path.
     */
    protected int windingRule;

    /**
     * Utility method for creating an ExtendedGeneralPath.
     * @param r The reader used to read the path specification.
     * @param wr The winding rule to use for creating the path.
     */
    public static Shape createShape(Reader r, int wr)
        throws IOException,
               ParseException {
        PointsParser p = new PointsParser();
        AWTPolylineProducer ph = new AWTPolylineProducer();

        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
        p.parse(r);

        return ph.getShape();
    }

    /**
     * Sets the winding rule used to construct the path.
     */
    public void setWindingRule(int i) {
        windingRule = i;
    }

    /**
     * Returns the current winding rule.
     */
    public int getWindingRule() {
        return windingRule;
    }

    /**
     * Returns the Shape object initialized during the last parsing.
     * @return the shape or null if this handler has not been used by
     *         a parser.
     */
    public Shape getShape() {
        return path;
    }

    /**
     * Implements {@link PointsHandler#startPoints()}.
     */
    public void startPoints() throws ParseException {
        path = new GeneralPath(windingRule);
        newPath = true;
    }

    /**
     * Implements {@link PointsHandler#point(float,float)}.
     */
    public void point(float x, float y) throws ParseException {
        if (newPath) {
            newPath = false;
            path.moveTo(x, y);
        } else {
            path.lineTo(x, y);
        }
    }

    /**
     * Implements {@link PointsHandler#endPoints()}.
     */
    public void endPoints() throws ParseException {
    }
}
