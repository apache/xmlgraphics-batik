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

    protected float   top;
    protected float   right;
    protected float   bottom;
    protected float   left;

    protected float   firstLineLeft;
    protected float   firstLineRight;

    protected int     justification;
    protected boolean flowRegionBreak;


    public MarginInfo(float top, float right, float bottom, float left,
                      float firstLineLeft, float firstLineRight,
                      int justification, boolean flowRegionBreak) {
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.left   = left;

        this.firstLineLeft = firstLineLeft;
        this.firstLineRight = firstLineRight;

        this.justification = justification;
        this.flowRegionBreak = flowRegionBreak;
    }

    public MarginInfo(float margin, int justification) {
        setMargin(margin);
        this.justification = justification;
        this.flowRegionBreak = false;
    }

    public void setMargin(float margin) {
        this.top    = margin;
        this.right  = margin;
        this.bottom = margin;
        this.left   = margin;
        this.firstLineLeft  = margin;
        this.firstLineRight = margin;
    }
    public float   getTopMargin()            { return top; }
    public float   getRightMargin()          { return right; }
    public float   getBottomMargin()         { return bottom; }
    public float   getLeftMargin()           { return left; }

    public float   getFirstLineLeftMargin()  { return firstLineLeft; }
    public float   getFirstLineRightMargin() { return firstLineLeft; }

    public int     getJustification()        { return justification; }
    public boolean isFlowRegionBreak()       { return flowRegionBreak; }
}
