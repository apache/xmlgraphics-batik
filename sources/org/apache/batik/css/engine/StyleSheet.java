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
            sb.append(rules[i].toString(eng));
        }
        return sb.toString();
    }
}
