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

package org.apache.batik.gvt;

import java.awt.geom.Point2D;

/**
 * A Marker describes a GraphicsNode with a reference point that can be used to
 * position the Marker at a particular location and a particular policy for
 * rotating the marker when drawing it.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$ 
 */
public class Marker {

    /**
     * Rotation angle, about (0, 0) is user space. If orient is NaN then the
     * marker's x-axis should be aligned with the slope of the curve on the
     * point where the object is drawn 
     */
    protected double orient;

    /**
     * GraphicsNode this marker is associated to
     */
    protected GraphicsNode markerNode;

    /**
     * Reference point about which the marker should be drawn
     */
    protected Point2D ref;

    /**
     * Constructs a new marker.
     *
     * @param markerNode the graphics node that represents the marker
     * @param ref the reference point
     * @param orient the orientation of the marker
     */
    public Marker(GraphicsNode markerNode, Point2D ref, double orient){

        if (markerNode == null) {
            throw new IllegalArgumentException();
        }

        if (ref == null) {
            throw new IllegalArgumentException();
        }

        this.markerNode = markerNode;
        this.ref = ref;
        this.orient = orient;
    }

    /**
     * Returns the reference point of this marker.
     */
    public Point2D getRef(){
        return (Point2D)ref.clone();
    }

    /**
     * Returns the orientation of this marker.
     */
    public double getOrient(){
        return orient;
    }

    /**
     * Returns the <code>GraphicsNode</code> that draws this marker.
     */
    public GraphicsNode getMarkerNode(){
        return markerNode;
    }
}
