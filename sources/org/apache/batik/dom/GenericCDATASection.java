/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;

/**
 * This class implements the {@link org.w3c.dom.CDATASection} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericCDATASection extends AbstractText implements CDATASection {
    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new CDATASection object.
     */
    public GenericCDATASection() {
    }

    /**
     * Creates a new CDATASection object.
     */
    public GenericCDATASection(String value, AbstractDocument owner) {
	ownerDocument = owner;
	setNodeValue(value);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return "#cdata-section".
     */
    public String getNodeName() {
	return "#cdata-section";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#CDATA_SECTION_NODE}
     */
    public short getNodeType() {
	return CDATA_SECTION_NODE;
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
     * Creates a text node of the current type.
     */
    protected Text createTextNode(String text) {
	return getOwnerDocument().createCDATASection(text);
    }
}
