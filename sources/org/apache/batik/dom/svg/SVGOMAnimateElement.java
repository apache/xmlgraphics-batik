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

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimateElement;

import java.util.Map;
import java.util.HashMap;

/**
 * This class implements {@link SVGAnimateElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimateElement
    extends    SVGOMAnimationElement
    implements SVGAnimateElement {

    /**
     * this map supports a fast lookup from svg-attribute-name string to
     * svgType-integer. It is faster than doing string-equals in a
     * lengthy if-else-statement.
     * This map is used only by {@link #getAttributeType }
     */
    private static final Map typeMap = new HashMap();


    static {

        Map map = typeMap;

        Integer svgType = new Integer( SVGTypes.TYPE_IDENT );
        map.put(  SVG_ACCUMULATE_ATTRIBUTE, svgType );
        map.put(  SVG_ADDITIVE_ATTRIBUTE, svgType );
        map.put(  SVG_ATTRIBUTE_TYPE_ATTRIBUTE, svgType );
        map.put(  SVG_CALC_MODE_ATTRIBUTE, svgType );
        map.put(  SVG_FILL_ATTRIBUTE, svgType );
        map.put(  SVG_RESTART_ATTRIBUTE, svgType );

        svgType = new Integer( SVGTypes.TYPE_CDATA );
        map.put(  SVG_ATTRIBUTE_NAME_ATTRIBUTE, svgType );
        map.put(  SVG_BY_ATTRIBUTE, svgType );
        map.put(  SVG_FROM_ATTRIBUTE, svgType );
        map.put(  SVG_MAX_ATTRIBUTE, svgType );
        map.put(  SVG_MIN_ATTRIBUTE, svgType );
        map.put(  SVG_TO_ATTRIBUTE, svgType );
        map.put(  SVG_VALUES_ATTRIBUTE, svgType );

        svgType = new Integer( SVGTypes.TYPE_TIMING_SPECIFIER_LIST );
        map.put(  SVG_BEGIN_ATTRIBUTE, svgType );
        map.put(  SVG_END_ATTRIBUTE, svgType );

        svgType = new Integer( SVGTypes.TYPE_TIME );
        map.put(  SVG_DUR_ATTRIBUTE, svgType );
        map.put(  SVG_REPEAT_DUR_ATTRIBUTE, svgType );

        svgType = new Integer( SVGTypes.TYPE_NUMBER_LIST );
        map.put(  SVG_KEY_SPLINES_ATTRIBUTE, svgType );
        map.put(  SVG_KEY_TIMES_ATTRIBUTE, svgType );

        svgType = new Integer( SVGTypes.TYPE_INTEGER );
        map.put(  SVG_REPEAT_COUNT_ATTRIBUTE, svgType );

    }



    /**
     * Creates a new SVGOMAnimateElement object.
     */
    protected SVGOMAnimateElement() {
    }

    /**
     * Creates a new SVGOMAnimateElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMAnimateElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_ANIMATE_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMAnimateElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {

        if (ns == null) {
            Integer typeCode = (Integer)typeMap.get( ln );
            if ( typeCode != null ){
                // it is one of 'my' mappings..
                return typeCode.intValue();
            }
        }
        return super.getAttributeType(ns, ln);
    }
}
