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

package org.apache.batik.ext.awt.image;



/**
 * This class implements the interface expected from a component 
 * transfer function.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteComponentTransferFunction implements ComponentTransferFunction {
    private int type;
    private float slope;
    private float[] tableValues;
    private float intercept;
    private float amplitude;
    private float exponent;
    private float offset;

    /**
     * Instances should be created through the various
     * factory methods.
     */
    private ConcreteComponentTransferFunction(){
    }

    /**
     * Returns an instance initialized as an identity 
     * transfer function
     */
    public static ComponentTransferFunction getIdentityTransfer(){
        ConcreteComponentTransferFunction f = new ConcreteComponentTransferFunction();
        f.type = IDENTITY;
        return f;
    }

    /**
     * Returns a table transfer function
     */
    public static ComponentTransferFunction 
        getTableTransfer(float tableValues[]){
        ConcreteComponentTransferFunction f = new ConcreteComponentTransferFunction();
        f.type = TABLE;
        
        if(tableValues == null){
            throw new IllegalArgumentException();
        }

        if(tableValues.length < 2){
            throw new IllegalArgumentException();
        }

        f.tableValues = new float[tableValues.length];
        System.arraycopy(tableValues, 0, 
                         f.tableValues, 0, 
                         tableValues.length);

        return f;
    }

    /**
     * Returns a discrete transfer function
     */
    public static ComponentTransferFunction
        getDiscreteTransfer(float tableValues[]){
        ConcreteComponentTransferFunction f = new ConcreteComponentTransferFunction();
        f.type = DISCRETE;
        
        if(tableValues == null){
            throw new IllegalArgumentException();
        }

        if(tableValues.length < 2){
            throw new IllegalArgumentException();
        }

        f.tableValues = new float[tableValues.length];
        System.arraycopy(tableValues, 0, 
                         f.tableValues, 0, 
                         tableValues.length);

        return f;
    }

    /**
     * Returns a linear transfer function
     */
    public static ComponentTransferFunction
        getLinearTransfer(float slope, float intercept){
        ConcreteComponentTransferFunction f = new ConcreteComponentTransferFunction();
        f.type = LINEAR;
        f.slope = slope;
        f.intercept = intercept;

        return f;
    }

    /**
     * Returns a gamma function
     */
    public static ComponentTransferFunction
        getGammaTransfer(float amplitude,
                         float exponent,
                         float offset){
        ConcreteComponentTransferFunction f = new ConcreteComponentTransferFunction();
        f.type = GAMMA;
        f.amplitude = amplitude;
        f.exponent = exponent;
        f.offset = offset;

        return f;
    }
        
    /**
     * Returns the type of this transfer function
     */
    public int getType(){
        return type;
    }

    /**
     * Returns the slope value for this transfer function
     */
    public float getSlope(){
        return slope;
    }

    /**
     * Returns the table values for this transfer function
     */
    public float[] getTableValues(){
        return tableValues;
    }

    /**
     * Returns the intercept value for this transfer function
     */
    public float getIntercept(){
        return intercept;
    }

    /**
     * Returns the amplitude value for this transfer function
     */
    public float getAmplitude(){
        return amplitude;
    }

    /**
     * Returns the exponent value for this transfer function
     */
    public float getExponent(){
        return exponent;
    }

    /**
     * Returns the offset value for this transfer function
     */
    public float getOffset(){
        return offset;
    }
}

