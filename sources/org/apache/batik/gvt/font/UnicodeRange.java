/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
            int unicodeVal = (int)unicode.charAt(0);
            if (unicodeVal >= firstUnicodeValue
                 && unicodeVal <= lastUnicodeValue) {
                return true;
            }
        }
        return false;
    }
}