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

import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides support for AWT system colors.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SystemColorSupport implements CSSConstants {
    
    /**
     * Returns the Value corresponding to the given system color.
     */
    public static Value getSystemColor(String ident) {
        ident = ident.toLowerCase();
        SystemColor sc = (SystemColor)factories.get(ident);
        return new RGBColorValue
            (new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getRed()),
             new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getGreen()),
             new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getBlue()));
    }

    /**
     * The color factories.
     */
    protected final static Map factories = new HashMap();
    static {
        factories.put(CSS_ACTIVEBORDER_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_ACTIVECAPTION_VALUE,
                      SystemColor.activeCaption);
        factories.put(CSS_APPWORKSPACE_VALUE,
                      SystemColor.desktop);
        factories.put(CSS_BACKGROUND_VALUE,
                      SystemColor.desktop);
        factories.put(CSS_BUTTONFACE_VALUE,
                      SystemColor.control);
        factories.put(CSS_BUTTONHIGHLIGHT_VALUE,
                      SystemColor.controlLtHighlight);
        factories.put(CSS_BUTTONSHADOW_VALUE,
                      SystemColor.controlDkShadow);
        factories.put(CSS_BUTTONTEXT_VALUE,
                      SystemColor.controlText);
        factories.put(CSS_CAPTIONTEXT_VALUE,
                      SystemColor.activeCaptionText);
        factories.put(CSS_GRAYTEXT_VALUE,
                      SystemColor.textInactiveText);
        factories.put(CSS_HIGHLIGHT_VALUE,
                      SystemColor.textHighlight);
        factories.put(CSS_HIGHLIGHTTEXT_VALUE,
                      SystemColor.textHighlightText);
        factories.put(CSS_INACTIVEBORDER_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_INACTIVECAPTION_VALUE,
                      SystemColor.inactiveCaption);
        factories.put(CSS_INACTIVECAPTIONTEXT_VALUE,
                      SystemColor.inactiveCaptionText);
        factories.put(CSS_INFOBACKGROUND_VALUE,
                      SystemColor.info);
        factories.put(CSS_INFOTEXT_VALUE,
                      SystemColor.infoText);
        factories.put(CSS_MENU_VALUE,
                      SystemColor.menu);
        factories.put(CSS_MENUTEXT_VALUE,
                      SystemColor.menuText);
        factories.put(CSS_SCROLLBAR_VALUE,
                      SystemColor.scrollbar);
        factories.put(CSS_THREEDDARKSHADOW_VALUE,
                      SystemColor.controlDkShadow);
        factories.put(CSS_THREEDFACE_VALUE,
                      SystemColor.control);
        factories.put(CSS_THREEDHIGHLIGHT_VALUE,
                      SystemColor.controlHighlight);
        factories.put(CSS_THREEDLIGHTSHADOW_VALUE,
                      SystemColor.controlLtHighlight);
        factories.put(CSS_THREEDSHADOW_VALUE,
                      SystemColor.controlShadow);
        factories.put(CSS_WINDOW_VALUE,
                      SystemColor.window);
        factories.put(CSS_WINDOWFRAME_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_WINDOWTEXT_VALUE,
                      SystemColor.windowText);
    }

    /**
     * This class does not need to be instantiated.
     */
    protected SystemColorSupport() {
    }
}
