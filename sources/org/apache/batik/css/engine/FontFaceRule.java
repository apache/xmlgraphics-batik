/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.apache.batik.util.ParsedURL;

/**
 * This class represents a @font-face CSS rule.
 *
 * This mostly exists to give us a place to store the 
 * URI to be used for 'src' URI resolution.
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class FontFaceRule implements Rule {
    /**
     * The type constant.
     */
    public final static short TYPE = (short)3;

    StyleMap sm;
    ParsedURL purl;
    public FontFaceRule(StyleMap sm, ParsedURL purl) {
        this.sm = sm;
        this.purl = purl;
    }
    
    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() { return TYPE; }

    /**
     * Returns the URI of the @font-face rule.
     */
    public ParsedURL getURL() {
        return purl;
    }

    /**
     * Returns the StyleMap from the @font-face rule.
     */
    public StyleMap getStyleMap() {
        return sm;
    }

    /**
     * Returns a printable representation of this rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        sb.append("@font-face { ");
        sb.append(sm.toString(eng));
        sb.append(" }\n");
        return sb.toString();
    }
};
