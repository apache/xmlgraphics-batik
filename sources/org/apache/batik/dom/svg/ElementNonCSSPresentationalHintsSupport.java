/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.HashSet;
import java.util.Set;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.dom.util.CSSStyleDeclarationFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class provides support for non-CSS presentational hints
 * processing in SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ElementNonCSSPresentationalHintsSupport
    implements CSSConstants {
    /**
     * The presentation attributes.
     */
    public final static Set PRESENTATION_ATTRIBUTES = new HashSet();
    static {
        PRESENTATION_ATTRIBUTES.add(CSS_ALIGNMENT_BASELINE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_BASELINE_SHIFT_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_CLIP_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_CLIP_PATH_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_CLIP_RULE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_COLOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_COLOR_INTERPOLATION_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_COLOR_PROFILE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_COLOR_RENDERING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_CURSOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_DIRECTION_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_DISPLAY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_DOMINANT_BASELINE_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_ENABLE_BACKGROUND_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_FILL_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FILL_OPACITY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FILL_RULE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FILTER_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FLOOD_COLOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FLOOD_OPACITY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_FAMILY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_SIZE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_SIZE_ADJUST_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_STRETCH_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_STYLE_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_VARIANT_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_FONT_WEIGHT_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_GLYPH_ORIENTATION_HORIZONTAL_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_GLYPH_ORIENTATION_VERTICAL_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_IMAGE_RENDERING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_LETTER_SPACING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_LIGHTING_COLOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_MARKER_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_MARKER_END_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_MARKER_MID_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_MARKER_START_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_MASK_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_OPACITY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_OVERFLOW_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_POINTER_EVENTS_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_SHAPE_RENDERING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_STOP_COLOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_STOP_OPACITY_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_DASHARRAY_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_DASHOFFSET_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_LINECAP_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_LINEJOIN_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_MITERLIMIT_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_STROKE_OPACITY_PROPERTY);
	PRESENTATION_ATTRIBUTES.add(CSS_STROKE_WIDTH_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_TEXT_ANCHOR_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_TEXT_DECORATION_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_TEXT_RENDERING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_UNICODE_BIDI_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_VISIBILITY_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_WORD_SPACING_PROPERTY);
        PRESENTATION_ATTRIBUTES.add(CSS_WRITING_MODE_PROPERTY);
    }

    /**
     * Returns the translation of the non-CSS hints to the corresponding
     * CSS rules. The result can be null.
     */
    public static CSSStyleDeclaration getNonCSSPresentationalHints
        (Element elt) {
	CSSStyleDeclaration result = null;

	NamedNodeMap nnm = elt.getAttributes();
	int len = nnm.getLength();
	for (int i = 0; i < len; i++) {
	    Node attr = nnm.item(i);
	    String an = attr.getNodeName();
	    if (PRESENTATION_ATTRIBUTES.contains(an)) {
		if (result == null) {
		    DOMImplementation impl;
                    impl = elt.getOwnerDocument().getImplementation();
		    CSSStyleDeclarationFactory f;
                    f = (CSSStyleDeclarationFactory)impl;
		    result = f.createCSSStyleDeclaration();
		}
		result.setProperty(an, attr.getNodeValue(), "");
	    }
	}
	return result;
    }
}
