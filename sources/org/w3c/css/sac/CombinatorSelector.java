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
 * @see Selector#SAC_DESCENDANT_SELECTOR
 * @see Selector#SAC_CHILD_SELECTOR
 * @see Selector#SAC_DIRECT_ADJACENT_SELECTOR
 * @see Selector#SAC_INDIRECT_ADJACENT_SELECTOR
 */
public interface CombinatorSelector extends Selector {
    
    /**
     * Returns the parent selector.
     */    
    public Selector getParentSelector();

    /*
     * Returns the simple selector.
     */    
    public SimpleSelector getSimpleSelector();
}
