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

package org.apache.batik.bridge;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.gvt.font.AWTFontFamily;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.Element;

/**
 * This class represents a &lt;font-face> element or @font-face rule
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class CSSFontFace extends FontFace implements SVGConstants {

    GVTFontFamily fontFamily = null;

    /**
     * Constructes an CSSFontFace with the specfied font-face attributes.
     */
    public CSSFontFace
        (List srcs,
         String familyName, float unitsPerEm, String fontWeight,
         String fontStyle, String fontVariant, String fontStretch,
         float slope, String panose1, float ascent, float descent,
         float strikethroughPosition, float strikethroughThickness,
         float underlinePosition, float underlineThickness,
         float overlinePosition, float overlineThickness) {
        super(srcs,
              familyName, unitsPerEm, fontWeight, fontStyle,
              fontVariant, fontStretch, slope, panose1, ascent, descent,
              strikethroughPosition, strikethroughThickness,
              underlinePosition, underlineThickness,
              overlinePosition, overlineThickness);
    }

    protected CSSFontFace(String familyName) {
        super(familyName);
    }

    public static CSSFontFace createCSSFontFace(CSSEngine eng,
                                                FontFaceRule ffr) {
        StyleMap sm = ffr.getStyleMap();
        String familyName = getStringProp
            (sm, eng, SVGCSSEngine.FONT_FAMILY_INDEX);

        CSSFontFace ret = new CSSFontFace(familyName);

        ValueManager [] vms = eng.getValueManagers();
        Value v;
        v = sm.getValue(SVGCSSEngine.FONT_WEIGHT_INDEX);
        if (v != null) 
            ret.fontWeight = v.getCssText();
        v = sm.getValue(SVGCSSEngine.FONT_STYLE_INDEX);
        if (v != null) 
            ret.fontStyle = v.getCssText();
        v = sm.getValue(SVGCSSEngine.FONT_VARIANT_INDEX);
        if (v != null) 
            ret.fontVariant = v.getCssText();
        v = sm.getValue(SVGCSSEngine.FONT_STRETCH_INDEX);
        if (v != null) 
            ret.fontStretch = v.getCssText();
        v = sm.getValue(SVGCSSEngine.SRC_INDEX);
        
        ParsedURL base = ffr.getURL();
        if ((v != null) && (v != ValueConstants.NONE_VALUE)) {
            if (v.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
                ret.srcs = new LinkedList();
                ret.srcs.add(getSrcValue(v, base));
            } else if (v.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
                ret.srcs = new LinkedList();
                for (int i=0; i<v.getLength(); i++) {
                    ret.srcs.add(getSrcValue(v.item(i), base));
                }
            }
        }
        /*
        float unitsPerEm = getFloatProp
            (sm, eng, SVGCSSEngine.UNITS_PER_EM_INDEX);
        String slope = getFloatProp
            (sm, eng, SVGCSSEngine.SLOPE_INDEX);
        String panose1 = getStringProp
            (sm, eng, SVGCSSEngine.PANOSE1_INDEX);
        String ascent = getFloatProp
            (sm, eng, SVGCSSEngine.ASCENT_INDEX);
        String descent = getFloatProp
            (sm, eng, SVGCSSEngine.DESCENT_INDEX);
        String strikethroughPosition = getFloatProp
            (sm, eng, SVGCSSEngine.STRIKETHROUGH_POSITION_INDEX);
        String strikethroughThickness = getFloatProp
            (sm, eng, SVGCSSEngine.STRIKETHROUGH_THICKNESS_INDEX);
        String underlinePosition = getFloatProp
            (sm, eng, SVGCSSEngine.UNDERLINE_POSITION_INDEX);
        String underlineThickness = getFloatProp
            (sm, eng, SVGCSSEngine.UNDERLINE_THICKNESS_INDEX);
        String overlinePosition = getFloatProp
            (sm, eng, SVGCSSEngine.OVERLINE_POSITION_INDEX);
        String overlineThickness = getFloatProp
            (sm, eng, SVGCSSEngine.OVERLINE_THICKNESS_INDEX);
        */
        return ret;
    }

    public static Object getSrcValue(Value v, ParsedURL base) {
        if (v.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE) 
            return null;
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
            if (base != null)
                return new ParsedURL(base, v.getStringValue());
            return new ParsedURL(v.getStringValue());
        } 
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_STRING)
            return v.getStringValue();
        return null;
    }
    public static String getStringProp(StyleMap sm, CSSEngine eng, int pidx) {
        Value v = sm.getValue(pidx);
        ValueManager [] vms = eng.getValueManagers();
        if (v == null) {
            ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            v = v.item(0);
        }
        return v.getStringValue();
    }

    public static float getFloatProp(StyleMap sm, CSSEngine eng, int pidx) {
        Value v = sm.getValue(pidx);
        ValueManager [] vms = eng.getValueManagers();
        if (v == null) {
            ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            v = v.item(0);
        }
        return v.getFloatValue();
    }

    /**
     * Returns the font associated with this rule or element.
     */
    public GVTFontFamily getFontFamily(BridgeContext ctx) {
        if (fontFamily != null)
            return fontFamily ;

        fontFamily = super.getFontFamily(ctx);
        return fontFamily;
    }
}
