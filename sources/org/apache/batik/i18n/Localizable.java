/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.i18n;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * This interface must be implemented by the classes which must provide a
 * way to override the default locale. 
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Localizable {
    /**
     * Provides a way to the user to specify a locale which override the
     * default one. If null is passed to this method, the used locale
     * becomes the global one. 
     * @param l The locale to set.
     */
    void setLocale(Locale l);

    /**
     * Returns the current locale or null if the locale currently used is
     * the default one.     
     */
    Locale getLocale();

    /**
     * Creates and returns a localized message, given the key of the message
     * in the resource bundle and the message parameters.
     * The messages in the resource bundle must have the syntax described in
     * the java.text.MessageFormat class documentation.
     * @param key  The key used to retreive the message from the resource
     *             bundle.
     * @param args The objects that compose the message.
     * @exception MissingResourceException if the key is not in the bundle.
     */
    String formatMessage(String key, Object[] args)
        throws MissingResourceException;
}
