/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.font.GlyphMetrics;
import java.awt.geom.Rectangle2D;

/**
 * GVTGlyphMetrics is essentially a wrapper class for java.awt.font.GlyphMetrics
 * with the addition of horizontal and vertical advance values.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class GVTGlyphMetrics {


    private GlyphMetrics gm;
    private float verticalAdvance;

    public GVTGlyphMetrics(GlyphMetrics gm, float verticalAdvance) {
        this.gm = gm;
        this.verticalAdvance = verticalAdvance;
    }


    public GVTGlyphMetrics(float horizontalAdvance, float verticalAdvance,
                           Rectangle2D bounds, byte glyphType) {
        this.gm = new GlyphMetrics(horizontalAdvance, bounds, glyphType);
        this.verticalAdvance = verticalAdvance;
    }

    public float getHorizontalAdvance() {
        return gm.getAdvance();
    }

    public float getVerticalAdvance() {
        return verticalAdvance;
    }

    public Rectangle2D getBounds2D() {
        return gm.getBounds2D();
    }

    public float getLSB() {
        return gm.getLSB();
    }

    public float getRSB() {
        return gm.getRSB();
    }

    public int getType() {
        return gm.getType();
    }

    public boolean isCombining() {
        return gm.isCombining();
    }

    public boolean isComponent() {
        return gm.isComponent();
    }

    public boolean isLigature() {
        return gm.isLigature();
    }

    public boolean isStandard() {
        return gm.isStandard();
    }

    public boolean isWhitespace() {
        return gm.isWhitespace();
    }

}