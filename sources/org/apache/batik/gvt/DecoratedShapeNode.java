/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;

import java.util.List;
import java.util.Vector;

/**
 * A <code>DecoratedShapeNode</code> can draw <code>Markers</code>
 * at the start, end or along the path of a <code>Shape</code>.
 * <code>DecoratedShapeNode</code> extends <code>ShapeNode</code>
 * because it has all the same properties as a <code>ShapeNode</code>,
 * the markers being additions to the rendering behavior.
 * <br />
 * <code>DecoratedShapeNode</code> accepts three types of 
 * <code>Markers</code>: start, end and middle. The start 
 * <code>Marker</code>, if any, is drawn on the first vertice
 * of the decorated shape. The end <code>Marker</code>, if any, is
 * drawn on the last vertice of the decorated shape. The middle
 * <code>Marker</code>, if any, is drawn on all vertices, except the
 * first and last ones.
 *
 * @author <a mailto:"vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a mailto:"tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class DecoratedShapeNode extends ShapeNode {
    /**
     * Start Marker
     */
    private Marker startMarker;
    
    /**
     * Start Marker Proxy
     */
    private ProxyGraphicsNode startMarkerProxy;

    /**
     * Middle Marker
     */
    private Marker middleMarker;

    /**
     * Middle Marker Proxy
     */
    private ProxyGraphicsNode middleMarkerProxies[];

    /**
     * End Marker
     */
    private Marker endMarker;

    /**
     * End Marker Proxy
     */
    private ProxyGraphicsNode endMarkerProxy;

    /**
     * Internal Cache: Primitive bounds
     */
    private Rectangle2D dPrimitiveBounds;

    /**
     * Internal Cache: Geometry bounds
     */
    private Rectangle2D dGeometryBounds;

    /**
     * Contains the various marker proxies
     */
    private CompositeGraphicsNode markerGroup = new CompositeGraphicsNode();

    public void setStartMarker(Marker startMarker){
        this.startMarker = startMarker;
        this.startMarkerProxy = null;
        invalidateGeometryCache();
        markerGroup = buildMarkerGroup();
    }

    public void setMiddleMarker(Marker middleMarker){
        this.middleMarker = middleMarker;
        this.middleMarkerProxies = null;
        invalidateGeometryCache();
        markerGroup = buildMarkerGroup();
    }

    public void setEndMarker(Marker endMarker){
        this.endMarker = endMarker;
        this.endMarkerProxy = null;
        invalidateGeometryCache();
        markerGroup = buildMarkerGroup();
    }

    public Marker getStartMarker(){
        return startMarker;
    }

    public Marker getMiddleMarker(){
        return middleMarker;
    }

    public Marker getEndMarker(){
        return endMarker;
    }

    /**
     * Builds a new marker group with the current set of 
     * markers
     */
    private CompositeGraphicsNode buildMarkerGroup(){
        if(startMarker != null && startMarkerProxy == null){
            startMarkerProxy = buildStartMarkerProxy();
        }

        if(middleMarker != null && middleMarkerProxies == null){
            middleMarkerProxies = buildMiddleMarkerProxies();
        }

        if(endMarker != null && endMarkerProxy == null){
            endMarkerProxy = buildEndMarkerProxy();
        }

        CompositeGraphicsNode group = new CompositeGraphicsNode();
        List children = group.getChildren();
        if(startMarkerProxy != null){
            children.add(startMarkerProxy);
        }

        if(middleMarkerProxies != null){
            for(int i=0; i<middleMarkerProxies.length; i++){
                children.add(middleMarkerProxies[i]);
            }
        }

        if(endMarkerProxy != null){
            children.add(endMarkerProxy);
        }

        return group;
    }

    /**
     * Builds a proxy <tt>GraphicsNode</tt> for the input 
     * <tt>Marker</tt> to be drawn at the start position
     */
    public ProxyGraphicsNode buildStartMarkerProxy(){
        PathIterator iter = getShape().getPathIterator(null);

        // Get initial point on the path
        double coords[] = new double[6];
        int segType = 0;

        if(iter.isDone()){
            return null;
        }

        segType = iter.currentSegment(coords);
        if(segType != iter.SEG_MOVETO){
            return null;
        }
        iter.next();

        Point2D markerPosition 
            = new Point2D.Double(coords[0],
                                 coords[1]);

        // If the marker's orient property is NaN,
        // the slope needs to be computed
        double rotation = startMarker.getOrient();
        if(Double.isNaN(rotation)){
            if(!iter.isDone()){
                double next[] = new double[6];
                int nextSegType = 0;
                nextSegType = iter.currentSegment(next);
                rotation = computeRotation((double[])null, 0, // no previous seg.
                                           coords, segType,   // segment ending on start point
                                           next, nextSegType, // segment out of start point
                                           new double[]{ coords[0], coords[1] });
                
            }
        }
        
        // Now, compute the marker's proxy transform
        AffineTransform markerTxf =
            computeMarkerTransform(startMarker,
                                   markerPosition,
                                   rotation);
                                   
        ProxyGraphicsNode gn 
            = new ProxyGraphicsNode();

        gn.setSource(startMarker.getMarkerNode());
        gn.setTransform(markerTxf);

        return gn;
    }

    /**
     * Builds a proxy <tt>GraphicsNode</tt> for the input 
     * <tt>Marker</tt> to be drawn at the end position
     */
    public ProxyGraphicsNode buildEndMarkerProxy(){
        PathIterator iter = getShape().getPathIterator(null);

        int nPoints = 0;

        // Get first point, in case the last segment on the
        // path is a close
        if(iter.isDone()){
            return null;
        }

        double coords[] = new double[6];
        double moveTo[] = new double[2];
        int segType = 0;
        segType = iter.currentSegment(coords);
        if(segType != iter.SEG_MOVETO){
            return null;
        }
        nPoints++;
        moveTo[0] = coords[0];
        moveTo[1] = coords[1];

        iter.next();
        
        // Now, get the last two points on the path
        double[] lastButOne = new double[6];
        double[] last = {coords[0], coords[1], coords[2],
                         coords[3], coords[4], coords[5] }, tmp = null;
        int lastSegType = segType;
        int lastButOneSegType = 0;
        while(!iter.isDone()){
            tmp = lastButOne;
            lastButOne = last;
            last = tmp;
            lastButOneSegType = lastSegType;

            lastSegType = iter.currentSegment(last);

            if(lastSegType == PathIterator.SEG_MOVETO){
                moveTo[0] = last[0];
                moveTo[1] = last[1];
            }

            iter.next();
            nPoints++;
        }

        if (nPoints < 2){
            return null;
        }

        // Turn the last segment into a position
        Point2D markerPosition = null;
        if(lastSegType != PathIterator.SEG_CLOSE){
            markerPosition = getSegmentTerminatingPoint(last, lastSegType);
        }
        else{
            markerPosition = getSegmentTerminatingPoint(coords, segType);
        }

        if(markerPosition == null){
            return null;
        }

        // If the marker's orient property is NaN,
        // the slope needs to be computed
        double rotation = endMarker.getOrient();
        if(Double.isNaN(rotation)){
            rotation = computeRotation(lastButOne, 
                                       lastButOneSegType, 
                                       last, lastSegType,
                                       null, 0,
                                       moveTo);
        }

        // Now, compute the marker's proxy transform
        AffineTransform markerTxf =
            computeMarkerTransform(endMarker,
                                   markerPosition,
                                   rotation);
                                   
        ProxyGraphicsNode gn 
            = new ProxyGraphicsNode();

        gn.setSource(endMarker.getMarkerNode());
        gn.setTransform(markerTxf);

        return gn;
    }

    /**
     * Extracts the terminating point, depending on the segment type.
     */
    private final Point2D getSegmentTerminatingPoint(double coords[], int segType){
        switch(segType){
        case PathIterator.SEG_CLOSE:
            return null;
        case PathIterator.SEG_CUBICTO:
            return new Point2D.Double(coords[4], coords[5]);
        case PathIterator.SEG_LINETO:
            return new Point2D.Double(coords[0], coords[1]);
        case PathIterator.SEG_MOVETO:
            return new Point2D.Double(coords[0], coords[1]);
        case PathIterator.SEG_QUADTO:
            return new Point2D.Double(coords[2], coords[3]);
        default:
            return null;
        }
    }

    /**
     * Builds a proxy <tt>GraphicsNode</tt> for the input 
     * <tt>Marker</tt> to be drawn at the middle positions
     */
    public ProxyGraphicsNode[] buildMiddleMarkerProxies(){
        PathIterator iter = getShape().getPathIterator(null);

        double[] prev = new double[6];
        double[] cur = new double[6];
        double[] next = new double[6], tmp = null;
        int prevSegType = 0, curSegType = 0, nextSegType = 0;

        // Get the first three points on the path
        if(iter.isDone()){
            return null;
        }

        prevSegType = iter.currentSegment(prev);

        double[] moveTo = new double[2];

        if(prevSegType != PathIterator.SEG_MOVETO){
            return null;
        }

        moveTo[0] = prev[0];
        moveTo[1] = prev[1];

        iter.next();

        if(iter.isDone()){
            return null;
        }

        curSegType = iter.currentSegment(cur);

        if(curSegType == PathIterator.SEG_MOVETO){
            moveTo[0] = cur[0];
            moveTo[1] = cur[1];
        }

        iter.next();

        Vector proxies = new Vector();
        while(!iter.isDone()){
            nextSegType = iter.currentSegment(next);

            if(nextSegType == PathIterator.SEG_MOVETO){
                moveTo[0] = next[0];
                moveTo[1] = next[1];
            }

            proxies.addElement(createMiddleMarker(prev, prevSegType,
                                                  cur, curSegType,
                                                  next, nextSegType,
                                                  moveTo));
            
            tmp = prev;
            prev = cur;
            prevSegType = curSegType;
            cur = next;
            curSegType = nextSegType;
            next = tmp;
            
            iter.next();
        }

        ProxyGraphicsNode gn[]
            = new ProxyGraphicsNode[proxies.size()];

        proxies.copyInto(gn);

        return gn;
    }

    private ProxyGraphicsNode createMiddleMarker(double[] prev,
                                                 int prevSegType,
                                                 double[] cur,
                                                 int curSegType,
                                                 double[] next,
                                                 int nextSegType,
                                                 double[] moveTo){

        // Turn the cur segment into a position
        Point2D markerPosition = null;
        if(curSegType != PathIterator.SEG_CLOSE){
            markerPosition = getSegmentTerminatingPoint(cur, curSegType);
        }
        else{
            markerPosition = new Point2D.Double(moveTo[0], moveTo[1]);
        }

        if(markerPosition == null){
            return null;
        }

        // If the marker's orient property is NaN,
        // the slope needs to be computed
        double rotation = middleMarker.getOrient();
        if(Double.isNaN(rotation)){
            rotation = computeRotation(prev, prevSegType,
                                       cur, curSegType,
                                       next, nextSegType,
                                       moveTo);
        }

        // Now, compute the marker's proxy transform
        AffineTransform markerTxf =
            computeMarkerTransform(middleMarker,
                                   markerPosition,
                                   rotation);
                                   
        ProxyGraphicsNode gn 
            = new ProxyGraphicsNode();

        gn.setSource(middleMarker.getMarkerNode());
        gn.setTransform(markerTxf);

        return gn;
    }

    private double computeRotation(double[] prev,
                                   int prevSegType,
                                   double[] cur,
                                   int curSegType,
                                   double[] next,
                                   int nextSegType,
                                   double[] moveTo){
        // Compute in slope, i.e., the slope of the segment
        // going into the current point
        double inSlope = computeInSlope(prev, prevSegType, 
                                        cur, curSegType, moveTo);

        // Compute out slope, i.e., the slope of the segment
        // going out of the current point
        double outSlope = computeOutSlope(cur, curSegType, 
                                          next, nextSegType, moveTo);

        if(Double.isNaN(inSlope)){
            inSlope = outSlope;
        }

        if(Double.isNaN(outSlope)){
            outSlope = inSlope;
        }

        if(Double.isNaN(inSlope)){
            return 0;
        }

       
        double rotationIn  = Math.atan(inSlope)*180./Math.PI;
        double rotationOut = Math.atan(outSlope)*180./Math.PI;
        double rotation = (rotationIn + rotationOut)/2;

        return rotation;
    }

    private double computeInSlope(double[] prev,
                                  int prevSegType,
                                  double[] cur,
                                  int curSegType,
                                  double[] moveTo){
        // Compute point into which the slope runs
        Point2D curEndPoint = null;
        if(curSegType != PathIterator.SEG_CLOSE){
            curEndPoint = getSegmentTerminatingPoint(cur, curSegType);
            if(curEndPoint == null){
                return Double.NaN;
            }
        }
        else{
            curEndPoint = new Point2D.Double(moveTo[0], moveTo[1]);
        }

        double dx = 0, dy = 0;
        switch(curSegType){
        case PathIterator.SEG_LINETO:
        case PathIterator.SEG_CLOSE:
            //
            // This is equivalent to a line from the previous
            // segment's terminating point and the current end
            // point.
            Point2D prevEndPoint = null;
            if(prevSegType != PathIterator.SEG_CLOSE){
                prevEndPoint = getSegmentTerminatingPoint(prev, prevSegType);
                if(prevEndPoint == null){
                    return Double.NaN;
                }
            }
            else{
                prevEndPoint = new Point2D.Double(moveTo[0], moveTo[1]);
            }
                
            dx = curEndPoint.getX() - prevEndPoint.getX();
            dy = curEndPoint.getY() - prevEndPoint.getY();
            if(dx > 0){
                return dy / dx;
            }
            if(dy > 0){
                return Double.POSITIVE_INFINITY;
            }
            if(dy < 0){
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        case PathIterator.SEG_CUBICTO:
            // If the current segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from the last control point and the curEndPoint
            dx = curEndPoint.getX() - prev[4];
            dy = curEndPoint.getY() - prev[5];
            if(dx > 0){
                return dy / dx;
            }
            if(dy > 0){
                return Double.POSITIVE_INFINITY;
            }
            if(dy < 0){
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        case PathIterator.SEG_QUADTO:
            // If the current segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from the last control point and the curEndPoint
            dx = curEndPoint.getX() - prev[2];
            dy = curEndPoint.getY() - prev[3];
            if(dx > 0){
                return dy / dx;
            }
            if(dy > 0){
                return Double.POSITIVE_INFINITY;
            }
            if(dy < 0){
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        case PathIterator.SEG_MOVETO:
            // Cannot compute the slope
        default:
            return Double.NaN;
        }
    }

    private double computeOutSlope(double[] cur,
                                   int curSegType,
                                   double[] next,
                                   int nextSegType,
                                   double[] moveTo){
        Point2D curEndPoint = null;
        if(curSegType != PathIterator.SEG_CLOSE){
            curEndPoint = getSegmentTerminatingPoint(cur, curSegType);
            if(curEndPoint == null){
                return Double.NaN;
            }
        }
        else{
            curEndPoint = new Point2D.Double(moveTo[0], moveTo[1]);
        }
        
        double dx = 0, dy = 0;

        switch(nextSegType){
        case PathIterator.SEG_CLOSE:
            //
            // This is equivalent to a line to the
            // last moveTo. Use the slope between the
            // last moveTo and the terminating point on 
            // the current segment
            //
            dx = moveTo[0] - curEndPoint.getX();
            dy = moveTo[1] - curEndPoint.getY();
            if(dx != 0){
                return dy / dx;
            }
            if(dy > 0){
                return Double.POSITIVE_INFINITY;
            }
            if(dy < 0){
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        case PathIterator.SEG_CUBICTO:
        case PathIterator.SEG_LINETO:
        case PathIterator.SEG_QUADTO:
            // If the next segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from curEndPoint and the first control
            // point
            dx = next[0] - curEndPoint.getX();
            dy = next[1] - curEndPoint.getY();
            if(dx != 0){
                return dy / dx;
            }
            if(dy > 0){
                return Double.POSITIVE_INFINITY;
            }
            if(dy < 0){
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        case PathIterator.SEG_MOVETO:
            // Cannot compute the out slope
        default:
            return Double.NaN;
        }
    }

    /**
     * Computes the transform for the input marker, so that
     * it is positioned at the given position with the specified
     * rotation
     */
    private AffineTransform 
        computeMarkerTransform(Marker marker,
                               Point2D markerPosition,
                               double rotation){
        Point2D ref = marker.getRef();
        /*AffineTransform txf = 
            AffineTransform.getTranslateInstance(markerPosition.getX()
                                                 - ref.getX(),
                                                 markerPosition.getY()
                                                 - ref.getY());*/
        AffineTransform txf = new AffineTransform();

        txf.translate(markerPosition.getX()
                      - ref.getX(),
                      markerPosition.getY()
                      - ref.getY());

        if(!Double.isNaN(rotation)){
            txf.rotate(rotation*Math.PI/180., 
                       ref.getX(),
                       ref.getY());
        }
        else{
            Exception e = new Exception();
            e.printStackTrace();
        }

        return txf;
    }

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     *
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc){
        // First, draw this ShapeNode
        super.primitivePaint(g2d, rc);

        // Now, paint markers
        try{
            markerGroup.paint(g2d, rc);
        }catch(InterruptedException e){
            // ????????? Should we really have interrupted exceptions?
        }
    }

    /**
     * Invalidates this <tt>ShapeNode</tt>. This node and all its
     * ancestors have been informed that all its cached values related
     * to its bounds must be recomputed.
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        dPrimitiveBounds = null;
        dGeometryBounds = null;
    }

    /**
     * Tests if the specified Point2D is inside the boundary of this node.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param p the specified Point2D in the user space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the coordinates are inside, false otherwise
     */
    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        return (super.contains(p, rc) | markerGroup.contains(p, rc));
    }

    /**
     * Tests if the interior of this node intersects the interior of a
     * specified Rectangle2D.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param r the specified Rectangle2D in the user node space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the rectangle intersects, false otherwise
     */
    public boolean intersects(Rectangle2D r, GraphicsNodeRenderContext rc) {
        return (super.intersects(r, rc) | markerGroup.intersects(r, rc));
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (dPrimitiveBounds == null) {
            Rectangle2D shapePrimitiveBounds = super.getPrimitiveBounds(rc);
            Rectangle2D markerGroupBounds = markerGroup.getBounds(rc);
            dPrimitiveBounds = (Rectangle2D)shapePrimitiveBounds.clone();
            dPrimitiveBounds.add(markerGroupBounds);
        }
        return dPrimitiveBounds;
    }

    /**
     * Returns the bounds of the area covered by this <tt>ShapeNode</tt>,
     * without taking any of its rendering attribute into account.
     * (i.e., exclusive of any clipping, masking, filtering or stroking...)
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        if (dGeometryBounds == null) {
            Rectangle2D shapeGeometryBounds = super.getGeometryBounds(rc);
            Rectangle2D markerGroupGeometryBounds 
                = markerGroup.getGeometryBounds(rc);
            dGeometryBounds  = (Rectangle2D)shapeGeometryBounds.clone();
            dGeometryBounds.add(markerGroupGeometryBounds);
        }
        return dGeometryBounds;
    }

    /**
     * Returns the outline of this <tt>DecoratedShapeNode</tt>.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return the outline of this node
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        return super.getOutline(rc);
    }

}
