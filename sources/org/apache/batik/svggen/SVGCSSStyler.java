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

package org.apache.batik.svggen;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This utility class converts a standard SVG document that uses
 * attribute into one that uses the CSS style attribute instead
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGCSSStyler implements SVGSyntax{
    static private String CSS_PROPERTY_VALUE_SEPARATOR = ":";
    static private String CSS_RULE_SEPARATOR = ";";
    static private String SPACE = " ";

    /**
     * Invoking this method removes all the styling attributes
     * (such as 'fill' or 'fill-opacity') from the input element
     * and its descendant and replaces them with their CSS2
     * property counterparts.
     * @param node SVG Node to be converted to use style
     *
     */
    public static void style(Node node){
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null){
            // Has to be an Element, as it has attributes
            // According to spec.
            Element element = (Element)node;
            StringBuffer styleAttrBuffer = new StringBuffer();
            int nAttr = attributes.getLength();
            Vector toBeRemoved = new Vector();
            for(int i=0; i<nAttr; i++){
                Attr attr = (Attr)attributes.item(i);
                if(SVGStylingAttributes.set.contains(attr.getName())){
                    // System.out.println("Found new style attribute");
                    styleAttrBuffer.append(attr.getName());
                    styleAttrBuffer.append(CSS_PROPERTY_VALUE_SEPARATOR);
                    styleAttrBuffer.append(attr.getValue());
                    styleAttrBuffer.append(CSS_RULE_SEPARATOR);
                    styleAttrBuffer.append(SPACE);
                    toBeRemoved.addElement(attr.getName());
                }
            }

            if(styleAttrBuffer.length() > 0){
                                // System.out.println("Setting style attribute on node: " + styleAttrBuffer.toString().trim());
                                // There were some styling attributes
                element.setAttributeNS(null,
                                       SVG_STYLE_ATTRIBUTE,
                                       styleAttrBuffer.toString().trim());

                int n = toBeRemoved.size();
                for(int i=0; i<n; i++)
                    element.removeAttribute((String)toBeRemoved.elementAt(i));
            }
            // else
            // System.out.println("NO STYLE PROPERTIES");
        }

        // Now, process child elements
        NodeList children = node.getChildNodes();
        int nChildren = children.getLength();
        for(int i=0; i<nChildren; i++){
            Node child = children.item(i);
            style(child);
        }

    }
}
