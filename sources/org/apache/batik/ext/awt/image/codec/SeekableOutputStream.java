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
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * An <code>OutputStream</code> which can seek to an arbitrary offset.
 */
public class SeekableOutputStream extends OutputStream {

    private RandomAccessFile file;

    /**
     * Constructs a <code>SeekableOutputStream</code> from a
     * <code>RandomAccessFile</code>.  Unless otherwise indicated,
     * all method invocations are fowarded to the underlying
     * <code>RandomAccessFile</code>.
     *
     * @param file The <code>RandomAccessFile</code> to which calls
     *             will be forwarded.
     * @exception IllegalArgumentException if <code>file</code> is
     *            <code>null</code>.
     */
    public SeekableOutputStream(RandomAccessFile file) {
        if(file == null) {
            throw new IllegalArgumentException("SeekableOutputStream0");
        }
        this.file = file;
    }

    public void write(int b) throws IOException {
        file.write(b);
    }

    public void write(byte b[]) throws IOException {
        file.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {
        file.write(b, off, len);
    }

    /**
     * Invokes <code>getFD().sync()</code> on the underlying
     * <code>RandomAccessFile</code>.
     */
    public void flush() throws IOException {
        file.getFD().sync();
    }

    public void close() throws IOException {
        file.close();
    }

    public long getFilePointer() throws IOException {
        return file.getFilePointer();
    }

    public void seek(long pos) throws IOException {
        file.seek(pos);
    }
}
