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

package org.apache.batik.parser;

import java.awt.geom.AffineTransform;
import java.io.Reader;

/**
 * This class provides an implementation of the PathHandler that initializes
 * an AffineTransform from the value of a 'transform' attribute.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AWTTransformProducer implements TransformListHandler {
    /**
     * The value of the current affine transform.
     */
    protected AffineTransform affineTransform;

    /**
     * Utility method for creating an AffineTransform.
     * @param r The reader used to read the transform specification.
     */
    public static AffineTransform createAffineTransform(Reader r)
        throws ParseException {
        TransformListParser p = new TransformListParser();
        AWTTransformProducer th = new AWTTransformProducer();

        p.setTransformListHandler(th);
        p.parse(r);

        return th.getAffineTransform();
    }

    /**
     * Utility method for creating an AffineTransform.
     * @param r The reader used to read the transform specification.
     */
    public static AffineTransform createAffineTransform(String s)
        throws ParseException {
        TransformListParser p = new TransformListParser();
        AWTTransformProducer th = new AWTTransformProducer();

        p.setTransformListHandler(th);
        p.parse(s);

        return th.getAffineTransform();
    }

    /**
     * Returns the AffineTransform object initialized during the last parsing.
     * @return the transform or null if this handler has not been used by
     *         a parser.
     */
    public AffineTransform getAffineTransform() {
        return affineTransform;
    }

    /**
     * Implements {@link TransformListHandler#startTransformList()}.
     */
    public void startTransformList() throws ParseException {
        affineTransform = new AffineTransform();
    }

    /**
     * Implements {@link
     * TransformListHandler#matrix(float,float,float,float,float,float)}.
     */
    public void matrix(float a, float b, float c, float d, float e, float f)
        throws ParseException {
        affineTransform.concatenate(new AffineTransform(a, b, c, d, e, f));
    }

    /**
     * Implements {@link TransformListHandler#rotate(float)}.
     */
    public void rotate(float theta) throws ParseException {
        affineTransform.concatenate
            (AffineTransform.getRotateInstance(Math.PI * theta / 180));
    }

    /**
     * Implements {@link TransformListHandler#rotate(float,float,float)}.
     */
    public void rotate(float theta, float cx, float cy) throws ParseException {
        AffineTransform at
            = AffineTransform.getRotateInstance(Math.PI * theta / 180, cx, cy);
        affineTransform.concatenate(at);
    }

    /**
     * Implements {@link TransformListHandler#translate(float)}.
     */
    public void translate(float tx) throws ParseException {
        AffineTransform at = AffineTransform.getTranslateInstance(tx, 0);
        affineTransform.concatenate(at);
    }

    /**
     * Implements {@link TransformListHandler#translate(float,float)}.
     */
    public void translate(float tx, float ty) throws ParseException {
        AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
        affineTransform.concatenate(at);
    }

    /**
     * Implements {@link TransformListHandler#scale(float)}.
     */
    public void scale(float sx) throws ParseException {
        affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sx));
    }

    /**
     * Implements {@link TransformListHandler#scale(float,float)}.
     */
    public void scale(float sx, float sy) throws ParseException {
        affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    /**
     * Implements {@link TransformListHandler#skewX(float)}.
     */
    public void skewX(float skx) throws ParseException {
        affineTransform.concatenate
            (AffineTransform.getShearInstance(Math.tan(Math.PI * skx / 180),
                                              0));
    }

    /**
     * Implements {@link TransformListHandler#skewY(float)}.
     */
    public void skewY(float sky) throws ParseException {
        affineTransform.concatenate
            (AffineTransform.getShearInstance(0,
                                              Math.tan(Math.PI * sky / 180)));
    }

    /**
     * Implements {@link TransformListHandler#endTransformList()}.
     */
    public void endTransformList() throws ParseException {
    }
}
