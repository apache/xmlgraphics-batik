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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node which provides a placeholder for another graphics node. This
 * node is self defined except that it delegates to the enclosed (proxied)
 * graphics node, its paint routine and bounds computation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$ 
 */
public class ProxyGraphicsNode extends AbstractGraphicsNode {

    /**
     * The graphics node to proxy.
     */
    protected GraphicsNode source;

    /**
     * Constructs a new empty proxy graphics node.
     */
    public ProxyGraphicsNode() {}

    /**
     * Sets the graphics node to proxy to the specified graphics node.
     *
     * @param source the graphics node to proxy
     */
    public void setSource(GraphicsNode source) {
        this.source = source;
    }

    /**
     * Returns the proxied graphics node.
     */
    public GraphicsNode getSource() {
        return source;
    }

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (source != null) {
            source.paint(g2d);
        }
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        if (source == null) 
            return null;

        return source.getBounds();
    }

    /**
     * Returns the bounds of this node's primitivePaint after applying
     * the input transform (if any), concatenated with this node's
     * transform (if any).
     *
     * @param txf the affine transform with which this node's transform should
     *        be concatenated. Should not be null.  */
    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform txf) {
        if (source == null) 
            return null;

        AffineTransform t = txf;
        if (transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }
        return source.getTransformedPrimitiveBounds(t);
    }

    /**
     * Returns the bounds of the area covered by this node, without
     * taking any of its rendering attribute into account. i.e.,
     * exclusive of any clipping, masking, filtering or stroking, for
     * example.
     */
    public Rectangle2D getGeometryBounds() {
        if (source == null) 
            return null;

        return source.getGeometryBounds();
    }

    /**
     * Returns the bounds of the sensitive area covered by this node,
     * This includes the stroked area but does not include the effects
     * of clipping, masking or filtering. The returned value is
     * transformed by the concatenation of the input transform and
     * this node's transform.
     *
     * @param txf the affine transform with which this node's
     * transform should be concatenated. Should not be null.
     */
    public Rectangle2D getTransformedGeometryBounds(AffineTransform txf) {
        if (source == null) 
            return null;

        AffineTransform t = txf;
        if (transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(transform);
        }
        return source.getTransformedGeometryBounds(t);
    }


    /**
     * Returns the bounds of the sensitive area covered by this node,
     * This includes the stroked area but does not include the effects
     * of clipping, masking or filtering.
     */
    public Rectangle2D getSensitiveBounds() {
        if (source == null) 
            return null;

        return source.getSensitiveBounds();
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        if (source == null) 
            return null;

        return source.getOutline();
    }
}
