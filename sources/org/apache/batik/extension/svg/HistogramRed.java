/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

/**
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class HistogramRed extends AbstractRed {

    // This is used to track which tiles we have computed
    // a histogram for.
    boolean [] computed;
    int tallied = 0;

    int [] bins = new int[256];

    public HistogramRed(CachableRed src){
        super(src, null);

        int tiles = getNumXTiles()*getNumYTiles();
        computed = new boolean[tiles];
    }
    
    public void tallyTile(Raster r) {
        final int minX = r.getMinX();
        final int minY = r.getMinY();
        final int w = r.getWidth();
        final int h = r.getHeight();
        
        int [] samples = null;
        int val;
        for (int y=minY; y<minY+h; y++) {
            samples = r.getPixels(minX, y, w, 1, samples);
            for (int x=0; x<3*w; x++) {
                // Simple fixed point conversion to lumincence.
                val  = samples[x++]*5; // Red
                val += samples[x++]*9; // Green
                val += samples[x++]*2; // blue
                bins[val>>4]++;
            }
        }
        tallied++;
    }

    public int [] getHistogram() {
        if (tallied == computed.length)
            return bins;

        CachableRed src = (CachableRed)getSources().elementAt(0);
        int yt0 = src.getMinTileY();

        int xtiles = src.getNumXTiles();
        int xt0 = src.getMinTileX();
        int xt1 = xt0+xtiles-1;
            
        for (int y=0; y<src.getNumYTiles(); y++) {
            for (int x=0; x<xtiles; x++) {
                int idx = (x+xt0)+y*xtiles;
                if (computed[idx]) continue;

                Raster r = src.getTile(x+xt0, y+yt0);
                tallyTile(r);
                computed[idx]=true;
            }
        }
        return bins;
    }

    public WritableRaster copyData(WritableRaster wr) {
        copyToRaster(wr);
        return wr;
    }
    
    public Raster getTile(int tileX, int tileY) {
        int yt = tileY-getMinTileY();
        int xt = tileX-getMinTileX();

        CachableRed src = (CachableRed)getSources().elementAt(0);
        Raster r = src.getTile(tileX, tileY);
        
        int idx = xt+yt*getNumXTiles();

        if (computed[idx]) 
            return r;

        tallyTile(r);
        computed[idx] = true;
        return r;
    }
}

