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

package org.apache.batik.dom.svg;

import java.net.URL;

import org.apache.batik.css.engine.CSSImportedElementRoot;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides support for the xml:base attribute.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class XMLBaseSupport implements XMLConstants {
    
    /**
     * This class does not need to be instanciated.
     */
    protected XMLBaseSupport() {
    }

    /**
     * Returns the xml:base attribute value of the given element.
     */
    public static String getXMLBase(Element elt) {
        return elt.getAttributeNS(XML_NAMESPACE_URI, "base");
    }

    /**
     * Returns the xml:base attribute value of the given element
     * Resolving any dependency on parent bases if needed.
     */
    public static String getCascadedXMLBase(Element elt) {
        String base = null;
        Node n = elt.getParentNode();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                base = getCascadedXMLBase((Element)n);
                break;
            }
            if (n instanceof CSSImportedElementRoot) {
                n = ((CSSImportedElementRoot)n).getCSSParentElement();
            } else {
                n = n.getParentNode();
            }
        }
        if (base == null) {
            SVGOMDocument svgDoc;
            svgDoc = (SVGOMDocument)elt.getOwnerDocument();
            URL url = svgDoc.getURLObject();
            if (url != null) {
                base = url.toString();
            }
        }
        Attr attr = elt.getAttributeNodeNS(XML_NAMESPACE_URI, "base");
        if (attr != null) {
            if (base == null) {
                base = attr.getNodeValue();
            } else {
                base = new ParsedURL(base, attr.getNodeValue()).toString();
            }
        }
        return base;
    }

}
