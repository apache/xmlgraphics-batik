/*

   Copyright 2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGPointList;

/**
 * This class provide support for the SVGAnimatedPoints interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGAnimatedPointsSupport {

    /**
     * Default value for the 'points' attribute.
     */
    public static final String POINTS_DEFAULT_VALUE
        = "";

    /**
     * Returns an {@link SVGOMAnimatedPoints} object for the 'points'
     * attribute of the given element.
     */
    public static SVGOMAnimatedPoints
            getSVGOMAnimatedPoints(AbstractElement e) {
        SVGOMAnimatedPoints result = (SVGOMAnimatedPoints)
            e.getLiveAttributeValue(null, SVGConstants.SVG_POINTS_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPoints(e, null,
                                             SVGConstants.SVG_POINTS_ATTRIBUTE,
                                             POINTS_DEFAULT_VALUE);
            SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
            result.addAnimatedAttributeListener
                (doc.getAnimatedAttributeListener());
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_POINTS_ATTRIBUTE, result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getPoints()}.
     */
    public static SVGPointList getPoints(AbstractElement e) {
        return getSVGOMAnimatedPoints(e).getPoints();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getAnimatedPoints()}.
     */
    public static SVGPointList getAnimatedPoints(AbstractElement e) {
        return getSVGOMAnimatedPoints(e).getAnimatedPoints();
    }
}
