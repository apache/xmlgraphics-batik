/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

/**
 * This class is an adapter of the {@link org.w3c.css.sac.DocumentHandler}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentHandlerAdapter implements DocumentHandler {
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startDocument(InputSource)}.
     */
    public void startDocument(InputSource source) throws CSSException {
        // !!! Internationalization
	throw new CSSException("Illegal CSS Text");
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endDocument(InputSource)}.
     */
    public void endDocument(InputSource source) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#comment(String)}.
     */
    public void comment(String text) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#ignorableAtRule(String)}.
     */
    public void ignorableAtRule(String atRule) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#namespaceDeclaration(String,String)}.
     */
    public void namespaceDeclaration(String prefix, String uri)
        throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     *org.w3c.css.sac.DocumentHandler#importStyle(String,SACMediaList,String)}.
     */
    public void importStyle(String uri, SACMediaList media,
                            String defaultNamespaceURI)
	throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startMedia(SACMediaList)}.
     */
    public void startMedia(SACMediaList media) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endMedia(SACMediaList)}.
     */
    public void endMedia(SACMediaList media) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startPage(String,String)}.
     */    
    public void startPage(String name, String pseudo_page)
        throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endPage(String,String)}.
     */
    public void endPage(String name, String pseudo_page) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startFontFace()}.
     */
    public void startFontFace() throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endFontFace()}.
     */
    public void endFontFace() throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startSelector(SelectorList)}.
     */
    public void startSelector(SelectorList selectors) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endSelector(SelectorList)}.
     */
    public void endSelector(SelectorList selectors) throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#property(String,LexicalUnit,boolean)}.
     */
    public void property(String name, LexicalUnit value, boolean important)
	throws CSSException {
	throw new CSSException("Illegal CSS Text");
    }
}
