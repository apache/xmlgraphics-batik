/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * Define SVG constants, such as tag names, attribute names and URI
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGConstants extends CSSConstants {
    String SVG_PUBLIC_ID =
        "-//W3C//DTD SVG 20001102//EN";
    String SVG_SYSTEM_ID =
        "http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd";
    String SVG_NAMESPACE_URI =
        "http://www.w3.org/2000/svg";

    /**
     * Tags
     */
    String SVG_A_TAG = "a";
    String SVG_ANIMATE_TAG = "animate";
    String SVG_CIRCLE_TAG = "circle";
    String SVG_CLIP_PATH_TAG = "clipPath";
    String SVG_DEFS_TAG = "defs";
    String SVG_DESC_TAG = "desc";
    String SVG_ELLIPSE_TAG = "ellipse";
    String SVG_FE_BLEND_TAG = "feBlend";
    String SVG_FE_COLOR_MATRIX_TAG = "feColorMatrix";
    String SVG_FE_COMPONENT_TRANSFER_TAG = "feComponentTransfer";
    String SVG_FE_COMPOSITE_TAG = "feComposite";
    String TAG_FE_CONVOLVE_MATRIX = "feConvolveMatrix";
    String TAG_FE_DIFFUSE_LIGHTING = "feDiffuseLighting";
    String TAG_FE_DISPLACEMENT_MAP = "feDisplacementMap";
    String TAG_FE_DISTANT_LIGHT = "feDistantLight";
    String TAG_FE_FLOOD = "feFlood";
    String TAG_FE_FUNC_A = "feFuncA";
    String TAG_FE_FUNC_B = "feFuncB";
    String TAG_FE_FUNC_G = "feFuncG";
    String TAG_FE_FUNC_R = "feFuncR";
    String TAG_FE_GAUSSIAN_BLUR = "feGaussianBlur";
    String TAG_FE_IMAGE = "feImage";
    String TAG_FE_MERGE = "feMerge";
    String TAG_FE_MERGE_NODE = "feMergeNode";
    String TAG_FE_MORPHOLOGY = "feMorphology";
    String TAG_FE_OFFSET = "feOffset";
    String TAG_FE_POINT_LIGHT = "fePointLight";
    String TAG_FE_SPECULAR_LIGHTING = "feSpecularLighting";
    String TAG_FE_SPOT_LIGHT = "feSpotLight";
    String TAG_FE_TILE = "feTile";
    String TAG_FE_TURBULENCE = "feTurbulence";
    String TAG_FILTER = "filter";
    String TAG_G = "g";
    String TAG_IMAGE = "image";
    String TAG_LINE = "line";
    String TAG_LINEAR_GRADIENT = "linearGradient";
    String TAG_MASK = "mask";
    String TAG_METADATA = "metadata";
    String TAG_PATH = "path";
    String TAG_PATTERN = "pattern";
    String TAG_POLYGON = "polygon";
    String TAG_POLYLINE = "polyline";
    String TAG_RADIAL_GRADIENT = "radialGradient";
    String TAG_RECT = "rect";
    String TAG_SCRIPT = "script";
    String SVG_STOP_TAG = "stop";
    String TAG_STYLE = "style";
    String TAG_SVG = "svg";
    String TAG_SWITCH = "switch";
    String TAG_SYMBOL = "symbol";
    String TAG_TEXT = "text";
    String TAG_TEXT_PATH = "textPath";
    String TAG_TITLE = "title";
    String TAG_TREF = "tref";
    String TAG_TSPAN = "tspan";
    String TAG_USE = "use";

    /**
     * Attribute names
     */
    String SVG_AMPLITUDE_ATTRIBUTE = "amplitude";
    String ATTR_AZIMUTH = "azimuth";
    String ATTR_BASE_FREQUENCY = "baseFrequency";
    String ATTR_BIAS = "bias";
    String ATTR_CLIP_PATH = CSS_CLIP_PATH_PROPERTY;
    String SVG_CLIP_PATH_UNITS_ATTRIBUTE = "clipPathUnits";
    String ATTR_COLOR_INTERPOLATION = CSS_COLOR_INTERPOLATION_PROPERTY;
    String ATTR_COLOR_RENDERING = CSS_COLOR_RENDERING_PROPERTY;
    String SVG_CX_ATTRIBUTE = "cx";
    String SVG_CY_ATTRIBUTE = "cy";
    String ATTR_D = "d";
    String ATTR_DIFFUSE_CONSTANT = "diffuseConstant";
    String ATTR_DIVISOR = "divisor";
    String ATTR_DX = "dx";
    String ATTR_DY = "dy";
    String ATTR_EDGE_MODE = "edgemode";
    String ATTR_ELEVATION = "elevation";
    String ATTR_ENABLE_BACKGROUND = CSS_ENABLE_BACKGROUND_PROPERTY;
    String SVG_EXPONENT_ATTRIBUTE = "exponent";
    String ATTR_FILL = CSS_FILL_PROPERTY;
    String ATTR_FILL_OPACITY = CSS_FILL_OPACITY_PROPERTY;
    String ATTR_FILL_RULE = CSS_FILL_RULE_PROPERTY;
    String ATTR_FILTER = CSS_FILTER_PROPERTY;
    String ATTR_FILTER_RES = "filterRes";
    String ATTR_FILTER_UNITS = "filterUnits";
    String ATTR_FLOOD_COLOR = CSS_FLOOD_COLOR_PROPERTY;
    String ATTR_FLOOD_OPACITY = CSS_FLOOD_OPACITY_PROPERTY;
    String ATTR_FONT_FAMILY = CSS_FONT_FAMILY_PROPERTY;
    String ATTR_FONT_SIZE = CSS_FONT_SIZE_PROPERTY;
    String ATTR_FONT_STYLE = CSS_FONT_STYLE_PROPERTY;
    String ATTR_FONT_WEIGHT = CSS_FONT_WEIGHT_PROPERTY;
    String ATTR_FX = "fx";
    String ATTR_FY = "fy";
    String ATTR_GRADIENT_TRANSFORM = "gradientTransform";
    String ATTR_GRADIENT_UNITS = "gradientUnits";
    String ATTR_HEIGHT = "height";
    String ATTR_HREF = "href";
    String ATTR_ID = "id";
    String ATTR_IMAGE_RENDERING = CSS_IMAGE_RENDERING_PROPERTY;
    String SVG_IN_ATTRIBUTE = "in";
    String SVG_IN2_ATTRIBUTE = "in2";
    String SVG_INTERCEPT_ATTRIBUTE = "intercept";
    String SVG_K1_ATTRIBUTE = "k1";
    String SVG_K2_ATTRIBUTE = "k2";
    String SVG_K3_ATTRIBUTE = "k3";
    String SVG_K4_ATTRIBUTE = "k4";
    String ATTR_KERNEL_MATRIX = "kernelMatrix";
    String ATTR_KERNEL_UNIT_LENGTH_X = "kernelUnitLengthX";
    String ATTR_KERNEL_UNIT_LENGTH_Y = "kernelUnitLengthY";
    String ATTR_LIGHT_COLOR = "lightColor";
    String ATTR_LIMITING_CONE_ANGLE = "limitingConeAngle";
    String ATTR_NUM_OCTAVES = "numOctaves";
    String ATTR_MASK = CSS_MASK_PROPERTY;
    String ATTR_MASK_CONTENT_UNITS = "maskContentUnits";
    String ATTR_MASK_UNITS = "maskUnits";
    String ATTR_MEDIA = "media";
    String ATTR_METHOD = "method";
    String SVG_MODE_ATTRIBUTE = "mode";
    String SVG_OFFSET_ATTRIBUTE = "offset";
    String ATTR_OPACITY = CSS_OPACITY_PROPERTY;
    String SVG_OPERATOR_ATTRIBUTE = "operator";
    String ATTR_ORDER = "order";
    String ATTR_ORDER_X = "orderX";
    String ATTR_ORDER_Y = "orderY";
    String ATTR_PATTERN_CONTENT_UNITS = "patternContentUnits";
    String ATTR_PATTERN_TRANSFORM = "patternTransform";
    String ATTR_PATTERN_UNITS = "patternUnits";
    String ATTR_POINTS_AT_X = "pointsAtX";
    String ATTR_POINTS_AT_Y = "pointsAtY";
    String ATTR_POINTS_AT_Z = "pointsAtZ";
    String ATTR_POINTS = "points";
    String ATTR_PRESERVE_ALPHA = "preserveAlpha";
    String ATTR_PRESERVE_ASPECT_RATIO = "preserveAspectRatio";
    String ATTR_PRIMITIVE_UNITS = "primitiveUnits";
    String SVG_R_ATTRIBUTE = "r";
    String ATTR_RADIUS = "radius";
    String ATTR_RESULT = "result";
    String ATTR_RESULT_SCALE = "resultScale";
    String SVG_RX_ATTRIBUTE = "rx";
    String SVG_RY_ATTRIBUTE = "ry";
    String ATTR_SCALE = "scale";
    String ATTR_SEED = "seed";
    String ATTR_SHAPE_RENDERING = CSS_SHAPE_RENDERING_PROPERTY;
    String SVG_SLOPE_ATTRIBUTE = "slope";
    String ATTR_SPACE = "space";
    String ATTR_SPACING = "spacing";
    String ATTR_SPECULAR_CONSTANT = "specularConstant";
    String ATTR_SPECULAR_EXPONENT = "specularExponent";
    String ATTR_SPREAD_METHOD = "spreadMethod";
    String ATTR_START_OFFSET = "startOffset";
    String ATTR_STD_DEVIATION = "stdDeviation";
    String ATTR_STOP_COLOR = "stop-color";
    String ATTR_STITCH_TILES = "stitchTiles";
    String ATTR_STOP_OPACITY = CSS_STOP_OPACITY_PROPERTY;
    String ATTR_STROKE = CSS_STROKE_PROPERTY;
    String ATTR_STROKE_OPACITY = CSS_STROKE_OPACITY_PROPERTY;
    String ATTR_STROKE_DASHARRAY = CSS_STROKE_DASHARRAY_PROPERTY;
    String ATTR_STROKE_DASHOFFSET = CSS_STROKE_DASHOFFSET_PROPERTY;
    String ATTR_STROKE_LINECAP = CSS_STROKE_LINECAP_PROPERTY;
    String ATTR_STROKE_LINEJOIN = CSS_STROKE_LINEJOIN_PROPERTY;
    String ATTR_STROKE_MITERLIMIT = CSS_STROKE_MITERLIMIT_PROPERTY;
    String ATTR_STROKE_WIDTH = CSS_STROKE_WIDTH_PROPERTY;
    String ATTR_STYLE = "style";
    String ATTR_SURFACE_SCALE = "surfaceScale";
    String ATTR_SYSTEM_LANGUAGE = "systemLanguage";
    String ATTR_TABLE = "table";
    String SVG_TARGET_ATTRIBUTE = "target";
    String ATTR_TARGET_X = "targetX";
    String ATTR_TARGET_Y = "targetY";
    String SVG_TABLE_VALUES_ATTRIBUTE = "tableValues";
    String ATTR_TEXT_ANCHOR = CSS_TEXT_ANCHOR_PROPERTY;
    String ATTR_TEXT_LENGTH = "textLength";
    String ATTR_TEXT_RENDERING = "text-rendering";
    String ATTR_TITLE = "title";
    String ATTR_TRANSFORM = "transform";
    String SVG_TYPE_ATTRIBUTE = "type";
    String SVG_VALUES_ATTRIBUTE = "values";
    String ATTR_VIEW_BOX = "viewBox";
    String ATTR_X = "x";
    String ATTR_X_CHANNEL_SELECTOR = "xChannelSelector";
    String ATTR_X1 = "x1";
    String ATTR_X2 = "x2";
    String ATTR_Y = "y";
    String ATTR_Y_CHANNEL_SELECTOR = "yChannelSelector";
    String ATTR_Y1 = "y1";
    String ATTR_Y2 = "y2";
    String ATTR_WIDTH = "width";
    String ATTR_Z = "z";

    /**
     * Attribute values
     */
    String VALUE_A = "A";
    String VALUE_ARITHMETIC = "arithmetic";
    String VALUE_ALIGN = "align";
    String VALUE_ATOP = "atop";
    String VALUE_AUTO = "auto";
    String VALUE_B = "B";
    String VALUE_BACKGROUND_IMAGE = "BackgroundImage";
    String VALUE_COMPOSITE = "composite";
    String VALUE_CRISP_EDGES = "crispEdges";
    String SVG_DARKEN_VALUE = "darken";
    String VALUE_DIGIT_ONE = "1";
    String VALUE_DILATE = "dilate";
    String SVG_DISCRETE_VALUE = "discrete";
    String VALUE_EDGE_DUPLICATE = "duplicate";
    String VALUE_EDGE_NONE = "none";
    String VALUE_END = "end";
    String VALUE_ERODE = "erode";
    String VALUE_EVEN_ODD = "evenodd";
    String VALUE_EXACT = "exact";
    String VALUE_FLOOD = "flood";
    String VALUE_FONT_WEIGHT_BOLD = "bold";
    String VALUE_FONT_WEIGHT_BOLDER = "bolder";
    String VALUE_FONT_WEIGHT_LIGHTER = "lighter";
    String VALUE_FONT_WEIGHT_100 = "100";
    String VALUE_FONT_WEIGHT_200 = "200";
    String VALUE_FONT_WEIGHT_300 = "300";
    String VALUE_FONT_WEIGHT_400 = "400";
    String VALUE_FONT_WEIGHT_500 = "500";
    String VALUE_FONT_WEIGHT_600 = "600";
    String VALUE_FONT_WEIGHT_700 = "700";
    String VALUE_FONT_WEIGHT_800 = "800";
    String VALUE_FONT_WEIGHT_900 = "900";
    String VALUE_FONT_WEIGHT_NORMAL = "normal";
    String VALUE_FONT_STYLE_NORMAL = "normal";
    String VALUE_FONT_STYLE_ITALIC = "italic";
    String VALUE_FONT_STYLE_OBLIQUE = "oblique";
    String VALUE_FRACTAL_NOISE = "fractalNoise";
    String VALUE_G = "G";
    String SVG_GAMMA_VALUE = "gamma";
    String VALUE_GEOMETRIC_PRECISION = "geometricPrecision";
    String SVG_HUE_ROTATE_VALUE = "hueRotate";
    String VALUE_HUNDRED_PERCENT = "100%";
    String VALUE_IN = "in";
    String SVG_IDENTITY_VALUE = "identity";
    String SVG_LIGHTEN_VALUE = "lighten";
    String VALUE_LINE_CAP_BUTT = "butt";
    String VALUE_LINE_CAP_SQUARE = "square";
    String VALUE_LINE_CAP_ROUND = "round";
    String VALUE_LINE_JOIN_ROUND = "round";
    String VALUE_LINE_JOIN_BEVEL = "bevel";
    String VALUE_LINE_JOIN_MITER = "miter";
    String SVG_LINEAR_VALUE = "linear";
    String VALUE_LINEAR_RGB = "linearRGB";
    String SVG_LUMINANCE_TO_ALPHA_VALUE = "luminanceToAlpha";
    String SVG_MATRIX_VALUE = "matrix";
    String VALUE_MIDDLE = "middle";
    String SVG_MULTIPLY_VALUE = "multiply";
    String VALUE_NEW = "new";
    String VALUE_NINETY = "90";
    String SVG_NORMAL_VALUE = "normal";
    String VALUE_NO_STITCH = "noStitch";
    String VALUE_NON_ZERO = "nonzero";
    String VALUE_NONE = "none";
    String SVG_OBJECT_BOUNDING_BOX_VALUE = "objectBoundingBox";
    String VALUE_ONE = "1";
    String VALUE_OPAQUE = "1";
    String VALUE_OPTIMIZE_LEGIBILITY = "optimizeLegibility";
    String VALUE_OPTIMIZE_QUALITY = "optimizeQuality";
    String VALUE_OPTIMIZE_SPEED = "optimizeSpeed";
    String VALUE_OUT = "out";
    String SVG_OVER_VALUE = "over";
    String VALUE_PAD = "pad";
    String VALUE_PRESERVE = "preserve";
    String VALUE_R = "R";
    String VALUE_REFLECT = "reflect";
    String VALUE_REPEAT = "repeat";
    String SVG_SATURATE_VALUE = "saturate";
    String SVG_SCREEN_VALUE = "screen";
    String VALUE_SOURCE_GRAPHIC = "SourceGraphic";
    String VALUE_SRGB = "sRGB";
    String VALUE_START = "start";
    String VALUE_STITCH = "stitch";
    String VALUE_STRETCH = "stretch";
    String SVG_TABLE_VALUE = "table";
    String VALUE_TURBULENCE = "turbulence";
    String VALUE_TYPE_LINEAR = "linear";
    String VALUE_TYPE_TABLE = "table";
    String SVG_USER_SPACE_ON_USE_VALUE = "userSpaceOnUse";
    String VALUE_USER_SPACE = "userSpace";
    String VALUE_XOR = "xor";
    String VALUE_ZERO = "0";
    String VALUE_ZERO_PERCENT = "0%";

    /**
     * Transform constants
     */
    String TRANSFORM_TRANSLATE = "translate";
    String TRANSFORM_ROTATE = "rotate";
    String TRANSFORM_SCALE = "scale";
    String TRANSFORM_SKEWX = "skewX";
    String TRANSFORM_SKEWY = "skewY";
    String TRANSFORM_MATRIX = "matrix";

    /**
     * Path constants
     */
    String PATH_CLOSE = "Z ";
    String PATH_CUBIC_TO = "C ";
    String PATH_MOVE = "M ";
    String PATH_LINE_TO = "L ";
    String PATH_QUAD_TO = "Q ";

    /**
     * Default values for attributes
     */
    String SVG_DEFAULT_VALUE_CIRCLE_CX = "0";
    String SVG_DEFAULT_VALUE_CIRCLE_CY = "0";

    String SVG_DEFAULT_VALUE_CLIP_PATH_CLIP_PATH_UNITS = SVG_USER_SPACE_ON_USE_VALUE;

    String SVG_DEFAULT_VALUE_ELLIPSE_CX = "0";
    String SVG_DEFAULT_VALUE_ELLIPSE_CY = "0";

    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K1 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K2 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K3 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K4 = "0";

    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_TABLE_VALUES = "";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_SLOPE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_INTERCEPT = "0";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_AMPLITUDE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_EXPONENT = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_OFFSET = "0";

    String SVG_DEFAULT_VALUE_FE_DIFFUSE_LIGHTING_SURFACE_SCALE = "1";
    String SVG_DEFAULT_VALUE_FE_DIFFUSE_LIGHTING_DIFFUSE_CONSTANT = "1";

    String SVG_DEFAULT_VALUE_FE_DISPLACEMENT_MAP_SCALE = "0";

    String SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_AZIMUTH = "0";
    String SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_ELEVATION = "0";

    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_X = "0";
    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Z = "0";

    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_X = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Z = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_X = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Z = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_SPECULAR_EXPONENT = "1";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_LIMITING_CONE_ANGLE = "90";

    String DEFAULT_VALUE_FILTER_X = "-10%";
    String DEFAULT_VALUE_FILTER_Y = "-10%";
    String DEFAULT_VALUE_FILTER_WIDTH = "120%";
    String DEFAULT_VALUE_FILTER_HEIGHT = "120%";

    String DEFAULT_VALUE_MASK_X = "-10%";
    String DEFAULT_VALUE_MASK_Y = "-10%";
    String DEFAULT_VALUE_MASK_WIDTH = "120%";
    String DEFAULT_VALUE_MASK_HEIGHT = "120%";

    String DEFAULT_VALUE_PATTERN_X = "0%";
    String DEFAULT_VALUE_PATTERN_Y = "0%";
    String DEFAULT_VALUE_PATTERN_WIDTH = null; // required
    String DEFAULT_VALUE_PATTERN_HEIGHT = null; // required

    /**
     * Default values for attributes
     */
    //String VALUE_FILTER_X_DEFAULT = "-10%";
    //String VALUE_FILTER_Y_DEFAULT = "-10%";
    //String VALUE_FILTER_WIDTH_DEFAULT = "120%";
    //String VALUE_FILTER_HEIGHT_DEFAULT = "120%";
    //String VALUE_MASK_X_DEFAULT = "-10%";
    //String VALUE_MASK_Y_DEFAULT = "-10%";
    //String VALUE_MASK_WIDTH_DEFAULT = "120%";
    //String VALUE_MASK_HEIGHT_DEFAULT = "120%";
    //String VALUE_PATTERN_X_DEFAULT = "0%";
    //String VALUE_PATTERN_Y_DEFAULT = "0%";
    //String VALUE_PATTERN_WIDTH_DEFAULT = "100%";
    //String VALUE_PATTERN_HEIGHT_DEFAULT = "100%";
    //float   DEFAULT_VALUE_BASE_FREQUENCY = 0.001f;
    //float   DEFAULT_VALUE_DX = 0f;
    //float   DEFAULT_VALUE_DY = 0f;
    //boolean DEFAULT_VALUE_FE_TURBULENCE_TYPE = false;
    //String  DEFAULT_VALUE_FILL_RULE = VALUE_NON_ZERO;
    //int     DEFAULT_VALUE_NUM_OCTAVES = 1;
    //int     DEFAULT_VALUE_SEED = 0;
    //boolean DEFAULT_VALUE_STITCH_TILES = false;
    //String  DEFAULT_VALUE_TEXT_ANCHOR = VALUE_START;

}
