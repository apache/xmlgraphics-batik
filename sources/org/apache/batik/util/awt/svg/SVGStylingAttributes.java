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
        attrSet.add(ATTR_CLIP_PATH);
        attrSet.add(ATTR_COLOR_INTERPOLATION);
        attrSet.add(ATTR_COLOR_RENDERING);
        attrSet.add(ATTR_ENABLE_BACKGROUND);
        attrSet.add(ATTR_FILL);
        attrSet.add(ATTR_FILL_OPACITY);
        attrSet.add(ATTR_FILL_RULE);
        attrSet.add(ATTR_FILTER);
        attrSet.add(ATTR_FLOOD_COLOR);
        attrSet.add(ATTR_FLOOD_OPACITY);
        attrSet.add(ATTR_FONT_FAMILY);
        attrSet.add(ATTR_FONT_SIZE);
        attrSet.add(ATTR_FONT_WEIGHT);
        attrSet.add(ATTR_FONT_STYLE);
        attrSet.add(ATTR_IMAGE_RENDERING);
        attrSet.add(ATTR_MASK);
        attrSet.add(ATTR_OPACITY);
        attrSet.add(ATTR_SHAPE_RENDERING);
        attrSet.add(ATTR_STOP_COLOR);
        attrSet.add(ATTR_STOP_OPACITY);
        attrSet.add(ATTR_STROKE);
        attrSet.add(ATTR_STROKE_OPACITY);
        attrSet.add(ATTR_STROKE_DASH_ARRAY);
        attrSet.add(ATTR_STROKE_DASH_OFFSET);
        attrSet.add(ATTR_STROKE_LINE_CAP);
        attrSet.add(ATTR_STROKE_LINE_JOIN);
        attrSet.add(ATTR_STROKE_MITER_LIMIT);
        attrSet.add(ATTR_STROKE_WIDTH);
        attrSet.add(ATTR_TEXT_RENDERING);
    }

    /**
     * Attributes that represent styling properties
     */
    public static final Set set = attrSet;
}
