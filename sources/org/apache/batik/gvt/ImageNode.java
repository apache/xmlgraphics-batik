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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * A graphics node that represents an image described as a graphics node.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ImageNode extends CompositeGraphicsNode {

    /**
     * Constructs a new empty <tt>ImageNode</tt>.
     */
    public ImageNode() {}

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (isVisible) {
            super.paint(g2d);
        }
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case VISIBLE_FILL:
        case VISIBLE_STROKE:
        case VISIBLE:
            return isVisible && super.contains(p);
        case PAINTED:
        case FILL:
        case STROKE:
        case ALL:
            return super.contains(p);
        case NONE:
            return false;
        default:
            return false;
        }
    }

    /**
     * Returns the GraphicsNode containing point p if this node or one of its
     * children is sensitive to mouse events at p.
     *
     * @param p the specified Point2D in the user space
     */
    public GraphicsNode nodeHitAt(Point2D p) {
        // Used to return super.nodeHitAt(p);
        return (contains(p) ? this : null);
    }

    //
    // Properties methods
    //

    /**
     * Sets the graphics node that represents the image.
     *
     * @param newImage the new graphics node that represents the image
     */
    public void setImage(GraphicsNode newImage) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        if (count == 0) ensureCapacity(1);
        children[0] = newImage;
        ((AbstractGraphicsNode)newImage).setParent(this);
        ((AbstractGraphicsNode)newImage).setRoot(getRoot());
        count=1;
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the graphics node that represents the image.
     */
    public GraphicsNode getImage() {
        if (count > 0) {
            return children[0];
        } else {
            return null;
        }
    }
}
