/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik;

/**
 * This class defines the Batik version number.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class Version {
    public static final String LABEL_DEVELOPMENT_BUILD
        = "development.build";
    
    /**
     * @return the Batik version. This is based on the CVS tag.
     * If this Version is not part of a tagged release, then
     * the returned value is a constant reflecting a development
     * build.
     */
    public static String getVersion() {
        String tagName = "$Name$";
        if (tagName.startsWith("$Name:")) {
            tagName = tagName.substring(6, tagName.length()-1);
        } else {
            tagName = "";
        }
        
        if(tagName.trim().intern().equals("")){
            tagName = LABEL_DEVELOPMENT_BUILD;
        }

        return tagName;
    }
}
