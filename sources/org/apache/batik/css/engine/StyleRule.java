/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.w3c.css.sac.SelectorList;

/**
 * This class represents a style rule.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleRule implements Rule {
    
    /**
     * The type constant.
     */
    public final static short TYPE = (short)0;

    /**
     * The selector list.
     */
    protected SelectorList selectorList;

    /**
     * The style declaration.
     */
    protected StyleDeclaration styleDeclaration;

    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() {
        return TYPE;
    }

    /**
     * Sets the selector list.
     */
    public void setSelectorList(SelectorList sl) {
        selectorList = sl;
    }

    /**
     * Returns the selector list.
     */
    public SelectorList getSelectorList() {
        return selectorList;
    }

    /**
     * Sets the style map.
     */
    public void setStyleDeclaration(StyleDeclaration sd) {
        styleDeclaration = sd;
    }

    /**
     * Returns the style declaration.
     */
    public StyleDeclaration getStyleDeclaration() {
        return styleDeclaration;
    }

    /**
     * Returns a printable representation of this style rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        if (selectorList != null) {
            sb.append(selectorList.item(0));
            for (int i = 1; i < selectorList.getLength(); i++) {
                sb.append(", ");
                sb.append(selectorList.item(i));
            }
        }
        sb.append(" {\n");
        if (styleDeclaration != null) {
            sb.append(styleDeclaration.toString(eng));
        }
        sb.append("}\n");
        return sb.toString();
    }
}
