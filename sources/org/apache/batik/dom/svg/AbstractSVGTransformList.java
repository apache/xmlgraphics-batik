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

import java.awt.geom.AffineTransform;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

/**
 * This class is the implementation of
 * <code>SVGTransformList</code>.
 *
 * @author nicolas.socheleau@bitflash.com
 * @version $Id$
 */
public abstract class AbstractSVGTransformList 
    extends AbstractSVGList 
    implements SVGTransformList {

    /**
     * Separator for a point list.
     */
    public final static String SVG_TRANSFORMATION_LIST_SEPARATOR
        = "";

    /**
     * Creates a new SVGTransformationList.
     */
    protected AbstractSVGTransformList() {
        super();
    }

    /**
     * Return the separator between transform in the list.
     */
    protected String getItemSeparator(){
        return SVG_TRANSFORMATION_LIST_SEPARATOR;
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
     */
    public SVGTransform initialize ( SVGTransform newItem )
        throws DOMException, SVGException {

        return (SVGTransform)initializeImpl(newItem);
    }
    /**
     */
    public SVGTransform getItem ( int index )
        throws DOMException {

        return (SVGTransform)getItemImpl(index);
    }

    /**
     */
    public SVGTransform insertItemBefore ( SVGTransform newItem, int index )
        throws DOMException, SVGException {

        return (SVGTransform)insertItemBeforeImpl(newItem,index);
    }

    /**
     */
    public SVGTransform replaceItem ( SVGTransform newItem, int index )
        throws DOMException, SVGException {

        return (SVGTransform)replaceItemImpl(newItem,index);
    }

    /**
     */
    public SVGTransform removeItem ( int index )
        throws DOMException {

        return (SVGTransform)removeItemImpl(index);
    }

    /**
     */
    public SVGTransform appendItem ( SVGTransform newItem )
        throws DOMException, SVGException {

        return (SVGTransform) appendItemImpl(newItem);
    }

    /**
     */
    public SVGTransform createSVGTransformFromMatrix ( SVGMatrix matrix ){
        SVGOMTransform transform = new SVGOMTransform();
        transform.setMatrix(matrix);
        return transform;
    }

    /**
     */
    public SVGTransform consolidate (  ){
        revalidate();

        if ( itemList.size() == 0 ){
            return null;
        }
        if ( itemList.size() == 1 ){
            return getItem(0);
        }

        SVGTransform view = (SVGTransform)getItemImpl(0);

        AffineTransform transform = (AffineTransform)
            ((SVGTransformItem)view).affineTransform.clone();

        for(int i = 1 ; i < itemList.size() ; i++ ){
            view = (SVGTransform)getItemImpl(i);
            transform.concatenate(((SVGTransformItem)view).affineTransform);
        }
        SVGOMMatrix matrix = new SVGOMMatrix(transform);
        return initialize(createSVGTransformFromMatrix(matrix));
    }

    /**
     */
    protected SVGItem createSVGItem(Object newItem){
        
        SVGTransform transform= (SVGTransform)newItem;

        return new SVGTransformItem(transform);
    }

    /**
     * Parse the 'points' attribute.
     *
     * @param value 'points' attribute value
     * @param handler : list handler
     */
    protected void doParse(String value, ListHandler handler)
        throws ParseException{

        TransformListParser transformListParser = new TransformListParser();
        
        TransformListBuilder builder = new TransformListBuilder(handler);
        
        transformListParser.setTransformListHandler(builder);
        transformListParser.parse(value);
        
    }

    /**
     * Check if the item is an SVGPoint.
     */
    protected void checkItemType(Object newItem){
        if ( !( newItem instanceof SVGTransform ) ){
            createSVGException(SVGException.SVG_WRONG_TYPE_ERR,
                               "expected SVGTransform",
                               null);
        }
    }

    /**
     * Internal representation of the item SVGPoint.
     */
    protected class SVGTransformItem extends AbstractSVGTransform 
        implements SVGItem {

        protected boolean xOnly;
        protected boolean angleOnly;

        /**
         * List the item belongs to.
         */
        protected AbstractSVGList parent;
        
        /**
         * String representation of the item.
         *
         * This is a cached representation of the
         * item while it is not changed.
         */
        protected String itemStringValue;

        protected SVGTransformItem(){            
        }

        /**
         * Notifies the parent list that
         * the item has changed.
         *
         * Discard the cached representation
         * of the item.
         */
        protected void resetAttribute(){
            if ( parent != null ){
                itemStringValue = null;
                parent.itemChanged();
            }
        }

        /**
         * Assign a parent list to this item.
         *
         * @param list : list the item belongs.
         */
        public void setParent(AbstractSVGList list){
            parent = list;
        }
        
        /**
         * Return the parent list of the item.
         *
         * @return list the item belongs.
         */
        public AbstractSVGList getParent(){
            return parent;
        }

        /**
         * Return the cached representation
         * of the item if valid otherwise
         * re-computes the String representation
         * of the item.
         */
        public String getValueAsString(){
            if ( itemStringValue == null ){
                itemStringValue = getStringValue();
            }
            return itemStringValue;
        }

        protected SVGTransformItem(SVGTransform transform){
            super();
            type = transform.getType();
            SVGMatrix matrix = transform.getMatrix();
            switch(type){
            case SVGTransform.SVG_TRANSFORM_TRANSLATE:
                setTranslate(matrix.getE(),matrix.getF());
                break;
            case SVGTransform.SVG_TRANSFORM_SCALE:
                setScale(matrix.getA(),matrix.getD());
                break;
            case SVGTransform.SVG_TRANSFORM_ROTATE:
                if (matrix.getE() == 0.0f ){
                    rotate(transform.getAngle());
                }
                else{
                    angleOnly = false;
                    if ( matrix.getA() == 1.0f ){
                        setRotate(transform.getAngle(),matrix.getE(),matrix.getF());
                    }
                    else{
                        if ( transform instanceof AbstractSVGTransform){
                            AbstractSVGTransform internal = (AbstractSVGTransform)transform;
                            setRotate(internal.getAngle(),internal.getX(),internal.getY());
                        }
                    }
                }
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWX:
                setSkewX(transform.getAngle());
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWY:
                setSkewY(transform.getAngle());
                break;
            case SVGTransform.SVG_TRANSFORM_MATRIX:
                setMatrix(matrix);
                break;
            }
            
        }

        protected void translate(float x){
            xOnly = true;
            setTranslate(x,0.0f);
        }
        protected void rotate(float angle){
            angleOnly = true;
            setRotate(angle,0.0f,0.0f);
        }
        protected void scale(float x){
            xOnly = true;
            setScale(x,x);
        }
        protected void matrix(float a,float b,float c,
                              float d,float e,float f){
            setMatrix(new SVGOMMatrix(new AffineTransform(a,b,c,d,e,f)));
        }


        public void setMatrix ( SVGMatrix matrix ){
            super.setMatrix(matrix);
            resetAttribute();
        }
        public void setTranslate ( float tx, float ty ){
            super.setTranslate(tx,ty);
            resetAttribute();
        }
        public void setScale ( float sx, float sy ){
            super.setScale(sx,sy);
            resetAttribute();
        }
        public void setRotate ( float angle, float cx, float cy ){
            super.setRotate(angle,cx,cy);
            resetAttribute();
        }
        public void setSkewX ( float angle ){
            super.setSkewX(angle);
            resetAttribute();
        }
        public void setSkewY ( float angle ){
            super.setSkewY(angle);
            resetAttribute();
        }

        protected SVGMatrix createMatrix(){
            return new AbstractSVGMatrix(){
                protected AffineTransform getAffineTransform(){
                    return SVGTransformItem.this.affineTransform;
                }

                public void setA(float a) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setA(a);
                    SVGTransformItem.this.resetAttribute();
                }
                public void setB(float b) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setB(b);
                    SVGTransformItem.this.resetAttribute();
                }
                public void setC(float c) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setC(c);
                    SVGTransformItem.this.resetAttribute();
                }
                public void setD(float d) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setD(d);
                    SVGTransformItem.this.resetAttribute();
                }
                public void setE(float e) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setE(e);
                    SVGTransformItem.this.resetAttribute();
                }
                public void setF(float f) throws DOMException {
                    SVGTransformItem.this.type = SVGTransform.SVG_TRANSFORM_MATRIX;
                    super.setF(f);
                    SVGTransformItem.this.resetAttribute();
                }
            };
        }

        protected String getStringValue(){
            StringBuffer buf = new StringBuffer();
            switch(type){
            case SVGTransform.SVG_TRANSFORM_TRANSLATE:
                buf.append("translate(");
                buf.append(affineTransform.getTranslateX());
                if( !xOnly ){
                    buf.append(' ');
                    buf.append(affineTransform.getTranslateY());
                }
                buf.append(')');
                break;
            case SVGTransform.SVG_TRANSFORM_ROTATE:
                buf.append("rotate(");
                buf.append(angle);
                if ( !angleOnly ){
                    buf.append(' ');
                    buf.append(x);
                    buf.append(' ');
                    buf.append(y);
                }
                buf.append(')');
                break;
            case SVGTransform.SVG_TRANSFORM_SCALE:
                buf.append("scale(");
                buf.append(affineTransform.getScaleX());
                if ( !xOnly ){
                    buf.append(' ');
                    buf.append(affineTransform.getScaleY());
                }
                buf.append(')');
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWX:
                buf.append("skewX(");
                buf.append(angle);
                buf.append(')');
                break;
            case SVGTransform.SVG_TRANSFORM_SKEWY:
                buf.append("skewY(");
                buf.append(angle);
                buf.append(')');
                break;
            case SVGTransform.SVG_TRANSFORM_MATRIX:
                buf.append("matrix(");
                double[] matrix = new double[6];
                affineTransform.getMatrix(matrix);
                for(int i = 0 ; i < 6 ; i++ ){
                    if ( i != 0 ){
                        buf.append(' ');
                    }
                    buf.append((float)matrix[i]);
                }
                buf.append(')');
                break;                    
            }
            return buf.toString();
        }
    }

    /**
     * Helper class to interface the <code>PointsParser</code>
     * and the <code>ListHandler</code>
     */
    protected class TransformListBuilder
        implements TransformListHandler {

        /**
         * list handler.
         */
        protected ListHandler listHandler;
        
        public TransformListBuilder(ListHandler listHandler){
            this.listHandler = listHandler;
        }

        public void startTransformList() throws ParseException{
            listHandler.startList();
        }

    /**
     * Invoked when 'matrix(a, b, c, d, e, f)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void matrix(float a, float b, float c, 
                           float d, float e, float f)
            throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.matrix(a,b,c,d,e,f);
            listHandler.item(item);
        }

    /**
     * Invoked when 'rotate(theta)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void rotate(float theta) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.rotate(theta);
            listHandler.item(item);
        }

    /**
     * Invoked when 'rotate(theta, cx, cy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void rotate(float theta, float cx, float cy) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.setRotate(theta,cx,cy);
            listHandler.item(item);
        }

    /**
     * Invoked when 'translate(tx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void translate(float tx) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.translate(tx);
            listHandler.item(item);

        }

    /**
     * Invoked when 'translate(tx, ty)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void translate(float tx, float ty) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.setTranslate(tx,ty);
            listHandler.item(item);
        }

    /**
     * Invoked when 'scale(sx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void scale(float sx) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.scale(sx);
            listHandler.item(item);

        }

    /**
     * Invoked when 'scale(sx, sy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void scale(float sx, float sy) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.setScale(sx,sy);
            listHandler.item(item);
        }

    /**
     * Invoked when 'skewX(skx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
        public void skewX(float skx) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.setSkewX(skx);
            listHandler.item(item);
        }

    /**
     * Invoked when 'skewY(sky)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
        public void skewY(float sky) throws ParseException{
            SVGTransformItem item  = new SVGTransformItem();
            item.setSkewY(sky);
            listHandler.item(item);
        }

    /**
     * Invoked when the transform ends.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
        public void endTransformList() throws ParseException{
            listHandler.endList();
        }
    }
    
}
