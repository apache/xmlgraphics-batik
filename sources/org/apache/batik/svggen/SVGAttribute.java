/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an SVG attribute and provides convenience
 * methods to determine whether or not the attribute applies
 * to a given element type.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGAttribute {
    /**
     * SVG syntax for the attribute
     */
    private String name;

    /**
     * Set of Element tags to which the attribute does or
     * does not apply.
     */
    private Set applicabilitySet;

    /**
     * Controls the semantic of applicabilitySet. If
     * true, then the applicabilitySet contains the elments
     * to which the attribute applies. If false, the
     * Set contains the elements to which the attribute
     * does not apply.
     */
    private boolean isSetInclusive;

    /**
     * @param applicabilitySet Set of Element tags (Strings) to which
     *        the attribute applies
     * @param isSetInclusive defines whether elements in applicabilitySet
     *        define the list of elements to which the attribute
     *        applies or to which it does not apply
     */
    public SVGAttribute(Set applicabilitySet, boolean isSetInclusive){
        if(applicabilitySet == null)
            applicabilitySet = new HashSet();

        this.applicabilitySet = applicabilitySet;
        this.isSetInclusive = isSetInclusive;
    }

    /**
     * @param tag the tag of the Element to which the attribute
     *        could apply.
     * @return true if the attribute applies to the given Element
     */
    public boolean appliesTo(String tag){
        boolean tagInMap = applicabilitySet.contains(tag);
        if(isSetInclusive)
            return tagInMap;
        else
            return !tagInMap;
    }
}
