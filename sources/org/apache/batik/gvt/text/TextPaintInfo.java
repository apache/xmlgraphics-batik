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

package org.apache.batik.gvt.text;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id$
 */
public class TextPaintInfo {
    public Paint     fillPaint;
    public Paint     strokePaint;
    public Stroke    strokeStroke;
    public Composite composite;
    
    public Paint  underlinePaint;
    public Paint  underlineStrokePaint;
    public Stroke underlineStroke;
    
    public Paint  overlinePaint;
    public Paint  overlineStrokePaint;
    public Stroke overlineStroke;
    
    public Paint  strikethroughPaint;
    public Paint  strikethroughStrokePaint;
    public Stroke strikethroughStroke;
    
    public TextPaintInfo() { }
    
    public TextPaintInfo(TextPaintInfo pi) {
        set(pi);
    }

    public void set(TextPaintInfo pi) {
        if (pi == null) {
            this.fillPaint    = null;
            this.strokePaint  = null;
            this.strokeStroke = null;
            this.composite    = null;
        
            this.underlinePaint       = null;
            this.underlineStrokePaint = null;
            this.underlineStroke      = null;
        
            this.overlinePaint       = null;
            this.overlineStrokePaint = null;
            this.overlineStroke      = null;
        
            this.strikethroughPaint       = null;
            this.strikethroughStrokePaint = null;
            this.strikethroughStroke      = null;
        } else {
            this.fillPaint    = pi.fillPaint;
            this.strokePaint  = pi.strokePaint;
            this.strokeStroke = pi.strokeStroke;
            this.composite    = pi.composite;
            
            this.underlinePaint       = pi.underlinePaint;
            this.underlineStrokePaint = pi.underlineStrokePaint;
            this.underlineStroke      = pi.underlineStroke;
            
            this.overlinePaint       = pi.overlinePaint;
            this.overlineStrokePaint = pi.overlineStrokePaint;
            this.overlineStroke      = pi.overlineStroke;

            this.strikethroughPaint       = pi.strikethroughPaint;
            this.strikethroughStrokePaint = pi.strikethroughStrokePaint;
            this.strikethroughStroke      = pi.strikethroughStroke;
        }
    }

    public static boolean equivilent(TextPaintInfo tpi1, TextPaintInfo tpi2) {
        if (tpi1 == null) {
            if (tpi2 == null) return true;
            return false;
        } else if (tpi2 == null) return false;

        if ((tpi1.fillPaint == null) != (tpi2.fillPaint == null))
            return false;
        
        boolean tpi1Stroke = ((tpi1.strokePaint != null) &&
                              (tpi1.strokeStroke != null));

        boolean tpi2Stroke = ((tpi2.strokePaint != null) &&
                              (tpi2.strokeStroke != null));
        return (tpi1Stroke == tpi2Stroke);

    }
}
