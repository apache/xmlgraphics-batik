/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
    public static final String ERR_READ =
        "could not read image File ";
    public static final String ERR_IMAGE_HANDLER_NOT_SUPPORTED = 
        "imageHandler does not implement CachedImageHandler: ";

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
