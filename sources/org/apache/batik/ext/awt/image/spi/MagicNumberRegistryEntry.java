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
import java.io.StreamCorruptedException;

/**
 * This Image tag registry entry is built around the notion of magic
 * numbers.  These are strings of bytes that are at a well known
 * location in the input stream (often the start).
 *
 * This base class can handle the compatiblity check based on a list
 * of Magic Numbers that correspond to your format (Some formats have
 * multiple magic numbers associated with them).
 */
public abstract class MagicNumberRegistryEntry 
    extends AbstractRegistryEntry 
    implements StreamRegistryEntry {

    public static final float PRIORITY = 1000;

    /**
     * Inner class that represents one magic number. Simply consists
     * of an offset in bytes from the start of the file, and a byte
     * array that must match.
     */
    public static class MagicNumber {
        int offset;
        byte [] magicNumber;
        byte [] buffer;
        
        /**
         *  Constructor.
         * @param offset the location of the magic number in file.
         * @param magicNumber the sequence of bytes that must match.
         */
        public MagicNumber(int offset, byte[]magicNumber) {
            this.offset = offset;
            this.magicNumber = (byte[])magicNumber.clone();
            buffer = new byte[magicNumber.length];
        }

        /**
         * Returns the maximum number of bytes that will be read for
         * this magic number compairison.  
         */
        int getReadlimit() {
            return offset+magicNumber.length;
        }

        /**
         * Performs the check of is.
         */
        boolean isMatch(InputStream is) 
            throws StreamCorruptedException {
            int idx = 0;
            is.mark(getReadlimit());
            try {
                // Skip to the offset location.
                while (idx < offset) {
                    int rn = (int)is.skip(offset-idx);
                    if (rn == -1) return false;
                    idx += rn;
                }
		
                idx = 0;
                while (idx < buffer.length) {
                    int rn = is.read(buffer, idx, buffer.length-idx);
                    if (rn == -1) return false;
                    idx += rn;
                }
		
                for (int i=0; i<magicNumber.length; i++) {
                    if (magicNumber[i] != buffer[i])
                        return false;
                }
            } catch (IOException ioe) {
                return false;
            } finally {
                try {
                    // Make sure we always put back what we have read.
                    // If this throws an IOException then the current
                    // stream should be closed an reopend by the registry.
                    is.reset();
                } catch (IOException ioe) {
                    throw new StreamCorruptedException(ioe.getMessage());
                }
            }
            return true;
        }
    }

    /** The list of magic numbers associated with this entry */
    MagicNumber [] magicNumbers;

    /**
     * Constructor, simplifies construction of entry when only
     * one extension and one magic number is required.
     * @param name        Format Name
     * @param ext         Standard extension
     * @param offset      Offset of magic number
     * @param magicNumber byte array to match.
     */
    public MagicNumberRegistryEntry(String name,
                                    String ext,
                                    String mimeType,
                                    int offset, byte[]magicNumber) {
        super(name, PRIORITY, ext, mimeType);
        magicNumbers    = new MagicNumber[1];
        magicNumbers[0] = new MagicNumber(offset, magicNumber);
    }
    
    /**
     * Constructor, simplifies construction of entry when only
     * one extension is required.
     * @param name         Format Name
     * @param ext          Standard extension
     * @param magicNumbers Array of magic numbers any of which can match.
     */
    public MagicNumberRegistryEntry(String name,
                                    String ext,
                                    String mimeType,
                                    MagicNumber [] magicNumbers) {
        super(name, PRIORITY, ext, mimeType);
        this.magicNumbers = magicNumbers;
    }

    /**
     * Constructor, simplifies construction of entry when only
     * one magic number is required.
     * @param name Format Name
     * @param exts Standard set of extensions
     * @param offset Offset of magic number
     * @param magicNumber byte array to match.
     */
    public MagicNumberRegistryEntry(String    name,
                                    String [] exts,
                                    String [] mimeTypes,
                                    int offset, byte[]magicNumber) {
        super(name, PRIORITY, exts, mimeTypes);
        magicNumbers    = new MagicNumber[1];
        magicNumbers[0] = new MagicNumber(offset, magicNumber);
    }
    
    /**
     * Constructor
     * @param name Format Name
     * @param exts Standard set of extensions
     * @param magicNumbers array of magic numbers any of which can match.
     */
    public MagicNumberRegistryEntry(String    name,
                                    String [] exts,
                                    String [] mimeTypes,
                                    MagicNumber [] magicNumbers) {
        super(name, PRIORITY, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }
    
    /**
     * Constructor, allows for overriding the default priority of
     * magic number entries.  This should be needed very rarely since
     * magic number checks are fairly relyable and hence aren't usually
     * sensative to order issues.
     * @param name Format Name
     * @param exts Standard set of extensions
     * @param magicNumbers array of magic numbers any of which can match.
     * @param priority     The priority of this entry (1000 is baseline)
     */
    public MagicNumberRegistryEntry(String         name,
                                    String []      exts,
                                    String []      mimeTypes,
                                    MagicNumber [] magicNumbers,
                                    float          priority) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }

    /**
     * Returns the maximume read ahead needed for all magic numbers.
     */
    public int getReadlimit() {
        int maxbuf = 0;
        for (int i=0; i<magicNumbers.length; i++) {
            int req = magicNumbers[i].getReadlimit();
            if (req > maxbuf) maxbuf = req;
        }
        return maxbuf;
    }
    
    /**
     * Check if the stream contains an image that can be
     * handled by this format handler
     */
    public boolean isCompatibleStream(InputStream is) 
        throws StreamCorruptedException {
        for (int i=0; i<magicNumbers.length; i++) {
            if (magicNumbers[i].isMatch(is)) 
                return true;
        }

        return false;
    }
}
