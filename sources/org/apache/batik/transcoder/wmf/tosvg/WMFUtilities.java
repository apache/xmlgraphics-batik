/*

   Copyright 2006 The Apache Software Foundation 

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

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.font.TextLayout;
import java.io.UnsupportedEncodingException;
import org.apache.batik.transcoder.wmf.WMFConstants;

/** This class holds various utilies for importing WMF files that can be used either for
 *  {@link org.apache.batik.transcoder.wmf.tosvg.AbstractWMFReader}s and 
 *  {@link org.apache.batik.transcoder.wmf.tosvg.AbstractWMFPainter}s
 */
public class WMFUtilities {
    /** Decode a byte array in a String, considering the last selected charset.
     */
    public static String decodeString(WMFFont wmfFont, byte[] bstr) {
        // manage the charset encoding
        String str;
        try {
            if (wmfFont.charset == WMFConstants.META_CHARSET_ANSI) {
                str = new String(bstr);
            } else if (wmfFont.charset == WMFConstants.META_CHARSET_DEFAULT) {
                str = new String(bstr, WMFConstants.CHARSET_DEFAULT);
            } else if (wmfFont.charset == WMFConstants.META_CHARSET_GREEK) {
                str = new String(bstr, WMFConstants.CHARSET_GREEK);
            } else if (wmfFont.charset == WMFConstants.META_CHARSET_RUSSIAN) {
                str = new String(bstr, WMFConstants.CHARSET_CYRILLIC);
            } else if (wmfFont.charset == WMFConstants.META_CHARSET_HEBREW) {
                str = new String(bstr, WMFConstants.CHARSET_HEBREW);
            } else if (wmfFont.charset == WMFConstants.META_CHARSET_ARABIC) {
                str = new String(bstr, WMFConstants.CHARSET_ARABIC);
            } else str = new String(bstr);
        } catch (UnsupportedEncodingException e) {
            str = new String(bstr);
        }
        
        return str;
    }          
    
    /** Get the Horizontal Alignement for the Alignment property.
     */
    public static int getHorizontalAlignment(int align) {
        int v = align;
        v = v % WMFConstants.TA_BASELINE; // skip baseline alignment (24)
        v = v % WMFConstants.TA_BOTTOM;  // skip bottom aligment (8)
        if (v >= 6) return WMFConstants.TA_CENTER;
        else if (v >= 2) return WMFConstants.TA_RIGHT;
        else return WMFConstants.TA_LEFT;
    }
    
    /** Get the Vertical Alignement for the Alignment property.
     */    
    public static int getVerticalAlignment(int align) {
        int v = align;
        if ((v/WMFConstants.TA_BASELINE) != 0) return WMFConstants.TA_BASELINE;
        v = v % WMFConstants.TA_BASELINE; // skip baseline alignment (24)
        if ((v/WMFConstants.TA_BOTTOM) != 0) return WMFConstants.TA_BOTTOM;
        else return WMFConstants.TA_TOP;
    }     
}

