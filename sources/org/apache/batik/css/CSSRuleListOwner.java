/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;

/**
 * This interface represents the objects which own a list of CSSRules.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSRuleListOwner {
    /**
     * Returns the parser used to read style sheets.
     */
    Parser getParser();

    /**
     * Returns the map of value factories.
     */
    ValueFactoryMap getValueFactoryMap();

    /**
     *  Used to insert a new rule into the list.  
     */
    int insertRule(String rule, int index) throws DOMException;

    /**
     * Used to delete a rule from the list. 
     */
    void deleteRule(int index) throws DOMException;

    /**
     * Appends a rule.
     */
    void appendRule(CSSRule r);
}
