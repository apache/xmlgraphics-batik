/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.io.StringReader;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;

/**
 * This class implements the {@link org.w3c.dom.css.CSSImportRule} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMImportRule extends AbstractCSSRule implements CSSImportRule {
    /**
     * The parent StyleSheet.
     */
    protected CSSStyleSheet styleSheet;

    /**
     * The Href
     */
    protected String href;

    /**
     * The media.
     */
    protected MediaList media;

    /**
     * The CSS document handler.
     */
    protected DocumentHandler ruleHandler = new ImportRuleHandler();

    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * Creates a new CSSOMImportRule object.
     */
    public CSSOMImportRule(CSSStyleSheet   parent,
			   String          href,
			   MediaList       media) {
	super(parent, null);
	this.href = href;
	this.media = media;
	CSSOMStyleSheet ss = (CSSOMStyleSheet)parent;
	parser = ss.getParser();
	try {
	    Parser p = CSSDocumentHandler.createParser();
	    styleSheet = new CSSOMStyleSheet(null,
					     parent,
					     href,
					     null,
					     media,
					     this,
					     ss.getValueFactoryMap(),
					     p);
	    CSSDocumentHandler.parseStyleSheet((CSSOMStyleSheet)styleSheet,
                                               href);
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "syntax.error.at",
		 new Object[] { href });
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getType()}.
     */
    public short getType() {
	return IMPORT_RULE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getCssText()}.
     */
    public String getCssText() {
	if (media == null) {
	    return "@import \"" + href + "\";";
	}
	return "@import \"" + href + "\" " + media.getMediaText() + ";";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSRule#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	try {
	    parser.setDocumentHandler(ruleHandler);
	    parser.parseRule(new InputSource(new StringReader(cssText)));
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "rule.syntax.error",
		 new Object[] { cssText });
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSImportRule#getHref()}.
     */
    public String getHref() {
	return href;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSImportRule#getMedia()}.
     */
    public MediaList getMedia() {
	return media;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSImportRule#getStyleSheet()}.
     */
    public CSSStyleSheet getStyleSheet() {
	return styleSheet;
    }

    /**
     * To handle an import rule.
     */
    protected class ImportRuleHandler extends DocumentHandlerAdapter {
	/**
	 * Receive notification of a import statement in the style sheet.
	 */
	public void importStyle(String uri,
				SACMediaList m,
				String defaultNamespaceURI)
	    throws CSSException {
	    href = uri;
	    media = new DOMMediaList();
	    for (int i = 0; i < m.getLength(); i++) {
		media.appendMedium(m.item(i));
	    }
	    try {
		CSSOMStyleSheet ss = (CSSOMStyleSheet)getParentStyleSheet();
		Parser p = CSSDocumentHandler.createParser();
		styleSheet = new CSSOMStyleSheet(null,
						 ss,
						 href,
						 null,
						 media,
						 CSSOMImportRule.this,
						 ss.getValueFactoryMap(),
						 p);
		CSSDocumentHandler.parseStyleSheet
                    ((CSSOMStyleSheet)styleSheet, href);
	    } catch (CSSException e) {
		throw e;
	    } catch (Exception e) {
		throw new CSSException(e);
	    }
	}
    }
}
