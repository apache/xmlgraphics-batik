/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface must be implemented and then registred as the
 * handler of a <code>TransformParser</code> instance in order to
 * be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface TransformListHandler {
    /**
     * Invoked when the tranform starts.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void startTransformList() throws ParseException;

    /**
     * Invoked when 'matrix(a, b, c, d, e, f)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void matrix(float a, float b, float c, float d, float e, float f)
	throws ParseException;

    /**
     * Invoked when 'rotate(theta)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void rotate(float theta) throws ParseException;

    /**
     * Invoked when 'rotate(theta, cx, cy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void rotate(float theta, float cx, float cy) throws ParseException;

    /**
     * Invoked when 'translate(tx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void translate(float tx) throws ParseException;

    /**
     * Invoked when 'translate(tx, ty)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void translate(float tx, float ty) throws ParseException;

    /**
     * Invoked when 'scale(sx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void scale(float sx) throws ParseException;

    /**
     * Invoked when 'scale(sx, sy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void scale(float sx, float sy) throws ParseException;

    /**
     * Invoked when 'skewX(skx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void skewX(float skx) throws ParseException;

    /**
     * Invoked when 'skewY(sky)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
    void skewY(float sky) throws ParseException;

    /**
     * Invoked when the transform ends.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
    void endTransformList() throws ParseException;
}
