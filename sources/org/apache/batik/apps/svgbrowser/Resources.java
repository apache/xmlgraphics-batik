/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class manages the message for the Swing extensions.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class Resources {

    /**
     * This class does not need to be instantiated.
     */
    protected Resources() { }

    /**
     * The error messages bundle class name.
     */
    protected final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.GUI";

    /**
     * The localizable support for the error messages.
     */
    protected static LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    /**
     * The resource manager to decode messages.
     */
    protected static ResourceManager resourceManager =
        new ResourceManager(localizableSupport.getResourceBundle());

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public static void setLocale(Locale l) {
        localizableSupport.setLocale(l);
        resourceManager = new ResourceManager(localizableSupport.getResourceBundle());
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public static Locale getLocale() {
        return localizableSupport.getLocale();
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public static String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    public static String getString(String key)
        throws MissingResourceException {
        return resourceManager.getString(key);
    }

    public static int getInteger(String key) 
        throws MissingResourceException {
        return resourceManager.getInteger(key);
    }

    public static int getCharacter(String key)
        throws MissingResourceException {
        return resourceManager.getCharacter(key);
    }
}
