/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.parser.ParseException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents a parser for SVG style attributes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleAttributeParser implements Localizable {
    /**
     * The resources bundle classname.
     */
    public final static String BUNDLE_CLASSNAME =
        "org.apache.batik.parser.style.resources.Messages";

    /**
     * The localizable support.
     */
    final static LocalizableSupport LOCALIZABLE_SUPPORT =
        new LocalizableSupport(BUNDLE_CLASSNAME);

    /**
     * The underlying CSS parser.
     */
    protected Parser parser;

    /**
     * The ErrorHandler.
     */
    protected ErrorHandler errorHandler;

    /**
     * The value factories.
     */
    protected Map factories = new HashMap(5);

    /**
     * Creates a new StyleAttributeParser object.
     * @param s The name of a SAC compliant CSS parser class.
     */
    public StyleAttributeParser(String s)
        throws ParseException {
        try {
            parser = (Parser)Class.forName(s).newInstance();
        } catch (Exception e) {
            throw new ParseException
                (formatMessage("creation.exception",
                               new Object[] { e.getMessage() }),
                 e);
        }
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public void setLocale(Locale l) {
        parser.setLocale(l);
        LOCALIZABLE_SUPPORT.setLocale(l);
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public Locale getLocale() {
        Locale l;
        return ((l = LOCALIZABLE_SUPPORT.getLocale()) == null)
            ? Locale.getDefault()
            : l;
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args) {
        return LOCALIZABLE_SUPPORT.formatMessage(key, args);
    }

    /**
     * Parses the given reader and returns the CSSValue object
     * corresponding to the attribute with the given namespaceURI
     * and localName.
     * @param ns The namespace URI of the attribute to parse.
     * @param ln The local name of the attribute to parse.
     */
    public CSSValue parse(Reader r, String ns, String ln)
        throws ParseException {
        ns = (ns == null) ? "" : ns;
        Map m = (Map)factories.get(ns);
        if (m == null) {
            throw new ParseException
                (formatMessage("unknown.attribute",
                               new Object[] { ns, ln }),
                 -1, -1);
        }
        CSSValueFactory f = (CSSValueFactory)m.get(ln);
        if (f == null) {
            throw new ParseException
                (formatMessage("unknown.attribute",
                               new Object[] { ns, ln }),
                 -1, -1);
        }
        try {
            LexicalUnit lu = parser.parsePropertyValue(new InputSource(r));
            return f.createCSSValue(lu);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException
                (formatMessage("parser.exception",
                               new Object[] { e.getMessage() }),
                 e);
        }
    }

    /**
     * Sets the ErrorHandler.
     */
    public void setErrorHandler(ErrorHandler e) {
        parser.setErrorHandler(errorHandler = e);
    }

    /**
     * Gets the ErrorHandler.
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Allows the user to register its own ValueFactory to create a
     * style attribute value. 
     * @param ns The namespace URI of the attribute associated with
     *           the given factory.
     * @param ln The local name of the attribute associated with
     *           the given factory.
     */
    public void putCSSValueFactory(String ns, String ln, CSSValueFactory vf) {
        ns = (ns == null) ? "" : ns;
        Map m = (Map)factories.get(ns);
        if (m == null) {
            factories.put(ns, m = new HashMap(5));
        }
        m.put(ln, vf);
    }

    /**
     * Allows the user to unregister a CSSValueFactory.
     * @param ns The namespace URI of the attribute associated with
     *           the factory to remove.
     * @param ln The local name of the attribute associated with
     *           the factory to remove.
     */
    public void removeCSSValueFactory(String ns, String ln) {
        ns = (ns == null) ? "" : ns;
        Map m = (Map)factories.get(ns);
        if (m != null) {
            m.remove(ln);
        }
    }
}
