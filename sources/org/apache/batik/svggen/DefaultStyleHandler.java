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

import org.apache.batik.util.SVGConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * The <code>DefaultStyleHandler</code> class provides the default
 * way to style an SVG <code>Element</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class DefaultStyleHandler implements StyleHandler, SVGConstants {
    /**
     * Static initializer for which attributes should be ignored on
     * some elements.
     */
    static HashMap ignoreAttributes = new HashMap();

    static {
        Vector textAttributes = new Vector();
        textAttributes.addElement(SVG_FONT_SIZE_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_FAMILY_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_STYLE_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_WEIGHT_ATTRIBUTE);

        ignoreAttributes.put(SVG_RECT_TAG, textAttributes);
        ignoreAttributes.put(SVG_CIRCLE_TAG, textAttributes);
        ignoreAttributes.put(SVG_ELLIPSE_TAG, textAttributes);
        ignoreAttributes.put(SVG_POLYGON_TAG, textAttributes);
        ignoreAttributes.put(SVG_POLYGON_TAG, textAttributes);
        ignoreAttributes.put(SVG_LINE_TAG, textAttributes);
        ignoreAttributes.put(SVG_PATH_TAG, textAttributes);
    }

    /**
     * Sets the style described by <code>styleMap</code> on the given
     * <code>element</code>. That is sets the xml attributes with their
     * styled value.
     * @param element the SVG <code>Element</code> to be styled.
     * @param styleMap the <code>Map</code> containing pairs of style
     * property names, style values.
     */
    public void setStyle(Element element, Map styleMap,
                         SVGGeneratorContext generatorContext) {
        String tagName = element.getTagName();
        Iterator iter = styleMap.keySet().iterator();
        String styleName = null;
        while (iter.hasNext()) {
            styleName = (String)iter.next();
            if (element.getAttributeNS(null, styleName).length() == 0){
                if (appliesTo(styleName, tagName)) {
                    element.setAttributeNS(null, styleName,
                                           (String)styleMap.get(styleName));
                }
            }
        }
    }

    /**
     * Controls whether or not a given attribute applies to a particular 
     * element.
     */
    protected boolean appliesTo(String styleName, String tagName) {
        Vector v = (Vector)ignoreAttributes.get(tagName);
        if (v == null) {
            return true;
        } else {
            return !v.contains(styleName);
        }
    }
}
