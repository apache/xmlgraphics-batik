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
import java.awt.geom.NoninvertibleTransformException;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;

/**
 * This class provides an abstract implementation of the {@link SVGMatrix}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractSVGMatrix implements SVGMatrix {

    /**
     * The transform used to implement flipX.
     */
    protected final static AffineTransform FLIP_X_TRANSFORM =
        new AffineTransform(-1, 0, 0, 1, 0, 0);

    /**
     * The transform used to implement flipX.
     */
    protected final static AffineTransform FLIP_Y_TRANSFORM =
        new AffineTransform(1, 0, 0, -1, 0, 0);

    /**
     * Returns the associated AffineTransform.
     */
    protected abstract AffineTransform getAffineTransform();
    
    /**
     * Implements {@link SVGMatrix#getA()}.
     */
    public float getA() {
        return (float)getAffineTransform().getScaleX();
    }

    /**
     * Implements {@link SVGMatrix#setA(float)}.
     */
    public void setA(float a) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(a,
                        at.getShearY(),
                        at.getShearX(),
                        at.getScaleY(),
                        at.getTranslateX(),
                        at.getTranslateY());
    }

    /**
     * Implements {@link SVGMatrix#getB()}.
     */
    public float getB() {
        return (float)getAffineTransform().getShearY();
    }

    /**
     * Implements {@link SVGMatrix#setB(float)}.
     */
    public void setB(float b) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(at.getScaleX(),
                        b,
                        at.getShearX(),
                        at.getScaleY(),
                        at.getTranslateX(),
                        at.getTranslateY());
    }

    /**
     * Implements {@link SVGMatrix#getC()}.
     */
    public float getC() {
        return (float)getAffineTransform().getShearX();
    }

    /**
     * Implements {@link SVGMatrix#setC(float)}.
     */
    public void setC(float c) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(at.getScaleX(),
                        at.getShearY(),
                        c,
                        at.getScaleY(),
                        at.getTranslateX(),
                        at.getTranslateY());
    }

    /**
     * Implements {@link SVGMatrix#getD()}.
     */
    public float getD() {
        return (float)getAffineTransform().getScaleY();
    }

    /**
     * Implements {@link SVGMatrix#setD(float)}.
     */
    public void setD(float d) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(at.getScaleX(),
                        at.getShearY(),
                        at.getShearX(),
                        d,
                        at.getTranslateX(),
                        at.getTranslateY());
    }

    /**
     * Implements {@link SVGMatrix#getE()}.
     */
    public float getE() {
        return (float)getAffineTransform().getTranslateX();
    }

    /**
     * Implements {@link SVGMatrix#setE(float)}.
     */
    public void setE(float e) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(at.getScaleX(),
                        at.getShearY(),
                        at.getShearX(),
                        at.getScaleY(),
                        e,
                        at.getTranslateY());
    }

    /**
     * Implements {@link SVGMatrix#getF()}.
     */
    public float getF() {
        return (float)getAffineTransform().getTranslateY();
    }

    /**
     * Implements {@link SVGMatrix#setF(float)}.
     */
    public void setF(float f) throws DOMException {
        AffineTransform at = getAffineTransform();
        at.setTransform(at.getScaleX(),
                        at.getShearY(),
                        at.getShearX(),
                        at.getScaleY(),
                        at.getTranslateX(),
                        f);
    }

    /**
     * Implements {@link SVGMatrix#multiply(SVGMatrix)}.
     */
    public SVGMatrix multiply(SVGMatrix secondMatrix) {
        AffineTransform at = new AffineTransform(secondMatrix.getA(),
                                                 secondMatrix.getB(),
                                                 secondMatrix.getC(),
                                                 secondMatrix.getD(),
                                                 secondMatrix.getE(),
                                                 secondMatrix.getF());
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.concatenate(at);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#inverse()}.
     */
    public SVGMatrix inverse() throws SVGException {
        try {
            return new SVGOMMatrix(getAffineTransform().createInverse());
        } catch (NoninvertibleTransformException e) {
            throw new SVGOMException(SVGException.SVG_MATRIX_NOT_INVERTABLE,
                                     e.getMessage());
        }
    }

    /**
     * Implements {@link SVGMatrix#translate(float,float)}.
     */
    public SVGMatrix translate(float x, float y) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.translate(x, y);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#scale(float)}.
     */
    public SVGMatrix scale(float scaleFactor) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.scale(scaleFactor, scaleFactor);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#scaleNonUniform(float,float)}.
     */
    public SVGMatrix scaleNonUniform (float scaleFactorX, float scaleFactorY) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.scale(scaleFactorX, scaleFactorY);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#rotate(float)}.
     */
    public SVGMatrix rotate(float angle) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.rotate(angle);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#rotateFromVector(float,float)}.
     */
    public SVGMatrix rotateFromVector(float x, float y) throws SVGException {
        if (x == 0 || y == 0) {
            throw new SVGOMException(SVGException.SVG_INVALID_VALUE_ERR, "");
        }
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.rotate(Math.atan2(y, x));
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#flipX()}.
     */
    public SVGMatrix flipX() {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.concatenate(FLIP_X_TRANSFORM);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#flipY()}.
     */
    public SVGMatrix flipY() {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.concatenate(FLIP_Y_TRANSFORM);
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#skewX(float)}.
     */
    public SVGMatrix skewX(float angle) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.concatenate
            (AffineTransform.getShearInstance(Math.tan(Math.PI * angle / 180),
                                              0));
        return new SVGOMMatrix(tr);
    }

    /**
     * Implements {@link SVGMatrix#skewY(float)}.
     */
    public SVGMatrix skewY(float angle) {
        AffineTransform tr = (AffineTransform)getAffineTransform().clone();
        tr.concatenate
            (AffineTransform.getShearInstance(0,
                                              Math.tan(Math.PI *
                                                       angle / 180)));
        return new SVGOMMatrix(tr);
    }
}
