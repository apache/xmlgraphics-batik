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

/**
 * This interface must be implemented and then registred as the
 * handler of a <code>TransformParser</code> instance in order to
 * be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface TransformListHandler {
    /**
     * Invoked when the tranform starts.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void startTransformList() throws ParseException;

    /**
     * Invoked when 'matrix(a, b, c, d, e, f)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void matrix(float a, float b, float c, float d, float e, float f)
	throws ParseException;

    /**
     * Invoked when 'rotate(theta)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void rotate(float theta) throws ParseException;

    /**
     * Invoked when 'rotate(theta, cx, cy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void rotate(float theta, float cx, float cy) throws ParseException;

    /**
     * Invoked when 'translate(tx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void translate(float tx) throws ParseException;

    /**
     * Invoked when 'translate(tx, ty)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void translate(float tx, float ty) throws ParseException;

    /**
     * Invoked when 'scale(sx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void scale(float sx) throws ParseException;

    /**
     * Invoked when 'scale(sx, sy)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void scale(float sx, float sy) throws ParseException;

    /**
     * Invoked when 'skewX(skx)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform 
     */
    void skewX(float skx) throws ParseException;

    /**
     * Invoked when 'skewY(sky)' has been parsed.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
    void skewY(float sky) throws ParseException;

    /**
     * Invoked when the transform ends.
     *
     * @exception ParseException if an error occured while processing
     * the transform
     */
    void endTransformList() throws ParseException;
}
