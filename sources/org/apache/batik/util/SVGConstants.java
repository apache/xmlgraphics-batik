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
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGConstants extends CSSConstants {

    String TAG_TEXT_PATH = "textPath";
    String ATTR_ID = "id";

    /////////////////////////////////////////////////////////////////////////
    // SVG general
    /////////////////////////////////////////////////////////////////////////

    String SVG_PUBLIC_ID =
        "-//W3C//DTD SVG 20001102//EN";
    String SVG_SYSTEM_ID =
        "http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd";
    String SVG_NAMESPACE_URI =
        "http://www.w3.org/2000/svg";

    /////////////////////////////////////////////////////////////////////////
    // SVG tags
    /////////////////////////////////////////////////////////////////////////

    String SVG_ANIMATE_TAG = "animate";
    String SVG_A_TAG = "a";
    String SVG_CIRCLE_TAG = "circle";
    String SVG_CLIP_PATH_TAG = "clipPath";
    String SVG_COLOR_PROFILE_TAG = "color-profile";
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
    String SVG_FE_MERGE_NODE_TAG = "feMergeNode";
    String SVG_FE_MERGE_TAG = "feMerge";
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
    String SVG_LINEAR_GRADIENT_TAG = "linearGradient";
    String SVG_LINE_TAG = "line";
    String SVG_MARKER_TAG = "marker";
    String SVG_MASK_TAG = "mask";
    String SVG_METADATA_TAG = "metadata";
    String SVG_PATH_TAG = "path";
    String SVG_PATTERN_TAG = "pattern";
    String SVG_POLYGON_TAG = "polygon";
    String SVG_POLYLINE_TAG = "polyline";
    String SVG_RADIAL_GRADIENT_TAG = "radialGradient";
    String SVG_RECT_TAG = "rect";
    String SVG_SCRIPT_TAG = "script";
    String SVG_STOP_TAG = "stop";
    String SVG_STYLE_TAG = "style";
    String SVG_SVG_TAG = "svg";
    String SVG_SWITCH_TAG = "switch";
    String SVG_SYMBOL_TAG = "symbol";
    String SVG_TEXT_PATH_TAG = "textPath";
    String SVG_TEXT_TAG = "text";
    String SVG_TITLE_TAG = "title";
    String SVG_TREF_TAG = "tref";
    String SVG_TSPAN_TAG = "tspan";
    String SVG_USE_TAG = "use";

    /////////////////////////////////////////////////////////////////////////
    // SVG attributes
    /////////////////////////////////////////////////////////////////////////

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
    String SVG_DIFFUSE_CONSTANT_ATTRIBUTE = "diffuseConstant";
    String SVG_DIVISOR_ATTRIBUTE = "divisor";
    String SVG_DX_ATTRIBUTE = "dx";
    String SVG_DY_ATTRIBUTE = "dy";
    String SVG_D_ATTRIBUTE = "d";
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
    String SVG_FX_ATTRIBUTE = "fx";
    String SVG_FY_ATTRIBUTE = "fy";
    String SVG_GRADIENT_TRANSFORM_ATTRIBUTE = "gradientTransform";
    String SVG_GRADIENT_UNITS_ATTRIBUTE = "gradientUnits";
    String SVG_HEIGHT_ATTRIBUTE = "height";
    String SVG_HREF_ATTRIBUTE = "href";
    String SVG_ID_ATTRIBUTE = "id";
    String SVG_IMAGE_RENDERING_ATTRIBUTE = CSS_IMAGE_RENDERING_PROPERTY;
    String SVG_IN2_ATTRIBUTE = "in2";
    String SVG_INTERCEPT_ATTRIBUTE = "intercept";
    String SVG_IN_ATTRIBUTE = "in";
    String SVG_K1_ATTRIBUTE = "k1";
    String SVG_K2_ATTRIBUTE = "k2";
    String SVG_K3_ATTRIBUTE = "k3";
    String SVG_K4_ATTRIBUTE = "k4";
    String SVG_KERNEL_MATRIX_ATTRIBUTE = "kernelMatrix";
    String SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE = "kernelUnitLength";
    String SVG_KERNEL_UNIT_LENGTH_X_ATTRIBUTE = "kernelUnitLengthX";
    String SVG_KERNEL_UNIT_LENGTH_Y_ATTRIBUTE = "kernelUnitLengthY";
    String SVG_KERNING_ATTRIBUTE = "kerning";
    String SVG_LENGTH_ADJUST_ATTRIBUTE = "lengthAdjust";
    String SVG_LIGHT_COLOR_ATTRIBUTE = "lightColor";
    String SVG_LIMITING_CONE_ANGLE_ATTRIBUTE = "limitingConeAngle";
    String SVG_MARKER_HEIGHT_ATTRIBUTE = "markerHeight";
    String SVG_MARKER_UNITS_ATTRIBUTE = "markerUnits";
    String SVG_MARKER_WIDTH_ATTRIBUTE = "markerWidth";
    String SVG_MASK_ATTRIBUTE = CSS_MASK_PROPERTY;
    String SVG_MASK_CONTENT_UNITS_ATTRIBUTE = "maskContentUnits";
    String SVG_MASK_UNITS_ATTRIBUTE = "maskUnits";
    String SVG_MEDIA_ATTRIBUTE = "media";
    String SVG_METHOD_ATTRIBUTE = "method";
    String SVG_MODE_ATTRIBUTE = "mode";
    String SVG_NAME_ATTRIBUTE = "name";
    String SVG_NUM_OCTAVES_ATTRIBUTE = "numOctaves";
    String SVG_OFFSET_ATTRIBUTE = "offset";
    String SVG_OPACITY_ATTRIBUTE = CSS_OPACITY_PROPERTY;
    String SVG_OPERATOR_ATTRIBUTE = "operator";
    String SVG_ORDER_ATTRIBUTE = "order";
    String SVG_ORDER_X_ATTRIBUTE = "orderX";
    String SVG_ORDER_Y_ATTRIBUTE = "orderY";
    String SVG_ORIENT_ATTRIBUTE = "orient";
    String SVG_PATTERN_CONTENT_UNITS_ATTRIBUTE = "patternContentUnits";
    String SVG_PATTERN_TRANSFORM_ATTRIBUTE = "patternTransform";
    String SVG_PATTERN_UNITS_ATTRIBUTE = "patternUnits";
    String SVG_POINTS_ATTRIBUTE = "points";
    String SVG_POINTS_AT_X_ATTRIBUTE = "pointsAtX";
    String SVG_POINTS_AT_Y_ATTRIBUTE = "pointsAtY";
    String SVG_POINTS_AT_Z_ATTRIBUTE = "pointsAtZ";
    String SVG_PRESERVE_ALPHA_ATTRIBUTE = "preserveAlpha";
    String SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE = "preserveAspectRatio";
    String SVG_PRIMITIVE_UNITS_ATTRIBUTE = "primitiveUnits";
    String SVG_RADIUS_ATTRIBUTE = "radius";
    String SVG_REFX_ATTRIBUTE = "refX";
    String SVG_REFY_ATTRIBUTE = "refY";
    String SVG_RENDERING_INTENT_ATTRIBUTE = "rendering-intent";
    String SVG_RESULT_ATTRIBUTE = "result";
    String SVG_RESULT_SCALE_ATTRIBUTE = "resultScale";
    String SVG_RX_ATTRIBUTE = "rx";
    String SVG_RY_ATTRIBUTE = "ry";
    String SVG_R_ATTRIBUTE = "r";
    String SVG_SCALE_ATTRIBUTE = "scale";
    String SVG_SEED_ATTRIBUTE = "seed";
    String SVG_SHAPE_RENDERING_ATTRIBUTE = CSS_SHAPE_RENDERING_PROPERTY;
    String SVG_SLOPE_ATTRIBUTE = "slope";
    String SVG_SPACE_ATTRIBUTE = "space";
    String SVG_SPACING_ATTRIBUTE = "spacing";
    String SVG_SPECULAR_CONSTANT_ATTRIBUTE = "specularConstant";
    String SVG_SPECULAR_EXPONENT_ATTRIBUTE = "specularExponent";
    String SVG_SPREAD_METHOD_ATTRIBUTE = "spreadMethod";
    String SVG_START_OFFSET_ATTRIBUTE = "startOffset";
    String SVG_STD_DEVIATION_ATTRIBUTE = "stdDeviation";
    String SVG_STITCH_TILES_ATTRIBUTE = "stitchTiles";
    String SVG_STOP_COLOR_ATTRIBUTE = "stop-color";
    String SVG_STOP_OPACITY_ATTRIBUTE = CSS_STOP_OPACITY_PROPERTY;
    String SVG_STROKE_ATTRIBUTE = CSS_STROKE_PROPERTY;
    String SVG_STROKE_DASHARRAY_ATTRIBUTE = CSS_STROKE_DASHARRAY_PROPERTY;
    String SVG_STROKE_DASHOFFSET_ATTRIBUTE = CSS_STROKE_DASHOFFSET_PROPERTY;
    String SVG_STROKE_LINECAP_ATTRIBUTE = CSS_STROKE_LINECAP_PROPERTY;
    String SVG_STROKE_LINEJOIN_ATTRIBUTE = CSS_STROKE_LINEJOIN_PROPERTY;
    String SVG_STROKE_MITERLIMIT_ATTRIBUTE = CSS_STROKE_MITERLIMIT_PROPERTY;
    String SVG_STROKE_OPACITY_ATTRIBUTE = CSS_STROKE_OPACITY_PROPERTY;
    String SVG_STROKE_WIDTH_ATTRIBUTE = CSS_STROKE_WIDTH_PROPERTY;
    String SVG_STYLE_ATTRIBUTE = "style";
    String SVG_SURFACE_SCALE_ATTRIBUTE = "surfaceScale";
    String SVG_SYSTEM_LANGUAGE_ATTRIBUTE = "systemLanguage";
    String SVG_TABLE_ATTRIBUTE = "table";
    String SVG_TABLE_VALUES_ATTRIBUTE = "tableValues";
    String SVG_TARGET_ATTRIBUTE = "target";
    String SVG_TARGET_X_ATTRIBUTE = "targetX";
    String SVG_TARGET_Y_ATTRIBUTE = "targetY";
    String SVG_TEXT_ANCHOR_ATTRIBUTE = CSS_TEXT_ANCHOR_PROPERTY;
    String SVG_TEXT_LENGTH_ATTRIBUTE = "textLength";
    String SVG_TEXT_RENDERING_ATTRIBUTE = CSS_TEXT_RENDERING_PROPERTY;
    String SVG_TITLE_ATTRIBUTE = "title";
    String SVG_TRANSFORM_ATTRIBUTE = "transform";
    String SVG_TYPE_ATTRIBUTE = "type";
    String SVG_VALUES_ATTRIBUTE = "values";
    String SVG_VIEW_BOX_ATTRIBUTE = "viewBox";
    String SVG_WIDTH_ATTRIBUTE = "width";
    String SVG_X1_ATTRIBUTE = "x1";
    String SVG_X2_ATTRIBUTE = "x2";
    String SVG_X_ATTRIBUTE = "x";
    String SVG_X_CHANNEL_SELECTOR_ATTRIBUTE = "xChannelSelector";
    String SVG_Y1_ATTRIBUTE = "y1";
    String SVG_Y2_ATTRIBUTE = "y2";
    String SVG_Y_ATTRIBUTE = "y";
    String SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE = "yChannelSelector";
    String SVG_Z_ATTRIBUTE = "z";

    /////////////////////////////////////////////////////////////////////////
    // SVG attribute value
    /////////////////////////////////////////////////////////////////////////

    String SVG_ADJUST_SPACINGANDGLYPHS_VALUE = "spacingAndGlyphs";
    String SVG_ADJUST_SPACING_VALUE = "spacing";
    String SVG_ALIGN_VALUE = "align";
    String SVG_ARITHMETIC_VALUE = "arithmetic";
    String SVG_ATOP_VALUE = "atop";
    String SVG_AUTO_VALUE = "auto";
    String SVG_A_VALUE = "A";
    String SVG_BACKGROUND_ALPHA_VALUE = "BackgroundAlpha";
    String SVG_BACKGROUND_IMAGE_VALUE = "BackgroundImage";
    String SVG_B_VALUE = "B";
    String SVG_COMPOSITE_VALUE = "composite";
    String SVG_CRISP_EDGES_VALUE = "crispEdges";
    String SVG_DARKEN_VALUE = "darken";
    String SVG_DIGIT_ONE_VALUE = "1";
    String SVG_DILATE_VALUE = "dilate";
    String SVG_DISCRETE_VALUE = "discrete";
    String SVG_DUPLICATE_VALUE = "duplicate";
    String SVG_END_VALUE = "end";
    String SVG_ERODE_VALUE = "erode";
    String SVG_EVEN_ODD_VALUE = "evenodd";
    String SVG_EXACT_VALUE = "exact";
    String SVG_FALSE_VALUE = "false";
    String SVG_FILL_PAINT_VALUE = "FillPaint";
    String SVG_FLOOD_VALUE = "flood";
    String SVG_FONT_STYLE_ITALIC_VALUE = "italic";
    String SVG_FONT_STYLE_NORMAL_VALUE = "normal";
    String SVG_FONT_STYLE_OBLIQUE_VALUE = "oblique";
    String SVG_FONT_WEIGHT_100_VALUE = "100";
    String SVG_FONT_WEIGHT_200_VALUE = "200";
    String SVG_FONT_WEIGHT_300_VALUE = "300";
    String SVG_FONT_WEIGHT_400_VALUE = "400";
    String SVG_FONT_WEIGHT_500_VALUE = "500";
    String SVG_FONT_WEIGHT_600_VALUE = "600";
    String SVG_FONT_WEIGHT_700_VALUE = "700";
    String SVG_FONT_WEIGHT_800_VALUE = "800";
    String SVG_FONT_WEIGHT_900_VALUE = "900";
    String SVG_FONT_WEIGHT_BOLDER_VALUE = "bolder";
    String SVG_FONT_WEIGHT_BOLD_VALUE = "bold";
    String SVG_FONT_WEIGHT_LIGHTER_VALUE = "lighter";
    String SVG_FONT_WEIGHT_NORMAL_VALUE = "normal";
    String SVG_FRACTAL_NOISE_VALUE = "fractalNoise";
    String SVG_GAMMA_VALUE = "gamma";
    String SVG_GEOMETRIC_PRECISION_VALUE = "geometricPrecision";
    String SVG_G_VALUE = "G";
    String SVG_HUE_ROTATE_VALUE = "hueRotate";
    String SVG_HUNDRED_PERCENT_VALUE = "100%";
    String SVG_IDENTITY_VALUE = "identity";
    String SVG_IN_VALUE = "in";
    String SVG_LIGHTEN_VALUE = "lighten";
    String SVG_LINEAR_RGB_VALUE = "linearRGB";
    String SVG_LINEAR_VALUE = "linear";
    String SVG_LINE_CAP_BUTT_VALUE = "butt";
    String SVG_LINE_CAP_ROUND_VALUE = "round";
    String SVG_LINE_CAP_SQUARE_VALUE = "square";
    String SVG_LINE_JOIN_BEVEL_VALUE = "bevel";
    String SVG_LINE_JOIN_MITER_VALUE = "miter";
    String SVG_LINE_JOIN_ROUND_VALUE = "round";
    String SVG_LUMINANCE_TO_ALPHA_VALUE = "luminanceToAlpha";
    String SVG_MATRIX_VALUE = "matrix";
    String SVG_MIDDLE_VALUE = "middle";
    String SVG_MULTIPLY_VALUE = "multiply";
    String SVG_NEW_VALUE = "new";
    String SVG_NINETY_VALUE = "90";
    String SVG_NONE_VALUE = "none";
    String SVG_NON_ZERO_VALUE = "nonzero";
    String SVG_NORMAL_VALUE = "normal";
    String SVG_NO_STITCH_VALUE = "noStitch";
    String SVG_OBJECT_BOUNDING_BOX_VALUE = "objectBoundingBox";
    String SVG_ONE_VALUE = "1";
    String SVG_OPAQUE_VALUE = "1";
    String SVG_OPTIMIZE_LEGIBILITY_VALUE = "optimizeLegibility";
    String SVG_OPTIMIZE_QUALITY_VALUE = "optimizeQuality";
    String SVG_OPTIMIZE_SPEED_VALUE = "optimizeSpeed";
    String SVG_OUT_VALUE = "out";
    String SVG_OVER_VALUE = "over";
    String SVG_PAD_VALUE = "pad";
    String SVG_PRESERVE_VALUE = "preserve";
    String SVG_REFLECT_VALUE = "reflect";
    String SVG_RENDERING_INTENT_ABSOLUTE_COLORIMETRIC_VALUE = "absolute-colorimetric";
    String SVG_RENDERING_INTENT_AUTO_VALUE = "auto";
    String SVG_RENDERING_INTENT_PERCEPTUAL_VALUE = "perceptual";
    String SVG_RENDERING_INTENT_RELATIVE_COLORIMETRIC_VALUE = "relative-colorimetric";
    String SVG_RENDERING_INTENT_SATURATION_VALUE = "saturation";
    String SVG_REPEAT_VALUE = "repeat";
    String SVG_R_VALUE = "R";
    String SVG_SATURATE_VALUE = "saturate";
    String SVG_SCREEN_VALUE = "screen";
    String SVG_SOURCE_ALPHA_VALUE = "SourceAlpha";
    String SVG_SOURCE_GRAPHIC_VALUE = "SourceGraphic";
    String SVG_SRGB_VALUE = "sRGB";
    String SVG_START_VALUE = "start";
    String SVG_STITCH_VALUE = "stitch";
    String SVG_STRETCH_VALUE = "stretch";
    String SVG_STROKE_PAINT_VALUE = "StrokePaint";
    String SVG_STROKE_WIDTH_VALUE = "strokeWidth";
    String SVG_TABLE_VALUE = "table";
    String SVG_TRUE_VALUE = "true";
    String SVG_TURBULENCE_VALUE = "turbulence";
    String SVG_TYPE_LINEAR_VALUE = "linear";
    String SVG_TYPE_TABLE_VALUE = "table";
    String SVG_USER_SPACE_ON_USE_VALUE = "userSpaceOnUse";
    String SVG_WRAP_VALUE = "wrap";
    String SVG_XOR_VALUE = "xor";
    String SVG_ZERO_PERCENT_VALUE = "0%";
    String SVG_ZERO_VALUE = "0";


    ///////////////////////////////////////////////////////////////////


    String TRANSFORM_TRANSLATE = "translate";
    String TRANSFORM_ROTATE = "rotate";
    String TRANSFORM_SCALE = "scale";
    String TRANSFORM_SKEWX = "skewX";
    String TRANSFORM_SKEWY = "skewY";
    String TRANSFORM_MATRIX = "matrix";

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

    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_TABLE_VALUES = "";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_SLOPE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_INTERCEPT = "0";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_AMPLITUDE = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_EXPONENT = "1";
    String SVG_DEFAULT_VALUE_COMPONENT_TRANSFER_FUNCTION_OFFSET = "0";

    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K1 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K2 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K3 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_K4 = "0";
    String SVG_DEFAULT_VALUE_FE_COMPOSITE_OPERATOR = SVG_OVER_VALUE;

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

    String SVG_DEFAULT_VALUE_IMAGE_X = "0";
    String SVG_DEFAULT_VALUE_IMAGE_Y = "0";

    String SVG_DEFAULT_VALUE_MARKER_REFX = "0";
    String SVG_DEFAULT_VALUE_MARKER_REFY = "0";
    String SVG_DEFAULT_VALUE_MARKER_MARKER_WIDTH = "3";
    String SVG_DEFAULT_VALUE_MARKER_MARKER_HEIGHT = "3";
    String SVG_DEFAULT_VALUE_MARKER_MARKER_UNITS = "strokeWidth";
    String SVG_DEFAULT_VALUE_MARKER_ORIENT = "0";

    String SVG_DEFAULT_VALUE_MASK_MASK_UNITS =
        SVG_USER_SPACE_ON_USE_VALUE;

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
}
