/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.i18n;

import java.util.Locale;

/**
 * This class represents a group of ExtendedLocalizable objects which
 * have a shared default locale.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocaleGroup {
    /**
     * The default group.
     */
    public final static LocaleGroup DEFAULT = new LocaleGroup();

    /**
     * The shared Locale.
     */
    protected Locale locale;

    /**
     * Sets the default locale for all the instances of ExtendedLocalizable
     * in this group. 
     */
    public void setLocale(Locale l) {
        locale = l;
    }

    /**
     * Gets the current default locale in this group, or null.
     */
    public Locale getLocale() {
        return locale;
    }
}
