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

/**
 * This class represents a NormalizingReader which handles Strings.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StringNormalizingReader extends NormalizingReader {

    /**
     * The characters.
     */
    protected String string;

    /**
     * The length of the string.
     */
    protected int length;
    
    /**
     * The index of the next character.
     */
    protected int next;

    /**
     * The current line in the stream.
     */
    protected int line = 1;

    /**
     * The current column in the stream.
     */
    protected int column;

    /**
     * Creates a new StringNormalizingReader.
     * @param s The string to read.
     */
    public StringNormalizingReader(String s) {
        string = s;
        length = s.length();
    }

    /**
     * Read a single character.  This method will block until a
     * character is available, an I/O error occurs, or the end of the
     * stream is reached.
     */
    public int read() throws IOException {
        int result = (length == next) ? -1 : string.charAt(next++);
        if (result <= 13) {
            switch (result) {
            case 13:
                column = 0;
                line++;
                int c = (length == next) ? -1 : string.charAt(next);
                if (c == 10) {
                    next++;
                }
                return 10;
                
            case 10:
                column = 0;
                line++;
            }
        }
        return result;
    }

    /**
     * Returns the current line in the stream.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the current column in the stream.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Close the stream.
     */
    public void close() throws IOException {
        string = null;
    }
}
