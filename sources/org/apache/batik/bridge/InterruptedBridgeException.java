/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * This class represents the exception thrown by the bridge when the
 * current thread was interrupted.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class InterruptedBridgeException extends BridgeException {
    
    /**
     * Creates a new InterruptedBridgeException.
     */
    public InterruptedBridgeException() {
        super("");
    }
}
