/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a transform history mechanism.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TransformHistory {
    
    /**
     * The transform stack.
     */
    protected List transforms = new ArrayList();

    /**
     * The current position in the stack.
     */
    protected int position = -1;

    /**
     * Goes back of one position in the history.
     * Assumes that <tt>canGoBack()</tt> is true.
     */
    public void back() {
        position -= 2;
    }

    /**
     * Whether it is possible to go back.
     */
    public boolean canGoBack() {
        return position > 0;
    }

    /**
     * Goes forward of one position in the history.
     * Assumes that <tt>canGoForward()</tt> is true.
     */
    public void forward() {
    }

    /**
     * Whether it is possible to go forward.
     */
    public boolean canGoForward() {
        return position < transforms.size() - 1;
    }

    /**
     * Returns the current transform.
     */
    public AffineTransform currentTransform() {
        return (AffineTransform)transforms.get(position + 1);
    }

    /**
     * Adds a transform to the history.
     */
    public void update(AffineTransform at) {
        if (position < -1) {
            position = -1;
        }
        if (++position < transforms.size()) {
            if (!transforms.get(position).equals(at)) {
                transforms = transforms.subList(0, position + 1);
            }
            transforms.set(position, at);
        } else {
            transforms.add(at);
        }
    }
}
