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

import java.io.IOException;

/**
 * This class represents a parser with support for numbers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class NumberParser extends AbstractParser {

    /**
     * Parses the content of the buffer and converts it to a float.
     */
    protected float parseFloat() throws ParseException, IOException {
        int     mant     = 0;
        int     mantDig  = 0;
        boolean mantPos  = true;
        boolean mantRead = false;

        int     exp      = 0;
        int     expDig   = 0;
        int     expAdj   = 0;
        boolean expPos   = true;

        switch (current) {
        case '-':
            mantPos = false;
        case '+':
            current = reader.read();
        }

        m1: switch (current) {
        default:
            reportError("character.unexpected",
                        new Object[] { new Integer(current) });
            return 0f;

        case '.':
            break;

        case '0':
            mantRead = true;
            l: for (;;) {
                current = reader.read();
                switch (current) {
                case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                    break l;
                case '.': case 'e': case 'E':
                    break m1;
                default:
                    return 0f;
                case '0':
                }
            }

        case '1': case '2': case '3': case '4': 
        case '5': case '6': case '7': case '8': case '9': 
            mantRead = true;
            l: for (;;) {
                if (mantDig < 9) {
                    mantDig++;
                    mant = mant * 10 + (current - '0');
                } else {
                    expAdj++;
                }
                current = reader.read();
                switch (current) {
                default:
                    break l;
                case '0': case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                }                
            }
        }
        
        if (current == '.') {
            current = reader.read();
            m2: switch (current) {
            default:
            case 'e': case 'E':
                if (!mantRead) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return 0f;
                }
                break;

            case '0':
                if (mantDig == 0) {
                    l: for (;;) {
                        current = reader.read();
                        expAdj--;
                        switch (current) {
                        case '1': case '2': case '3': case '4': 
                        case '5': case '6': case '7': case '8': case '9': 
                            break l;
                        default:
                            if (!mantRead) {
                                return 0f;
                            }
                            break m2;
                        case '0':
                        }
                    }
                }
            case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                l: for (;;) {
                    if (mantDig < 9) {
                        mantDig++;
                        mant = mant * 10 + (current - '0');
                        expAdj--;
                    }
                    current = reader.read();
                    switch (current) {
                    default:
                        break l;
                    case '0': case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                    }
                }
            }
        }

        switch (current) {
        case 'e': case 'E':
            current = reader.read();
            switch (current) {
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                return 0f;
            case '-':
                expPos = false;
            case '+':
                current = reader.read();
                switch (current) {
                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return 0f;
                case '0': case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                }
            case '0': case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
            }
            
            en: switch (current) {
            case '0':
                l: for (;;) {
                    current = reader.read();
                    switch (current) {
                    case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                        break l;
                    default:
                        break en;
                    case '0':
                    }
                }

            case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                l: for (;;) {
                    if (expDig < 3) {
                        expDig++;
                        exp = exp * 10 + (current - '0');
                    }
                    current = reader.read();
                    switch (current) {
                    default:
                        break l;
                    case '0': case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                    }
                }
            }
        default:
        }

        if (!expPos) {
            exp = -exp;
        }
        exp += expAdj;
        if (!mantPos) {
            mant = -mant;
        }

        return buildFloat(mant, exp);
    }

    /**
     * Computes a float from mantissa and exponent.
     */
    public static float buildFloat(int mant, int exp) {
        if (exp < -125 || mant == 0) {
            return 0f;
        }

        if (exp >  128) {
            return (mant > 0)
                ? Float.POSITIVE_INFINITY
                : Float.NEGATIVE_INFINITY;
        }

        if (exp == 0) {
            return mant;
        }
            
        if (mant >= 1 << 26) {
            mant++;  // round up trailing bits if they will be dropped.
        }

        return (exp > 0) ? mant * pow10[exp] : mant / pow10[-exp];
    }

    /**
     * Array of powers of ten.
     */
    private static final float pow10[] = new float [128];
    static {
        for (int i = 0; i < pow10.length; i++) {
            pow10[i] = (float)Math.pow(10, i);
        }
    }
}
