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

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * This implementation of RenderedImage will generate an infinate
 * field of a single color.  It reports bounds but will in fact render
 * out to infinity.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$ 
 */
public class FloodRed extends AbstractRed {

    /**
     * A single tile that we move around as needed...
     */
    private WritableRaster raster;

    /**
     * Construct a fully transparent black image <tt>bounds</tt> size.
     * @param bounds the bounds of the image (in fact will respond with
     *               any request).
     */
    public FloodRed(Rectangle bounds) {
        this(bounds, new Color(0, 0, 0, 0));
    }

    /**
     * Construct a fully transparent image <tt>bounds</tt> size, will
     * paint one tile with paint.  Thus paint should not be a pattered
     * paint or gradient but should be a solid color.
     * @param bounds the bounds of the image (in fact will respond with
     *               any request).  
     */
    public FloodRed(Rectangle bounds,
                    Paint paint) {
        super(); // We _must_ call init...

        ColorModel cm = GraphicsUtil.sRGB_Unpre;
        
        int defSz = AbstractTiledRed.getDefaultTileSize();

        int tw = bounds.width;
        if (tw > defSz) tw = defSz;
        int th = bounds.height;
        if (th > defSz) th = defSz;

        // fix my sample model so it makes sense given my size.
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);

        // Finish initializing our base class...
        init((CachableRed)null, bounds, cm, sm, 0, 0, null);

        raster = Raster.createWritableRaster(sm, new Point(0, 0));
        BufferedImage offScreen = new BufferedImage(cm, raster,
                                                    cm.isAlphaPremultiplied(),
                                                    null);

        Graphics2D g = GraphicsUtil.createGraphics(offScreen);
        g.setPaint(paint);
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.dispose();
    }

    public Raster getTile(int x, int y) {
        // We have a Single raster that we translate where needed
        // position.  So just offest appropriately.
        int tx = tileGridXOff+x*tileWidth;
        int ty = tileGridYOff+y*tileHeight;
        return raster.createTranslatedChild(tx, ty);
    }

    public WritableRaster copyData(WritableRaster wr) {
        int tx0 = getXTile(wr.getMinX());
        int ty0 = getYTile(wr.getMinY());
        int tx1 = getXTile(wr.getMinX()+wr.getWidth() -1);
        int ty1 = getYTile(wr.getMinY()+wr.getHeight()-1);

        final boolean is_INT_PACK = 
            GraphicsUtil.is_INT_PACK_Data(getSampleModel(), false);

        for (int y=ty0; y<=ty1; y++)
            for (int x=tx0; x<=tx1; x++) {
                Raster r = getTile(x, y);
                if (is_INT_PACK)
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                else
                    GraphicsUtil.copyData_FALLBACK(r, wr);
            }

        return wr;
    }
}




