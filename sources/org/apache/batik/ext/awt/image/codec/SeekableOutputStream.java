/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
