/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

/**
 * The class provides an adapter for PathHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultPathHandler implements PathHandler {
    /**
     * The only instance of this class.
     */
    public final static PathHandler INSTANCE
        = new DefaultPathHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultPathHandler() {
    }

    /**
     * Implements {@link PathHandler#startPath()}.
     */
    public void startPath() throws ParseException {
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
    }

    /**
     * Implements {@link PathHandler#movetoAbs(float,float)}.
     */
    public void movetoAbs(float x, float y) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#closePath()}.
     */
    public void closePath() throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoRel(float,float)}.
     */
    public void linetoRel(float x, float y) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoAbs(float,float)}.
     */
    public void linetoAbs(float x, float y) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoHorizontalRel(float)}.
     */
    public void linetoHorizontalRel(float x) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoHorizontalAbs(float)}.
     */
    public void linetoHorizontalAbs(float x) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoVerticalRel(float)}.
     */
    public void linetoVerticalRel(float y) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#linetoVerticalAbs(float)}.
     */
    public void linetoVerticalAbs(float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicRel(float,float,float,float,float,float)}.
     */
    public void curvetoCubicRel(float x1, float y1, 
				float x2, float y2, 
				float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicAbs(float,float,float,float,float,float)}.
     */
    public void curvetoCubicAbs(float x1, float y1, 
				float x2, float y2, 
				float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicSmoothRel(float,float,float,float)}.
     */
    public void curvetoCubicSmoothRel(float x2, float y2, 
				      float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoCubicSmoothAbs(float,float,float,float)}.
     */
    public void curvetoCubicSmoothAbs(float x2, float y2, 
				      float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoQuadraticRel(float,float,float,float)}.
     */
    public void curvetoQuadraticRel(float x1, float y1, 
				    float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#curvetoQuadraticAbs(float,float,float,float)}.
     */
    public void curvetoQuadraticAbs(float x1, float y1, 
				    float x, float y) throws ParseException {
    }

    /**
     * Implements {@link PathHandler#curvetoQuadraticSmoothRel(float,float)}.
     */
    public void curvetoQuadraticSmoothRel(float x, float y)
        throws ParseException {
    }

    /**
     * Implements {@link PathHandler#curvetoQuadraticSmoothAbs(float,float)}.
     */
    public void curvetoQuadraticSmoothAbs(float x, float y)
        throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#arcRel(float,float,float,boolean,boolean,float,float)}.
     */
    public void arcRel(float rx, float ry, 
		       float xAxisRotation, 
		       boolean largeArcFlag, boolean sweepFlag, 
		       float x, float y) throws ParseException {
    }

    /**
     * Implements {@link
     * PathHandler#arcAbs(float,float,float,boolean,boolean,float,float)}.
     */
    public void arcAbs(float rx, float ry, 
		       float xAxisRotation, 
		       boolean largeArcFlag, boolean sweepFlag, 
		       float x, float y) throws ParseException {
    }
}
