/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.w3c.css.sac.SACMediaList;

/**
 * This class represents a list of rules.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleSheet {
    
    /**
     * The rules.
     */
    protected Rule[] rules = new Rule[16];
    
    /**
     * The number of rules.
     */
    protected int size;

    /**
     * The parent sheet, if any.
     */
    protected StyleSheet parent;

    /**
     * Whether or not this stylesheet is alternate.
     */
    protected boolean alternate;

    /**
     * The media to use to cascade properties.
     */
    protected SACMediaList media;

    /**
     * The style sheet title.
     */
    protected String title;

    /**
     * Sets the media to use to compute the styles.
     */
    public void setMedia(SACMediaList m) {
        media = m;
    }

    /**
     * Returns the media to use to compute the styles.
     */
    public SACMediaList getMedia() {
        return media;
    }

    /**
     * Returns the parent sheet.
     */
    public StyleSheet getParent() {
        return parent;
    }

    /**
     * Sets the parent sheet.
     */
    public void setParent(StyleSheet ss) {
        parent = ss;
    }

    /**
     * Sets the 'alternate' attribute of this style-sheet.
     */
    public void setAlternate(boolean b) {
        alternate = b;
    }

    /**
     * Tells whether or not this stylesheet is alternate.
     */
    public boolean isAlternate() {
        return alternate;
    }

    /**
     * Sets the 'title' attribute of this style-sheet.
     */
    public void setTitle(String t) {
        title = t;
    }

    /**
     * Returns the title of this style-sheet.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the number of rules.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the rule at the given index.
     */
    public Rule getRule(int i) {
        return rules[i];
    }

    /**
     * Clears the content.
     */
    public void clear() {
        size = 0;
        rules = new Rule[10];
    }

    /**
     * Appends a rule to the stylesheet.
     */
    public void append(Rule r) {
        if (size == rules.length) {
            Rule[] t = new Rule[size * 2];
            for (int i = 0; i < size; i++) {
                t[i] = rules[i];
            }
            rules = t;
        }
        rules[size++] = r;
    }

    /**
     * Returns a printable representation of this style-sheet.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(((Rule)rules[i]).toString(eng));
        }
        return sb.toString();
    }
}
