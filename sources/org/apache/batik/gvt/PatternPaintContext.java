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

package org.apache.batik.gvt;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.TileRable;
import org.apache.batik.ext.awt.image.renderable.TileRable8Bit;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;

/**
 * <tt>PaintContext</tt> for the <tt>ConcretePatterPaint</tt>
 * paint implementation.
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PatternPaintContext implements PaintContext {

    /**
     * ColorModel for the Rasters created by this Paint
     */
    private ColorModel rasterCM;

    /**
     * Working Raster
     */
    private WritableRaster raster;

    /**
     * Tile
     */
    private RenderedImage tiled;

    protected AffineTransform usr2dev;

    public AffineTransform getUsr2Dev() { return usr2dev; }

    private static Rectangle EVERYTHING = 
        new Rectangle(Integer.MIN_VALUE/4, Integer.MIN_VALUE/4, 
                      Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);

    /**
     * @param destCM     ColorModel that receives the paint data
     * @param usr2dev    user space to device space transform
     * @param hints      RenderingHints
     * @param patternRegion region tiled by this paint. In user space.
     * @param overflow   controls whether the pattern region clips the
     *                   pattern tile
     */
    public PatternPaintContext(ColorModel      destCM,
                               AffineTransform usr2dev,
                               RenderingHints  hints,
                               Filter          tile,
                               Rectangle2D     patternRegion,
                               boolean         overflow) {

        if(usr2dev == null){
            throw new IllegalArgumentException();
        }

        if(hints == null){
            hints = new RenderingHints(null);
        }

        if(tile == null){
            throw new IllegalArgumentException();
        }

        this.usr2dev    = usr2dev;

        // System.out.println("PatB: " + patternRegion);
        // System.out.println("Tile: " + tile);

        TileRable tileRable = new TileRable8Bit(tile,
                                                EVERYTHING,
                                                patternRegion,
                                                overflow);
        ColorSpace destCS = destCM.getColorSpace();
        if (destCS == ColorSpace.getInstance(ColorSpace.CS_sRGB))
            tileRable.setColorSpaceLinear(false);
        else if (destCS == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
            tileRable.setColorSpaceLinear(true);

        RenderContext rc = new RenderContext(usr2dev,  EVERYTHING, hints);
        tiled = tileRable.createRendering(rc);
        // System.out.println("tileRed: " + tiled);
        // org.apache.batik.test.gvt.ImageDisplay.showImage("Tiled: ", tiled);

        //System.out.println("Created rendering");
        if(tiled != null) {
            Rectangle2D devRgn = usr2dev.createTransformedShape
                (patternRegion).getBounds();
            if ((devRgn.getWidth() > 128) ||
                (devRgn.getHeight() > 128))
                tiled = new TileCacheRed(GraphicsUtil.wrap(tiled), 256, 64);
        } else {
            //System.out.println("Tile was null");
            rasterCM = ColorModel.getRGBdefault();
            WritableRaster wr;
            wr = rasterCM.createCompatibleWritableRaster(32, 32);
            tiled = GraphicsUtil.wrap
                (new BufferedImage(rasterCM, wr, false, null));
            return;
        }

        rasterCM = tiled.getColorModel();
        if (rasterCM.hasAlpha()) {
            if (destCM.hasAlpha()) 
                rasterCM = GraphicsUtil.coerceColorModel
                    (rasterCM, destCM.isAlphaPremultiplied());
            else 
                rasterCM = GraphicsUtil.coerceColorModel(rasterCM, false);
        }
    }

    public void dispose(){
        raster = null;
    }

    public ColorModel getColorModel(){
        return rasterCM;
    }

    public Raster getRaster(int x, int y, int width, int height){

        // System.out.println("GetRaster: [" + x + ", " + y + ", " 
        //                    + width + ", " + height + "]");
        if ((raster == null)             ||
            (raster.getWidth() < width)  ||
            (raster.getHeight() < height)) {
            raster = rasterCM.createCompatibleWritableRaster(width, height);
        }

        WritableRaster wr
            = raster.createWritableChild(0, 0, width, height, x, y, null);

        tiled.copyData(wr);
        GraphicsUtil.coerceData(wr, tiled.getColorModel(), 
                                rasterCM.isAlphaPremultiplied());

        // On Mac OS X it always wants the raster at 0,0 if the
        // requested width and height matches raster we can just
        // return it.  Otherwise we create a translated child that
        // lives at 0,0.
        if ((raster.getWidth()  == width) &&
            (raster.getHeight() == height))
            return raster;

        return wr.createTranslatedChild(0,0);
    }
}
