/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

/**
 * Contains the definition of the SVG tags and attribute names
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGSyntax{
    // public static final String SVG_PUBLIC_ID = "-//W3C//DTD SVG 10 January 2000//EN";
    // public static final String SVG_SYSTEM_ID = "http://www.w3.org/Graphics/SVG/SVG-19991203.dtd";
    public static final String SVG_PUBLIC_ID = "-//W3C//DTD SVG 20000303 Stylable//EN";
    public static final String SVG_SYSTEM_ID = "http://www.w3.org/TR/2000/03/WD-SVG-20000303/DTD/svg-20000303-stylable.dtd";

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

    /**
     * Tags
     */
    public static final String TAG_CIRCLE = "circle";
    public static final String TAG_CLIP_PATH = "clipPath";
    public static final String TAG_DEFS = "defs";
    public static final String TAG_ELLIPSE = "ellipse";
    public static final String TAG_FE_COLOR_MATRIX = "feColorMatrix";
    public static final String TAG_FE_COMPONENT_TRANSFER = "feComponentTransfer";
    public static final String TAG_FE_COMPOSITE = "feComposite";
    public static final String TAG_FE_CONVOLVE_MATRIX = "feConvolveMatrix";
    public static final String TAG_FE_DIFFUSE_LIGHTING = "feDiffuseLighting";
    public static final String TAG_FE_DISTANT_LIGHT = "feDistantLight";
    public static final String TAG_FE_FLOOD = "feFlood";
    public static final String TAG_FE_FUNC_A = "feFuncA";
    public static final String TAG_FE_FUNC_B = "feFuncB";
    public static final String TAG_FE_FUNC_G = "feFuncG";
    public static final String TAG_FE_FUNC_R = "feFuncR";
    public static final String TAG_FE_GAUSSIAN_BLUR = "feGaussianBlur";
    public static final String TAG_FE_IMAGE = "feImage";
    public static final String TAG_FE_MERGE = "feMerge";
    public static final String TAG_FE_MERGE_NODE = "feMergeNode";
    public static final String TAG_FE_SPECULAR_LIGHTING = "feSpecularLighting";
    public static final String TAG_FE_SPOT_LIGHT = "feSpotLight";
    public static final String TAG_FE_TILE = "feTile";
    public static final String TAG_FILTER = "filter";
    public static final String TAG_G = "g";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_LINE = "line";
    public static final String TAG_LINEAR_GRADIENT = "linearGradient";
    public static final String TAG_MASK = "mask";
    public static final String TAG_PATH = "path";
    public static final String TAG_PATTERN = "pattern";
    public static final String TAG_POLYGON = "polygon";
    public static final String TAG_RADIAL_GRADIENT = "radialGradient";
    public static final String TAG_RECT = "rect";
    public static final String TAG_STOP = "stop";
    public static final String TAG_TEXT = "text";
    public static final String TAG_SVG = "svg";

    /**
     * Attribute names
     */
    public static final String ATTR_AZIMUTH = "azimuth";
    public static final String ATTR_CLIP_PATH = "clip-path";
    public static final String ATTR_CLIP_PATH_UNITS = "clipPathUnits";
    public static final String ATTR_COLOR_INTERPOLATION = "color-interpolation";
    public static final String ATTR_COLOR_RENDERING = "color-rendering";
    public static final String ATTR_CX = "cx";
    public static final String ATTR_CY = "cy";
    public static final String ATTR_D = "d";
    public static final String ATTR_DIFFUSE_CONSTANT = "diffuseConstant";
    public static final String ATTR_EDGE_MODE = "edgemode";
    public static final String ATTR_ELEVATION = "elevation";
    public static final String ATTR_ENABLE_BACKGROUND = "enable-background";
    public static final String ATTR_FILL = "fill";
    public static final String ATTR_FILL_OPACITY = "fill-opacity";
    public static final String ATTR_FILL_RULE = "fill-rule";
    public static final String ATTR_FILTER = "filter";
    public static final String ATTR_FILTER_UNITS = "filterUnits";
    public static final String ATTR_FLOOD_COLOR = "flood-color";
    public static final String ATTR_FLOOD_OPACITY = "flood-opacity";
    public static final String ATTR_FONT_FAMILY = "font-family";
    public static final String ATTR_FONT_SIZE = "font-size";
    public static final String ATTR_FONT_WEIGHT = "font-weight";
    public static final String ATTR_FONT_STYLE = "font-style";
    public static final String ATTR_FX = "fx";
    public static final String ATTR_FY = "fy";
    public static final String ATTR_GRADIENT_TRANSFORM = "gradientTransform";
    public static final String ATTR_GRADIENT_UNITS = "gradientUnits";
    public static final String ATTR_HEIGHT = "height";
    public static final String ATTR_HREF = "xlink:href";
    public static final String ATTR_ID = "id";
    public static final String ATTR_IMAGE_RENDERING = "image-rendering";
    public static final String ATTR_IN = "in";
    public static final String ATTR_IN2 = "in2";
    public static final String ATTR_INTERCEPT = "intercept";
    public static final String ATTR_K1 = "k1";
    public static final String ATTR_K2 = "k2";
    public static final String ATTR_K3 = "k3";
    public static final String ATTR_K4 = "k4";
    public static final String ATTR_KERNEL_MATRIX = "kernelMatrix";
    public static final String ATTR_LIGHT_COLOR = "lightColor";
    public static final String ATTR_MASK = "mask";
    public static final String ATTR_OFFSET = "offset";
    public static final String ATTR_OPACITY = "opacity";
    public static final String ATTR_OPERATOR = "operator";
    public static final String ATTR_ORDER = "order";
    public static final String ATTR_PATTERN_UNITS = "patternUnits";
    public static final String ATTR_POINTS_AT_X = "pointsAtX";
    public static final String ATTR_POINTS_AT_Y = "pointsAtY";
    public static final String ATTR_POINTS_AT_Z = "pointsAtZ";
    public static final String ATTR_POINTS = "points";
    public static final String ATTR_R = "r";
    public static final String ATTR_RESULT = "result";
    public static final String ATTR_RESULT_SCALE = "resultScale";
    public static final String ATTR_RX = "rx";
    public static final String ATTR_RY = "ry";
    public static final String ATTR_SHAPE_RENDERING = "shape-rendering";
    public static final String ATTR_SLOPE = "slope";
    public static final String ATTR_SPECULAR_CONSTANT = "specularConstant";
    public static final String ATTR_SPECULAR_EXPONENT = "specularExponent";
    public static final String ATTR_SPREAD_METHOD = "spreadMethod";
    public static final String ATTR_STD_DEVIATION = "stdDeviation";
    public static final String ATTR_STOP_COLOR = "stop-color";
    public static final String ATTR_STOP_OPACITY = "stop-opacity";
    public static final String ATTR_STROKE = "stroke";
    public static final String ATTR_STROKE_OPACITY = "stroke-opacity";
    public static final String ATTR_STROKE_DASH_ARRAY = "stroke-dasharray";
    public static final String ATTR_STROKE_DASH_OFFSET = "stroke-dashoffset";
    public static final String ATTR_STROKE_LINE_CAP = "stroke-linecap";
    public static final String ATTR_STROKE_LINE_JOIN = "stroke-linejoin";
    public static final String ATTR_STROKE_MITER_LIMIT = "stroke-miterlimit";
    public static final String ATTR_STROKE_WIDTH = "stroke-width";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_SURFACE_SCALE = "surfaceScale";
    public static final String ATTR_TABLE = "table";
    public static final String ATTR_TABLE_VALUES = "tableValues";
    public static final String ATTR_TEXT_RENDERING = "text-rendering";
    public static final String ATTR_TRANSFORM = "transform";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_VALUES = "values";
    public static final String ATTR_X = "x";
    public static final String ATTR_X1 = "x1";
    public static final String ATTR_X2 = "x2";
    public static final String ATTR_Y = "y";
    public static final String ATTR_Y1 = "y1";
    public static final String ATTR_Y2 = "y2";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_Z = "z";

    /**
     * Attribute values
     */
    public static final String VALUE_ARITHMETIC = "arithmetic";
    public static final String VALUE_AUTO = "auto";
    public static final String VALUE_BACKGROUND_IMAGE = "BackgroundImage";
    public static final String VALUE_COMPOSITE = "composite";
    public static final String VALUE_CRISP_EDGES = "crispEdges";
    public static final String VALUE_DIGIT_ONE = "1";
    public static final String VALUE_EDGE_DUPLICATE = "duplicate";
    public static final String VALUE_EDGE_NONE = "none";
    public static final String VALUE_EVEN_ODD = "evenodd";
    public static final String VALUE_FLOOD = "flood";
    public static final String VALUE_FONT_WEIGHT_BOLD = "bold";
    public static final String VALUE_FONT_WEIGHT_BOLDER = "bolder";
    public static final String VALUE_FONT_WEIGHT_LIGHTER = "lighter";
    public static final String VALUE_FONT_WEIGHT_100 = "100";
    public static final String VALUE_FONT_WEIGHT_200 = "200";
    public static final String VALUE_FONT_WEIGHT_300 = "300";
    public static final String VALUE_FONT_WEIGHT_400 = "400";
    public static final String VALUE_FONT_WEIGHT_500 = "500";
    public static final String VALUE_FONT_WEIGHT_600 = "600";
    public static final String VALUE_FONT_WEIGHT_700 = "700";
    public static final String VALUE_FONT_WEIGHT_800 = "800";
    public static final String VALUE_FONT_WEIGHT_900 = "900";
    public static final String VALUE_FONT_WEIGHT_NORMAL = "normal";
    public static final String VALUE_FONT_STYLE_NORMAL = "normal";
    public static final String VALUE_FONT_STYLE_ITALIC = "italic";
    public static final String VALUE_FONT_STYLE_OBLIQUE = "oblique";
    public static final String VALUE_GEOMETRIC_PRECISION = "geometricPrecision";
    public static final String VALUE_HUNDRED_PERCENT = "100%";
    public static final String VALUE_IN = "in";
    public static final String VALUE_LINE_CAP_BUTT = "butt";
    public static final String VALUE_LINE_CAP_SQUARE = "square";
    public static final String VALUE_LINE_CAP_ROUND = "round";
    public static final String VALUE_LINE_JOIN_ROUND = "round";
    public static final String VALUE_LINE_JOIN_BEVEL = "bevel";
    public static final String VALUE_LINE_JOIN_MITER = "miter";
    public static final String VALUE_LINEAR_RGB = "linearRGB";
    public static final String VALUE_LUMINANCE_TO_ALPHA = "luminanceToAlpha";
    public static final String VALUE_MATRIX = "matrix";
    public static final String VALUE_NEW = "new";
    public static final String VALUE_NINETY = "90";
    public static final String VALUE_NON_ZERO = "nonzero";
    public static final String VALUE_NONE = "none";
    public static final String VALUE_OBJECT_BOUNDING_BOX = "objectBoundingBox";
    public static final String VALUE_ONE = "1";
    public static final String VALUE_OPAQUE = "1";
    public static final String VALUE_OPTIMIZE_LEGIBILITY = "optimizeLegibility";
    public static final String VALUE_OPTIMIZE_QUALITY = "optimizeQuality";
    public static final String VALUE_OPTIMIZE_SPEED = "optimizeSpeed";
    public static final String VALUE_OUT = "out";
    public static final String VALUE_OVER = "over";
    public static final String VALUE_PAD = "pad";
    public static final String VALUE_REFLECT = "reflect";
    public static final String VALUE_SOURCE_GRAPHIC = "SourceGraphic";
    public static final String VALUE_SRGB = "sRGB";
    public static final String VALUE_TYPE_LINEAR = "linear";
    public static final String VALUE_TYPE_TABLE = "table";
    public static final String VALUE_USER_SPACE_ON_USE = "userSpaceOnUse";
    public static final String VALUE_USER_SPACE = "userSpace";
    public static final String VALUE_ZERO = "0";
    public static final String VALUE_ZERO_PERCENT = "0%";

    /**
     * Transform constants
     */
    public static final String TRANSFORM_TRANSLATE = "translate";
    public static final String TRANSFORM_ROTATE = "rotate";
    public static final String TRANSFORM_SCALE = "scale";
    public static final String TRANSFORM_SKEWX = "skewX";
    public static final String TRANSFORM_SKEWY = "skewY";
    public static final String TRANSFORM_MATRIX = "matrix";

    /**
     * Path constants
     */
    public static final String PATH_CLOSE = "Z ";
    public static final String PATH_CUBIC_TO = "C ";
    public static final String PATH_MOVE = "M ";
    public static final String PATH_LINE_TO = "L ";
    public static final String PATH_QUAD_TO = "Q ";

}
