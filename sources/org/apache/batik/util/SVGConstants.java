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

    //
    // Tags
    //
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
    String SVG_FE_CONVOLVE_MATRIX_TAG = "feConvolveMatrix";
    String SVG_FE_DIFFUSE_LIGHTING_TAG = "feDiffuseLighting";
    String SVG_FE_DISPLACEMENT_MAP_TAG = "feDisplacementMap";
    String SVG_FE_DISTANT_LIGHT_TAG = "feDistantLight";
    String SVG_FE_FLOOD_TAG = "feFlood";
    String SVG_FE_FUNC_A_TAG = "feFuncA";
    String SVG_FE_FUNC_B_TAG = "feFuncB";
    String SVG_FE_FUNC_G_TAG = "feFuncG";
    String SVG_FE_FUNC_R_TAG = "feFuncR";
    String SVG_FE_GAUSSIAN_BLUR_TAG = "feGaussianBlur";
    String SVG_FE_IMAGE_TAG = "feImage";
    String SVG_FE_MERGE_TAG = "feMerge";
    String SVG_FE_MERGE_NODE_TAG = "feMergeNode";
    String SVG_FE_MORPHOLOGY_TAG = "feMorphology";
    String SVG_FE_OFFSET_TAG = "feOffset";
    String SVG_FE_POINT_LIGHT_TAG = "fePointLight";
    String SVG_FE_SPECULAR_LIGHTING_TAG = "feSpecularLighting";
    String SVG_FE_SPOT_LIGHT_TAG = "feSpotLight";
    String SVG_FE_TILE_TAG = "feTile";
    String SVG_FE_TURBULENCE_TAG = "feTurbulence";
    String SVG_FILTER_TAG = "filter";
    String SVG_G_TAG = "g";
    String SVG_IMAGE_TAG = "image";
    String TAG_LINE = "line";
    String TAG_LINEAR_GRADIENT = "linearGradient";
    String SVG_MASK_TAG = "mask";
    String SVG_METADATA_TAG = "metadata";
    String TAG_PATH = "path";
    String TAG_PATTERN = "pattern";
    String TAG_POLYGON = "polygon";
    String TAG_POLYLINE = "polyline";
    String TAG_RADIAL_GRADIENT = "radialGradient";
    String TAG_RECT = "rect";
    String SVG_SCRIPT_TAG = "script";
    String SVG_STOP_TAG = "stop";
    String TAG_STYLE = "style";
    String TAG_SVG = "svg";
    String SVG_SWITCH_TAG = "switch";
    String TAG_SYMBOL = "symbol";
    String SVG_TEXT_TAG = "text";
    String TAG_TEXT_PATH = "textPath";
    String SVG_TITLE_TAG = "title";
    String TAG_TREF = "tref";
    String TAG_TSPAN = "tspan";
    String SVG_USE_TAG = "use";

    //
    // Attribute names
    //
    String SVG_AMPLITUDE_ATTRIBUTE = "amplitude";
    String SVG_AZIMUTH_ATTRIBUTE = "azimuth";
    String SVG_BASE_FREQUENCY_ATTRIBUTE = "baseFrequency";
    String SVG_BIAS_ATTRIBUTE = "bias";
    String SVG_CLIP_PATH_ATTRIBUTE = CSS_CLIP_PATH_PROPERTY;
    String SVG_CLIP_PATH_UNITS_ATTRIBUTE = "clipPathUnits";
    String SVG_COLOR_INTERPOLATION_ATTRIBUTE = CSS_COLOR_INTERPOLATION_PROPERTY;
    String SVG_COLOR_RENDERING_ATTRIBUTE = CSS_COLOR_RENDERING_PROPERTY;
    String SVG_CX_ATTRIBUTE = "cx";
    String SVG_CY_ATTRIBUTE = "cy";
    String SVG_D_ATTRIBUTE = "d";
    String SVG_DIFFUSE_CONSTANT_ATTRIBUTE = "diffuseConstant";
    String SVG_DIVISOR_ATTRIBUTE = "divisor";
    String SVG_DX_ATTRIBUTE = "dx";
    String SVG_DY_ATTRIBUTE = "dy";
    String SVG_EDGE_MODE_ATTRIBUTE = "edgeMode";
    String SVG_ELEVATION_ATTRIBUTE = "elevation";
    String SVG_ENABLE_BACKGROUND_ATTRIBUTE = CSS_ENABLE_BACKGROUND_PROPERTY;
    String SVG_EXPONENT_ATTRIBUTE = "exponent";
    String SVG_FILL_ATTRIBUTE = CSS_FILL_PROPERTY;
    String SVG_FILL_OPACITY_ATTRIBUTE = CSS_FILL_OPACITY_PROPERTY;
    String SVG_FILL_RULE_ATTRIBUTE = CSS_FILL_RULE_PROPERTY;
    String SVG_FILTER_ATTRIBUTE = CSS_FILTER_PROPERTY;
    String SVG_FILTER_RES_ATTRIBUTE = "filterRes";
    String SVG_FILTER_UNITS_ATTRIBUTE = "filterUnits";
    String SVG_FLOOD_COLOR_ATTRIBUTE = CSS_FLOOD_COLOR_PROPERTY;
    String SVG_FLOOD_OPACITY_ATTRIBUTE = CSS_FLOOD_OPACITY_PROPERTY;
    String SVG_FONT_FAMILY_ATTRIBUTE = CSS_FONT_FAMILY_PROPERTY;
    String SVG_FONT_SIZE_ATTRIBUTE = CSS_FONT_SIZE_PROPERTY;
    String SVG_FONT_STYLE_ATTRIBUTE = CSS_FONT_STYLE_PROPERTY;
    String SVG_FONT_WEIGHT_ATTRIBUTE = CSS_FONT_WEIGHT_PROPERTY;
    String ATTR_FX = "fx";
    String ATTR_FY = "fy";
    String ATTR_GRADIENT_TRANSFORM = "gradientTransform";
    String ATTR_GRADIENT_UNITS = "gradientUnits";
    String SVG_HEIGHT_ATTRIBUTE = "height";
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
    String SVG_KERNEL_MATRIX_ATTRIBUTE = "kernelMatrix";
    String SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE = "kernelUnitLength";
    String SVG_KERNEL_UNIT_LENGTH_X_ATTRIBUTE = "kernelUnitLengthX";
    String SVG_KERNEL_UNIT_LENGTH_Y_ATTRIBUTE = "kernelUnitLengthY";
    String ATTR_LIGHT_COLOR = "lightColor";
    String SVG_LIMITING_CONE_ANGLE_ATTRIBUTE = "limitingConeAngle";
    String SVG_NUM_OCTAVES_ATTRIBUTE = "numOctaves";
    String ATTR_MASK = CSS_MASK_PROPERTY;
    String ATTR_MASK_CONTENT_UNITS = "maskContentUnits";
    String SVG_MASK_UNITS_ATTRIBUTE = "maskUnits";
    String ATTR_MEDIA = "media";
    String ATTR_METHOD = "method";
    String SVG_MODE_ATTRIBUTE = "mode";
    String SVG_OFFSET_ATTRIBUTE = "offset";
    String ATTR_OPACITY = CSS_OPACITY_PROPERTY;
    String SVG_OPERATOR_ATTRIBUTE = "operator";
    String SVG_ORDER_ATTRIBUTE = "order";
    String SVG_ORDER_X_ATTRIBUTE = "orderX";
    String SVG_ORDER_Y_ATTRIBUTE = "orderY";
    String ATTR_PATTERN_CONTENT_UNITS = "patternContentUnits";
    String ATTR_PATTERN_TRANSFORM = "patternTransform";
    String ATTR_PATTERN_UNITS = "patternUnits";
    String SVG_POINTS_AT_X_ATTRIBUTE = "pointsAtX";
    String SVG_POINTS_AT_Y_ATTRIBUTE = "pointsAtY";
    String SVG_POINTS_AT_Z_ATTRIBUTE = "pointsAtZ";
    String ATTR_POINTS = "points";
    String SVG_PRESERVE_ALPHA_ATTRIBUTE = "preserveAlpha";
    String ATTR_PRESERVE_ASPECT_RATIO = "preserveAspectRatio";
    String SVG_PRIMITIVE_UNITS_ATTRIBUTE = "primitiveUnits";
    String SVG_R_ATTRIBUTE = "r";
    String ATTR_RADIUS = "radius";
    String ATTR_RESULT = "result";
    String ATTR_RESULT_SCALE = "resultScale";
    String SVG_RX_ATTRIBUTE = "rx";
    String SVG_RY_ATTRIBUTE = "ry";
    String ATTR_SCALE = "scale";
    String SVG_SEED_ATTRIBUTE = "seed";
    String ATTR_SHAPE_RENDERING = CSS_SHAPE_RENDERING_PROPERTY;
    String SVG_SLOPE_ATTRIBUTE = "slope";
    String ATTR_SPACE = "space";
    String ATTR_SPACING = "spacing";
    String SVG_SPECULAR_CONSTANT_ATTRIBUTE = "specularConstant";
    String SVG_SPECULAR_EXPONENT_ATTRIBUTE = "specularExponent";
    String ATTR_SPREAD_METHOD = "spreadMethod";
    String ATTR_START_OFFSET = "startOffset";
    String ATTR_STD_DEVIATION = "stdDeviation";
    String ATTR_STOP_COLOR = "stop-color";
    String SVG_STITCH_TILES_ATTRIBUTE = "stitchTiles";
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
    String SVG_SURFACE_SCALE_ATTRIBUTE = "surfaceScale";
    String SVG_SYSTEM_LANGUAGE_ATTRIBUTE = "systemLanguage";
    String ATTR_TABLE = "table";
    String SVG_TARGET_ATTRIBUTE = "target";
    String SVG_TARGET_X_ATTRIBUTE = "targetX";
    String SVG_TARGET_Y_ATTRIBUTE = "targetY";
    String SVG_TABLE_VALUES_ATTRIBUTE = "tableValues";
    String ATTR_TEXT_ANCHOR = CSS_TEXT_ANCHOR_PROPERTY;
    String ATTR_TEXT_LENGTH = "textLength";
    String ATTR_TEXT_RENDERING = CSS_TEXT_RENDERING_PROPERTY;
    String ATTR_TITLE = "title";
    String ATTR_TRANSFORM = "transform";
    String SVG_TYPE_ATTRIBUTE = "type";
    String SVG_VALUES_ATTRIBUTE = "values";
    String ATTR_VIEW_BOX = "viewBox";
    String SVG_X_ATTRIBUTE = "x";
    String SVG_X_CHANNEL_SELECTOR_ATTRIBUTE = "xChannelSelector";
    String ATTR_X1 = "x1";
    String ATTR_X2 = "x2";
    String SVG_Y_ATTRIBUTE = "y";
    String SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE = "yChannelSelector";
    String ATTR_Y1 = "y1";
    String ATTR_Y2 = "y2";
    String SVG_WIDTH_ATTRIBUTE = "width";
    String SVG_Z_ATTRIBUTE = "z";

    //
    // Attribute values
    //
    String SVG_A_VALUE = "A";
    String SVG_ARITHMETIC_VALUE = "arithmetic";
    String VALUE_ALIGN = "align";
    String SVG_ATOP_VALUE = "atop";
    String VALUE_AUTO = "auto";
    String SVG_B_VALUE = "B";
    String VALUE_BACKGROUND_IMAGE = "BackgroundImage";
    String VALUE_COMPOSITE = "composite";
    String VALUE_CRISP_EDGES = "crispEdges";
    String SVG_DARKEN_VALUE = "darken";
    String VALUE_DIGIT_ONE = "1";
    String VALUE_DILATE = "dilate";
    String SVG_DISCRETE_VALUE = "discrete";
    String SVG_DUPLICATE_VALUE = "duplicate";
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
    String SVG_FRACTAL_NOISE_VALUE = "fractalNoise";
    String SVG_G_VALUE = "G";
    String SVG_GAMMA_VALUE = "gamma";
    String VALUE_GEOMETRIC_PRECISION = "geometricPrecision";
    String SVG_HUE_ROTATE_VALUE = "hueRotate";
    String VALUE_HUNDRED_PERCENT = "100%";
    String SVG_IN_VALUE = "in";
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
    String SVG_NO_STITCH_VALUE = "noStitch";
    String VALUE_NON_ZERO = "nonzero";
    String SVG_NONE_VALUE = "none";
    String SVG_OBJECT_BOUNDING_BOX_VALUE = "objectBoundingBox";
    String VALUE_ONE = "1";
    String VALUE_OPAQUE = "1";
    String VALUE_OPTIMIZE_LEGIBILITY = "optimizeLegibility";
    String VALUE_OPTIMIZE_QUALITY = "optimizeQuality";
    String VALUE_OPTIMIZE_SPEED = "optimizeSpeed";
    String SVG_OUT_VALUE = "out";
    String SVG_OVER_VALUE = "over";
    String VALUE_PAD = "pad";
    String VALUE_PRESERVE = "preserve";
    String SVG_R_VALUE = "R";
    String VALUE_REFLECT = "reflect";
    String VALUE_REPEAT = "repeat";
    String SVG_SATURATE_VALUE = "saturate";
    String SVG_SCREEN_VALUE = "screen";
    String VALUE_SOURCE_GRAPHIC = "SourceGraphic";
    String VALUE_SRGB = "sRGB";
    String VALUE_START = "start";
    String SVG_STITCH_VALUE = "stitch";
    String VALUE_STRETCH = "stretch";
    String SVG_TABLE_VALUE = "table";
    String SVG_TURBULENCE_VALUE = "turbulence";
    String VALUE_TYPE_LINEAR = "linear";
    String VALUE_TYPE_TABLE = "table";
    String SVG_USER_SPACE_ON_USE_VALUE = "userSpaceOnUse";
    String VALUE_USER_SPACE = "userSpace";
    String SVG_WRAP_VALUE = "wrap";
    String SVG_XOR_VALUE = "xor";
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
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_OPERATOR = SVG_OVER_VALUE;

    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_TABLE_VALUES = "";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_SLOPE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_INTERCEPT = "0";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_AMPLITUDE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_EXPONENT = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_OFFSET = "0";

    String SVG_DEFAULT_VALUE_FE_CONVOLVE_MATRIX_EDGE_MODE = SVG_DUPLICATE_VALUE;

    String SVG_DEFAULT_VALUE_FE_DIFFUSE_LIGHTING_SURFACE_SCALE = "1";
    String SVG_DEFAULT_VALUE_FE_DIFFUSE_LIGHTING_DIFFUSE_CONSTANT = "1";

    String SVG_DEFAULT_VALUE_FE_DISPLACEMENT_MAP_SCALE = "0";

    String SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_AZIMUTH = "0";
    String SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_ELEVATION = "0";

    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_X = "0";
    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Z = "0";

    String SVG_DEFAULT_VALUE_FE_SPECULAR_LIGHTING_SURFACE_SCALE = "1";
    String SVG_DEFAULT_VALUE_FE_SPECULAR_LIGHTING_SPECULAR_CONSTANT = "1";
    String SVG_DEFAULT_VALUE_FE_SPECULAR_LIGHTING_SPECULAR_EXPONENT = "1";

    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_X = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Z = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_X = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Y = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Z = "0";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_SPECULAR_EXPONENT = "1";
    String SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_LIMITING_CONE_ANGLE = "90";

    String SVG_DEFAULT_VALUE_FE_TURBULENCE_NUM_OCTAVES = "1";
    String SVG_DEFAULT_VALUE_FE_TURBULENCE_SEED = "0";

    String SVG_DEFAULT_VALUE_FILTER_FILTER_UNITS =
        SVG_USER_SPACE_ON_USE_VALUE;
    String SVG_DEFAULT_VALUE_FILTER_PRIMITIVE_UNITS =
        SVG_USER_SPACE_ON_USE_VALUE;

    String SVG_DEFAULT_VALUE_MASK_MASK_UNITS =
        SVG_USER_SPACE_ON_USE_VALUE;

    String SVG_DEFAULT_VALUE_IMAGE_X = "0";
    String SVG_DEFAULT_VALUE_IMAGE_Y = "0";

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
