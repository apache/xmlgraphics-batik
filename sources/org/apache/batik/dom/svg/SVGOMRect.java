/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGRect;

public class SVGOMRect implements SVGRect{
    float x;
    float y;
    float w;
    float h;
    public SVGOMRect() { x = y = w = h = 0; }
    public SVGOMRect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public float getX( ) { return x; }
    public void  setX( float x ) throws DOMException { this.x = x; }
    public float getY( ) { return y; }
    public void  setY( float y ) throws DOMException { this.y = y; }
    public float getWidth( ) { return w; }
    public void  setWidth( float width ) throws DOMException { this.w = w; }
    public float getHeight( ) { return h; }
    public void  setHeight( float height ) throws DOMException { this.h = h; }
}
