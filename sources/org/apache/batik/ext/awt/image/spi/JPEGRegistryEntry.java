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

package org.apache.batik.ext.awt.image.spi;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.util.ParsedURL;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.TruncatedFileException;

public class JPEGRegistryEntry 
    extends MagicNumberRegistryEntry {

    static final byte [] sigJPEG   = {(byte)0xFF, (byte)0xd8, 
                                      (byte)0xFF};
    static final String [] exts      = {"jpeg", "jpg" };
    static final String [] mimeTypes = {"image/jpeg", "image/jpg" };
    static final MagicNumber [] magicNumbers = {
        new MagicNumber(0, sigJPEG)
    };

    public JPEGRegistryEntry() {
        super("JPEG", exts, mimeTypes, magicNumbers);
    }

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param origURL The original URL, if any, for documentation
     *                purposes only.  This may be null.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleStream(InputStream inIS, 
                               ParsedURL   origURL,
                               boolean     needRawData) {
        final DeferRable  dr  = new DeferRable();
        final InputStream is  = inIS;
        final String      errCode;
        final Object []   errParam;
        if (origURL != null) {
            errCode  = ERR_URL_FORMAT_UNREADABLE;
            errParam = new Object[] {"JPEG", origURL};
        } else {
            errCode  = ERR_STREAM_FORMAT_UNREADABLE;
            errParam = new Object[] {"JPEG"};
        }

        Thread t = new Thread() {
                public void run() {
                    Filter filt;
                    try{
                        JPEGImageDecoder decoder;
                        decoder = JPEGCodec.createJPEGDecoder(is);
                        BufferedImage image;
                        try { 
                            image   = decoder.decodeAsBufferedImage();
                        } catch (TruncatedFileException tfe) {
                            image = tfe.getBufferedImage();
                            // Should probably draw some indication
                            // that this is a partial image....
                            if (image == null)
                                throw new IOException
                                    ("JPEG File was truncated");
                        }
                        dr.setBounds(new Rectangle2D.Double
                                     (0, 0, image.getWidth(), 
                                      image.getHeight()));
                        CachableRed cr;
                        cr = GraphicsUtil.wrap(image);
                        cr = new Any2sRGBRed(cr);
                        cr = new FormatRed(cr, GraphicsUtil.sRGB_Unpre);
                        WritableRaster wr = (WritableRaster)cr.getData();
                        ColorModel cm = cr.getColorModel();
                        image = new BufferedImage
                            (cm, wr, cm.isAlphaPremultiplied(), null);
                        cr = GraphicsUtil.wrap(image);
                        filt = new RedRable(cr);
                    } catch (IOException ioe) {
                        // Something bad happened here...
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (this, errCode, errParam);
                    }

                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }
}
