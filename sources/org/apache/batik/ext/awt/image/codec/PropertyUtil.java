/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.codec;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

public class PropertyUtil {
    protected final static String RESOURCES =
        "org.apache.batik.refimpl.bridge.resources.properties";


    protected static LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    public static String getString(String key) {
        try{
            return localizableSupport.formatMessage(key, null);
        }catch(MissingResourceException e){
            return key;
        }
   }
}
