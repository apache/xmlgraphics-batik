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

import java.awt.geom.Rectangle2D;

import org.apache.batik.ext.awt.image.Light;

/**
 * This filter primitive lights an image using the alpha channel as a bump map. 
 * The resulting image is an RGBA opaque image based on the light color
 * with alpha = 1.0 everywhere. The lighting calculation follows the standard diffuse
 * component of the Phong lighting model. The resulting image depends on the light color, 
 * light position and surface geometry of the input bump map.
 *
 * This filter follows the specification of the feDiffuseLighting filter in 
 * the SVG 1.0 specification.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface DiffuseLightingRable extends FilterColorInterpolation {
    /**
     * Returns the source to be filtered
     */
    public Filter getSource();

    /**
     * Sets the source to be filtered
     */
    public void setSource(Filter src);

    /**
     * @return Light object used for the diffuse lighting
     */
    public Light getLight();

    /**
     * @param New Light object
     */
    public void setLight(Light light);

    /**
     * @return surfaceScale
     */
    public double getSurfaceScale();

    /**
     * Sets the surface scale
     */
    public void setSurfaceScale(double surfaceScale);

    /**
     * @return diffuse constant, or kd.
     */
    public double getKd();

    /**
     * Sets the diffuse constant, or kd
     */
    public void setKd(double kd);

    /**
     * @return the litRegion for this filter
     */
    public Rectangle2D getLitRegion();

    /**
     * Sets the litRegion for this filter
     */
    public void setLitRegion(Rectangle2D litRegion);

    /**
     * Returns the min [dx,dy] distance in user space for evalutation of 
     * the sobel gradient.
     */
    public double [] getKernelUnitLength();

    /**
     * Sets the min [dx,dy] distance in user space for evaluation of the 
     * sobel gradient. If set to zero or null then device space will be used.
     */
    public void setKernelUnitLength(double [] kernelUnitLength);
}

