/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.awt.Point;
import java.awt.RenderingHints;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.ComponentTransferRed;

import org.apache.batik.ext.awt.image.ComponentTransferFunction;
import org.apache.batik.ext.awt.image.TransferFunction;
import org.apache.batik.ext.awt.image.IdentityTransfer;
import org.apache.batik.ext.awt.image.TableTransfer;
import org.apache.batik.ext.awt.image.DiscreteTransfer;
import org.apache.batik.ext.awt.image.LinearTransfer;
import org.apache.batik.ext.awt.image.GammaTransfer;

/**
 * This class implements the interface expected from a component 
 * transfer operation.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ComponentTransferRable8Bit 
    extends AbstractColorInterpRable
    implements ComponentTransferRable {

    public static final int ALPHA = 0;
    public static final int RED   = 1;
    public static final int GREEN = 2;
    public static final int BLUE  = 3;

    /**
     * Array of transfer functions. There are four
     * elements. Elements may be null.
     */
    private ComponentTransferFunction 
        functions[] = new ComponentTransferFunction[4];

    /**
     * Array of transfer functions. Elements are computed
     * lazily.
     */
    private TransferFunction 
        txfFunc[] = new TransferFunction[4];

    public ComponentTransferRable8Bit(Filter src,
                                      ComponentTransferFunction alphaFunction,
                                      ComponentTransferFunction redFunction,
                                      ComponentTransferFunction greenFunction,
                                      ComponentTransferFunction blueFunction){
        super(src, null);
        setAlphaFunction(alphaFunction);
        setRedFunction(redFunction);
        setGreenFunction(greenFunction);
        setBlueFunction(blueFunction);
    }

    /**
     * Sets the source of the blur operation
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns the source of the blur operation
     */
    public Filter getSource(){
        return (Filter)getSources().get(0);
    }

    /**
     * Returns the transfer function for the alpha channel
     */
    public ComponentTransferFunction getAlphaFunction(){
        return functions[ALPHA];
    }

    /**
     * Sets the transfer function for the alpha channel
     */
    public void setAlphaFunction(ComponentTransferFunction alphaFunction){
        functions[ALPHA] = alphaFunction;
        txfFunc[ALPHA] = null;
    }

    /**
     * Returns the transfer function for the red channel
     */
    public ComponentTransferFunction getRedFunction(){
        return functions[RED];
    }

    /**
     * Sets the transfer function for the red channel
     */
    public void setRedFunction(ComponentTransferFunction redFunction){
        functions[RED] = redFunction;
        txfFunc[RED] = null;
    }

    /**
     * Returns the transfer function for the green channel
     */
    public ComponentTransferFunction getGreenFunction(){
        return functions[GREEN];
    }

    /**
     * Sets the transfer function for the green channel
     */
    public void setGreenFunction(ComponentTransferFunction greenFunction){
        functions[GREEN] = greenFunction;
        txfFunc[GREEN] = null;
    }

    /**
     * Returns the transfer function for the blue channel
     */
    public ComponentTransferFunction getBlueFunction(){
        return functions[BLUE];
    }

    /**
     * Sets the transfer function for the blue channel
     */
    public void setBlueFunction(ComponentTransferFunction blueFunction){
        functions[BLUE] = blueFunction;
        txfFunc[BLUE] = null;
    }

    public RenderedImage createRendering(RenderContext rc){
        //
        // Get source's rendered image
        //
        RenderedImage srcRI = getSource().createRendering(rc);

        if(srcRI == null)
            return null;

        return new ComponentTransferRed(convertSourceCS(srcRI), 
                                        getTransferFunctions(),
                                        rc.getRenderingHints());
    }

    /**
     * Builds an array of transfer functions for the 
     * ComponentTransferOp.
     */
    private TransferFunction[] getTransferFunctions(){
        //
        // Copy array to avoid multi-thread conflicts on
        // array access.
        //
        TransferFunction txfFunc[] = new TransferFunction[4];
        System.arraycopy(this.txfFunc, 0, txfFunc, 0, 4);

        ComponentTransferFunction functions[];
        functions = new ComponentTransferFunction[4];
        System.arraycopy(this.functions, 0, functions, 0, 4);

        for(int i=0; i<4; i++){
            if(txfFunc[i] == null){
                txfFunc[i] = getTransferFunction(functions[i]);
                synchronized(this.functions){
                    if(this.functions[i] == functions[i]){
                        this.txfFunc[i] = txfFunc[i];
                    }
                }
            }
        }

        return txfFunc;
    }

    /**
     * Converts a ComponentTransferFunction to a TransferFunction
     */
    private static TransferFunction getTransferFunction
        (ComponentTransferFunction function){

        TransferFunction txfFunc = null;
        if(function == null){
            txfFunc = new IdentityTransfer();
        }
        else{
            switch(function.getType()){
            case ComponentTransferFunction.IDENTITY:
                txfFunc = new IdentityTransfer();
                break;
            case ComponentTransferFunction.TABLE:
                txfFunc = new TableTransfer(tableFloatToInt(function.getTableValues()));
                break;
            case ComponentTransferFunction.DISCRETE:
                txfFunc = new DiscreteTransfer(tableFloatToInt(function.getTableValues()));
                break;
            case ComponentTransferFunction.LINEAR:
                txfFunc = new LinearTransfer(function.getSlope(),
                                             function.getIntercept());
                break;
            case ComponentTransferFunction.GAMMA:
                txfFunc = new GammaTransfer(function.getAmplitude(),
                                            function.getExponent(),
                                            function.getOffset());
                break;
            default:
                // Should never happen
                throw new Error();
            }
        }

        return txfFunc;
    }

    /**
     * Converts a intensity values (0-1) to code values (0-255)
     */
    private static int[] tableFloatToInt(float tableValues[]){
        int values[] = new int[tableValues.length];
        for(int i=0; i<tableValues.length; i++){
            values[i] = (int)(tableValues[i]*255f);
        }

        return values;
    }

}
