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
public interface ParserFactory {
    /**
     * Creates and returns an angle parser.
     */
    AngleParser createAngleParser();

    /**
     * Creates and returns a clock parser.
     */
    ClockParser createClockParser();

    /**
     * Creates and returns a fragment identifier parser.
     */
    FragmentIdentifierParser createFragmentIdentifierParser();

    /**
     * Creates and returns a length parser.
     */
    LengthParser createLengthParser();
    
    /**
     * Creates and returns a length list parser.
     */
    LengthParser createLengthListParser();

    /**
     * Creates and returns a path parser.
     */
    PathParser createPathParser();

    /**
     * Creates and returns a points parser.
     */
    PointsParser createPointsParser();

    /**
     * Creates and returns a points parser.
     */
    PreserveAspectRatioParser createPreserveAspectRatioParser();

    /**
     * Creates and returns a transform list parser.
     */
    TransformListParser createTransformListParser();
}
