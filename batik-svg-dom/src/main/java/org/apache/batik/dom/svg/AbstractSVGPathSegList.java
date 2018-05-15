/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;

/**
 * This class is the implementation of
 * <code>SVGPathSegList</code>.
 *
 * @author nicolas.socheleau@bitflash.com
 * @version $Id$
 */
public abstract class AbstractSVGPathSegList
    extends AbstractSVGList
    implements SVGPathSegList,
               SVGPathSegConstants {

    /**
     * Separator for a point list.
     */
    public static final String SVG_PATHSEG_LIST_SEPARATOR
        =" ";

    /**
     * Creates a new SVGPathSegList.
     */
    protected AbstractSVGPathSegList() {
        super();
    }

    /**
     * Return the separator between segments in the list.
     */
    protected String getItemSeparator(){
        return SVG_PATHSEG_LIST_SEPARATOR;
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
    public SVGPathSeg initialize ( SVGPathSeg newItem )
        throws DOMException, SVGException {

        return (SVGPathSeg)initializeImpl(newItem);
    }

    /**
     */
    public SVGPathSeg getItem ( int index )
        throws DOMException {

        return (SVGPathSeg)getItemImpl(index);
    }

    /**
     */
    public SVGPathSeg insertItemBefore ( SVGPathSeg newItem, int index )
        throws DOMException, SVGException {

        return (SVGPathSeg)insertItemBeforeImpl(newItem,index);
    }

    /**
     */
    public SVGPathSeg replaceItem ( SVGPathSeg newItem, int index )
        throws DOMException, SVGException {

        return (SVGPathSeg)replaceItemImpl(newItem,index);
    }

    /**
     */
    public SVGPathSeg removeItem ( int index )
        throws DOMException {

        return (SVGPathSeg)removeItemImpl(index);
    }

    /**
     */
    public SVGPathSeg appendItem ( SVGPathSeg newItem )
        throws DOMException, SVGException {

        return (SVGPathSeg) appendItemImpl(newItem);
    }

    /**
     */
    protected SVGItem createSVGItem(Object newItem){

        SVGPathSeg pathSeg = (SVGPathSeg)newItem;

        return createPathSegItem(pathSeg);
    }

    /**
     * Parse the 'd' attribute.
     *
     * @param value 'd' attribute value
     * @param handler : list handler
     */
    protected void doParse(String value, ListHandler handler)
        throws ParseException{

        PathParser pathParser = new PathParser();

        PathSegListBuilder builder = new PathSegListBuilder(handler);

        pathParser.setPathHandler(builder);
        pathParser.parse(value);

    }

    /**
     * Check if the item is an SVGPathSeg.
     */
    protected void checkItemType(Object newItem){
        if ( !( newItem instanceof SVGPathSeg ) ){
            createSVGException(SVGException.SVG_WRONG_TYPE_ERR,
                               "expected SVGPathSeg",
                               null);
        }
    }

    /**
     * create an SVGItem representing this SVGPathSeg.
     */
    protected SVGPathSegItem createPathSegItem(SVGPathSeg pathSeg){

        SVGPathSegItem pathSegItem = null;

        short type = pathSeg.getPathSegType();

        switch(type){
        case SVGPathSeg.PATHSEG_ARC_ABS:
        case SVGPathSeg.PATHSEG_ARC_REL:
            pathSegItem = new SVGPathSegArcItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_CLOSEPATH:
            pathSegItem = new SVGPathSegItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
        case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL:
            pathSegItem = new SVGPathSegCurvetoCubicItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS:
        case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL:
            pathSegItem = new SVGPathSegCurvetoCubicSmoothItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
        case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL:
            pathSegItem = new SVGPathSegCurvetoQuadraticItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS:
        case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL:
            pathSegItem = new SVGPathSegCurvetoQuadraticSmoothItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_LINETO_ABS:
        case SVGPathSeg.PATHSEG_LINETO_REL:
        case SVGPathSeg.PATHSEG_MOVETO_ABS:
        case SVGPathSeg.PATHSEG_MOVETO_REL:
            pathSegItem = new SVGPathSegMovetoLinetoItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL:
        case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS:
            pathSegItem = new SVGPathSegLinetoHorizontalItem(pathSeg);
            break;
        case SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL:
        case SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS:
            pathSegItem = new SVGPathSegLinetoVerticalItem(pathSeg);
            break;
        default:
        }
        return pathSegItem;
    }

    public static class SVGPathSegMovetoLinetoItem extends SVGPathSegItem
        implements SVGPathSegMovetoAbs,
                   SVGPathSegMovetoRel,
                   SVGPathSegLinetoAbs,
                   SVGPathSegLinetoRel {

        public SVGPathSegMovetoLinetoItem(short type, String letter,
                                          float x, float y){
            super(type,letter);
            this.setX(x);
            this.setY(y);
        }

        public SVGPathSegMovetoLinetoItem(SVGPathSeg pathSeg){
            type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_LINETO_REL:
                letter = PATHSEG_LINETO_REL_LETTER;
                setX(((SVGPathSegLinetoRel)pathSeg).getX());
                setY(((SVGPathSegLinetoRel)pathSeg).getY());
                break;
            case SVGPathSeg.PATHSEG_LINETO_ABS:
                letter = PATHSEG_LINETO_ABS_LETTER;
                setX(((SVGPathSegLinetoAbs)pathSeg).getX());
                setY(((SVGPathSegLinetoAbs)pathSeg).getY());
                break;
            case SVGPathSeg.PATHSEG_MOVETO_REL:
                letter = PATHSEG_MOVETO_REL_LETTER;
                setX(((SVGPathSegMovetoRel)pathSeg).getX());
                setY(((SVGPathSegMovetoRel)pathSeg).getY());
                break;
            case SVGPathSeg.PATHSEG_MOVETO_ABS:
                letter = PATHSEG_MOVETO_ABS_LETTER;
                setX(((SVGPathSegMovetoAbs)pathSeg).getX());
                setY(((SVGPathSegMovetoAbs)pathSeg).getY());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
        }
    }

    public static class SVGPathSegCurvetoCubicItem extends SVGPathSegItem
        implements SVGPathSegCurvetoCubicAbs,
                   SVGPathSegCurvetoCubicRel {

        public SVGPathSegCurvetoCubicItem(short type,String letter,
                                      float x1,float y1,float x2, float y2,
                                      float x, float y){
            super(type,letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
            this.setX2(x2);
            this.setY2(y2);
        }

        public SVGPathSegCurvetoCubicItem(SVGPathSeg pathSeg){
            this.type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
                letter = PATHSEG_CURVETO_CUBIC_ABS_LETTER;
                setX(((SVGPathSegCurvetoCubicAbs)pathSeg).getX());
                setY(((SVGPathSegCurvetoCubicAbs)pathSeg).getY());
                setX1(((SVGPathSegCurvetoCubicAbs)pathSeg).getX1());
                setY1(((SVGPathSegCurvetoCubicAbs)pathSeg).getY1());
                setX2(((SVGPathSegCurvetoCubicAbs)pathSeg).getX2());
                setY2(((SVGPathSegCurvetoCubicAbs)pathSeg).getY2());
                break;
            case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL:
                letter = PATHSEG_CURVETO_CUBIC_REL_LETTER;
                setX(((SVGPathSegCurvetoCubicRel)pathSeg).getX());
                setY(((SVGPathSegCurvetoCubicRel)pathSeg).getY());
                setX1(((SVGPathSegCurvetoCubicRel)pathSeg).getX1());
                setY1(((SVGPathSegCurvetoCubicRel)pathSeg).getY1());
                setX2(((SVGPathSegCurvetoCubicRel)pathSeg).getX2());
                setY2(((SVGPathSegCurvetoCubicRel)pathSeg).getY2());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        public void setX1(float x1){
            super.setX1(x1);
            resetAttribute();
        }
        public void setY1(float y1){
            super.setY1(y1);
            resetAttribute();
        }

        public void setX2(float x2){
            super.setX2(x2);
            resetAttribute();
        }
        public void setY2(float y2){
            super.setY2(y2);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getX1())
                    + ' '
                    + Float.toString(getY1())
                    + ' '
                    + Float.toString(getX2())
                    + ' '
                    + Float.toString(getY2())
                    + ' '
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
        }
    }

    public static class SVGPathSegCurvetoQuadraticItem extends SVGPathSegItem
        implements SVGPathSegCurvetoQuadraticAbs,
                   SVGPathSegCurvetoQuadraticRel {

        public SVGPathSegCurvetoQuadraticItem(short type,String letter,
                                          float x1,float y1,float x, float y ){
            super(type,letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
        }

        public SVGPathSegCurvetoQuadraticItem(SVGPathSeg pathSeg){
            this.type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
                letter = PATHSEG_CURVETO_QUADRATIC_ABS_LETTER;
                setX(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX());
                setY(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY());
                setX1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX1());
                setY1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY1());
                break;
            case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL:
                letter = PATHSEG_CURVETO_QUADRATIC_REL_LETTER;
                setX(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX());
                setY(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY());
                setX1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX1());
                setY1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY1());
                break;
        default:

            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        public void setX1(float x1){
            super.setX1(x1);
            resetAttribute();
        }
        public void setY1(float y1){
            super.setY1(y1);
            resetAttribute();
        }

        protected String getStringValue(){

            return letter
                    + ' '
                    + Float.toString(getX1())
                    + ' '
                    + Float.toString(getY1())
                    + ' '
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
         }
    }

    public static class SVGPathSegArcItem extends SVGPathSegItem
        implements SVGPathSegArcAbs,
                   SVGPathSegArcRel {

        public SVGPathSegArcItem(short type,String letter,
                             float r1,float r2,float angle,
                             boolean largeArcFlag, boolean sweepFlag,
                             float x, float y ){
            super(type,letter);
            this.setX(x);
            this.setY(y);
            this.setR1(r1);
            this.setR2(r2);
            this.setAngle(angle);
            this.setLargeArcFlag(largeArcFlag);
            this.setSweepFlag(sweepFlag);
        }

        public SVGPathSegArcItem(SVGPathSeg pathSeg){
            type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_ARC_ABS:
                letter = PATHSEG_ARC_ABS_LETTER;
                setX(((SVGPathSegArcAbs)pathSeg).getX());
                setY(((SVGPathSegArcAbs)pathSeg).getY());
                setR1(((SVGPathSegArcAbs)pathSeg).getR1());
                setR2(((SVGPathSegArcAbs)pathSeg).getR2());
                setAngle(((SVGPathSegArcAbs)pathSeg).getAngle());
                setLargeArcFlag(((SVGPathSegArcAbs)pathSeg).getLargeArcFlag());
                setSweepFlag(((SVGPathSegArcAbs)pathSeg).getSweepFlag());
                break;
            case SVGPathSeg.PATHSEG_ARC_REL:
                letter = PATHSEG_ARC_REL_LETTER;
                setX(((SVGPathSegArcRel)pathSeg).getX());
                setY(((SVGPathSegArcRel)pathSeg).getY());
                setR1(((SVGPathSegArcRel)pathSeg).getR1());
                setR2(((SVGPathSegArcRel)pathSeg).getR2());
                setAngle(((SVGPathSegArcRel)pathSeg).getAngle());
                setLargeArcFlag(((SVGPathSegArcRel)pathSeg).getLargeArcFlag());
                setSweepFlag(((SVGPathSegArcRel)pathSeg).getSweepFlag());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        public void setR1(float r1){
            super.setR1(r1);
            resetAttribute();
        }
        public void setR2(float r2){
            super.setR2(r2);
            resetAttribute();
        }

        public void setAngle(float angle){
            super.setAngle(angle);
            resetAttribute();
        }

        public boolean getSweepFlag(){
            return isSweepFlag();
        }

        public void setSweepFlag(boolean sweepFlag){
            super.setSweepFlag(sweepFlag);
            resetAttribute();
        }

        public boolean getLargeArcFlag(){
            return isLargeArcFlag();
        }

        public void setLargeArcFlag(boolean largeArcFlag){
            super.setLargeArcFlag(largeArcFlag);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getR1())
                    + ' '
                    + Float.toString(getR2())
                    + ' '
                    + Float.toString(getAngle())
                    + ' '
                    + ((isLargeArcFlag()?"1":"0"))
                    + ' '
                    + ((isSweepFlag()?"1":"0"))
                    + (' ')
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
        }
    }

    public static class SVGPathSegLinetoHorizontalItem
        extends SVGPathSegItem
        implements SVGPathSegLinetoHorizontalAbs,
                   SVGPathSegLinetoHorizontalRel {

        public SVGPathSegLinetoHorizontalItem(short type, String letter,
                                              float value){
            super(type,letter);
            this.setX(value);
        }
        public SVGPathSegLinetoHorizontalItem(SVGPathSeg pathSeg){
            this.type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS:
                letter = PATHSEG_LINETO_HORIZONTAL_ABS_LETTER;
                setX(((SVGPathSegLinetoHorizontalAbs)pathSeg).getX());
                break;
            case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL:
                letter = PATHSEG_LINETO_HORIZONTAL_REL_LETTER;
                setX(((SVGPathSegLinetoHorizontalRel)pathSeg).getX());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getX());
        }
    }

    public static class SVGPathSegLinetoVerticalItem
        extends SVGPathSegItem
    implements SVGPathSegLinetoVerticalAbs,
               SVGPathSegLinetoVerticalRel {

        public SVGPathSegLinetoVerticalItem(short type, String letter,
                                          float value){
            super(type,letter);
            this.setY(value);
        }

        public SVGPathSegLinetoVerticalItem(SVGPathSeg pathSeg){
            type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS:
                letter = PATHSEG_LINETO_VERTICAL_ABS_LETTER;
                setY(((SVGPathSegLinetoVerticalAbs)pathSeg).getY());
                break;
            case SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL:
                letter = PATHSEG_LINETO_VERTICAL_REL_LETTER;
                setY(((SVGPathSegLinetoVerticalRel)pathSeg).getY());
                break;
            default:
            }
        }

        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getY());
        }
    }

    public static class SVGPathSegCurvetoCubicSmoothItem extends SVGPathSegItem
        implements SVGPathSegCurvetoCubicSmoothAbs,
                   SVGPathSegCurvetoCubicSmoothRel {

        public SVGPathSegCurvetoCubicSmoothItem(short type,String letter,
                                          float x2,float y2,float x, float y ){
            super(type,letter);
            this.setX(x);
            this.setY(y);
            this.setX2(x2);
            this.setY2(y2);
        }

        public SVGPathSegCurvetoCubicSmoothItem(SVGPathSeg pathSeg){
            type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS:
                letter = PATHSEG_CURVETO_CUBIC_SMOOTH_ABS_LETTER;
                setX(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX());
                setY(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY());
                setX2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX2());
                setY2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY2());
                break;
            case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL:
                letter = PATHSEG_CURVETO_CUBIC_SMOOTH_REL_LETTER;
                setX(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX());
                setY(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY());
                setX2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX2());
                setY2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY2());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        public void setX2(float x2){
            super.setX2(x2);
            resetAttribute();
        }
        public void setY2(float y2){
            super.setY2(y2);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getX2())
                    + ' '
                    + Float.toString(getY2())
                    + ' '
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
        }
    }

    public static class SVGPathSegCurvetoQuadraticSmoothItem extends SVGPathSegItem
        implements SVGPathSegCurvetoQuadraticSmoothAbs ,
                   SVGPathSegCurvetoQuadraticSmoothRel {

        public SVGPathSegCurvetoQuadraticSmoothItem(short type, String letter,
                                                float x, float y){
            super(type,letter);
            this.setX(x);
            this.setY(y);
        }

        public SVGPathSegCurvetoQuadraticSmoothItem(SVGPathSeg pathSeg){
            type = pathSeg.getPathSegType();
            switch(type){
            case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS:
                letter = PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS_LETTER;
                setX(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getX());
                setY(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getY());
                break;
            case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL:
                letter = PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL_LETTER;
                setX(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getX());
                setY(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getY());
                break;
            default:
            }
        }

        public void setX(float x){
            super.setX(x);
            resetAttribute();
        }
        public void setY(float y){
            super.setY(y);
            resetAttribute();
        }

        protected String getStringValue(){
            return letter
                    + ' '
                    + Float.toString(getX())
                    + ' '
                    + Float.toString(getY());
        }
    }

    protected static class PathSegListBuilder extends DefaultPathHandler {

        protected ListHandler listHandler;

        public PathSegListBuilder(ListHandler listHandler){
            this.listHandler  = listHandler;
        }
        /**
         * Implements {@link org.apache.batik.parser.PathHandler#startPath()}.
         */
        public void startPath() throws ParseException {
            listHandler.startList();
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#endPath()}.
         */
        public void endPath() throws ParseException {
            listHandler.endList();
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#movetoRel(float,float)}.
         */
        public void movetoRel(float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegMovetoLinetoItem(SVGPathSeg.PATHSEG_MOVETO_REL, PATHSEG_MOVETO_REL_LETTER,
                    x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#movetoAbs(float,float)}.
         */
        public void movetoAbs(float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegMovetoLinetoItem(SVGPathSeg.PATHSEG_MOVETO_ABS, PATHSEG_MOVETO_ABS_LETTER,
                    x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#closePath()}.
         */
        public void closePath() throws ParseException {
            listHandler.item(new SVGPathSegItem
                (SVGPathSeg.PATHSEG_CLOSEPATH,PATHSEG_CLOSEPATH_LETTER));

        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoRel(float,float)}.
         */
        public void linetoRel(float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegMovetoLinetoItem(SVGPathSeg.PATHSEG_LINETO_REL, PATHSEG_LINETO_REL_LETTER,
                    x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoAbs(float,float)}.
         */
        public void linetoAbs(float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegMovetoLinetoItem(SVGPathSeg.PATHSEG_LINETO_ABS, PATHSEG_LINETO_ABS_LETTER,
                    x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoHorizontalRel(float)}.
         */
        public void linetoHorizontalRel(float x) throws ParseException {
            listHandler.item(new SVGPathSegLinetoHorizontalItem(SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL, PATHSEG_LINETO_HORIZONTAL_REL_LETTER,
                    x));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoHorizontalAbs(float)}.
         */
        public void linetoHorizontalAbs(float x) throws ParseException {
            listHandler.item(new SVGPathSegLinetoHorizontalItem(SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS, PATHSEG_LINETO_HORIZONTAL_ABS_LETTER,
                    x));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoVerticalRel(float)}.
         */
        public void linetoVerticalRel(float y) throws ParseException {
            listHandler.item(new SVGPathSegLinetoVerticalItem(SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL, PATHSEG_LINETO_VERTICAL_REL_LETTER,
                    y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#linetoVerticalAbs(float)}.
         */
        public void linetoVerticalAbs(float y) throws ParseException {
            listHandler.item(new SVGPathSegLinetoVerticalItem(SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS, PATHSEG_LINETO_VERTICAL_ABS_LETTER,
                    y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoCubicRel(float,float,float,float,float,float)}.
         */
        public void curvetoCubicRel(float x1, float y1,
                                    float x2, float y2,
                                    float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoCubicItem(SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL, PATHSEG_CURVETO_CUBIC_REL_LETTER,
                    x1, y1, x2, y2, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoCubicAbs(float,float,float,float,float,float)}.
         */
        public void curvetoCubicAbs(float x1, float y1,
                                    float x2, float y2,
                                    float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoCubicItem(SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS, PATHSEG_CURVETO_CUBIC_ABS_LETTER,
                    x1, y1, x2, y2, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoCubicSmoothRel(float,float,float,float)}.
         */
        public void curvetoCubicSmoothRel(float x2, float y2,
                                          float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoCubicSmoothItem(SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL,
                    PATHSEG_CURVETO_CUBIC_SMOOTH_REL_LETTER,
                    x2, y2, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoCubicSmoothAbs(float,float,float,float)}.
         */
        public void curvetoCubicSmoothAbs(float x2, float y2,
                                          float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoCubicSmoothItem(SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS,
                    PATHSEG_CURVETO_CUBIC_SMOOTH_ABS_LETTER,
                    x2, y2, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoQuadraticRel(float,float,float,float)}.
         */
        public void curvetoQuadraticRel(float x1, float y1,
                                        float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoQuadraticItem(SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL,
                    PATHSEG_CURVETO_QUADRATIC_REL_LETTER,
                    x1, y1, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#curvetoQuadraticAbs(float,float,float,float)}.
         */
        public void curvetoQuadraticAbs(float x1, float y1,
                                        float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegCurvetoQuadraticItem(SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS,
                    PATHSEG_CURVETO_QUADRATIC_ABS_LETTER,
                    x1, y1, x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#curvetoQuadraticSmoothRel(float,float)}.
         */
        public void curvetoQuadraticSmoothRel(float x, float y)
            throws ParseException {
            listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem(SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL,
                    PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL_LETTER,
                    x, y));
        }

        /**
         * Implements {@link org.apache.batik.parser.PathHandler#curvetoQuadraticSmoothAbs(float,float)}.
         */
        public void curvetoQuadraticSmoothAbs(float x, float y)
            throws ParseException {
            listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem(SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS,
                    PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS_LETTER,
                    x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#arcRel(float,float,float,boolean,boolean,float,float)}.
         */
        public void arcRel(float rx, float ry,
                           float xAxisRotation,
                           boolean largeArcFlag, boolean sweepFlag,
                           float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegArcItem(SVGPathSeg.PATHSEG_ARC_REL, PATHSEG_ARC_REL_LETTER,
                    rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }

        /**
         * Implements {@link
         * org.apache.batik.parser.PathHandler#arcAbs(float,float,float,boolean,boolean,float,float)}.
         */
        public void arcAbs(float rx, float ry,
                           float xAxisRotation,
                           boolean largeArcFlag, boolean sweepFlag,
                           float x, float y) throws ParseException {
            listHandler.item(new SVGPathSegArcItem(SVGPathSeg.PATHSEG_ARC_ABS, PATHSEG_ARC_ABS_LETTER,
                    rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }
    }
}
