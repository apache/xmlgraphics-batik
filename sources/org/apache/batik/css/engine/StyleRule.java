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

package org.apache.batik.css.engine;

import org.w3c.css.sac.SelectorList;

/**
 * This class represents a style rule.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleRule implements Rule {
    
    /**
     * The type constant.
     */
    public final static short TYPE = (short)0;

    /**
     * The selector list.
     */
    protected SelectorList selectorList;

    /**
     * The style declaration.
     */
    protected StyleDeclaration styleDeclaration;

    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() {
        return TYPE;
    }

    /**
     * Sets the selector list.
     */
    public void setSelectorList(SelectorList sl) {
        selectorList = sl;
    }

    /**
     * Returns the selector list.
     */
    public SelectorList getSelectorList() {
        return selectorList;
    }

    /**
     * Sets the style map.
     */
    public void setStyleDeclaration(StyleDeclaration sd) {
        styleDeclaration = sd;
    }

    /**
     * Returns the style declaration.
     */
    public StyleDeclaration getStyleDeclaration() {
        return styleDeclaration;
    }

    /**
     * Returns a printable representation of this style rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        if (selectorList != null) {
            sb.append(selectorList.item(0));
            for (int i = 1; i < selectorList.getLength(); i++) {
                sb.append(", ");
                sb.append(selectorList.item(i));
            }
        }
        sb.append(" {\n");
        if (styleDeclaration != null) {
            sb.append(styleDeclaration.toString(eng));
        }
        sb.append("}\n");
        return sb.toString();
    }
}
