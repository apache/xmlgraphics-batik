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

package org.apache.batik.apps.rasterizer;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

/**
 * Describes the type of destination for an <tt>SVGConverter</tt>
 * operation.
 *
 * @author Henri Ruini
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public final class DestinationType {
    public static final String PNG_STR  = "image/png";
    public static final String JPEG_STR = "image/jpeg";
    public static final String TIFF_STR = "image/tiff";
    public static final String PDF_STR  = "application/pdf";

    public static final int PNG_CODE  = 0;
    public static final int JPEG_CODE = 1;
    public static final int TIFF_CODE = 2;
    public static final int PDF_CODE  = 3;
        
    public static final String PNG_EXTENSION  = ".png";
    public static final String JPEG_EXTENSION = ".jpg";
    public static final String TIFF_EXTENSION = ".tif";
    public static final String PDF_EXTENSION  = ".pdf";

    public static final DestinationType PNG  
        = new DestinationType(PNG_STR, PNG_CODE, PNG_EXTENSION);
    public static final DestinationType JPEG 
        = new DestinationType(JPEG_STR, JPEG_CODE, JPEG_EXTENSION);
    public static final DestinationType TIFF 
        = new DestinationType(TIFF_STR, TIFF_CODE, TIFF_EXTENSION);
    public static final DestinationType PDF  
        = new DestinationType(PDF_STR, PDF_CODE, PDF_EXTENSION);

    private String type;
    private int    code;
    private String extension;

    private DestinationType(String type, int code, String extension){
        this.type = type;
        this.code = code;
        this.extension = extension;
    }
    
    public String getExtension(){
        return extension;
    }

    public String toString(){
        return type;
    }

    public int toInt(){
        return code;
    }

    /**
     * Returns a transcoder object of the result image type.
     *
     * @param type Type of the result image.
     *
     * @return Transcoder object or <tt>null</tt> if there isn't a proper transcoder.
     */
    protected Transcoder getTranscoder(){
        switch(code) {
            case PNG_CODE:
                return new PNGTranscoder();
            case JPEG_CODE:
                return new JPEGTranscoder();
            case TIFF_CODE:
                return new TIFFTranscoder();
            case PDF_CODE:
                try {
                    Class pdfClass = Class.forName("org.apache.fop.svg.PDFTranscoder");
                    return (Transcoder)pdfClass.newInstance();
                } catch(Exception e) {
                    return null;
                }
            default:
                return null;
        }

    }

    /**
     * Defines valid image types.
     *
     * @return Array of valid values as strings.
     */
    public DestinationType[] getValues() {
        return new DestinationType[]{PNG, JPEG, TIFF, PDF};
    }

    public Object readResolve(){
        switch(code){
        case PNG_CODE:
            return PNG;
        case JPEG_CODE:
            return JPEG;
        case TIFF_CODE:
            return TIFF;
        case PDF_CODE:
            return PDF;
        default:
            throw new Error();
        }
    }
}
