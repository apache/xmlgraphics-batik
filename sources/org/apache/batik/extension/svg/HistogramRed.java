/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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

