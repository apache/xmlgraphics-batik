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

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is the superclass of all the char decoders.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCharDecoder implements CharDecoder {

    /**
     * The buffer size.
     */
    protected final static int BUFFER_SIZE = 8192;

    /**
     * The input stream to read.
     */
    protected InputStream inputStream;
    
    /**
     * The input buffer.
     */
    protected byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * The current position in the buffer.
     */
    protected int position;

    /**
     * The byte count in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharDecoder object.
     * @param is The stream to read.
     */
    protected AbstractCharDecoder(InputStream is) {
        inputStream = is;
    }

    /**
     * Disposes the associated resources.
     */
    public void dispose() throws IOException {
        inputStream.close();
        inputStream = null;
    }

    /**
     * Fills the input buffer.
     */
    protected void fillBuffer() throws IOException {
        count = inputStream.read(buffer, 0, BUFFER_SIZE);
        position = 0;
    }

    /**
     * To throws an exception when the input stream contains an
     * invalid character.
     * @param encoding The encoding name.
     */
    protected void charError(String encoding) throws IOException {
        throw new IOException
            (Messages.formatMessage("invalid.char",
                                    new Object[] { encoding }));
    }

    /**
     * To throws an exception when the end of stream was unexpected.
     * @param encoding The encoding name.
     */
    protected void endOfStreamError(String encoding) throws IOException {
        throw new IOException
            (Messages.formatMessage("end.of.stream",
                                    new Object[] { encoding }));
    }
}
