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

package org.apache.batik.css.parser;

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

public class DefaultSelectorFactory implements SelectorFactory {

    /**
     * The instance of this class.
     */
    public final static SelectorFactory INSTANCE =
        new DefaultSelectorFactory();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultSelectorFactory() {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createConditionalSelector(SimpleSelector,Condition)}.
     */    
    public ConditionalSelector createConditionalSelector
        (SimpleSelector selector,
         Condition condition) 
	throws CSSException {
	return new DefaultConditionalSelector(selector, condition);
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
	return new DefaultElementSelector(namespaceURI, tagName);
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
	return new DefaultPseudoElementSelector(namespaceURI, pseudoName);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createDescendantSelector(Selector,SimpleSelector)}.
     */    
    public DescendantSelector createDescendantSelector
        (Selector parent,
         SimpleSelector descendant)
	throws CSSException {
	return new DefaultDescendantSelector(parent, descendant);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * SelectorFactory#createChildSelector(Selector,SimpleSelector)}.
     */    
    public DescendantSelector createChildSelector(Selector parent,
						  SimpleSelector child)
	throws CSSException {
	return new DefaultChildSelector(parent, child);
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
	return new DefaultDirectAdjacentSelector(nodeType, child,
                                                 directAdjacent);
    }
}
