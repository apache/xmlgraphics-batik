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

package org.apache.batik.dom.util;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * This class provides support for XLink features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XLinkSupport {
    /**
     * The xlink namespace URI.
     */
    public final static String XLINK_NAMESPACE_URI =
	"http://www.w3.org/1999/xlink";

    /**
     * Returns the value of the 'xlink:type' attribute of the given element.
     */
    public static String getXLinkType(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "type");
    }

    /**
     * Sets the value of the 'xlink:type' attribute of the given element.
     */
    public static void setXLinkType(Element elt, String str) {
	if (!"simple".equals(str)   &&
	    !"extended".equals(str) &&
	    !"locator".equals(str)  &&
	    !"arc".equals(str)) {
	    throw new DOMException(DOMException.SYNTAX_ERR,
                                   "xlink:type='" + str + "'");
	}
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "type", str);
    }

    /**
     * Returns the value of the 'xlink:role' attribute of the given element.
     */
    public static String getXLinkRole(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "role");
    }

    /**
     * Sets the value of the 'xlink:role' attribute of the given element.
     */
    public static void setXLinkRole(Element elt, String str) {
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "role", str);
    }

    /**
     * Returns the value of the 'xlink:arcrole' attribute of the given element.
     */
    public static String getXLinkArcRole(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "arcrole");
    }

    /**
     * Sets the value of the 'xlink:arcrole' attribute of the given element.
     */
    public static void setXLinkArcRole(Element elt, String str) {
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "arcrole", str);
    }

    /**
     * Returns the value of the 'xlink:title' attribute of the given element.
     */
    public static String getXLinkTitle(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "title");
    }

    /**
     * Sets the value of the 'xlink:title' attribute of the given element.
     */
    public static void setXLinkTitle(Element elt, String str) {
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "title", str);
    }

    /**
     * Returns the value of the 'xlink:show' attribute of the given element.
     */
    public static String getXLinkShow(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "show");
    }

    /**
     * Sets the value of the 'xlink:show' attribute of the given element.
     */
    public static void setXLinkShow(Element elt, String str) {
	if (!"new".equals(str)   &&
	    !"replace".equals(str)  &&
	    !"embed".equals(str)) {
	    throw new DOMException(DOMException.SYNTAX_ERR,
                                   "xlink:show='" + str + "'");
	}
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "show", str);
    }

    /**
     * Returns the value of the 'xlink:actuate' attribute of the given element.
     */
    public static String getXLinkActuate(Element elt) {
	return elt.getAttributeNS(XLINK_NAMESPACE_URI, "actuate");
    }

    /**
     * Sets the value of the 'xlink:actuate' attribute of the given element.
     */
    public static void setXLinkActuate(Element elt, String str) {
	if (!"onReplace".equals(str) && !"onLoad".equals(str)) {
	    throw new DOMException(DOMException.SYNTAX_ERR,
                                   "xlink:actuate='" + str + "'");
	}
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "actuate", str);
    }

    /**
     * Returns the value of the 'xlink:href' attribute of the given element.
     */
    public static String getXLinkHref(Element elt) {
        return elt.getAttributeNS(XLINK_NAMESPACE_URI, "href");
    }

    /**
     * Sets the value of the 'xlink:href' attribute of the given element.
     */
    public static void setXLinkHref(Element elt, String str) {
	elt.setAttributeNS(XLINK_NAMESPACE_URI, "href", str);
    }
}
