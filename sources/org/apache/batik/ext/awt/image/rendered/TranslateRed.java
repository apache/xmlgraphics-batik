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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
/**
 * This is a special case of an Affine that only contains integer
 * translations, this allows it to do it's work by simply changing
 * the coordinate system of the tiles.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class TranslateRed extends AbstractRed {
    
    protected int deltaX;
    protected int deltaY;

    /**
     * Construct an instance of TranslateRed
     * @param xloc The new x coordinate of cr.getMinX().
     * @param yloc The new y coordinate of cr.getMinY().
     */
    public TranslateRed(CachableRed cr, int xloc, int yloc) {
        super(cr, new Rectangle(xloc,  yloc,
                                cr.getWidth(), cr.getHeight()),
              cr.getColorModel(), cr.getSampleModel(), 
              cr.getTileGridXOffset()+xloc-cr.getMinX(), 
              cr.getTileGridYOffset()+yloc-cr.getMinY(), 
              null);
        deltaX = xloc-cr.getMinX();
        deltaY = yloc-cr.getMinY();
    }
    
    /**
     * The delata translation in x (absolute loc is available from getMinX())
     */
    public int getDeltaX() { return deltaX; }

    /**
     * The delata translation in y (absolute loc is available from getMinY())
     */
    public int getDeltaY() { return deltaY; }

    /**
     * fetch the source image for this node.
     */
    public CachableRed getSource() {
        return (CachableRed)getSources().get(0);
    }

    public Object getProperty(String name) {
        return getSource().getProperty(name);
    }

    public String [] getPropertyNames() {
        return getSource().getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        Raster r = getSource().getTile(tileX, tileY);
        
        return r.createTranslatedChild(r.getMinX()+deltaX,
                                       r.getMinY()+deltaY);
    }

    public Raster getData() {
        Raster r = getSource().getData();
        return r.createTranslatedChild(r.getMinX()+deltaX,
                                       r.getMinY()+deltaY);
    }

    public Raster getData(Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();
        r.translate(-deltaX, -deltaY);
        Raster ret = getSource().getData(r);
        return ret.createTranslatedChild(ret.getMinX()+deltaX,
                                         ret.getMinY()+deltaY);
    }

    public WritableRaster copyData(WritableRaster wr) {
        WritableRaster wr2 = wr.createWritableTranslatedChild
            (wr.getMinX()-deltaX, wr.getMinY()-deltaY);

        getSource().copyData(wr2);

        return wr;
    }
}
