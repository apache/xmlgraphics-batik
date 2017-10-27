/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

import org.w3c.dom.DOMImplementation;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation},
 * {@link org.w3c.dom.css.DOMImplementationCSS} interfaces.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractDOMImplementation
        implements DOMImplementation,
                   Localizable,
                   Serializable {

    /**
     * The error messages bundle class name.
     */
    protected static final String RESOURCES =
        "org.apache.batik.dom.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES, getClass().getClassLoader());

    /**
     * The supported features.
     */
    protected final HashMap<String, Object> features = new HashMap<String, Object>();
    {
        // registerFeature("BasicEvents",        "3.0");
        registerFeature("Core",               new String[] { "2.0", "3.0" });
        registerFeature("XML",                new String[] { "1.0", "2.0",
                                                             "3.0" });
        registerFeature("Events",             new String[] { "2.0", "3.0" });
        registerFeature("UIEvents",           new String[] { "2.0", "3.0" });
        registerFeature("MouseEvents",        new String[] { "2.0", "3.0" });
        registerFeature("TextEvents",         "3.0");
        registerFeature("KeyboardEvents",     "3.0");
        registerFeature("MutationEvents",     new String[] { "2.0", "3.0" });
        registerFeature("MutationNameEvents", "3.0");
        registerFeature("Traversal",          "2.0");
        registerFeature("XPath",              "3.0");
    }
    
    /**
     * Registers a DOM feature.
     */
    protected void registerFeature(String name, Object value) {
        features.put(name.toLowerCase(), value);
    }

    /**
     * Creates a new AbstractDOMImplementation object.
     */
    protected AbstractDOMImplementation() {
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.DOMImplementation#hasFeature(String,String)}.
     */
    public boolean hasFeature(String feature, String version) {
        if (feature == null || feature.length() == 0) {
            return false;
        }
        if (feature.charAt(0) == '+') {
            // All features are directly castable.
            feature = feature.substring(1);
        }
        Object v = features.get(feature.toLowerCase());
        if (v == null) {
            return false;
        }
        if (version == null || version.length() == 0) {
            return true;
        }
        if (v instanceof String) {
            return version.equals(v);
        } else {
            String[] va = (String[])v;
            for (String aVa : va) {
                if (version.equals(aVa)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.DOMImplementation#getFeature(String,String)}.
     * No compound document support, so just return this DOMImlpementation
     * where appropriate.
     */
    public Object getFeature(String feature, String version) {
        if (hasFeature(feature, version)) {
            return this;
        }
        return null;
    }

    /**
     * Creates an DocumentEventSupport object suitable for use with this implementation.
     */
    public DocumentEventSupport createDocumentEventSupport() {
        return new DocumentEventSupport();
    }

    /**
     * Creates an EventSupport object for a given node.
     */
    public EventSupport createEventSupport(AbstractNode n) {
        return new EventSupport(n);
    }

    // Localizable //////////////////////////////////////////////////////

    /**
     * Implements {@link Localizable#setLocale(Locale)}.
     */
    public void setLocale(Locale l) {
        localizableSupport.setLocale(l);
    }

    /**
     * Implements {@link Localizable#getLocale()}.
     */
    public Locale getLocale() {
        return localizableSupport.getLocale();
    }

    protected void initLocalizable() {
    }

    /**
     * Implements {@link Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }
}
