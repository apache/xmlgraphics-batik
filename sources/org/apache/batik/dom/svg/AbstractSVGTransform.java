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

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;

/**
 * Abstract implementation for SVGTransform.
 *
 * This is the base implementation for SVGTransform
 * 
 * @author nicolas.socheleau@bitflash.com
 * @version $Id :$
 */
public abstract class AbstractSVGTransform implements SVGTransform {

    /**
     * Type of the transformation.
     * 
     * By default, the type is unknown
     */
    protected short type = SVG_TRANSFORM_UNKNOWN;

    /**
     * AffineTranform associated to the SVGTransform
     *
     * Java2D representation of the SVGTransform.
     */
    protected AffineTransform affineTransform;

    /**
     * Angle associated to the transform.
     * This value is not necessary since the AffineTransform
     * will contain it but it is easier to have it than
     * extracting it from the AffineTransform.
     */
    protected float angle;

    protected float x;

    protected float y;

    /**
     * Create a SVGMatrix associated to the transform.
     *
     * @return SVGMatrix representing the transformation
     */
    protected abstract SVGMatrix createMatrix();

    /**
     * Default constructor.
     */
    protected AbstractSVGTransform(){
    }

    /**
     */
    protected void setType(short type){
        this.type = type;
    }

    protected float getX(){
        return x;
    }

    protected float getY(){
        return y;
    }

    /**
     */
    public short getType( ){
        return type;
    }

    /**
     */
    public SVGMatrix getMatrix( ){
        return createMatrix();
    }
    /**
     */
    public float getAngle( ){
        return angle;
    }
    /**
     */
    public void setMatrix ( SVGMatrix matrix ){
        type = SVG_TRANSFORM_MATRIX;
        affineTransform = new AffineTransform(matrix.getA(),matrix.getB(),matrix.getC(),
                                              matrix.getD(),matrix.getE(),matrix.getF());
    }
    /**
     */
    public void setTranslate ( float tx, float ty ){
        type = SVG_TRANSFORM_TRANSLATE;
        affineTransform = AffineTransform.getTranslateInstance(tx,ty);
    }
    /**
     */
    public void setScale ( float sx, float sy ){
        type = SVG_TRANSFORM_SCALE;
        affineTransform = AffineTransform.getScaleInstance(sx,sy);
    }
    /**
     */
    public void setRotate ( float angle, float cx, float cy ){
        type = SVG_TRANSFORM_ROTATE;
        affineTransform = AffineTransform.getRotateInstance(Math.toRadians(angle),cx,cy);
        this.angle = angle;
        this.x = cx;
        this.y = cy;
    }
    /**
     */
    public void setSkewX ( float angle ){
        type = SVG_TRANSFORM_SKEWX;
        affineTransform = new AffineTransform(1.0,Math.tan(Math.toRadians(angle)),0.0,
                                              1.0,0.0,0.0);
        this.angle = angle;
    }
    /**
     */
    public void setSkewY ( float angle ){
        type = SVG_TRANSFORM_SKEWY;
        this.angle = angle;
        affineTransform = new AffineTransform(1.0,0.0,Math.tan(Math.toRadians(angle)),
                                              1.0,0.0,0.0);
    }

}

