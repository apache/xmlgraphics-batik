/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 * This class provides an implementation of the 'xml-stylesheet' processing
 * instructions.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleSheetProcessingInstruction
    extends AbstractProcessingInstruction
    implements LinkStyle {

    /**
     * Is this node immutable?
     */
    protected boolean readonly;

    /**
     * The style sheet.
     */
    protected transient StyleSheet sheet;

    /**
     * The stylesheet factory.
     */
    protected StyleSheetFactory factory;

    /**
     * The pseudo attributes.
     */
    protected transient HashTable pseudoAttributes;

    /**
     * Creates a new ProcessingInstruction object.
     */
    protected StyleSheetProcessingInstruction() {
    }

    /**
     * Creates a new ProcessingInstruction object.
     */
    public StyleSheetProcessingInstruction(String            data,
					   AbstractDocument  owner,
					   StyleSheetFactory f) {
	ownerDocument = owner;
	setData(data);
	factory = f;
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
	return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
	readonly = v;
    }

    /**
     * Sets the node name.
     */
    public void setNodeName(String v) {
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.ProcessingInstruction#getTarget()}.
     * @return "xml-stylesheet".
     */
    public String getTarget() {
	return "xml-stylesheet";
    }

    /**
     *  The style sheet. 
     */
    public StyleSheet getSheet() {
	if (sheet == null) {
	    sheet = factory.createStyleSheet(this, getPseudoAttributes());
	}
	return sheet;
    }

    /**
     * Returns the pseudo attributes in a table.
     */
    public HashTable getPseudoAttributes() {
        if (pseudoAttributes == null) {
            pseudoAttributes = new HashTable();
            pseudoAttributes.put("alternate", "no");
            pseudoAttributes.put("media",     "all");
            DOMUtilities.parseStyleSheetPIData(data, pseudoAttributes);
        }
        return pseudoAttributes;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.ProcessingInstruction#setData(String)}.
     */
    public void setData(String data) throws DOMException {
	super.setData(data);
	sheet = null;
        pseudoAttributes = null;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new StyleSheetProcessingInstruction();
    }
}
