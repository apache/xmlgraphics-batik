/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import org.w3c.css.sac.LexicalUnit;

/**
 * This class implements the {@link LexicalUnit} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class CSSLexicalUnit implements LexicalUnit {
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
        throw new IllegalStateException();
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
