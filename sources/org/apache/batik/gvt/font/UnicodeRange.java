/*

   Copyright 2001,2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.gvt.font;


/**
 * A class that represents a CSS unicode range.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class UnicodeRange {

    private int firstUnicodeValue;
    private int lastUnicodeValue;

    /**
     * Constructs a unicode range from a CSS unicode range string.
     */
    public UnicodeRange(String unicodeRange) {

        if (unicodeRange.startsWith("U+") && unicodeRange.length() > 2) {
            // strip off the U+
            unicodeRange = unicodeRange.substring(2);
            int dashIndex = unicodeRange.indexOf('-');
            String firstValue;
            String lastValue;

            if (dashIndex != -1) { // it is a simple 2 value range
                firstValue = unicodeRange.substring(0, dashIndex);
                lastValue = unicodeRange.substring(dashIndex+1);

            } else {
                firstValue = unicodeRange;
                lastValue = unicodeRange;
                if (unicodeRange.indexOf('?') != -1) {
                    firstValue = firstValue.replace('?', '0');
                    lastValue = lastValue.replace('?', 'F');
                }
            }
            try {
                firstUnicodeValue = Integer.parseInt(firstValue, 16);
                lastUnicodeValue = Integer.parseInt(lastValue, 16);
            } catch (NumberFormatException e) {
                firstUnicodeValue = -1;
                lastUnicodeValue = -1;
            }
        } else {
            // not a valid unicode range
            firstUnicodeValue = -1;
            lastUnicodeValue = -1;
        }
    }

    /**
     * Returns true if the specified unicode value is within this range.
     */
    public boolean contains(String unicode) {
        if (unicode.length() == 1) {
            int unicodeVal = unicode.charAt(0);
            if (unicodeVal >= firstUnicodeValue
                 && unicodeVal <= lastUnicodeValue) {
                return true;
            }
        }
        return false;
    }
}
