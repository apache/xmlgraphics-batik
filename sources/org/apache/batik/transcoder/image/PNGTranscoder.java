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

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.ext.awt.image.codec.PNGEncodeParam;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
import org.apache.batik.ext.awt.image.rendered.IndexImage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.transcoder.keys.IntegerKey;

/**
 * This class is an <tt>ImageTranscoder</tt> that produces a PNG image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class PNGTranscoder extends ImageTranscoder {

    /**
     * Constructs a new transcoder that produces png images.
     */
    public PNGTranscoder() {
        hints.put(KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
    }

    /**
     * Creates a new ARGB image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @param TranscoderException if an error occured while storing the image
     */
    public void writeImage(BufferedImage img, TranscoderOutput output)
            throws TranscoderException {

        OutputStream ostream = output.getOutputStream();
        if (ostream == null) {
            throw new TranscoderException(
                Messages.formatMessage("png.badoutput", null));
        }

        //
        // This is a trick so that viewers which do not support the alpha
        // channel will see a white background (and not a black one).
        //
        boolean forceTransparentWhite = false;

        if (hints.containsKey(KEY_FORCE_TRANSPARENT_WHITE)) {
            forceTransparentWhite =
                ((Boolean)hints.get
                 (KEY_FORCE_TRANSPARENT_WHITE)).booleanValue();
        }

        if (forceTransparentWhite) {
            int w = img.getWidth(), h = img.getHeight();
            DataBufferInt biDB = (DataBufferInt)img.getRaster().getDataBuffer();
            int scanStride = ((SinglePixelPackedSampleModel)
                              img.getSampleModel()).getScanlineStride();
            int dbOffset = biDB.getOffset();
            int pixels[] = biDB.getBankData()[0];
            int p = dbOffset;
            int adjust = scanStride - w;
            int a=0, r=0, g=0, b=0, pel=0;
            for(int i=0; i<h; i++){
                for(int j=0; j<w; j++){
                    pel = pixels[p];
                    a = (pel >> 24) & 0xff;
                    r = (pel >> 16) & 0xff;
                    g = (pel >> 8 ) & 0xff;
                    b =  pel        & 0xff;
                    r = (255*(255 -a) + a*r)/255;
                    g = (255*(255 -a) + a*g)/255;
                    b = (255*(255 -a) + a*b)/255;
                    pixels[p++] =
                        (a<<24 & 0xff000000) |
                        (r<<16 & 0xff0000) |
                        (g<<8  & 0xff00) |
                        (b     & 0xff);
                }
                p += adjust;
            }
        }

        int n=-1;
        if (hints.containsKey(KEY_INDEXED)) {
            n=((Integer)hints.get(KEY_INDEXED)).intValue();
            if (n==1||n==2||n==4||n==8) 
                //PNGEncodeParam.Palette can handle these numbers only.
                img = IndexImage.getIndexedImage(img,1<<n);
        }

        PNGEncodeParam params = PNGEncodeParam.getDefaultEncodeParam(img);
        if (params instanceof PNGEncodeParam.RGB) {
            ((PNGEncodeParam.RGB)params).setBackgroundRGB
                (new int [] { 255, 255, 255 });
        }

        // If they specify GAMMA key then use it otherwise don't
        // write a gAMA chunk, (default Gamma=2.2).
        if (hints.containsKey(KEY_GAMMA)) {
            params.setGamma(((Float)hints.get(KEY_GAMMA)).floatValue());
        } 

        // We always want an sRGB chunk and Our encoding intent is
        // perceptual
        params.setSRGBIntent(PNGEncodeParam.INTENT_PERCEPTUAL);

        float PixSzMM = userAgent.getPixelUnitToMillimeter();
        // num Pixs in 1 Meter
        int numPix      = (int)((1000/PixSzMM)+0.5);
        params.setPhysicalDimension(numPix, numPix, 1); // 1 means 'pix/meter'

        try {
            PNGImageEncoder pngEncoder = new PNGImageEncoder(ostream, params);
            pngEncoder.encode(img);
            ostream.close();
        } catch (IOException ex) {
            throw new TranscoderException(ex);
        }
    }

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * The gamma correction key.
     *
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_GAMMA</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">PNGEncodeParam.INTENT_PERCEPTUAL</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Controls the gamma correction of the png image.</TD>
     * </TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_GAMMA
        = new FloatKey();

    /**
     * The color indexed image key to specify number of colors used in
     * palette.
     *
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_INDEXED</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">none/true color image</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Turns on the reduction of the image to index 
     *     colors by specifying color bit depth, 1,2,4,8. The resultant 
     *     PNG will be an indexed PNG with color bit depth specified.</TD>
     * </TR>
     * </TABLE> 
     */
    public static final TranscodingHints.Key KEY_INDEXED
        = new IntegerKey();
}
