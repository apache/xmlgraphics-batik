/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This class provides an adapter for TransformListHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultTransformListHandler implements TransformListHandler {

    /**
     * The only instance of this class.
     */
    public final static TransformListHandler INSTANCE
        = new DefaultTransformListHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultTransformListHandler() {
    }

    /**
     * Implements {@link TransformListHandler#startTransformList()}.
     */
    public void startTransformList() throws ParseException {
    }

    /**
     * Implements {@link
     * TransformListHandler#matrix(float,float,float,float,float,float)}.
     */
    public void matrix(float a, float b, float c, float d, float e, float f)
	throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#rotate(float)}.
     */
    public void rotate(float theta) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#rotate(float,float,float)}.
     */
    public void rotate(float theta, float cx, float cy) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#translate(float)}.
     */
    public void translate(float tx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#translate(float,float)}.
     */
    public void translate(float tx, float ty) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#scale(float)}.
     */
    public void scale(float sx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#scale(float,float)}.
     */
    public void scale(float sx, float sy) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#skewX(float)}.
     */
    public void skewX(float skx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#skewY(float)}.
     */
    public void skewY(float sky) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#endTransformList()}.
     */
    public void endTransformList() throws ParseException {
    }
}
