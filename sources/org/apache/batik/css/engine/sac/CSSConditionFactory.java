/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;


/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.ConditionFactory} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSConditionFactory implements ConditionFactory {

    /**
     * The class attribute namespace URI.
     */
    protected String classNamespaceURI;

    /**
     * The class attribute local name.
     */
    protected String classLocalName;
    
    /**
     * The id attribute namespace URI.
     */
    protected String idNamespaceURI;

    /**
     * The id attribute local name.
     */
    protected String idLocalName;
    
    /**
     * Creates a new condition factory.
     */
    public CSSConditionFactory(String cns,  String cln,
                               String idns, String idln) {
        classNamespaceURI = cns;
        classLocalName = cln;
        idNamespaceURI = idns;
        idLocalName = idln;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * ConditionFactory#createAndCondition(Condition,Condition)}.
     */    
    public CombinatorCondition createAndCondition(Condition first,
                                                  Condition second)
	throws CSSException {
	return new CSSAndCondition(first, second);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * ConditionFactory#createOrCondition(Condition,Condition)}.
     */    
    public CombinatorCondition createOrCondition(Condition first,
                                                 Condition second)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createNegativeCondition(Condition)}.
     */    
    public NegativeCondition createNegativeCondition(Condition condition)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * ConditionFactory#createPositionalCondition(int,boolean,boolean)}.
     */    
    public PositionalCondition createPositionalCondition(int position, 
							 boolean typeNode, 
							 boolean type)
	throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     *ConditionFactory#createAttributeCondition(String,String,boolean,String)}.
     */    
    public AttributeCondition createAttributeCondition(String localName,
						       String namespaceURI,
						       boolean specified,
						       String value)
	throws CSSException {
	return new CSSAttributeCondition(localName, namespaceURI, specified,
                                           value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createIdCondition(String)}.
     */    
    public AttributeCondition createIdCondition(String value)
        throws CSSException {
	return new CSSIdCondition(idNamespaceURI, idLocalName, value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createLangCondition(String)}.
     */    
    public LangCondition createLangCondition(String lang) throws CSSException {
	return new CSSLangCondition(lang);
    }

    /**
     * <b>SAC</b>: Implements {@link
 ConditionFactory#createOneOfAttributeCondition(String,String,boolean,String)}.
     */    
    public AttributeCondition createOneOfAttributeCondition(String localName,
							    String nsURI,
							    boolean specified,
							    String value)
	throws CSSException {
	return new CSSOneOfAttributeCondition(localName, nsURI, specified,
                                                value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * ConditionFactory#createBeginHyphenAttributeCondition(String,String,boolean,String)}.
     */    
    public AttributeCondition createBeginHyphenAttributeCondition
        (String localName,
         String namespaceURI,
         boolean specified,
         String value)
	throws CSSException {
	return new CSSBeginHyphenAttributeCondition
	    (localName, namespaceURI, specified, value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createClassCondition(String,String)}.
     */    
    public AttributeCondition createClassCondition(String namespaceURI,
						   String value)
	throws CSSException {
	return new CSSClassCondition(classLocalName, classNamespaceURI, value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * ConditionFactory#createPseudoClassCondition(String,String)}.
     */    
    public AttributeCondition createPseudoClassCondition(String namespaceURI,
							 String value)
	throws CSSException {
	return new CSSPseudoClassCondition(namespaceURI, value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createOnlyChildCondition()}.
     */    
    public Condition createOnlyChildCondition() throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createOnlyTypeCondition()}.
     */    
    public Condition createOnlyTypeCondition() throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionFactory#createContentCondition(String)}.
     */    
    public ContentCondition createContentCondition(String data)
        throws CSSException {
	throw new CSSException("Not implemented in CSS2");
    }
}
