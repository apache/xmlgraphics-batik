/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGMatrix;

/**
 * This class provides support for the SVGTextContentElement interface.
 *
 * @author nicolas.socheleau@bitflash.com
 * @version $Id$
 */
public class SVGTextContentSupport
{

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getNumberOfChars()}.
     */
    public static int getNumberOfChars(Element elt)
    {
        final SVGOMElement svgelt = (SVGOMElement)elt;

        return (((SVGTextContent)svgelt.getSVGContext()).getNumberOfChars());
    }


    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getExtentOfChar(int charnum)}.
     */
    public static SVGRect getExtentOfChar(Element elt, final int charnum ) {
        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        return new SVGRect() {
                public float getX() {
                    return (float)context.getExtentOfChar(charnum).getX();
                }
                public void setX(float x) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getY() {
                    return (float)context.getExtentOfChar(charnum).getY();
                }
                public void setY(float y) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getWidth() {
                    return (float)context.getExtentOfChar(charnum).getWidth();
                }
                public void setWidth(float width) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
                public float getHeight() {
                    return (float)context.getExtentOfChar(charnum).getHeight();
                }
                public void setHeight(float height) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
            };

    }    

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getStartPositionOfChar(int charnum)}.
     */
    public static SVGPoint getStartPositionOfChar(Element elt,final int charnum) throws DOMException {

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        return new SVGPoint(){
                public float getX(){
                    return (float)context.getStartPositionOfChar(charnum).getX();
                }
                public float getY(){
                    return (float)context.getStartPositionOfChar(charnum).getY();
                }
                public void setX(float x) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.point", null);
                }
                public void setY(float y) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.point", null);
                }
                public SVGPoint matrixTransform(SVGMatrix matrix) {
                    throw new RuntimeException("!!! TODO: matrixTransform()");
                }

            };

    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getEndPositionOfChar(int charnum)}.
     */
    public static SVGPoint getEndPositionOfChar(Element elt,final int charnum) throws DOMException {

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        return new SVGPoint(){
                public float getX(){
                    return (float)context.getEndPositionOfChar(charnum).getX();
                }
                public float getY(){
                    return (float)context.getEndPositionOfChar(charnum).getY();
                }
                public void setX(float x) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.point", null);
                }
                public void setY(float y) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.point", null);
                }
                public SVGPoint matrixTransform(SVGMatrix matrix) {
                    throw new RuntimeException("!!! TODO: matrixTransform()");
                }

            };

    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int charnum, int nchars)}.
     */
    public static void selectSubString(Element elt, int charnum, int nchars){

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        context.selectSubString(charnum,nchars);
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getRotationOfChar(int charnum)}.
     */
    public static float getRotationOfChar(Element elt, final int charnum ) {
        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        
        return context.getRotationOfChar(charnum);
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int charnum, int nchars)}.
     */
    public static float getComputedTextLength(Element elt){

        final SVGOMElement svgelt = (SVGOMElement)elt;

        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        return context.getComputedTextLength();
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int charnum, int nchars)}.
     */
    public static float getSubStringLength(Element elt, int charnum, int nchars){

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();

        return context.getSubStringLength(charnum,nchars);
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getCharNumAtPosition(SVGPoint point)}.
     */
    public static int getCharNumAtPosition(Element elt, final float x, final float y) throws DOMException {

        final SVGOMElement svgelt = (SVGOMElement)elt;

        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        
        return context.getCharNumAtPosition(x,y);
    }
}
