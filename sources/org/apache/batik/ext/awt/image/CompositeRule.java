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
 * This is a typesafe enumeration of the standard Composite rules for
 * the CompositeRable operation. (over, in, out, atop, xor, arith)
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public final class CompositeRule implements java.io.Serializable {

    /** Porter-Duff src over rule, also used for feBlend <tt>normal</tt>. */
    public static final int RULE_OVER = 1;

    /** Porter-Duff src in rule. */
    public static final int RULE_IN = 2;

    /** Porter-Duff src out rule. */
    public static final int RULE_OUT = 3;

    /** Porter-Duff src atop rule. */
    public static final int RULE_ATOP = 4;

    /** Porter-Duff src xor rule. */
    public static final int RULE_XOR = 5;

    /** Arithmatic rule 'out = k1*i1*i2 + k2*i1 + k3*i2 + k4'. */
    public static final int RULE_ARITHMETIC = 6;

    /** SVG feBlend Multiply rule */
    public static final int RULE_MULTIPLY = 7;
        
    /** SVG feBlend Screen rule */
    public static final int RULE_SCREEN = 8;
        
    /** SVG feBlend Darken rule */
    public static final int RULE_DARKEN = 9;
        
    /** SVG feBlend Lighten rule */
    public static final int RULE_LIGHTEN = 10;
        

      /**
       * Porter-Duff Source Over Destination rule. The source is
       * composited over the destination.<pre>
       *
       *  Fs = 1 and Fd = (1-As), thus:
       *
       *        Cd = Cs + Cd*(1-As)
       *        Ad = As + Ad*(1-As)</pre>
       * </pre>
       */
    public static final CompositeRule OVER = new CompositeRule(RULE_OVER);

      /**
       * Porter-Duff Source In Destination rule. The part of the
       * source lying inside of the destination replaces the destination.<pre>
       *
       *  Fs = Ad and Fd = 0, thus:
       *
       *        Cd = Cs*Ad
       *        Ad = As*Ad
       * </pre>
       */
    public static final CompositeRule IN = new CompositeRule(RULE_IN);

      /**
       * Porter-Duff Source Out Destination rule. The part of the
       * source lying outside of the destination replaces the destination.<pre>
       *
       *  Fs = (1-Ad) and Fd = 0, thus:
       *
       *        Cd = Cs*(1-Ad)
       *        Ad = As*(1-Ad)
       * </pre>
       */
    public static final CompositeRule OUT = new CompositeRule(RULE_OUT);

      /**
       * Porter-Duff Source Atop Destination rule. The part of the
       * source lying inside of the destination replaces the destination,
       * destination remains outside of source.<pre>
       *
       *  Fs = Ad and Fd = (1-As), thus:
       *
       *        Cd = Cs*Ad + Cd*(1-As)
       *        Ad = As*Ad + Ad*(1-As)
       * </pre>
       */
    public static final CompositeRule ATOP = new CompositeRule(RULE_ATOP);

      /**
       * Xor rule. The source and destination are Xor'ed togeather.<pre>
       *
       *  Fs = (1-Ad) and Fd = (1-As), thus:
       *
       *        Cd = Cs*(1-Ad) + Cd*(1-As)
       *        Ad = As*(1-Ad) + Ad*(1-As)
       * </pre>
       */
    public static final CompositeRule XOR = new CompositeRule(RULE_XOR);

      /**
       * Factory to create artithmatic CompositeRules.
       * 'out = k1*i1*i2 + k2*i1 + k3*i2 + k4'
       * Note that arithmatic CompositeRules are not singletons.
       */
    public static CompositeRule ARITHMETIC
        (float k1, float k2, float k3, float k4) {
        return new CompositeRule(k1, k2, k3, k4);
    }

      /**
       * FeBlend Multiply rule. <pre>
       *
       *        Cd = Cs*(1-Ad) + Cd*(1-As) + Cs*Cd
       *        Ad = 1 - (1-Ad)*(1-As)
       * </pre>
       */
    public static final CompositeRule MULTIPLY = 
        new CompositeRule(RULE_MULTIPLY);

      /**
       * FeBlend Screen rule. <pre>
       *
       *        Cd = Cs + Cd - Cs*Cd
       *        Ad = 1 - (1-Ad)*(1-As)
       * </pre>
       */
    public static final CompositeRule SCREEN = 
        new CompositeRule(RULE_SCREEN);

      /**
       * FeBlend Darken rule. <pre>
       *
       *        Cd = Min(Cs*(1-Ad) + Cd,
       *                 Cd*(1-As) + Cs)
       *        Ad = 1 - (1-Ad)*(1-As)
       * </pre>
       */
    public static final CompositeRule DARKEN = 
        new CompositeRule(RULE_DARKEN);


      /**
       * FeBlend Lighten rule. <pre>
       *
       *        Cd = Max(Cs*(1-Ad) + Cd,
       *                 Cd*(1-As) + Cs)
       *        Ad = 1 - (1-Ad)*(1-As)
       * </pre>
       */
    public static final CompositeRule LIGHTEN = 
        new CompositeRule(RULE_LIGHTEN);


    /**
     * Returns the type of this composite rule
     */
    public int getRule() {
        return rule;
    }

      /**
       * The composite rule for this object.
       */
    private int rule;

      /* Arithmatic constants, only used for RULE_ARITHMETIC */
    private float k1, k2, k3, k4;

    private CompositeRule(int rule) {
        this.rule = rule;
    }

    private CompositeRule(float k1, float k2, float k3, float k4) {
        rule = RULE_ARITHMETIC;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
    }

    public float [] getCoefficients() {
        if (rule != RULE_ARITHMETIC)
            return null;

        return new float[] {k1, k2, k3, k4};
    }

    /**
     * This is called by the serialization code before it returns
     * an unserialized object. To provide for unicity of
     * instances, the instance that was read is replaced by its
     * static equivalent. See the serialiazation specification for
     * further details on this method's logic.
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        switch(rule){
        case RULE_OVER:
            return OVER;
        case RULE_IN:
            return IN;
        case RULE_OUT:
            return OUT;
        case RULE_ATOP:
            return ATOP;
        case RULE_XOR:
            return XOR;
        case RULE_ARITHMETIC:
            return this;
        case RULE_MULTIPLY:
            return MULTIPLY;
        case RULE_SCREEN:
            return SCREEN;
        case RULE_DARKEN:
            return DARKEN;
        case RULE_LIGHTEN:
            return LIGHTEN;
        default:
            throw new Error("Unknown Composite Rule type");
        }
    }

    /**
     * This is called by the serialization code before it returns
     * an unserialized object. To provide for unicity of
     * instances, the instance that was read is replaced by its
     * static equivalent. See the serialiazation specification for
     * further details on this method's logic.
     */
    public String toString() {
        switch(rule){
        case RULE_OVER:
            return "[CompositeRule: OVER]";
        case RULE_IN:
            return "[CompositeRule: IN]";
        case RULE_OUT:
            return "[CompositeRule: OUT]";
        case RULE_ATOP:
            return "[CompositeRule: ATOP]";
        case RULE_XOR:
            return "[CompositeRule: XOR]";
        case RULE_ARITHMETIC:
            return ("[CompositeRule: ARITHMATIC k1:" +
                    k1 + " k2: " + k2 + " k3: " + k3 + " k4: " + k4 + "]");
        case RULE_MULTIPLY:
            return "[CompositeRule: MULTIPLY]";
        case RULE_SCREEN:
            return "[CompositeRule: SCREEN]";
        case RULE_DARKEN:
            return "[CompositeRule: DARKEN]";
        case RULE_LIGHTEN:
            return "[CompositeRule: LIGHTEN]";
        default:
            throw new Error("Unknown Composite Rule type");
        }
    }

}
