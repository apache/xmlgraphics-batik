/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTGlyphVector;

public class LineInfo {
    AttributedCharacterIterator aci;
    GVTGlyphVector              gv;
    int                         startIdx;
    int                         endIdx;
    Point2D.Float               loc;
    float                       advance;
    float                       offset;
    float                       lastCharWidth;
    float                       lineWidth;
    boolean                     partial;

    public LineInfo(AttributedCharacterIterator aci,
                    GVTGlyphVector gv,
                    int startIdx, int endIdx,
                    Point2D.Float loc,
                    float advance,
                    float offset,
                    float lastCharWidth,
                    float lineWidth,
                    boolean partial) {
        this.aci           = aci;
        this.gv            = gv;
        this.startIdx      = startIdx;
        this.endIdx        = endIdx;
        this.loc           = loc;
        this.advance       = advance;
        this.offset        = offset;
        this.lastCharWidth = lastCharWidth;
        this.lineWidth     = lineWidth;
        this.partial       = partial;
    }
                        
    public AttributedCharacterIterator getACI() { return aci; }
    public GVTGlyphVector getGlyphVector()      { return gv; }
    public int            getStartIdx()         { return startIdx; }
    public int            getEndIdx()           { return endIdx; }
    public Point2D.Float  getLocation()         { return loc; }
    public float          getAdvance()          { return advance; }
    public float          getOffset()           { return offset; }
    public float          getLastCharWidth()    { return lastCharWidth; }
    public float          getLineWidth()        { return lineWidth; }
    public boolean        isPartialLine()       { return partial; }
}
