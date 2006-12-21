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
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;

/**
 * This class provide support for the SVGTextPositionningElement
 * interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGTextPositioningElementSupport {

//     public static final String X_DEFAULT_VALUE
//         = "";
//     public static final String Y_DEFAULT_VALUE
//         = "";
//     public static final String DX_DEFAULT_VALUE
//         = "";
//     public static final String DY_DEFAULT_VALUE
//         = "";
//     public static final String ROTATE_DEFAULT_VALUE
//         = "";
// 
//     /**
//      * <b>DOM</b>: Implements {@link
//      * org.w3c.dom.svg.SVGTextPositioningElement#getX()}.
//      */
//     public static SVGAnimatedLengthList getX(AbstractElement e){
// 
//         SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
//             e.getLiveAttributeValue(null, SVGConstants.SVG_X_ATTRIBUTE);
//         if (result == null) {
//             SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
//             result = new SVGOMAnimatedLengthList(e, null,
//                                                  SVGConstants.SVG_X_ATTRIBUTE,
//                                                  X_DEFAULT_VALUE, true,
//                                                  AbstractSVGLength.HORIZONTAL_LENGTH);
//             result.addAnimatedAttributeListener
//                 (doc.getAnimatedAttributeListener());
//             e.putLiveAttributeValue(null,
//                                     SVGConstants.SVG_X_ATTRIBUTE, result);
//         }
//         return result;
//     }
// 
//     /**
//      * <b>DOM</b>: Implements {@link
//      * org.w3c.dom.svg.SVGTextPositioningElement#getY()}.
//      */
//     public static SVGAnimatedLengthList getY(AbstractElement e){
// 
//         SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
//             e.getLiveAttributeValue(null, SVGConstants.SVG_Y_ATTRIBUTE);
//         if (result == null) {
//             SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
//             result = new SVGOMAnimatedLengthList(e, null,
//                                                  SVGConstants.SVG_Y_ATTRIBUTE,
//                                                  Y_DEFAULT_VALUE, true,
//                                                  AbstractSVGLength.VERTICAL_LENGTH);
//             result.addAnimatedAttributeListener
//                 (doc.getAnimatedAttributeListener());
//             e.putLiveAttributeValue(null,
//                                     SVGConstants.SVG_Y_ATTRIBUTE, result);
//         }
//         return result;
//     }
// 
//     /**
//      * <b>DOM</b>: Implements {@link
//      * org.w3c.dom.svg.SVGTextPositioningElement#getDx()}.
//      */
//     public static SVGAnimatedLengthList getDx(AbstractElement e){
// 
//         SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
//             e.getLiveAttributeValue(null, SVGConstants.SVG_DX_ATTRIBUTE);
//         if (result == null) {
//             SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
//             result = new SVGOMAnimatedLengthList(e, null,
//                                                  SVGConstants.SVG_DX_ATTRIBUTE,
//                                                  DX_DEFAULT_VALUE, true,
//                                                  AbstractSVGLength.HORIZONTAL_LENGTH);
//             result.addAnimatedAttributeListener
//                 (doc.getAnimatedAttributeListener());
//             e.putLiveAttributeValue(null,
//                                     SVGConstants.SVG_DX_ATTRIBUTE, result);
//         }
//         return result;
//     }
// 
//     /**
//      * <b>DOM</b>: Implements {@link
//      * org.w3c.dom.svg.SVGTextPositioningElement#getDy()}.
//      */
//     public static SVGAnimatedLengthList getDy(AbstractElement e){
// 
//         SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
//             e.getLiveAttributeValue(null, SVGConstants.SVG_DY_ATTRIBUTE);
//         if (result == null) {
//             SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
//             result = new SVGOMAnimatedLengthList(e, null,
//                                                  SVGConstants.SVG_DY_ATTRIBUTE,
//                                                  DY_DEFAULT_VALUE, true,
//                                                  AbstractSVGLength.VERTICAL_LENGTH);
//             result.addAnimatedAttributeListener
//                 (doc.getAnimatedAttributeListener());
//             e.putLiveAttributeValue(null,
//                                     SVGConstants.SVG_DY_ATTRIBUTE, result);
//         }
//         return result;
//     }
// 
//     /**
//      * <b>DOM</b>: Implements {@link
//      * org.w3c.dom.svg.SVGTextPositioningElement#getRotate()}.
//      */
//     public static SVGAnimatedNumberList getRotate(AbstractElement e){
// 
//         SVGOMAnimatedNumberList result =(SVGOMAnimatedNumberList)
//             e.getLiveAttributeValue(null, SVGConstants.SVG_ROTATE_ATTRIBUTE);
//         if (result == null) {
//             SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
//             result = new SVGOMAnimatedNumberList(e, null,
//                                                  SVGConstants.SVG_ROTATE_ATTRIBUTE,
//                                                  ROTATE_DEFAULT_VALUE, true);
//             result.addAnimatedAttributeListener
//                 (doc.getAnimatedAttributeListener());
//             e.putLiveAttributeValue(null,
//                                     SVGConstants.SVG_ROTATE_ATTRIBUTE, result);
//         }
//         return result;
//     }
}
