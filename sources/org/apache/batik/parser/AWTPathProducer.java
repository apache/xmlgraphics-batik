/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Reader;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;

/**
 * This class provides an implementation of the PathHandler that initializes
 * a Shape from the value of a path's 'd' attribute.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AWTPathProducer implements PathHandler, ShapeProducer {
    /**
     * The temporary value of extendedGeneralPath.
     */
    protected ExtendedGeneralPath path;

    /**
     * The current x position.
     */
    protected float currentX;

    /**
     * The current y position.
     */
    protected float currentY;

    /**
     * The reference x point for smooth arcs.
     */
    protected float xCenter;

    /**
     * The reference y point for smooth arcs.
     */
    protected float yCenter;

    /**
     * The winding rule to use to construct the path.
     */
    protected int windingRule;

    /**
     * Utility method for creating an ExtendedGeneralPath.
     * @param r The reader used to read the path specification.
     * @param wr The winding rule to use for creating the path.
     * @param pf The parser factory to use.
     */
    public static Shape createShape(Reader r, int wr, ParserFactory pf)
        throws IOException,
               ParseException {
        PathParser p = pf.createPathParser();
        AWTPathProducer ph = new AWTPathProducer();

        ph.setWindingRule(wr);
        p.setPathHandler(ph);
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
     * Implements {@link PathHandler#startPath()}.
     */
    public void startPath() throws ParseException {
        currentX = 0;
        currentY = 0;
        xCenter = 0;
        yCenter = 0;
        path = new ExtendedGeneralPath(windingRule);
    }

    /**
     * Implements {@link PathHandler#endPath()}.
     */
    public void endPath() throws ParseException {
    }

    /**
     * Implements {@link PathHandler#movetoRel(float,float)}.
     */
    public void movetoRel(float x, float y) throws ParseException {
        path.moveTo(xCenter = currentX += x, yCenter = currentY += y);
    }

    /**
     * Implements {@link PathHandler#movetoAbs(float,float)}.
     */
    public void movetoAbs(float x, float y) throws ParseException {
        path.moveTo(xCenter = currentX = x, yCenter = currentY = y);
    }

    /**
     * Implements {@link PathHandler#closePath()}.
     */
    public void closePath() throws ParseException {
        path.closePath();
        Point2D pt = path.getCurrentPoint();
        currentX = (float)pt.getX();
        currentY = (float)pt.getY();
    }

    /**
     * Implements {@link PathHandler#linetoRel(float,float)}.
     */
    public void linetoRel(float x, float y) throws ParseException {
        path.lineTo(xCenter = currentX += x, yCenter = currentY += y);
    }

    /**
     * Implements {@link PathHandler#linetoAbs(float,float)}.
     */
    public void linetoAbs(float x, float y) throws ParseException {
        path.lineTo(xCenter = currentX = x, yCenter = currentY = y);
    }

    /**
     * Implements {@link PathHandler#linetoHorizontalRel(float)}.
     */
    public void linetoHorizontalRel(float x) throws ParseException {
        path.lineTo(xCenter = currentX += x, yCenter = currentY);
    }

    /**
     * Implements {@link PathHandler#linetoHorizontalAbs(float)}.
     */
    public void linetoHorizontalAbs(float x) throws ParseException {
        path.lineTo(xCenter = currentX = x, yCenter = currentY);
    }

    /**
     * Implements {@link PathHandler#linetoVerticalRel(float)}.
     */
    public void linetoVerticalRel(float y) throws ParseException {
        path.lineTo(xCenter = currentX, yCenter = currentY += y);
    }

    /**
     * Implements {@link PathHandler#linetoVerticalAbs(float)}.
     */
    public void linetoVerticalAbs(float y) throws ParseException {
        path.lineTo(xCenter = currentX, yCenter = currentY = y);
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicRel(float,float,float,float,float,float)}.
     */
    public void curvetoCubicRel(float x1, float y1,
                                float x2, float y2,
                                float x, float y) throws ParseException {
        path.curveTo(currentX + x1, currentY + y1,
                     xCenter = currentX + x2, yCenter = currentY + y2,
                     currentX += x, currentY += y);
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicAbs(float,float,float,float,float,float)}.
     */
    public void curvetoCubicAbs(float x1, float y1,
                                float x2, float y2,
                                float x, float y) throws ParseException {
        path.curveTo(x1, y1, xCenter = x2, yCenter = y2, currentX = x,
                     currentY = y);
    }

    /**
     * Implements
     * {@link PathHandler#curvetoCubicSmoothRel(float,float,float,float)}.
     */
    public void curvetoCubicSmoothRel(float x2, float y2,
                                      float x, float y) throws ParseException {
        path.curveTo(currentX * 2 - xCenter,
                     currentY * 2 - yCenter,
                     xCenter = currentX + x2,
                     yCenter = currentY + y2,
                     currentX += x,
                     currentY += y);
    }

    /**
     * Implements
     * {@link PathHandler#curvetoCubicSmoothAbs(float,float,float,float)}.
     */
    public void curvetoCubicSmoothAbs(float x2, float y2,
                                      float x, float y) throws ParseException {
        path.curveTo(currentX * 2 - xCenter,
                     currentY * 2 - yCenter,
                     xCenter = x2,
                     yCenter = y2,
                     currentX = x,
                     currentY = y);
    }

    /**
     * Implements
     * {@link PathHandler#curvetoQuadraticRel(float,float,float,float)}.
     */
    public void curvetoQuadraticRel(float x1, float y1,
                                    float x, float y) throws ParseException {
        path.quadTo(xCenter = currentX + x1, yCenter = currentY + y1,
                    currentX += x, currentY += y);
    }

    /**
     * Implements
     * {@link PathHandler#curvetoQuadraticAbs(float,float,float,float)}.
     */
    public void curvetoQuadraticAbs(float x1, float y1,
                                    float x, float y) throws ParseException {
        path.quadTo(xCenter = x1, yCenter = y1, currentX = x, currentY = y);
    }

    /**
     * Implements {@link PathHandler#curvetoQuadraticSmoothRel(float,float)}.
     */
    public void curvetoQuadraticSmoothRel(float x, float y)
        throws ParseException {
        path.quadTo(xCenter = currentX * 2 - xCenter,
                    yCenter = currentY * 2 - yCenter,
                    currentX += x,
                    currentY += y);
    }

    /**
     * Implements {@link PathHandler#curvetoQuadraticSmoothAbs(float,float)}.
     */
    public void curvetoQuadraticSmoothAbs(float x, float y)
        throws ParseException {
        path.quadTo(xCenter = currentX * 2 - xCenter,
                    yCenter = currentY * 2 - yCenter,
                    currentX = x,
                    currentY = y);
    }

    /**
     * Implements {@link
     * PathHandler#arcRel(float,float,float,boolean,boolean,float,float)}.
     */
    public void arcRel(float rx, float ry,
                       float xAxisRotation,
                       boolean largeArcFlag, boolean sweepFlag,
                       float x, float y) throws ParseException {
        path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag,
                   xCenter = currentX += x, yCenter = currentY += y);
    }

    /**
     * Implements {@link
     * PathHandler#arcAbs(float,float,float,boolean,boolean,float,float)}.
     */
    public void arcAbs(float rx, float ry,
                       float xAxisRotation,
                       boolean largeArcFlag, boolean sweepFlag,
                       float x, float y) throws ParseException {
        path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag,
                   xCenter = currentX = x, yCenter = currentY = y);
    }
}
