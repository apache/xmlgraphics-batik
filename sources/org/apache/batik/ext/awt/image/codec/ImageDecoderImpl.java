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

package org.apache.batik.ext.awt.image.codec;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A partial implementation of the <code>ImageDecoder</code> interface
 * useful for subclassing.
 *
 */
public abstract class ImageDecoderImpl implements ImageDecoder {

    /**
     * The <code>SeekableStream</code> associcted with this
     * <code>ImageEncoder</code>.
     */
    protected SeekableStream input;

    /**
     * The <code>ImageDecodeParam</code> object associated with this
     * <code>ImageEncoder</code>.
     */
    protected ImageDecodeParam param;

    /**
     * Constructs an <code>ImageDecoderImpl</code> with a given
     * <code>SeekableStream</code> and <code>ImageDecodeParam</code>
     * instance.
     */
    public ImageDecoderImpl(SeekableStream input,
                            ImageDecodeParam param) {
        this.input = input;
        this.param = param;
    }

    /**
     * Constructs an <code>ImageDecoderImpl</code> with a given
     * <code>InputStream</code> and <code>ImageDecodeParam</code>
     * instance.  The <code>input</code> parameter will be used to
     * construct a <code>ForwardSeekableStream</code>; if the ability
     * to seek backwards is required, the caller should construct
     * an instance of <code>SeekableStream</code> and
     * make use of the other contructor.
     */
    public ImageDecoderImpl(InputStream input,
                            ImageDecodeParam param) {
        this.input = new ForwardSeekableStream(input);
        this.param = param;
    }

    /**
     * Returns the current parameters as an instance of the
     * <code>ImageDecodeParam</code> interface.  Concrete
     * implementations of this interface will return corresponding
     * concrete implementations of the <code>ImageDecodeParam</code>
     * interface.  For example, a <code>JPEGImageDecoder</code> will
     * return an instance of <code>JPEGDecodeParam</code>.
     */
    public ImageDecodeParam getParam() {
        return param;
    }

    /**
     * Sets the current parameters to an instance of the
     * <code>ImageDecodeParam</code> interface.  Concrete
     * implementations of <code>ImageDecoder</code> may throw a
     * <code>RuntimeException</code> if the <code>param</code>
     * argument is not an instance of the appropriate subclass or
     * subinterface.  For example, a <code>JPEGImageDecoder</code>
     * will expect <code>param</code> to be an instance of
     * <code>JPEGDecodeParam</code>.
     */
    public void setParam(ImageDecodeParam param) {
        this.param = param;
    }

    /**
     * Returns the <code>SeekableStream</code> associated with
     * this <code>ImageDecoder</code>.
     */
    public SeekableStream getInputStream() {
        return input;
    }

    /**
     * Returns the number of pages present in the current stream.
     * By default, the return value is 1.  Subclasses that deal with
     * multi-page formats should override this method.
     */
    public int getNumPages() throws IOException {
        return 1;
    }
    
    /**
     * Returns a <code>Raster</code> that contains the decoded
     * contents of the <code>SeekableStream</code> associated
     * with this <code>ImageDecoder</code>.  Only
     * the first page of a multi-page image is decoded.
     */
    public Raster decodeAsRaster() throws IOException {
        return decodeAsRaster(0);
    }

    /**
     * Returns a <code>Raster</code> that contains the decoded
     * contents of the <code>SeekableStream</code> associated
     * with this <code>ImageDecoder</code>.
     * The given page of a multi-page image is decoded.  If
     * the page does not exist, an IOException will be thrown.
     * Page numbering begins at zero.
     *
     * @param page The page to be decoded.
     */
    public Raster decodeAsRaster(int page) throws IOException {
        RenderedImage im = decodeAsRenderedImage(page);
        return im.getData();
    }

    /**
     * Returns a <code>RenderedImage</code> that contains the decoded
     * contents of the <code>SeekableStream</code> associated
     * with this <code>ImageDecoder</code>.  Only
     * the first page of a multi-page image is decoded.
     */
    public RenderedImage decodeAsRenderedImage() throws IOException {
        return decodeAsRenderedImage(0);
    }

    /**
     * Returns a <code>RenderedImage</code> that contains the decoded
     * contents of the <code>SeekableStream</code> associated
     * with this <code>ImageDecoder</code>.
     * The given page of a multi-page image is decoded.  If
     * the page does not exist, an IOException will be thrown.
     * Page numbering begins at zero.
     *
     * @param page The page to be decoded.
     */
    public abstract RenderedImage decodeAsRenderedImage(int page)
        throws IOException;
}
