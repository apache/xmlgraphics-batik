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

import java.io.InputStream;
import java.io.StreamCorruptedException;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;

/**
 * This type of Image tag registy entry is used for most normal image
 * file formats.  You are given a markable stream and an opportunity
 * to check if it is "compatible" if you return true then you will
 * likely be asked to provide the decoded image next.
 * @see MagicNumberRegistryEntry
 */
public interface StreamRegistryEntry extends RegistryEntry {

    /**
     * returns the number of bytes that need to be
     * supported by mark on the InputStream for this
     * to check the stream for compatibility.
     */
    public int getReadlimit();

    /**
     * Check if the Stream references an image that can be handled by
     * this format handler.  The input stream passed in should be
     * assumed to support mark and reset.
     *
     * If this method throws a StreamCorruptedException then the
     * InputStream will be closed and a new one opened (if possible).
     *
     * This method should only throw a StreamCorruptedException if it
     * is unable to restore the state of the InputStream
     * (i.e. mark/reset fails basically).  
     */
    public boolean isCompatibleStream(InputStream is) 
        throws StreamCorruptedException;

    /**
     * Decode the Stream into a Filter.  If the stream turns out not to
     * be of a format this RegistryEntry can handle you should attempt
     * to reset the stream, then return null.<P>
     *
     * This should only return a broken link image when the image is
     * clearly of this format, but is unreadable for some reason.
     *
     * @param is The input stream that contains the image.
     * @param origURL The original URL, if any, for documentation
     *                purposes only.  This may be null.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleStream(InputStream is, 
                               ParsedURL   origURL,
                               boolean     needRawData);
}

