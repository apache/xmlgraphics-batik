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

import java.io.IOException;
import java.io.InputStream;

/**
 * A subclass of <code>SeekableStream</code> that may be used
 * to wrap a regular <code>InputStream</code> efficiently.
 * Seeking backwards is not supported.
 *
 */
public class ForwardSeekableStream extends SeekableStream {

    /** The source <code>InputStream</code>. */
    private InputStream src;

    /** The current position. */
    long pointer = 0L;

    /** The marked position. */
    long markPos = -1L;

    /** 
     * Constructs a <code>InputStreamForwardSeekableStream</code> from a
     * regular <code>InputStream</code>.
     */
    public ForwardSeekableStream(InputStream src) {
        this.src = src;
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public final int read() throws IOException {
        int result = src.read();
        if (result != -1) {
            ++pointer;
        }
        return result;
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public final int read(byte[] b, int off, int len) throws IOException {
        int result = src.read(b, off, len);
        if (result != -1) {
            pointer += result;
        }
        return result;
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public final long skip(long n) throws IOException {
        long skipped = src.skip(n);
        pointer += skipped;
        return skipped;
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public final int available() throws IOException {
        return src.available();
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public final void close() throws IOException {
        src.close();
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public synchronized final void mark(int readLimit) {
        markPos = pointer;
        src.mark(readLimit);
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public synchronized final void reset() throws IOException {
        if (markPos != -1) {
            pointer = markPos;
        }
        src.reset();
    }

    /** Forwards the request to the real <code>InputStream</code>. */
    public boolean markSupported() {
        return src.markSupported();
    }

    /** Returns <code>false</code> since seking backwards is not supported. */
    public final boolean canSeekBackwards() {
        return false;
    }

    /** Returns the current position in the stream (bytes read). */
    public final long getFilePointer() {
        return pointer;
    }

    /**
     * Seeks forward to the given position in the stream.
     * If <code>pos</code> is smaller than the current position
     * as returned by <code>getFilePointer()</code>, nothing
     * happens.
     */
    public final void seek(long pos) throws IOException {
        while (pos - pointer > 0) {
            pointer += src.skip(pos - pointer);
        }
    }
}
