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
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;


/**
 * This implements CachableRed around a RenderedImage.
 * You can use this to wrap a RenderedImage that you want to
 * appear as a CachableRed.
 * It essentially ignores the dependency and dirty region methods.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class RenderedImageCachableRed implements CachableRed {

    public static CachableRed wrap(RenderedImage ri) {
        if (ri instanceof CachableRed)
            return (CachableRed) ri;
        if (ri instanceof BufferedImage)
            return new BufferedImageCachableRed((BufferedImage)ri);
        return new RenderedImageCachableRed(ri);
    }

    private RenderedImage src;
    private Vector srcs = new Vector(0);

    public RenderedImageCachableRed(RenderedImage src) {
        if(src == null){
            throw new IllegalArgumentException();
        }
        this.src = src;
    }

    public Vector getSources() {
        return srcs; // should always be empty...
    }

    public Rectangle getBounds() {
        return new Rectangle(getMinX(),
                             getMinY(),
                             getWidth(),
                             getHeight());
    }

    public int getMinX() {
        return src.getMinX();
    }
    public int getMinY() {
        return src.getMinY();
    }

    public int getWidth() {
        return src.getWidth();
    }
    public int getHeight() {
        return src.getHeight();
    }

    public ColorModel getColorModel() {
        return src.getColorModel();
    }

    public SampleModel getSampleModel() {
        return src.getSampleModel();
    }

    public int getMinTileX() {
        return src.getMinTileX();
    }
    public int getMinTileY() {
        return src.getMinTileY();
    }

    public int getNumXTiles() {
        return src.getNumXTiles();
    }
    public int getNumYTiles() {
        return src.getNumYTiles();
    }

    public int getTileGridXOffset() {
        return src.getTileGridXOffset();
    }

    public int getTileGridYOffset() {
        return src.getTileGridYOffset();
    }

    public int getTileWidth() {
        return src.getTileWidth();
    }
    public int getTileHeight() {
        return src.getTileHeight();
    }

    public Object getProperty(String name) {
        return src.getProperty(name);
    }

    public String[] getPropertyNames() {
        return src.getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        return src.getTile(tileX, tileY);
    }

    public WritableRaster copyData(WritableRaster raster) {
        return src.copyData(raster);
    }

    public Raster getData() {
        return src.getData();
    }

    public Raster getData(Rectangle rect) {
        return src.getData(rect);
    }

    public Shape getDependencyRegion(int srcIndex, Rectangle outputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }

    public Shape getDirtyRegion(int srcIndex, Rectangle inputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }
}
