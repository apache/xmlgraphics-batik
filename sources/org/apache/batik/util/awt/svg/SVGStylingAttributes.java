/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.util.Set;
import java.util.HashSet;

/**
 * Defines the set of attributes from Exchange SVG that
 * are defined as styling properties in Stylable SVG.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGStylingAttributes implements SVGSyntax{
    static Set attrSet = new HashSet();

    static {
        attrSet.add(SVG_CLIP_PATH_ATTRIBUTE);
        attrSet.add(SVG_COLOR_INTERPOLATION_ATTRIBUTE);
        attrSet.add(SVG_COLOR_RENDERING_ATTRIBUTE);
        attrSet.add(SVG_ENABLE_BACKGROUND_ATTRIBUTE);
        attrSet.add(SVG_FILL_ATTRIBUTE);
        attrSet.add(SVG_FILL_OPACITY_ATTRIBUTE);
        attrSet.add(SVG_FILL_RULE_ATTRIBUTE);
        attrSet.add(SVG_FILTER_ATTRIBUTE);
        attrSet.add(SVG_FLOOD_COLOR_ATTRIBUTE);
        attrSet.add(SVG_FLOOD_OPACITY_ATTRIBUTE);
        attrSet.add(SVG_FONT_FAMILY_ATTRIBUTE);
        attrSet.add(SVG_FONT_SIZE_ATTRIBUTE);
        attrSet.add(SVG_FONT_WEIGHT_ATTRIBUTE);
        attrSet.add(SVG_FONT_STYLE_ATTRIBUTE);
        attrSet.add(ATTR_IMAGE_RENDERING);
        attrSet.add(ATTR_MASK);
        attrSet.add(ATTR_OPACITY);
        attrSet.add(ATTR_SHAPE_RENDERING);
        attrSet.add(ATTR_STOP_COLOR);
        attrSet.add(ATTR_STOP_OPACITY);
        attrSet.add(ATTR_STROKE);
        attrSet.add(ATTR_STROKE_OPACITY);
        attrSet.add(ATTR_STROKE_DASHARRAY);
        attrSet.add(ATTR_STROKE_DASHOFFSET);
        attrSet.add(ATTR_STROKE_LINECAP);
        attrSet.add(ATTR_STROKE_LINEJOIN);
        attrSet.add(ATTR_STROKE_MITERLIMIT);
        attrSet.add(ATTR_STROKE_WIDTH);
        attrSet.add(ATTR_TEXT_RENDERING);
    }

    /**
     * Attributes that represent styling properties
     */
    public static final Set set = attrSet;
}
