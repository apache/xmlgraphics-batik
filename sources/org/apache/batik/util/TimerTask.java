/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * Class which is schedulable by class Timer.
 * The part of this class's code that is scheduled need to reside in the run() method.
 * [This class is provided to avoid dependencies on jdk1.3's
 * TimerTask class, and is intended to provide similar functionality.]
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface TimerTask extends Runnable {

    // TODO: complete implementation of java.util.TimerTask

    /** the method which is called by the scheduler at regular intervals. */
    public void run();
}
