/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Describes a file source for the <tt>SVGConverter</tt>
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGConverterFileSource implements SVGConverterSource {
    File file;

    public SVGConverterFileSource(File file){
        this.file = file;
    }

    public String getName(){
        return file.getName();
    }

    public File getFile(){
        return file;
    }

    public String toString(){
        return file.toString();
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

