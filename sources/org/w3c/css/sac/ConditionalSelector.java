/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id$
 */
package org.w3c.css.sac;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 * @see Selector#SAC_CONDITIONAL_SELECTOR
 */
public interface ConditionalSelector extends SimpleSelector {

    /**
     * Returns the simple selector.
     * <p>The simple selector can't be a <code>ConditionalSelector</code>.</p>
     */    
    public SimpleSelector getSimpleSelector();

    /**
     * Returns the condition to be applied on the simple selector.
     */    
    public Condition getCondition();
}
