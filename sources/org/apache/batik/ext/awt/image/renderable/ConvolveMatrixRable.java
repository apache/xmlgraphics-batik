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

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Point;
import java.awt.image.Kernel;

import org.apache.batik.ext.awt.image.PadMode;

/**
 * Convolves an image with a convolution matrix.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface ConvolveMatrixRable extends FilterColorInterpolation {

    /**
     * Returns the source to be Convolved
     */
    public Filter getSource();

    /**
     * Sets the source to be Convolved
     * @param src image to Convolved.
     */
    public void setSource(Filter src);


    /**
     * Returns the Convolution Kernel in use
     */
    public Kernel getKernel();

    /**
     * Sets the Convolution Kernel to use.
     * @param k Kernel to use for convolution.
     */
    public void setKernel(Kernel k);

    /**
     * Returns the target point of the kernel (what pixel under the kernel
     * should be set to the result of convolution).
     */
    public Point getTarget();

    /**
     * Sets the target point of the kernel (what pixel under the kernel
     * should be set to the result of the convolution).
     */
    public void setTarget(Point pt);

    /**
     * Returns the shift value to apply to the result of convolution
     */
    public double getBias();

    /**
     * Sets the shift value to apply to the result of convolution
     */
    public void setBias(double bias);

    /**
     * Returns the current edge handling mode.
     */
    public PadMode getEdgeMode();

    /**
     * Sets the current edge handling mode.
     */
    public void setEdgeMode(PadMode edgeMode);

    /**
     * Returns the [x,y] distance in user space between kernel values
     */
    public double [] getKernelUnitLength();

    /**
     * Sets the [x,y] distance in user space between kernel values
     * If set to zero then one pixel in device space will be used.
     */
    public void setKernelUnitLength(double [] kernelUnitLength);

    /**
     * Returns false if the convolution should affect the Alpha channel
     */
    public boolean getPreserveAlpha();

    /**
     * Sets Alpha channel handling.
     * A value of False indicates that the convolution should apply to
     * the Alpha Channel
     */
    public void setPreserveAlpha(boolean preserveAlpha);
}

