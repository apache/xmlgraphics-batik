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

import java.net.URL;
import java.net.MalformedURLException;

/*
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGConverterURLSource implements SVGConverterSource {
    /** 
     * SVG file extension 
     */
    protected static final String SVG_EXTENSION = ".svg";

    //
    // Reported when the URL for one of the sources is
    // invalid. This will happen if the URL is malformed or
    // if the URL file does not end with the ".svg" extension.
    // This is needed to be able to create a file name for
    // the ouptut automatically.
    //
    public static final String ERROR_INVALID_URL
        = "SVGConverterURLSource.error.invalid.url";

    URL url;
    String name;

    public SVGConverterURLSource(String url) throws SVGConverterException{
        try{
            this.url = new URL(url);
        } catch (MalformedURLException e){
            throw new SVGConverterException(ERROR_INVALID_URL, 
                                            new Object[]{url});
        }

        // Get the path portion
        String path = this.url.getFile();
        if (path == null || !path.toLowerCase().endsWith(SVG_EXTENSION)){
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
        String ref = this.url.getRef();
        if (ref != null && !"".equals(ref)) {
            name += "" + ref.hashCode();
        }
    }

    public String toString(){
        return url.toString();
    }

    public boolean equals(Object o){
        if (o == null || !(o instanceof SVGConverterURLSource)){
            return false;
        }

        return url.equals(((SVGConverterURLSource)o).url);
    }

    public InputStream openStream() throws IOException {
        return url.openStream();
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
