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

package org.apache.batik.css.parser;

import org.w3c.css.sac.LexicalUnit;

/**
 * This class implements the {@link LexicalUnit} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class CSSLexicalUnit implements LexicalUnit {

         public static final String UNIT_TEXT_CENTIMETER  = "cm";
         public static final String UNIT_TEXT_DEGREE      = "deg";
         public static final String UNIT_TEXT_EM          = "em";
         public static final String UNIT_TEXT_EX          = "ex";
         public static final String UNIT_TEXT_GRADIAN     = "grad";
         public static final String UNIT_TEXT_HERTZ       = "Hz";
         public static final String UNIT_TEXT_INCH        = "in";
         public static final String UNIT_TEXT_KILOHERTZ   = "kHz";
         public static final String UNIT_TEXT_MILLIMETER  = "mm";
         public static final String UNIT_TEXT_MILLISECOND = "ms";
         public static final String UNIT_TEXT_PERCENTAGE  = "%";
         public static final String UNIT_TEXT_PICA        = "pc";
         public static final String UNIT_TEXT_PIXEL       = "px";
         public static final String UNIT_TEXT_POINT       = "pt";
         public static final String UNIT_TEXT_RADIAN      = "rad";
         public static final String UNIT_TEXT_REAL        = "";
         public static final String UNIT_TEXT_SECOND      = "s";


    /**
     * The lexical unit type.
     */
    protected short lexicalUnitType;

    /**
     * The next lexical unit.
     */
    protected LexicalUnit nextLexicalUnit;

    /**
     * The previous lexical unit.
     */
    protected LexicalUnit previousLexicalUnit;

    /**
     * Creates a new LexicalUnit.
     */
    protected CSSLexicalUnit(short t, LexicalUnit prev) {
        lexicalUnitType = t;
        previousLexicalUnit = prev;
        if (prev != null) {
            ((CSSLexicalUnit)prev).nextLexicalUnit = this;
        }
    }

    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getLexicalUnitType()}.
     */
    public short getLexicalUnitType() {
        return lexicalUnitType;
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getNextLexicalUnit()}.
     */
    public LexicalUnit getNextLexicalUnit() {
        return nextLexicalUnit;
    }

    /**
     * Sets the next lexical unit.
     */
    public void setNextLexicalUnit(LexicalUnit lu) {
        nextLexicalUnit = lu;
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getPreviousLexicalUnit()}.
     */
    public LexicalUnit getPreviousLexicalUnit() {
        return previousLexicalUnit;
    }
    
    /**
     * Sets the previous lexical unit.
     */
    public void setPreviousLexicalUnit(LexicalUnit lu) {
        previousLexicalUnit = lu;
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getIntegerValue()}.
     */
    public int getIntegerValue() {
        throw new IllegalStateException();
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getFloatValue()}.
     */
    public float getFloatValue() {
        throw new IllegalStateException();
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getDimensionUnitText()}.
     */
    public String getDimensionUnitText() {
        switch (lexicalUnitType) {
        case LexicalUnit.SAC_CENTIMETER:  return UNIT_TEXT_CENTIMETER;
        case LexicalUnit.SAC_DEGREE:      return UNIT_TEXT_DEGREE;
        case LexicalUnit.SAC_EM:          return UNIT_TEXT_EM;
        case LexicalUnit.SAC_EX:          return UNIT_TEXT_EX;
        case LexicalUnit.SAC_GRADIAN:     return UNIT_TEXT_GRADIAN;
        case LexicalUnit.SAC_HERTZ:       return UNIT_TEXT_HERTZ;
        case LexicalUnit.SAC_INCH:        return UNIT_TEXT_INCH;
        case LexicalUnit.SAC_KILOHERTZ:   return UNIT_TEXT_KILOHERTZ;
        case LexicalUnit.SAC_MILLIMETER:  return UNIT_TEXT_MILLIMETER;
        case LexicalUnit.SAC_MILLISECOND: return UNIT_TEXT_MILLISECOND;
        case LexicalUnit.SAC_PERCENTAGE:  return UNIT_TEXT_PERCENTAGE;
        case LexicalUnit.SAC_PICA:        return UNIT_TEXT_PICA;
        case LexicalUnit.SAC_PIXEL:       return UNIT_TEXT_PIXEL;
        case LexicalUnit.SAC_POINT:       return UNIT_TEXT_POINT;
        case LexicalUnit.SAC_RADIAN:      return UNIT_TEXT_RADIAN;
        case LexicalUnit.SAC_REAL:        return UNIT_TEXT_REAL;
        case LexicalUnit.SAC_SECOND:      return UNIT_TEXT_SECOND;
        default:
            throw new IllegalStateException("No Unit Text for type: " + 
                                            lexicalUnitType);
        }
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getFunctionName()}.
     */
    public String getFunctionName() {
        throw new IllegalStateException();
    }
    
    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getParameters()}.
     */
    public LexicalUnit getParameters() {
        throw new IllegalStateException();
    }

    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getStringValue()}.
     */
    public String getStringValue() {
        throw new IllegalStateException();
    }

    /**
     * <b>SAC</b>: Implements {@link LexicalUnit#getSubValues()}.
     */
    public LexicalUnit getSubValues() {
        throw new IllegalStateException();
    }

    /**
     * Creates a new integer lexical unit.
     */
    public static CSSLexicalUnit createSimple(short t, LexicalUnit prev) {
        return new SimpleLexicalUnit(t, prev);
    }

    /**
     * This class represents a simple unit.
     */
    protected static class SimpleLexicalUnit extends CSSLexicalUnit {

        /**
         * Creates a new LexicalUnit.
         */
        public SimpleLexicalUnit(short t, LexicalUnit prev) {
            super(t, prev);
        }
    }

    /**
     * Creates a new integer lexical unit.
     */
    public static CSSLexicalUnit createInteger(int val, LexicalUnit prev) {
        return new IntegerLexicalUnit(val, prev);
    }

    /**
     * This class represents an integer unit.
     */
    protected static class IntegerLexicalUnit extends CSSLexicalUnit {

        /**
         * The integer value.
         */
        protected int value;

        /**
         * Creates a new LexicalUnit.
         */
        public IntegerLexicalUnit(int val, LexicalUnit prev) {
            super(LexicalUnit.SAC_INTEGER, prev);
            value = val;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getIntegerValue()}.
         */
        public int getIntegerValue() {
            return value;
        }
    }

    /**
     * Creates a new float lexical unit.
     */
    public static CSSLexicalUnit createFloat(short t, float val, LexicalUnit prev) {
        return new FloatLexicalUnit(t, val, prev);
    }

    /**
     * This class represents a float unit.
     */
    protected static class FloatLexicalUnit extends CSSLexicalUnit {

        /**
         * The float value.
         */
        protected float value;

        /**
         * Creates a new LexicalUnit.
         */
        public FloatLexicalUnit(short t, float val, LexicalUnit prev) {
            super(t, prev);
            value = val;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getFloatValue()}.
         */
        public float getFloatValue() {
            return value;
        }
    }

    /**
     * Creates a new float lexical unit.
     */
    public static CSSLexicalUnit createDimension(float val, String dim,
                                                 LexicalUnit prev) {
        return new DimensionLexicalUnit(val, dim, prev);
    }

    /**
     * This class represents a dimension unit.
     */
    protected static class DimensionLexicalUnit extends CSSLexicalUnit {

        /**
         * The float value.
         */
        protected float value;

        /**
         * The dimension.
         */
        protected String dimension;

        /**
         * Creates a new LexicalUnit.
         */
        public DimensionLexicalUnit(float val, String dim, LexicalUnit prev) {
            super(SAC_DIMENSION, prev);
            value = val;
            dimension = dim;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getFloatValue()}.
         */
        public float getFloatValue() {
            return value;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getDimensionUnitText()}.
         */
        public String getDimensionUnitText() {
            return dimension;
        }
    }

    /**
     * Creates a new function lexical unit.
     */
    public static CSSLexicalUnit createFunction(String f, LexicalUnit params,
                                                LexicalUnit prev) {
        return new FunctionLexicalUnit(f, params, prev);
    }

    /**
     * This class represents a function unit.
     */
    protected static class FunctionLexicalUnit extends CSSLexicalUnit {

        /**
         * The function name.
         */
        protected String name;

        /**
         * The function parameters.
         */
        protected LexicalUnit parameters;

        /**
         * Creates a new LexicalUnit.
         */
        public FunctionLexicalUnit(String f, LexicalUnit params, LexicalUnit prev) {
            super(SAC_FUNCTION, prev);
            name = f;
            parameters = params;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getFunctionName()}.
         */
        public String getFunctionName() {
            return name;
        }
    
        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getParameters()}.
         */
        public LexicalUnit getParameters() {
            return parameters;
        }

    }

    /**
     * Creates a new function lexical unit.
     */
    public static CSSLexicalUnit createPredefinedFunction(short t, LexicalUnit params,
                                                          LexicalUnit prev) {
        return new PredefinedFunctionLexicalUnit(t, params, prev);
    }

    /**
     * This class represents a function unit.
     */
    protected static class PredefinedFunctionLexicalUnit extends CSSLexicalUnit {

        /**
         * The function parameters.
         */
        protected LexicalUnit parameters;

        /**
         * Creates a new LexicalUnit.
         */
        public PredefinedFunctionLexicalUnit(short t, LexicalUnit params,
                                             LexicalUnit prev) {
            super(t, prev);
            parameters = params;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getParameters()}.
         */
        public LexicalUnit getParameters() {
            return parameters;
        }
    }

    /**
     * Creates a new string lexical unit.
     */
    public static CSSLexicalUnit createString(short t, String val, LexicalUnit prev) {
        return new StringLexicalUnit(t, val, prev);
    }

    /**
     * This class represents a string unit.
     */
    protected static class StringLexicalUnit extends CSSLexicalUnit {

        /**
         * The string value.
         */
        protected String value;

        /**
         * Creates a new LexicalUnit.
         */
        public StringLexicalUnit(short t, String val, LexicalUnit prev) {
            super(t, prev);
            value = val;
        }

        /**
         * <b>SAC</b>: Implements {@link LexicalUnit#getStringValue()}.
         */
        public String getStringValue() {
            return value;
        }
    }
}
