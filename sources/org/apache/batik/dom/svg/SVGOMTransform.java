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

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;

/**
 * This class is the implementation of
 * the SVGTransform interface.
 *
 * Create an identity SVGTransform
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGOMTransform extends AbstractSVGTransform {


    public SVGOMTransform(){
        super();
        affineTransform = new AffineTransform();
    }

    protected SVGMatrix createMatrix(){
        return new AbstractSVGMatrix(){
                protected AffineTransform getAffineTransform(){
                    return SVGOMTransform.this.affineTransform;
                }

                public void setA(float a) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setA(a);
                }
                public void setB(float b) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setB(b);
                }
                public void setC(float c) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setC(c);
                }
                public void setD(float d) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setD(d);
                }
                public void setE(float e) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setE(e);
                }
                public void setF(float f) throws DOMException {
                    SVGOMTransform.this.setType(SVG_TRANSFORM_MATRIX);
                    super.setF(f);
                }
            };
    }
}
