/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.tools.ant.taskdefs.optional;


// -- Batik classes ----------------------------------------------------------
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.apps.rasterizer.SVGConverterController;
import org.apache.batik.apps.rasterizer.SVGConverterSource;

// -- Ant classes ------------------------------------------------------------
import org.apache.tools.ant.Task;

// -- Java SDK classes -------------------------------------------------------
import java.io.File;
import java.util.Vector;
import java.util.Map;


/**
 * Implements simple controller for the <code>SVGConverter</code> operation.
 *
 * <p>This is almost the same as the 
 * {@link org.apache.batik.apps.rasterizer.DefaultSVGConverterController DefaultSVGConverterController}
 * except this produces error message when the conversion fails.</p>
 *
 * <p>See {@link SVGConverterController} for the method documentation.</p>
 *
 * @see SVGConverterController SVGConverterController
 * @see org.apache.batik.apps.rasterizer.DefaultSVGConverterController DefaultSVGConverterController
 *
 * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
 * @version $Id$
 */
public class RasterizerTaskSVGConverterController implements SVGConverterController {

    // -- Variables ----------------------------------------------------------
    /** Ant task that is used to log messages. */
    protected Task executingTask = null;


    // -- Constructors -------------------------------------------------------
    /**
     * Don't allow public usage.
     */
    protected RasterizerTaskSVGConverterController() {
    }

    /**
     * Sets the given Ant task to receive log messages.
     *
     * @param task Ant task. The value can be <code>null</code> when log messages won't be written.
     */
    public RasterizerTaskSVGConverterController(Task task) {
        executingTask = task;
    }


    // -- Public interface ---------------------------------------------------
    public boolean proceedWithComputedTask(Transcoder transcoder,
                                           Map hints,
                                           Vector sources,
                                           Vector dest){
        return true;
    }
    
    public boolean proceedWithSourceTranscoding(SVGConverterSource source, 
                                                File dest) {
        return true;
    }
    
    public boolean proceedOnSourceTranscodingFailure(SVGConverterSource source,
                                                     File dest,
                                                     String errorCode){
        if(executingTask != null) {
            executingTask.log("Unable to rasterize image '" 
                + source.getName() + "' to '" 
                + dest.getAbsolutePath() + "': " + errorCode);
        }
        return true;
    }

    public void onSourceTranscodingSuccess(SVGConverterSource source,
                                           File dest){
    }

}
