/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

public class MarginInfo {
    public final static int JUSTIFY_START  = 0;
    public final static int JUSTIFY_MIDDLE = 1;
    public final static int JUSTIFY_END    = 2;
    public final static int JUSTIFY_FULL   = 3;

    protected float top;
    protected float right;
    protected float bottom;
    protected float left;
    protected int   justification;

    public MarginInfo(float top, float right, float bottom, float left,
                      int justification) {
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.left   = left;
        this.justification = justification;
    }

    public MarginInfo(float margin, int justification) {
        setMargin(margin);
    }

    public void setMargin(float margin) {
        this.top    = margin;
        this.right  = margin;
        this.bottom = margin;
        this.left   = margin;
    }
    public float getTopMargin()     { return top; }
    public float getRightMargin()   { return right; }
    public float getBottomMargin()  { return bottom; }
    public float getLeftMargin()    { return left; }
    public int   getJustification() { return justification; }
}
