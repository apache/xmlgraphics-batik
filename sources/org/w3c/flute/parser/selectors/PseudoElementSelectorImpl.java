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

import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Selector;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class PseudoElementSelectorImpl implements ElementSelector {

    String localName;

    /**
     * Creates a new ElementSelectorImpl
     */
    public PseudoElementSelectorImpl(String localName) {
        this.localName = localName;
    }
    
    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_PSEUDO_ELEMENT_SELECTOR;
    }
    
    /**
     * Returns the
     * <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace
     * URI</a> of this element selector.
     * <p><code>NULL</code> if this element selector can match any namespace.</p>
     */
    public String getNamespaceURI() {
	return null;
    }

    /**
     * Returns the
     * <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a>
     * of the
     * <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified
     * name</a> of this element.
     * <p><code>NULL</code> if this element selector can match any element.</p>
     * </ul>
     */
    public String getLocalName() {
	return localName;
    }
}
