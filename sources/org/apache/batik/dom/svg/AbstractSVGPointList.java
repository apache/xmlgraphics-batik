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
