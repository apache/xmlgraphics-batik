/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.ParseException;

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
    protected float parseFloat()
        throws NumberFormatException,
               ParseException {
        // readNumber();
        // float ret = Float.parseFloat(getBufferContent());

        int mant       =0;
        int mantDig    =0;
        boolean mantPos=true;
        boolean mantRead=false;
        int exp        =0;
        int expDig     =0;
        int expAdj     =0;
        boolean expPos =true;

        boolean eRead     = false;
        boolean eJustRead = false;
        boolean first     = true;
        boolean done      = false;
        boolean dotRead   = false;

        while (!done) {
            switch (current) {
            case '0': case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                if (!eRead) {
                    // Still creating mantisa
                    mantRead = true;
                    // Only keep first 9 digits rest won't count anyway...
                    if (mantDig >= 9) {
                        if (!dotRead)
                            expAdj++;
                        break;
                    }
                    if (dotRead) expAdj--;
                    if ((mantDig != 0) || (current != '0')) {
                                // Ignore leading zeros.
                        mantDig++;
                        mant = mant*10+(current-'0');
                    }
                } else {
                    // Working on exp.
                    if (expDig >= 3) break;
                    if ((expDig != 0) || (current != '0')) {
                                // Ignore leading zeros.
                        expDig++;
                        expDig = expDig*10+(current-'0');
                    }
                }
                eJustRead = false;
                break;
            case 'e': case 'E':
                if (eRead) {
                    done = true;
                    break;
                }
                eJustRead = true;
                eRead = true;
                break;
            case '.':
                if (eRead || dotRead) {
                    done=true;
                    break;
                }
                dotRead = true;
                break;
            case '+': 
                if ((!first) && (!eJustRead))
                    done=true;
                eJustRead = false;
                break;
            case '-':
                if      (first)     mantPos = false;
                else if (eJustRead) expPos  = false;
                else                done    = true;
                eJustRead = false;
                break;

            case 10:
                line++;
                column =1;
                done = true;
                break;
            default:
                done=true;
                break;
            }
            if (!done) {
                first = false;
                if ((position == count) && (!fillBuffer())) {
                    current = -1;
                    break;
                }
                current = buffer[position++];
                column++;
            }
        }
        if (!mantRead)
            throw new NumberFormatException
                ("No digits where number expected '" + ((char)current) + "'");

        if (!expPos) exp = -exp;
        exp += expAdj; // account for digits after 'dot'.
        if (!mantPos) mant = -mant;

        return buildFloat(mant, exp);
    }

    public static float buildFloat(int mant, int exp) {
        if ((exp < -125) || (mant==0)) return 0f;
        if (exp >  128) {
            if (mant > 0) return Float.POSITIVE_INFINITY;
            else          return Float.NEGATIVE_INFINITY;
        }

        if (exp == 0) return mant;
            
        if (mant >= 1<<26)
            mant++;  // round up trailing bits if they will be dropped.

        if (exp >  0) return mant*pow10[exp];
        else          return mant/pow10[-exp];
    }

    /**
     * Array of powers of ten.
     */
    private static final float pow10[] = new float [128];
    static {
      for (int i=0; i<pow10.length; i++) {
        pow10[i] = (float)Math.pow(10, i);
      }
    };
}
