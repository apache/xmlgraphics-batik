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

import org.apache.batik.util.SVGConstants;

/**
 * Contains the definition of the SVG tags and attribute names.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGSyntax extends SVGConstants{
    /**
     * This is a qualified form for href, using the xlink: namespace prefix
     */
    public static final String ATTR_XLINK_HREF = "xlink:" + SVG_HREF_ATTRIBUTE;

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
    public static final String ID_PREFIX_FONT = "font";
    public static final String ID_PREFIX_GENERIC_DEFS = "genericDefs";
    public static final String ID_PREFIX_IMAGE = "image";
    public static final String ID_PREFIX_IMAGE_DEFS = "imageDefs";
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

    public static final String DATA_PROTOCOL_PNG_PREFIX = "data:image/png;base64,";


}
