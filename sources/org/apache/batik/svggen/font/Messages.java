/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen.font;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

/**
 * This class manages the message for the bridge module.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class Messages {

    /**
     * This class does not need to be instantiated.
     */
    protected Messages() { }

    /**
     * The error messages bundle class name.
     */
    protected final static String RESOURCES =
        "org.apache.batik.svggen.font.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected static LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public static void setLocale(Locale l) {
        localizableSupport.setLocale(l);
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
}
