/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.apache.batik.util.SVGConstants;

/**
 * Contains the definition of the SVG tags and attribute names
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGSyntax extends SVGConstants{
    /**
     * This is a qualified form for href, using the xlink: namespace prefix
     */
    public static final String ATTR_XLINK_HREF = "xlink:" + ATTR_HREF;

    /**
     * ID Prefix. Generated IDs have the form <prefix><nn>
     */
    public static final String ID_PREFIX_ALPHA_COMPOSITE_CLEAR = "alphaCompositeClear";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_DST_IN = "alphaCompositeDstIn";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_DST_OUT = "alphaCompositeDstOut";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_DST_OVER = "alphaCompositeDstOver";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_SRC = "alphaCompositeSrc";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_SRC_IN = "alphaCompositeSrcIn";
    public static final String ID_PREFIX_ALPHA_COMPOSITE_SRC_OUT = "alphaCompositeSrcOut";
    public static final String ID_PREFIX_AMBIENT_LIGHT = "ambientLight";
    public static final String ID_PREFIX_BUMP_MAP = "bumpMap";
    public static final String ID_PREFIX_CLIP_PATH = "clipPath";
    public static final String ID_PREFIX_DEFS = "defs";
    public static final String ID_PREFIX_DIFFUSE_ADD = "diffuseAdd";
    public static final String ID_PREFIX_DIFFUSE_LIGHTING_RESULT = "diffuseLightingResult";
    public static final String ID_PREFIX_FE_CONVOLVE_MATRIX = "convolve";
    public static final String ID_PREFIX_FE_COMPONENT_TRANSFER = "componentTransfer";
    public static final String ID_PREFIX_FE_COMPOSITE = "composite";
    public static final String ID_PREFIX_FE_COMPLEX_FILTER = "complexFilter";
    public static final String ID_PREFIX_FE_DIFFUSE_LIGHTING = "diffuseLighting";
    public static final String ID_PREFIX_FE_FLOOD = "flood";
    public static final String ID_PREFIX_FE_GAUSSIAN_BLUR = "feGaussianBlur";
    public static final String ID_PREFIX_FE_LIGHTING_FILTER = "feLightingFilter";
    public static final String ID_PREFIX_FE_SPECULAR_LIGHTING = "feSpecularLighting";
    public static final String ID_PREFIX_GENERIC_DEFS = "genericDefs";
    public static final String ID_PREFIX_LINEAR_GRADIENT = "linearGradient";
    public static final String ID_PREFIX_MASK = "mask";
    public static final String ID_PREFIX_PATTERN = "pattern";
    public static final String ID_PREFIX_RADIAL_GRADIENT = "radialGradient";
    public static final String ID_PREFIX_SPECULAR_ADD = "specularAdd";
    public static final String ID_PREFIX_SPECULAR_LIGHTING_RESULT = "specularLightingResult";

    /**
     * Generic
     */
    public static final String CLOSE_PARENTHESIS = ")";
    public static final String COMMA = ",";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String RGB_PREFIX = "rgb(";
    public static final String RGB_SUFFIX = ")";
    public static final String SIGN_PERCENT = "%";
    public static final String SIGN_POUND = "#";
    public static final String SPACE = " ";
    public static final String URL_PREFIX = "url(";
    public static final String URL_SUFFIX = ")";

}
