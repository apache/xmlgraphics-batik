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
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UpdateTracker extends GraphicsNodeChangeAdapter {

    Map dirtyNodes = null;

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
        // System.out.println("Getting dirty areas");

        if (dirtyNodes == null) 
        return null;

        List ret = new LinkedList();
        Set keys = dirtyNodes.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            GraphicsNode gn = (GraphicsNode)((WeakReference)i.next()).get();
            DirtyInfo di = (DirtyInfo)dirtyNodes.get(gn.getWeakReference());

            Rectangle2D srcORgn = di.getOriginalRgn().getBounds2D();
            AffineTransform oat = di.getGn2Parent();

            Rectangle2D srcNRgn = gn.getBounds();
            AffineTransform nat = gn.getTransform();

            Shape oRgn = srcORgn;
            Shape nRgn = srcNRgn;

            do {
                Filter f;
                f = gn.getGraphicsNodeRable(false);
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                f = gn.getEnableBackgroundGraphicsNodeRable(false);
                // (need to push rgn through filter chain if any...)
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                gn = gn.getParent();
                if (gn == null){
                    break;
                }

                AffineTransform at = gn.getTransform();

                if (oat != null){
                    oRgn = oat.createTransformedShape(srcORgn);
                    if (at != null){
                        oat.preConcatenate(at);
                    }
                } else {
                    oat = at;
                }
                if (nat != null){
                    nRgn = nat.createTransformedShape(srcNRgn);
                    if (at != null){
                        nat.preConcatenate(at);
                    }
                } else {
                    nat = at;
                }

            } while ((gn != null) && 
                     (dirtyNodes.get(gn.getWeakReference()) == null));

            if (gn == null) {
                // We made it to the root graphics node so add them.
                ret.add(oRgn);
                ret.add(nRgn);
            }
    }

        // System.out.println("Dirty area: " + ret);
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

        if (dirtyNodes == null) {
            dirtyNodes = new HashMap();
            dirtyNodes.put(gnWRef, new DirtyInfo(gn, gn.getBounds()));
            return;
        }

        DirtyInfo di = (DirtyInfo)dirtyNodes.get(gnWRef);
        if (di != null) return;

        dirtyNodes.put(gnWRef, new DirtyInfo(gn, gn.getBounds()));
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

        // The original location affected by this gn in the gn's
        // coordinate system.
        Shape           rgn;

        // The transform from gn to parent at time of construction.
        AffineTransform gn2parent;

        public DirtyInfo(GraphicsNode gn, Shape rgn) {
            this.gn     = gn.getWeakReference();
            this.rgn    = rgn;

            this.gn2parent = gn.getInverseTransform();
        }

        public GraphicsNode getGraphicsNode() {
            return (GraphicsNode)gn.get();
        }

        public Shape getOriginalRgn() {
            return rgn;
        }

        public AffineTransform getGn2Parent() {
            return gn2parent;
        }
    }
}
