/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.g2d;

import java.awt.geom.AffineTransform;

/**
 * Contains a description of an elementary transform stack element,
 * such as a rotate or translate. A transform stack element has a
 * type and a value, which is an array of double values.<br>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TransformStackElement implements Cloneable{
    /**
     * Transform type
     */
    private TransformType type;

    /**
     * Value
     */
    private double transformParameters[];

    /**
     * @param type transform type
     * @param transformParameters parameters for transform
     */
    private TransformStackElement(TransformType type,
                                  double transformParameters[]){
        this.type = type;
        this.transformParameters = transformParameters;
    }

    /**
     * @return an object which is a deep copy of this one
     */
    public Object clone() {
        double transformParameters[] = new double[this.transformParameters.length];
        System.arraycopy(this.transformParameters, 0, transformParameters, 0, transformParameters.length);
        return new TransformStackElement(type, transformParameters);
    }

    /**
     * @return a String describing this object
     */
    public String toString(){
        // return SVGTransform.convertTransform(this);
        return super.toString();
    }

    /*
     * Factory methods
     */

    public static TransformStackElement createTranslateElement(double tx, double ty){
        return new TransformStackElement(TransformType.TRANSLATE, new double[]{ tx, ty });
    }

    public static TransformStackElement createRotateElement(double theta){
        return new TransformStackElement(TransformType.ROTATE, new double[]{ theta });
    }

    public static TransformStackElement createScaleElement(double scaleX, double scaleY){
        return new TransformStackElement(TransformType.SCALE, new double[]{ scaleX, scaleY });
    }

    public static TransformStackElement createShearElement(double shearX, double shearY){
        return new TransformStackElement(TransformType.SHEAR, new double[]{ shearX, shearY });
    }

    public static TransformStackElement createGeneralTransformElement(AffineTransform txf){
        double matrix[] = new double[6];
        txf.getMatrix(matrix);
        return new TransformStackElement(TransformType.GENERAL, matrix);
    }

    /**
     * @return array of values containing this transform element's parameters
     */
    public double[] getTransformParameters(){
        return transformParameters;
    }

    /**
     * @return this transform type
     */
    public TransformType getType(){
        return type;
    }

    /*
     * Concatenation utility. Requests this transform stack element
     * to concatenate with the input stack element. Only elements
     * of the same types are concatenated. For example, if this
     * element represents a translation, it will concatenate with
     * another translation, but not with any other kind of
     * stack element.
     * @param stackElement element to be concatenated with this one.
     * @return true if the input stackElement was concatenated with
     *         this one. False otherwise.
     */
    public boolean concatenate(TransformStackElement stackElement){
        boolean canConcatenate = false;

        if(type.toInt() == stackElement.type.toInt()){
            canConcatenate = true;
            switch(type.toInt()){
            case TransformType.TRANSFORM_TRANSLATE:
                transformParameters[0] += stackElement.transformParameters[0];
                transformParameters[1] += stackElement.transformParameters[1];
                break;
            case TransformType.TRANSFORM_ROTATE:
                transformParameters[0] += stackElement.transformParameters[0];
                break;
            case TransformType.TRANSFORM_SCALE:
                transformParameters[0] *= stackElement.transformParameters[0];
                transformParameters[1] *= stackElement.transformParameters[1];
                break;
            default:
                canConcatenate = false;
            }
        }

        return canConcatenate;
    }
}
