/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class provides a default implementation of the Localizable interface.
 * You can use it as a base class or as a member field and delegates various
 * work to it.<p>
 * For example, to implement Localizable, the following code can be used:
 * <pre>
 *  package mypackage;
 *  ...
 *  public class MyClass implements Localizable {
 *      // This code fragment requires a file named
 *      // 'mypackage/resources/Messages.properties', or a
 *      // 'mypackage.resources.Messages' class which extends
 *      // java.util.ResourceBundle, accessible using the current
 *      // classpath.
 *      LocalizableSupport localizableSupport =
 *          new LocalizableSupport("mypackage.resources.Messages");
 *
 *      public void setLocale(Locale l) {
 *          localizableSupport.setLocale(l);
 *      }
 *      public Local getLocale() {
 *          return localizableSupport.getLocale();
 *      }
 *      public String formatMessage(String key, Object[] args) {
 *          return localizableSupport.formatMessage(key, args);
 *      }
 *  }
 * </pre>
 * The algorithm for the Locale lookup in a LocalizableSupport object is:
 * <ul>
 *   <li>
 *     if a Locale has been set by a call to setLocale(), use this Locale,
 *     else,
 *   <li/>
 *   <li>
 *     if a Locale has been set by a call to the setDefaultLocale() method
 *     of a LocalizableSupport object in the current LocaleGroup, use this
 *     Locale, else,
 *   </li>
 *   <li>
 *     use the object returned by Locale.getDefault() (and set by
 *     Locale.setDefault()).
 *   <li/>
 * </ul>
 * This offers the possibility to have a different Locale for each object,
 * a Locale for a group of object and/or a Locale for the JVM instance.
 * <p>
 * Note: if no group is specified a LocalizableSupport object belongs to a
 * default group common to each instance of LocalizableSupport.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocalizableSupport implements Localizable {
    /**
     * The locale group to which this object belongs.
     */
    protected LocaleGroup localeGroup = LocaleGroup.DEFAULT;

    /**
     * The resource bundle classname.
     */
    protected String bundleName;

    /**
     * The classloader to use to create the resource bundle.
     */
    protected ClassLoader classLoader;

    /**
     * The current locale.
     */
    protected Locale locale;

    /**
     * The locale in use.
     */
    protected Locale usedLocale;

    /**
     * The resources
     */
    protected ResourceBundle resourceBundle;

    /**
     * Same as LocalizableSupport(s, null).
     */
    public LocalizableSupport(String s) {
        this(s, null);
    }

    /**
     * Creates a new Localizable object.
     * The resource bundle class name is required allows the use of custom
     * classes of resource bundles.
     * @param s  must be the name of the class to use to get the appropriate
     *           resource bundle given the current locale.
     * @param cl is the classloader used to create the resource bundle,
     *           or null.
     * @see java.util.ResourceBundle
     */
    public LocalizableSupport(String s, ClassLoader cl) {
        bundleName = s;
        classLoader = cl;
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public void setLocale(Locale l) {
        if (locale != l) {
            locale = l;
            resourceBundle = null;
        }
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.ExtendedLocalizable#setLocaleGroup(LocaleGroup)}.
     */
    public void setLocaleGroup(LocaleGroup lg) {
        localeGroup = lg;
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.ExtendedLocalizable#getLocaleGroup()}.
     */
    public LocaleGroup getLocaleGroup() {
        return localeGroup;
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.ExtendedLocalizable#setDefaultLocale(Locale)}.
     * Later invocations of the instance methods will lead to update the
     * resource bundle used.
     */
    public void setDefaultLocale(Locale l) {
        localeGroup.setLocale(l);
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.ExtendedLocalizable#getDefaultLocale()}.
     */
    public Locale getDefaultLocale() {
        return localeGroup.getLocale();
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args) {
        getResourceBundle();
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.ExtendedLocalizable#getResourceBundle()}.
     */
    public ResourceBundle getResourceBundle() {
        Locale l;

        if (resourceBundle == null) {
            if (locale == null) {
                if ((l = localeGroup.getLocale()) == null) {
                    usedLocale = Locale.getDefault();
                } else {
                    usedLocale = l;
                }
            } else {
                usedLocale = locale;
            }
            if (classLoader == null) {
                resourceBundle = ResourceBundle.getBundle(bundleName,
                                                          usedLocale);
            } else {
                resourceBundle = ResourceBundle.getBundle(bundleName,
                                                          usedLocale,
                                                          classLoader);
            }
        } else if (locale == null) {
            // Check for group Locale and JVM default locale changes.
            if ((l = localeGroup.getLocale()) == null) {
                if (usedLocale != (l = Locale.getDefault())) {
                    usedLocale = l;
                    if (classLoader == null) {
                        resourceBundle = ResourceBundle.getBundle(bundleName,
                                                                  usedLocale);
                    } else {
                        resourceBundle = ResourceBundle.getBundle(bundleName,
                                                                  usedLocale,
                                                                  classLoader);
                    }
                }
            } else if (usedLocale != l) {
                usedLocale = l;
                if (classLoader == null) {
                    resourceBundle = ResourceBundle.getBundle(bundleName,
                                                              usedLocale);
                } else {
                    resourceBundle = ResourceBundle.getBundle(bundleName,
                                                              usedLocale,
                                                              classLoader);
                }
            }
        }

        return resourceBundle;
    }
}
