/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.batik.ext.awt.image.renderable.Filter;
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
    Map nodeBounds = new HashMap();

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
            
            Rectangle2D srcORgn = (Rectangle2D)nodeBounds.get(gnWRef);

            Rectangle2D srcNRgn = gn.getBounds();
            AffineTransform nat = gn.getTransform();

            if (nat != null){
                nat = (nat == null) ? null : new AffineTransform(nat);
            }

            nodeBounds.put(gnWRef, srcNRgn); // remember the new bounds...
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

                gnWRef = gn.getWeakReference();

                if (dirtyNodes.containsKey(gnWRef))
                    break; // We already have the parent in the list of
                           // dirty nodes. so let it handle this...

                if (nodeBounds.containsKey(gnWRef)) {
                    // Update the bounds in the nodeBounds array
                    nodeBounds.put(gnWRef, gn.getBounds());
                }

                AffineTransform at = gn.getTransform();

                if (oat != null){
                    // oRgn = oat.createTransformedShape(srcORgn);
                    if (at != null){
                        oat.preConcatenate(at);
                    }
                } else {
                    oat = (at == null) ? null : new AffineTransform(at);
                }
                if (nat != null){
                    //  nRgn = nat.createTransformedShape(srcNRgn);
                    if (at != null){
                        nat.preConcatenate(at);
                    }
                } else {
                    nat = (at == null) ? null : new AffineTransform(at);
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
            dirtyNodes.put(gnWRef, at);
        }


        while (!nodeBounds.containsKey(gnWRef)) {
            nodeBounds.put(gnWRef, gn.getBounds());
            gn = gn.getParent();
            if (gn == null) break;
            gnWRef = gn.getWeakReference();
        }
    }

    /**
     * Clears the tracker.
     */
    public void clear() {
        dirtyNodes = null;
    }

    public static class DirtyInfo {
        // Always references a GraphicsNode.
        WeakReference   gn;

        // The transform from gn to parent at time of construction.
        AffineTransform gn2parent;

        public DirtyInfo(GraphicsNode gn, AffineTransform at) {
            this.gn     = gn.getWeakReference();
            this.gn2parent = at;
        }

        public GraphicsNode getGraphicsNode() {
            return (GraphicsNode)gn.get();
        }

        public AffineTransform getGn2Parent() {
            return gn2parent;
        }
    }
}
