/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
     * Implements {@link SVGMatrix#getA(float)}.
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
     * Implements {@link SVGMatrix#getB(float)}.
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
     * Implements {@link SVGMatrix#getC(float)}.
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
     * Implements {@link SVGMatrix#getD(float)}.
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
     * Implements {@link SVGMatrix#getE(float)}.
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
     * Implements {@link SVGMatrix#getF(float)}.
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
            class Ex extends SVGException {
                Ex(String str) {
                    super(SVGException.SVG_MATRIX_NOT_INVERTABLE, str);
                }
            }
            throw new Ex(e.getMessage());
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
        throw new InternalError("!!! rotateFromVector");
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
     * Implements {@link SVGMatrix#skewY()}.
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
