/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import java.util.Set;

import org.w3c.css.sac.LangCondition;
import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.LangCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class CSSLangCondition
    implements LangCondition,
	       ExtendedCondition {
    /**
     * The language.
     */
    protected String lang;

    /**
     * Creates a new LangCondition object.
     */
    public CSSLangCondition(String lang) {
	this.lang = lang;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	CSSLangCondition c = (CSSLangCondition)obj;
	return c.lang.equals(lang);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_LANG_CONDITION;
    }

    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.LangCondition#getLang()}.
     */
    public String getLang() {
	return lang;
    }

    /**
     * Returns the specificity of this condition.
     */
    public int getSpecificity() {
	return 1 << 8;
    }

    /**
     * Tests whether this condition matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return e.getAttribute("lang").startsWith(getLang());
    }

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    public void fillAttributeSet(Set attrSet) {
        attrSet.add("lang");
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	return ":lang(" + lang + ")";
    }
}
