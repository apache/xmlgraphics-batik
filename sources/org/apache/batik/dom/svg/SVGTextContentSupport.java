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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;

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
        Rectangle2D r2d = getExtent(svgelt, context, charnum);
            
        return new SVGRect() {
                public float getX() {
                    return (float)SVGTextContentSupport.getExtent
                        (svgelt, context, charnum).getX();
                }
                public void setX(float x) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }

                public float getY() {
                    return (float)SVGTextContentSupport.getExtent
                        (svgelt, context, charnum).getY();
                }
                public void setY(float y) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }

                public float getWidth() {
                    return (float)SVGTextContentSupport.getExtent
                        (svgelt, context, charnum).getWidth();
                }
                public void setWidth(float width) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }

                public float getHeight() {
                    return (float)SVGTextContentSupport.getExtent
                        (svgelt, context, charnum).getHeight();
                }
                public void setHeight(float height) throws DOMException {
                    throw svgelt.createDOMException
                        (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                         "readonly.rect", null);
                }
            };
    }

    protected static Rectangle2D getExtent
        (SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Rectangle2D r2d = context.getExtentOfChar(charnum);
        if (r2d == null) throw svgelt.createDOMException
                             (DOMException.INDEX_SIZE_ERR, "",null);
        return r2d;
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getStartPositionOfChar(int charnum)}.
     */
    public static SVGPoint getStartPositionOfChar
        (Element elt, final int charnum) throws DOMException {

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        Point2D p2d = getStartPos(svgelt, context, charnum);

        return new SVGTextPoint(svgelt){
                public float getX(){
                    return (float)SVGTextContentSupport.getStartPos
                        (this.svgelt, context, charnum).getX();
                }
                public float getY(){
                    return (float)SVGTextContentSupport.getStartPos
                        (this.svgelt, context, charnum).getY();
                }
            };
    }

    protected static Point2D getStartPos
        (SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Point2D p2d = context.getStartPositionOfChar(charnum);
        if (p2d == null) throw svgelt.createDOMException
                             (DOMException.INDEX_SIZE_ERR, "",null);
        return p2d;
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTextContentElement#getEndPositionOfChar(int charnum)}.
     */
    public static SVGPoint getEndPositionOfChar
        (Element elt,final int charnum) throws DOMException {

        final SVGOMElement svgelt = (SVGOMElement)elt;

        if ( (charnum < 0) || 
             (charnum >= getNumberOfChars(elt)) ){
            throw svgelt.createDOMException
                (DOMException.INDEX_SIZE_ERR,
                 "",null);
        }
        
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        Point2D p2d = getEndPos(svgelt, context, charnum);

        return new SVGTextPoint(svgelt){
                public float getX(){
                    return (float)SVGTextContentSupport.getEndPos
                        (this.svgelt, context, charnum).getX();
                }
                public float getY(){
                    return (float)SVGTextContentSupport.getEndPos
                        (this.svgelt, context, charnum).getY();
                }
            };
    }

    protected static Point2D getEndPos
        (SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Point2D p2d = context.getEndPositionOfChar(charnum);
        if (p2d == null) throw svgelt.createDOMException
                             (DOMException.INDEX_SIZE_ERR, "",null);
        return p2d;
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

        context.selectSubString(charnum, nchars);
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

    public static class SVGTextPoint extends SVGOMPoint {
        SVGOMElement svgelt;
        SVGTextPoint(SVGOMElement elem) {
            svgelt = elem;
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
    }

}
