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

import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegClosePath;

/**
 * Internal representation of the item SVGPathSeg.
 */
public class SVGPathSegItem extends AbstractSVGItem implements SVGPathSeg, SVGPathSegClosePath {

    protected short type;

    protected String letter;

    private float x;
    private float y;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float r1;
    private float r2;
    private float angle;
    private boolean largeArcFlag;
    private boolean sweepFlag;

    protected SVGPathSegItem(){}

    public SVGPathSegItem(short type,String letter){
        this.type = type;
        this.letter = letter;
    }

    public SVGPathSegItem(SVGPathSeg pathSeg){
        type = pathSeg.getPathSegType();
        switch(type){
        case SVGPathSeg.PATHSEG_CLOSEPATH:
            letter = AbstractSVGPathSegList.PATHSEG_CLOSEPATH_LETTER;
            break;
        default:
        }
    }
    protected String getStringValue(){
        return letter;
    }

    public short getPathSegType() {
        return type;
    }


    public String getPathSegTypeAsLetter(){
        return letter;
    }

    public float getR1() {
        return r1;
    }

    public void setR1(float r1) {
        this.r1 = r1;
    }

    public float getR2() {
        return r2;
    }

    public void setR2(float r2) {
        this.r2 = r2;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isLargeArcFlag() {
        return largeArcFlag;
    }

    public void setLargeArcFlag(boolean largeArcFlag) {
        this.largeArcFlag = largeArcFlag;
    }

    public boolean isSweepFlag() {
        return sweepFlag;
    }

    public void setSweepFlag(boolean sweepFlag) {
        this.sweepFlag = sweepFlag;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

}
