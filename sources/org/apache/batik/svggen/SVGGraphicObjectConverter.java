/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;



/**
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class SVGGraphicObjectConverter implements SVGSyntax {
    /**
     * Used by converters to create Elements and other DOM objects.
     */
    protected SVGGeneratorContext generatorContext;

    /**
     * @param generatorContext can be used by the SVGGraphicObjectConverter
     * extentions to create Elements and other types of DOM objects.
     */
    public SVGGraphicObjectConverter(SVGGeneratorContext generatorContext) {
        if (generatorContext == null)
            throw new SVGGraphics2DRuntimeException(ErrorConstants.ERR_CONTEXT_NULL);
        this.generatorContext = generatorContext;
    }

    /**
     * Utility method for subclasses.
     * @return the double value formated as an int if there
     *         is no fractional part. This avoids the extra
     *         ".0" that a standard convertion gives.
     */
    public static String doubleString(double value) {
        if(((int)value) == value)
            return Integer.toString((int)value);
        else
            return Double.toString(value);
    }
}
