/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface represents a factory of micro-parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ParserFactory {

    /**
     * Creates and returns an angle parser.
     */
    public AngleParser createAngleParser() {
        return new AngleParser();
    }

    /**
     * Creates and returns a clock parser.
     */
    public ClockParser createClockParser() {
        throw new RuntimeException(" !!! TODO");
    }

    /**
     * Creates and returns a fragment identifier parser.
     */
    public FragmentIdentifierParser createFragmentIdentifierParser() {
        throw new RuntimeException(" !!! TODO");
    }

    /**
     * Creates and returns a length parser.
     */
    public LengthParser createLengthParser() {
        return new LengthParser();
    }

    /**
     * Creates and returns a length list parser.
     */
    public LengthParser createLengthListParser() {
        return new LengthListParser();
    }

    /**
     * Creates and returns a path parser.
     */
    public PathParser createPathParser() {
        return new PathParser();
    }

    /**
     * Creates and returns a points parser.
     */
    public PointsParser createPointsParser() {
        return new PointsParser();
    }

    /**
     * Creates and returns a points parser.
     */
    public PreserveAspectRatioParser createPreserveAspectRatioParser() {
        return new PreserveAspectRatioParser();
    }

    /**
     * Creates and returns a transform list parser.
     */
    public TransformListParser createTransformListParser() {
        return new TransformListParser();
    }
}
