/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.i18n.LocalizableSupport;
import org.w3c.dom.DOMException;

/**
 * This class contains a method to create a DOMException object
 * with a localized message. The localization of the CSS engine
 * is effective for each instance in the JVM.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSDOMExceptionFactory {
    /**
     * The resource bundle class name.
     */
    protected final static String RESOURCES =
        "org.apache.batik.css.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected final static LocalizableSupport LOCALIZABLE_SUPPORT =
        new LocalizableSupport(RESOURCES);

    /**
     * This class does not need to be instantiated.
     */
    protected CSSDOMExceptionFactory() {
    }

    /**
     * Sets the locale used for error messages.
     */
    public static void setLocale(Locale l) {
        LOCALIZABLE_SUPPORT.setLocale(l);
    }

    /**
     * Creates an exception with the appropriate error message.
     * @param type The DOMException type.
     * @param key  The message key in the resource bundle.
     * @param args The message arguments.
     */
    public static DOMException createDOMException(short    type,
						  String   key,
						  Object[] args) {
	try {
            return new DOMException
                (type, LOCALIZABLE_SUPPORT.formatMessage(key, args));
        } catch (MissingResourceException e) {
            return new DOMException(type, key);
        }
    }
}
