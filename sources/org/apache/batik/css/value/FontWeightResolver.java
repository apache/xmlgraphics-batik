/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.HiddenChildElementSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.ViewCSS;

/**
 * This class provides a relative value resolver for the 'font-weight' CSS
 * property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontWeightResolver implements RelativeValueResolver {

    /**
     * Whether the handled property is inherited or not.
     */
    public boolean isInheritedProperty() {
        return true;
    }

    /**
     * Returns the name of the handled property.
     */
    public String getPropertyName() {
        return ValueConstants.CSS_FONT_WEIGHT_PROPERTY;
    }

    /**
     * Returns the default value for the handled property.
     */
    public CSSOMReadOnlyValue getDefaultValue() {
        return new CSSOMReadOnlyValue(ValueConstants.NORMAL_VALUE);
    }

    /**
     * Resolves the given value if relative, and puts it in the given table.
     * @param element The element to which this value applies.
     * @param pseudoElement The pseudo element if one.
     * @param view The view CSS of the current document.
     * @param styleDeclaration The computed style declaration.
     * @param value The cascaded value.
     * @param priority The priority of the cascaded value.
     * @param origin The origin of the cascaded value.
     */
    public void resolveValue(Element element,
                             String pseudoElement,
                             ViewCSS view,
                             CSSOMReadOnlyStyleDeclaration styleDeclaration,
                             CSSOMReadOnlyValue value,
                             String priority,
                             int origin) {
        ImmutableValue im = value.getImmutableValue();
        /**
         * Note:  this implementation should change, since the spec says
         * the next lighter/bolder "available" font should be chosen, and
         * that if none available then increment/decrement by 100.
         * Since at the moment we only have normal and bold fonts,
         * the interim solution is a reasonable "fit".  Eventually this
         * resolution must be done in the renderer instead of here.
         */
        boolean b = im == ValueConstants.BOLDER_VALUE;
        if (b || im == ValueConstants.LIGHTER_VALUE) {
            Element p = HiddenChildElementSupport.getParentElement(element);
            CSSOMReadOnlyValue val;
            if (p == null) {
                val = new CSSOMReadOnlyValue((b)
                                             ? ValueConstants.NUMBER_600
                                             : ValueConstants.NUMBER_300);
            } else {
                CSSOMReadOnlyStyleDeclaration sd;
                sd = (CSSOMReadOnlyStyleDeclaration)view.getComputedStyle
                    (p, null);
                CSSOMReadOnlyValue prop;
                prop = (CSSOMReadOnlyValue)sd.getPropertyCSSValue
                    (getPropertyName());
                im = prop.getImmutableValue();
                if (im == ValueConstants.NUMBER_100) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_600
                         : ValueConstants.NUMBER_100);
                } else if (im == ValueConstants.NUMBER_200) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_600
                         : ValueConstants.NUMBER_100);
                } else if (im == ValueConstants.NUMBER_300) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_600
                         : ValueConstants.NUMBER_200);
                } else if (im == ValueConstants.NUMBER_400 ||
                           im == ValueConstants.NORMAL_VALUE) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_600
                         : ValueConstants.NUMBER_300);
                } else if (im == ValueConstants.NUMBER_500) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_600
                         : ValueConstants.NUMBER_400);
                } else if (im == ValueConstants.NUMBER_600) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_700
                         : ValueConstants.NUMBER_400);
                } else if (im == ValueConstants.NUMBER_700 ||
                           im == ValueConstants.BOLD_VALUE) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_800
                         : ValueConstants.NUMBER_400);
                } else if (im == ValueConstants.NUMBER_800) {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_900
                         : ValueConstants.NUMBER_400);
                } else {
                    val = new CSSOMReadOnlyValue
                        ((b)
                         ? ValueConstants.NUMBER_900
                         : ValueConstants.NUMBER_400);
                }
            }
            styleDeclaration.setPropertyCSSValue(getPropertyName(),
                                                 val,
                                                 priority,
                                                 origin);
        }
    }
}
