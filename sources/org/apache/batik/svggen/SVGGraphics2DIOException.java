/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.io.IOException;

/**
 * Thrown when an SVG Generator method receives an illegal argument in parameter.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class SVGGraphics2DIOException extends IOException {
    /** The enclosed exception. */
    private IOException embedded;

    /**
     * Constructs a new <code>SVGGraphics2DIOException</code> with the
     * specified detail message.
     * @param s the detail message of this exception
     */
    public SVGGraphics2DIOException(String s) {
        this(s, null);
    }

    /**
     * Constructs a new <code>SVGGraphics2DIOException</code> with the
     * specified detail message.
     * @param ex the enclosed exception
     */
    public SVGGraphics2DIOException(IOException ex) {
        this(null, ex);
    }

    /**
     * Constructs a new <code>SVGGraphics2DIOException</code> with the
     * specified detail message.
     * @param s the detail message of this exception
     * @param ex the original exception
     */
    public SVGGraphics2DIOException(String s, IOException ex) {
        super(s);
        embedded = ex;
    }

    /**
     * Returns the message of this exception. If an error message has
     * been specified, returns that one. Otherwise, return the error message
     * of enclosed exception or null if any.
     */
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        } else if (embedded != null) {
            return embedded.getMessage();
        } else {
            return null;
        }
    }

    /**
     * Returns the original enclosed exception or null if any.
     */
    public IOException getException() {
        return embedded;
    }
}
