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

/**
 * An interface describing objects that transform an InputStream into a
 * BufferedImage or Raster.
 *
 */
public interface ImageDecoder {

    /**
     * Returns the current parameters as an instance of the
     * ImageDecodeParam interface.  Concrete implementations of this
     * interface will return corresponding concrete implementations of
     * the ImageDecodeParam interface.  For example, a JPEGImageDecoder
     * will return an instance of JPEGDecodeParam.
     */
    ImageDecodeParam getParam();

    /**
     * Sets the current parameters to an instance of the 
     * ImageDecodeParam interface.  Concrete implementations
     * of ImageDecoder may throw a RuntimeException if the
     * param argument is not an instance of the appropriate
     * subclass or subinterface.  For example, a JPEGImageDecoder
     * will expect param to be an instance of JPEGDecodeParam.
     */
    void setParam(ImageDecodeParam param);

    /** Returns the SeekableStream associated with this ImageDecoder. */
    SeekableStream getInputStream();

    /** Returns the number of pages present in the current stream. */
    int getNumPages() throws IOException;

    /**
     * Returns a Raster that contains the decoded contents of the
     * SeekableStream associated with this ImageDecoder.  Only
     * the first page of a multi-page image is decoded.
     */
    Raster decodeAsRaster() throws IOException;

    /**
     * Returns a Raster that contains the decoded contents of the
     * SeekableStream associated with this ImageDecoder.
     * The given page of a multi-page image is decoded.  If
     * the page does not exist, an IOException will be thrown.
     * Page numbering begins at zero.
     *
     * @param page The page to be decoded.
     */
    Raster decodeAsRaster(int page) throws IOException;

    /**
     * Returns a RenderedImage that contains the decoded contents of the
     * SeekableStream associated with this ImageDecoder.  Only
     * the first page of a multi-page image is decoded.
     */
    RenderedImage decodeAsRenderedImage() throws IOException;

    /**
     * Returns a RenderedImage that contains the decoded contents of the
     * SeekableStream associated with this ImageDecoder.
     * The given page of a multi-page image is decoded.  If
     * the page does not exist, an IOException will be thrown.
     * Page numbering begins at zero.
     *
     * @param page The page to be decoded.
     */
    RenderedImage decodeAsRenderedImage(int page) throws IOException;
}
