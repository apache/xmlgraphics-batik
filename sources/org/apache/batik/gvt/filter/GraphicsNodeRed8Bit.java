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

package org.apache.batik.gvt.filter;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.gvt.GraphicsNode;

/**
 * This implementation of RenderableImage will render its input
 * GraphicsNode on demand for tiles.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GraphicsNodeRed8Bit extends AbstractRed {

    /**
     * GraphicsNode this image can render
     */
    private GraphicsNode node;

    private AffineTransform node2dev;

    private RenderingHints  hints;

    private boolean usePrimitivePaint;

    public GraphicsNodeRed8Bit(GraphicsNode node,
                               AffineTransform node2dev,
                               boolean usePrimitivePaint,
                               RenderingHints  hints) {
        super(); // We _must_ call init...

        this.node              = node;
        this.node2dev          = node2dev;
        this.hints             = hints;
        this.usePrimitivePaint = usePrimitivePaint;

        // Calculate my bounds by applying the affine transform to
        // my input data..

        AffineTransform at = node2dev;
        Rectangle2D bounds2D = node.getPrimitiveBounds();
        if (bounds2D == null) bounds2D = new Rectangle2D.Float(0,0,1,1);
        if (!usePrimitivePaint) {
            // When not using Primitive paint we return our bounds in
            // the nodes parent's user space.  This makes sense since
            // this is the space that we will draw our selves into
            // (since paint unlike primitivePaint incorporates the
            // transform from our user space to our parents user
            // space).
            AffineTransform nodeAt = node.getTransform();
            if (nodeAt != null) {
                at = (AffineTransform)at.clone();
                at.concatenate(nodeAt);
            }
        }
        Rectangle   bounds = at.createTransformedShape(bounds2D).getBounds();
        // System.out.println("Bounds: " + bounds);

        ColorModel cm = GraphicsUtil.sRGB_Unpre;

        int defSz = AbstractTiledRed.getDefaultTileSize();

        // Make tile(0,0) fall on the closest intersection of defaultSz.
        int tgX = defSz*(int)Math.floor(bounds.x/defSz);
        int tgY = defSz*(int)Math.floor(bounds.y/defSz);

        int tw  = (bounds.x+bounds.width)-tgX;
        if (tw > defSz) tw = defSz;
        int th  = (bounds.y+bounds.height)-tgY;
        if (th > defSz) th = defSz;
        if ((tw <= 0) || (th <= 0)) {
            tw = 1;
            th = 1;
        }

        // fix my sample model so it makes sense given my size.
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);

        // Finish initializing our base class...
        init((CachableRed)null, bounds, cm, sm, tgX, tgY, null);
    }

    public WritableRaster copyData(WritableRaster wr) {
        genRect(wr);
        return wr;
    }

    public void genRect(WritableRaster wr) {
        // System.out.println("  Rect: " + wr.getBounds());
        BufferedImage offScreen
            = new BufferedImage(cm, 
                                wr.createWritableTranslatedChild(0,0),
                                cm.isAlphaPremultiplied(),
                                null);

        Graphics2D g = GraphicsUtil.createGraphics(offScreen, hints);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, wr.getWidth(), wr.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(-wr.getMinX(), -wr.getMinY());

        // Set transform
        g.transform(node2dev);


        // Invoke primitive paint.
        if (usePrimitivePaint) {
            node.primitivePaint(g);
        }
        else {
            node.paint (g);
        }

        g.dispose();
    }
}




