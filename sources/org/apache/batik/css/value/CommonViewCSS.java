/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.css.CSSOMStyleSheet;
import org.w3c.dom.Document;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.views.DocumentView;

/**
 * This class represents a ViewCSS object initialized to manage
 * the CSS values common to CSS2 ans SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class CommonViewCSS extends AbstractViewCSS {
    /**
     * Creates a new ViewCSS object.
     * @param doc The document view associated with this abstract view.
     * @param ctx The application context.
     */
    protected CommonViewCSS(DocumentView doc, CommonCSSContext ctx) {
	super(doc);
	addRelativeValueResolver(new ClipResolver());
	addRelativeValueResolver(new ColorResolver(ctx));
	addRelativeValueResolver(new CursorResolver());
	addRelativeValueResolver(new DirectionResolver());
 	addRelativeValueResolver(new DisplayResolver());
	addRelativeValueResolver(new FontFamilyResolver(ctx));
	addRelativeValueResolver(new FontSizeResolver());
	addRelativeValueResolver(new FontSizeAdjustResolver());
	addRelativeValueResolver(new FontStretchResolver());
	addRelativeValueResolver(new FontStyleResolver());
	addRelativeValueResolver(new FontVariantResolver());
	addRelativeValueResolver(new FontWeightResolver());
	addRelativeValueResolver(new SpacingResolver
                                 (ValueConstants.CSS_LETTER_SPACING_PROPERTY));
	addRelativeValueResolver(new OverflowResolver());
	addRelativeValueResolver(new TextDecorationResolver());
	addRelativeValueResolver(new UnicodeBidiResolver());
	addRelativeValueResolver(new VisibilityResolver());
	addRelativeValueResolver(new SpacingResolver
                                 (ValueConstants.CSS_WORD_SPACING_PROPERTY));

        Document document = (Document)doc;
        DOMImplementationCSS impl =
            (DOMImplementationCSS)document.getImplementation();
        

        String uri  = ctx.getUserStyleSheetURI();
        if (uri != null) {
            userAgentStyleSheet =
                impl.createCSSStyleSheet("User Style Sheet", "all");
            try {
                CSSDocumentHandler.parseStyleSheet
                    ((CSSOMStyleSheet)userAgentStyleSheet, uri);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
