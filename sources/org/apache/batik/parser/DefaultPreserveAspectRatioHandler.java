/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This class provides an adapter for PreserveAspectRatioHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultPreserveAspectRatioHandler
    implements PreserveAspectRatioHandler {
    /**
     * The only instance of this class.
     */
    public final static PreserveAspectRatioHandler INSTANCE
        = new DefaultPreserveAspectRatioHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultPreserveAspectRatioHandler() {
    }

    /**
     * Implements {@link
     * PreserveAspectRatioHandler#startPreserveAspectRatio()}.
     */
    public void startPreserveAspectRatio() throws ParseException {
    }
    
    /**
     * Implements {@link PreserveAspectRatioHandler#none()}.
     */
    public void none() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMaxYMax()}.
     */
    public void xMaxYMax() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMaxYMid()}.
     */
    public void xMaxYMid() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMaxYMin()}.
     */
    public void xMaxYMin() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMidYMax()}.
     */
    public void xMidYMax() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMidYMid()}.
     */
    public void xMidYMid() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMidYMin()}.
     */
    public void xMidYMin() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMinYMax()}.
     */
    public void xMinYMax() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMinYMid()}.
     */
    public void xMinYMid() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#xMinYMin()}.
     */
    public void xMinYMin() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#meet()}.
     */
    public void meet() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#slice()}.
     */
    public void slice() throws ParseException {
    }

    /**
     * Implements {@link PreserveAspectRatioHandler#endPreserveAspectRatio()}.
     */
    public void endPreserveAspectRatio() throws ParseException {
    }
}
