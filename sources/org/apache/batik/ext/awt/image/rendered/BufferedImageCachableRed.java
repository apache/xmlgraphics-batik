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


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;
/**
 * This implements CachableRed based on a BufferedImage.
 * You can use this to wrap a BufferedImage that you want to
 * appear as a CachableRed.
 * It essentially ignores the dependency and dirty region methods.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class BufferedImageCachableRed extends AbstractRed {
    // The bufferedImage that we wrap...
    BufferedImage bi;

    /**
     * Construct an instance of CachableRed around a BufferedImage.
     */
    public BufferedImageCachableRed(BufferedImage bi) {
        super((CachableRed)null, 
              new Rectangle(bi.getMinX(),  bi.getMinY(),
                            bi.getWidth(), bi.getHeight()),
              bi.getColorModel(), bi.getSampleModel(), 
              bi.getMinX(), bi.getMinY(), null);

        this.bi = bi;
    }

    public BufferedImageCachableRed(BufferedImage bi, 
                                            int xloc, int yloc) {
        super((CachableRed)null, new Rectangle(xloc,  yloc,
                                               bi.getWidth(), 
                                               bi.getHeight()),
              bi.getColorModel(), bi.getSampleModel(), xloc, yloc, null);

        this.bi = bi;
    }

    public Rectangle getBounds() {
        return new Rectangle(getMinX(),
                             getMinY(),
                             getWidth(),
                             getHeight());
    }

    /**
     * fetch the bufferedImage from this node.
     */
    public BufferedImage getBufferedImage() {
        return bi;
    }

    public Object getProperty(String name) {
        return bi.getProperty(name);
    }

    public String [] getPropertyNames() {
        return bi.getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        return bi.getTile(tileX,tileY);
    }

    public Raster getData() {
        Raster r = bi.getData();
        return r.createTranslatedChild(getMinX(), getMinY());
    }

    public Raster getData(Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();

        if (r.intersects(getBounds()) == false)
            return null;
        r = r.intersection(getBounds());
        r.translate(-getMinX(), - getMinY());

        Raster ret = bi.getData(r);
        return ret.createTranslatedChild(ret.getMinX()+getMinX(), 
                                         ret.getMinY()+getMinY());
    }

    public WritableRaster copyData(WritableRaster wr) {
        WritableRaster wr2 = wr.createWritableTranslatedChild
            (wr.getMinX()-getMinX(),
             wr.getMinY()-getMinY());

        GraphicsUtil.copyData(bi.getRaster(), wr2);

        /* This was the original code. This is _bad_ since it causes a
         * multiply and divide of the alpha channel to do the draw
         * operation.  I believe that at some point I switched to
         * drawImage in order to avoid some issues with
         * BufferedImage's copyData implementation but I can't
         * reproduce them now. Anyway I'm now using GraphicsUtil which
         * should generally be as fast if not faster...
         */
        /*
          BufferedImage dest;
         dest = new BufferedImage(bi.getColorModel(), 
                                  wr.createWritableTranslatedChild(0,0), 
                                  bi.getColorModel().isAlphaPremultiplied(), 
                                  null);
         java.awt.Graphics2D g2d = dest.createGraphics();
         g2d.drawImage(bi, null, getMinX()-wr.getMinX(), 
                       getMinY()-wr.getMinY());
         g2d.dispose(); 
         */
        return wr;
    }
}
