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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Path object into an SVG clip
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGClip extends AbstractSVGConverter {
    /**
     * Constant used for some degenerate cases
     */
    public static final Shape ORIGIN = new Line2D.Float(0,0,0,0);

    /**
     * Descriptor to use where there is no clip on an element
     */
    public static final SVGClipDescriptor NO_CLIP =
        new SVGClipDescriptor(SVG_NONE_VALUE, null);

    /**
     * Used to convert clip object to SVG elements
     */
    private SVGShape shapeConverter;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGClip(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.shapeConverter = new SVGShape(generatorContext);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions.
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        Shape clip = gc.getClip();

        SVGClipDescriptor clipDesc = null;

        if (clip != null) {
            StringBuffer clipPathAttrBuf = new StringBuffer(URL_PREFIX);

            // First, convert to a GeneralPath so that the
            GeneralPath clipPath = new GeneralPath(clip);

            // Check if this object is already in the Map
            ClipKey clipKey = new ClipKey(clipPath, generatorContext);
            clipDesc = (SVGClipDescriptor)descMap.get(clipKey);

            if (clipDesc == null) {
                Element clipDef = clipToSVG(clip);
                if (clipDef == null)
                    clipDesc = NO_CLIP;
                else {
                    clipPathAttrBuf.append(SIGN_POUND);
                    clipPathAttrBuf.append(clipDef.getAttributeNS(null, ATTR_ID));
                    clipPathAttrBuf.append(URL_SUFFIX);

                    clipDesc = new SVGClipDescriptor(clipPathAttrBuf.toString(),
                                                     clipDef);

                    descMap.put(clipKey, clipDesc);
                    defSet.add(clipDef);
                }
            }
        } else
            clipDesc = NO_CLIP;

        return clipDesc;
    }

    /**
     * In the following method, an clipping Shape is converted to
     * an SVG clipPath.
     *
     * @param clip path to convert to an SVG clipPath
     *        element
     */
    private Element clipToSVG(Shape clip) {
        Element clipDef =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_CLIP_PATH_TAG);
        clipDef.setAttributeNS(null, SVG_CLIP_PATH_UNITS_ATTRIBUTE,
                               SVG_USER_SPACE_ON_USE_VALUE);

        clipDef.setAttributeNS(null, ATTR_ID,
                               generatorContext.
                               idGenerator.generateID(ID_PREFIX_CLIP_PATH));

        Element clipPath = shapeConverter.toSVG(clip);
        // unfortunately it may be null because of SVGPath that may produce null
        // SVG elements.
        if (clipPath != null) {
            clipDef.appendChild(clipPath);
            return clipDef;
        } else {
            // Here, we know clip is not null but we got a
            // null clipDef. This means we ran into a degenerate 
            // case which in Java 2D means everything is clippped.
            // To provide an equivalent behavior, we clip to a point
            clipDef.appendChild(shapeConverter.toSVG(ORIGIN));
            return clipDef;
        }
    }
}

/**
 * Inner class used to key clip definitions in a Map.
 * This is needed because we need to test equality
 * on the value of GeneralPath and GeneralPath's equal
 * method does not implement that behavior.
 */
class ClipKey {
    /**
     * This clip hash code. Based on the serialized path
     * data
     */
    int hashCodeValue = 0;

    /**
     * @param proxiedPath path used as an index in the Map
     */
    public ClipKey(GeneralPath proxiedPath, SVGGeneratorContext gc){
        String pathData = SVGPath.toSVGPathData(proxiedPath, gc);
        hashCodeValue = pathData.hashCode();
    }

    /**
     * @return this object's hashcode
     */
    public int hashCode() {
        return hashCodeValue;
    }

    /**
     * @param object to compare
     * @return true if equal, false otherwise
     */
    public boolean equals(Object clipKey){
        boolean isEqual = false;
        if((clipKey != null) &&clipKey instanceof ClipKey)
            isEqual = (hashCodeValue == ((ClipKey)clipKey).hashCodeValue);

        return isEqual;
    }
}
