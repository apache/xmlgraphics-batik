/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.batik.gvt.event.GraphicsNodeChangeAdapter;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;

/**
 * This class tracks the changes on a GVT tree
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class UpdateTracker extends GraphicsNodeChangeAdapter {

    Map dirtyNodes = null;
    Map fromBounds = new HashMap();
    Map toBounds   = new HashMap();

    public UpdateTracker(){
    }
    
    /**
     * Tells whether the GVT tree has changed.
     */
    public boolean hasChanged() {
        return (dirtyNodes != null);
    }

    /**
     * Returns the list of dirty areas on GVT.
     */
    public List getDirtyAreas() {
        if (dirtyNodes == null) 
            return null;

        List ret = new LinkedList();
        Set keys = dirtyNodes.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            WeakReference gnWRef = (WeakReference)i.next();
            GraphicsNode  gn     = (GraphicsNode)gnWRef.get();

            // if the weak ref has been cleared then this node is no
            // longer part of the GVT tree (and the change should be
            // reflected in some ancestor that should also be in the
            // dirty list).
            if (gn == null) continue;

            AffineTransform oat;
            oat = (AffineTransform)dirtyNodes.get(gnWRef);
            if (oat != null){
                oat = new AffineTransform(oat);
            }
            
            Rectangle2D srcORgn = (Rectangle2D)fromBounds.remove(gnWRef);

            Rectangle2D srcNRgn = gn.getBounds();
            AffineTransform nat = gn.getTransform();

            if (nat != null){
                nat = new AffineTransform(nat);
            }

            // System.out.println("Rgns: " + srcORgn + " - " + srcNRgn);
            // System.out.println("ATs: " + oat + " - " + nat);
            Shape oRgn = srcORgn;
            Shape nRgn = srcNRgn;
            
            do {
                // Filter f;
                // f = gn.getGraphicsNodeRable(false);
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                // f = gn.getEnableBackgroundGraphicsNodeRable(false);
                // (need to push rgn through filter chain if any...)
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                gn = gn.getParent();
                if (gn == null)
                    break; // We reached the top of the tree

                // Get the parent's current Affine
                AffineTransform at = gn.getTransform();
                // Get the parent's Affine last time we rendered.
                gnWRef = gn.getWeakReference();
                AffineTransform poat = (AffineTransform)dirtyNodes.get(gnWRef);
                if (poat == null) poat = at;
                if (poat != null) {
                    if (oat != null)
                        oat.preConcatenate(poat);
                    else 
                        oat = new AffineTransform(poat);
                }

                if (at != null){
                    if (nat != null)
                        nat.preConcatenate(at);
                    else
                        nat = new AffineTransform(at);
                }

            } while (true);

            if (gn == null) {
                // We made it to the root graphics node so add them.
                // System.out.println
                //     ("Adding: " + oat + " - " + nat + "\n" +
                //      org.ImageDisplay.stringShape(oRgn) + "\n" +
                //      org.ImageDisplay.stringShape(nRgn) + "\n");
                // <!>
                if (oat != null){
                    oRgn = oat.createTransformedShape(srcORgn);
                }
                if (nat != null){
                    nRgn = nat.createTransformedShape(srcNRgn);
                }

                if (oRgn != null) {
                    ret.add(oRgn);
                }

                if (nRgn != null) {
                    ret.add(nRgn);
                }
            }
        }
        return ret;
    }

    /**
     * This returns the dirty region for gn in the coordinate system
     * given by <code>at</at>.
     * @param gn Node tree to return dirty region for.
     * @param at Affine transform to coordinate space to accumulate
     *           dirty regions in.
     */
    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn, 
                                          AffineTransform at) {
        WeakReference gnWRef = gn.getWeakReference();
        AffineTransform nat = (AffineTransform)dirtyNodes.get(gnWRef);
        if (nat == null) nat = gn.getTransform();
        if (nat != null) {
            at = new AffineTransform(at);
            at.concatenate(nat);
        }

        Rectangle2D ret = null;
        if (gn instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            Iterator iter = cgn.iterator();

            while (iter.hasNext()) {
                GraphicsNode childGN = (GraphicsNode)iter.next();
                Rectangle2D r2d = getNodeDirtyRegion(childGN, at);
                if (r2d != null) {
                    if (ret == null) ret = r2d;
                    else ret = ret.createUnion(r2d);
                }
            }
        } else {
            ret = (Rectangle2D)fromBounds.remove(gnWRef);
            if (ret == null) 
                ret = gn.getBounds();
            if (ret != null)
                ret = at.createTransformedShape(ret).getBounds2D();
        }
        return ret;
    }

    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn) {
        return getNodeDirtyRegion(gn, new AffineTransform());
    }

    /**
     * Recieves notification of a change to a GraphicsNode.
     * @param gn The graphics node that is changing.
     */
    public void changeStarted(GraphicsNodeChangeEvent gnce) {
        // System.out.println("A node has changed for: " + this);
        GraphicsNode gn = gnce.getGraphicsNode();
        WeakReference gnWRef = gn.getWeakReference();

        boolean doPut = false;
        if (dirtyNodes == null) {
            dirtyNodes = new HashMap();
            doPut = true;
        } else if (!dirtyNodes.containsKey(gnWRef)) 
            doPut = true;

        if (doPut) {
            AffineTransform at = gn.getTransform();
            if (at != null) at = (AffineTransform)at.clone();
            else            at = new AffineTransform();
            dirtyNodes.put(gnWRef, at);
        }

        GraphicsNode chngSrc = gnce.getChangeSrc();
        Rectangle2D rgn;
        if (chngSrc != null) {
            // A child node is moving in the tree so assign it's dirty
            // regions to this node before it moves.
            rgn = getNodeDirtyRegion(chngSrc);
        } else {
            // Otherwise just use gn's dirty region.
            rgn = gn.getBounds();
        }
        // Add this dirty region to any existing dirty region.
        Rectangle2D r2d = (Rectangle2D)fromBounds.remove(gnWRef);
        if (rgn != null) {
            if (r2d != null) r2d = r2d.createUnion(rgn);
            else             r2d = rgn;
        }
        // Store the bounds for the future.
        fromBounds.put(gnWRef, r2d);
    }

    /**
     * Clears the tracker.
     */
    public void clear() {
        dirtyNodes = null;
    }
}
