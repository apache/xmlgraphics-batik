/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

/**
 * The <code>ErrorHandler</code> interface allows you to specialize
 * how the error will be set on an SVG <code>Element</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface ErrorHandler {
    /**
     * This method handles the <code>SVGGraphics2DIOException</code>.
     */
    public void handleError(SVGGraphics2DIOException ex)
        throws SVGGraphics2DIOException;

    /**
     * This method handles the <code>SVGGraphics2DRuntimeException</code>.
     */
    public void handleError(SVGGraphics2DRuntimeException ex)
        throws SVGGraphics2DRuntimeException;
}
