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
 * A shape painter that can be used to paint markers on a shape.
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class MarkerShapePainter implements ShapePainter {
    /** The Shape to be painted */
    protected Shape shape;

    /**
     * Constructs a new <tt>FillShapePainter</tt> that can be used to fill
     * a <tt>Shape</tt>.
     *
     * @param shape Shape to be painted by this painter. Should not be null
     */
    public MarkerShapePainter(Shape shape) {
        if(shape == null){
            throw new IllegalArgumentException();
        }

        this.shape = shape;
    }

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
        buildMarkerGroup();
    }

    public void setMiddleMarker(Marker middleMarker){
        this.middleMarker = middleMarker;
        this.middleMarkerProxies = null;
        buildMarkerGroup();
    }

    public void setEndMarker(Marker endMarker){
        this.endMarker = endMarker;
        this.endMarkerProxy = null;
        buildMarkerGroup();
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
    private void buildMarkerGroup(){
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

        markerGroup = group;
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
                if(nextSegType == PathIterator.SEG_CLOSE){
                    nextSegType = PathIterator.SEG_LINETO;
                    next[0] = coords[0];
                    next[1] = coords[1];
                }
                rotation = computeRotation((double[])null, 0,  // no previous seg.
                                           coords, segType,    // segment ending on start point
                                           next, nextSegType); // segment out of start point
                
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
            } else if(lastSegType == PathIterator.SEG_CLOSE){
                lastSegType = PathIterator.SEG_LINETO;
                last[0] = moveTo[0];
                last[1] = moveTo[1];
            }

            iter.next();
            nPoints++;
        }

        if (nPoints < 2){
            return null;
        }

        // Turn the last segment into a position
        Point2D markerPosition = 
            getSegmentTerminatingPoint(last, lastSegType);

        // If the marker's orient property is NaN,
        // the slope needs to be computed
        double rotation = endMarker.getOrient();
        if(Double.isNaN(rotation)){
            rotation = computeRotation(lastButOne, 
                                       lastButOneSegType, 
                                       last, lastSegType,
                                       null, 0);
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
        case PathIterator.SEG_CUBICTO:
            return new Point2D.Double(coords[4], coords[5]);
        case PathIterator.SEG_LINETO:
            return new Point2D.Double(coords[0], coords[1]);
        case PathIterator.SEG_MOVETO:
            return new Point2D.Double(coords[0], coords[1]);
        case PathIterator.SEG_QUADTO:
            return new Point2D.Double(coords[2], coords[3]);
        case PathIterator.SEG_CLOSE:
        default:
            throw new Error(); 
            // Should never happen: close segments are 
            // replaced with lineTo
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
        } else if(curSegType == PathIterator.SEG_CLOSE){
            curSegType = PathIterator.SEG_LINETO;
            cur[0] = moveTo[0];
            cur[1] = moveTo[1];
        }

        iter.next();

        Vector proxies = new Vector();
        while(!iter.isDone()){
            nextSegType = iter.currentSegment(next);

            if(nextSegType == PathIterator.SEG_MOVETO){
                moveTo[0] = next[0];
                moveTo[1] = next[1];
            } else if(nextSegType == PathIterator.SEG_CLOSE){
                nextSegType = PathIterator.SEG_LINETO;
                next[0] = moveTo[0];
                next[1] = moveTo[1];
            }

            proxies.addElement(createMiddleMarker(prev, prevSegType,
                                                  cur, curSegType,
                                                  next, nextSegType));
            
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
                                                 int nextSegType){

        // Turn the cur segment into a position
        Point2D markerPosition = getSegmentTerminatingPoint(cur, curSegType);

        // If the marker's orient property is NaN,
        // the slope needs to be computed
        double rotation = middleMarker.getOrient();
        if(Double.isNaN(rotation)){
            rotation = computeRotation(prev, prevSegType,
                                       cur, curSegType,
                                       next, nextSegType);
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
                                   int nextSegType){
        // Compute in slope, i.e., the slope of the segment
        // going into the current point
        double[] inSlope = computeInSlope(prev, prevSegType, 
                                          cur, curSegType);

        // Compute out slope, i.e., the slope of the segment
        // going out of the current point
        double[] outSlope = computeOutSlope(cur, curSegType, 
                                            next, nextSegType);

        if(inSlope == null){
            inSlope = outSlope;
        }

        if(outSlope == null){
            outSlope = inSlope;
        }

        if(inSlope == null){
            return 0;
        }

       
        double rotationIn  = Math.atan2(inSlope[1], inSlope[0])*180./Math.PI;
        double rotationOut = Math.atan2(outSlope[1], outSlope[0])*180./Math.PI;
        double rotation = (rotationIn + rotationOut)/2;

        return rotation;
    }

    /**
     * @return dx/dy for the in slope
     */
    private double[] computeInSlope(double[] prev,
                                    int prevSegType,
                                    double[] cur,
                                    int curSegType){
        // Compute point into which the slope runs
        Point2D curEndPoint = getSegmentTerminatingPoint(cur, curSegType);

        double dx = 0, dy = 0;
        switch(curSegType){
        case PathIterator.SEG_QUADTO:
            // If the current segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from the last control point and the curEndPoint
            dx = curEndPoint.getX() - cur[0];
            dy = curEndPoint.getY() - cur[1];
            break;
        case PathIterator.SEG_LINETO:
            //
            // This is equivalent to a line from the previous
            // segment's terminating point and the current end
            // point.
            Point2D prevEndPoint = getSegmentTerminatingPoint(prev, prevSegType);
                
            dx = curEndPoint.getX() - prevEndPoint.getX();
            dy = curEndPoint.getY() - prevEndPoint.getY();
            break;
        case PathIterator.SEG_CUBICTO:
            // If the current segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from the last control point and the curEndPoint
            dx = curEndPoint.getX() - cur[2];
            dy = curEndPoint.getY() - cur[3];
            break;
        case PathIterator.SEG_CLOSE:
            // Should not have any close at this point
            throw new Error();
        case PathIterator.SEG_MOVETO:
            // Cannot compute the slope
        default:
            return null;
        }

        if(dx == 0 && dy == 0){
            return null;
        }

        return new double[] { dx, dy };
    }

    /**
     * @return dx/dy for the out slope
     */
    private double[] computeOutSlope(double[] cur,
                                     int curSegType,
                                     double[] next,
                                     int nextSegType){
        Point2D curEndPoint = getSegmentTerminatingPoint(cur, curSegType);
        
        double dx = 0, dy = 0;

        switch(nextSegType){
        case PathIterator.SEG_CLOSE:
            // Should not happen at this point, because all close
            // segments have been replaced by lineTo segments.
            break;
        case PathIterator.SEG_CUBICTO:
        case PathIterator.SEG_LINETO:
        case PathIterator.SEG_QUADTO:
            // If the next segment is a line, quad or cubic curve. 
            // the slope is about equal to that of the
            // line from curEndPoint and the first control
            // point
            dx = next[0] - curEndPoint.getX();
            dy = next[1] - curEndPoint.getY();
            break;
        case PathIterator.SEG_MOVETO:
            // Cannot compute the out slope
        default:
            return null;
        }

        if(dx == 0 && dy == 0){
            return null;
        }

        return new double[] { dx, dy };
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

        return txf;
    }

    /**
     * Paints the specified shape using the specified Graphics2D and context.
     *
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param ctx the render context to use
     */

     public void paint(Graphics2D g2d,
                       GraphicsNodeRenderContext ctx) {
         if(markerGroup.getChildren().size() > 0){
             markerGroup.paint(g2d, ctx);
         }
     }

    /**
     * Returns the area painted by this painter
     */
    public Shape getPaintedArea(GraphicsNodeRenderContext rc){
        return markerGroup.getBounds(rc);
    }

    /**
     * Sets the Shape this painter is associated with.
     * @param shape new shape this painter should be associated with.
     *        should not be null.
     */
    public void setShape(Shape shape){
        this.shape = shape;
        this.startMarkerProxy = null;
        this.middleMarkerProxies = null;
        this.endMarkerProxy = null;
        buildMarkerGroup();
    }

    /**
     * Gets the Shape this painter is associated with.
     *
     * @return shape associated with this Painter.
     */
    public Shape getShape(){
        return shape;
    }
}
