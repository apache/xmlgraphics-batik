/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This interface provides much more control over internationalization
 * than the Localizable interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ExtendedLocalizable extends Localizable {
    /**
     * Sets the group to which this object belongs.
     */
    void setLocaleGroup(LocaleGroup lg);

    /**
     * Returns the group to which this object belongs.
     */
    LocaleGroup getLocaleGroup();

    /**
     * Sets the default locale for all the instances of this class in
     * the same LocaleGroup. 
     */
    void setDefaultLocale(Locale l);

    /**
     * Gets the current default locale in the LocaleGroup.
     */
    Locale getDefaultLocale();

    /**
     * Returns the current resource bundle. Getting this object gives access
     * to the keys in the bundle, raw string resources, arrays of raw string
     * resources and object resources.
     */
    ResourceBundle getResourceBundle();
}
