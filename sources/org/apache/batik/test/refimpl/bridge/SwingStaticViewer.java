/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.refimpl.bridge;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;

/**
 * Viewer for Static SVG Content. This viewer uses the StaticRenderer
 * of the reference implementation and the Swing API for all its GUI
 * components.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
*/
public class SwingStaticViewer extends JComponent{
    /**
     * Cursor used by default
     */
    public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();

    /**
     * Cursor used to indicate to the user that processing is going
     * on.
     */
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

    /**
     * Static renderer this component uses to paint into its offscreen
     * buffer.
     */
    private StaticRenderer staticRenderer;

    /**
     * Offscreen buffer, matches the component's size.
     */
    private BufferedImage offScreen;

    /**
     * The user space to device space transform
     */
    private AffineTransform usr2dev = new AffineTransform();

    /**
     * Root of the GVT tree displayed by this viewer
     */
    private GraphicsNode treeRoot;

    /**
     * x-axis offset for the viewer's origin
     */
    private int tx = 0;

    /**
     * y-axis offset for the viewer's origin
     */
    private int ty = 0;

    /**
     * Area made dirty by the marker
     */
    private Rectangle dirtyArea;

    /**
     * Used to track whether the component needs to be repainted
     * or not.
     */
    private boolean needRepaint = true;

    /**
     * Used to mark a specific area in the viewer
     */
    private Shape marker = null;

    /**
     * Used to draw marker
     */
    private BasicStroke markerStroke
        = new BasicStroke(1, BasicStroke.CAP_SQUARE,
                          BasicStroke.JOIN_MITER,
                          10,
                          new float[]{4, 4}, 0);

    /**
     * @param treeRoot the root of the GVT tree this viewer should
     *        display. May be null.
     */
    public SwingStaticViewer(GraphicsNode treeRoot){
        this.treeRoot = treeRoot;
    }

    /**
     * @param treeRoot the root of the GVT tree this viewer should
     * display
     */
    public void setTreeRoot(GraphicsNode treeRoot){
        if((treeRoot != null)
           &&
           !(treeRoot.equals(this.treeRoot))){
            this.treeRoot = treeRoot;
            needRepaint = true;
        }
    }

    /**
     * Simply paints the content of the offscreen buffer.
     */
    public void paint(Graphics _g){
        Graphics2D g = (Graphics2D)_g;

        updateOffScreen();

        // If any rendering happened, the offscreen buffer has been
        // created and can be painted.
        if(offScreen != null){
            g.translate(tx, ty);
            g.drawImage(offScreen, 1, 1, null);
            g.translate(-tx, -ty);
        }

        if(marker != null){
            Stroke defaultStroke = g.getStroke();
            g.setStroke(markerStroke);
            g.draw(marker);
            g.setStroke(defaultStroke);
        }

        paintBorder(g);
    }

    /**
     * If the marker is not null, it will be drawn by the
     * viewer.
     */
    public void setMarker(Shape marker){
        this.marker = marker;
        Rectangle newDirtyArea = dirtyArea;
        if(marker != null){
            newDirtyArea = markerStroke.createStrokedShape(marker).getBounds();
            if(dirtyArea == null){
                dirtyArea = newDirtyArea;
            }
        }

        if(newDirtyArea != null){
            Rectangle refreshArea = new Rectangle(newDirtyArea);
            refreshArea.add(dirtyArea);
            paintImmediately(refreshArea.x, refreshArea.y,
                             refreshArea.width, refreshArea.height);
            dirtyArea = newDirtyArea;
        }
    }

    /**
     * Updates the offScreen buffer if it is null of if the size has
     * changed.
     */
    private void updateOffScreen(){
        Dimension size = getSize();
        if(size.width <= 0 && size.height <= 0)
            return;

        //
        // Make sure the offScreen buffer has the right size
        //
        if(offScreen == null){
            // Create a new offScreen image. It will have to be painted.
            offScreen = new BufferedImage(size.width, size.height,
                                          BufferedImage.TYPE_INT_ARGB_PRE);
            needRepaint = true;
        }
        else{
            Dimension oldSize = new Dimension(offScreen.getWidth(), offScreen.getHeight());
            Dimension newSize = getSize();

            if((newSize.width <= oldSize.width)
               &&
               (newSize.height <= oldSize.height)){
                // We can reuse a sub image of the previous offScreen. If nothing
                // else changed, it may be reused as-is.
                offScreen = offScreen.getSubimage(0, 0, newSize.width, newSize.height);
            }
            else{
                offScreen = new BufferedImage(size.width, size.height,
                                              BufferedImage.TYPE_INT_ARGB_PRE);
                needRepaint = true;
            }
        }

        //
        // Build a static renderer if needed
        //
        if((staticRenderer == null) && (treeRoot != null)){
            staticRenderer = new StaticRenderer(offScreen);
            staticRenderer.setTree(treeRoot);
            staticRenderer.setTransform(usr2dev);
        }

        //
        // Check if the transform has changed, which would require a
        // repaint.
        //
        if((staticRenderer != null)
           &&
           !staticRenderer.getTransform().equals(usr2dev)){
            needRepaint = true;
        }

        //
        // Only repaint if required.
        //
        if(needRepaint){
            clearOffScreen();
            if(staticRenderer != null){
                needRepaint = false;
                staticRenderer.setOffScreen(offScreen);
                staticRenderer.setTree(treeRoot);
                staticRenderer.setTransform(usr2dev);
                setCursor(WAIT_CURSOR);
                try {
                    staticRenderer.repaint(getAreaOfInterest());
                } catch (InterruptedException ie) {
                }
                setCursor(DEFAULT_CURSOR);
            }
        }
    }

    /**
     * Sets the offscreen to full white
     */
    private void clearOffScreen(){
        if(offScreen != null){
            Graphics2D g = offScreen.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.setPaint(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, offScreen.getWidth(), offScreen.getHeight());
        }
    }

    /**
     * @return the area of interest displayed in the viewer, in usr space.
     */
    public Shape getAreaOfInterest(){
        AffineTransform dev2usr = null;
        try {
            dev2usr = usr2dev.createInverse();
        } catch(NoninvertibleTransformException e){
            // This should not happen. See setTransform
            throw new Error();
        }
        Dimension size = getSize();
        Rectangle devAOI = new Rectangle(0, 0, size.width, size.height);
        return dev2usr.createTransformedShape(devAOI);
    }

    /**
     * Sets the user space to device space transform for this component.
     */
    public void setTransform(AffineTransform usr2dev)
            throws NoninvertibleTransformException {
        if(usr2dev == null) {
            usr2dev = new AffineTransform();
        } else {
            usr2dev = new AffineTransform(usr2dev);
        }
        if (usr2dev.getDeterminant() == 0) {
            throw new NoninvertibleTransformException("");
        }
        this.usr2dev = usr2dev;
    }

    /**
     * Returns the user space to device space transform
     */
    public AffineTransform getTransform(){
        return usr2dev;
    }

    /**
     * Translates the origin of the viewer
     */
    public void translate(float dx, float dy){
        tx += dx;
        ty += dy;
        repaint();
    }

    /**
     * Coerces the tx/ty into a transform that is pre concatenated
     * with the usr2dev transform
     */
    public void coerceTranslate(){
        AffineTransform t
            = AffineTransform.getTranslateInstance(tx, ty);

        if(usr2dev == null){
            usr2dev = t;
        }
        else{
            usr2dev.preConcatenate(t);
        }

        tx = 0;
        ty = 0;
    }


}
