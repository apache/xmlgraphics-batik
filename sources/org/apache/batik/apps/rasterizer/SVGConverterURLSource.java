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

import java.io.IOException;
import java.io.InputStream;

import org.apache.batik.util.ParsedURL;

/*
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGConverterURLSource implements SVGConverterSource {
    /** 
     * SVG file extension 
     */
    protected static final String SVG_EXTENSION = ".svg";
    protected static final String SVGZ_EXTENSION = ".svgz";

    //
    // Reported when the URL for one of the sources is
    // invalid. This will happen if the URL is malformed or
    // if the URL file does not end with the ".svg" extension.
    // This is needed to be able to create a file name for
    // the ouptut automatically.
    //
    public static final String ERROR_INVALID_URL
        = "SVGConverterURLSource.error.invalid.url";

    ParsedURL purl;
    String name;

    public SVGConverterURLSource(String url) throws SVGConverterException{
        this.purl = new ParsedURL(url);

        // Get the path portion
        String path = this.purl.getPath();
        if (path == null || 
            !(path.toLowerCase().endsWith(SVG_EXTENSION) ||
              path.toLowerCase().endsWith(SVGZ_EXTENSION))){
            throw new SVGConverterException(ERROR_INVALID_URL,
                                            new Object[]{url});
        }

        int n = path.lastIndexOf("/");
        if (n != -1){
            // The following is safe because we know there is at least ".svg"
            // after the slash.
            path = path.substring(n+1);
        }
            
        name = path;

        //
        // The following will force creation of different output file names
        // for urls with references (e.g., anne.svg#svgView(viewBox(0,0,4,5)))
        //
        String ref = this.purl.getRef();
        if (ref != null && (ref.length()!=0)) {
            name += "" + ref.hashCode();
        }
    }

    public String toString(){
        return purl.toString();
    }

    public String getURI(){
        return toString();
    }

    public boolean equals(Object o){
        if (o == null || !(o instanceof SVGConverterURLSource)){
            return false;
        }

        return purl.equals(((SVGConverterURLSource)o).purl);
    }

    public InputStream openStream() throws IOException {
        return purl.openStream();
    }

    public boolean isSameAs(String srcStr){
        return toString().equals(srcStr);
    }

    public boolean isReadable(){
        return true;
    }

    public String getName(){
        return name;
    }
}
