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

import java.io.IOException;
import java.io.InputStream;

import org.apache.batik.ext.awt.image.codec.SeekableStream;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFDecodeParam;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFImage;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.util.ParsedURL;

public class TIFFRegistryEntry 
    extends MagicNumberRegistryEntry {

    static final byte [] sig1 = {(byte)0x49, (byte)0x49, 42,  0};
    static final byte [] sig2 = {(byte)0x4D, (byte)0x4D,  0, 42};

    static MagicNumberRegistryEntry.MagicNumber [] magicNumbers = {
        new MagicNumberRegistryEntry.MagicNumber(0, sig1),
        new MagicNumberRegistryEntry.MagicNumber(0, sig2) };

    static final String [] exts      = {"tiff", "tif" };
    static final String [] mimeTypes = {"image/tiff", "image/tif" };

    public TIFFRegistryEntry() {
        super("TIFF", exts, mimeTypes, magicNumbers);
    }

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param origURL The original URL, if any, for documentation
     *                purposes only.  This may be null.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  */
    public Filter handleStream(InputStream inIS, 
                               ParsedURL   origURL,
                               boolean needRawData) {

        final DeferRable  dr  = new DeferRable();
        final InputStream is  = inIS;
        final String      errCode;
        final Object []   errParam;
        if (origURL != null) {
            errCode  = ERR_URL_FORMAT_UNREADABLE;
            errParam = new Object[] {"TIFF", origURL};
        } else {
            errCode  = ERR_STREAM_FORMAT_UNREADABLE;
            errParam = new Object[] {"TIFF"};
        }

        Thread t = new Thread() {
                public void run() {
                    Filter filt;
                    try {
                        TIFFDecodeParam param = new TIFFDecodeParam();
                        SeekableStream ss = 
                            SeekableStream.wrapInputStream(is, true);
                        CachableRed cr = new TIFFImage(ss, param, 0);
                        cr = new Any2sRGBRed(cr);
                        filt = new RedRable(cr);
                    } catch (IOException ioe) {
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (this, errCode, errParam);
                    } catch (ThreadDeath td) {
                        throw td;
                    } catch (Throwable t) {
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
