/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id$
 */
package org.w3c.flute.parser.selectors;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.Condition;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class PseudoClassConditionImpl implements AttributeCondition {

    String value;
    
    /**
     * Creates a new AttributeConditionImpl
     */
    public PseudoClassConditionImpl(String value) {
	this.value     = value;
    }
    
    /**
     * An integer indicating the type of <code>Condition</code>.
     */    
    public short getConditionType() {
	return Condition.SAC_PSEUDO_CLASS_CONDITION;
    }

    /**
     * Returns the
     * <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace
     * URI</a> of this attribute condition.
     * <p><code>NULL</code> if :
     * <ul>
     * <li>this attribute condition can match any namespace.
     * <li>this attribute is an id attribute.
     * </ul>
     */    
    public String getNamespaceURI() {
	return null;
    }

    /**
     * Returns the
     * <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a>
     * of the
     * <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified
     * name</a> of this attribute.
     * <p><code>NULL</code> if :
     * <ul>
     * <li><p>this attribute condition can match any attribute.
     * <li><p>this attribute is a class attribute.
     * <li><p>this attribute is an id attribute.
     * <li><p>this attribute is a pseudo-class attribute.
     * </ul>
     */
    public String getLocalName() {
	return null;
    }

    /**
     * Returns <code>true</code> if the attribute must have an explicit value
     * in the original document, <code>false</code> otherwise. If this is a
     * pseudo class, the return value is unspecified.
     * <p><code>false</code> if:
     * <ul>
     * <li>if this is an id attribute.
     * <li>if this is a pseudo class a class attribute.
     * </ul>
     */
    public boolean getSpecified() {
	return true;
    }

    /**
     * Returns the value of the attribute.
     * If this attribute is a class or a pseudo class attribute, you'll get
     * the class name (or psedo class name) without the '.' or ':'.
     */
    public String getValue() {
	return value;
    }
}

