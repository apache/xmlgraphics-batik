/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

public interface ErrorConstants {
    // general errors

    public static final String ERR_UNEXPECTED =
        "unexpected exception";
    public static final String ERR_CONTEXT_NULL =
        "generatorContext should not be null";

    /// image handling errors

    public static final String ERR_IMAGE_DIR_NULL =
        "imageDir should not be null";
    public static final String ERR_IMAGE_DIR_DOES_NOT_EXIST =
        "imageDir does not exist";
    public static final String ERR_CANNOT_USE_IMAGE_DIR =
        "cannot convert imageDir to a URL value : ";
    public static final String ERR_IMAGE_NULL =
        "image should not be null";
    public static final String ERR_WRITE =
        "could not write image File ";

    // SVGGraphics2D errors

    public static final String ERR_CANVAS_SIZE_NULL =
        "canvas size should not be null";
    public static final String ERR_XOR =
        "XOR Mode is not supported by Graphics2D SVG Generator";
    public static final String ERR_ACI =
        "AttributedCharacterIterator not supported yet";

    // XmlWriter
    public static final String ERR_PROXY =
        "proxy should not be null";
    public static final String INVALID_NODE =
        "Unable to write node of type ";

    // DOMGroup/TreeManager

    public static final String ERR_GC_NULL = "gc should not be null";
    public static final String ERR_DOMTREEMANAGER_NULL =
        "domTreeManager should not be null";
    public static final String ERR_MAXGCOVERRIDES_OUTOFRANGE =
        "maxGcOverrides should be greater than zero";
    public static final String ERR_TOP_LEVEL_GROUP_NULL =
        "topLevelGroup should not be null";
    public static final String ERR_TOP_LEVEL_GROUP_NOT_G =
        "topLevelGroup should be a group <g>";

    // SVGClip/Font/Hint/Stroke descriptor
    public static final String ERR_CLIP_NULL = "clipPathValue should not be null";
    public static final String ERR_FONT_NULL =
        "none of the font description parameters should be null";
    public static final String ERR_HINT_NULL =
        "none of the hints description parameters should be null";
    public static final String ERR_STROKE_NULL =
        "none of the stroke description parameters should be null";

    // context
    public static final String ERR_MAP_NULL = "context map(s) should not be null";
    public static final String ERR_TRANS_NULL =
        "transformer stack should not be null";

    // SVGLookUp/RescaleOp
    public static final String ERR_ILLEGAL_BUFFERED_IMAGE_LOOKUP_OP =
        "BufferedImage LookupOp should have 1, 3 or 4 lookup arrays";
    public static final String ERR_SCALE_FACTORS_AND_OFFSETS_MISMATCH =
        "RescapeOp offsets and scaleFactor array length do not match";
    public static final String ERR_ILLEGAL_BUFFERED_IMAGE_RESCALE_OP =
        "BufferedImage RescaleOp should have 1, 3 or 4 scale factors";


    // SVGGeneratorContext
    public static final String ERR_DOM_FACTORY_NULL =
        "domFactory should not be null";
    public static final String ERR_IMAGE_HANDLER_NULL =
        "imageHandler should not be null";
    public static final String ERR_EXTENSION_HANDLER_NULL =
        "extensionHandler should not be null";
    public static final String ERR_ID_GENERATOR_NULL =
        "idGenerator should not be null";
    public static final String ERR_STYLE_HANDLER_NULL =
        "styleHandler should not be null";
    public static final String ERR_ERROR_HANDLER_NULL =
        "errorHandler should not be null";
}
