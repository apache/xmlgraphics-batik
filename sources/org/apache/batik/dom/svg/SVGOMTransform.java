/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
