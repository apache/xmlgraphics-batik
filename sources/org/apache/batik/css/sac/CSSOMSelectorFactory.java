/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class implements the {@link org.w3c.css.sac.SelectorFactory} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class CSSOMSelectorFactory implements SelectorFactory {
    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createConditionalSelector(SimpleSelector,Condition)}.
     */    
    public ConditionalSelector createConditionalSelector
        (SimpleSelector selector,
         Condition condition) 
	throws CSSException {
	return new CSSOMConditionalSelector(selector, condition);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createAnyNodeSelector()}.
     */    
    public SimpleSelector createAnyNodeSelector() throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createRootNodeSelector()}.
     */    
    public SimpleSelector createRootNodeSelector() throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createNegativeSelector(SimpleSelector)}.
     */    
    public NegativeSelector createNegativeSelector(SimpleSelector selector) 
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createElementSelector(String,String)}.
     */    
    public ElementSelector createElementSelector(String namespaceURI,
                                                 String tagName)
	throws CSSException {
	return new CSSOMElementSelector(namespaceURI, tagName);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createTextNodeSelector(String)}.
     */    
    public CharacterDataSelector createTextNodeSelector(String data)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createCDataSectionSelector(String)}.
     */    
    public CharacterDataSelector createCDataSectionSelector(String data)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createProcessingInstructionSelector(String,String)}.
     */    
    public ProcessingInstructionSelector createProcessingInstructionSelector
	(String target,
	 String data) throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SelectorFactory#createCommentSelector(String)}.
     */    
    public CharacterDataSelector createCommentSelector(String data)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createPseudoElementSelector(String,String)}.
     */    
    public ElementSelector createPseudoElementSelector(String namespaceURI, 
						       String pseudoName) 
	throws CSSException {
	return new CSSOMPseudoElementSelector(namespaceURI, pseudoName);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createDescendantSelector(Selector,SimpleSelector)}.
     */    
    public DescendantSelector createDescendantSelector
        (Selector parent,
         SimpleSelector descendant)
	throws CSSException {
	return new CSSOMDescendantSelector(parent, descendant);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createChildSelector(Selector,SimpleSelector)}.
     */    
    public DescendantSelector createChildSelector(Selector parent,
						  SimpleSelector child)
	throws CSSException {
	return new CSSOMChildSelector(parent, child);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createDirectAdjacentSelector(short,Selector,SimpleSelector)}.
     */
    public SiblingSelector createDirectAdjacentSelector
        (short          nodeType,
         Selector       child,
         SimpleSelector directAdjacent)
	throws CSSException {
	return new CSSOMDirectAdjacentSelector(nodeType, child,
                                               directAdjacent);
    }
}
