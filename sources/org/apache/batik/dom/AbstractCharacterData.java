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

package org.apache.batik.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This class implements the {@link org.w3c.dom.CharacterData} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCharacterData
    extends    AbstractChildNode
    implements CharacterData {
    /**
     * The value of this node.
     */
    protected String nodeValue = "";

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeValue()}.
     * @return {@link #nodeValue}.
     */
    public String getNodeValue() throws DOMException {
	return nodeValue;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setNodeValue(String)}.
     */
    public void setNodeValue(String nodeValue) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	// Node modification
	String val = this.nodeValue;
	this.nodeValue = (nodeValue == null) ? "" : nodeValue;

	// Mutation event
	fireDOMCharacterDataModifiedEvent(val, this.nodeValue);
	if (getParentNode() != null) {
	    ((AbstractParentNode)getParentNode()).
                fireDOMSubtreeModifiedEvent();
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.CharacterData#getData()}.
     * @return {@link #getNodeValue()}.
     */
    public String getData() throws DOMException {
	return getNodeValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#setData(String)}.
     */
    public void setData(String data) throws DOMException {
	setNodeValue(data);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.CharacterData#getLength()}.
     * @return {@link #nodeValue}.length().
     */
    public int getLength() {
	return nodeValue.length();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#substringData(int,int)}.
     */
    public String substringData(int offset, int count) throws DOMException {
	checkOffsetCount(offset, count);

	String v = getNodeValue();
	return v.substring(offset, Math.min(v.length(), offset + count));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#appendData(String)}.
     */
    public void appendData(String arg) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	setNodeValue(getNodeValue() + ((arg == null) ? "" : arg));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#insertData(int,String)}.
     */
    public void insertData(int offset, String arg) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	if (offset < 0 || offset > getLength()) {
	    throw createDOMException(DOMException.INDEX_SIZE_ERR,
				     "offset",
				     new Object[] { new Integer(offset) });
	}
	String v = getNodeValue();
	setNodeValue(v.substring(0, offset) + 
                     arg + v.substring(offset, v.length()));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#deleteData(int,int)}.
     */
    public void deleteData(int offset, int count) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	checkOffsetCount(offset, count);

	String v = getNodeValue();
	setNodeValue(v.substring(0, offset) +
		     v.substring(Math.min(v.length(), offset + count),
                                 v.length()));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#replaceData(int,int,String)}.
     */
    public void replaceData(int offset, int count, String arg)
        throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	checkOffsetCount(offset, count);

	String v = getNodeValue();
	setNodeValue(v.substring(0, offset) +
		     arg +
		     v.substring(Math.min(v.length(), offset + count),
                                 v.length()));
    }

    /**
     * Checks the given offset and count validity.
     */
    protected void checkOffsetCount(int offset, int count)
        throws DOMException {
	if (offset < 0 || offset >= getLength()) {
	    throw createDOMException(DOMException.INDEX_SIZE_ERR,
				     "offset",
				     new Object[] { new Integer(offset) });
	}
	if (count < 0) {
	    throw createDOMException(DOMException.INDEX_SIZE_ERR,
				     "negative.count",
				     new Object[] { new Integer(count) });
	}
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	AbstractCharacterData cd = (AbstractCharacterData)n;
	cd.nodeValue = nodeValue;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	AbstractCharacterData cd = (AbstractCharacterData)n;
	cd.nodeValue = nodeValue;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractCharacterData cd = (AbstractCharacterData)n;
	cd.nodeValue = nodeValue;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractCharacterData cd = (AbstractCharacterData)n;
	cd.nodeValue = nodeValue;
	return n;
    }
}
