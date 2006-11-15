/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;

/**
 * Support for the 'preserveAspectRatio' interface on the SVG element.
 * @author <a href="mailto:tonny@kiyut.com">Tonny Kohar</a>
 * @version $Id$
 */
public class SVGPreserveAspectRatioSupport {

    /**
     * To implement getPreserveAspectRatio.
     * Returns the value of the 'preserveAspectRatio' attribute of the
     * given element.
     */
    public static SVGAnimatedPreserveAspectRatio 
        getPreserveAspectRatio(AbstractElement elt) {
        SVGOMAnimatedPreserveAspectRatio ret;
        ret = (SVGOMAnimatedPreserveAspectRatio)elt.getLiveAttributeValue
            (null, SVGConstants.SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE);

        if (ret == null) {
            SVGOMDocument doc = (SVGOMDocument) elt.getOwnerDocument();
            ret = new SVGOMAnimatedPreserveAspectRatio(elt);
            ret.addAnimatedAttributeListener
                (doc.getAnimatedAttributeListener());
            elt.putLiveAttributeValue
                (null, SVGConstants.SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE, ret);
        }
        return ret;
    }
}
