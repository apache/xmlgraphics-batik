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

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class provides a manager for the property with support for
 * length values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class LengthManager extends AbstractValueManager {
    
    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_EM:
	    return new FloatValue(CSSPrimitiveValue.CSS_EMS,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_EX:
	    return new FloatValue(CSSPrimitiveValue.CSS_EXS,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_PIXEL:
	    return new FloatValue(CSSPrimitiveValue.CSS_PX,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_CENTIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_CM,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_MILLIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_MM,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_INCH:
	    return new FloatValue(CSSPrimitiveValue.CSS_IN,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_POINT:
	    return new FloatValue(CSSPrimitiveValue.CSS_PT,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_PICA:
	    return new FloatValue(CSSPrimitiveValue.CSS_PC,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_INTEGER:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getIntegerValue());

	case LexicalUnit.SAC_REAL:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getFloatValue());

	case LexicalUnit.SAC_PERCENTAGE:
	    return new FloatValue(CSSPrimitiveValue.CSS_PERCENTAGE,
                                  lu.getFloatValue());
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link ValueManager#createFloatValue(short,float)}.
     */
    public Value createFloatValue(short type, float floatValue)
        throws DOMException {
	switch (type) {
	case CSSPrimitiveValue.CSS_PERCENTAGE:
	case CSSPrimitiveValue.CSS_EMS:
	case CSSPrimitiveValue.CSS_EXS:
	case CSSPrimitiveValue.CSS_PX:
	case CSSPrimitiveValue.CSS_CM:
	case CSSPrimitiveValue.CSS_MM:
	case CSSPrimitiveValue.CSS_IN:
	case CSSPrimitiveValue.CSS_PT:
	case CSSPrimitiveValue.CSS_PC:
	case CSSPrimitiveValue.CSS_NUMBER:
	    return new FloatValue(type, floatValue);
	}
        throw createInvalidFloatTypeDOMException(type);
    }

    /**
     * Implements {@link
     * ValueManager#computeValue(CSSStylableElement,String,CSSEngine,int,StyleMap,Value)}.
     */
    public Value computeValue(CSSStylableElement elt,
                              String pseudo,
                              CSSEngine engine,
                              int idx,
                              StyleMap sm,
                              Value value) {
        if (value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE) {
            return value;
        }

        switch (value.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_NUMBER:
        case CSSPrimitiveValue.CSS_PX:
            return value;

        case CSSPrimitiveValue.CSS_MM:
            CSSContext ctx = engine.getCSSContext();
            float v = value.getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  v / ctx.getPixelUnitToMillimeter());

        case CSSPrimitiveValue.CSS_CM:
            ctx = engine.getCSSContext(); 
            v = value.getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  v * 10f / ctx.getPixelUnitToMillimeter());

        case CSSPrimitiveValue.CSS_IN:
            ctx = engine.getCSSContext();
            v = value.getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  v * 25.4f / ctx.getPixelUnitToMillimeter());

        case CSSPrimitiveValue.CSS_PT:
            ctx = engine.getCSSContext();
            v = value.getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  v * 25.4f /
                                  (72f * ctx.getPixelUnitToMillimeter()));

        case CSSPrimitiveValue.CSS_PC:
            ctx = engine.getCSSContext();
            v = value.getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  (v * 25.4f /
                                   (6f * ctx.getPixelUnitToMillimeter())));

        case CSSPrimitiveValue.CSS_EMS:
            sm.putFontSizeRelative(idx, true);

            v = value.getFloatValue();
            int fsidx = engine.getFontSizeIndex();
            float fs;
            fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER, v * fs);


        case CSSPrimitiveValue.CSS_EXS:
            sm.putFontSizeRelative(idx, true);

            v = value.getFloatValue();
            fsidx = engine.getFontSizeIndex();
            fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER, v * fs * 0.5f);

        case CSSPrimitiveValue.CSS_PERCENTAGE:
            ctx = engine.getCSSContext();
            switch (getOrientation()) {
            case HORIZONTAL_ORIENTATION:
                sm.putBlockWidthRelative(idx, true);
                fs = value.getFloatValue() * ctx.getBlockWidth(elt) / 100f;
                break;
            case VERTICAL_ORIENTATION:
                sm.putBlockHeightRelative(idx, true);
                fs = value.getFloatValue() * ctx.getBlockHeight(elt) / 100f;
                break;
            default: // Both
                sm.putBlockWidthRelative(idx, true);
                sm.putBlockHeightRelative(idx, true);
                double w = ctx.getBlockWidth(elt);
                double h = ctx.getBlockHeight(elt);
                fs = (float)(value.getFloatValue() *
                        (Math.sqrt(w * w + h * h) / Math.sqrt(2)) / 100.0);
            }
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER, fs);
        }
        return value;
    }

    //
    // Orientation enumeration
    //
    protected final static int HORIZONTAL_ORIENTATION = 0;
    protected final static int VERTICAL_ORIENTATION = 1;
    protected final static int BOTH_ORIENTATION = 2;

    /**
     * Indicates the orientation of the property associated with
     * this manager.
     */
    protected abstract int getOrientation();
}
