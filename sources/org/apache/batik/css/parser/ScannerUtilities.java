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

package org.apache.batik.css.parser;

/**
 * A collection of utility functions for a CSS scanner.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ScannerUtilities {

    /**
     * The set of the valid identifier start characters.
     */
    protected final static int[] IDENTIFIER_START = { 0, 0, 134217726, 134217726 };

    /**
     * The set of the valid name characters.
     */
    protected final static int[] NAME = { 0, 67051520, 134217726, 134217726 };

    /**
     * The set of the valid hexadecimal characters.
     */
    protected final static int[] HEXADECIMAL = { 0, 67043328, 126, 126 };

    /**
     * The set of the valid string characters.
     */
    protected final static int[] STRING = { 512, -133, -1, 2147483647 };

    /**
     * The set of the valid uri characters.
     */
    protected final static int[] URI = { 0, -902, -1, 2147483647 };

    /**
     * This class does not need to be instantiated.
     */
    protected ScannerUtilities() {
    }

    /**
     * Tests whether the given character is a valid space.
     */
    public static boolean isCSSSpace(char c) {
      return (c <= 0x0020) &&
             (((((1L << '\t') |
                 (1L << '\n') |
                 (1L << '\r') |
                 (1L << '\f') |
                 (1L << 0x0020)) >> c) & 1L) != 0);
    }

    /**
     * Tests whether the given character is a valid identifier start character.
     */
    public static boolean isCSSIdentifierStartCharacter(char c) {
	return c >= 128 || ((IDENTIFIER_START[c / 32] & (1 << (c % 32))) != 0);
    }

    /**
     * Tests whether the given character is a valid name character.
     */
    public static boolean isCSSNameCharacter(char c) {
	return c >= 128 || ((NAME[c / 32] & (1 << (c % 32))) != 0);
    }

    /**
     * Tests whether the given character is a valid hexadecimal character.
     */
    public static boolean isCSSHexadecimalCharacter(char c) {
	return c < 128 && ((HEXADECIMAL[c / 32] & (1 << (c % 32))) != 0);
    }

    /**
     * Tests whether the given character is a valid string character.
     */
    public static boolean isCSSStringCharacter(char c) {
	return c >= 128 || ((STRING[c / 32] & (1 << (c % 32))) != 0);
    }

    /**
     * Tests whether the given character is a valid URI character.
     */
    public static boolean isCSSURICharacter(char c) {
	return c >= 128 || ((URI[c / 32] & (1 << (c % 32))) != 0);
    }
}
