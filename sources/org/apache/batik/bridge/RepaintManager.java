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

package org.apache.batik.bridge;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.ext.awt.geom.RectListManager;

/**
 * This class manages the rendering of a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class RepaintManager {
    final static int COPY_OVERHEAD      = 1000;
    final static int COPY_LINE_OVERHEAD = 10;

    /**
     * The renderer used to repaint the buffer.
     */
    protected ImageRenderer renderer;

    /**
     * Creates a new repaint manager.
     */
    public RepaintManager(ImageRenderer r) {
        renderer = r;
    }
    
    /**
     * Updates the rendering buffer.
     * @param aoi The area of interest in the renderer space units.
     * @return the list of the rectangles to repaint.
     */
    public Collection updateRendering(Collection areas) 
        throws InterruptedException {
        renderer.flush(areas);
        List rects = new ArrayList(areas.size());
        AffineTransform at = renderer.getTransform();

        Iterator i = areas.iterator();
        while (i.hasNext()) {
            Shape s = (Shape)i.next();
            s = at.createTransformedShape(s);
            Rectangle2D r2d = s.getBounds2D();
            int x0 = (int)Math.floor(r2d.getX());
            int y0 = (int)Math.floor(r2d.getY());
            int x1 = (int)Math.ceil(r2d.getX()+r2d.getWidth());
            int y1 = (int)Math.ceil(r2d.getY()+r2d.getHeight());
            // This rectangle must be outset one pixel to ensure
            // it includes the effects of anti-aliasing on object.s
            Rectangle r = new Rectangle(x0-1, y0-1, x1-x0+3, y1-y0+3);
                
            rects.add(r);
        }
        RectListManager devRLM =null;
        try {
             devRLM = new RectListManager(rects);
             devRLM.mergeRects(COPY_OVERHEAD, COPY_LINE_OVERHEAD);
        } catch(Exception e) {
            e.printStackTrace();
        }

        renderer.repaint(devRLM);
        return devRLM;
    }

    /**
     * Sets up the renderer so that it is ready to render for the new
     * 'context' defined by the user to device transform, double buffering
     * state, area of interest and width/height.
     * @param u2d The user to device transform.
     * @param dbr Whether the double buffering should be used.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     */
    public void setupRenderer(AffineTransform u2d,
                              boolean dbr,
                              Shape aoi,
                              int width,
                              int height) {
        renderer.setTransform(u2d);
        renderer.setDoubleBuffered(dbr);
        renderer.updateOffScreen(width, height);
        renderer.clearOffScreen();
    }

    /**
     * Returns the renderer's offscreen, i.e., the current state as rendered
     * by the associated renderer.
     */
    public BufferedImage getOffScreen(){
        return renderer.getOffScreen();
    }
}
