/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;
import org.apache.batik.parser.PointsParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;



/**
 * This class is the implementation of
 * <code>SVGPointList</code>.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public abstract class AbstractSVGPointList
    extends AbstractSVGList
    implements SVGPointList {

    /**
     * Separator for a point list.
     */
    public final static String SVG_POINT_LIST_SEPARATOR
        =" ";

    /**
     * Return the separator between points in the list.
     */
    protected String getItemSeparator(){
        return SVG_POINT_LIST_SEPARATOR;
    }

    /**
     * Create an SVGException when the checkItemType fails.
     *
     * @return SVGException
     */
    protected abstract SVGException createSVGException(short type,
                                                       String key,
                                                       Object[] args);

    /**
     * Creates a new SVGPointList.
     */
    protected AbstractSVGPointList() {
        super();
    }

    /**
     */
    public SVGPoint initialize ( SVGPoint newItem )
        throws DOMException, SVGException {

        return (SVGPoint)initializeImpl(newItem);
    }

    /**
     */
    public SVGPoint getItem ( int index )
        throws DOMException {

        return (SVGPoint)getItemImpl(index);
    }

    /**
     */
    public SVGPoint insertItemBefore ( SVGPoint newItem, int index )
        throws DOMException, SVGException {

        return (SVGPoint)insertItemBeforeImpl(newItem,index);
    }

    /**
     */
    public SVGPoint replaceItem ( SVGPoint newItem, int index )
        throws DOMException, SVGException {

        return (SVGPoint)replaceItemImpl(newItem,index);
    }

    /**
     */
    public SVGPoint removeItem ( int index )
        throws DOMException {

        return (SVGPoint)removeItemImpl(index);
    }

    /**
     */
    public SVGPoint appendItem ( SVGPoint newItem )
        throws DOMException, SVGException {

        return (SVGPoint) appendItemImpl(newItem);
    }

    /**
     */
    protected SVGItem createSVGItem(Object newItem){
        
        SVGPoint point= (SVGPoint)newItem;

        return new SVGPointItem(point.getX(), point.getY());
    }
    
    /**
     * Parse the 'points' attribute.
     *
     * @param value 'points' attribute value
     * @param handler : list handler
     */
    protected void doParse(String value, ListHandler handler)
        throws ParseException{

        PointsParser pointsParser = new PointsParser();
        
        PointsListBuilder builder = new PointsListBuilder(handler);
        
        pointsParser.setPointsHandler(builder);
        pointsParser.parse(value);
        
    }

    /**
     * Check if the item is an SVGPoint.
     */
    protected void checkItemType(Object newItem)
        throws SVGException {
        if ( !( newItem instanceof SVGPoint ) ){
            createSVGException(SVGException.SVG_WRONG_TYPE_ERR,
                               "expected SVGPoint",
                               null);
        }
    }

    /**
     * Representation of the item SVGPoint.
     */
    protected class SVGPointItem 
        extends AbstractSVGItem 
        implements SVGPoint {

        ///x-axis value
        protected float x;
        ///yaxis value
        protected float y;

        /**
         * Default contructor.
         * @param x x-axis value
         * @param y y-axis value
         */
        public SVGPointItem(float x, float y){
            this.x = x;
            this.y = y;
        }

        /**
         * Return a String representation of
         * on SVGPoint in a SVGPointList.
         *
         * @return String representation of the item
         */
        protected String getStringValue(){
            StringBuffer value = new StringBuffer();
            value.append(x);
            value.append(',');
            value.append(y);

            return value.toString();
        }

        /**
         */
        public float getX(){
            return x;
        }
        /**
         */
        public float getY(){
            return y;
        }
        /**
         */
        public void setX(float x){
            this.x = x;
            resetAttribute();
        }
        /**
         */
        public void setY(float y){
            this.y = y;
            resetAttribute();
        }
        /**
         */
        public SVGPoint matrixTransform ( SVGMatrix matrix ){
            throw new RuntimeException(" !!! TODO: matrixTransform ( SVGMatrix matrix )");
        }
    }

    /**
     * Helper class to interface the <code>PointsParser</code>
     * and the <code>ListHandler</code>
     */
    protected class PointsListBuilder
        implements PointsHandler {

        /**
         * list handler.
         */
        protected ListHandler listHandler;
        
        public PointsListBuilder(ListHandler listHandler){
            this.listHandler = listHandler;
        }

        public void startPoints() 
            throws ParseException{

            listHandler.startList();
        }
        /**
         * Create SVGPoint item and motifies
         * the list handler is new item was created.
         */
        public void point(float x, float y) 
            throws ParseException {

            listHandler.item(new SVGPointItem(x,y));
        }

        public void endPoints() 
            throws ParseException {
            listHandler.endList();
        }
    }
   
}
