/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Path object into an SVG clip
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGClip extends AbstractSVGConverter{
    /**
     * Descriptor to use where there is no clip on an element
     */
    public static final SVGClipDescriptor NO_CLIP = new SVGClipDescriptor(VALUE_NONE, null);

    /**
     * Used to convert clip object to SVG elements
     */
    private SVGShape shapeConverter;

    /**
     * @param domFactory used to build Elements
     */
    public SVGClip(Document domFactory){
        super(domFactory);
        this.shapeConverter = new SVGShape(domFactory);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions.
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.util.awt.svg.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        Shape userClip = gc.getClip();
        return toSVG(userClip);
    }

    /**
     * @param clip the path that should be converted to a clip object.
     * @return value of the clip-path property. If required, a
     *         new clipPath element definition may have been
     *         added to clipDefsMap.
     */
    public SVGClipDescriptor toSVG(Shape clip){
        SVGClipDescriptor clipDesc = null;

        if(clip != null){
            StringBuffer clipPathAttrBuf = new StringBuffer(URL_PREFIX);

            // First, convert to a GeneralPath so that the
            GeneralPath clipPath = new GeneralPath(clip);

            // Check if this object is already in the Map
            ClipKey clipKey = new ClipKey(clipPath);
            clipDesc = (SVGClipDescriptor)descMap.get(clipKey);

            if(clipDesc == null){
                Element clipDef = clipToSVG(clip);
                clipPathAttrBuf.append(SIGN_POUND);
                clipPathAttrBuf.append(clipDef.getAttribute(ATTR_ID));
                clipPathAttrBuf.append(URL_SUFFIX);

                clipDesc = new SVGClipDescriptor(clipPathAttrBuf.toString(),
                                                 clipDef);

                descMap.put(clipKey, clipDesc);
                defSet.add(clipDef);
            }
        }
        else
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
    private Element clipToSVG(Shape clip){
        Element clipDef = domFactory.createElement(TAG_CLIP_PATH);
        clipDef.setAttribute(ATTR_CLIP_PATH_UNITS, VALUE_USER_SPACE_ON_USE);
        clipDef.setAttribute(ATTR_ID, SVGIDGenerator.generateID(ID_PREFIX_CLIP_PATH));

        Element clipPath = shapeConverter.toSVG(clip);
        clipDef.appendChild(clipPath);
        return clipDef;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Polygon polygon = new Polygon();
        polygon.addPoint(1, 1);
        polygon.addPoint(2, 1);
        polygon.addPoint(3, 2);
        polygon.addPoint(3, 3);
        polygon.addPoint(2, 4);
        polygon.addPoint(1, 3);
        polygon.addPoint(1, 2);

        GeneralPath square = new GeneralPath();
        square.moveTo(0, 0);
        square.lineTo(1, 0);
        square.lineTo(1, 1);
        square.lineTo(0, 1);
        square.closePath();

        Ellipse2D hole = new Ellipse2D.Double(0, 0, 1, 1);
        Area area = new Area(square);
        area.subtract(new Area(hole));

        ClipKey key1 = new ClipKey(new GeneralPath(polygon));
        ClipKey key2 = new ClipKey(new GeneralPath(polygon));

        System.out.println("key1.equals(key2) = " + key1.equals(key2));

        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();

        System.out.println("hash1 = " + hash1);
        System.out.println("hash2 = " + hash2);

        Shape clips[] = {
            // polygon
            polygon,

            // rect
            new Rectangle(10, 20, 30, 40),
            new Rectangle2D.Double(100., 200., 300., 400.),
            new Rectangle2D.Float(1000f, 2000f, 3000f, 4000f),
            new RoundRectangle2D.Double(15., 16., 17., 18., 30., 20.),
            new RoundRectangle2D.Float(35f, 45f, 55f, 65f, 25f, 45f),

            // Circle
            new Ellipse2D.Float(0, 0, 100, 100),
            new Ellipse2D.Double(40, 40, 240, 240),

            // Ellipse
            new Ellipse2D.Float(0, 0, 100, 200),
            new Ellipse2D.Float(40, 100, 240, 200),

            // line
            new Line2D.Double(1, 2, 3, 4),
            new Line2D.Double(10, 20, 30, 40),

            // path
            new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
            new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85),
            new Arc2D.Double(0, 0, 100, 100, 0, 90, Arc2D.OPEN),
            square,
            area
        };

        Document domFactory = TestUtil.getDocumentPrototype();
        SVGClip converter = new SVGClip(domFactory);

        Element topLevelGroup = domFactory.createElement(TAG_G);
        Element defs = domFactory.createElement(TAG_DEFS);

        Element groupOne = domFactory.createElement(TAG_G);
        for(int i=0; i<clips.length; i++){
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_ID, clips[i].getClass().getName());
            rect.setAttribute(ATTR_CLIP_PATH, (String)converter.toSVG(clips[i]).getAttributeMap(null).get(ATTR_CLIP_PATH));
            groupOne.appendChild(rect);
        }

        // Elements in groupTwo should have the same clip reference as
        // corresponding elements in groupOne, as the clip definition
        // has already be done and put in clipDefsMap.
        Element groupTwo = domFactory.createElement(TAG_G);
        for(int i=0; i<clips.length; i++){
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_ID, clips[i].getClass().getName());
            rect.setAttribute(ATTR_CLIP_PATH, (String)converter.toSVG(clips[i]).getAttributeMap(null).get(ATTR_CLIP_PATH));
            groupTwo.appendChild(rect);
        }

        Iterator defValuesIter = converter.getDefinitionSet().iterator();
        while(defValuesIter.hasNext()){
            Element clipPathElement = (Element)defValuesIter.next();
            defs.appendChild(clipPathElement);
        }

        topLevelGroup.appendChild(defs);
        topLevelGroup.appendChild(groupOne);
        topLevelGroup.appendChild(groupTwo);

        TestUtil.trace(topLevelGroup, System.out);
    }

}

/**
 * Inner class used to key clip definitions in a Map.
 * This is needed because we need to test equality
 * on the value of GeneralPath and GeneralPath's equal
 * method does not implement that behavior.
 */
class ClipKey{
/**
* This clip hash code. Based on the serialized path
* data
*/
int hashCodeValue = 0;

/**
* @param proxiedPath path used as an index in the Map
*/
public ClipKey(GeneralPath proxiedPath){
String pathData = SVGPath.toSVGPathData(proxiedPath);
hashCodeValue = pathData.hashCode();
}

/**
* @return this object's hashcode
*/
 public int hashCode(){
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
