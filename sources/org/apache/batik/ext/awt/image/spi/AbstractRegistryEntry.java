/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public abstract class AbstractRegistryEntry implements RegistryEntry{

    String name;
    float  priority;
    List   exts;
    
    public AbstractRegistryEntry(String    name,
                                 float     priority,
                                 String [] exts) {
        this.name     = name;
        this.priority = priority;
        this.exts     = new ArrayList(exts.length);
        for (int i=0; i<exts.length; i++) {
            this.exts.add(exts[i]);
        }

        this.exts = Collections.unmodifiableList(this.exts);
    }
			    
    public AbstractRegistryEntry(String name,
                                 float  priority,
                                 String ext) {
        this.name = name;
        this.priority = priority;
        this.exts = new ArrayList(1);
        this.exts.add(ext);
        this.exts = Collections.unmodifiableList(exts);
    }
			    

    public String getFormatName() {
        return name;
    }

    public List   getStandardExtensions() {
        return exts;
    }

    public float  getPriority() {
        return priority;
    }
}
