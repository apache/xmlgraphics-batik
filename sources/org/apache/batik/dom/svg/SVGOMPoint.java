/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

/**
 * This class provides an abstract implementation of the {@link SVGMatrix}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMPoint implements SVGPoint {
    float x, y;
    public SVGOMPoint() { x=0; y=0; }
    public SVGOMPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX( )                             { return x; }
    public void  setX( float x ) throws DOMException { this.x = x; }
    public float getY( )                             { return y; }
    public void  setY( float y ) throws DOMException { this.y = y; }

    public SVGPoint matrixTransform ( SVGMatrix matrix ) {
        float newX = matrix.getA()*x + matrix.getC()*y + matrix.getE();
        float newY = matrix.getB()*x + matrix.getD()*y + matrix.getF();
        return new SVGOMPoint(newX, newY);
    }
}
