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
