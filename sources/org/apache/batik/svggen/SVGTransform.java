/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformType;
import org.apache.batik.ext.awt.g2d.TransformStackElement;

/**
 * Utility class that converts a GraphicContext transform stack
 * into an SVG transform attribute.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGTransform extends AbstractSVGConverter{
    /**
     * Ratio used to convert radians to degrees
     */
    private static double radiansToDegrees = 180.0 / Math.PI;
    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        return new SVGTransformDescriptor(toSVGTransform(gc));
    }

    /**
     * @param gc GraphicContext whose transform stack should be converted
     *           to SVG.
     * @return the value of an SVG attribute equivalent to the input
     *         GraphicContext's transform stack.
     */
    public static String toSVGTransform(GraphicContext gc){
        return toSVGTransform(gc.getTransformStack());
    }

    /**
     * This method tries to collapse the transform stack into an SVG
     * string as compact as possible while still conveying the semantic
     * of the stack. Successive stack elements of the same kind (e.g., two
     * successive transforms or scales) are collasped into a single element.
     *
     * @param transformStack sequence of transform that should
     *        be converted to an SVG transform attribute equivalent
     */
    public static String toSVGTransform(TransformStackElement transformStack[]){
        StringBuffer transformStackBuffer = new StringBuffer();

        //
        // Append transforms in the stack
        //
        int nTransforms = transformStack.length;
        boolean canConcatenate = false;
        int i = 0, j = 0;
        while(i<nTransforms){
            // Clone current element and try to concatenate
            // elements on top of it
            TransformStackElement element = (TransformStackElement)transformStack[i].clone();
            canConcatenate = true;
            for(j= (i+1); j < nTransforms; j++){
                canConcatenate = element.concatenate(transformStack[j]);
                if(!canConcatenate)
                    break;
            }
            i = j;
            transformStackBuffer.append(convertTransform(element));
            transformStackBuffer.append(SPACE);
        }

        String transformValue = transformStackBuffer.toString().trim();
        return transformValue;
    }

    /**
     * Converts an AffineTransform to an SVG transform string
     */
    static String convertTransform(TransformStackElement transformElement){
        StringBuffer transformString = new StringBuffer();
        double transformParameters[] = transformElement.getTransformParameters();
        switch(transformElement.getType().toInt()){
        case TransformType.TRANSFORM_TRANSLATE:
            if(transformParameters[0] != 0 || transformParameters[1] != 0){
                transformString.append(TRANSFORM_TRANSLATE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_ROTATE:
            if(transformParameters[0] != 0){
                transformString.append(TRANSFORM_ROTATE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(radiansToDegrees*transformParameters[0]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_SCALE:
            if(transformParameters[0] != 1 || transformParameters[1] != 1){
                transformString.append(TRANSFORM_SCALE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_SHEAR:
            transformString.append(TRANSFORM_MATRIX);
            transformString.append(OPEN_PARENTHESIS);
            transformString.append(1);
            transformString.append(COMMA);
            transformString.append(doubleString(transformParameters[1]));
            transformString.append(COMMA);
            transformString.append(doubleString(transformParameters[0]));
            transformString.append(COMMA);
            transformString.append(1);
            transformString.append(COMMA);
            transformString.append(0);
            transformString.append(COMMA);
            transformString.append(0);
            transformString.append(CLOSE_PARENTHESIS);
            break;
        case TransformType.TRANSFORM_GENERAL:
            if(!isIdentity(transformParameters)){
                transformString.append(TRANSFORM_MATRIX);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[2]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[3]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[4]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[5]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        default:
            // This should never happen. If it does, there is a
            // serious error.
            throw new Error();
        }

        return transformString.toString();
    }

    /**
     * Examines the input array to find if it represents an
     * identity transform.
     */
    private static boolean isIdentity(double matrix[]){
        if(matrix.length != 6)
            throw new Error();

        return (matrix[0] == 1 && matrix[2] == 0 && matrix[4] == 0 &&
                matrix[1] == 0 && matrix[3] == 1 && matrix[5] == 0);
    }
}
