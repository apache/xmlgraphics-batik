/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Describes a file source for the <tt>SVGConverter</tt>
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGConverterFileSource implements SVGConverterSource {
    File file;
    String ref;

    public SVGConverterFileSource(File file){
        this.file = file;
    }

    public SVGConverterFileSource(File file, String ref){
        this.file = file;
        this.ref = ref;
    }

    public String getName(){
        String name = file.getName();
        if (ref != null && !"".equals(ref)){
            name += "#" + ref;
        }
        return name;
    }

    public File getFile(){
        return file;
    }

    public String toString(){
        return getName();
    }

    public String getURI(){
        try{
            String uri = file.toURL().toString();
            if (ref != null && !"".equals(ref)){
                uri += "#" + ref;
            }
            return uri;
        } catch(MalformedURLException e){
            throw new Error();
        }
    }

    public boolean equals(Object o){
        if (o == null || !(o instanceof SVGConverterFileSource)){
            return false;
        }
        
        return file.equals(((SVGConverterFileSource)o).file);
    }

    public InputStream openStream() throws FileNotFoundException{
        return new FileInputStream(file);
    }

    public boolean isSameAs(String srcStr){
        if (file.toString().equals(srcStr)){
            return true;
        }

        return false;
    }
        
    public boolean isReadable(){
        return file.canRead();
    }
}

