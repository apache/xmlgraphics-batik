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

package org.apache.batik.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains utility functions to manage encodings.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class EncodingUtilities {
    
    /**
     * The standard to Java encoding table.
     */
    protected final static Map ENCODINGS = new HashMap();
    static {
        ENCODINGS.put("UTF-8",           "UTF8");
        ENCODINGS.put("UTF-16",          "Unicode");
        ENCODINGS.put("US-ASCII",        "ASCII");

        ENCODINGS.put("ISO-8859-1",      "8859_1");
        ENCODINGS.put("ISO-8859-2",      "8859_2");
        ENCODINGS.put("ISO-8859-3",      "8859_3");
        ENCODINGS.put("ISO-8859-4",      "8859_4");
        ENCODINGS.put("ISO-8859-5",      "8859_5");
        ENCODINGS.put("ISO-8859-6",      "8859_6");
        ENCODINGS.put("ISO-8859-7",      "8859_7");
        ENCODINGS.put("ISO-8859-8",      "8859_8");
        ENCODINGS.put("ISO-8859-9",      "8859_9");
        ENCODINGS.put("ISO-2022-JP",     "JIS");

        ENCODINGS.put("WINDOWS-31J",     "MS932");
        ENCODINGS.put("EUC-JP",          "EUCJIS");
        ENCODINGS.put("GB2312",          "GB2312");
        ENCODINGS.put("BIG5",            "Big5");
        ENCODINGS.put("EUC-KR",          "KSC5601");
        ENCODINGS.put("ISO-2022-KR",     "ISO2022KR");
        ENCODINGS.put("KOI8-R",          "KOI8_R");

        ENCODINGS.put("EBCDIC-CP-US",    "Cp037");
        ENCODINGS.put("EBCDIC-CP-CA",    "Cp037");
        ENCODINGS.put("EBCDIC-CP-NL",    "Cp037");
	ENCODINGS.put("EBCDIC-CP-WT",    "Cp037");
        ENCODINGS.put("EBCDIC-CP-DK",    "Cp277");
        ENCODINGS.put("EBCDIC-CP-NO",    "Cp277");
        ENCODINGS.put("EBCDIC-CP-FI",    "Cp278");
        ENCODINGS.put("EBCDIC-CP-SE",    "Cp278");
        ENCODINGS.put("EBCDIC-CP-IT",    "Cp280");
        ENCODINGS.put("EBCDIC-CP-ES",    "Cp284");
        ENCODINGS.put("EBCDIC-CP-GB",    "Cp285");
        ENCODINGS.put("EBCDIC-CP-FR",    "Cp297");
        ENCODINGS.put("EBCDIC-CP-AR1",   "Cp420");
        ENCODINGS.put("EBCDIC-CP-HE",    "Cp424");
        ENCODINGS.put("EBCDIC-CP-BE",    "Cp500");
        ENCODINGS.put("EBCDIC-CP-CH",    "Cp500");
        ENCODINGS.put("EBCDIC-CP-ROECE", "Cp870");
        ENCODINGS.put("EBCDIC-CP-YU",    "Cp870");
        ENCODINGS.put("EBCDIC-CP-IS",    "Cp871");
        ENCODINGS.put("EBCDIC-CP-AR2",   "Cp918");

        ENCODINGS.put("CP1252",          "Cp1252");
    }

    /**
     * This class does not need to be instantiated.
     */
    protected EncodingUtilities() {
    }

    /**
     * Returns the Java encoding string mapped with the given standard
     * encoding string.
     * @return null if no mapping was found.
     */
    public static String javaEncoding(String encoding) {
        return (String)ENCODINGS.get(encoding.toUpperCase());
    }
}
