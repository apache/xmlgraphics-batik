/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.InputStream;
import java.io.IOException;

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
